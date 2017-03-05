package com.example.cesarsk.say_it;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.NotificationCompat;

/**
 * Created by cesarsk on 05/03/2017.
 */

public class NotificationReceiver extends BroadcastReceiver {

    public final static int REQUEST_CODE = 1;

    @Override
    public void onReceive(Context context, Intent intent) {
        showNotification(context);
    }

    public void showNotification(Context context) {
        Intent intent = new Intent(context, MainActivity.class);
        PendingIntent pi = PendingIntent.getActivity(context, REQUEST_CODE, intent, 0);
        NotificationCompat.Builder mBuilder = (NotificationCompat.Builder) new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.ic_settings)
                .setContentTitle("Say it!")
                .setContentText("Here's your word of the day: "+MainActivity.wordOfTheDay+"\n Practice now!");
        mBuilder.setContentIntent(pi);
        mBuilder.setDefaults(Notification.DEFAULT_SOUND);
        mBuilder.setAutoCancel(true);
        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(REQUEST_CODE, mBuilder.build());
    }
}

