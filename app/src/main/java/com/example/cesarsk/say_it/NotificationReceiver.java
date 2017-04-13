package com.example.cesarsk.say_it;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v7.app.NotificationCompat;
import android.widget.Toast;

import com.example.cesarsk.say_it.ui.MainActivity;
import com.example.cesarsk.say_it.ui.PlayActivity;
import com.example.cesarsk.say_it.utility.UtilityDictionary;

import java.util.Calendar;

public class NotificationReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        PendingIntent notificationPendingIntent = PendingIntent.getBroadcast(context, MainActivity.notifId, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        //Loading word of the day in Notification in order to show on it.
        if (MainActivity.wordOfTheDay == null) {
            Calendar c = Calendar.getInstance();
            Long seed = Long.parseLong(UtilityDictionary.getDate(c.getTimeInMillis()));
            MainActivity.wordOfTheDay = UtilityDictionary.getRandomWord(seed, false);
            MainActivity.IPAofTheDay = UtilityDictionary.getRandomWord(seed, true);
            PlayActivity.selected_word = MainActivity.wordOfTheDay;
            PlayActivity.selected_ipa = MainActivity.IPAofTheDay;
        }

        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        long when = System.currentTimeMillis();

        //Building our custom Notification
        NotificationCompat.Builder mBuilder =
                (NotificationCompat.Builder) new NotificationCompat.Builder(context)
                        .setSmallIcon(R.drawable.ic_notification_icon)
                        .setContentTitle("Say it! Reminder ")
                        .setContentText("Hey! Here's your word of the day: " + MainActivity.wordOfTheDay)
                        .setSound(alarmSound)
                        .setVibrate(new long[]{300, 300, 300, 300, 300})
                        .setAutoCancel(true).setWhen(when);


        // Creates an explicit intent for an Activity in your app
        Intent resultIntent = new Intent(context, PlayActivity.class);
        resultIntent.putExtra(PlayActivity.PLAY_WORD, MainActivity.wordOfTheDay);
        resultIntent.putExtra(PlayActivity.PLAY_IPA, MainActivity.IPAofTheDay);
        resultIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        // The stack builder object will contain an artificial back stack for the
        // started Activity. This ensures that navigating backward from the Activity leads out of
        // your application to the Home screen.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        // Adds the back stack for the Intent (but not the Intent itself)

        //TODO Stacking activities on Notification. Shouldn't this add MainActivity as previous activity of PlayActivity?
        stackBuilder.addParentStack(PlayActivity.class);
        // Adds the Intent that starts the Activity to the top of the stack
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



        if(mode_code == 1){
            //Daily Notifications
            if (calendar.getTimeInMillis() < System.currentTimeMillis()) {
                calendar.setTimeInMillis(calendar.getTimeInMillis() + AlarmManager.INTERVAL_DAY);
            }

            alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, notificationPendingIntent);
        }

        else if(mode_code == 2){
            //Weekly Notifications

            if (calendar.getTimeInMillis() < System.currentTimeMillis()) {
                calendar.setTimeInMillis(calendar.getTimeInMillis() + AlarmManager.INTERVAL_DAY*7);
            }

            alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY*7, notificationPendingIntent);
        }

        //TODO remove Toast
        Toast.makeText(context, "Alarm Set", Toast.LENGTH_SHORT).show();

        pm.setComponentEnabledSetting(receiver,
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP);
    }
}
