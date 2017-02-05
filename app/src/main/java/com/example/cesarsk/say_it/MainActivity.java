package com.example.cesarsk.say_it;

import android.app.Fragment;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.ncapdevi.fragnav.FragNavController;
import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnTabReselectListener;
import com.roughike.bottombar.OnTabSelectListener;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends FragmentActivity {

    private FragNavController fragNavController;

    private final int TAB_FIRST = FragNavController.TAB1;
    private final int TAB_SECOND = FragNavController.TAB2;
    private final int TAB_THIRD = FragNavController.TAB3;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //FragNav - list of fragments
        List<Fragment> fragments = new ArrayList<>(3);

        //add fragments to list
  //      fragments.add(FirstFragment.newInstance(0));
  //      fragments.add(SecondFragment.newInstance(0));
  //      fragments.add(ThirdFragment.newInstance(0));

        //link fragments to container
  //      fragNavController = new FragNavController(getSupportFragmentManager(),R.id.bottomBar,fragments);
        //End of FragNav

        final BottomBar bottomBar = (BottomBar) findViewById(R.id.bottomBar);

        bottomBar.selectTabAtPosition(1);
        bottomBar.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelected(@IdRes int tabId) {
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