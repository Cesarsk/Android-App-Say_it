package com.cesarsk.say_it;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.cesarsk.say_it.ui.MainActivity;
import com.cesarsk.say_it.utility.UtilitySharedPrefs;

/**
 * Created by cesarsk on 05/03/2017.
 */

@SuppressWarnings("ALL")
public class NotificationBootReceiver extends BroadcastReceiver {

    public static int REQUEST_CODE = 1;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
            UtilitySharedPrefs.loadSettingsPrefs(context);
            int hour = Integer.parseInt(MainActivity.DEFAULT_NOTIFICATION_HOUR);
            int minute = Integer.parseInt(MainActivity.DEFAULT_NOTIFICATION_MINUTE);
            String mode = MainActivity.DEFAULT_NOTIFICATION_RATE;
            NotificationReceiver.scheduleNotification(context, hour, minute, mode);
        }
    }
}