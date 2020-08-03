package com.example.justdoit;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class ReminderBroadcast extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent intent_ = new Intent(context.getApplicationContext(), MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context.getApplicationContext(), 0, intent_, 0);
        String title = intent.getStringExtra("title");
        String category = intent.getStringExtra("category");
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "notifyJustDoIt")
                .setSmallIcon(R.drawable.ic_baseline_notifications_24)
                .setContentTitle("Task due: " + title)
                .setContentText(title + " under the category: " + category)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent).setAutoCancel(true);

        NotificationManagerCompat notificationmanager = NotificationManagerCompat.from(context);

        notificationmanager.notify(1, builder.build());
    }
}
