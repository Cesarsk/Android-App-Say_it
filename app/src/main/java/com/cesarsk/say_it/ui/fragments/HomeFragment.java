package com.cesarsk.say_it.ui.fragments;


import android.app.ActivityOptions;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.media.AudioManager;
import android.os.Bundle;
import android.app.Fragment;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v4.util.Pair;
import android.support.v4.widget.NestedScrollView;

import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cesarsk.say_it.ui.MainActivity;
import com.cesarsk.say_it.ui.PlayActivity;
import com.cesarsk.say_it.R;
import com.cesarsk.say_it.ui.components.FadingTextView;
import com.cesarsk.say_it.ui.SettingsActivity;
import com.cesarsk.say_it.utility.Utility;
import com.cesarsk.say_it.utility.UtilityRecordings;
import com.cesarsk.say_it.utility.UtilitySharedPrefs;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.NativeExpressAdView;

import static android.speech.tts.TextToSpeech.QUEUE_FLUSH;
import static com.cesarsk.say_it.ui.MainActivity.IPAofTheDay;
import static com.cesarsk.say_it.ui.MainActivity.wordOfTheDay;
import static com.cesarsk.say_it.utility.UtilityDictionary.getDailyRandomQuote;


/*
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {

    private static final int RECENT_HISTORY_CARD_ROW_LIMIT = 5;
    private boolean favorite_flag = false;
    private View view;

    /*private LinearLayout recentHistoryLinearLayout;
    private LinearLayout.LayoutParams layoutParams;
    private float scale;
    private RelativeLayout recent_search = null; */
    private AudioManager audio;

    public HomeFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        //scale = getResources().getDisplayMetrics().density;

    }

    @Override
    public void onResume() {
        super.onResume();

    /*
        ArrayList<SayItPair> recentHistory = UtilitySharedPrefs.getRecentHistory(getActivity(), RECENT_HISTORY_CARD_ROW_LIMIT);

        //History not empty
        if (recentHistory != null && !(recentHistory.isEmpty())) {
            recentHistoryLinearLayout.removeAllViews();
            recent_search.setVisibility(View.VISIBLE);
            for (int i = 0; i < recentHistory.size(); i++) {
                LinearLayout current_LL = new LinearLayout(getActivity());
                recentHistoryLinearLayout.addView(current_LL);
                current_LL.setLayoutParams(layoutParams);
                TextView wordTextView = new TextView(getActivity());
                wordTextView.setLayoutParams(layoutParams);
                wordTextView.setTextColor(ContextCompat.getColor(getActivity(), R.color.primary_dark));
                wordTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                wordTextView.setTypeface(Typeface.DEFAULT_BOLD);
                wordTextView.setPaddingRelative((int) (12 * scale + 0.5f), 0, (int) (2 * scale + 0.5f), 0);
                wordTextView.setText(recentHistory.get(i).first);
                current_LL.addView(wordTextView);
                TextView ipaTextView = new TextView(getActivity());
                ipaTextView.setLayoutParams(layoutParams);
                ipaTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
                //ipaTextView.setPaddingRelative((int) (16 * scale + 0.5f), 0, (int) (8 * scale + 0.5f), 0);
                ipaTextView.setText(recentHistory.get(i).second);
                current_LL.addView(ipaTextView);
            }
        } else if (recent_search.getVisibility() != View.GONE) {
            recent_search.setVisibility(View.GONE);
        }

    */
        //Setup our Stats
        if (MainActivity.RECORDINGS != null || MainActivity.FAVORITES != null) {

            RelativeLayout card_stats = (RelativeLayout) view.findViewById(R.id.card_stats);
            final TextView stats_item1 = (TextView) view.findViewById(R.id.card_stats_item1);
            final TextView stats_item2 = (TextView) view.findViewById(R.id.card_stats_item2);

            if (!(MainActivity.RECORDINGS.isEmpty())) {
                card_stats.setVisibility(View.VISIBLE);
                stats_item1.setVisibility(View.VISIBLE);
                UtilityRecordings.updateRecordings(getActivity());
                stats_item1.setText("You've \uD83C\uDFB5 " + MainActivity.RECORDINGS.size() + " words so far!");
            } else {
                stats_item1.setVisibility(View.GONE);
            }

            if (!MainActivity.FAVORITES.isEmpty()) {
                card_stats.setVisibility(View.VISIBLE);
                stats_item2.setVisibility(View.VISIBLE);
                stats_item2.setText("You've ♥ " + MainActivity.FAVORITES.size() + " words so far!");
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

        /*recentHistoryLinearLayout = (LinearLayout) view.findViewById(R.id.recent_hist_linear_layout);
        recent_search = (RelativeLayout) view.findViewById(R.id.Recent_Search);*/

        Typeface plainItalic = Typeface.createFromAsset(getActivity().getAssets(), "fonts/GentiumPlus-I.ttf");
        Typeface plainRegular = Typeface.createFromAsset(getActivity().getAssets(), "fonts/GentiumPlus-R.ttf");

        UtilitySharedPrefs.loadAdsStatus(getActivity());
        if (!MainActivity.NO_ADS) {
            NativeExpressAdView adView = new NativeExpressAdView(getActivity());
            RelativeLayout.LayoutParams adViewlayoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            adView.setLayoutParams(adViewlayoutParams);
            adView.setAdUnitId(getString(R.string.ad_unit_id_native_card));

            DisplayMetrics displayMetrics = getActivity().getResources().getDisplayMetrics();
            float dpWidth = displayMetrics.widthPixels / displayMetrics.density;
            int adWidth = (int) dpWidth - 16;

            adView.setAdSize(new AdSize(adWidth, 150));

            RelativeLayout adCardRL = (RelativeLayout) view.findViewById(R.id.adNativeCard);
            /* 08-08-2017 -> One month premium no ads
            adCardRL.setVisibility(View.VISIBLE);*/
            adCardRL.setVisibility(View.GONE);
            adCardRL.addView(adView);

            AdRequest request = new AdRequest.Builder().addTestDevice(getString(R.string.test_device_oneplus_3)).addTestDevice(getString(R.string.test_device_honor_6)).addTestDevice(getString(R.string.test_device_htc_one_m8)).build();
            adView.loadAd(request);
        }

        final FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.floating_button_home);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Intent intent = new Intent(getActivity(), SettingsActivity.class);
                startActivity(intent);
            }
        });

        final NestedScrollView scroller = (NestedScrollView) view.findViewById(R.id.nested_scroll_view);
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


        final ImageButton favorite_button = (ImageButton) view.findViewById(R.id.favorite_card_button);
        favorite_flag = UtilitySharedPrefs.checkFavs(getActivity(), wordOfTheDay);
        if (favorite_flag)
            favorite_button.setColorFilter(ContextCompat.getColor(getActivity(), R.color.Red700));
        favorite_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!favorite_flag) {
                    UtilitySharedPrefs.addFavs(v.getContext(), new Pair<>(wordOfTheDay, IPAofTheDay));
                    favorite_flag = !favorite_flag;
                    favorite_button.setColorFilter(ContextCompat.getColor(getActivity(), R.color.Red700));
                } else {
                    favorite_button.setColorFilter(ContextCompat.getColor(getActivity(), R.color.White));
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
                ClipData clip = ClipData.newPlainText(null, wordOfTheDay + " [" + IPAofTheDay + "]");
                clipboard.setPrimaryClip(clip);
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

        ImageButton share_word = (ImageButton) view.findViewById(R.id.share_word_button_card);
        share_word.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utility.shareWord(wordOfTheDay, IPAofTheDay, getActivity());
            }
        });

        final TextView random_quote = (TextView) view.findViewById(R.id.random_quote);
        random_quote.setText(getDailyRandomQuote());

        final TextView view_full_history = (TextView) view.findViewById(R.id.view_full_history);
        view_full_history.setText(getString(R.string.full_history_button));
        view_full_history.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.bottomBar.selectTabAtPosition(2);
            }
        });

        LinearLayout linearLayoutFirstRow = (LinearLayout) view.findViewById(R.id.first_row_linear_layout);
        LinearLayout linearLayoutSecondRow = (LinearLayout) view.findViewById(R.id.second_row_linear_layout);
        LinearLayout linearLayoutThirdRow = (LinearLayout) view.findViewById(R.id.third_row_linear_layout);

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
}