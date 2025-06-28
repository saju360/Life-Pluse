package com.saju.lifepluse.activity;

import java.util.List;
import java.util.Map;

public class Medicine {
    private String name;
    private String dosage;
    private long notificationId;
    private Map<String, Boolean> daysOfWeek;
    private boolean takenToday;

    // MODIFIED: Store a list of times, e.g., ["08:00", "13:30", "22:00"]
    private List<String> reminderTimes;

    // Firestore requires a public no-argument constructor
    public Medicine() {}

    // MODIFIED: Updated constructor
    public Medicine(String name, String dosage, long notificationId, List<String> reminderTimes) {
        this.name = name;
        this.dosage = dosage;
        this.notificationId = notificationId;
        this.reminderTimes = reminderTimes;
        this.takenToday = false; // Default value
    }

    // --- Getters and Setters ---
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDosage() { return dosage; }
    public void setDosage(String dosage) { this.dosage = dosage; }

    public long getNotificationId() { return notificationId; }
    public void setNotificationId(long notificationId) { this.notificationId = notificationId; }

    public Map<String, Boolean> getDaysOfWeek() { return daysOfWeek; }
    public void setDaysOfWeek(Map<String, Boolean> daysOfWeek) { this.daysOfWeek = daysOfWeek; }

    public boolean isTakenToday() { return takenToday; }
    public void setTakenToday(boolean takenToday) { this.takenToday = takenToday; }

    // NEW: Getter and Setter for the list of times
    public List<String> getReminderTimes() { return reminderTimes; }
    public void setReminderTimes(List<String> reminderTimes) { this.reminderTimes = reminderTimes; }
}