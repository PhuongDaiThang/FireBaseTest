package com.movieticketapp.movieticket.firebase;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import android.app.NotificationChannel;
import android.app.NotificationManager;

import com.movieticketapp.movieticket.R;
import com.movieticketapp.movieticket.activities.MyTicketsActivity;

public class ShowtimeReminderReceiver extends BroadcastReceiver {

    private static final String CHANNEL_ID = "showtime_reminder_channel";

    @Override
    public void onReceive(Context context, Intent intent) {
        String movieTitle = intent.getStringExtra("movieTitle");
        String time = intent.getStringExtra("time");
        String theaterName = intent.getStringExtra("theaterName");

        createNotificationChannel(context);

        Intent openIntent = new Intent(context, MyTicketsActivity.class);
        openIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, openIntent,
                PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_IMMUTABLE);

        String body = "Phim \"" + movieTitle + "\" sẽ chiếu lúc " + time + " tại " + theaterName;

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle("Nhắc lịch chiếu phim!")
                .setContentText(body)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(body))
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent);

        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (manager != null) {
            manager.notify((int) System.currentTimeMillis(), builder.build());
        }
    }

    private void createNotificationChannel(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Showtime Reminders",
                    NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription("Reminders for upcoming movie showtimes");
            NotificationManager manager = context.getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }
    }

    /**
     * Schedule a reminder 30 minutes before showtime
     */
    public static void scheduleReminder(Context context, String ticketId,
            String movieTitle, String time,
            String theaterName, long showtimeTimestamp) {
        long reminderTime = showtimeTimestamp - (30 * 60 * 1000); // 30 minutes before

        if (reminderTime <= System.currentTimeMillis()) {
            return; // Don't schedule if the time has passed
        }

        Intent intent = new Intent(context, ShowtimeReminderReceiver.class);
        intent.putExtra("movieTitle", movieTitle);
        intent.putExtra("time", time);
        intent.putExtra("theaterName", theaterName);

        int requestCode = ticketId.hashCode();
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, requestCode, intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (alarmManager != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, reminderTime, pendingIntent);
            } else {
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, reminderTime, pendingIntent);
            }
        }
    }
}
