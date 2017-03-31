package com.example.cesarsk.say_it.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.example.cesarsk.say_it.R;
import com.example.cesarsk.say_it.utility.UtilityDictionary;

import java.io.IOException;

public class LicenseActivity extends Activity {
    public final static String LICENSE = "com.example.cesarsk.say_it.LICENSE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_license);

        final TextView selected_license_text_view = (TextView) findViewById(R.id.text_view);
        Intent intent = getIntent();
        Bundle args = intent.getExtras();
        String selected_license = args.getString(LICENSE);
        if(selected_license.equals("bottom_bar")){
            try {
                selected_license_text_view.setText(UtilityDictionary.load_textfile(this));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if(selected_license.equals("easy_rating_dialog")){
            try {
                selected_license_text_view.setText(UtilityDictionary.load_textfile(this));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if(selected_license.equals("material_show_case")){
            try {
                selected_license_text_view.setText(UtilityDictionary.load_textfile(this));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if(selected_license.equals("gson")){
            try {
                selected_license_text_view.setText(UtilityDictionary.load_textfile(this));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if(selected_license.equals("wordlist")){
            try {
                selected_license_text_view.setText(UtilityDictionary.load_textfile(this));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
}
