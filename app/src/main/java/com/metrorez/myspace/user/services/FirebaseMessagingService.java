package com.metrorez.myspace.user.services;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.text.Html;

import com.google.firebase.messaging.RemoteMessage;
import com.metrorez.myspace.R;

public class FirebaseMessagingService extends com.google.firebase.messaging.FirebaseMessagingService {

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        String messageTitle = remoteMessage.getNotification().getTitle();
        String messageBody = Html.fromHtml(remoteMessage.getNotification().getBody()).toString();

        String click_action = remoteMessage.getNotification().getClickAction();
        createNotificationChannel();
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, getString(R.string.channel_Id))
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle(messageTitle)
                .setContentText(messageBody)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

        Intent intent = new Intent(click_action);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
        mBuilder.setContentIntent(pendingIntent);

        // notificationId is a unique int for each notification that you must define
        int mNotificationId = (int) System.currentTimeMillis();
        notificationManager.notify(mNotificationId, mBuilder.build());
    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_Id);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(getString(R.string.channel_Id), name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }


}
