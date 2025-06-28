package com.saju.lifepluse.activity;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.saju.lifepluse.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class AddMedicineActivity extends AppCompatActivity {

    // UI Elements
    private TextInputEditText etMedicineName, etDosage;
    private Button btnSaveMedicine, btnAddTime;
    private ChipGroup chipGroupDays, timeChipGroup;
    private Chip chipMonday, chipTuesday, chipWednesday, chipThursday, chipFriday, chipSaturday, chipSunday;
    private ProgressBar progressBar; // The new ProgressBar

    // Firebase & System Services
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private AlarmManager alarmManager;

    // State for holding multiple selected times
    private final List<String> selectedTimes = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_medicine);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        initializeViews();

        btnAddTime.setOnClickListener(v -> showTimePickerDialog());
        btnSaveMedicine.setOnClickListener(v -> checkPermissionAndSave());
    }

    private void initializeViews() {
        etMedicineName = findViewById(R.id.etMedicineName);
        etDosage = findViewById(R.id.etDosage);
        btnAddTime = findViewById(R.id.btnAddTime);
        timeChipGroup = findViewById(R.id.timeChipGroup);
        btnSaveMedicine = findViewById(R.id.btnSaveMedicine);
        chipGroupDays = findViewById(R.id.chipGroupDays);
        progressBar = findViewById(R.id.progressbar); // Initialize the ProgressBar

        chipMonday = findViewById(R.id.chipMonday);
        chipTuesday = findViewById(R.id.chipTuesday);
        chipWednesday = findViewById(R.id.chipWednesday);
        chipThursday = findViewById(R.id.chipThursday);
        chipFriday = findViewById(R.id.chipFriday);
        chipSaturday = findViewById(R.id.chipSaturday);
        chipSunday = findViewById(R.id.chipSunday);
    }

    private void checkPermissionAndSave() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (alarmManager.canScheduleExactAlarms()) {
                saveMedicine();
            } else {
                Toast.makeText(this, "Permission needed to set precise reminders.", Toast.LENGTH_LONG).show();
                startActivity(new Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM));
            }
        } else {
            saveMedicine();
        }
    }

    private void saveMedicine() {
        String name = etMedicineName.getText().toString().trim();
        String dosage = etDosage.getText().toString().trim();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(this, "Not logged in.", Toast.LENGTH_SHORT).show();
            return;
        }

        // --- 1. VALIDATE INPUTS ---
        if (name.isEmpty() || dosage.isEmpty() || selectedTimes.isEmpty()) {
            Toast.makeText(this, "Please fill name, dosage, and add at least one time.", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Boolean> daysOfWeek = getSelectedDays();
        if (!daysOfWeek.containsValue(true)) {
            Toast.makeText(this, "Please select at least one day.", Toast.LENGTH_SHORT).show();
            return;
        }

        // --- 2. SHOW LOADING INDICATOR ---
        setLoadingState(true);

        // --- 3. PREPARE DATA AND SAVE ---
        String userId = currentUser.getUid();
        long baseNotificationId = System.currentTimeMillis();
        Medicine medicine = new Medicine(name, dosage, baseNotificationId, selectedTimes);
        medicine.setDaysOfWeek(daysOfWeek);

        db.collection("users").document(userId)
                .collection("medicines").add(medicine)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(AddMedicineActivity.this, "Reminder Saved.", Toast.LENGTH_SHORT).show();
                    scheduleAllAlarms(medicine);
                    finish(); // Close activity on success
                })
                .addOnFailureListener(e -> {
                    setLoadingState(false); // Hide loading on failure
                    Toast.makeText(AddMedicineActivity.this, "Error saving: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void showTimePickerDialog() {
        final Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                (view, hourOfDay, minuteOfHour) -> {
                    String time24h = String.format(Locale.US, "%02d:%02d", hourOfDay, minuteOfHour);
                    if (selectedTimes.contains(time24h)) {
                        Toast.makeText(this, "This time is already added.", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    selectedTimes.add(time24h);
                    Collections.sort(selectedTimes);
                    updateTimeChips();
                }, hour, minute, true); // Use 24h picker for consistency
        timePickerDialog.show();
    }

    private void updateTimeChips() {
        timeChipGroup.removeAllViews();
        for (String time24h : selectedTimes) {
            Chip chip = new Chip(this);
            chip.setText(formatToAmPm(time24h));
            chip.setCloseIconVisible(true);
            chip.setOnCloseIconClickListener(v -> {
                selectedTimes.remove(time24h);
                updateTimeChips();
            });
            timeChipGroup.addView(chip);
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

    private Map<String, Boolean> getSelectedDays() {
        Map<String, Boolean> daysOfWeek = new HashMap<>();
        daysOfWeek.put("monday", chipMonday.isChecked());
        daysOfWeek.put("tuesday", chipTuesday.isChecked());
        daysOfWeek.put("wednesday", chipWednesday.isChecked());
        daysOfWeek.put("thursday", chipThursday.isChecked());
        daysOfWeek.put("friday", chipFriday.isChecked());
        daysOfWeek.put("saturday", chipSaturday.isChecked());
        daysOfWeek.put("sunday", chipSunday.isChecked());
        return daysOfWeek;
    }

    /**
     * A helper method to manage the UI loading state.
     * @param isLoading True to show ProgressBar, false to show the save button.
     */
    private void setLoadingState(boolean isLoading) {
        if (isLoading) {
            progressBar.setVisibility(View.VISIBLE);
            btnSaveMedicine.setVisibility(View.INVISIBLE);
        } else {
            progressBar.setVisibility(View.GONE);
            btnSaveMedicine.setVisibility(View.VISIBLE);
        }
    }

    private void scheduleAllAlarms(Medicine medicine) {
        // This method remains unchanged...
        Map<String, Boolean> days = medicine.getDaysOfWeek();
        long baseId = medicine.getNotificationId();

        for (String timeString : medicine.getReminderTimes()) {
            String[] parts = timeString.split(":");
            int hour = Integer.parseInt(parts[0]);
            int minute = Integer.parseInt(parts[1]);
            int pendingIntentId = (int) baseId + hour * 100 + minute;

            Calendar nextAlarmTime = getNextAlarmTime(hour, minute, days);
            if (nextAlarmTime == null) continue;

            Intent intent = new Intent(this, AlarmReceiver.class);
            intent.putExtra("MEDICINE_NAME", medicine.getName());
            intent.putExtra("NOTIFICATION_ID", pendingIntentId);
            intent.putExtra("MEDICINE_HOUR", hour);
            intent.putExtra("MEDICINE_MINUTE", minute);
            intent.putExtra("DAYS_OF_WEEK", (HashMap<String, Boolean>) days);

            PendingIntent pendingIntent = PendingIntent.getBroadcast(
                    this, pendingIntentId, intent,
                    PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

            alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP, nextAlarmTime.getTimeInMillis(), pendingIntent);
        }
    }

    public static Calendar getNextAlarmTime(int hour, int minute, Map<String, Boolean> days) {
        // This method remains unchanged...
        Map<String, Integer> dayToCalendarInt = new HashMap<>();
        dayToCalendarInt.put("sunday", Calendar.SUNDAY);
        dayToCalendarInt.put("monday", Calendar.MONDAY);
        dayToCalendarInt.put("tuesday", Calendar.TUESDAY);
        dayToCalendarInt.put("wednesday", Calendar.WEDNESDAY);
        dayToCalendarInt.put("thursday", Calendar.THURSDAY);
        dayToCalendarInt.put("friday", Calendar.FRIDAY);
        dayToCalendarInt.put("saturday", Calendar.SATURDAY);

        Calendar now = Calendar.getInstance();
        Calendar nextAlarm = (Calendar) now.clone();
        nextAlarm.set(Calendar.HOUR_OF_DAY, hour);
        nextAlarm.set(Calendar.MINUTE, minute);
        nextAlarm.set(Calendar.SECOND, 0);
        nextAlarm.set(Calendar.MILLISECOND, 0);

        for (int i = 0; i < 8; i++) { // Iterate 8 times to check today and the next 7 days
            int dayOfWeek = nextAlarm.get(Calendar.DAY_OF_WEEK);
            String dayKey = "";
            for (Map.Entry<String, Integer> entry : dayToCalendarInt.entrySet()) {
                if (entry.getValue() == dayOfWeek) {
                    dayKey = entry.getKey();
                    break;
                }
            }

            if (days.getOrDefault(dayKey, false) && nextAlarm.after(now)) {
                return nextAlarm;
            }
            nextAlarm.add(Calendar.DAY_OF_MONTH, 1);
        }
        return null;
    }
}