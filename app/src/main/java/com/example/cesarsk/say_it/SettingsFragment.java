package com.example.cesarsk.say_it;


import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.widget.Toast;
import static com.example.cesarsk.say_it.Utility.rateUs;
import static com.example.cesarsk.say_it.Utility.shareToMail;


/**
 * A simple {@link Fragment} subclass.
 */
public class SettingsFragment extends PreferenceFragment {
    private String emails[] = {"luca.cesarano1@gmail.com"};

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);

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

        final ListPreference default_accent = (ListPreference) getPreferenceManager().findPreference("default_accent");

        default_accent.setSummary(default_accent.getEntry());
        //final CharSequence choice = default_accent.getEntry();
       // Log.i("DEFAULT = AMERICAN", (String) choice);

        default_accent.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                String new_value = newValue.toString();
                int index = default_accent.findIndexOfValue(new_value);
                CharSequence[] entries = default_accent.getEntries();

                if(index == 0) {
                    default_accent.setSummary(default_accent.getEntries()[default_accent.findIndexOfValue(new_value)]);
                    Toast.makeText(getActivity(),String.valueOf(entries[index]),Toast.LENGTH_SHORT).show();
                    //Log.i("DEFAULT", String.valueOf(entries[index]));
                }
                else if (index == 1){
                    Toast.makeText(getActivity(),String.valueOf(entries[index]),Toast.LENGTH_SHORT).show();
                    default_accent.setSummary(default_accent.getEntries()[default_accent.findIndexOfValue(new_value)]);
                   // Log.i("DEFAULT", String.valueOf(entries[index]));
                }
            return true;
            }
        });

        TimePreference timePreference = (TimePreference) getPreferenceManager().findPreference("time_preference");
        timePreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object o) {
                return false;
            }
        });
    }
}

