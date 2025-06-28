package com.saju.lifepluse.activity;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

import com.saju.lifepluse.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class AlarmReceiver extends BroadcastReceiver {
    private static final String CHANNEL_ID = "MEDICINE_REMINDER_CHANNEL";

    @Override
    public void onReceive(Context context, Intent intent) {
        String medicineName = intent.getStringExtra("MEDICINE_NAME");
        int notificationId = intent.getIntExtra("NOTIFICATION_ID", 0);

        showNotification(context, medicineName, notificationId);

        // MODIFIED: Smarter rescheduling logic
        rescheduleNextAlarm(context, intent);
    }

    private void showNotification(Context context, String medicineName, int notificationId) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Medicine Reminders",
                    NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription("Channel for medicine reminder notifications");
            notificationManager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_foreground) // Use your actual icon
                .setContentTitle("Time for your medicine!")
                .setContentText("Don't forget to take your " + medicineName)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setAutoCancel(true);

        notificationManager.notify(notificationId, builder.build());
    }

    // MODIFIED: This method now correctly finds the next valid day and reschedules.
    private void rescheduleNextAlarm(Context context, Intent oldIntent) {
        int hour = oldIntent.getIntExtra("MEDICINE_HOUR", -1);
        int minute = oldIntent.getIntExtra("MEDICINE_MINUTE", -1);
        int pendingIntentId = oldIntent.getIntExtra("NOTIFICATION_ID", 0);
        HashMap<String, Boolean> daysOfWeek = (HashMap<String, Boolean>) oldIntent.getSerializableExtra("DAYS_OF_WEEK");

        if (hour == -1 || daysOfWeek == null) {
            // Cannot reschedule if data is missing
            return;
        }

        // CRITICAL: Use the utility from AddMedicineActivity to find the next alarm time
        Calendar nextAlarmTime = AddMedicineActivity.getNextAlarmTime(hour, minute, daysOfWeek);

        if (nextAlarmTime == null) {
            // No future alarm to set (e.g., if all days were unchecked in the database, though unlikely)
            return;
        }

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent newIntent = new Intent(context, AlarmReceiver.class);
        newIntent.putExtras(oldIntent.getExtras()); // Copy all data to the new intent

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context,
                pendingIntentId,
                newIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (alarmManager.canScheduleExactAlarms()) {
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, nextAlarmTime.getTimeInMillis(), pendingIntent);
            }
        } else {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, nextAlarmTime.getTimeInMillis(), pendingIntent);
        }
    }
}