package com.example.cesarsk.say_it;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

/**
 * Created by cesarsk on 08/03/17.
 */

public class NotificationAlarmService extends Service {
    NotificationManager notificationManager;
    PendingIntent pendingIntent;
    long when = System.currentTimeMillis();
    Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

    @Override
    public int onStartCommand(Intent intent, int flag, int startId)
    {

        NotificationManager notificationManager = (NotificationManager)
                this.getSystemService(Context.NOTIFICATION_SERVICE);
        Intent mIntent = new Intent(this, MainActivity.class);
        pendingIntent = PendingIntent.getActivity(this, intent.getIntExtra("notifId", 0), mIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder mBuilder =
                (NotificationCompat.Builder) new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.ic_flag)
                        .setContentTitle("Say it! Reminder ")
                        .setContentText("Hey! Here's your word of the day: "+MainActivity.wordOfTheDay)
                        .setSound(alarmSound)
                        .setVibrate(new long[]{300, 300, 300, 300, 300})
                        .setAutoCancel(true).setWhen(when);


        // Creates an explicit intent for an Activity in your app
        Intent resultIntent = new Intent(this, PlayActivity.class);
        resultIntent.putExtra(PlayActivity.PLAY_WORD, MainActivity.wordOfTheDay);
        resultIntent.putExtra(PlayActivity.PLAY_IPA, MainActivity.IPAofTheDay);
        resultIntent.setFlags(resultIntent.FLAG_ACTIVITY_CLEAR_TOP
                | resultIntent.FLAG_ACTIVITY_SINGLE_TOP);
        // The stack builder object will contain an artificial back stack for the
// started Activity. This ensures that navigating backward from the Activity leads out of
// your application to the Home screen.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
// Adds the back stack for the Intent (but not the Intent itself)

        //TODO Stacking activities on Notification. Shouldn't this add MainActivity as previous activity of PlayActivity?
        stackBuilder.addParentStack(MainActivity.class);
// Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(resultIntent);

        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(resultPendingIntent);

        notificationManager.notify(intent.getIntExtra("notifId", 0), mBuilder.build());

        return super.onStartCommand(intent, flag, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
