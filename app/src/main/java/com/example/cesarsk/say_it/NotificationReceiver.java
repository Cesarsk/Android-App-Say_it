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

    @Override
    public void onReceive(Context context, Intent intent) {
            // Set the alarm here.
            Intent service = new Intent(context, NotificationAlarmService.class);
            service.putExtra("notifId", intent.getIntExtra("notifId", 0));
            context.startService(service);
        }
    }