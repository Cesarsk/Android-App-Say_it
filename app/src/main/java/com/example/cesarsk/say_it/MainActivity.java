package com.example.cesarsk.say_it;

import android.app.Fragment;
import android.app.FragmentManager;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnTabReselectListener;
import com.roughike.bottombar.OnTabSelectListener;

import java.util.ArrayList;


public class MainActivity extends FragmentActivity {

    final int HOME_FRAGMENT_INDEX = 0;
    final int FAVORITES_FRAGMENT_INDEX = 1;
    final int HISTORY_FRAGMENT_INDEX = 2;
    final int SEARCH_FRAGMENT_INDEX = 3;
    final int SETTINGS_FRAGMENT_INDEX = 4;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final BottomBar bottomBar = (BottomBar) findViewById(R.id.bottomBar);
        final FragmentManager fragmentManager = getFragmentManager();

        final ArrayList<Fragment> FragmentArrayList = new ArrayList<>();
        FragmentArrayList.add(new HomeFragment());
        FragmentArrayList.add(new FavoritesFragment());
        FragmentArrayList.add(new HistoryFragment());
        FragmentArrayList.add(new SearchFragment());
        FragmentArrayList.add(new SettingsFragment());

        fragmentManager.beginTransaction().add(R.id.fragment_container, FragmentArrayList.get(HOME_FRAGMENT_INDEX));

        bottomBar.selectTabAtPosition(2); //Default: Home
        bottomBar.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelected(@IdRes int tabId) {
                if (tabId == R.id.tab_favorites) {
                    fragmentManager.beginTransaction().replace(R.id.fragment_container, FragmentArrayList.get(FAVORITES_FRAGMENT_INDEX)).commit();
                }

                else if(tabId == R.id.tab_search) {
                    fragmentManager.beginTransaction().replace(R.id.fragment_container, FragmentArrayList.get(SEARCH_FRAGMENT_INDEX)).commit();
                }

                else if(tabId == R.id.tab_home) {
                    fragmentManager.beginTransaction().replace(R.id.fragment_container, FragmentArrayList.get(HOME_FRAGMENT_INDEX)).commit();
                }

                else if(tabId == R.id.tab_history) {
                    fragmentManager.beginTransaction().replace(R.id.fragment_container, FragmentArrayList.get(HISTORY_FRAGMENT_INDEX)).commit();
                }

                else if(tabId == R.id.tab_settings) {
                    fragmentManager.beginTransaction().replace(R.id.fragment_container, FragmentArrayList.get(SETTINGS_FRAGMENT_INDEX)).commit();
                }
            }
        });

        bottomBar.setOnTabReselectListener(new OnTabReselectListener() {
            @Override
            public void onTabReSelected(@IdRes int tabId) {
                if (tabId == R.id.tab_favorites) {
                    // The tab with id R.id.tab_favorites was selected,
                    // change your content accordingly.
                }

                else if(tabId == R.id.tab_search) {

                }

                else if(tabId == R.id.tab_home) {

                }

                else if(tabId == R.id.tab_history) {

                }

                else if(tabId == R.id.tab_settings) {

                }
            }
        });
    }
}