package com.example.dawamitra.activities;

import android.app.AlertDialog;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.*;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dawamitra.R;
import com.example.dawamitra.adapters.MedicineAdapter;
import com.example.dawamitra.data.DoseManager;
import com.example.dawamitra.models.Dose;
import com.example.dawamitra.models.Medicine;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class MedicineActivity extends AppCompatActivity implements MedicineAdapter.OnMedicineActionListener {

    private static final String[] COLORS = {
            "#F44336", "#E91E63", "#9C27B0", "#2196F3",
            "#009688", "#4CAF50", "#FF9800", "#FFC107"
    };

    private static final String[] ICON_TYPES = {
            "tablet", "capsule", "syrup", "injection", "drops"
    };

    private String doseId;
    private Dose currentDose;
    private RecyclerView rvMedicines;
    private MedicineAdapter adapter;
    private LinearLayout emptyState;

    // Dialog state
    private String selectedColor = COLORS[0];
    private String selectedIconType = ICON_TYPES[0];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medicine);

        doseId = getIntent().getStringExtra("doseId");

        rvMedicines = findViewById(R.id.rvMedicines);
        emptyState = findViewById(R.id.emptyMedState);
        FloatingActionButton fab = findViewById(R.id.fabAddMedicine);
        ImageButton btnBack = findViewById(R.id.btnBack);
        TextView tvTitle = findViewById(R.id.tvDoseTitle);

        rvMedicines.setLayoutManager(new LinearLayoutManager(this));

        // Load dose
        loadDose();

        if (currentDose != null) {
            tvTitle.setText(currentDose.time);
        }

        adapter = new MedicineAdapter(this, currentDose != null ? currentDose.medicines : new ArrayList<>(), this);
        rvMedicines.setAdapter(adapter);
        updateEmptyState();

        fab.setOnClickListener(v -> showMedicineDialog(null, -1));
        btnBack.setOnClickListener(v -> finish());
    }

    private void loadDose() {
        currentDose = DoseManager.findDoseById(this, doseId);
    }

    private void updateEmptyState() {
        boolean empty = currentDose == null || currentDose.medicines == null || currentDose.medicines.isEmpty();
        emptyState.setVisibility(empty ? View.VISIBLE : View.GONE);
        rvMedicines.setVisibility(empty ? View.GONE : View.VISIBLE);
    }

    @Override
    public void onEdit(int position, Medicine medicine) {
        showMedicineDialog(medicine, position);
    }

    @Override
    public void onDelete(int position, Medicine medicine) {
        new MaterialAlertDialogBuilder(this)
                .setTitle("Delete Medicine")
                .setMessage("Remove \"" + medicine.name + "\" from this dose?")
                .setPositiveButton("Delete", (d, w) -> {
                    currentDose.medicines.remove(position);
                    DoseManager.updateDose(this, currentDose);
                    adapter.updateData(currentDose.medicines);
                    updateEmptyState();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void showMedicineDialog(Medicine existingMedicine, int editPosition) {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_medicine, null);

        EditText etName = dialogView.findViewById(R.id.etMedicineName);
        GridLayout colorGrid = dialogView.findViewById(R.id.colorGrid);
        LinearLayout iconContainer = dialogView.findViewById(R.id.iconTypeContainer);
        Button btnCancel = dialogView.findViewById(R.id.btnCancel);
        Button btnSave = dialogView.findViewById(R.id.btnSave);

        // Pre-fill if editing
        if (existingMedicine != null) {
            etName.setText(existingMedicine.name);
            selectedColor = existingMedicine.color != null ? existingMedicine.color : COLORS[0];
            selectedIconType = existingMedicine.iconType != null ? existingMedicine.iconType : ICON_TYPES[0];
        } else {
            selectedColor = COLORS[0];
            selectedIconType = ICON_TYPES[0];
        }

        // Build color picker circles
        ArrayList<View> colorViews = new ArrayList<>();
        for (String color : COLORS) {
            FrameLayout frame = new FrameLayout(this);
            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
            params.width = dpToPx(48);
            params.height = dpToPx(48);
            params.setMargins(dpToPx(6), dpToPx(4), dpToPx(6), dpToPx(4));
            frame.setLayoutParams(params);

            View circle = new View(this);
            FrameLayout.LayoutParams circleParams = new FrameLayout.LayoutParams(dpToPx(40), dpToPx(40));
            circleParams.gravity = android.view.Gravity.CENTER;
            circle.setLayoutParams(circleParams);

            GradientDrawable gd = new GradientDrawable();
            gd.setShape(GradientDrawable.OVAL);
            gd.setColor(Color.parseColor(color));
            if (color.equals(selectedColor)) {
                gd.setStroke(dpToPx(3), Color.parseColor("#212121"));
            }
            circle.setBackground(gd);

            frame.addView(circle);
            frame.setOnClickListener(v -> {
                selectedColor = color;
                // Refresh all circles
                for (int i = 0; i < colorViews.size(); i++) {
                    FrameLayout f = (FrameLayout) colorViews.get(i);
                    View c = f.getChildAt(0);
                    GradientDrawable gd2 = new GradientDrawable();
                    gd2.setShape(GradientDrawable.OVAL);
                    gd2.setColor(Color.parseColor(COLORS[i]));
                    if (COLORS[i].equals(selectedColor)) {
                        gd2.setStroke(dpToPx(3), Color.parseColor("#212121"));
                    }
                    c.setBackground(gd2);
                }
            });

            colorViews.add(frame);
            colorGrid.addView(frame);
        }

        // Build icon type selector
        ArrayList<TextView> typeViews = new ArrayList<>();
        int[] icons = {
                R.drawable.ic_tablet, R.drawable.ic_capsule,
                R.drawable.ic_syrup, R.drawable.ic_injection, R.drawable.ic_drops
        };

        for (int idx = 0; idx < ICON_TYPES.length; idx++) {
            String type = ICON_TYPES[idx];

            LinearLayout chipLayout = new LinearLayout(this);
            chipLayout.setOrientation(LinearLayout.VERTICAL);
            chipLayout.setGravity(android.view.Gravity.CENTER);
            LinearLayout.LayoutParams chipParams = new LinearLayout.LayoutParams(
                    dpToPx(72), LinearLayout.LayoutParams.WRAP_CONTENT);
            chipParams.setMargins(dpToPx(4), 0, dpToPx(4), 0);
            chipLayout.setLayoutParams(chipParams);
            chipLayout.setPadding(dpToPx(8), dpToPx(8), dpToPx(8), dpToPx(8));

            // Icon
            ImageView iconView = new ImageView(this);
            iconView.setImageResource(icons[idx]);
            LinearLayout.LayoutParams iconParams = new LinearLayout.LayoutParams(dpToPx(28), dpToPx(28));
            iconView.setLayoutParams(iconParams);

            // Label
            TextView label = new TextView(this);
            String displayName = type.substring(0, 1).toUpperCase() + type.substring(1);
            label.setText(displayName);
            label.setTextSize(11);
            label.setGravity(android.view.Gravity.CENTER);

            chipLayout.addView(iconView);
            chipLayout.addView(label);

            // Set initial state
            updateChipState(chipLayout, iconView, label, type.equals(selectedIconType));

            chipLayout.setOnClickListener(v -> {
                selectedIconType = type;
                // Refresh all chips
                for (int i = 0; i < typeViews.size(); i++) {
                    LinearLayout cl = (LinearLayout) typeViews.get(i).getParent();
                    ImageView iv = (ImageView) cl.getChildAt(0);
                    TextView tv = (TextView) cl.getChildAt(1);
                    updateChipState(cl, iv, tv, ICON_TYPES[i].equals(selectedIconType));
                }
            });

            typeViews.add(label);
            iconContainer.addView(chipLayout);
        }

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(dialogView)
                .setCancelable(true)
                .create();

        btnCancel.setOnClickListener(v -> dialog.dismiss());

        btnSave.setOnClickListener(v -> {
            String name = etName.getText().toString().trim();
            if (name.isEmpty()) {
                etName.setError("Enter medicine name");
                return;
            }

            if (currentDose == null) return;

            if (existingMedicine != null && editPosition >= 0) {
                // Update existing
                Medicine med = currentDose.medicines.get(editPosition);
                med.name = name;
                med.color = selectedColor;
                med.iconType = selectedIconType;
            } else {
                // Add new
                Medicine med = new Medicine(name, selectedColor, selectedIconType);
                currentDose.medicines.add(med);
            }

            DoseManager.updateDose(this, currentDose);
            adapter.updateData(currentDose.medicines);
            updateEmptyState();
            dialog.dismiss();
        });

        dialog.show();
    }

    private void updateChipState(LinearLayout chipLayout, ImageView iconView, TextView label, boolean selected) {
        if (selected) {
            chipLayout.setBackgroundResource(R.drawable.chip_bg_selected);
            iconView.setColorFilter(Color.WHITE);
            label.setTextColor(Color.WHITE);
        } else {
            chipLayout.setBackgroundResource(R.drawable.chip_bg);
            iconView.setColorFilter(Color.parseColor("#757575"));
            label.setTextColor(Color.parseColor("#757575"));
        }
    }

    private int dpToPx(int dp) {
        return (int) (dp * getResources().getDisplayMetrics().density);
    }
}