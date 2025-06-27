package com.saju.lifepluse.activity;


import java.util.Map;

public class Medicine {
    private String name;
    private String dosage;
    private String time; // e.g., "08:30 AM"
    private int hour;
    private int minute;
    private long notificationId; // <-- ADD THIS FIELD

    private Map<String, Boolean> daysOfWeek;
    private boolean takenToday;

    // IMPORTANT: Must have a public no-argument constructor for Firestore
    public Medicine() {
    }

    // Update constructor
    public Medicine(String name, String dosage, String time, int hour, int minute, long notificationId) {
        this.name = name;
        this.dosage = dosage;
        this.time = time;
        this.hour = hour;
        this.minute = minute;
        this.notificationId = notificationId; // <-- ADD THIS
    }

    // Getters and Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDosage() {
        return dosage;
    }

    public void setDosage(String dosage) {
        this.dosage = dosage;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public int getHour() {
        return hour;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public int getMinute() {
        return minute;
    }

    public void setMinute(int minute) {
        this.minute = minute;
    }

    // <-- ADD GETTER AND SETTER FOR THE NEW FIELD -->
    public long getNotificationId() {
        return notificationId;
    }

    public void setNotificationId(long notificationId) {
        this.notificationId = notificationId;
    }

    public Map<String, Boolean> getDaysOfWeek() {
        return daysOfWeek;
    }

    public void setDaysOfWeek(Map<String, Boolean> daysOfWeek) {
        this.daysOfWeek = daysOfWeek;
    }

    public boolean isTakenToday() {
        return takenToday;
    }

    public void setTakenToday(boolean takenToday) {
        this.takenToday = takenToday;
    }
}