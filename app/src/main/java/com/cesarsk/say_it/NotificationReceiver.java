package com.cesarsk.say_it;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v7.app.NotificationCompat;

import com.cesarsk.say_it.R;
import com.cesarsk.say_it.ui.MainActivity;
import com.cesarsk.say_it.ui.PlayActivity;
import com.cesarsk.say_it.utility.UtilityDictionary;

import java.io.IOException;
import java.util.Calendar;

public class NotificationReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        //Loading word of the day in Notification in order to show on it.
        if (MainActivity.wordOfTheDay == null) {
            try {
                UtilityDictionary.loadDictionary(context);
            } catch (IOException e) {
                e.printStackTrace();
            }
            PlayActivity.selected_word = MainActivity.wordOfTheDay;
            PlayActivity.selected_ipa = MainActivity.IPAofTheDay;
        }

        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        //Building our custom Notification
        NotificationCompat.Builder mBuilder =
                (NotificationCompat.Builder) new NotificationCompat.Builder(context)
                        .setSmallIcon(R.drawable.ic_notification_icon)
                        .setContentTitle("Say it! Reminder ")
                        .setContentText("Hey! Here's your word of the day: " + MainActivity.wordOfTheDay)
                        .setSound(alarmSound)
                        .setColor(Color.argb(0,21,101,192))
                        .setVibrate(new long[]{300, 300, 300, 300, 300})
                        .setAutoCancel(true);

        Intent resultIntent = new Intent(context, PlayActivity.class);
        resultIntent.putExtra(PlayActivity.PLAY_WORD, MainActivity.wordOfTheDay);
        resultIntent.putExtra(PlayActivity.PLAY_IPA, MainActivity.IPAofTheDay);
        resultIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addParentStack(PlayActivity.class);
        stackBuilder.addNextIntent(resultIntent);

        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        mBuilder.setContentIntent(resultPendingIntent);
        notificationManager.notify(MainActivity.notifId, mBuilder.build());
    }

    public static void scheduleNotification(Context context, int hour, int minute, String mode){

        int mode_code = Integer.parseInt(mode);
        ComponentName receiver = new ComponentName(context, NotificationBootReceiver.class);
        PackageManager pm = context.getPackageManager();

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, NotificationReceiver.class);
        PendingIntent notificationPendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        if(mode_code == 0){
            //Notification OFF
            alarmManager.cancel(notificationPendingIntent);
            pm.setComponentEnabledSetting(receiver,
                    PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                    PackageManager.DONT_KILL_APP);

            return;
        }

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());

        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);



        if(mode_code == 2){
            //Daily Notifications
            if (calendar.getTimeInMillis() < System.currentTimeMillis()) {
                calendar.setTimeInMillis(calendar.getTimeInMillis() + AlarmManager.INTERVAL_DAY);
            }

            //TODO Provare Cancel
            alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, notificationPendingIntent);
        }

        else if(mode_code == 1){
            //Weekly Notifications
            if (calendar.getTimeInMillis() < System.currentTimeMillis()) {
                calendar.setTimeInMillis(calendar.getTimeInMillis() + AlarmManager.INTERVAL_DAY*7);
            }

            alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY*7, notificationPendingIntent);
        }

        pm.setComponentEnabledSetting(receiver,
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP);
    }
}