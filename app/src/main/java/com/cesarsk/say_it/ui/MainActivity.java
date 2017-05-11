package com.cesarsk.say_it.ui;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Build;
import android.os.Handler;
import android.provider.Settings;
import android.speech.tts.TextToSpeech;
import android.speech.tts.Voice;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.os.Bundle;
import android.support.v4.util.Pair;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.Toolbar;
import android.transition.Fade;
import android.transition.Slide;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.cesarsk.say_it.NotificationReceiver;
import com.cesarsk.say_it.R;
import com.cesarsk.say_it.ui.fragments.FavoritesFragment;
import com.cesarsk.say_it.ui.fragments.HistoryFragment;
import com.cesarsk.say_it.ui.fragments.HomeFragment;
import com.cesarsk.say_it.ui.fragments.RecordingsFragment;
import com.cesarsk.say_it.utility.UtilityDictionary;
import com.cesarsk.say_it.utility.UtilityRecordings;
import com.cesarsk.say_it.utility.UtilitySharedPrefs;
import com.cesarsk.say_it.utility.utility_aidl.IabHelper;
import com.cesarsk.say_it.utility.utility_aidl.IabResult;
import com.cesarsk.say_it.utility.utility_aidl.Inventory;
import com.github.fernandodev.easyratingdialog.library.EasyRatingDialog;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnTabSelectListener;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import uk.co.deanwild.materialshowcaseview.MaterialShowcaseView;

import static android.speech.tts.TextToSpeech.QUEUE_ADD;
import static android.speech.tts.Voice.LATENCY_VERY_LOW;
import static android.speech.tts.Voice.QUALITY_VERY_HIGH;
import static com.cesarsk.say_it.utility.LCSecurity.base64EncodedPublicKey;


public class MainActivity extends AppCompatActivity {

    //ENABLE this variable only for debug purpose. DO NOT RELEASE THE APP IF THIS VARIABLE IS SET TO TRUE
    public static final boolean isLoggingEnabled = false;

    //Fragments' indexes and bottom bar variables
    private static final int HOME_FRAGMENT_INDEX = 0;
    private static final int FAVORITES_FRAGMENT_INDEX = 1;
    private static final int HISTORY_FRAGMENT_INDEX = 2;
    private static final int RECORDINGS_FRAGMENT_INDEX = 3;
    private final FragmentManager fragmentManager = getFragmentManager();
    private int selectedTab = 0;
    public static BottomBar bottomBar;

    //TTS variables
    public static TextToSpeech american_speaker_google;
    public static TextToSpeech british_speaker_google;
    public static final String google_tts = "com.google.android.tts";
    public static final Voice voice_american_female = new Voice("American Language", Locale.US, QUALITY_VERY_HIGH, LATENCY_VERY_LOW, false, null);
    public static final Voice voice_british_female = new Voice("British Language", Locale.UK, QUALITY_VERY_HIGH, LATENCY_VERY_LOW, false, null);

    //shared preferences strings
    public final static String PREFS_NAME = "SAY_IT_PREFS"; //Nome del file delle SharedPreferences
    public final static String FAVORITES_PREFS_KEY = "SAY.IT.FAVORITES"; //Chiave che identifica il Set dei favorites nelle SharedPreferences
    public final static String HISTORY_PREFS_KEY = "SAY.IT.HISTORY"; //Chiave che identifica il Set della history nelle SharedPreferences
    public final static String DEFAULT_ACCENT_KEY = "SAY.IT.DEFAULT.ACCENT"; //Chiave che identifica il DEFAULT ACCENT
    public final static String DEFAULT_NOTIFICATION_RATE_KEY = "SAY.IT.DEFAULT.NOTIFICATION.RATE";
    public final static String DEFAULT_NOTIFICATION_HOUR_KEY = "SAY.IT.DEFAULT.NOTIFICATION.HOUR";
    public final static String DEFAULT_NOTIFICATION_MINUTE_KEY = "SAY.IT.DEFAULT.NOTIFICATION.MINUTE";
    public final static String NO_ADS_STATUS_KEY = "SAY.IT.NO.ADS.KEY";
    public final static String FIRST_LAUNCH_KEY = "SAY.IT.FIRST.LAUNCH";

    //shared preferences variables
    public static Set<String> FAVORITES = null;
    public static Set<String> HISTORY = null;
    public static ArrayList<File> RECORDINGS = null;
    public static String DEFAULT_NOTIFICATION_RATE = null;
    public static String DEFAULT_ACCENT = null;
    public static String DEFAULT_NOTIFICATION_HOUR = null;
    public static String DEFAULT_NOTIFICATION_MINUTE = null;
    public static boolean NO_ADS = false;
    public static boolean FIRST_LAUNCH;

    //showcase's unique IDs
    public static String id_showcase_playactivity = "utente_playactivity";
    public static String id_showcase_fragments = "utente_fragments";
    public static MaterialShowcaseView showCaseFragmentView;

    //other variables
    private boolean doubleBackToExitPressedOnce = false;
    private boolean hasInterstitialDisplayed = false;
    private final InterstitialAd mInterstitialAd = new InterstitialAd(this);
    public static final int notifId = 150;

    //In-App Billing Helper
    private IabHelper mHelper;

    //wordList variables
    public static final HashMap<String, ArrayList<Pair<String, String>>> Wordlists_Map = new HashMap<>();
    public static final ArrayList<String> Quotes = new ArrayList<>();
    public static String wordOfTheDay;
    public static String IPAofTheDay;

    //rate Dialog variables
    private EasyRatingDialog easyRatingDialog;

    @Override
    protected void onStop() {
        super.onStop();
        //saving preferences every time the app has been stopped (not killed)
        UtilitySharedPrefs.savePrefs(this, FAVORITES, FAVORITES_PREFS_KEY);
        UtilitySharedPrefs.savePrefs(this, HISTORY, HISTORY_PREFS_KEY);
        UtilitySharedPrefs.savePrefs(this, false, FIRST_LAUNCH_KEY);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        //to avoid exceptions, it's very important to shutdown android's tts service
        american_speaker_google.shutdown();
        british_speaker_google.shutdown();

        //as above, mHelper should be closed aswell (related to In App Purchase)
        if (mHelper != null) try {
            mHelper.dispose();
        } catch (IabHelper.IabAsyncInProgressException e) {
            e.printStackTrace();
        }
        mHelper = null;
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Bundle b = intent.getExtras();
        if (b != null) {
            selectedTab = b.getInt("fragment_index");
            bottomBar.selectTabAtPosition(selectedTab);
        } else bottomBar.selectTabAtPosition(HOME_FRAGMENT_INDEX);
    }

    @Override
    protected void onStart() {
        super.onStart();
        easyRatingDialog.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        easyRatingDialog.showIfNeeded();

        UtilitySharedPrefs.loadAdsStatus(this);

        //check in-app purchase and do not show app, if so.
        if (!NO_ADS) {
            if(mInterstitialAd.getAdUnitId() == null)
                mInterstitialAd.setAdUnitId(getResources().getString(R.string.ad_unit_id_interstitial_mainactivity_back));
            mInterstitialAd.setAdListener(new AdListener() {
                @Override
                public void onAdClosed() {
                    requestNewInterstitial();
                }
            });
            requestNewInterstitial();
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //easy rating dialog
        easyRatingDialog = new EasyRatingDialog(this);

        //is it the first time that the app has been launched?
        UtilitySharedPrefs.loadFirstLaunch(this);

        //check Huawei Protected Apps
        ifHuaweiAlert();

        //set default Stream Controller
        setVolumeControlStream(android.media.AudioManager.STREAM_MUSIC);

        final IabHelper.QueryInventoryFinishedListener mGotInventoryListener
                = new IabHelper.QueryInventoryFinishedListener() {
            @Override
            public void onQueryInventoryFinished(IabResult result,
                                                 Inventory inventory) {

                if (result.isFailure()) {
                    if (isLoggingEnabled)
                        Toast.makeText(MainActivity.this, "Query Failed!", Toast.LENGTH_SHORT).show();
                } else {

                    /*//Try-catch snippet for testing in-app
                    try {
                        mHelper.consumeAsync(inventory.getPurchase(PlayActivity.no_ads_in_app), new IabHelper.OnConsumeFinishedListener() {
                            @Override
                            public void onConsumeFinished(Purchase purchase, IabResult result) {
                                UtilitySharedPrefs.savePrefs(MainActivity.this, false, NO_ADS_STATUS_KEY);
                            }
                        });
                    } catch (IabHelper.IabAsyncInProgressException e) {
                        e.printStackTrace();
                    }*/
                    if (inventory.hasPurchase(PlayActivity.no_ads_in_app)) {
                        UtilitySharedPrefs.savePrefs(MainActivity.this, true, NO_ADS_STATUS_KEY);
                        if(FIRST_LAUNCH)
                            Toast.makeText(MainActivity.this, "Premium Version restored, restart to apply changes", Toast.LENGTH_LONG).show();
                    }
                }
            }
        };

        //IAB helper initialization
        mHelper = new IabHelper(this, base64EncodedPublicKey);
        mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
            public void onIabSetupFinished(IabResult result) {
                if (!result.isSuccess()) {
                    // oh no, there was a problem.
                    if (MainActivity.isLoggingEnabled)
                        Log.d("Say It!", "Problem setting up In-app Billing: " + result);
                }
                ArrayList<String> SKUs = new ArrayList<>();
                SKUs.add(PlayActivity.no_ads_in_app);
                try {
                    mHelper.flagEndAsync();
                    mHelper.queryInventoryAsync(SKUs, mGotInventoryListener);
                } catch (IabHelper.IabAsyncInProgressException e) {
                    e.printStackTrace();
                }
                if (MainActivity.isLoggingEnabled)
                    Log.d("Say It!", "Hooray. IAB is fully set up!" + result);
            }
        });

        //loading preferences
        UtilitySharedPrefs.loadSettingsPrefs(this);
        UtilitySharedPrefs.loadFavs(this);
        UtilitySharedPrefs.loadHist(this);
        RECORDINGS = UtilityRecordings.loadRecordingsfromStorage(this);

        if (Wordlists_Map.isEmpty()) {
            //loading dictionary (word of the day included)
            try {
                UtilityDictionary.loadDictionary(this);
                UtilitySharedPrefs.loadQuotes(this);
                int parsedHour = Integer.parseInt(DEFAULT_NOTIFICATION_HOUR);
                int parsedMinute = Integer.parseInt(DEFAULT_NOTIFICATION_MINUTE);
                NotificationReceiver.scheduleNotification(this, parsedHour, parsedMinute, DEFAULT_NOTIFICATION_RATE);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        //toolbar setup
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        EditText editText = (EditText) findViewById(R.id.search_bar_edit_text);
        ImageView lens_search_button = (ImageView) findViewById(R.id.search_bar_hint_icon);
        ImageButton voice_search_button = (ImageButton) findViewById(R.id.search_bar_voice_icon);

        View.OnClickListener search_bar_listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent search_activity_intent = new Intent(v.getContext(), SearchActivity.class);
                if (v.getId() == R.id.search_bar_voice_icon)
                    search_activity_intent.putExtra("VOICE_SEARCH_SELECTED", true);
                startActivity(search_activity_intent);
            }
        };

        editText.setOnClickListener(search_bar_listener);
        lens_search_button.setOnClickListener(search_bar_listener);
        voice_search_button.setOnClickListener(search_bar_listener);

        //managing fragments
        final ArrayList<Fragment> FragmentArrayList = new ArrayList<>();
        FragmentArrayList.add(new HomeFragment());
        FragmentArrayList.add(new FavoritesFragment());
        FragmentArrayList.add(new HistoryFragment());
        FragmentArrayList.add(new RecordingsFragment());

        for (Fragment element : FragmentArrayList) {
            element.setExitTransition(new Fade());
        }

        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.fragment_container, FragmentArrayList.get(HOME_FRAGMENT_INDEX));
        transaction.commit();

        bottomBar = (BottomBar) findViewById(R.id.bottomBar);
        bottomBar.selectTabAtPosition(HOME_FRAGMENT_INDEX); //Default: Home
        bottomBar.setOnTabSelectListener(new OnTabSelectListener() {

            private int last_index = HOME_FRAGMENT_INDEX;

            @Override
            public void onTabSelected(@IdRes int tabId) {
                //creating the Fragment transaction
                FragmentTransaction transaction = fragmentManager.beginTransaction();

                //this switch case is used to move among fragments using the bottombar
                switch (tabId) {
                    case R.id.tab_favorites:
                        if (FAVORITES_FRAGMENT_INDEX > last_index) {
                            FragmentArrayList.get(FAVORITES_FRAGMENT_INDEX).setEnterTransition(new Slide(Gravity.RIGHT));
                        } else if (FAVORITES_FRAGMENT_INDEX < last_index) {
                            FragmentArrayList.get(FAVORITES_FRAGMENT_INDEX).setEnterTransition(new Slide(Gravity.LEFT));
                        }
                        selectedTab = FAVORITES_FRAGMENT_INDEX;
                        transaction.replace(R.id.fragment_container, FragmentArrayList.get(FAVORITES_FRAGMENT_INDEX));
                        last_index = FAVORITES_FRAGMENT_INDEX;
                        break;

                    case R.id.tab_home:
                        if (HOME_FRAGMENT_INDEX > last_index) {
                            FragmentArrayList.get(HOME_FRAGMENT_INDEX).setEnterTransition(new Slide(Gravity.RIGHT));
                        } else if (HOME_FRAGMENT_INDEX < last_index) {
                            FragmentArrayList.get(HOME_FRAGMENT_INDEX).setEnterTransition(new Slide(Gravity.LEFT));
                        }
                        selectedTab = HOME_FRAGMENT_INDEX;
                        transaction.replace(R.id.fragment_container, FragmentArrayList.get(HOME_FRAGMENT_INDEX));
                        last_index = HOME_FRAGMENT_INDEX;
                        break;

                    case R.id.tab_history:
                        if (HISTORY_FRAGMENT_INDEX > last_index) {
                            FragmentArrayList.get(HISTORY_FRAGMENT_INDEX).setEnterTransition(new Slide(Gravity.RIGHT));
                        } else if (HISTORY_FRAGMENT_INDEX < last_index) {
                            FragmentArrayList.get(HISTORY_FRAGMENT_INDEX).setEnterTransition(new Slide(Gravity.LEFT));
                        }
                        selectedTab = HISTORY_FRAGMENT_INDEX;
                        transaction.replace(R.id.fragment_container, FragmentArrayList.get(HISTORY_FRAGMENT_INDEX));
                        last_index = HISTORY_FRAGMENT_INDEX;
                        break;

                    case R.id.tab_recordings:
                        if (RECORDINGS_FRAGMENT_INDEX > last_index) {
                            FragmentArrayList.get(RECORDINGS_FRAGMENT_INDEX).setEnterTransition(new Slide(Gravity.RIGHT));
                        } else if (RECORDINGS_FRAGMENT_INDEX < last_index) {
                            FragmentArrayList.get(RECORDINGS_FRAGMENT_INDEX).setEnterTransition(new Slide(Gravity.LEFT));
                        }
                        selectedTab = RECORDINGS_FRAGMENT_INDEX;
                        transaction.replace(R.id.fragment_container, FragmentArrayList.get(RECORDINGS_FRAGMENT_INDEX));
                        last_index = RECORDINGS_FRAGMENT_INDEX;
                        break;
                }
                transaction.commit();
            }

        });

        //Init TTS
        initTTS(this);
    }

    @Override
    public void onBackPressed() {
        //if the tab selected is not the home fragment, then we should back to the home fragment pressing BACK.
        //loading also an ad to display before closing the app
        if (selectedTab != HOME_FRAGMENT_INDEX) {
            bottomBar.selectTabAtPosition(HOME_FRAGMENT_INDEX);
            if (showCaseFragmentView != null) showCaseFragmentView.hide();
        } else {
            if (mInterstitialAd.isLoaded() && !hasInterstitialDisplayed) {
                mInterstitialAd.show();
                hasInterstitialDisplayed = true;
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        hasInterstitialDisplayed = false;
                    }
                }, 60000);
            } else {
                if (doubleBackToExitPressedOnce) {
                    super.onBackPressed();
                    return;
                }
                this.doubleBackToExitPressedOnce = true;
                Toast.makeText(this, "Click Back again to exit", Toast.LENGTH_SHORT).show();

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        doubleBackToExitPressedOnce = false;
                    }
                }, 2000);
            }
        }
    }

    private void requestNewInterstitial() {
        //request a new ad
        AdRequest adRequest = new AdRequest.Builder().addTestDevice(getString(R.string.test_device_oneplus_3)).addTestDevice(getString(R.string.test_device_honor_6)).addTestDevice(getString(R.string.test_device_htc_one_m8)).build();
        mInterstitialAd.loadAd(adRequest);
    }

    private void initTTS(Context context) {
        //init american and british tts
        //tts.speak(""); it is used to pre-load the tts after launching the app, to help low-end devices to reproduce words without lag
        american_speaker_google = new TextToSpeech(context, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int i) {
                if (i == TextToSpeech.SUCCESS) {
                    american_speaker_google.setPitch(0.90f);
                    american_speaker_google.setSpeechRate(0.90f);
                    american_speaker_google.setVoice(MainActivity.voice_american_female);
                    american_speaker_google.speak("", QUEUE_ADD, null, null);
                } else {
                    if (MainActivity.isLoggingEnabled)
                        Log.e("error", "Initialization Failed!");
                }

            }
        }, google_tts);

        british_speaker_google = new TextToSpeech(context, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int i) {
                if (i == TextToSpeech.SUCCESS) {
                    british_speaker_google.setPitch(0.90f);
                    british_speaker_google.setSpeechRate(0.90f);
                    british_speaker_google.setVoice(MainActivity.voice_british_female);
                    british_speaker_google.speak("", QUEUE_ADD, null, null);
                } else {
                    if (MainActivity.isLoggingEnabled)
                        Log.e("error", "Initialization Failed!");
                }
            }
        }, google_tts);
    }

    //check Huawei Protected Apps
    //huawei has a system to block every notification incoming. The user should be ALWAYS warn if a notification has been blocked. So this method warns the user, if it has a Huawei smartphone
    //to add this app to Protected Apps in order to receive notifications
    private void ifHuaweiAlert() {
        final SharedPreferences settings = getSharedPreferences("ProtectedApps", MODE_PRIVATE);
        final String saveIfSkip = "skipProtectedAppsMessage";
        boolean skipMessage = settings.getBoolean(saveIfSkip, false);
        if (!skipMessage) {
            final SharedPreferences.Editor editor = settings.edit();
            Intent intent = new Intent();
            intent.setClassName("com.huawei.systemmanager", "com.huawei.systemmanager.optimize.process.ProtectActivity");
            if (isCallable(intent)) {
                final AppCompatCheckBox dontShowAgain = new AppCompatCheckBox(this);
                dontShowAgain.setText("Do not show again");
                dontShowAgain.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        editor.putBoolean(saveIfSkip, isChecked);
                        editor.apply();
                    }
                });

                new AlertDialog.Builder(this)
                        .setTitle("Huawei Protected Apps")
                        .setMessage(String.format("%s requires to be enabled in 'Protected Apps' to send notifications.%n", getString(R.string.app_name)))
                        .setView(dontShowAgain)
                        .setPositiveButton("Protected Apps", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                huaweiProtectedApps();
                            }
                        })
                        .setNegativeButton("Cancel", null)
                        .show();
            } else {
                editor.putBoolean(saveIfSkip, true);
                editor.apply();
            }
        }
    }

    private boolean isCallable(Intent intent) {
        List<ResolveInfo> list = getPackageManager().queryIntentActivities(intent,
                PackageManager.MATCH_DEFAULT_ONLY);
        return list.size() > 0;
    }

    private void huaweiProtectedApps() {
        try {
            String cmd = "am start -n com.huawei.systemmanager/.optimize.process.ProtectActivity";
            cmd += " --user " + getUserSerial();
            Runtime.getRuntime().exec(cmd);
        } catch (IOException ignored) {
        }
    }

    private String getUserSerial() {
        //noinspection ResourceType
        Object userManager = getSystemService("user");
        if (null == userManager) return "";

        try {
            Method myUserHandleMethod = android.os.Process.class.getMethod("myUserHandle", (Class<?>[]) null);
            Object myUserHandle = myUserHandleMethod.invoke(android.os.Process.class, (Object[]) null);
            Method getSerialNumberForUser = userManager.getClass().getMethod("getSerialNumberForUser", myUserHandle.getClass());
            Long userSerial = (Long) getSerialNumberForUser.invoke(userManager, myUserHandle);
            if (userSerial != null) {
                return String.valueOf(userSerial);
            } else {
                return "";
            }
        } catch (NoSuchMethodException | IllegalArgumentException | InvocationTargetException | IllegalAccessException ignored) {
        }
        return "";
    }

}