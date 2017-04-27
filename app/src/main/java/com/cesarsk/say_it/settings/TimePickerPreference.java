package com.cesarsk.say_it.settings;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.preference.DialogPreference;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.TimePicker;
import android.widget.Toast;

import com.cesarsk.say_it.NotificationReceiver;
import com.cesarsk.say_it.ui.MainActivity;
import com.cesarsk.say_it.utility.UtilitySharedPrefs;

import java.util.Calendar;
import java.util.Locale;

/**
 * Created by cesarsk on 27/04/17.
 */

public class TimePickerPreference extends DialogPreference
        implements TimePickerDialog.OnTimeSetListener {

    Context context;
    public TimePickerPreference(Context context) {
        super(context);
        this.context = context;
    }

    @Override
    protected View onCreateDialogView() {
        TimePicker time_picker = new TimePicker((getContext()));
        if(DateFormat.is24HourFormat(context)) time_picker.setIs24HourView(true);

        //retrieving hour and minute
        final Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY); time_picker.setCurrentHour(hour);
        int minute = c.get(Calendar.MINUTE); time_picker.setCurrentMinute(minute);

        return time_picker;
    }

    @Override
    public void onTimeSet(TimePicker view, int hour, int minute) {
        // Do something with the time chosen by the user
        String formatted_hour = String.format(Locale.getDefault(),"%02d", hour);
        String formatted_minute = String.format(Locale.getDefault(), "%02d", minute);
        UtilitySharedPrefs.savePrefs(view.getContext(), formatted_hour, MainActivity.DEFAULT_NOTIFICATION_HOUR_KEY);
        UtilitySharedPrefs.savePrefs(view.getContext(), formatted_minute, MainActivity.DEFAULT_NOTIFICATION_MINUTE_KEY);
        setSummary(getTimeFromSharedPrefs());

        //Change 24-12 system in schedule notification
        NotificationReceiver.scheduleNotification(view.getContext(), hour, minute, MainActivity.DEFAULT_NOTIFICATION_RATE);
    }

    private String getTimeFromSharedPrefs() {
        UtilitySharedPrefs.loadSettingsPrefs(context);
        String hour = MainActivity.DEFAULT_NOTIFICATION_HOUR;
        String minute = MainActivity.DEFAULT_NOTIFICATION_MINUTE;
        return hour + ":" + minute;
    }
}

