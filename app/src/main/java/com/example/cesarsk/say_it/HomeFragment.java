package com.example.cesarsk.say_it;


import android.app.Activity;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.app.FragmentManager;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import static android.speech.tts.TextToSpeech.QUEUE_ADD;
import static com.example.cesarsk.say_it.MainActivity.tts;
import static com.example.cesarsk.say_it.MainActivity.voice_american_female;
import static com.example.cesarsk.say_it.MainActivity.voice_british_female;
import static com.example.cesarsk.say_it.MainActivity.wordOfTheDay;


/*
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {

    public HomeFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.fragment_home,
                container, false);

        TextView wordOfTheDayTextView = (TextView)view.findViewById(R.id.WOTD_word);
        wordOfTheDayTextView.setText(wordOfTheDay);

        final FragmentManager fragmentManager= (getActivity()).getFragmentManager();

        ImageButton settings_button = (ImageButton)view.findViewById(R.id.settings_button);
        settings_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO CONFIGURARE SETTINGSACTIVITY
                final Intent intent = new Intent(getActivity(), SettingsActivity.class);
                startActivity(intent);
            }
        });

        return view;
    }
}