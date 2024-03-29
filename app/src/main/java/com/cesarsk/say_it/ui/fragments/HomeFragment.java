package com.cesarsk.say_it.ui.fragments;


import android.app.Activity;
import android.app.ActivityOptions;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.app.Fragment;
import android.os.Debug;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.util.Pair;
import android.support.v4.widget.NestedScrollView;

import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cesarsk.say_it.ui.activities.MainActivity;
import com.cesarsk.say_it.ui.activities.PlayActivity;
import com.cesarsk.say_it.R;
import com.cesarsk.say_it.ui.components.FadingTextView;
import com.cesarsk.say_it.ui.activities.SettingsActivity;
import com.cesarsk.say_it.utility.Utility;
import com.cesarsk.say_it.utility.UtilityRecordings;
import com.cesarsk.say_it.utility.UtilitySharedPrefs;
import com.google.ads.mediation.admob.AdMobAdapter;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.NativeExpressAdView;

import static android.speech.tts.TextToSpeech.QUEUE_FLUSH;
import static com.cesarsk.say_it.ui.activities.MainActivity.IPAofTheDay;
import static com.cesarsk.say_it.ui.activities.MainActivity.isLoggingEnabled;
import static com.cesarsk.say_it.ui.activities.MainActivity.wordOfTheDay;
import static com.cesarsk.say_it.ui.activities.MainActivity.wordOfTheGame;
import static com.cesarsk.say_it.ui.activities.MainActivity.IPAofTheGame;
import static com.cesarsk.say_it.utility.UtilityDictionary.getDailyRandomQuote;


/*
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {

    private static final int RECENT_HISTORY_CARD_ROW_LIMIT = 5;
    private boolean favorite_flag = false;
    private View view;
    private boolean slow_temp = false;

    /*private LinearLayout recentHistoryLinearLayout;
    private LinearLayout.LayoutParams layoutParams;
    private float scale;
    private RelativeLayout recent_search = null; */
    private AudioManager audio;

    public HomeFragment() {
    }

    @Override
    public void onResume() {
        super.onResume();

        //Setup our Stats
        if (MainActivity.RECORDINGS != null || MainActivity.FAVORITES != null) {

            RelativeLayout card_stats = view.findViewById(R.id.card_stats);
            final TextView stats_item1 = view.findViewById(R.id.card_stats_item1);
            final TextView stats_item2 = view.findViewById(R.id.card_stats_item2);

            if (!(MainActivity.RECORDINGS.isEmpty())) {
                card_stats.setVisibility(View.VISIBLE);
                stats_item1.setVisibility(View.VISIBLE);
                UtilityRecordings.updateRecordings(getActivity());
                stats_item1.setText(getString(R.string.card_history_first_part) + MainActivity.RECORDINGS.size() + getString(R.string.card_history_second_part));
            } else {
                stats_item1.setVisibility(View.GONE);
            }

            if (!MainActivity.FAVORITES.isEmpty()) {
                card_stats.setVisibility(View.VISIBLE);
                stats_item2.setVisibility(View.VISIBLE);
                stats_item2.setText(getString(R.string.card_history_fav_first_part) + MainActivity.FAVORITES.size() + getString(R.string.card_history_fav_second_part));
            } else {
                stats_item2.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        //Get audio service
        audio = (AudioManager) getActivity().getSystemService(Context.AUDIO_SERVICE);

        this.view = inflater.inflate(R.layout.fragment_home,
                container, false);

        Typeface plainItalic = Typeface.createFromAsset(getActivity().getAssets(), "fonts/GentiumPlus-I.ttf");
        Typeface plainRegular = Typeface.createFromAsset(getActivity().getAssets(), "fonts/GentiumPlus-R.ttf");

        UtilitySharedPrefs.loadAdsStatus(getActivity());
        UtilitySharedPrefs.loadCardGamePrefs(getActivity());

        final FloatingActionButton fab = view.findViewById(R.id.floating_button_home);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Intent intent = new Intent(getActivity(), SettingsActivity.class);
                startActivity(intent);
            }
        });

        final NestedScrollView scroller = view.findViewById(R.id.nested_scroll_view);
        if (scroller != null) {
            scroller.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
                @Override
                public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {

                    if (scrollY > oldScrollY) {
                        if (MainActivity.isLoggingEnabled) Log.i("DEBUG", "Scroll DOWN");
                        fab.hide();
                    }
                    if (scrollY < oldScrollY) {
                        if (MainActivity.isLoggingEnabled) Log.i("DEBUG", "Scroll UP");
                    }

                    if (scrollY == 0) {
                        if (MainActivity.isLoggingEnabled) Log.i("DEBUG", "TOP SCROLL");
                        fab.show();
                    }

                    if (scrollY == (v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight())) {
                        if (MainActivity.isLoggingEnabled) Log.i("DEBUG", "BOTTOM SCROLL");
                    }
                }
            });
        }

        final TextView wordOfTheDayTextView = (TextView) view.findViewById(R.id.WOTD_word);
        wordOfTheDay = wordOfTheDay.substring(0, 1).toUpperCase() + wordOfTheDay.substring(1);
        wordOfTheDayTextView.setTypeface(plainRegular);
        wordOfTheDayTextView.setText(wordOfTheDay);
        final TextView IPATextView = (TextView) view.findViewById(R.id.ipa_wotd);
        IPATextView.setTypeface(plainItalic);
        IPATextView.setText(IPAofTheDay);

        final Button gameCardPlayButton = (Button) view.findViewById(R.id.game_card_play_word);
        gameCardPlayButton.setTypeface(plainItalic);
        gameCardPlayButton.setText(IPAofTheGame);

        final TextView gameCardStreak = view.findViewById(R.id.game_card_streak);
        gameCardStreak.setText("Your current streak: " +MainActivity.GAME_STREAK);

        final EditText wordOfTheGame_editText = view.findViewById(R.id.card_game_edit_Text);

        if(isLoggingEnabled) Log.i("STRING TEST", "MainActivity.wotg: "+MainActivity.wordOfTheGame+"\nMainActivity.WOFG: "+MainActivity.WORD_OF_THE_GAME);
        if(MainActivity.WORD_OF_THE_GAME.equals(MainActivity.wordOfTheGame.trim()))
        {
            gameCardPlayButton.setText(R.string.you_rock);
            gameCardPlayButton.setEnabled(false);
            wordOfTheGame_editText.setFocusable(false);
            wordOfTheGame_editText.setText("");
            wordOfTheGame_editText.setHint("See you tomorrow for a new word!");
        }

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


        final ImageButton favorite_button = view.findViewById(R.id.favorite_card_button);
        favorite_flag = UtilitySharedPrefs.checkFavs(getActivity(), wordOfTheDay);
        if (favorite_flag)
            favorite_button.setColorFilter(Utility.setColorByTheme(R.attr.favoriteButton, view.getContext()));
        favorite_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!favorite_flag) {
                    UtilitySharedPrefs.addFavs(v.getContext(), new Pair<>(wordOfTheDay, IPAofTheDay));
                    favorite_flag = !favorite_flag;
                    favorite_button.setColorFilter(Utility.setColorByTheme(R.attr.favoriteButton, view.getContext()));
                } else {
                    favorite_button.setColorFilter(Utility.setColorByTheme(R.attr.unfavoriteButton, view.getContext()));
                    UtilitySharedPrefs.removeFavs(v.getContext(), new Pair<>(wordOfTheDay, IPAofTheDay));
                    favorite_flag = !favorite_flag;
                }
            }
        });

        ImageButton copy_button = view.findViewById(R.id.copy_button_card);
        copy_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardManager clipboard = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText(null, wordOfTheDay + " [" + IPAofTheDay + "]");
                if(clipboard != null) clipboard.setPrimaryClip(clip);
                Toast.makeText(getActivity(), "Copied to Clipboard!", Toast.LENGTH_SHORT).show();
            }
        });

        ImageButton quick_play = (ImageButton) view.findViewById(R.id.quick_play_button_card);
        quick_play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isVolumeMuted()) {
                    if (MainActivity.DEFAULT_ACCENT.equals("0")) {
                        MainActivity.american_speaker_google.speak(wordOfTheDay, QUEUE_FLUSH, null, null);
                    } else if (MainActivity.DEFAULT_ACCENT.equals("1")) {
                        MainActivity.british_speaker_google.speak(wordOfTheDay, QUEUE_FLUSH, null, null);
                    }
                } else {
                    Toast toast = Toast.makeText(getActivity(), "Please turn the volume up", Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                }
            }
        });

        gameCardPlayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isVolumeMuted()) {
                    if (MainActivity.DEFAULT_ACCENT.equals("0")) {
                        MainActivity.american_speaker_google.setSpeechRate(0.60f);
                        MainActivity.american_speaker_google.speak(wordOfTheGame, QUEUE_FLUSH, null, null);
                        MainActivity.american_speaker_google.setSpeechRate(1f);

                    } else if (MainActivity.DEFAULT_ACCENT.equals("1")) {
                        MainActivity.british_speaker_google.setSpeechRate(0.60f);
                        MainActivity.british_speaker_google.speak(wordOfTheGame, QUEUE_FLUSH, null, null);
                        MainActivity.british_speaker_google.setSpeechRate(1f);
                    }
                } else {
                    Toast toast = Toast.makeText(getActivity(), "Please turn the volume up", Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                }
            }
        });

        ImageButton share_word = view.findViewById(R.id.share_word_button_card);
        share_word.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utility.shareWord(wordOfTheDay, IPAofTheDay, getActivity());
            }
        });

        final MediaPlayer mp_pos = MediaPlayer.create(getActivity(), R.raw.clap);
        final MediaPlayer mp_neg = MediaPlayer.create(getActivity(), R.raw.oh_no);

        wordOfTheGame_editText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                wordOfTheGame_editText.setCursorVisible(true);
            }
        });

        ImageButton submit_word = view.findViewById(R.id.submit_word);
        submit_word.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String typedWord = wordOfTheGame_editText.getText().toString().toLowerCase();
                wordOfTheGame = wordOfTheGame.trim();

                if(MainActivity.isLoggingEnabled) {
                    Log.i("STRING TEST", "" + typedWord);
                    Log.i("STRING TEST", "" + wordOfTheGame);
                }

                if (typedWord.compareTo(wordOfTheGame) == 0) {
                    gameCardPlayButton.setText(R.string.you_rock);
                    gameCardPlayButton.setEnabled(false);
                    hideSoftKeyboard(getActivity());
                    wordOfTheGame_editText.setFocusable(false);
                    wordOfTheGame_editText.setText("");
                    wordOfTheGame_editText.setHint("See you tomorrow for a new word!");
                    MainActivity.GAME_STREAK++;
                    gameCardStreak.setText(getString(R.string.game_card_current_streak + MainActivity.GAME_STREAK));
                    UtilitySharedPrefs.savePrefs(getActivity(), MainActivity.GAME_STREAK, MainActivity.GAME_STREAK_KEY);
                    UtilitySharedPrefs.savePrefs(getActivity(), MainActivity.wordOfTheGame, MainActivity.WORD_OF_THE_GAME_KEY);
                    mp_pos.start();
                } else {
                    MainActivity.GAME_STREAK = 0;
                    wordOfTheGame_editText.setHint("Oh no! Try again!");
                    wordOfTheGame_editText.setText("");
                    mp_neg.start();
                    gameCardStreak.setText(getString(R.string.game_card_current_streak) + MainActivity.GAME_STREAK);
                    UtilitySharedPrefs.savePrefs(getActivity(), MainActivity.GAME_STREAK, MainActivity.GAME_STREAK_KEY);
                }

            }
        });

        final TextView random_quote = view.findViewById(R.id.random_quote);
        random_quote.setText(getDailyRandomQuote());

        final TextView view_full_history = view.findViewById(R.id.view_full_history);
        view_full_history.setText(getString(R.string.full_history_button));
        view_full_history.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });

        AdView mAdView = view.findViewById(R.id.adView);
        if (MainActivity.NO_ADS) {
            mAdView.setVisibility(View.GONE);
        } else {
            Bundle extras = new Bundle();
            extras.putBoolean("is_designed_for_families", true);
            MobileAds.initialize(getActivity().getApplicationContext(), getResources().getString(R.string.ad_unit_id_banner_homeFragment));
            AdRequest adRequest = new AdRequest.Builder()
                    .addTestDevice(getString(R.string.test_device_oneplus_3))
                    .addTestDevice(getString(R.string.test_device_honor_6))
                    .addTestDevice(getString(R.string.test_device_htc_one_m8))
                    .addNetworkExtrasBundle(AdMobAdapter.class, extras)
                    .tagForChildDirectedTreatment(true)
                    .build();
            mAdView.loadAd(adRequest);
        }

        LinearLayout linearLayoutFirstRow = view.findViewById(R.id.first_row_linear_layout);
        LinearLayout linearLayoutSecondRow = view.findViewById(R.id.second_row_linear_layout);
        LinearLayout linearLayoutThirdRow = view.findViewById(R.id.third_row_linear_layout);

        for (int i = 0; i < 2; i++) {
            linearLayoutFirstRow.addView(new FadingTextView(getActivity()));
            linearLayoutThirdRow.addView(new FadingTextView(getActivity()));
        }

        for (int i = 0; i < 1; i++) {
            linearLayoutSecondRow.addView(new FadingTextView(getActivity()));
        }

        return view;
    }

    private boolean isVolumeMuted() {
        int currentVolume = audio.getStreamVolume(AudioManager.STREAM_MUSIC);
        if (currentVolume == 0) return true;
        else return false;
    }

    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager =
                (InputMethodManager) activity.getSystemService(
                        Activity.INPUT_METHOD_SERVICE);
        if(activity.getCurrentFocus() != null) {
            inputMethodManager.hideSoftInputFromWindow(
                    activity.getCurrentFocus().getWindowToken(), 0);
        }
    }
}