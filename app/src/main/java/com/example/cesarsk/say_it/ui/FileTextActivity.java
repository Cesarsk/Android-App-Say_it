package com.example.cesarsk.say_it.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.example.cesarsk.say_it.R;
import com.example.cesarsk.say_it.utility.UtilityDictionary;

import java.io.IOException;

public class FileTextActivity extends Activity {
    public final static String PREFERENCE = "com.example.cesarsk.say_it.PREFERENCE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_text);

        final TextView selected_license_text_view = (TextView) findViewById(R.id.text_view);
        Intent intent = getIntent();
        Bundle args = intent.getExtras();
        String selected_preference = args.getString(PREFERENCE);
        if(selected_preference.equals("bottom_bar")){
            try {
                selected_license_text_view.setText(UtilityDictionary.load_textfile(this,R.raw.bottombar_license));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if(selected_preference.equals("easy_rating_dialog")){
            try {
                selected_license_text_view.setText(UtilityDictionary.load_textfile(this,R.raw.easy_rating_dialog_license));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if(selected_preference.equals("material_show_case")){
            try {
                selected_license_text_view.setText(UtilityDictionary.load_textfile(this,R.raw.material_show_case_license));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if(selected_preference.equals("gson")){
            try {
                selected_license_text_view.setText(UtilityDictionary.load_textfile(this,R.raw.gson_license));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if(selected_preference.equals("wordlist")){
            try {
                selected_license_text_view.setText(UtilityDictionary.load_textfile(this,R.raw.wordlist_license));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if(selected_preference.equals("freepik")){
            try {
                selected_license_text_view.setText(UtilityDictionary.load_textfile(this,R.raw.freepik_license));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if(selected_preference.equals("flaticon")){
            try {
                selected_license_text_view.setText(UtilityDictionary.load_textfile(this,R.raw.flaticon_license));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if(selected_preference.equals("acknowledgements")){
            try {
                selected_license_text_view.setText(UtilityDictionary.load_textfile(this,R.raw.acknowledgements));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
