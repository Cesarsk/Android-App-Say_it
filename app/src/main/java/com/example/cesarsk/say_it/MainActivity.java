package com.example.cesarsk.say_it;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.SearchManager;
import android.content.Context;
import android.speech.tts.TextToSpeech;
import android.speech.tts.Voice;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnTabReselectListener;
import com.roughike.bottombar.OnTabSelectListener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;

import java.util.Locale;

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
    static Voice voice_american_female = new Voice("American",Locale.US,QUALITY_VERY_HIGH,LATENCY_VERY_LOW,false,null);
    static Voice voice_british_female = new Voice("British",Locale.UK,QUALITY_VERY_HIGH,LATENCY_VERY_LOW,false,null);

    //Definizione variabile WordList
    public static final ArrayList<String> WordList = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Gestione Fragment
        final FragmentManager fragmentManager = getFragmentManager();

        final ArrayList<Fragment> FragmentArrayList = new ArrayList<>();
        FragmentArrayList.add(new HomeFragment());
        FragmentArrayList.add(new FavoritesFragment());
        FragmentArrayList.add(new HistoryFragment());
        FragmentArrayList.add(new RecordingsFragment());

        fragmentManager.beginTransaction().add(R.id.fragment_container, FragmentArrayList.get(HOME_FRAGMENT_INDEX)).commit();

        final BottomBar bottomBar = (BottomBar) findViewById(R.id.bottomBar);
        bottomBar.selectTabAtPosition(HOME_FRAGMENT_INDEX); //Default: Home
        bottomBar.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelected(@IdRes int tabId) {
                if (tabId == R.id.tab_favorites) {
                    fragmentManager.beginTransaction().replace(R.id.fragment_container, FragmentArrayList.get(FAVORITES_FRAGMENT_INDEX)).commit();
                } else if (tabId == R.id.tab_recordings) {
                    fragmentManager.beginTransaction().replace(R.id.fragment_container, FragmentArrayList.get(RECORDINGS_FRAGMENT_INDEX)).commit();
                } else if (tabId == R.id.tab_home) {
                    fragmentManager.beginTransaction().replace(R.id.fragment_container, FragmentArrayList.get(HOME_FRAGMENT_INDEX)).commit();
                } else if (tabId == R.id.tab_history) {
                    fragmentManager.beginTransaction().replace(R.id.fragment_container, FragmentArrayList.get(HISTORY_FRAGMENT_INDEX)).commit();
                }
            }
        });

        bottomBar.setOnTabReselectListener(new OnTabReselectListener() {
            @Override
            public void onTabReSelected(@IdRes int tabId) {
                if (tabId == R.id.tab_favorites) {
                    fragmentManager.beginTransaction().replace(R.id.fragment_container, FragmentArrayList.get(FAVORITES_FRAGMENT_INDEX)).commit();
                } else if (tabId == R.id.tab_recordings) {
                    fragmentManager.beginTransaction().replace(R.id.fragment_container, FragmentArrayList.get(RECORDINGS_FRAGMENT_INDEX)).commit();
                } else if (tabId == R.id.tab_home) {
                    fragmentManager.beginTransaction().replace(R.id.fragment_container, FragmentArrayList.get(HOME_FRAGMENT_INDEX)).commit();
                } else if (tabId == R.id.tab_history) {
                    fragmentManager.beginTransaction().replace(R.id.fragment_container, FragmentArrayList.get(HISTORY_FRAGMENT_INDEX)).commit();
                }
            }
        });


        //CARICAMENTO WORDLIST
            BufferedReader line_reader = new BufferedReader(new InputStreamReader(getResources().openRawResource(R.raw.wordlist)));
        String line;

        try {
            while((line = line_reader.readLine()) != null){
                WordList.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        Collections.sort(WordList);

        Toast.makeText(this, "Caricate " + WordList.size() + " parole.", Toast.LENGTH_LONG).show();

        //IMPOSTAZIONE TEXT TO SPEECH
        tts= new TextToSpeech(MainActivity.this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                // TODO OTTIMIZZARE TTS
                if(status == TextToSpeech.SUCCESS) {
                    //Ridondante?
                    tts.setPitch((float)0.90);
                    tts.setSpeechRate((float)0.90);
                    tts.setVoice(voice_american_female);
                }
                else
                    Log.e("error", "Initilization Failed!");
            }
        });

        //CONFIGURAZIONE SEARCHBAR
        SearchView search_bar = (SearchView) findViewById(R.id.top_search_bar);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        search_bar.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));


        //Gestione AD
        MobileAds.initialize(getApplicationContext(), getResources().getString(R.string.banner_id));
        AdView mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

    }
}