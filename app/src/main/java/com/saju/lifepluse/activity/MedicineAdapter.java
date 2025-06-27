package com.saju.lifepluse.activity;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.saju.lifepluse.R; // Assuming R is in this package

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

public class MedicineAdapter extends FirestoreRecyclerAdapter<Medicine, MedicineAdapter.MedicineViewHolder> {

    public MedicineAdapter(@NonNull FirestoreRecyclerOptions<Medicine> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull MedicineViewHolder holder, int position, @NonNull Medicine model) {
        if (model == null) return;

        holder.tvMedicineName.setText(model.getName());
        holder.tvDosage.setText(model.getDosage());

        // IMPROVED: Use SimpleDateFormat for robust time formatting
        if (model.getTime() != null && !model.getTime().isEmpty()) {
            SimpleDateFormat inputFormat = new SimpleDateFormat("HH:mm", Locale.US);
            SimpleDateFormat outputFormat = new SimpleDateFormat("h:mm a", Locale.US);
            try {
                Date date = inputFormat.parse(model.getTime());
                holder.tvTime.setText(outputFormat.format(date));
            } catch (ParseException e) {
                holder.tvTime.setText(model.getTime()); // Fallback to raw time
            }
        } else {
            holder.tvTime.setText("N/A");
        }

        // Update checkbox without firing the listener
        holder.cbTaken.setOnCheckedChangeListener(null);
        holder.cbTaken.setChecked(model.isTakenToday());
        holder.cbTaken.setOnCheckedChangeListener((buttonView, isChecked) -> {
            // Check isPressed to ensure the change is from a user click
            if (buttonView.isPressed()) {
                getSnapshots().getSnapshot(holder.getAdapterPosition()).getReference().update("takenToday", isChecked);
            }
        });

        // Populate the days of the week
        updateDaysOfWeek(holder.daysOfWeekContainer, model.getDaysOfWeek());
    }

    private void updateDaysOfWeek(LinearLayout container, Map<String, Boolean> days) {
        container.removeAllViews(); // Clear previous views for simplicity
        if (days == null || days.isEmpty()) return;

        Context context = container.getContext();
        // Use a consistent order for displaying days
        String[] dayOrder = {"monday", "tuesday", "wednesday", "thursday", "friday", "saturday", "sunday"};

        for (String day : dayOrder) {
            TextView dayView = new TextView(context);
            dayView.setText(day.substring(0, 1).toUpperCase());

            // Apply the correct style based on whether the day is active
            if (days.getOrDefault(day, false)) {
                dayView.setTextAppearance(R.style.DayIndicator_Active);
                dayView.setBackgroundResource(R.drawable.day_indicator_active);
            } else {
                dayView.setTextAppearance(R.style.DayIndicator);
                dayView.setBackgroundResource(R.drawable.day_indicator_inactive);
            }

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    dpToPx(context, 28), dpToPx(context, 28)
            );
            params.setMarginEnd(dpToPx(context, 6));
            dayView.setLayoutParams(params);
            dayView.setGravity(android.view.Gravity.CENTER);

            container.addView(dayView);
        }
    }

    private int dpToPx(Context context, int dp) {
        return (int) (dp * context.getResources().getDisplayMetrics().density);
    }

    @NonNull
    @Override
    public MedicineViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_medicine, parent, false);
        return new MedicineViewHolder(view);
    }

    @Override
    public void onDataChanged() {
        super.onDataChanged();
        Log.d("MedicineAdapter", "Data changed. New item count: " + getItemCount());
        // Logic to show/hide an empty list view can go here
    }

    static class MedicineViewHolder extends RecyclerView.ViewHolder {
        TextView tvMedicineName, tvDosage, tvTime;
        CheckBox cbTaken;
        LinearLayout daysOfWeekContainer;

        public MedicineViewHolder(@NonNull View itemView) {
            super(itemView);
            tvMedicineName = itemView.findViewById(R.id.tvMedicineName);
            tvDosage = itemView.findViewById(R.id.tvDosage);
            tvTime = itemView.findViewById(R.id.tvTime);
            cbTaken = itemView.findViewById(R.id.cbTaken);
            daysOfWeekContainer = itemView.findViewById(R.id.daysOfWeekContainer);
        }
    }
}