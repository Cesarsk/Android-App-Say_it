package com.example.cesarsk.say_it;


import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import static android.speech.tts.TextToSpeech.QUEUE_ADD;
import static com.example.cesarsk.say_it.MainActivity.tts;
import static com.example.cesarsk.say_it.MainActivity.voice_american_female;
import static com.example.cesarsk.say_it.MainActivity.voice_british_female;


/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {



    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.fragment_home,
                container, false);
        Button button = (Button) view.findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                EditText et = (EditText)view.findViewById(R.id.editText);
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
               tts.setVoice(voice_american_female);
               tts.speak(et.getEditableText().toString(), QUEUE_ADD, null, null);
           }
        });
        return view;
    }
}
