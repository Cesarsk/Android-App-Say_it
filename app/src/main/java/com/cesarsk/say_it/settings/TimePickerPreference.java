package com.cesarsk.say_it.settings;

import android.content.Context;
import android.preference.DialogPreference;
import android.text.format.DateFormat;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TimePicker;

import com.cesarsk.say_it.NotificationReceiver;
import com.cesarsk.say_it.ui.MainActivity;
import com.cesarsk.say_it.utility.UtilitySharedPrefs;

import java.util.Calendar;
import java.util.Locale;

/**
 * Created by cesarsk on 27/04/17.
 */

public class TimePickerPreference extends DialogPreference implements TimePicker.OnTimeChangedListener {

    private final Context context;
    private TimePicker time_picker = null;
    private int selected_hour;
    private int selected_minute;

    public TimePickerPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        setSummary(getTimeFromSharedPrefs());
    }


    @Override
    protected View onCreateDialogView() {
        time_picker = new TimePicker((getContext()));
        time_picker.setOnTimeChangedListener(this);

        return time_picker;
    }

    @Override
    protected void onBindDialogView(View view) {
        super.onBindDialogView(view);

        if (DateFormat.is24HourFormat(context)) {
            time_picker.setIs24HourView(true);
        }

        //retrieving hour and minute
        final Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        time_picker.setCurrentHour(hour);
        int minute = c.get(Calendar.MINUTE);
        time_picker.setCurrentMinute(minute);
    }

    @Override
    public void onTimeChanged(TimePicker timePicker, int hour, int minute) {
        selected_hour = hour;
        selected_minute = minute;
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        super.onDialogClosed(positiveResult);

        if (positiveResult) {
            String formatted_hour = String.format(Locale.getDefault(), "%02d", selected_hour);
            String formatted_minute = String.format(Locale.getDefault(), "%02d", selected_minute);
            UtilitySharedPrefs.savePrefs(context, formatted_hour, MainActivity.DEFAULT_NOTIFICATION_HOUR_KEY);
            UtilitySharedPrefs.savePrefs(context, formatted_minute, MainActivity.DEFAULT_NOTIFICATION_MINUTE_KEY);
            setSummary(getTimeFromSharedPrefs());
            //Change 24-12 system in schedule notification
            NotificationReceiver.scheduleNotification(context, selected_hour, selected_minute, MainActivity.DEFAULT_NOTIFICATION_RATE);
        }
    }

    private String getTimeFromSharedPrefs() {
        UtilitySharedPrefs.loadSettingsPrefs(context);
        String hour = MainActivity.DEFAULT_NOTIFICATION_HOUR;
        String minute = MainActivity.DEFAULT_NOTIFICATION_MINUTE;
        return hour + ":" + minute;
    }
}

