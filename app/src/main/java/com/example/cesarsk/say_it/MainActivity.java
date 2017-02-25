package com.example.cesarsk.say_it;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.speech.tts.TextToSpeech;
import android.speech.tts.Voice;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.transition.Fade;
import android.transition.Slide;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnTabSelectListener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;

import java.util.Date;
import java.util.Locale;
import java.util.Set;
import java.util.TimeZone;

import static android.speech.tts.Voice.LATENCY_VERY_LOW;
import static android.speech.tts.Voice.QUALITY_VERY_HIGH;


public class MainActivity extends AppCompatActivity {

    //Indici per la FragmentList
    private final int HOME_FRAGMENT_INDEX = 0;
    private final int FAVORITES_FRAGMENT_INDEX = 1;
    private final int HISTORY_FRAGMENT_INDEX = 2;
    private final int RECORDINGS_FRAGMENT_INDEX = 3;

    //Definizione variabile TTS
    static TextToSpeech tts;
    static Voice voice_american_female = new Voice("American", Locale.US, QUALITY_VERY_HIGH, LATENCY_VERY_LOW, false, null);
    static Voice voice_british_female = new Voice("British", Locale.UK, QUALITY_VERY_HIGH, LATENCY_VERY_LOW, false, null);

    //Gestione preferiti e history
    public static Set<String> FAVORITES = null;
    public static Set<String> HISTORY = null;

    //Gestione Preferenze
    public final static String PREFS_NAME = "SAY_IT_PREFS"; //Nome del file delle SharedPreferences
    public final static String FAVORITES_PREFS_KEY = "SAY.IT.FAVORITES"; //Chiave che identifica il Set dei favorites nelle SharedPreferences
    public final static String HISTORY_PREFS_KEY = "SAY.IT.HISTORY"; //Chiave che identifica il Set della history nelle SharedPreferences

    //Definizione variabile WordList
    public static final ArrayList<String> WordList = new ArrayList<>();
    static String wordOfTheDay = new String();

    //Bottom Bar variable
    BottomBar bottomBar;

    @Override
    protected void onStop() {
        super.onStop();
        Utility.savePrefs(this, FAVORITES, FAVORITES_PREFS_KEY);
        Utility.savePrefs(this, HISTORY, HISTORY_PREFS_KEY);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
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
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //SETUP TOOLBAR
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);


        //TODO Aprire l'activity dei risultati al momento del click
        EditText editText = (EditText) findViewById(R.id.search_bar_edit_text);
        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                Toast.makeText(v.getContext(), "Cercato: " + v.getText(), Toast.LENGTH_SHORT).show();
                return false;
            }
        });

        //Caricamento preferenze
        Utility.loadFavs(this);
        Utility.loadHist(this);

        //Caricamento dizionario (inclusa word of the day)
        Utility.loadDictionary(this);

        //Gestione Fragment
        final FragmentManager fragmentManager = getFragmentManager();

        final ArrayList<Fragment> FragmentArrayList = new ArrayList<>();
        FragmentArrayList.add(new HomeFragment());
        FragmentArrayList.add(new FavoritesFragment());
        FragmentArrayList.add(new HistoryFragment());
        FragmentArrayList.add(new RecordingsFragment());

        for(Fragment element: FragmentArrayList){
            element.setExitTransition(new Fade());
        }

        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.add(R.id.fragment_container, FragmentArrayList.get(HOME_FRAGMENT_INDEX));
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

        //TODO Risolvere eccezione SERVICE CONNECTION!
        //IMPOSTAZIONE TEXT TO SPEECH
        tts = new TextToSpeech(MainActivity.this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                // TODO OTTIMIZZARE TTS
                if (status == TextToSpeech.SUCCESS) {
                    //Ridondante?
                    tts.setPitch((float) 0.90);
                    tts.setSpeechRate((float) 0.90);
                    tts.setVoice(voice_american_female);
                } else
                    Log.e("error", "Initilization Failed!");
            }
        });

        //TODO FINIRE!
        //CONFIGURAZIONE SEARCHBAR
        SearchView search_bar = (SearchView) findViewById(R.id.top_search_bar);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        search_bar.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        search_bar.setIconified(false);

        //Gestione AD (TEST AD)
        MobileAds.initialize(getApplicationContext(), "ca-app-pub-3940256099942544/6300978111");
        AdView mAdView = (AdView) findViewById(R.id.adView);
        mAdView.bringToFront();
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
    }

    private void setupSlideExitTransition() {
        Slide slide = new Slide();
        slide.setDuration(1000);
        getWindow().setExitTransition(slide);
    }
}