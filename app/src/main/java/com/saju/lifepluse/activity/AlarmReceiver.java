package com.saju.lifepluse.activity;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;

import androidx.core.app.NotificationCompat;

import com.saju.lifepluse.R;

import java.util.Calendar;

public class AlarmReceiver extends BroadcastReceiver {
    private static final String CHANNEL_ID = "MEDICINE_REMINDER_CHANNEL";

    @Override
    public void onReceive(Context context, Intent intent) {
        // --- Show the Notification ---
        String medicineName = intent.getStringExtra("MEDICINE_NAME");
        int notificationId = intent.getIntExtra("NOTIFICATION_ID", 0);
        showNotification(context, medicineName, notificationId);

        // --- Reschedule the NEXT alarm for the following day ---
        rescheduleNextAlarm(context, intent);
    }

    private void showNotification(Context context, String medicineName, int notificationId) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Uri defaultSoundUri = Settings.System.DEFAULT_NOTIFICATION_URI;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Medicine Reminders",
                    NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription("Channel for medicine reminder notifications");
            channel.setSound(defaultSoundUri, null);
            notificationManager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle("Time for your medicine!")
                .setContentText("Don't forget to take your " + medicineName)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setSound(defaultSoundUri)
                .setAutoCancel(true);

        notificationManager.notify(notificationId, builder.build());
    }

    private void rescheduleNextAlarm(Context context, Intent oldIntent) {
        // Extract original alarm details
        int hour = oldIntent.getIntExtra("MEDICINE_HOUR", -1);
        int minute = oldIntent.getIntExtra("MEDICINE_MINUTE", -1);
        int pendingIntentId = oldIntent.getIntExtra("NOTIFICATION_ID", 0);

        if (hour == -1) return; // Cannot reschedule if time is missing

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        // The intent must be the same to be identified
        Intent newIntent = new Intent(context, AlarmReceiver.class);
        newIntent.putExtras(oldIntent.getExtras()); // Copy all extras

        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, pendingIntentId, newIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        // CRITICAL: Schedule for the NEXT DAY
        calendar.add(Calendar.DAY_OF_MONTH, 1);

        // Use setExactAndAllowWhileIdle again
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (alarmManager.canScheduleExactAlarms()) {
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
            }
        } else {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
        }
    }
}