package com.example.dawamitra.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.*;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.dawamitra.R;
import com.example.dawamitra.activities.MedicineActivity;
import com.example.dawamitra.models.Dose;

import java.util.ArrayList;

public class DoseAdapter extends RecyclerView.Adapter<DoseAdapter.ViewHolder> {

    private Context context;
    private ArrayList<Dose> list;

    // Constructor with null safety
    public DoseAdapter(Context context, ArrayList<Dose> list) {
        this.context = context;
        this.list = (list != null) ? list : new ArrayList<>();
    }

    // ViewHolder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView title;

        public ViewHolder(View v) {
            super(v);
            title = v.findViewById(R.id.tvTitle);
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

        // Safety check (prevents crash even if something goes wrong)
        if (list == null || list.size() <= position) return;

        Dose d = list.get(position);

        // Avoid null crash
        holder.title.setText(d.time != null ? d.time : "No Time");

        holder.itemView.setOnClickListener(v -> {
            Intent i = new Intent(context, MedicineActivity.class);

            if (d.medicines != null) {
                i.putStringArrayListExtra("meds", d.medicines);
            } else {
                i.putStringArrayListExtra("meds", new ArrayList<>());
            }

            context.startActivity(i);
        });
    }

    @Override
    public int getItemCount() {
        return (list != null) ? list.size() : 0;
    }

    // 🔥 IMPORTANT: method to update data properly
    public void updateData(ArrayList<Dose> newList) {
        this.list = (newList != null) ? newList : new ArrayList<>();
        notifyDataSetChanged();
    }
}