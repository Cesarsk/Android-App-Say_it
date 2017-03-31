package com.example.cesarsk.say_it.ui.fragments;

import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import com.example.cesarsk.say_it.R;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.os.Bundle;

import com.example.cesarsk.say_it.R;

/**
 * Created by Andrea Croce on 31/03/2017.
 */

public class OpenLicencesFragment extends PreferenceFragment {

    public static final int NESTED_SCREEN_2_KEY = 2;

    private static final String TAG_KEY = "button_notification";

    public static OpenLicencesFragment newInstance(int key) {
        OpenLicencesFragment fragment = new OpenLicencesFragment();
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

        /*
        Preference number_one = getPreferenceManager().findPreference("number_1");
        number_one.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                return false;
            }
        });

        Preference number_two = getPreferenceManager().findPreference("number_2");
        number_two.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                return false;
            }
        });

        Preference number_three = getPreferenceManager().findPreference("number_3");
        number_three.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                return false;
            }
        });

        Preference number_four = getPreferenceManager().findPreference("number_4");
        number_four.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                return false;
            }
        });
        */
    }

    private void checkPreferenceResource() {
        int key = getArguments().getInt("open_source_licenses");
        // Load the preferences from an XML resource
        switch (key) {
            case NESTED_SCREEN_2_KEY:
                addPreferencesFromResource(R.xml.open_licenses_preference);
                break;

            default:
                break;
        }
    }
}