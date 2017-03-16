package com.example.cesarsk.say_it;

/**
 * Created by Andrea Croce on 16/03/2017.
 */
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.preference.DialogPreference;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TimePicker;

public class TimePreference extends DialogPreference {
    private final Context mContext;
    private int lastHour = 0;
    private int lastMinute = 0;
    private TimePicker picker = null;
    public static final String DEFAULT_TIME = "12:30";


    public static int getHour(String time) {
        String[] pieces = time.split(":");

        return Integer.parseInt(pieces[0]);
    }

    public static int getMinute(String time) {
        String[] pieces = time.split(":");

        return Integer.parseInt(pieces[1]);
    }

    public TimePreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        setPositiveButtonText("Set");
        setNegativeButtonText("Cancel");

        lastHour = getHour(DEFAULT_TIME);
        lastMinute = getMinute(DEFAULT_TIME);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        //setSummary(getFormattedSummary(prefs.getString(SettingsActivity.PREF_TIME, DEFAULT_TIME)));

        setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference pref, Object value) {
                pref.setSummary(getFormattedSummary((String) value));
                return true;
            }
        });
    }

    @Override
    protected View onCreateDialogView() {
        picker = new TimePicker(getContext());
        picker.setIs24HourView(true);
        return(picker);
    }

    @Override
    protected void onBindDialogView(View v) {
        super.onBindDialogView(v);
        picker.setCurrentHour(lastHour);
        picker.setCurrentMinute(lastMinute);
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        super.onDialogClosed(positiveResult);
        if (positiveResult) {
            lastHour=picker.getCurrentHour();
            lastMinute=picker.getCurrentMinute();

            String time = String.valueOf(lastHour)+":"+String.valueOf(lastMinute);
            if (callChangeListener(time)) {
                persistString(time);
            }
        }
    }

    @Override
    protected Object onGetDefaultValue(TypedArray a, int index) {
        return(a.getString(index));
    }

    @Override
    protected void onSetInitialValue(boolean restoreValue, Object defaultValue) {
        String time=null;

        if (restoreValue) {
            if (defaultValue==null) {
                time=getPersistedString(DEFAULT_TIME);
            }
            else {
                time=getPersistedString(defaultValue.toString());
            }
        }
        else {
            time=defaultValue.toString();
        }

        lastHour=getHour(time);
        lastMinute=getMinute(time);
    }

    public String getFormattedSummary(String time) {

        int h = getHour(time);
        int m = getMinute(time);
;
        //String hours = mContext.getResources().getQuantityString(R.plurals.hour, h, h);
        //String minutes = mContext.getResources().getQuantityString(R.plurals.minute, m, m);
        return h+ " " + m;
    }
}