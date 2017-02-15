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
import android.widget.RelativeLayout;

import static android.speech.tts.TextToSpeech.QUEUE_ADD;
import static com.example.cesarsk.say_it.MainActivity.tts;
import static com.example.cesarsk.say_it.MainActivity.voice_american_female;
import static com.example.cesarsk.say_it.MainActivity.voice_british_female;


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


//Old two buttons used to manual TTS a word.
/*Button button = (Button) view.findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                EditText et = (EditText)view.findViewById(R.id.editText);
                tts.setPitch((float)0.85);
                tts.setSpeechRate((float)0.90);
                tts.setVoice(voice_british_female);
                tts.speak(et.getEditableText().toString(), QUEUE_ADD, null, null);
            }
        });

        Button button1 = (Button) view.findViewById(R.id.button2);
        button1.setOnClickListener(new View.OnClickListener()
        {
           @Override
           public void onClick(View v)
           {
               EditText et = (EditText)view.findViewById(R.id.editText);
               tts.setPitch((float)0.85);
               tts.setSpeechRate((float)0.70);
               tts.setVoice(voice_american_female);
               tts.speak(et.getEditableText().toString(), QUEUE_ADD, null, null);
           }
        }); */