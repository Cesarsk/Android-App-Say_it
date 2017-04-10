package com.example.cesarsk.say_it.settings;

/**
 * Created by Andrea Croce on 16/03/2017.
 */

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.TimePicker;

import com.example.cesarsk.say_it.ui.MainActivity;
import com.example.cesarsk.say_it.utility.UtilitySharedPrefs;

public class TimePreference extends DialogPreference implements
        TimePicker.OnTimeChangedListener {

    private static final String VALIDATION_EXPRESSION = "[0-2]*[0-9]:[0-5]*[0-9]";

    private String defaultValue;
    Context context;

    public TimePreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        setPersistent(true);
        UtilitySharedPrefs.loadSettingsPrefs(context);
        defaultValue = getTimeFromSharedPrefs();
        setSummary(getTimeFromSharedPrefs());
    }

    @Override
    protected View onCreateDialogView() {
        TimePicker time_picker = new TimePicker(getContext());
        time_picker.setIs24HourView(true);
        time_picker.setOnTimeChangedListener(this);

        int h = getHour();
        int m = getMinute();
        //TODO Solve deprecated method issue
        if (h >= 0 && h < 24)  time_picker.setCurrentHour(h);
        if ( m >= 0 && m < 60) time_picker.setCurrentMinute(m);

        return time_picker;
    }

    @Override
    public void onTimeChanged(TimePicker view, int hour, int minute) {
        //persistString(String.format("%02d", hour) + ":" + String.format("%02d", minute));
        String formatted_hour = String.format("%02d", hour);
        String formatted_minute = String.format("%02d", minute);
        UtilitySharedPrefs.savePrefs(view.getContext(), formatted_hour, MainActivity.DEFAULT_NOTIFICATION_HOUR_KEY);
        UtilitySharedPrefs.savePrefs(view.getContext(), formatted_minute, MainActivity.DEFAULT_NOTIFICATION_MINUTE_KEY);
        setSummary(getTimeFromSharedPrefs());
    }

    @Override
    public void setDefaultValue(Object defaultValue) {
        super.setDefaultValue(defaultValue);
        if (!(defaultValue instanceof String)) {
            return;
        }

        if (!((String) defaultValue).matches(VALIDATION_EXPRESSION)) {
            return;
        }

        this.defaultValue = (String) defaultValue;
    }

    private int getHour() {
        String time = getTimeFromSharedPrefs();
        if (time == null || !time.matches(VALIDATION_EXPRESSION)) {
            return -1;
        }
        return Integer.valueOf(time.split(":")[0]);
    }

    private int getMinute() {
        String time = getTimeFromSharedPrefs();
        if (time == null || !time.matches(VALIDATION_EXPRESSION)) {
            return -1;
        }
        return Integer.valueOf(time.split(":")[1]);
    }

    private String get_hour(){
        String time = getPersistedString(this.defaultValue);
        if (time == null || !time.matches(VALIDATION_EXPRESSION)) {
            return null;
        }
        return time.split(":")[0];
    }

    private String get_minute(){
        String time = getPersistedString(this.defaultValue);
        if (time == null || !time.matches(VALIDATION_EXPRESSION)) {
            return null;
        }
        return time.split(":")[1];
    }

    private String getTimeUnitfromSharedPrefs(String prefs_key){
        UtilitySharedPrefs.loadSettingsPrefs(context);
        SharedPreferences prefs = context.getSharedPreferences(MainActivity.PREFS_NAME, Context.MODE_PRIVATE);
        return prefs.getString(prefs_key, "00");

    }

    private String getTimeFromSharedPrefs() {
        String hour = getTimeUnitfromSharedPrefs(MainActivity.DEFAULT_NOTIFICATION_HOUR_KEY);
        String minute = getTimeUnitfromSharedPrefs(MainActivity.DEFAULT_NOTIFICATION_MINUTE_KEY);
        return hour + ":" + minute;
    }
}