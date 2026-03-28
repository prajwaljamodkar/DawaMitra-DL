package com.example.dawamitra.activities;

import android.app.*;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dawamitra.R;
import com.example.dawamitra.ReminderReceiver;
import com.example.dawamitra.adapters.DoseAdapter;
import com.example.dawamitra.data.DoseManager;
import com.example.dawamitra.models.Dose;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.*;

public class DashboardActivity extends AppCompatActivity implements DoseAdapter.OnDoseActionListener {

    ArrayList<Dose> list;
    RecyclerView recyclerView;
    DoseAdapter adapter;
    LinearLayout emptyState;
    TextView tvDoseCount;

    // For add dose dialog
    private int selectedHour = 8;
    private int selectedMinute = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        recyclerView = findViewById(R.id.recyclerView);
        emptyState = findViewById(R.id.emptyState);
        tvDoseCount = findViewById(R.id.tvDoseCount);
        FloatingActionButton fab = findViewById(R.id.fabAddDose);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Seed initial data on first launch
        if (DoseManager.isFirstLaunch(this)) {
            DoseManager.seedInitialData(this);
            DoseManager.setFirstLaunchDone(this);
        }

        // Load data
        loadData();

        // Set adapter
        adapter = new DoseAdapter(this, list, this);
        recyclerView.setAdapter(adapter);

        // FAB click → Add dose dialog
        fab.setOnClickListener(v -> showAddDoseDialog());

        // Schedule alarms for all doses
        scheduleAllAlarms();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh data when returning from MedicineActivity
        loadData();
        if (adapter != null) {
            adapter.updateData(list);
        }
        updateEmptyState();
    }

    private void loadData() {
        list = DoseManager.loadDoses(this);
        updateEmptyState();
    }

    private void updateEmptyState() {
        if (list == null || list.isEmpty()) {
            emptyState.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
            tvDoseCount.setText("0 doses");
        } else {
            emptyState.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            tvDoseCount.setText(list.size() + (list.size() == 1 ? " dose" : " doses"));
        }
    }

    private void showAddDoseDialog() {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_dose, null);

        EditText etLabel = dialogView.findViewById(R.id.etDoseLabel);
        Button btnPickTime = dialogView.findViewById(R.id.btnPickTime);
        Button btnCancel = dialogView.findViewById(R.id.btnCancelDose);
        Button btnSave = dialogView.findViewById(R.id.btnSaveDose);

        selectedHour = 8;
        selectedMinute = 0;
        btnPickTime.setText(formatTime(selectedHour, selectedMinute));

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(dialogView)
                .setCancelable(true)
                .create();

        btnPickTime.setOnClickListener(v -> {
            TimePickerDialog tp = new TimePickerDialog(this, (view, hourOfDay, minute) -> {
                selectedHour = hourOfDay;
                selectedMinute = minute;
                btnPickTime.setText(formatTime(hourOfDay, minute));
            }, selectedHour, selectedMinute, false);
            tp.show();
        });

        btnCancel.setOnClickListener(v -> dialog.dismiss());

        btnSave.setOnClickListener(v -> {
            String label = etLabel.getText().toString().trim();
            if (label.isEmpty()) {
                etLabel.setError("Enter a label");
                return;
            }

            String timeStr = label + " " + formatTime(selectedHour, selectedMinute);
            Dose newDose = new Dose(timeStr, selectedHour, selectedMinute, new ArrayList<>());

            list.add(newDose);
            DoseManager.saveDoses(this, list);
            adapter.updateData(list);
            updateEmptyState();

            // Schedule alarm for new dose
            setReminder(newDose);

            dialog.dismiss();

            // Open MedicineActivity to add medicines
            Intent i = new Intent(this, MedicineActivity.class);
            i.putExtra("doseId", newDose.id);
            startActivity(i);
        });

        dialog.show();
    }

    @Override
    public void onDelete(int position, Dose dose) {
        new MaterialAlertDialogBuilder(this)
                .setTitle("Delete Dose")
                .setMessage("Are you sure you want to delete \"" + dose.time + "\"?")
                .setPositiveButton("Delete", (d, w) -> {
                    cancelReminder(dose);
                    DoseManager.deleteDose(this, dose.id);
                    list = DoseManager.loadDoses(this);
                    adapter.updateData(list);
                    updateEmptyState();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    @Override
    public void onEdit(int position, Dose dose) {
        Intent i = new Intent(this, MedicineActivity.class);
        i.putExtra("doseId", dose.id);
        startActivity(i);
    }

    private void scheduleAllAlarms() {
        if (list == null) return;
        for (Dose dose : list) {
            setReminder(dose);
        }
    }

    private void setReminder(Dose dose) {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY, dose.hour);
        c.set(Calendar.MINUTE, dose.minute);
        c.set(Calendar.SECOND, 0);

        // If time has passed today, schedule for tomorrow
        if (c.getTimeInMillis() <= System.currentTimeMillis()) {
            c.add(Calendar.DAY_OF_MONTH, 1);
        }

        AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
            if (!am.canScheduleExactAlarms()) {
                Intent intent = new Intent(android.provider.Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM);
                startActivity(intent);
                return;
            }
        }

        Intent i = new Intent(this, ReminderReceiver.class);
        i.putExtra("doseId", dose.id);
        i.putExtra("doseHour", dose.hour);
        i.putExtra("doseMinute", dose.minute);

        int requestCode = dose.hour * 100 + dose.minute;

        PendingIntent pi = PendingIntent.getBroadcast(
                this,
                requestCode,
                i,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        if (am != null) {
            am.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    c.getTimeInMillis(),
                    pi
            );
        }
    }

    private void cancelReminder(Dose dose) {
        AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
        Intent i = new Intent(this, ReminderReceiver.class);
        int requestCode = dose.hour * 100 + dose.minute;
        PendingIntent pi = PendingIntent.getBroadcast(
                this, requestCode, i, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );
        if (am != null) {
            am.cancel(pi);
        }
    }

    private String formatTime(int hour, int minute) {
        String amPm = (hour >= 12) ? "PM" : "AM";
        int displayHour = (hour == 0) ? 12 : (hour > 12) ? hour - 12 : hour;
        return String.format(Locale.US, "%d:%02d %s", displayHour, minute, amPm);
    }
}