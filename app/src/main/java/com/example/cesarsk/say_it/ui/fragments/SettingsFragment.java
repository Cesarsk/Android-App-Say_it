package com.example.cesarsk.say_it.ui.fragments;


import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.preference.DialogPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.util.Log;
import android.widget.Toast;
import com.example.cesarsk.say_it.R;
import com.example.cesarsk.say_it.settings.TimePreference;
import com.example.cesarsk.say_it.ui.MainActivity;
import com.example.cesarsk.say_it.utility.Utility;

import static com.example.cesarsk.say_it.utility.Utility.rateUs;
import static com.example.cesarsk.say_it.utility.Utility.shareToMail;

/**
 * A simple {@link Fragment} subclass.
 */
public class SettingsFragment extends PreferenceFragment{
    private String emails[] = {"luca.cesarano1@gmail.com"};
    static private int index_default_accent = 0;
    static private int index_notification_rate = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
        //SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());

        Preference rate_us = (Preference) getPreferenceManager().findPreference("rate_us");
        rate_us.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                rateUs(getActivity());
                return false;
            }
        });

        Preference contact_us = getPreferenceManager().findPreference("contact_us");
        contact_us.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                shareToMail(emails, "[CONTACT US - SAY IT!]", getActivity());
                return false;
            }
        });

        Preference bug_report = getPreferenceManager().findPreference("bug_report");
        bug_report.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                shareToMail(emails, "[CONTACT US - SAY IT!]", getActivity());
                return false;
            }
        });

        final Preference delete_recordings = getPreferenceManager().findPreference("delete_recordings");
        delete_recordings.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                new AlertDialog.Builder(getActivity())
                        .setTitle("Delete Recordings")
                        .setMessage("Are you sure you want to delete all recordings?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                Utility.delete_recordings();
                                Toast.makeText(getActivity(), "Recordings deleted!", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                //do nothing
                            }
                        })
                        .show();
                return true;
            }
        });


        final ListPreference notification_rate = (ListPreference) getPreferenceManager().findPreference("default_notification_rate");
                Log.i("Say It!", notification_rate.getValue());
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

        final ListPreference default_accent = (ListPreference) getPreferenceManager().findPreference("default_accent");
        default_accent.setSummary(default_accent.getEntry());
        final CharSequence choice = default_accent.getValue();
        Log.i("DEFAULT LANGUAGE SETTED", (String) choice);

        default_accent.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                String new_value = newValue.toString();
                index_default_accent = default_accent.findIndexOfValue(new_value);
                CharSequence[] entries = default_accent.getEntries();

                if (MainActivity.DEFAULT_ACCENT.equals("0")) {
                    MainActivity.DEFAULT_ACCENT = new_value;
                    default_accent.setSummary(default_accent.getEntries()[default_accent.findIndexOfValue(new_value)]);
                    Toast.makeText(getActivity(), String.valueOf(entries[index_default_accent]), Toast.LENGTH_SHORT).show();
                    Log.i("DEFAULT", String.valueOf(entries[index_default_accent]));
                } else if (MainActivity.DEFAULT_ACCENT.equals("1")) {
                    MainActivity.DEFAULT_ACCENT = new_value;
                    default_accent.setSummary(default_accent.getEntries()[default_accent.findIndexOfValue(new_value)]);
                    Toast.makeText(getActivity(),String.valueOf(entries[index_default_accent]), Toast.LENGTH_SHORT).show();
                    Log.i("DEFAULT", String.valueOf(entries[index_default_accent]));
                }
                return true;
            }
        });
    }

    public static int getIndex_default_accent() {
        return index_default_accent;
    }

    public static int setIndex(int index) {
        SettingsFragment.index_default_accent = index;
        return index;
    }
}
