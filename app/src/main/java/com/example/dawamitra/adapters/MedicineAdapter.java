package com.example.dawamitra.adapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.*;
import android.widget.*;

import androidx.recyclerview.widget.RecyclerView;

import com.example.dawamitra.R;
import com.example.dawamitra.models.Medicine;

import java.util.ArrayList;

public class MedicineAdapter extends RecyclerView.Adapter<MedicineAdapter.ViewHolder> {

    public interface OnMedicineActionListener {
        void onEdit(int position, Medicine medicine);
        void onDelete(int position, Medicine medicine);
    }

    private Context context;
    private ArrayList<Medicine> list;
    private OnMedicineActionListener listener;

    public MedicineAdapter(Context context, ArrayList<Medicine> list, OnMedicineActionListener listener) {
        this.context = context;
        this.list = (list != null) ? list : new ArrayList<>();
        this.listener = listener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        View colorCircle;
        ImageView icon;
        TextView name, type;
        ImageButton btnEdit, btnDelete;

        public ViewHolder(View v) {
            super(v);
            colorCircle = v.findViewById(R.id.medColorCircle);
            icon = v.findViewById(R.id.medIcon);
            name = v.findViewById(R.id.tvMedName);
            type = v.findViewById(R.id.tvMedType);
            btnEdit = v.findViewById(R.id.btnEditMed);
            btnDelete = v.findViewById(R.id.btnDeleteMed);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_medicine, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (list == null || list.size() <= position) return;

        Medicine med = list.get(position);

        holder.name.setText(med.name != null ? med.name : "Unknown");

        // Set type text (capitalize first letter)
        String typeText = med.iconType != null ? med.iconType : "tablet";
        holder.type.setText(typeText.substring(0, 1).toUpperCase() + typeText.substring(1));

        // Set color circle
        if (med.color != null) {
            GradientDrawable circle = new GradientDrawable();
            circle.setShape(GradientDrawable.OVAL);
            circle.setColor(Color.parseColor(med.color));
            holder.colorCircle.setBackground(circle);
        }

        // Set icon based on type
        holder.icon.setImageResource(getIconForType(med.iconType));

        // Edit
        holder.btnEdit.setOnClickListener(v -> {
            if (listener != null) listener.onEdit(holder.getAdapterPosition(), med);
        });

        // Delete
        holder.btnDelete.setOnClickListener(v -> {
            if (listener != null) listener.onDelete(holder.getAdapterPosition(), med);
        });
    }

    @Override
    public int getItemCount() {
        return (list != null) ? list.size() : 0;
    }

    public void updateData(ArrayList<Medicine> newList) {
        this.list = (newList != null) ? newList : new ArrayList<>();
        notifyDataSetChanged();
    }

    public static int getIconForType(String type) {
        if (type == null) return R.drawable.ic_tablet;
        switch (type) {
            case "capsule": return R.drawable.ic_capsule;
            case "syrup": return R.drawable.ic_syrup;
            case "injection": return R.drawable.ic_injection;
            case "drops": return R.drawable.ic_drops;
            default: return R.drawable.ic_tablet;
        }
    }
}
