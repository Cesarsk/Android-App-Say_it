package com.example.cesarsk.say_it;

/**
 * Created by Andrea Croce on 16/03/2017.
 */

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.DialogPreference;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.TimePicker;

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

        /*setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference pref, Object value) {
                //pref.setSummary(getFormattedSummary((String) value));
                return true;
            }
        });*/
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

        persistString(String.format("%02d:%02d",hour , minute));
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

    public String getFormattedSummary() {
        int h = getHour();
        int m = getMinute();
        return h+ ":" + m;
    }
}