package com.example.cesarsk.say_it;


import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.TimeInterpolator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.ActivityOptions;
import android.app.FragmentTransaction;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.media.Image;
import android.os.Bundle;
import android.app.Fragment;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.util.Pair;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.app.FragmentManager;
import android.view.animation.Interpolator;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.NativeExpressAdView;
import com.google.android.gms.ads.VideoController;
import com.google.android.gms.ads.VideoOptions;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Random;

import static android.speech.tts.TextToSpeech.QUEUE_ADD;
import static android.speech.tts.TextToSpeech.QUEUE_FLUSH;
import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;
import static com.example.cesarsk.say_it.MainActivity.IPAofTheDay;
import static com.example.cesarsk.say_it.MainActivity.WordList;
import static com.example.cesarsk.say_it.MainActivity.american_speaker_google;
import static com.example.cesarsk.say_it.MainActivity.wordOfTheDay;
import static com.example.cesarsk.say_it.Utility.getRandomQuote;
import static com.example.cesarsk.say_it.Utility.shareToMail;


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

        //Gestione AD (TEST AD)
    /*    MobileAds.initialize(getApplicationContext(), "ca-app-pub-3940256099942544/6300978111");
        AdView mAdView = (AdView) findViewById(R.id.adView);
        mAdView.bringToFront();
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest); */
/*
        mAdView = (NativeExpressAdView)view.findViewById(R.id.adViewCard);
        mAdView.setVideoOptions(new VideoOptions.Builder()
                .setStartMuted(true)
                .build());

        AdRequest request = new AdRequest.Builder()
                .addTestDevice("f2e4f110")
                .build();
        mAdView.loadAd(request);

        mVideoController = mAdView.getVideoController();
        mVideoController.setVideoLifecycleCallbacks(new VideoController.VideoLifecycleCallbacks() {
            @Override
            public void onVideoEnd() {
                Log.d("AD DEBUG", "Video playback is finished.");
                super.onVideoEnd();
            }
        });

        mAdView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                if (mVideoController.hasVideoContent()) {
                    Log.d("AD DEBUG", "Received an ad that contains a video asset.");
                } else {
                    Log.d("AD DEBUG", "Received an ad that does not contain a video asset.");
                }
            }
        });

        mAdView.loadAd(new AdRequest.Builder().build());

*/

        Typeface plainItalic = Typeface.createFromAsset(getActivity().getAssets(), "fonts/GentiumPlus-I.ttf");
        Typeface plainRegular = Typeface.createFromAsset(getActivity().getAssets(), "fonts/GentiumPlus-R.ttf");

        final TextView wordOfTheDayTextView = (TextView)view.findViewById(R.id.WOTD_word);
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
        favorite_flag = Utility.checkFavs(getActivity(), wordOfTheDay);
        if(favorite_flag) favorite_button.setColorFilter(getResources().getColor(R.color.RudolphsNose));
        favorite_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!favorite_flag) {
                    Utility.addFavs(v.getContext(), new Pair<>(wordOfTheDay, IPAofTheDay));
                    favorite_flag= !favorite_flag;
                    favorite_button.setColorFilter(getResources().getColor(R.color.RudolphsNose));
                }
                else {
                    favorite_button.setColorFilter(getResources().getColor(R.color.white));
                    Utility.removeFavs(v.getContext(), new Pair<>(wordOfTheDay, IPAofTheDay));
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
                Utility.share(wordOfTheDay, IPAofTheDay, getActivity());
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


        View.OnClickListener random_word_listener = new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                final Intent play_activity_intent = new Intent(v.getContext(), PlayActivity.class);
                switch(v.getId()) {
                    case R.id.first_wotd:
                        play_activity_intent.putExtra(PlayActivity.PLAY_WORD, wotd_text_view1.word);
                        play_activity_intent.putExtra(PlayActivity.PLAY_IPA, wotd_text_view1.ipa);
                        //TODO AGGIUNGERE IPA
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

        FloatingActionButton fab =(FloatingActionButton) view.findViewById(R.id.floating_button);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Intent intent = new Intent(getActivity(),SettingsActivity.class);
                startActivity(intent);
            }
        });


        /*
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener(){
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy){
                if (dy > 0 ||dy<0 && fab.isShown())
                    fab.hide();
            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {

                if (newState == RecyclerView.SCROLL_STATE_IDLE){
                    fab.show();
                }
                super.onScrollStateChanged(recyclerView, newState);
            }
        });
        */

        return view;
    }
}