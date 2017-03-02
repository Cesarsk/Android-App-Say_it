package com.example.cesarsk.say_it;


import android.app.Activity;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.EditText;
import android.app.FragmentManager;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.Random;

import static android.speech.tts.TextToSpeech.QUEUE_ADD;
import static com.example.cesarsk.say_it.MainActivity.tts;
import static com.example.cesarsk.say_it.MainActivity.voice_american_female;
import static com.example.cesarsk.say_it.MainActivity.voice_british_female;
import static com.example.cesarsk.say_it.MainActivity.wordOfTheDay;


/*
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {
    int cont = 0;
    public HomeFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.fragment_home,
                container, false);

        final TextView wordOfTheDayTextView = (TextView)view.findViewById(R.id.WOTD_word);
        wordOfTheDayTextView.setText(wordOfTheDay);

        final FragmentManager fragmentManager= (getActivity()).getFragmentManager();

        /*ImageButton settings_button = (ImageButton)view.findViewById(R.id.settings_button);
        settings_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO CONFIGURARE SETTINGSACTIVITY
                final Intent intent = new Intent(getActivity(), SettingsActivity.class);
                startActivity(intent);
            }
        });*/

        final TextView first_wotd = (TextView)view.findViewById(R.id.first_wotd);
        final TextView second_wotd = (TextView)view.findViewById(R.id.second_wotd);
        final TextView third_wotd = (TextView)view.findViewById(R.id.third_wotd);
        final TextView fourth_wotd = (TextView)view.findViewById(R.id.fourth_wotd);
        final TextView fifth_wotd = (TextView)view.findViewById(R.id.fifth_wotd);
        final TextView sixth_wotd = (TextView)view.findViewById(R.id.sixth_wotd);
        final TextView seventh_wotd = (TextView)view.findViewById(R.id.seventh_wotd);
        final TextView eighth_wotd = (TextView)view.findViewById(R.id.eighth_wotd);
        final TextView ninth_wotd = (TextView)view.findViewById(R.id.ninth_wotd);

        first_wotd.setText(Utility.getRandomWord(getActivity()));
        second_wotd.setText(Utility.getRandomWord(getActivity()));
        third_wotd.setText(Utility.getRandomWord(getActivity()));
        fourth_wotd.setText(Utility.getRandomWord(getActivity()));
        fifth_wotd.setText(Utility.getRandomWord(getActivity()));
        sixth_wotd.setText(Utility.getRandomWord(getActivity()));
        seventh_wotd.setText(Utility.getRandomWord(getActivity()));
        eighth_wotd.setText(Utility.getRandomWord(getActivity()));
        ninth_wotd.setText(Utility.getRandomWord(getActivity()));

        AlphaAnimation anim = new AlphaAnimation(1.0f, 0.0f);
        anim.setDuration(5000 + (int)(Math.random() * 8000));
        anim.setRepeatCount(5);
        anim.setRepeatMode(Animation.REVERSE);

        AlphaAnimation anim2 = new AlphaAnimation(1.0f, 0.0f);
        anim2.setDuration(5000 + (int)(Math.random() * 8000));
        anim2.setRepeatCount(Animation.INFINITE);
        anim2.setRepeatMode(Animation.REVERSE);

        AlphaAnimation anim3 = new AlphaAnimation(1.0f, 0.0f);
        anim3.setDuration(5000 + (int)(Math.random() * 8000));
        anim3.setRepeatCount(Animation.INFINITE);
        anim3.setRepeatMode(Animation.REVERSE);

        AlphaAnimation anim4 = new AlphaAnimation(1.0f, 0.0f);
        anim4.setDuration(5000 + (int)(Math.random() * 8000));
        anim4.setRepeatCount(Animation.INFINITE);
        anim4.setRepeatMode(Animation.REVERSE);

        AlphaAnimation anim5 = new AlphaAnimation(1.0f, 0.0f);
        anim5.setDuration(5000 + (int)(Math.random() * 8000));
        anim5.setRepeatCount(Animation.INFINITE);
        anim5.setRepeatMode(Animation.REVERSE);

        AlphaAnimation anim6 = new AlphaAnimation(1.0f, 0.0f);
        anim6.setDuration(5000 + (int)(Math.random() * 8000));
        anim6.setRepeatCount(Animation.INFINITE);
        anim6.setRepeatMode(Animation.REVERSE);

        AlphaAnimation anim7 = new AlphaAnimation(1.0f, 0.0f);
        anim7.setDuration(5000 + (int)(Math.random() * 8000));
        anim7.setRepeatCount(Animation.INFINITE);
        anim7.setRepeatMode(Animation.REVERSE);

        AlphaAnimation anim8 = new AlphaAnimation(1.0f, 0.0f);
        anim8.setDuration(5000 + (int)(Math.random() * 8000));
        anim8.setRepeatCount(Animation.INFINITE);
        anim8.setRepeatMode(Animation.REVERSE);

        AlphaAnimation anim9 = new AlphaAnimation(1.0f, 0.0f);
        anim9.setDuration(5000 + (int)(Math.random() * 8000));
        anim9.setRepeatCount(Animation.INFINITE);
        anim9.setRepeatMode(Animation.REVERSE);

        first_wotd.startAnimation(anim);
        second_wotd.startAnimation(anim2);
        third_wotd.startAnimation(anim3);
        fourth_wotd.startAnimation(anim4);
        fifth_wotd.startAnimation(anim5);
        sixth_wotd.startAnimation(anim6);
        seventh_wotd.startAnimation(anim7);
        eighth_wotd.startAnimation(anim8);
        ninth_wotd.startAnimation(anim9);

        anim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {

            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                first_wotd.setText(Utility.getRandomWord(getActivity()));
            }
        });
        anim2.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {

            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                second_wotd.setText(Utility.getRandomWord(getActivity()));
            }
        });
        anim3.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {

            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                third_wotd.setText(Utility.getRandomWord(getActivity()));
            }
        });
        anim4.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {

            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                fourth_wotd.setText(Utility.getRandomWord(getActivity()));
            }
        });
        anim5.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {

            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                fifth_wotd.setText(Utility.getRandomWord(getActivity()));
            }
        });
        anim6.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {

            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                sixth_wotd.setText(Utility.getRandomWord(getActivity()));
            }
        });
        anim7.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {

            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                seventh_wotd.setText(Utility.getRandomWord(getActivity()));
            }
        });
        anim8.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {

            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                eighth_wotd.setText(Utility.getRandomWord(getActivity()));
            }
        });
        anim9.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {

            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                ninth_wotd.setText(Utility.getRandomWord(getActivity()));
            }
        });


        return view;
    }
}