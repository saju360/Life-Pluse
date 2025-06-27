package com.saju.lifepluse.activity;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.chip.Chip;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.saju.lifepluse.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class AddMedicineActivity extends AppCompatActivity {

    // UI Elements
    private TextInputEditText etMedicineName, etDosage;
    private Button btnSaveMedicine;
    private TextView tvSelectedTime;
    private Chip chipMonday, chipTuesday, chipWednesday, chipThursday, chipFriday, chipSaturday, chipSunday;

    // Firebase & System Services
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private AlarmManager alarmManager;

    // State
    private int selectedHour = -1;
    private int selectedMinute = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_medicine);

        // Initialize Firebase and System Services
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        // Initialize Views
        initializeViews();


        btnSaveMedicine.setOnClickListener(v -> checkPermissionAndSave());
    }

    // Inside your initializeViews() method:
    private void initializeViews() {
        // Note the change in EditText type
        etMedicineName = findViewById(R.id.etMedicineName);
        etDosage = findViewById(R.id.etDosage);
        // The clickable TextView for time now has two jobs
        tvSelectedTime = findViewById(R.id.tvSelectedTime);
        btnSaveMedicine = findViewById(R.id.btnSaveMedicine);

        // Set the click listener on the TextView instead of a separate button
        tvSelectedTime.setOnClickListener(v -> showTimePickerDialog());

        // Initialize Chips instead of CheckBoxes
        chipMonday = findViewById(R.id.chipMonday);
        chipTuesday = findViewById(R.id.chipTuesday);
        chipWednesday = findViewById(R.id.chipWednesday);
        chipThursday = findViewById(R.id.chipThursday);
        chipFriday = findViewById(R.id.chipFriday);
        chipSaturday = findViewById(R.id.chipSaturday);
        chipSunday = findViewById(R.id.chipSunday);
    }

    /**
     * Checks for the required exact alarm permission on modern Android versions
     * before proceeding to save the medicine data.
     */
    private void checkPermissionAndSave() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (alarmManager.canScheduleExactAlarms()) {
                saveMedicine();
            } else {
                Toast.makeText(this, "App requires permission to set reminders.", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM);
                startActivity(intent);
            }
        } else {
            saveMedicine();
        }
    }

    private void showTimePickerDialog() {
        // ... (This method is complete and correct)
        final Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                (view, hourOfDay, minuteOfHour) -> {
                    selectedHour = hourOfDay;
                    selectedMinute = minuteOfHour;
                    String amPm = (hourOfDay < 12) ? "AM" : "PM";
                    int displayHour = (hourOfDay == 0 || hourOfDay == 12) ? 12 : hourOfDay % 12;
                    String time = String.format(Locale.getDefault(), "%d:%02d %s", displayHour, minuteOfHour, amPm);
                    tvSelectedTime.setText("Selected Time: " + time);
                }, hour, minute, false);
        timePickerDialog.show();
    }

    /**
     * Gathers all data from the UI, validates it, creates a Medicine object,
     * and saves it to Firestore.
     */
    private void saveMedicine() {
        String name = etMedicineName.getText().toString().trim();
        String dosage = etDosage.getText().toString().trim();

        // --- 1. VALIDATE USER & INPUTS ---
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(this, "Authentication error. Please log in again.", Toast.LENGTH_SHORT).show();
            return;
        }
        if (name.isEmpty() || dosage.isEmpty() || selectedHour == -1) {
            Toast.makeText(this, "Please fill all fields and select a time.", Toast.LENGTH_SHORT).show();
            return;
        }

        // --- 2. GATHER DAYS OF WEEK ---
        Map<String, Boolean> daysOfWeek = new HashMap<>();
        daysOfWeek.put("monday", chipMonday.isChecked());
        daysOfWeek.put("tuesday", chipTuesday.isChecked());
        daysOfWeek.put("wednesday", chipWednesday.isChecked());
        daysOfWeek.put("thursday", chipThursday.isChecked());
        daysOfWeek.put("friday", chipFriday.isChecked());
        daysOfWeek.put("saturday", chipSaturday.isChecked());
        daysOfWeek.put("sunday", chipSunday.isChecked());

        if (!daysOfWeek.containsValue(true)) {
            Toast.makeText(this, "Please select at least one day for the reminder.", Toast.LENGTH_SHORT).show();
            return;
        }

        // --- 3. CREATE MEDICINE OBJECT ---
        String userId = currentUser.getUid();
        long notificationId = System.currentTimeMillis();
        String timeString = String.format(Locale.getDefault(), "%02d:%02d", selectedHour, selectedMinute);
        Medicine medicine = new Medicine(name, dosage, timeString, selectedHour, selectedMinute, notificationId);
        medicine.setDaysOfWeek(daysOfWeek); // Set the selected days

        // --- 4. SAVE TO FIRESTORE & SCHEDULE ALARM ---
        db.collection("users").document(userId)
                .collection("medicines").add(medicine)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(AddMedicineActivity.this, "Medicine Saved.", Toast.LENGTH_SHORT).show();
                    scheduleFirstAlarm(medicine); // Schedule the very first alarm
                    finish();
                })
                .addOnFailureListener(e -> Toast.makeText(AddMedicineActivity.this, "Error saving: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    /**
     * Calculates the very next time this alarm should fire based on the selected days
     * and schedules a single, exact alarm.
     */
    private void scheduleFirstAlarm(Medicine medicine) {
        // Calculate the next occurrence
        Calendar nextAlarmTime = getNextAlarmTime(medicine.getHour(), medicine.getMinute(), medicine.getDaysOfWeek());

        if (nextAlarmTime == null) {
            // This should not happen due to the validation in saveMedicine(), but it's good practice
            Toast.makeText(this, "Could not schedule reminder. No day selected.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create the intent for the AlarmReceiver
        Intent intent = new Intent(this, AlarmReceiver.class);
        intent.putExtra("MEDICINE_NAME", medicine.getName());
        int pendingIntentId = (int) medicine.getNotificationId();
        intent.putExtra("NOTIFICATION_ID", pendingIntentId);
        // Pass all data needed for the receiver to reschedule the *next* alarm
        intent.putExtra("MEDICINE_HOUR", medicine.getHour());
        intent.putExtra("MEDICINE_MINUTE", medicine.getMinute());
        intent.putExtra("DAYS_OF_WEEK", (HashMap<String, Boolean>) medicine.getDaysOfWeek());

        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, pendingIntentId, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        // Schedule the exact alarm
        alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                nextAlarmTime.getTimeInMillis(),
                pendingIntent
        );

        // Inform the user of when the next alarm is set
        SimpleDateFormat sdf = new SimpleDateFormat("EEE, MMM d 'at' h:mm a", Locale.getDefault());
        Toast.makeText(this, "Next reminder set for " + sdf.format(nextAlarmTime.getTime()), Toast.LENGTH_LONG).show();
    }

    /**
     * A helper utility to find the next calendar date and time for an alarm based on selected weekdays.
     *
     * @param hour The hour of the day (0-23) for the alarm.
     * @param minute The minute of the hour (0-59) for the alarm.
     * @param days A map where keys are lowercase day names (e.g., "monday") and values are booleans.
     * @return A Calendar object set to the next valid alarm time, or null if no days are selected.
     */
    public static Calendar getNextAlarmTime(int hour, int minute, Map<String, Boolean> days) {
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

        // Iterate up to 7 days to find the next valid day
        for (int i = 0; i < 7; i++) {
            int dayOfWeek = nextAlarm.get(Calendar.DAY_OF_WEEK);

            String dayKey = "";
            for (Map.Entry<String, Integer> entry : dayToCalendarInt.entrySet()) {
                if (entry.getValue() == dayOfWeek) {
                    dayKey = entry.getKey();
                    break;
                }
            }

            // Check if this day is selected AND if the time is in the future
            if (days.getOrDefault(dayKey, false) && nextAlarm.after(now)) {
                return nextAlarm; // Found the next valid time
            }

            // Move to the next day
            nextAlarm.add(Calendar.DAY_OF_MONTH, 1);
        }

        return null; // No valid day found in the next week
    }
}