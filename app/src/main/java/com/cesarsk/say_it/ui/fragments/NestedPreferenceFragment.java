package com.cesarsk.say_it.ui.fragments;

import android.app.ActivityOptions;
import android.content.Intent;
import android.graphics.Color;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;

import com.cesarsk.say_it.notifications.NotificationReceiver;
import com.cesarsk.say_it.R;
import com.cesarsk.say_it.ui.activities.FileTextActivity;
import com.cesarsk.say_it.ui.activities.MainActivity;
import com.cesarsk.say_it.utility.UtilitySharedPrefs;

/**
 * Created by team on 30/03/2017.
 */

public class NestedPreferenceFragment extends PreferenceFragment {

    public static final int NESTED_SCREEN_1_KEY = 1;
    public static final int NESTED_SCREEN_2_KEY = 2;
    private static final String TAG_KEY = "NESTED_KEY";

    public static NestedPreferenceFragment newInstance(int key) {
        NestedPreferenceFragment fragment = new NestedPreferenceFragment();
        // supply arguments to bundle.
        Bundle args = new Bundle();
        args.putInt(TAG_KEY, key);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        int selected_fragment_layout = checkPreferenceResource();

        /*the following is the scheme used in this class:
         |- Notification Settings
           |- Notification Rate
           |- Notification Time (define ONLY in XML, not JAVA)
         |- Open Source Licenses
           |- BottomBar
           |- Easy Rating Dialog
           |- MaterialShowCase
           |- Gson
           |- Wordlist
           |- Snowfall

            */
        if (selected_fragment_layout == 1) {
            final ListPreference notification_rate = (ListPreference) getPreferenceManager().findPreference("default_notification_rate");
            final Spannable notification_rate_title = new SpannableString("Set Notification Rate");

            notification_rate_title.setSpan(new ForegroundColorSpan(Color.DKGRAY), 0, notification_rate_title.length(), 0);
            notification_rate.setDialogTitle(notification_rate_title);

            notification_rate.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    String new_value = newValue.toString();
                    UtilitySharedPrefs.savePrefs(getActivity(), new_value, MainActivity.DEFAULT_NOTIFICATION_RATE_KEY);
                    UtilitySharedPrefs.loadSettingsPrefs(getActivity());
                    int hour = Integer.parseInt(MainActivity.DEFAULT_NOTIFICATION_HOUR);
                    int minute = Integer.parseInt(MainActivity.DEFAULT_NOTIFICATION_MINUTE);
                    NotificationReceiver.scheduleNotification(getActivity(), hour, minute, MainActivity.DEFAULT_NOTIFICATION_RATE);
                    return true;
                }
            });
        } else if (selected_fragment_layout == 2) {
            Preference number_one = getPreferenceManager().findPreference("number_1");
            number_one.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    final Intent preference_intent = new Intent(getActivity(), FileTextActivity.class);
                    Bundle args = new Bundle();
                    args.putString(FileTextActivity.PREFERENCE, "bottom_bar");
                    preference_intent.putExtras(args);
                    startActivity(preference_intent, ActivityOptions.makeSceneTransitionAnimation(getActivity()).toBundle());
                    return false;
                }
            });

            Preference number_two = getPreferenceManager().findPreference("number_2");
            number_two.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    final Intent preference_intent = new Intent(getActivity(), FileTextActivity.class);
                    Bundle args = new Bundle();
                    args.putString(FileTextActivity.PREFERENCE, "easy_rating_dialog");
                    preference_intent.putExtras(args);
                    startActivity(preference_intent, ActivityOptions.makeSceneTransitionAnimation(getActivity()).toBundle());
                    return false;
                }
            });

            Preference number_three = getPreferenceManager().findPreference("number_3");
            number_three.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    final Intent preference_intent = new Intent(getActivity(), FileTextActivity.class);
                    Bundle args = new Bundle();
                    args.putString(FileTextActivity.PREFERENCE, "material_show_case");
                    preference_intent.putExtras(args);
                    startActivity(preference_intent, ActivityOptions.makeSceneTransitionAnimation(getActivity()).toBundle());
                    return false;
                }
            });

            Preference number_four = getPreferenceManager().findPreference("number_4");
            number_four.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    final Intent preference_intent = new Intent(getActivity(), FileTextActivity.class);
                    Bundle args = new Bundle();
                    args.putString(FileTextActivity.PREFERENCE, "gson");
                    preference_intent.putExtras(args);
                    startActivity(preference_intent, ActivityOptions.makeSceneTransitionAnimation(getActivity()).toBundle());
                    return false;
                }
            });

            Preference number_five = getPreferenceManager().findPreference("number_5");
            number_five.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    final Intent preference_intent = new Intent(getActivity(), FileTextActivity.class);
                    Bundle args = new Bundle();
                    args.putString(FileTextActivity.PREFERENCE, "wordlist");
                    preference_intent.putExtras(args);
                    startActivity(preference_intent, ActivityOptions.makeSceneTransitionAnimation(getActivity()).toBundle());
                    return false;
                }
            });

            Preference number_six = getPreferenceManager().findPreference("number_6");
            number_six.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    final Intent preference_intent = new Intent(getActivity(), FileTextActivity.class);
                    Bundle args = new Bundle();
                    args.putString(FileTextActivity.PREFERENCE, "snowfall");
                    preference_intent.putExtras(args);
                    startActivity(preference_intent, ActivityOptions.makeSceneTransitionAnimation(getActivity()).toBundle());
                    return false;
                }
            });
        }
    }

    private int checkPreferenceResource() {
        int key = getArguments().getInt(TAG_KEY);
        // Load the preferences from an XML resource
        switch (key) {
            case NESTED_SCREEN_1_KEY:
                addPreferencesFromResource(R.xml.notification_preference);
                return 1;

            case NESTED_SCREEN_2_KEY:
                addPreferencesFromResource(R.xml.open_licenses_preference);
                return 2;

            default:
                return 0;
        }
    }
}