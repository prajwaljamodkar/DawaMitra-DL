package com.example.dawamitra.activities;

import android.app.*;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dawamitra.R;
import com.example.dawamitra.ReminderReceiver;
import com.example.dawamitra.adapters.DoseAdapter;
import com.example.dawamitra.models.Dose;

import java.util.*;

public class DashboardActivity extends AppCompatActivity {

    ArrayList<Dose> list;
    RecyclerView recyclerView;
    DoseAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // 🔥 STEP 1: LOAD DATA FIRST
        loadData();

        // 🔥 STEP 2: SET ADAPTER
        adapter = new DoseAdapter(this, list);
        recyclerView.setAdapter(adapter);

        // DEBUG
        Log.d("DATA_CHECK", "List size = " + list.size());

        // 🔔 SET ALARMS
        setReminder(8, 0);
        setReminder(14, 0);
        setReminder(21, 0);
    }

    private void loadData() {
        list = new ArrayList<>();

        list.add(new Dose("Morning 8:00 AM",
                new ArrayList<>(Arrays.asList("Paracetamol", "Vitamin C"))));

        list.add(new Dose("Afternoon 2:00 PM",
                new ArrayList<>(Arrays.asList("Metformin"))));

        list.add(new Dose("Night 9:00 PM",
                new ArrayList<>(Arrays.asList("Aspirin", "Calcium"))));
    }

    private void setReminder(int h, int m) {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY, h);
        c.set(Calendar.MINUTE, m);
        c.set(Calendar.SECOND, 0);

        AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
            if (!am.canScheduleExactAlarms()) {
                Intent intent = new Intent(android.provider.Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM);
                startActivity(intent);
                return;
            }
        }

        Intent i = new Intent(this, ReminderReceiver.class);

        PendingIntent pi = PendingIntent.getBroadcast(
                this,
                h * 100 + m,
                i,
                PendingIntent.FLAG_IMMUTABLE
        );

        if (am != null) {
            am.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    c.getTimeInMillis(),
                    pi
            );
        }
    }
}