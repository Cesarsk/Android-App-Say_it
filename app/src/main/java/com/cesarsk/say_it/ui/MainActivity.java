package com.cesarsk.say_it.ui;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.speech.tts.Voice;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.os.Bundle;
import android.support.v4.util.Pair;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.transition.Fade;
import android.transition.Slide;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
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
import java.util.ArrayList;

import java.util.HashMap;
import java.util.Locale;
import java.util.Set;

import uk.co.deanwild.materialshowcaseview.MaterialShowcaseView;

import static android.speech.tts.Voice.LATENCY_VERY_LOW;
import static android.speech.tts.Voice.QUALITY_VERY_HIGH;
import static com.cesarsk.say_it.utility.LCSecurity.base64EncodedPublicKey;


public class MainActivity extends AppCompatActivity {

    //Indici per la FragmentList
    public static final int HOME_FRAGMENT_INDEX = 0;
    public static final int FAVORITES_FRAGMENT_INDEX = 1;
    public static final int HISTORY_FRAGMENT_INDEX = 2;
    public static final int RECORDINGS_FRAGMENT_INDEX = 3;

    //Intent Extra
    public static final String IS_NOTIFICATION = "SAY.IT.FROM.NOTIFICATION";

    //Definizione variabile TTS
    private TextToSpeech tts_speaker;
    public static TextToSpeech american_speaker_google;
    public static TextToSpeech british_speaker_google;
    public static Voice voice_american_female = new Voice("American Language", Locale.US, QUALITY_VERY_HIGH, LATENCY_VERY_LOW, false, null);
    public static Voice voice_british_female = new Voice("British Language", Locale.UK, QUALITY_VERY_HIGH, LATENCY_VERY_LOW, false, null);

    //Gestione preferiti, history e recordings
    public static Set<String> FAVORITES = null;
    public static Set<String> HISTORY = null;
    public static ArrayList<File> RECORDINGS = null;
    public static String DEFAULT_NOTIFICATION_RATE = null;
    public static String DEFAULT_ACCENT = null;
    public static String DEFAULT_NOTIFICATION_HOUR = null;
    public static String DEFAULT_NOTIFICATION_MINUTE = null;
    public static boolean NO_ADS = false;

    //Gestione Preferenze
    public final static String PREFS_NAME = "SAY_IT_PREFS"; //Nome del file delle SharedPreferences
    public final static String FAVORITES_PREFS_KEY = "SAY.IT.FAVORITES"; //Chiave che identifica il Set dei favorites nelle SharedPreferences
    public final static String HISTORY_PREFS_KEY = "SAY.IT.HISTORY"; //Chiave che identifica il Set della history nelle SharedPreferences
    public final static String RECORDINGS_PREFS_KEY = "SAY.IT.RECORDINGS"; //Chiave che identifica il Set della lista dei Recordings
    public final static String DEFAULT_ACCENT_KEY = "SAY.IT.DEFAULT.ACCENT"; //Chiave che identifica il DEFAULT ACCENT
    public final static String DEFAULT_NOTIFICATION_RATE_KEY = "SAY.IT.DEFAULT.NOTIFICATION.RATE";
    public final static String DEFAULT_NOTIFICATION_HOUR_KEY = "SAY.IT.DEFAULT.NOTIFICATION.HOUR";
    public final static String DEFAULT_NOTIFICATION_MINUTE_KEY = "SAY.IT.DEFAULT.NOTIFICATION.MINUTE";
    public final static String NO_ADS_STATUS_KEY = "SAY.IT.NO.ADS.KEY";

    public final static int REQUEST_CODE = 1;

    //Unique IDs related to showcase
    public static String id_showcase_playactivity = "utente_playactivity";
    public static String id_showcase_fragments = "utente_fragments";
    public static MaterialShowcaseView showCaseFragmentView;


    int selectedTab = 0; // or other values

    boolean doubleBackToExitPressedOnce = false;
    boolean hasInterstitialDisplayed = false;

    //In-App Billing Helper
    IabHelper mHelper;

    //Definizione variabile WordList
    public static final ArrayList<String> WordList = new ArrayList<>();
    public static final HashMap<String, ArrayList<Pair<String, String>>> Wordlists_Map = new HashMap<>();
    public static final ArrayList<String> Quotes = new ArrayList<>();
    public static String wordOfTheDay;
    public static String IPAofTheDay;

    //Bottom Bar variable
    public static BottomBar bottomBar;

    //EditText Searchbar variable
    EditText editText;
    ImageView lens_search_button;
    ImageButton voice_search_button;

    //Notification id
    public static final int notifId = 150;

    //Rate Dialog
    EasyRatingDialog easyRatingDialog;

    final FragmentManager fragmentManager = getFragmentManager();
    InterstitialAd mInterstitialAd = new InterstitialAd(this);

    @Override
    protected void onStop() {
        super.onStop();
        UtilitySharedPrefs.savePrefs(this, FAVORITES, FAVORITES_PREFS_KEY);
        UtilitySharedPrefs.savePrefs(this, HISTORY, HISTORY_PREFS_KEY);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        american_speaker_google.shutdown();
        british_speaker_google.shutdown();

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
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        easyRatingDialog = new EasyRatingDialog(this);

        final IabHelper.QueryInventoryFinishedListener mGotInventoryListener
                = new IabHelper.QueryInventoryFinishedListener() {
            @Override
            public void onQueryInventoryFinished(IabResult result,
                                                 Inventory inventory) {

                if (result.isFailure()) {
                    Toast.makeText(MainActivity.this, "Query Failed!", Toast.LENGTH_SHORT).show();
                } else {

                    /*//TODO Blocco try-catch SOLO PER IL TESTING
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
                        //TODO Spostare la riga saveprefs
                        UtilitySharedPrefs.savePrefs(MainActivity.this, true, NO_ADS_STATUS_KEY);
                        Toast.makeText(MainActivity.this, "NO ADS", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        };

        //IAB Helper initialization
        mHelper = new IabHelper(this, base64EncodedPublicKey);
        mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
            public void onIabSetupFinished(IabResult result) {
                if (!result.isSuccess()) {
                    // Oh no, there was a problem.
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
                Log.d("Say It!", "Hooray. IAB is fully set up!" + result);
            }
        });

        //PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
        //Caricamento preferenze
        UtilitySharedPrefs.loadSettingsPrefs(this);
        UtilitySharedPrefs.loadFavs(this);
        UtilitySharedPrefs.loadHist(this);
        UtilitySharedPrefs.loadAdsStatus(this);
        RECORDINGS = UtilityRecordings.loadRecordingsfromStorage(this);

        if (!NO_ADS) {
            mInterstitialAd.setAdUnitId(getResources().getString(R.string.ad_unit_id_interstitial_mainactivity_back));
            mInterstitialAd.setAdListener(new AdListener() {
                @Override
                public void onAdClosed() {
                    requestNewInterstitial();
                }
            });

            requestNewInterstitial();
        }

        if (Wordlists_Map.isEmpty()) {
            //Caricamento dizionario (inclusa word of the day)
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

        //SETUP TOOLBAR
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        //TODO SISTEMARE LISTENER

        editText = (EditText) findViewById(R.id.search_bar_edit_text);
        lens_search_button = (ImageView) findViewById(R.id.search_bar_hint_icon);
        voice_search_button = (ImageButton) findViewById(R.id.search_bar_voice_icon);

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

        //Gestione Fragment
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

                //Creating the Fragment transaction
                FragmentTransaction transaction = fragmentManager.beginTransaction();

                switch (tabId) {
                    case R.id.tab_favorites:
                        if (FAVORITES_FRAGMENT_INDEX > last_index) {
                            FragmentArrayList.get(FAVORITES_FRAGMENT_INDEX).setEnterTransition(new Slide(Gravity.END));
                            //transaction.setCustomAnimations(R.animator.slide_from_right, R.animator.slide_to_left);
                        } else if (FAVORITES_FRAGMENT_INDEX < last_index) {
                            FragmentArrayList.get(FAVORITES_FRAGMENT_INDEX).setEnterTransition(new Slide(Gravity.START));
                            //transaction.setCustomAnimations(R.animator.slide_from_left, R.animator.slide_to_right);
                        }
                        selectedTab = FAVORITES_FRAGMENT_INDEX;
                        transaction.replace(R.id.fragment_container, FragmentArrayList.get(FAVORITES_FRAGMENT_INDEX));
                        last_index = FAVORITES_FRAGMENT_INDEX;
                        break;

                    case R.id.tab_home:
                        if (HOME_FRAGMENT_INDEX > last_index) {
                            FragmentArrayList.get(HOME_FRAGMENT_INDEX).setEnterTransition(new Slide(Gravity.END));
                            //transaction.setCustomAnimations(R.animator.slide_from_right, R.animator.slide_to_left);
                        } else if (HOME_FRAGMENT_INDEX < last_index) {
                            FragmentArrayList.get(HOME_FRAGMENT_INDEX).setEnterTransition(new Slide(Gravity.START));
                            //transaction.setCustomAnimations(R.animator.slide_from_left, R.animator.slide_to_right);
                        }
                        selectedTab = HOME_FRAGMENT_INDEX;
                        transaction.replace(R.id.fragment_container, FragmentArrayList.get(HOME_FRAGMENT_INDEX));
                        last_index = HOME_FRAGMENT_INDEX;
                        break;

                    case R.id.tab_history:
                        if (HISTORY_FRAGMENT_INDEX > last_index) {
                            FragmentArrayList.get(HISTORY_FRAGMENT_INDEX).setEnterTransition(new Slide(Gravity.END));
                            //transaction.setCustomAnimations(R.animator.slide_from_right, R.animator.slide_to_left);
                        } else if (HISTORY_FRAGMENT_INDEX < last_index) {
                            FragmentArrayList.get(HISTORY_FRAGMENT_INDEX).setEnterTransition(new Slide(Gravity.START));
                            //transaction.setCustomAnimations(R.animator.slide_from_left, R.animator.slide_to_right);
                        }
                        selectedTab = HISTORY_FRAGMENT_INDEX;
                        transaction.replace(R.id.fragment_container, FragmentArrayList.get(HISTORY_FRAGMENT_INDEX));
                        last_index = HISTORY_FRAGMENT_INDEX;
                        break;

                    case R.id.tab_recordings:
                        if (RECORDINGS_FRAGMENT_INDEX > last_index) {
                            FragmentArrayList.get(RECORDINGS_FRAGMENT_INDEX).setEnterTransition(new Slide(Gravity.END));
                            //transaction.setCustomAnimations(R.animator.slide_from_right, R.animator.slide_to_left);
                        } else if (RECORDINGS_FRAGMENT_INDEX < last_index) {
                            FragmentArrayList.get(RECORDINGS_FRAGMENT_INDEX).setEnterTransition(new Slide(Gravity.START));
                            //transaction.setCustomAnimations(R.animator.slide_from_left, R.animator.slide_to_right);
                        }
                        selectedTab = RECORDINGS_FRAGMENT_INDEX;
                        transaction.replace(R.id.fragment_container, FragmentArrayList.get(RECORDINGS_FRAGMENT_INDEX));
                        last_index = RECORDINGS_FRAGMENT_INDEX;
                        break;
                }
                transaction.commit();
            }

        });

        //IMPOSTAZIONE TEXT TO SPEECH
        american_speaker_google = initTTS(this, true);
        british_speaker_google = initTTS(this, false);
    }

    @Override
    public void onBackPressed() {
        if(selectedTab != HOME_FRAGMENT_INDEX)
        {
            bottomBar.selectTabAtPosition(HOME_FRAGMENT_INDEX);
            if(showCaseFragmentView != null) showCaseFragmentView.hide();
        }

        else
        {
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
        AdRequest adRequest = new AdRequest.Builder().addTestDevice(getString(R.string.test_device_oneplus_3)).addTestDevice(getString(R.string.test_device_honor_6)).addTestDevice(getString(R.string.test_device_htc_one_m8)).build();
        mInterstitialAd.loadAd(adRequest);
    }

    private TextToSpeech initTTS(Context context, final boolean accent) {
        TextToSpeech.OnInitListener onInitListener = null;
        tts_speaker = new TextToSpeech(context, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                tts_speaker.setPitch((float) 0.90);
                tts_speaker.setSpeechRate((float) 0.90);
                if(accent)tts_speaker.setVoice(voice_american_female);
                else if(!accent)tts_speaker.setVoice(voice_british_female);
            }
        });

        return tts_speaker;
    }
}