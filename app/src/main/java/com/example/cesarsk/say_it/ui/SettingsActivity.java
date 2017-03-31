package com.example.cesarsk.say_it.ui;

import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.example.cesarsk.say_it.R;
import com.example.cesarsk.say_it.ui.fragments.NotificationPreferenceFragment;
import com.example.cesarsk.say_it.ui.fragments.SettingsFragment;

public class SettingsActivity extends AppCompatActivity implements SettingsFragment.Callback {
    private static final String TAG_NESTED = "TAG_NESTED";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            //Change arrow color Toolbar
            final Drawable upArrow = ContextCompat.getDrawable(this, R.drawable.abc_ic_ab_back_material);
            upArrow.setColorFilter(ContextCompat.getColor(this, R.color.white), PorterDuff.Mode.SRC_ATOP);
            getSupportActionBar().setHomeAsUpIndicator(upArrow);
        }

        SettingsFragment fragment = new SettingsFragment();
        //TODO ANIMATION fragment.setEnterTransition(new Slide(Gravity.RIGHT));
        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.contentSettings, fragment)
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
        setTitle("Settings");
    }

    @Override
    public void onNestedPreferenceSelected(int key) {
        getFragmentManager().beginTransaction().replace(R.id.contentSettings, NotificationPreferenceFragment.newInstance(key), TAG_NESTED).addToBackStack(TAG_NESTED).commit();
        setTitle("Notification Settings");
    }
}