package com.example.cesarsk.say_it;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by cesarsk on 05/03/2017.
 */

@SuppressWarnings("ALL")
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