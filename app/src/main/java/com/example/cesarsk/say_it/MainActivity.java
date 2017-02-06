package com.example.cesarsk.say_it;

import android.app.Fragment;
import android.app.FragmentManager;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnTabSelectListener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;


public class MainActivity extends FragmentActivity {

    //Indici per la FragmentList
    private final int HOME_FRAGMENT_INDEX = 0;
    private final int FAVORITES_FRAGMENT_INDEX = 1;
    private final int HISTORY_FRAGMENT_INDEX = 2;
    private final int SEARCH_FRAGMENT_INDEX = 3;
    private final int SETTINGS_FRAGMENT_INDEX = 4;

    private ArrayList<String> WordList;

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
        FragmentArrayList.add(new SearchFragment());
        FragmentArrayList.add(new SettingsFragment());

        fragmentManager.beginTransaction().add(R.id.fragment_container, FragmentArrayList.get(HOME_FRAGMENT_INDEX)).commit();

        final BottomBar bottomBar = (BottomBar) findViewById(R.id.bottomBar);
        bottomBar.selectTabAtPosition(2); //Default: Home
        bottomBar.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelected(@IdRes int tabId) {
                if (tabId == R.id.tab_favorites) {
                    fragmentManager.beginTransaction().replace(R.id.fragment_container, FragmentArrayList.get(FAVORITES_FRAGMENT_INDEX)).commit();
                } else if (tabId == R.id.tab_search) {
                    fragmentManager.beginTransaction().replace(R.id.fragment_container, FragmentArrayList.get(SEARCH_FRAGMENT_INDEX)).commit();
                } else if (tabId == R.id.tab_home) {
                    fragmentManager.beginTransaction().replace(R.id.fragment_container, FragmentArrayList.get(HOME_FRAGMENT_INDEX)).commit();
                } else if (tabId == R.id.tab_history) {
                    fragmentManager.beginTransaction().replace(R.id.fragment_container, FragmentArrayList.get(HISTORY_FRAGMENT_INDEX)).commit();
                } else if (tabId == R.id.tab_settings) {
                    fragmentManager.beginTransaction().replace(R.id.fragment_container, FragmentArrayList.get(SETTINGS_FRAGMENT_INDEX)).commit();
                }
            }
        });

        WordList = new ArrayList<>();
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
    }

    public void SearchWord(View view){
        //FIXARE problema del non trovato!
        EditText searchbox = (EditText) findViewById(R.id.SearchBox);
        TextView result = (TextView) findViewById(R.id.Debug_Result_TextView);

        String lookup = searchbox.getText().toString().toLowerCase();

        if(!lookup.isEmpty()) {
            int found_index = Collections.binarySearch(WordList, lookup);
            if (found_index >= 0) {
                result.setText("Trovata " + WordList.get(found_index) + " all'indice " + found_index);
            }
            else{
                result.setText("Word not found!");
            }
        }
        else{
            result.setText("You have to write something!");
        }
    }
}