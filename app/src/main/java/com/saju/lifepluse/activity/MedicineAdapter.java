package com.saju.lifepluse.activity;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.saju.lifepluse.R;

import java.util.List;
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

        // Update checkbox
        holder.cbTaken.setOnCheckedChangeListener(null);
        holder.cbTaken.setChecked(model.isTakenToday());
        holder.cbTaken.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (buttonView.isPressed()) {
                getSnapshots().getSnapshot(holder.getAdapterPosition()).getReference().update("takenToday", isChecked);
            }
        });

        // NEW: Populate the time chips
        updateTimeChips(holder.timeChipGroupItem, model.getReminderTimes());

        // Populate the days of the week indicators
        updateDaysOfWeek(holder.daysOfWeekContainer, model.getDaysOfWeek());
    }

    private void updateTimeChips(ChipGroup chipGroup, List<String> times) {
        chipGroup.removeAllViews();
        if (times == null || times.isEmpty()) return;

        Context context = chipGroup.getContext();
        for (String time24h : times) {
            Chip chip = new Chip(context);
            chip.setText(formatToAmPm(time24h));
            chip.setChipIcon(ContextCompat.getDrawable(context, R.drawable.ic_time));
            chip.setChipIconTintResource(R.color.status_bar); // Or your primary color
            // Optional: style the chip to be smaller/less prominent
            // chip.setChipMinHeight(dpToPx(context, 28));
            // chip.setTextAppearance(...)
            chipGroup.addView(chip);
        }
    }

    private String formatToAmPm(String time24h) {
        try {
            String[] parts = time24h.split(":");
            int hour = Integer.parseInt(parts[0]);
            int minute = Integer.parseInt(parts[1]);
            String amPm = (hour < 12) ? "AM" : "PM";
            int displayHour = (hour == 0 || hour == 12) ? 12 : hour % 12;
            return String.format(Locale.getDefault(), "%d:%02d %s", displayHour, minute, amPm);
        } catch (Exception e) {
            return time24h;
        }
    }


    private void updateDaysOfWeek(LinearLayout container, Map<String, Boolean> days) {
        container.removeAllViews();
        if (days == null || days.isEmpty()) return;

        Context context = container.getContext();
        String[] dayOrder = {"M", "T", "W", "T", "F", "S", "S"};
        String[] dayKeys = {"monday", "tuesday", "wednesday", "thursday", "friday", "saturday", "sunday"};

        for (int i = 0; i < dayOrder.length; i++) {
            TextView dayView = (TextView) LayoutInflater.from(context).inflate(R.layout.day_indicator, container, false);
            dayView.setText(dayOrder[i]);
            if (days.getOrDefault(dayKeys[i], false)) {
                dayView.setBackgroundResource(R.drawable.day_indicator_active);
                dayView.setTextColor(ContextCompat.getColor(context, android.R.color.white));
            } else {
                dayView.setBackgroundResource(R.drawable.day_indicator_inactive);
                dayView.setTextColor(ContextCompat.getColor(context, android.R.color.darker_gray));
            }
            container.addView(dayView);
        }
    }

    @NonNull
    @Override
    public MedicineViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_medicine, parent, false);
        return new MedicineViewHolder(view);
    }

    static class MedicineViewHolder extends RecyclerView.ViewHolder {
        TextView tvMedicineName, tvDosage;
        CheckBox cbTaken;
        LinearLayout daysOfWeekContainer;
        ChipGroup timeChipGroupItem; // MODIFIED

        public MedicineViewHolder(@NonNull View itemView) {
            super(itemView);
            tvMedicineName = itemView.findViewById(R.id.tvMedicineName);
            tvDosage = itemView.findViewById(R.id.tvDosage);
            cbTaken = itemView.findViewById(R.id.cbTaken);
            daysOfWeekContainer = itemView.findViewById(R.id.daysOfWeekContainer);
            timeChipGroupItem = itemView.findViewById(R.id.timeChipGroupItem); // MODIFIED
        }
    }
}