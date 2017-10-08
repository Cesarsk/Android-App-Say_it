package com.cesarsk.say_it.ui;

import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.cesarsk.say_it.R;
import com.cesarsk.say_it.ui.fragments.NestedPreferenceFragment;
import com.cesarsk.say_it.ui.fragments.SettingsFragment;
import com.cesarsk.say_it.utility.Utility;

public class SettingsActivity extends AppCompatActivity implements SettingsFragment.Callback {
    private static final String TAG_NESTED = "TAG_NESTED";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            //noinspection ConstantConditions
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            //changing arrow color Toolbar
            final Drawable upArrow = ContextCompat.getDrawable(this, R.drawable.ic_ab_back_material);
            upArrow.setColorFilter(Utility.setColorByTheme(R.attr.upArrow, this), PorterDuff.Mode.SRC_ATOP);
            getSupportActionBar().setHomeAsUpIndicator(upArrow);
        }

        //this activity is used to host a SettingFragment, which contains all the preferences of the app (defined in preferences.xml)
        SettingsFragment fragment = new SettingsFragment();
        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .replace(R.id.contentSettings, fragment)
                    .commit();
        }
    }

    @Override
    public void onBackPressed() {
        // this if statement is necessary to navigate through nested and main fragments
        if (getFragmentManager().getBackStackEntryCount() == 0) {
            super.onBackPressed();
        } else {
            getFragmentManager().popBackStack();
        }
    }

    @Override
    public void onNestedPreferenceSelected(int key) {
        getFragmentManager().beginTransaction().replace(R.id.contentSettings, NestedPreferenceFragment.newInstance(key), TAG_NESTED).addToBackStack(TAG_NESTED).commit();
    }

}