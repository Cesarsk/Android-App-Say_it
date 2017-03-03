package com.example.cesarsk.say_it;


import android.graphics.Paint;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.app.FragmentManager;
import android.widget.TextView;

import static com.example.cesarsk.say_it.MainActivity.wordOfTheDay;


/*
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {

    boolean anim_direction1= false;
    boolean anim_direction2= false;
    boolean anim_direction3 = false;
    boolean anim_direction4 = false;
    boolean anim_direction6 = false;
    boolean anim_direction7 = false;
    boolean anim_direction8 = false;
    boolean anim_direction9 = false;


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
        final TextView sixth_wotd = (TextView)view.findViewById(R.id.sixth_wotd);
        final TextView seventh_wotd = (TextView)view.findViewById(R.id.seventh_wotd);
        final TextView eighth_wotd = (TextView)view.findViewById(R.id.eighth_wotd);
        final TextView ninth_wotd = (TextView)view.findViewById(R.id.ninth_wotd);

        first_wotd.setText(Utility.getRandomWord(getActivity()));
        first_wotd.setPaintFlags(first_wotd.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        second_wotd.setText(Utility.getRandomWord(getActivity()));
        second_wotd.setPaintFlags(second_wotd.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        third_wotd.setText(Utility.getRandomWord(getActivity()));
        third_wotd.setPaintFlags(third_wotd.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        fourth_wotd.setText(Utility.getRandomWord(getActivity()));
        fourth_wotd.setPaintFlags(fourth_wotd.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        sixth_wotd.setText(Utility.getRandomWord(getActivity()));
        sixth_wotd.setPaintFlags(sixth_wotd.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        seventh_wotd.setText(Utility.getRandomWord(getActivity()));
        seventh_wotd.setPaintFlags(seventh_wotd.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        eighth_wotd.setText(Utility.getRandomWord(getActivity()));
        eighth_wotd.setPaintFlags(eighth_wotd.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        ninth_wotd.setText(Utility.getRandomWord(getActivity()));
        ninth_wotd.setPaintFlags(ninth_wotd.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        AlphaAnimation anim = new AlphaAnimation(0.0f, 1.0f);
        anim.setDuration(2000 + (int)(Math.random() * 4000));
        anim.setRepeatCount(5);
        anim.setRepeatMode(Animation.REVERSE);

        AlphaAnimation anim2 = new AlphaAnimation(0.0f, 1.0f);
        anim2.setDuration(5000 + (int)(Math.random() * 8000));
        anim2.setRepeatCount(Animation.INFINITE);
        anim2.setRepeatMode(Animation.REVERSE);

        AlphaAnimation anim3 = new AlphaAnimation(0.0f, 1.0f);
        anim3.setDuration(5000 + (int)(Math.random() * 8000));
        anim3.setRepeatCount(Animation.INFINITE);
        anim3.setRepeatMode(Animation.REVERSE);

        AlphaAnimation anim4 = new AlphaAnimation(0.0f, 1.0f);
        anim4.setDuration(5000 + (int)(Math.random() * 8000));
        anim4.setRepeatCount(Animation.INFINITE);
        anim4.setRepeatMode(Animation.REVERSE);

        AlphaAnimation anim6 = new AlphaAnimation(0.0f, 1.0f);
        anim6.setDuration(5000 + (int)(Math.random() * 8000));
        anim6.setRepeatCount(Animation.INFINITE);
        anim6.setRepeatMode(Animation.REVERSE);

        AlphaAnimation anim7 = new AlphaAnimation(0.0f, 1.0f);
        anim7.setDuration(5000 + (int)(Math.random() * 8000));
        anim7.setRepeatCount(Animation.INFINITE);
        anim7.setRepeatMode(Animation.REVERSE);

        AlphaAnimation anim8 = new AlphaAnimation(0.0f, 1.0f);
        anim8.setDuration(5000 + (int)(Math.random() * 8000));
        anim8.setRepeatCount(Animation.INFINITE);
        anim8.setRepeatMode(Animation.REVERSE);

        AlphaAnimation anim9 = new AlphaAnimation(0.0f, 1.0f);
        anim9.setDuration(5000 + (int)(Math.random() * 8000));
        anim9.setRepeatCount(Animation.INFINITE);
        anim9.setRepeatMode(Animation.REVERSE);

        first_wotd.startAnimation(anim);
        second_wotd.startAnimation(anim2);
        third_wotd.startAnimation(anim3);
        fourth_wotd.startAnimation(anim4);
        sixth_wotd.startAnimation(anim6);
        seventh_wotd.startAnimation(anim7);
        eighth_wotd.startAnimation(anim8);
        ninth_wotd.startAnimation(anim9);

        //TODO RIBILANCIARE LE SCRITTE DELLE ANIMAZIONI, SONO TROPPO PRECISINE DIOCRISTO

        anim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                if(anim_direction1) first_wotd.setText(Utility.getRandomWord(getActivity()));
                anim_direction1=!anim_direction1;
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
                if(anim_direction2) second_wotd.setText(Utility.getRandomWord(getActivity()));
                anim_direction2 = !anim_direction2;
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
                if(anim_direction3) third_wotd.setText(Utility.getRandomWord(getActivity()));
                anim_direction3 = !anim_direction3;
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
                if(anim_direction4) fourth_wotd.setText(Utility.getRandomWord(getActivity()));
                anim_direction4 = !anim_direction4;
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
                if(anim_direction6) sixth_wotd.setText(Utility.getRandomWord(getActivity()));
                anim_direction6 = !anim_direction6;
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
                if(anim_direction7) seventh_wotd.setText(Utility.getRandomWord(getActivity()));
                anim_direction7 = !anim_direction7;
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
                if(anim_direction8) eighth_wotd.setText(Utility.getRandomWord(getActivity()));
                anim_direction8 = !anim_direction8;
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
                if(anim_direction9) ninth_wotd.setText(Utility.getRandomWord(getActivity()));
                anim_direction9 = !anim_direction9;
            }
        });

        return view;
    }
}