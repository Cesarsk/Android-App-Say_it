package com.example.cesarsk.say_it;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v7.app.NotificationCompat;

/**
 * Created by cesarsk on 05/03/2017.
 */

public class NotificationReceiver extends BroadcastReceiver {

    public static int REQUEST_CODE = 1;
/*
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
            // Set the alarm here.
            Intent service = new Intent(context, NotificationAlarmService.class);
            service.putExtra("notifId", intent.getIntExtra("notifId", 0));
            context.startService(service);
        }
    }
*/
    @Override
    public void onReceive(Context context, Intent intent) {

        long when = System.currentTimeMillis();
        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder mBuilder =
                (NotificationCompat.Builder) new NotificationCompat.Builder(context)
                        .setSmallIcon(R.drawable.ic_flag)
                        .setContentTitle("Say it! Reminder ")
                        .setContentText("Hey! Here's your word of the day: "+MainActivity.wordOfTheDay)
                        .setSound(alarmSound)
                        .setVibrate(new long[]{900, 900, 900, 900, 900})
                        .setAutoCancel(true).setWhen(when);

// Creates an explicit intent for an Activity in your app
        Intent resultIntent = new Intent(context, PlayActivity.class);
        resultIntent.putExtra(PlayActivity.PLAY_WORD, MainActivity.wordOfTheDay);

        resultIntent.setFlags(resultIntent.FLAG_ACTIVITY_CLEAR_TOP
                | resultIntent.FLAG_ACTIVITY_SINGLE_TOP);

// The stack builder object will contain an artificial back stack for the
// started Activity. This ensures that navigating backward from the Activity leads out of
// your application to the Home screen.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
// Adds the back stack for the Intent (but not the Intent itself)

        //TODO Stacking activities on Notification. Shouldn't this add MainActivity as previous activity of PlayActivity?
        stackBuilder.addParentStack(MainActivity.class);
// Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(resultIntent);

        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

// mId allows you to update the notification later on.
        mNotificationManager.notify(REQUEST_CODE, mBuilder.build());
        REQUEST_CODE++;
    }
}

