package com.cesarsk.say_it.ui.fragments;

import android.app.ActivityOptions;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.v4.content.IntentCompat;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.widget.Toast;

import com.cesarsk.say_it.R;
import com.cesarsk.say_it.ui.activities.FileTextActivity;
import com.cesarsk.say_it.ui.activities.MainActivity;
import com.cesarsk.say_it.ui.activities.PlayActivity;
import com.cesarsk.say_it.utility.LCSecurity;
import com.cesarsk.say_it.utility.Utility;
import com.cesarsk.say_it.utility.UtilitySharedPrefs;
import com.cesarsk.say_it.utility.utility_aidl.IabHelper;
import com.cesarsk.say_it.utility.utility_aidl.IabResult;
import com.cesarsk.say_it.utility.utility_aidl.Inventory;
import com.cesarsk.say_it.utility.utility_aidl.Purchase;

import java.util.Random;

import static com.cesarsk.say_it.utility.Utility.rateUs;
import static com.cesarsk.say_it.utility.Utility.setColorByTheme;
import static com.cesarsk.say_it.utility.Utility.shareToMail;

/**
 * A simple {@link Fragment} subclass.
 */
public class SettingsFragment extends PreferenceFragment implements Preference.OnPreferenceClickListener {
    private final String[] emails = {"sayit.edu@gmail.com"};
    static private int index_default_accent = 0;
    static private int index_default_theme = 0;
    static private int index_default_vibration = 1;
    private Callback mCallback;
    private static final String KEY_1 = "button_notification";
    private static final String KEY_2 = "open_source_licenses";
    private IabHelper mHelper;
    private IabHelper.QueryInventoryFinishedListener mQueryFinishedListener;
    private IabHelper.OnIabPurchaseFinishedListener mIabPurchaseFinishedListener;

    public interface Callback {
        void onNestedPreferenceSelected(int key);
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
    public void onDestroyView() {
        super.onDestroyView();
        if (mHelper != null) try {
            mHelper.dispose();
        } catch (IabHelper.IabAsyncInProgressException e) {
            e.printStackTrace();
        }
        mHelper = null;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);

        PackageInfo pInfo = null;
        try {
            pInfo = getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        String version = pInfo.versionName;
        final Preference app_version = getPreferenceManager().findPreference("app_version");
        app_version.setSummary("Version: " + version + " (Click for Privacy Policy)");

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

        Preference rate_us = getPreferenceManager().findPreference("rate_us");
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
                Utility.openURL(getActivity(), "https://lucacesaranoblog.wordpress.com/2017/07/18/about-us/");
                return false;
            }
        });

        final Preference eula = getPreferenceManager().findPreference("eula");
        eula.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Utility.openURL(getActivity(), "https://lucacesaranoblog.wordpress.com/2017/04/28/say-it-eula-agreement/");
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

        final Preference other_apps = getPreferenceManager().findPreference("other_apps");
        other_apps.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Utility.openURL(getActivity(), "https://play.google.com/store/apps/developer?id=Cesarsk+Dev+Team");
                return false;
            }
        });

        final Preference voice_settings = getPreferenceManager().findPreference("tts_settings");
        voice_settings.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Intent intent = new Intent();
                intent.setAction("com.android.settings.TTS_SETTINGS");
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
                return false;
            }
        });


        // compute your public key and store it in base64EncodedPublicKey
        mHelper = new IabHelper(getActivity(), LCSecurity.base64EncodedPublicKey);
        mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
            public void onIabSetupFinished(IabResult result) {
                if (!result.isSuccess()) {
                    // Oh no, there was a problem.
                    if (MainActivity.isLoggingEnabled)
                        Log.d("Say It!", "Problem setting up In-app Billing: " + result);
                }
                // Hooray, IAB is fully set up!
                if (MainActivity.isLoggingEnabled)
                    Log.d("Say It!", "Hooray. IAB is fully set up!" + result);
            }
        });

        mIabPurchaseFinishedListener = new IabHelper.OnIabPurchaseFinishedListener() {
            @Override
            public void onIabPurchaseFinished(IabResult result, Purchase info) {
                if (result.isFailure()) {
                    Toast.makeText(getActivity(), "Purchase Failed! Perhaps have you already purchased the item?", Toast.LENGTH_SHORT).show();
                } else if (info.getSku().equals(PlayActivity.no_ads_in_app)) {
                    UtilitySharedPrefs.loadAdsStatus(getActivity());
                    UtilitySharedPrefs.savePrefs(getActivity(), true, MainActivity.NO_ADS_STATUS_KEY);
                }
            }
        };

        mQueryFinishedListener = new IabHelper.QueryInventoryFinishedListener() {
            public void onQueryInventoryFinished(IabResult result, Inventory inventory) {
                if (result.isFailure()) {
                    if(MainActivity.isLoggingEnabled) Toast.makeText(getActivity(), "Query Failed!", Toast.LENGTH_SHORT).show();
                    return;
                }

                //Open Purchase Dialog
                try {
                    mHelper.flagEndAsync();
                    mHelper.launchPurchaseFlow(getActivity(), PlayActivity.no_ads_in_app, 64000, mIabPurchaseFinishedListener);
                } catch (IabHelper.IabAsyncInProgressException e) {
                    e.printStackTrace();
                }
            }
        };

        /* final Preference remove_ads = getPreferenceManager().findPreference("remove_ads");
        if (MainActivity.NO_ADS) {
            remove_ads.setEnabled(false);
            remove_ads.setSummary("Thank you for supporting us ❤");
        } else {
            remove_ads.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    //helper to remove ads
                    List<String> additionalSkuList = new ArrayList<>();
                    additionalSkuList.add(PlayActivity.no_ads_in_app);
                    try {
                        mHelper.flagEndAsync();
                        mHelper.queryInventoryAsync(true, additionalSkuList, mQueryFinishedListener);
                    } catch (IabHelper.IabAsyncInProgressException e) {
                        e.printStackTrace();
                    }
                    return false;
                }
            });
        } */


        final Preference reset_tutorial = getPreferenceManager().findPreference("reset_showcase");
        reset_tutorial.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Random rand = new Random();
                int randomNum = rand.nextInt((10000 - 10) + 1) + 10; //(max - min) + 1 + min
                MainActivity.id_showcase_playactivity = "" + randomNum;
                Toast.makeText(getActivity(), "Tutorial has been reset", Toast.LENGTH_SHORT).show();
                //MainActivity.id_showcase_fragments = "" + randomNum2;
                //int randomNum2 = rand.nextInt((10000 - 10) + 1) + 10; //(max - min) + 1 + min //These two statements are commented because MaterialShowCase often crashes on fragments and while pressing the back button.
                return false;
            }
        });

        //setting an own Alert Dialog's title color
        final Spannable title = new SpannableString("Delete Recordings");
        title.setSpan(new ForegroundColorSpan(Color.DKGRAY), 0, title.length(), 0);
        final Spannable message = new SpannableString("Are you sure you want to delete all recordings?");
        message.setSpan(new ForegroundColorSpan(Color.GRAY), 0, message.length(), 0);

        final Preference delete_recordings = getPreferenceManager().findPreference("delete_recordings");
        delete_recordings.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                new AlertDialog.Builder(getActivity())
                        .setTitle(title)
                        .setMessage(message)
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

        final ListPreference button_vibration = (ListPreference) getPreferenceManager().findPreference("button_vibration");
        button_vibration.setSummary(button_vibration.getEntry());
        final Spannable default_vibration_settings_title = new SpannableString("Vibration Settings");
        default_vibration_settings_title.setSpan(new ForegroundColorSpan(Color.DKGRAY), 0, default_vibration_settings_title.length(), 0);
        button_vibration.setDialogTitle(default_vibration_settings_title);

        button_vibration.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                String new_value = newValue.toString();
                index_default_vibration = button_vibration.findIndexOfValue(new_value);
                CharSequence[] entries = button_vibration.getEntries();
                UtilitySharedPrefs.savePrefs(getActivity(), new_value, MainActivity.DEFAULT_VIBRATION_KEY);
                button_vibration.setSummary(button_vibration.getEntries()[button_vibration.findIndexOfValue(new_value)]);
                Toast.makeText(getActivity(), String.valueOf(entries[index_default_vibration]), Toast.LENGTH_SHORT).show();
                UtilitySharedPrefs.loadSettingsPrefs(context);
                return true;
            }
        });


        final ListPreference default_accent = (ListPreference) getPreferenceManager().findPreference("default_accent");
        default_accent.setSummary(default_accent.getEntry());
        final Spannable default_accent_title = new SpannableString("Default Accent");
        default_accent_title.setSpan(new ForegroundColorSpan(Color.DKGRAY), 0, default_accent_title.length(), 0);
        default_accent.setDialogTitle(default_accent_title);

        default_accent.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                String new_value = newValue.toString();
                index_default_accent = default_accent.findIndexOfValue(new_value);
                CharSequence[] entries = default_accent.getEntries();

                UtilitySharedPrefs.savePrefs(getActivity(), new_value, MainActivity.DEFAULT_ACCENT_KEY);
                default_accent.setSummary(default_accent.getEntries()[default_accent.findIndexOfValue(new_value)]);
                //Toast.makeText(getActivity(), String.valueOf(entries[index_default_accent]), Toast.LENGTH_SHORT).show();
                UtilitySharedPrefs.loadSettingsPrefs(context);
                return true;
            }
        });

        final ListPreference theme_selector = (ListPreference) getPreferenceManager().findPreference("theme_selector");
        final Spannable theme_selector_title = new SpannableString("Theme");
        theme_selector_title.setSpan(new ForegroundColorSpan(Color.DKGRAY), 0, theme_selector_title.length(), 0);
        theme_selector.setDialogTitle(theme_selector_title);

        theme_selector.setSummary(theme_selector.getEntry());

        theme_selector.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                String new_value = newValue.toString();
                index_default_theme = theme_selector.findIndexOfValue(new_value);
                CharSequence[] entries = theme_selector.getEntries();
                UtilitySharedPrefs.savePrefs(getActivity(), new_value, MainActivity.DEFAULT_THEME_KEY);
                theme_selector.setSummary(theme_selector.getEntries()[theme_selector.findIndexOfValue(new_value)]);
                //Toast.makeText(getActivity(), String.valueOf(entries[index_default_theme]), Toast.LENGTH_SHORT).show();
                UtilitySharedPrefs.loadSettingsPrefs(context);
                Toast.makeText(getActivity(), "Done! Reboot to see changes.", Toast.LENGTH_SHORT).show();
                return true;
            }
        });

        Preference acknowledgements = getPreferenceManager().findPreference("acknowledgements");
        acknowledgements.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener()
        {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                final Intent preference_intent = new Intent(getActivity(), FileTextActivity.class);
                Bundle args = new Bundle();
                args.putString(FileTextActivity.PREFERENCE, "acknowledgements");
                preference_intent.putExtras(args);
                startActivity(preference_intent, ActivityOptions.makeSceneTransitionAnimation(getActivity()).toBundle());
                return false;
            }
        });

        app_version.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Utility.openURL(getActivity(), "https://lucacesaranoblog.wordpress.com/2017/04/18/privacy-policy/");
                return false;
            }
        });
    }
}
