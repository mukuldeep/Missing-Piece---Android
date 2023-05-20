package com.raabnits.missingpiece;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.BitSet;

public class MyMessagingService extends FirebaseMessagingService {
    private static final String TAG = "MyMessagingService";
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // ...
        show_notification(remoteMessage.getNotification().getTitle(),remoteMessage.getNotification().getBody(),remoteMessage.getNotification().getImageUrl());
        // TODO(developer): Handle FCM messages here.
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.d(TAG, "From: " + remoteMessage.getFrom());

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());

        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
        }

        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
    }


public void show_notification(String title, String message, Uri uri){
    // Create an Intent for the activity you want to start
    Intent resultIntent = new Intent(this, MainActivity.class);
    // Create the TaskStackBuilder and add the intent, which inflates the back stack
    TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
    stackBuilder.addNextIntentWithParentStack(resultIntent);
    // Get the PendingIntent containing the entire back stack
    PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);


    NotificationCompat.Builder builder=new NotificationCompat.Builder(this,"MyNotification")
            .setContentTitle(title)
            .setSmallIcon(R.drawable.ic_baseline_extension_24)
            .setContentIntent(resultPendingIntent)
            .setColor(Color.YELLOW)
            .setAutoCancel(true)
            .setContentText(message);
    if (uri != null) {
        Bitmap bitmap = getBitmapfromUrl(uri.toString());
        builder.setStyle(
                new NotificationCompat.BigPictureStyle()
                        .bigPicture(bitmap)
                        .bigLargeIcon(null)
        ).setLargeIcon(bitmap);
    }

    NotificationManagerCompat manager=NotificationManagerCompat.from(this);
    manager.notify(999,builder.build());
}
    public Bitmap getBitmapfromUrl(String imageUrl) {
        try {
            URL url = new URL(imageUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            return BitmapFactory.decodeStream(input);

        } catch (Exception e) {
            Log.e("awesome", "Error in getting notification image: " + e.getLocalizedMessage());
            return null;
        }
    }
}
