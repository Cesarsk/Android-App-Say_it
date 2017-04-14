package com.cesarsk.say_it.ui.fragments;

import android.app.ActivityOptions;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.util.Log;
import android.widget.Toast;

import com.cesarsk.say_it.R;
import com.cesarsk.say_it.ui.FileTextActivity;
import com.cesarsk.say_it.ui.MainActivity;
import com.cesarsk.say_it.ui.PlayActivity;
import com.cesarsk.say_it.utility.Utility;
import com.cesarsk.say_it.utility.UtilitySharedPrefs;

import java.util.Random;

import static com.cesarsk.say_it.utility.Utility.rateUs;
import static com.cesarsk.say_it.utility.Utility.shareToMail;

/**
 * A simple {@link Fragment} subclass.
 */
public class SettingsFragment extends PreferenceFragment implements Preference.OnPreferenceClickListener {
    private String emails[] = {"luca.cesarano1@gmail.com"};
    static private int index_default_accent = 0;
    private Callback mCallback;
    private static final String KEY_1 = "button_notification";
    private static final String KEY_2 = "open_source_licenses";


    public interface Callback {
        public void onNestedPreferenceSelected(int key);
    }

    @Override
    public boolean onPreferenceClick(Preference preference) {
        // here you should use the same keys as you used in the xml-file
        if (preference.getKey().equals(KEY_1)) {
            mCallback.onNestedPreferenceSelected(NestedPreferenceFragment.NESTED_SCREEN_1_KEY);
        }

        if (preference.getKey().equals(KEY_2)) {
            mCallback.onNestedPreferenceSelected(NestedPreferenceFragment.NESTED_SCREEN_2_KEY);
        }
        return false;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);

        final Context context = getActivity();

        if (context instanceof Callback) {
            mCallback = (Callback) context;
        } else {
            throw new IllegalStateException("Owner must implement URLCallback interface");
        }

        // add listeners for non-default actions
        Preference preference = findPreference(KEY_1);
        preference.setOnPreferenceClickListener(this);

        Preference open_source_licenses = findPreference(KEY_2);
        open_source_licenses.setOnPreferenceClickListener(this);

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

        final Preference about_us = getPreferenceManager().findPreference("about_us");
        about_us.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Utility.openURL(getActivity(), "https://lucacesaranoblog.wordpress.com");
                return false;
            }
        });

        final Preference github = getPreferenceManager().findPreference("github");
        github.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Utility.openURL(getActivity(), "https://github.com/Cesarsk/Say_it");
                return false;
            }
        });

        final Preference reset_tutorial = getPreferenceManager().findPreference("reset_showcase");
        reset_tutorial.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Random rand = new Random();
                int randomNum = rand.nextInt((10000 - 10) + 1) + 10; //(max - min) + 1 + min
                PlayActivity.id_showcase = ""+randomNum;
                Toast.makeText(getActivity(), "Tutorial has been reset", Toast.LENGTH_SHORT).show();
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
                                Utility.delete_recordings(getActivity());
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

                UtilitySharedPrefs.savePrefs(getActivity(), new_value, MainActivity.DEFAULT_ACCENT_KEY);
                default_accent.setSummary(default_accent.getEntries()[default_accent.findIndexOfValue(new_value)]);
                Toast.makeText(getActivity(), String.valueOf(entries[index_default_accent]), Toast.LENGTH_SHORT).show();
                UtilitySharedPrefs.loadSettingsPrefs(context);
                Log.i("DEFAULT", String.valueOf(entries[index_default_accent]));
                return true;
        }
    });

        Preference acknowledgements = getPreferenceManager().findPreference("acknowledgements");
        acknowledgements.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener()

    {
        @Override
        public boolean onPreferenceClick (Preference preference){
        final Intent preference_intent = new Intent(getActivity(), FileTextActivity.class);
        Bundle args = new Bundle();
        args.putString(FileTextActivity.PREFERENCE, "acknowledgements");
        preference_intent.putExtras(args);
        startActivity(preference_intent, ActivityOptions.makeSceneTransitionAnimation(getActivity()).toBundle());
        return false;
    }
    });
}
}
