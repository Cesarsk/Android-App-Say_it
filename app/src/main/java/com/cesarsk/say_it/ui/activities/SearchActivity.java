package com.cesarsk.say_it.ui.activities;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.speech.RecognizerIntent;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.cesarsk.say_it.R;
import com.cesarsk.say_it.ui.adapters.SearchListAdapter;
import com.cesarsk.say_it.utility.Utility;
import com.cesarsk.say_it.utility.UtilitySharedPrefs;

import java.util.ArrayList;
import java.util.Locale;

public class SearchActivity extends AppCompatActivity {

    private final int REQ_CODE_SPEECH_INPUT = 100;
    private EditText editText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //loading default_theme and applying themes
        UtilitySharedPrefs.loadSettingsPrefs(this);
        if (MainActivity.DEFAULT_THEME.equals("0")) {
            setTheme(R.style.BlueYellowStyle_Theme);
        } else if (MainActivity.DEFAULT_THEME.equals("1")) {
            setTheme(R.style.DarkStyle_Theme);
        }else if (MainActivity.DEFAULT_THEME.equals("2")){
            setTheme(R.style.ChristmasStyle_Theme);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        //set default Stream Controller
        setVolumeControlStream(android.media.AudioManager.STREAM_MUSIC);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_search);
        setSupportActionBar(toolbar);
        //noinspection ConstantConditions
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //change arrow color Toolbar
        final Drawable upArrow = ContextCompat.getDrawable(this, R.drawable.ic_ab_back_material);
        upArrow.setColorFilter(Utility.setColorByTheme(R.attr.upArrow, this), PorterDuff.Mode.SRC_ATOP);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);

        final ListView result_list = (ListView) findViewById(R.id.result_list_view);
        final SearchListAdapter adapter = new SearchListAdapter(this);

        result_list.setAdapter(adapter);
        final ImageButton voice_search_button = (ImageButton) findViewById(R.id.search_bar_voice_icon);
        final ImageButton clear_editText = (ImageButton) findViewById(R.id.clear_editText);
        clear_editText.setVisibility(View.GONE);
        editText = (EditText) findViewById(R.id.search_bar_edit_text);
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @SuppressWarnings("unchecked")
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().isEmpty()) {
                    clear_editText.setVisibility(View.GONE);
                    voice_search_button.setVisibility(View.VISIBLE);
                } else {
                    clear_editText.setVisibility(View.VISIBLE);
                    voice_search_button.setVisibility(View.GONE);

                }

                new AsyncFiltering().execute(adapter.getFilter(), s);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        clear_editText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editText.setText("");
                editText.requestFocus();
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);
            }
        });

        //Check Voice Search
        if (getIntent().getBooleanExtra("VOICE_SEARCH_SELECTED", false)) {
            promptSpeechInput();
        }

        //Voice Search Listener
        voice_search_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                promptSpeechInput();
            }
        });
    }


    /**
     * Showing google speech input dialog
     */
    private void promptSpeechInput() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                getString(R.string.speech_prompt));
        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(getApplicationContext(),
                    getString(R.string.speech_not_supported),
                    Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Receiving speech input
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQ_CODE_SPEECH_INPUT: {
                if (resultCode == RESULT_OK && null != data) {

                    ArrayList<String> result = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    editText.setText(result.get(0));
                }
                break;
            }

        }
    }

    private class AsyncFiltering extends AsyncTask {

        @Override
        protected Object doInBackground(Object[] objects) {

            Filter filter = (Filter) objects[0];
            CharSequence s = (CharSequence) objects[1];

            filter.filter(s);

            return null;
        }
    }
}