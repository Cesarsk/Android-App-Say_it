package com.example.cesarsk.say_it.settings;

/**
 * Created by Andrea Croce on 16/03/2017.
 */

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.DialogPreference;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.cesarsk.say_it.ui.SettingsActivity;

public class TimePreference extends DialogPreference implements
        TimePicker.OnTimeChangedListener {

    private static final String VALIDATION_EXPRESSION = "[0-2]*[0-9]:[0-5]*[0-9]";

    private String defaultValue = "12:00";

    public TimePreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        setPersistent(true);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        defaultValue = prefs.getString(getKey(), defaultValue);
        setSummary(getFormattedSummary());
    }

    @Override
    protected View onCreateDialogView() {
        TimePicker time_picker = new TimePicker(getContext());
        time_picker.setIs24HourView(true);
        time_picker.setOnTimeChangedListener(this);

        Log.d("TimePicker","Current default ="+getPersistedString(this.defaultValue));

        int h = getHour();
        int m = getMinute();
        if (h >= 0 && h < 24)  time_picker.setCurrentHour(h);
        if ( m >= 0 && m < 60) time_picker.setCurrentMinute(m);

        return time_picker;
    }

    @Override
    public void onTimeChanged(TimePicker view, int hour, int minute) {
        setSummary(getFormattedSummary());
        persistString(String.format("%02d", hour) + ":" + String.format("%02d", minute));
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
        String time = getPersistedString(this.defaultValue);
        if (time == null || !time.matches(VALIDATION_EXPRESSION)) {
            return -1;
        }
        return Integer.valueOf(time.split(":")[0]);
    }

    private int getMinute() {
        String time = getPersistedString(this.defaultValue);
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
        String hour = time.split(":")[0];
        return hour;
    }

    private String get_minute(){
        String time = getPersistedString(this.defaultValue);
        if (time == null || !time.matches(VALIDATION_EXPRESSION)) {
            return null;
        }
        String minute = time.split(":")[1];
        return minute;
    }

    public String getFormattedSummary() {
        String h = get_hour();
        String m = get_minute();
        return h+":"+m;
    }
}