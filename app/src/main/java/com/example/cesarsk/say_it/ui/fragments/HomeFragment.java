package com.example.cesarsk.say_it.ui.fragments;


import android.app.Activity;
import android.app.ActivityOptions;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Bundle;
import android.app.Fragment;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.util.Pair;
import android.support.v4.widget.NestedScrollView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.app.FragmentManager;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cesarsk.say_it.ui.MainActivity;
import com.example.cesarsk.say_it.ui.PlayActivity;
import com.example.cesarsk.say_it.R;
import com.example.cesarsk.say_it.ui.components.FadingTextView;
import com.example.cesarsk.say_it.ui.SettingsActivity;
import com.example.cesarsk.say_it.utility.Utility;
import com.example.cesarsk.say_it.utility.UtilityDictionary;
import com.example.cesarsk.say_it.utility.UtilitySharedPrefs;
import com.google.android.gms.ads.NativeExpressAdView;
import com.google.android.gms.ads.VideoController;

import org.w3c.dom.Text;

import static android.speech.tts.TextToSpeech.QUEUE_FLUSH;
import static com.example.cesarsk.say_it.ui.MainActivity.IPAofTheDay;
import static com.example.cesarsk.say_it.ui.MainActivity.american_speaker_google;
import static com.example.cesarsk.say_it.ui.MainActivity.wordOfTheDay;
import static com.example.cesarsk.say_it.utility.UtilitySharedPrefs.getRandomQuote;


/*
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {

    private boolean favorite_flag = false;

    //Gestione ADs
    NativeExpressAdView mAdView;
    VideoController mVideoController;


    public HomeFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.fragment_home,
                container, false);

        Typeface plainItalic = Typeface.createFromAsset(getActivity().getAssets(), "fonts/GentiumPlus-I.ttf");
        Typeface plainRegular = Typeface.createFromAsset(getActivity().getAssets(), "fonts/GentiumPlus-R.ttf");

        final FloatingActionButton fab =(FloatingActionButton) view.findViewById(R.id.floating_button);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Intent intent = new Intent(getActivity(),SettingsActivity.class);
                startActivity(intent);
            }
        });

        final NestedScrollView scroller = (NestedScrollView)view.findViewById(R.id.nested_scroll_view);
        if (scroller != null) {
            scroller.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
                @Override
                public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {

                    if (scrollY > oldScrollY) {
                        //Log.i("DEBUG", "Scroll DOWN");
                        fab.hide();
                    }
                    if (scrollY < oldScrollY) {
                        //Log.i("DEBUG", "Scroll UP");
                    }

                    if (scrollY == 0) {
                        //Log.i("DEBUG", "TOP SCROLL");
                        fab.show();
                    }

                    if (scrollY == (v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight())) {
                        //Log.i("DEBUG", "BOTTOM SCROLL");
                    }
                }
            });
        }


        final TextView wordOfTheDayTextView = (TextView)view.findViewById(R.id.WOTD_word);
        wordOfTheDay = wordOfTheDay.substring(0,1).toUpperCase() + wordOfTheDay.substring(1);
        wordOfTheDayTextView.setTypeface(plainRegular);
        wordOfTheDayTextView.setText(wordOfTheDay);
        final TextView IPATextView = (TextView) view.findViewById(R.id.ipa_wotd);
        IPATextView.setTypeface(plainItalic);
        IPATextView.setText(IPAofTheDay);

        wordOfTheDayTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Intent play_activity_intent = new Intent(getActivity(), PlayActivity.class);
                Bundle args = new Bundle();
                args.putString(PlayActivity.PLAY_WORD, MainActivity.wordOfTheDay);
                args.putString(PlayActivity.PLAY_IPA, MainActivity.IPAofTheDay);
                play_activity_intent.putExtras(args);
                startActivity(play_activity_intent, ActivityOptions.makeSceneTransitionAnimation(getActivity()).toBundle());
            }
        });


        final ImageButton favorite_button = (ImageButton)view.findViewById(R.id.favorite_card_button);
        favorite_flag = UtilitySharedPrefs.checkFavs(getActivity(), wordOfTheDay);
        if(favorite_flag) favorite_button.setColorFilter(getResources().getColor(R.color.RudolphsNose));
        favorite_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!favorite_flag) {
                    UtilitySharedPrefs.addFavs(v.getContext(), new Pair<>(wordOfTheDay, IPAofTheDay));
                    favorite_flag= !favorite_flag;
                    favorite_button.setColorFilter(getResources().getColor(R.color.RudolphsNose));
                }
                else {
                    favorite_button.setColorFilter(getResources().getColor(R.color.white));
                    UtilitySharedPrefs.removeFavs(v.getContext(), new Pair<>(wordOfTheDay, IPAofTheDay));
                    favorite_flag = !favorite_flag;
                }
            }
        });

        ImageButton copy_button = (ImageButton) view.findViewById(R.id.copy_button_card);
        copy_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardManager clipboard = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText(null,wordOfTheDay+" ["+IPAofTheDay+"]");
                clipboard.setPrimaryClip(clip);
                Toast.makeText(getActivity(), "Copied to Clipboard!", Toast.LENGTH_SHORT).show();
            }
        });

        ImageButton quick_play = (ImageButton) view.findViewById(R.id.quick_play_button_card);
        quick_play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO CHECK DEFAULT ACCENT
                american_speaker_google.speak(wordOfTheDay, QUEUE_FLUSH, null, null);
            }
        });

        ImageButton share_word = (ImageButton) view.findViewById(R.id.share_word_button_card);
        share_word.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utility.shareWord(wordOfTheDay, IPAofTheDay, getActivity());
            }
        });

        final TextView random_quote = (TextView)view.findViewById(R.id.random_quote);
        random_quote.setText(getRandomQuote());

        final TextView view_full_history = (TextView)view.findViewById(R.id.view_full_history);
        view_full_history.setText("Full History");
        view_full_history.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.bottomBar.selectTabAtPosition(2);
            }
        });

        final FadingTextView wotd_text_view1 = (FadingTextView)view.findViewById(R.id.first_wotd); wotd_text_view1.setPaintFlags(wotd_text_view1.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        final FadingTextView wotd_text_view2 = (FadingTextView)view.findViewById(R.id.second_wotd); wotd_text_view2.setPaintFlags(wotd_text_view1.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        final FadingTextView wotd_text_view3 = (FadingTextView)view.findViewById(R.id.third_wotd); wotd_text_view3.setPaintFlags(wotd_text_view1.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        final FadingTextView wotd_text_view4 = (FadingTextView)view.findViewById(R.id.fourth_wotd); wotd_text_view4.setPaintFlags(wotd_text_view1.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        final FadingTextView wotd_text_view6 = (FadingTextView)view.findViewById(R.id.sixth_wotd); wotd_text_view6.setPaintFlags(wotd_text_view1.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        final FadingTextView wotd_text_view7 = (FadingTextView)view.findViewById(R.id.seventh_wotd); wotd_text_view7.setPaintFlags(wotd_text_view1.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        final FadingTextView wotd_text_view8 = (FadingTextView)view.findViewById(R.id.eighth_wotd); wotd_text_view8.setPaintFlags(wotd_text_view1.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        final FadingTextView wotd_text_view9 = (FadingTextView)view.findViewById(R.id.ninth_wotd); wotd_text_view9.setPaintFlags(wotd_text_view1.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        final TextView stats_item1 = (TextView)view.findViewById(R.id.card_stats_item1);
        final TextView stats_item2 = (TextView)view.findViewById(R.id.card_stats_item2);

        //TODO DA RIMUOVERE, SOLO DI PROVA
        final TextView history_card_word1 = (TextView)view.findViewById(R.id.history_card_word1);
        final TextView history_card_word2 = (TextView)view.findViewById(R.id.history_card_word2);
        final TextView history_card_word3 = (TextView)view.findViewById(R.id.history_card_word3);
        final TextView history_card_ipa1 = (TextView)view.findViewById(R.id.history_card_ipa1);
        final TextView history_card_ipa2 = (TextView)view.findViewById(R.id.history_card_ipa2);
        final TextView history_card_ipa3 = (TextView)view.findViewById(R.id.history_card_ipa3);
        Pair<String, String> history_pair1 = UtilityDictionary.getRandomWordWithIPA();
        Pair<String, String> history_pair2 = UtilityDictionary.getRandomWordWithIPA();
        Pair<String, String> history_pair3 = UtilityDictionary.getRandomWordWithIPA();
        history_card_word1.setText(history_pair1.first);
        history_card_ipa1.setText(history_pair1.second);
        history_card_word2.setText(history_pair2.first);
        history_card_ipa2.setText(history_pair2.second);
        history_card_word3.setText(history_pair3.first);
        history_card_ipa3.setText(history_pair3.second);
        //RIMUOVERE FIN QUI

        //Setup our Stats
        stats_item1.setText("You've \uD83C\uDFB5 "+" words so far!");
        stats_item2.setText("You've ♥ "+MainActivity.FAVORITES.size()+" words so far!");

        View.OnClickListener random_word_listener = new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                final Intent play_activity_intent = new Intent(v.getContext(), PlayActivity.class);
                switch(v.getId()) {
                    case R.id.first_wotd:
                        play_activity_intent.putExtra(PlayActivity.PLAY_WORD, wotd_text_view1.word);
                        play_activity_intent.putExtra(PlayActivity.PLAY_IPA, wotd_text_view1.ipa);
                        v.getContext().startActivity(play_activity_intent, ActivityOptions.makeSceneTransitionAnimation((Activity) v.getContext()).toBundle());
                        break;

                    case R.id.second_wotd:
                        play_activity_intent.putExtra(PlayActivity.PLAY_WORD, wotd_text_view2.word);
                        play_activity_intent.putExtra(PlayActivity.PLAY_IPA, wotd_text_view2.ipa);
                        v.getContext().startActivity(play_activity_intent, ActivityOptions.makeSceneTransitionAnimation((Activity) v.getContext()).toBundle());
                        break;

                    case R.id.third_wotd:
                        play_activity_intent.putExtra(PlayActivity.PLAY_WORD, wotd_text_view3.word);
                        play_activity_intent.putExtra(PlayActivity.PLAY_IPA, wotd_text_view3.ipa);
                        v.getContext().startActivity(play_activity_intent, ActivityOptions.makeSceneTransitionAnimation((Activity) v.getContext()).toBundle());
                        break;

                    case R.id.fourth_wotd:
                        play_activity_intent.putExtra(PlayActivity.PLAY_WORD, wotd_text_view4.word);
                        play_activity_intent.putExtra(PlayActivity.PLAY_IPA, wotd_text_view4.ipa);
                        v.getContext().startActivity(play_activity_intent, ActivityOptions.makeSceneTransitionAnimation((Activity) v.getContext()).toBundle());
                        break;

                    case R.id.sixth_wotd:
                        play_activity_intent.putExtra(PlayActivity.PLAY_WORD, wotd_text_view6.word);
                        play_activity_intent.putExtra(PlayActivity.PLAY_IPA, wotd_text_view6.ipa);
                        v.getContext().startActivity(play_activity_intent, ActivityOptions.makeSceneTransitionAnimation((Activity) v.getContext()).toBundle());
                        break;

                    case R.id.seventh_wotd:
                        play_activity_intent.putExtra(PlayActivity.PLAY_WORD, wotd_text_view7.word);
                        play_activity_intent.putExtra(PlayActivity.PLAY_IPA, wotd_text_view7.ipa);
                        v.getContext().startActivity(play_activity_intent, ActivityOptions.makeSceneTransitionAnimation((Activity) v.getContext()).toBundle());
                        break;

                    case R.id.eighth_wotd:
                        play_activity_intent.putExtra(PlayActivity.PLAY_WORD, wotd_text_view8.word);
                        play_activity_intent.putExtra(PlayActivity.PLAY_IPA, wotd_text_view8.ipa);
                        v.getContext().startActivity(play_activity_intent, ActivityOptions.makeSceneTransitionAnimation((Activity) v.getContext()).toBundle());
                        break;

                    case R.id.ninth_wotd:
                        play_activity_intent.putExtra(PlayActivity.PLAY_WORD, wotd_text_view9.word);
                        play_activity_intent.putExtra(PlayActivity.PLAY_IPA, wotd_text_view9.ipa);
                        v.getContext().startActivity(play_activity_intent, ActivityOptions.makeSceneTransitionAnimation((Activity) v.getContext()).toBundle());
                        break;

                    default:
                        break;
                }
            }
        };

        wotd_text_view1.setOnClickListener(random_word_listener);
        wotd_text_view2.setOnClickListener(random_word_listener);
        wotd_text_view3.setOnClickListener(random_word_listener);
        wotd_text_view4.setOnClickListener(random_word_listener);
        wotd_text_view6.setOnClickListener(random_word_listener);
        wotd_text_view7.setOnClickListener(random_word_listener);
        wotd_text_view8.setOnClickListener(random_word_listener);
        wotd_text_view9.setOnClickListener(random_word_listener);

        final FragmentManager fragmentManager= (getActivity()).getFragmentManager();

        return view;
    }
}