package com.example.justdoit;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class ReminderBroadcast extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "notifyJustDoIt")
                .setSmallIcon(R.drawable.ic_baseline_notifications_24)
                .setContentTitle("Notification title")
                .setContentText("Notification text")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat notificationmanager = NotificationManagerCompat.from(context);

        notificationmanager.notify(1, builder.build());
    }
}
