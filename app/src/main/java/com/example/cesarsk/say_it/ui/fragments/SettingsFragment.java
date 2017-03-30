package com.example.cesarsk.say_it.ui.fragments;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.util.Log;
import android.widget.Toast;
import com.example.cesarsk.say_it.R;
import com.example.cesarsk.say_it.ui.MainActivity;
import com.example.cesarsk.say_it.utility.Utility;

import static com.example.cesarsk.say_it.utility.Utility.rateUs;
import static com.example.cesarsk.say_it.utility.Utility.shareToMail;

/**
 * A simple {@link Fragment} subclass.
 */
public class SettingsFragment extends PreferenceFragment implements Preference.OnPreferenceClickListener {
    private String emails[] = {"luca.cesarano1@gmail.com"};
    static private int index_default_accent = 0;
    private Callback mCallback;
    private static final String KEY_1 = "button_notification";

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof Callback) {
            mCallback = (Callback) context;
        } else {
            throw new IllegalStateException("Owner must implement URLCallback interface");
        }
    }

    public interface Callback {
        public void onNestedPreferenceSelected(int key);
    }

    @Override
    public boolean onPreferenceClick(Preference preference) {
        // here you should use the same keys as you used in the xml-file
        if (preference.getKey().equals(KEY_1)) {
            mCallback.onNestedPreferenceSelected(NotificationPreferenceFragment.NESTED_SCREEN_1_KEY);
        }
        return false;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);

        // add listeners for non-default actions
        Preference preference = findPreference(KEY_1);
        preference.setOnPreferenceClickListener(this);

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

        final Preference donate_us = getPreferenceManager().findPreference("donate_us");
        donate_us.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Utility.openURL(getActivity(), "https://www.paypal.me/cesarsk");
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
