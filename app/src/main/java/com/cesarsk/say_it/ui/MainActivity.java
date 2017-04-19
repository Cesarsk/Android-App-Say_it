package com.cesarsk.say_it.ui;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
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
import com.github.fernandodev.easyratingdialog.library.EasyRatingDialog;
import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnTabSelectListener;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import java.util.HashMap;
import java.util.Locale;
import java.util.Set;

import static android.speech.tts.Voice.LATENCY_VERY_LOW;
import static android.speech.tts.Voice.QUALITY_VERY_HIGH;


public class MainActivity extends AppCompatActivity {

    //Indici per la FragmentList
    private final int HOME_FRAGMENT_INDEX = 0;
    private final int FAVORITES_FRAGMENT_INDEX = 1;
    private final int HISTORY_FRAGMENT_INDEX = 2;
    private final int RECORDINGS_FRAGMENT_INDEX = 3;

    //Definizione variabile TTS
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

    boolean doubleBackToExitPressedOnce = false;

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
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Bundle b = intent.getExtras();
        int value = 0; // or other values
        if(b != null)
        {
            value = b.getInt("fragment_index");
            bottomBar.selectTabAtPosition(value);
        }
        else bottomBar.selectTabAtPosition(HOME_FRAGMENT_INDEX);
    }

    @Override
    protected void onStart()
    {
        super.onStart();
        easyRatingDialog.onStart();
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        easyRatingDialog.showIfNeeded();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        easyRatingDialog = new EasyRatingDialog(this);

        //PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
        //Caricamento preferenze
        UtilitySharedPrefs.loadSettingsPrefs(this);
        UtilitySharedPrefs.loadFavs(this);
        UtilitySharedPrefs.loadHist(this);
        UtilitySharedPrefs.loadAdsStatus(this);
        RECORDINGS = UtilityRecordings.loadRecordingsfromStorage(this);

        if(Wordlists_Map.isEmpty()) {
            //Caricamento dizionario (inclusa word of the day)
            try {
                UtilityDictionary.loadDictionary(this);
                UtilitySharedPrefs.loadQuotes(this);
                int parsedHour = Integer.parseInt(DEFAULT_NOTIFICATION_HOUR);
                int parsedMinute = Integer.parseInt(DEFAULT_NOTIFICATION_MINUTE);
                NotificationReceiver.scheduleNotification(this,parsedHour, parsedMinute, DEFAULT_NOTIFICATION_RATE);
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
                if(v.getId()==R.id.search_bar_voice_icon)search_activity_intent.putExtra("VOICE_SEARCH_SELECTED", true);
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

        for(Fragment element: FragmentArrayList){
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
                            FragmentArrayList.get(FAVORITES_FRAGMENT_INDEX).setEnterTransition(new Slide(Gravity.RIGHT));
                            //transaction.setCustomAnimations(R.animator.slide_from_right, R.animator.slide_to_left);
                        } else if (FAVORITES_FRAGMENT_INDEX < last_index) {
                            FragmentArrayList.get(FAVORITES_FRAGMENT_INDEX).setEnterTransition(new Slide(Gravity.LEFT));
                            //transaction.setCustomAnimations(R.animator.slide_from_left, R.animator.slide_to_right);
                        }
                        transaction.replace(R.id.fragment_container, FragmentArrayList.get(FAVORITES_FRAGMENT_INDEX));
                        last_index = FAVORITES_FRAGMENT_INDEX;
                        break;

                    case R.id.tab_home:
                        if (HOME_FRAGMENT_INDEX > last_index) {
                            FragmentArrayList.get(HOME_FRAGMENT_INDEX).setEnterTransition(new Slide(Gravity.RIGHT));
                            //transaction.setCustomAnimations(R.animator.slide_from_right, R.animator.slide_to_left);
                        } else if (HOME_FRAGMENT_INDEX < last_index) {
                            FragmentArrayList.get(HOME_FRAGMENT_INDEX).setEnterTransition(new Slide(Gravity.LEFT));
                            //transaction.setCustomAnimations(R.animator.slide_from_left, R.animator.slide_to_right);
                        }
                        transaction.replace(R.id.fragment_container, FragmentArrayList.get(HOME_FRAGMENT_INDEX));
                        last_index = HOME_FRAGMENT_INDEX;
                        break;

                    case R.id.tab_history:
                        if (HISTORY_FRAGMENT_INDEX > last_index) {
                            FragmentArrayList.get(HISTORY_FRAGMENT_INDEX).setEnterTransition(new Slide(Gravity.RIGHT));
                            //transaction.setCustomAnimations(R.animator.slide_from_right, R.animator.slide_to_left);
                        } else if (HISTORY_FRAGMENT_INDEX < last_index) {
                            FragmentArrayList.get(HISTORY_FRAGMENT_INDEX).setEnterTransition(new Slide(Gravity.LEFT));
                            //transaction.setCustomAnimations(R.animator.slide_from_left, R.animator.slide_to_right);
                        }
                        transaction.replace(R.id.fragment_container, FragmentArrayList.get(HISTORY_FRAGMENT_INDEX));
                        last_index = HISTORY_FRAGMENT_INDEX;
                        break;

                    case R.id.tab_recordings:
                        if (RECORDINGS_FRAGMENT_INDEX > last_index) {
                            FragmentArrayList.get(RECORDINGS_FRAGMENT_INDEX).setEnterTransition(new Slide(Gravity.RIGHT));
                            //transaction.setCustomAnimations(R.animator.slide_from_right, R.animator.slide_to_left);
                        } else if (RECORDINGS_FRAGMENT_INDEX < last_index) {
                            FragmentArrayList.get(RECORDINGS_FRAGMENT_INDEX).setEnterTransition(new Slide(Gravity.LEFT));
                            //transaction.setCustomAnimations(R.animator.slide_from_left, R.animator.slide_to_right);
                        }
                        transaction.replace(R.id.fragment_container, FragmentArrayList.get(RECORDINGS_FRAGMENT_INDEX));
                        last_index = RECORDINGS_FRAGMENT_INDEX;
                        break;
                }
                transaction.commit();
            }

        });

        //IMPOSTAZIONE TEXT TO SPEECH
        american_speaker_google = new TextToSpeech(MainActivity.this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                // TODO OTTIMIZZARE TTS
                if (status == TextToSpeech.SUCCESS) {
                    //Ridondante?
                    american_speaker_google.setPitch((float) 0.90);
                    american_speaker_google.setSpeechRate((float) 0.90);
                    american_speaker_google.setVoice(voice_american_female);
                } else
                    Log.e("error", "Initilization Failed!");
            }
        });

        british_speaker_google = new TextToSpeech(MainActivity.this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                // TODO OTTIMIZZARE TTS
                if (status == TextToSpeech.SUCCESS) {
                    //Ridondante?
                    british_speaker_google.setPitch((float) 0.90);
                    british_speaker_google.setSpeechRate((float) 0.90);
                    british_speaker_google.setVoice(voice_british_female);
                } else
                    Log.e("error", "Initilization Failed!");
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Click Back again to exit", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;
            }
        }, 2000);

    }
}