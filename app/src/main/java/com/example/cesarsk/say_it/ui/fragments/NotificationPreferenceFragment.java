package com.example.cesarsk.say_it.ui.fragments;

import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.os.Bundle;

import com.example.cesarsk.say_it.R;

/**
 * Created by lucac on 30/03/2017.
 */

public class NotificationPreferenceFragment extends PreferenceFragment {

    public static final int NESTED_SCREEN_1_KEY = 1;
    static private int index_notification_rate = 0; //DO NOT REMOVE THIS
    private static final String TAG_KEY = "button_notification";

    public static NotificationPreferenceFragment newInstance(int key) {
        NotificationPreferenceFragment fragment = new NotificationPreferenceFragment();
        // supply arguments to bundle.
        Bundle args = new Bundle();
        args.putInt(TAG_KEY, key);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        checkPreferenceResource();

        final ListPreference notification_rate = (ListPreference) getPreferenceManager().findPreference("default_notification_rate");
        //final CharSequence choice = default_accent.getEntry();
        // Log.i("DEFAULT = AMERICAN", (String) choice);
        notification_rate.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                String new_value = newValue.toString();
                index_notification_rate = notification_rate.findIndexOfValue(new_value);
                return true;
            }
        });
    }

    private void checkPreferenceResource() {
        int key = getArguments().getInt(TAG_KEY);
        // Load the preferences from an XML resource
        switch (key) {
            case NESTED_SCREEN_1_KEY:
                addPreferencesFromResource(R.xml.notification_preference);
                break;

            default:
                break;
        }
    }

}