package com.example.dawamitra.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.*;
import android.widget.*;

import androidx.recyclerview.widget.RecyclerView;

import com.example.dawamitra.R;
import com.example.dawamitra.activities.MedicineActivity;
import com.example.dawamitra.models.Dose;
import com.example.dawamitra.models.Medicine;

import java.util.ArrayList;

public class DoseAdapter extends RecyclerView.Adapter<DoseAdapter.ViewHolder> {

    public interface OnDoseActionListener {
        void onDelete(int position, Dose dose);
        void onEdit(int position, Dose dose);
    }

    private Context context;
    private ArrayList<Dose> list;
    private OnDoseActionListener listener;

    public DoseAdapter(Context context, ArrayList<Dose> list, OnDoseActionListener listener) {
        this.context = context;
        this.list = (list != null) ? list : new ArrayList<>();
        this.listener = listener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTime, tvMedCount;
        ImageButton btnEdit, btnDelete;
        LinearLayout medicineContainer;

        public ViewHolder(View v) {
            super(v);
            tvTime = v.findViewById(R.id.tvDoseTime);
            tvMedCount = v.findViewById(R.id.tvMedCount);
            btnEdit = v.findViewById(R.id.btnEditDose);
            btnDelete = v.findViewById(R.id.btnDeleteDose);
            medicineContainer = v.findViewById(R.id.medicineContainer);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_dose, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (list == null || list.size() <= position) return;

        Dose d = list.get(position);

        // Set time
        holder.tvTime.setText(d.time != null ? d.time : "No Time");

        // Medicine count
        int medCount = (d.medicines != null) ? d.medicines.size() : 0;
        holder.tvMedCount.setText(medCount + (medCount == 1 ? " medicine" : " medicines"));

        // Clear and rebuild medicine chips
        holder.medicineContainer.removeAllViews();

        if (d.medicines != null) {
            for (Medicine med : d.medicines) {
                View chipView = LayoutInflater.from(context)
                        .inflate(R.layout.item_medicine_chip, holder.medicineContainer, false);

                // Color dot
                View colorDot = chipView.findViewById(R.id.colorDot);
                if (med.color != null) {
                    GradientDrawable circle = new GradientDrawable();
                    circle.setShape(GradientDrawable.OVAL);
                    circle.setColor(Color.parseColor(med.color));
                    colorDot.setBackground(circle);
                }

                // Name
                TextView tvName = chipView.findViewById(R.id.tvChipName);
                tvName.setText(med.name != null ? med.name : "Unknown");

                // Type badge
                TextView tvType = chipView.findViewById(R.id.tvChipType);
                String typeText = med.iconType != null ? med.iconType : "tablet";
                tvType.setText(typeText.substring(0, 1).toUpperCase() + typeText.substring(1));

                holder.medicineContainer.addView(chipView);
            }
        }

        // Click card → open MedicineActivity
        holder.itemView.setOnClickListener(v -> {
            Intent i = new Intent(context, MedicineActivity.class);
            i.putExtra("doseId", d.id);
            context.startActivity(i);
        });

        // Edit button → open MedicineActivity
        holder.btnEdit.setOnClickListener(v -> {
            Intent i = new Intent(context, MedicineActivity.class);
            i.putExtra("doseId", d.id);
            context.startActivity(i);
        });

        // Delete button
        holder.btnDelete.setOnClickListener(v -> {
            if (listener != null) listener.onDelete(holder.getAdapterPosition(), d);
        });
    }

    @Override
    public int getItemCount() {
        return (list != null) ? list.size() : 0;
    }

    public void updateData(ArrayList<Dose> newList) {
        this.list = (newList != null) ? newList : new ArrayList<>();
        notifyDataSetChanged();
    }
}