package com.cesarsk.say_it.ui.activities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.graphics.drawable.TransitionDrawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.CountDownTimer;
import android.os.SystemClock;
import android.os.Vibrator;
import android.speech.tts.TextToSpeech;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.util.Pair;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.OvershootInterpolator;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.cesarsk.say_it.R;
import com.cesarsk.say_it.utility.UtilityRecordings;
import com.cesarsk.say_it.utility.Utility;
import com.cesarsk.say_it.utility.UtilitySharedPrefs;
import com.cesarsk.say_it.utility.utility_aidl.IabHelper;
import com.cesarsk.say_it.utility.utility_aidl.IabResult;
import com.cesarsk.say_it.utility.utility_aidl.Inventory;
import com.cesarsk.say_it.utility.utility_aidl.Purchase;
import com.google.ads.mediation.admob.AdMobAdapter;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import uk.co.deanwild.materialshowcaseview.MaterialShowcaseSequence;
import uk.co.deanwild.materialshowcaseview.MaterialShowcaseView;
import uk.co.deanwild.materialshowcaseview.ShowcaseConfig;

import static android.speech.tts.TextToSpeech.QUEUE_ADD;
import static android.speech.tts.TextToSpeech.QUEUE_FLUSH;
import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;
import static com.cesarsk.say_it.utility.LCSecurity.base64EncodedPublicKey;

public class PlayActivity extends AppCompatActivity {

    //bundle variables
    public final static String PLAY_WORD = "com.example.cesarsk.say_it.WORD";
    public final static String PLAY_IPA = "com.example.cesarsk.say_it.IPA";
    public static String selected_word;
    public static String selected_ipa;

    //snackbar variables
    private Snackbar snackbar;
    private static final long UNDO_TIMEOUT = 3000;

    //recorder and player variables
    private MediaRecorder recorder = null;
    private MediaPlayer mediaPlayer;
    private AudioManager audio;
    public static final int RequestPermissionCode = 1;

    //buttons variables
    private boolean slow_mode = false;
    private boolean accent_flag = false;
    private boolean favorite_flag = false;

    //TTS variables
    private static TextToSpeech american_speaker_google;
    private static TextToSpeech british_speaker_google;

    //in-app and ads variables
    private IabHelper mHelper;
    private IabHelper.QueryInventoryFinishedListener mQueryFinishedListener;
    private IabHelper.OnIabPurchaseFinishedListener mIabPurchaseFinishedListener;
    public static final String no_ads_in_app = "no_ads";
    private InterstitialAd mInterstitialAd;

    //animations variables
    private TransitionDrawable green_animation;
    private final long scaleAnimationDuration = 200;
    private final long scaleAnimationDurationLong = 300;
    private boolean maxDurationReached = false;
    private final int durationMillis = 500;

    //other variables
    private final Context context = this;
    private byte[] temp_recording_bytes;
    private Button rec_button;
    private Vibrator vibrator;

    @Override
    public void onDestroy() {
        //destroying in-app variable to avoid exceptions
        super.onDestroy();
        if (mHelper != null) try {
            mHelper.dispose();
        } catch (IabHelper.IabAsyncInProgressException e) {
            e.printStackTrace();
        }
        mHelper = null;
    }

    @Override
    public void onBackPressed() {
        //show ad after pressing back
        super.onBackPressed();
        if (mInterstitialAd != null) {
            if (mInterstitialAd.isLoaded()) {
                mInterstitialAd.show();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //loading default_theme and applying themes
        UtilitySharedPrefs.loadSettingsPrefs(this);
        if (MainActivity.DEFAULT_THEME.equals("0")) {
            setTheme(R.style.BlueYellowStyle_Theme);
        } else if (MainActivity.DEFAULT_THEME.equals("1")) {
            setTheme(R.style.DarkStyle_Theme);
        } else if (MainActivity.DEFAULT_THEME.equals("2")){
            setTheme(R.style.ChristmasStyle_Theme);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);

        //getting resources
        rec_button = (Button) findViewById(R.id.rec_button);
        final Button play_button = (Button) findViewById(R.id.play_button);
        final TextView selected_word_view = (TextView) findViewById(R.id.selected_word);
        final TextView selected_ipa_view = (TextView) findViewById(R.id.selected_word_ipa);
        final ImageButton delete_button = (ImageButton) findViewById(R.id.delete_button);
        final ImageButton favorite_button = (ImageButton) findViewById(R.id.favorite_button);
        final ImageButton slow_button = (ImageButton) findViewById(R.id.slow_button);
        final ImageButton accent_button = (ImageButton) findViewById(R.id.accent_button);
        final Button play_original_button = (Button) findViewById(R.id.play_original);
        final ImageButton your_recordings = (ImageButton) findViewById(R.id.recordings_button);
        final ImageButton remove_ad = (ImageButton) findViewById(R.id.remove_ads_button);
        final ImageButton search_meaning = (ImageButton) findViewById(R.id.search_meaning_button);
        final ImageButton tts_settings = (ImageButton) findViewById(R.id.tts_settings_button);

        //set default Stream Controller
        setVolumeControlStream(android.media.AudioManager.STREAM_MUSIC);

        //get audio service
        audio = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

        //if ads are disable not show the button
        if(MainActivity.NO_ADS){
            remove_ad.setVisibility(View.GONE);
        }

        //compute your public key and store it in base64EncodedPublicKey
        mHelper = new IabHelper(this, base64EncodedPublicKey);
        mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
            public void onIabSetupFinished(IabResult result) {
                if (!result.isSuccess()) {
                    // Oh no, there was a problem.
                    if (MainActivity.isLoggingEnabled)
                        Log.d("Say It!", "Problem setting up In-app Billing: " + result);
                }
                // Hooray, IAB is fully set up!
                if (MainActivity.isLoggingEnabled)
                    Log.d("Say It!", "Hooray. IAB is fully set up!" + result);
            }
        });

        //getting bundle parameters from other activities
        Intent intent = getIntent();
        Bundle args = intent.getExtras();
        selected_word = args.getString(PLAY_WORD);
        selected_ipa = args.getString(PLAY_IPA);

        //loading tts: if we opened other activities before this one, tts are coming from the main activity. If not, we init them again
        if ((MainActivity.american_speaker_google != null || british_speaker_google != null)) {
            //bind TTs
            american_speaker_google = MainActivity.american_speaker_google;
            british_speaker_google = MainActivity.british_speaker_google;
        } else {
            //init TTSs
            initTTS(context);
        }

        vibrator = (Vibrator) this.getSystemService(Context.VIBRATOR_SERVICE);

        //if premium purchase has been detected, disable ads. If not, init them
        UtilitySharedPrefs.loadAdsStatus(this);

        //setting Up Chronometer
        final Chronometer chronometer = (Chronometer) findViewById(R.id.recording_timer);
        chronometer.setBase(SystemClock.elapsedRealtime());

        //setting Up Recorder/Player
        recorder = new MediaRecorder();
        recorder.setOnInfoListener(new MediaRecorder.OnInfoListener() {
            @Override
            public void onInfo(MediaRecorder mediaRecorder, int what, int extra) {
                if (what == MediaRecorder.MEDIA_RECORDER_INFO_MAX_DURATION_REACHED) {
                    //flag used to check if the max duration has been reached. If so, stop the recording
                    maxDurationReached = true;
                    if(MainActivity.DEFAULT_VIBRATION.equals("1")) vibrator.vibrate(100);
                    else vibrator.vibrate(0);
                    rec_button.setBackground(getDrawable(R.drawable.circle_red));
                    chronometer.stop();

                    UtilityRecordings.stopRecording(context, recorder, selected_word);

                    if (checkDuration(chronometer.getText().toString())) {
                        play_button.setVisibility(VISIBLE);
                        //button reverse animation to NORMAL RED
                        rec_button.animate().setDuration(scaleAnimationDuration).scaleX(1).scaleY(1);
                        //starting overshoot animation
                        rec_button.animate().setDuration(scaleAnimationDurationLong).setInterpolator(new AccelerateDecelerateInterpolator()).scaleX(0).scaleY(0).withEndAction(new Runnable() {
                            @Override
                            public void run() {
                                rec_button.setVisibility(View.INVISIBLE);
                                play_button.animate().setDuration(scaleAnimationDurationLong).setInterpolator(new OvershootInterpolator()).scaleX(1).scaleY(1);
                                delete_button.setAlpha(0f);
                                delete_button.setVisibility(VISIBLE);
                                delete_button.animate().setDuration(scaleAnimationDuration).alpha(1);
                            }
                        });
                    } else {
                        rec_button.animate().setDuration(scaleAnimationDuration).scaleX(1).scaleY(1);
                        rec_button.setBackground(getDrawable(R.drawable.circle_red));
                        UtilityRecordings.deleteRecording(context, selected_word + ".aac");
                    }
                    Toast.makeText(context, "Maximum duration reached!", Toast.LENGTH_SHORT).show();
                }

            }
        });

        //init MediaPlayer
        mediaPlayer = new MediaPlayer();

        //loading settings before checking DEFAULT_ACCENT
        UtilitySharedPrefs.loadSettingsPrefs(this);

        //checking default_accent and set the button's color
        if (MainActivity.DEFAULT_ACCENT.equals("0")) {
            accent_button.setColorFilter(Utility.setColorByTheme(R.attr.idleButton, context));
            accent_flag = false;
        } else if (MainActivity.DEFAULT_ACCENT.equals("1")) {
            accent_button.setColorFilter(Utility.setColorByTheme(R.attr.colorAccent, context));
            accent_flag = true;
        }

        //setting Up Record/Play Buttons
        rec_button.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (UtilityRecordings.checkRecordAudioPermissions(view.getContext())) {
                    switch (motionEvent.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            if(MainActivity.DEFAULT_VIBRATION.equals("1")) vibrator.vibrate(100);
                            else vibrator.vibrate(0);
                            rec_button.setBackground(getDrawable(R.drawable.circle_red_pressed));
                            //Scale animation
                            rec_button.animate().setDuration(scaleAnimationDuration).setInterpolator(new OvershootInterpolator()).scaleX(0.8f).scaleY(0.8f);
                            chronometer.setBase(SystemClock.elapsedRealtime());
                            chronometer.start();
                            UtilityRecordings.startRecording(context, recorder, selected_word);
                            return true;

                        case MotionEvent.ACTION_UP:
                            if (!maxDurationReached) {
                                if(MainActivity.DEFAULT_VIBRATION.equals("1")) vibrator.vibrate(100);
                                else vibrator.vibrate(0);
                                rec_button.setBackground(getDrawable(R.drawable.circle_red));
                                chronometer.stop();

                                UtilityRecordings.stopRecording(view.getContext(), recorder, selected_word);

                                if (checkDuration(chronometer.getText().toString())) {
                                    play_button.setVisibility(VISIBLE);
                                    //Button reverse animation to NORMAL RED
                                    rec_button.animate().setDuration(scaleAnimationDuration).scaleX(1).scaleY(1);
                                    //START OVERSHOOT ANIMATION
                                    rec_button.animate().setDuration(scaleAnimationDurationLong).setInterpolator(new AccelerateDecelerateInterpolator()).scaleX(0).scaleY(0).withEndAction(new Runnable() {
                                        @Override
                                        public void run() {
                                            rec_button.setVisibility(View.INVISIBLE);
                                            play_button.animate().setDuration(scaleAnimationDurationLong).setInterpolator(new OvershootInterpolator()).scaleX(1).scaleY(1);
                                            delete_button.setAlpha(0f);
                                            delete_button.setVisibility(VISIBLE);
                                            delete_button.animate().setDuration(scaleAnimationDuration).alpha(1);
                                        }
                                    });
                                } else {
                                    rec_button.animate().setDuration(scaleAnimationDuration).scaleX(1).scaleY(1);
                                    rec_button.setBackground(getDrawable(R.drawable.circle_red));
                                    UtilityRecordings.deleteRecording(view.getContext(), selected_word + ".aac");
                                }
                                return false;
                            }
                    }
                } else {
                    UtilityRecordings.requestRecordAudioPermissions(context);
                }
                return false;
            }
        });

        //handling snackbar
        setupSnackbar(chronometer, delete_button, play_button);
        final Context context = this;

        delete_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                delete_button.animate().setDuration(scaleAnimationDuration).alpha(0).withEndAction(new Runnable() {
                    @Override
                    public void run() {
                        delete_button.setVisibility(View.GONE);
                    }
                });
                rec_button.setScaleY(0);
                rec_button.setScaleX(0);
                rec_button.setVisibility(VISIBLE);
                play_button.animate().setDuration(scaleAnimationDurationLong).setInterpolator(new AccelerateDecelerateInterpolator()).scaleX(0).scaleY(0).withEndAction(new Runnable() {
                    @Override
                    public void run() {
                        play_button.setVisibility(View.INVISIBLE);
                        rec_button.animate().setDuration(scaleAnimationDurationLong).setInterpolator(new OvershootInterpolator()).scaleX(1).scaleY(1);
                    }
                });
                chronometer.setBase(SystemClock.elapsedRealtime());
                String filename = context.getFilesDir().getAbsolutePath() + "/" + selected_word + ".aac";
                File recording_file = new File(filename);
                temp_recording_bytes = UtilityRecordings.getRecordingBytesfromFile(recording_file);
                UtilityRecordings.deleteRecording(context, recording_file.getName());
                setupSnackbar(chronometer, delete_button, play_button);
                snackbar.show();
            }
        });

        play_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isVolumeMuted()) {
                    delete_button.setEnabled(false);
                    UtilityRecordings.playRecording(view.getContext(), mediaPlayer, selected_word + ".aac");
                    //START GREEN-to-GREEN-PRESSED ANIMATION
                    play_button.setBackground(getDrawable(R.drawable.circle_color_anim_green_to_green_pressed));
                    green_animation = (TransitionDrawable) play_button.getBackground();
                    green_animation.startTransition(durationMillis);
                    play_button.animate().setDuration(durationMillis).scaleX(0.8f).scaleY(0.8f);
                    new CountDownTimer(mediaPlayer.getDuration(), 1000) {
                        @Override
                        public void onTick(long millisUntilFinished) {

                        }

                        @Override
                        public void onFinish() {
                            play_button.setBackground(getDrawable(R.drawable.circle_color_anim_green_pressed_to_green));
                            green_animation = (TransitionDrawable) play_button.getBackground();
                            green_animation.startTransition(durationMillis);
                            play_button.animate().setDuration(durationMillis).scaleX(1).scaleY(1).withEndAction(new Runnable() {
                                @Override
                                public void run() {
                                    delete_button.setEnabled(true);
                                }
                            });
                        }
                    }.start();
                } else {
                    Toast toast = Toast.makeText(context, "Please turn the volume up", Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                }
            }
        });

        if (UtilityRecordings.checkRecordingFile(this, selected_word)) {
            rec_button.setVisibility(View.INVISIBLE);
            play_button.setScaleX(1);
            play_button.setScaleY(1);
            play_button.setVisibility(VISIBLE);
            delete_button.setVisibility(VISIBLE);
            Long duration = UtilityRecordings.getRecordingDuration(this, mediaPlayer, selected_word);
            @SuppressLint("SimpleDateFormat") DateFormat dateFormat = new SimpleDateFormat("mm:ss");
            String durationText = dateFormat.format(new Date(duration));
            chronometer.setText(durationText);
        }

        AlphaAnimation delete_button_anim = new AlphaAnimation(1.0f, 0.0f);
        AlphaAnimation delete_button_anim_reverse = new AlphaAnimation(0.0f, 1.0f);
        delete_button_anim.setDuration(500);
        delete_button_anim_reverse.setDuration(500);
        delete_button_anim_reverse.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                delete_button.setEnabled(true);
                delete_button.setVisibility(VISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        delete_button_anim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                delete_button.setEnabled(false);
                delete_button.setVisibility(INVISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        //setting Gentium font for the text views
        Typeface plainItalic = Typeface.createFromAsset(getAssets(), "fonts/GentiumPlus-I.ttf");
        Typeface plainRegular = Typeface.createFromAsset(getAssets(), "fonts/GentiumPlus-R.ttf");
        selected_word_view.setTypeface(plainRegular);
        selected_ipa_view.setTypeface(plainItalic);
        selected_word_view.setText(selected_word);
        selected_ipa_view.setText(selected_ipa);

        mIabPurchaseFinishedListener = new IabHelper.OnIabPurchaseFinishedListener() {
            @Override
            public void onIabPurchaseFinished(IabResult result, Purchase info) {
                if (result.isFailure()) {
                    Toast.makeText(PlayActivity.this, "Purchase Failed! Perhaps have you already purchased the item?", Toast.LENGTH_SHORT).show();
                } else if (info.getSku().equals(no_ads_in_app)) {
                    UtilitySharedPrefs.loadAdsStatus(PlayActivity.this);
                    UtilitySharedPrefs.savePrefs(PlayActivity.this, true, MainActivity.NO_ADS_STATUS_KEY);
                }
            }
        };

        mQueryFinishedListener = new IabHelper.QueryInventoryFinishedListener() {
            public void onQueryInventoryFinished(IabResult result, Inventory inventory) {
                if (result.isFailure()) {
                    if (MainActivity.isLoggingEnabled)
                        Toast.makeText(PlayActivity.this, "Query Failed!", Toast.LENGTH_SHORT).show();
                    return;
                }

                //open Purchase Dialog
                try {
                    mHelper.flagEndAsync();
                    mHelper.launchPurchaseFlow(PlayActivity.this, no_ads_in_app, 64000, mIabPurchaseFinishedListener);
                } catch (IabHelper.IabAsyncInProgressException e) {
                    e.printStackTrace();
                }
                //update the UI
            }
        };

        remove_ad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<String> additionalSkuList = new ArrayList<>();
                additionalSkuList.add(no_ads_in_app);
                try {
                    mHelper.flagEndAsync();
                    mHelper.queryInventoryAsync(true, additionalSkuList, mQueryFinishedListener);
                } catch (IabHelper.IabAsyncInProgressException e) {
                    e.printStackTrace();
                }
            }
        });

        search_meaning.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utility.searchMeaning(context, selected_word);
            }
        });

        //handling ads
        UtilitySharedPrefs.loadAdsStatus(this);
        AdView mAdView = (AdView) findViewById(R.id.adView);
        if (MainActivity.NO_ADS) {
            mAdView.setVisibility(View.GONE);
        } else {
            Bundle extras = new Bundle();
            extras.putBoolean("is_designed_for_families", true);

            MobileAds.initialize(getApplicationContext(), getResources().getString(R.string.ad_unit_id_banner_playActivity));
            AdRequest adRequest = new AdRequest.Builder()
                    .addTestDevice(getString(R.string.test_device_oneplus_3))
                    .addTestDevice(getString(R.string.test_device_honor_6))
                    .addTestDevice(getString(R.string.test_device_htc_one_m8))
                    .addNetworkExtrasBundle(AdMobAdapter.class, extras)
                    .tagForChildDirectedTreatment(true)
                    .build();
            mAdView.loadAd(adRequest);
        }

        your_recordings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Intent main_activity_intent = new Intent(v.getContext(), MainActivity.class);
                Bundle b = new Bundle();
                b.putInt("fragment_index", 3); //Your id
                main_activity_intent.putExtras(b); //Put your id to your next Intent
                main_activity_intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                v.getContext().startActivity(main_activity_intent);
                finish(); //distruggiamo il play activity relativo alla parola
            }
        });

        favorite_flag = UtilitySharedPrefs.checkFavs(this, selected_word);
        if (favorite_flag)
            favorite_button.setColorFilter(Utility.setColorByTheme(R.attr.favoriteButton, context));

        favorite_button.setOnClickListener(new View.OnClickListener()

        {
            @Override
            public void onClick(View v) {
                if (!favorite_flag) {
                    UtilitySharedPrefs.addFavs(v.getContext(), new Pair<>(selected_word, selected_ipa));
                    favorite_flag = !favorite_flag;
                    Toast.makeText(PlayActivity.this, "Added to favorites!", Toast.LENGTH_SHORT).show();
                    favorite_button.setColorFilter(Utility.setColorByTheme(R.attr.favoriteButton, context));
                } else {
                    favorite_button.setColorFilter(Utility.setColorByTheme(R.attr.idleButton, context));
                    Toast.makeText(PlayActivity.this, "Removed from favorites!", Toast.LENGTH_SHORT).show();
                    UtilitySharedPrefs.removeFavs(v.getContext(), new Pair<>(selected_word, selected_ipa));
                    favorite_flag = !favorite_flag;
                }
            }
        });

        slow_button.setOnClickListener(new View.OnClickListener()

        {
            @Override
            public void onClick(View v) {
                if (!slow_mode) {
                    american_speaker_google.setSpeechRate((float) 0.30);
                    british_speaker_google.setSpeechRate((float) 0.30);
                    slow_mode = !slow_mode;
                    Toast.makeText(PlayActivity.this, "Slow Mode Activated", Toast.LENGTH_SHORT).show();
                    slow_button.setColorFilter(Utility.setColorByTheme(R.attr.colorAccent, context));
                } else {
                    american_speaker_google.setSpeechRate((float) 0.90);
                    british_speaker_google.setSpeechRate((float) 0.90);
                    Toast.makeText(PlayActivity.this, "Slow Mode Deactivated", Toast.LENGTH_SHORT).show();
                    slow_button.setColorFilter(Utility.setColorByTheme(R.attr.idleButton, context));
                    slow_mode = !slow_mode;
                }
            }
        });

        accent_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!accent_flag) {
                    accent_button.setColorFilter(Utility.setColorByTheme(R.attr.colorAccent, context));
                    Toast.makeText(PlayActivity.this, "British Accent selected", Toast.LENGTH_SHORT).show();
                    accent_flag = !accent_flag;
                } else {
                    accent_button.setColorFilter(Utility.setColorByTheme(R.attr.idleButton, context));
                    Toast.makeText(PlayActivity.this, "American English selected", Toast.LENGTH_SHORT).show();
                    accent_flag = !accent_flag;
                }
            }

        });

        play_original_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isVolumeMuted()) {
                    if (!accent_flag) {
                        american_speaker_google.speak(selected_word, QUEUE_FLUSH, null, null);
                        if(MainActivity.DEFAULT_VIBRATION.equals("1")) vibrator.vibrate(100);
                        else vibrator.vibrate(0);
                    } else if (accent_flag) {
                        british_speaker_google.speak(selected_word, QUEUE_FLUSH, null, null);
                        if(MainActivity.DEFAULT_VIBRATION.equals("1")) vibrator.vibrate(100);
                        else vibrator.vibrate(0);
                    }
                } else {
                    Toast toast = Toast.makeText(context, "Please turn the volume up", Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                }
            }
        });

        tts_settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction("com.android.settings.TTS_SETTINGS");
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }

        });
        startTutorialPlayActivity(rec_button, play_original_button, accent_button, slow_button, tts_settings);
    }

    private void setupSnackbar(final Chronometer chronometer, final ImageButton delete_button, final Button play_button) {
        snackbar = Snackbar.make(findViewById(R.id.play_activity_coordinator), "Deleted Recording", (int) UNDO_TIMEOUT);
        snackbar.setAction("UNDO", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                File recovered_file = new File(view.getContext().getFilesDir().getAbsolutePath() + "/" + selected_word + ".aac");
                FileOutputStream outputStream;
                try {
                    outputStream = new FileOutputStream(recovered_file);
                    outputStream.write(temp_recording_bytes);
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                Long duration = UtilityRecordings.getRecordingDuration(view.getContext(), mediaPlayer, selected_word);
                @SuppressLint("SimpleDateFormat") DateFormat dateFormat = new SimpleDateFormat("mm:ss");
                String durationText = dateFormat.format(new Date(duration));
                chronometer.setText(durationText);

                delete_button.setAlpha(0f);
                delete_button.setVisibility(VISIBLE);
                delete_button.animate().setDuration(scaleAnimationDuration).alpha(1);
                play_button.setScaleX(0);
                play_button.setScaleY(0);
                play_button.setVisibility(VISIBLE);
                rec_button.animate().setDuration(scaleAnimationDurationLong).setInterpolator(new AccelerateDecelerateInterpolator()).scaleX(0).scaleY(0).withEndAction(new Runnable() {
                    @Override
                    public void run() {
                        rec_button.setVisibility(View.INVISIBLE);
                        play_button.animate().setDuration(scaleAnimationDurationLong).setInterpolator(new OvershootInterpolator()).scaleX(1).scaleY(1);
                    }
                });
            }
        });
    }

    private boolean checkDuration(String time) {
        /*
            String string = "004-034556";
            String[] parts = string.split("-");
            String part1 = parts[0]; // 004
            String part2 = parts[1]; // 034556
        */

        String[] time_units = time.split(":");
        int seconds;

        //This is an attempt to handle the exception an user sent us through ANR with his XIAOMI RED MI NOTE 4.
        try {
            seconds = Integer.parseInt(time_units[1]);

        } catch(ArrayIndexOutOfBoundsException e)
        {
            seconds = 0;
        }

        if (seconds < 1) {
            Toast.makeText(context, "Minimum not reached!", Toast.LENGTH_SHORT).show();
            UtilityRecordings.deleteRecording(context, selected_word + ".aac");
            return false;
        }
        return true;
    }

    private void startTutorialPlayActivity(Button multibutton, Button play_original_button, ImageButton accent_button, ImageButton slow_button, ImageButton tts_settings_button) {
        ShowcaseConfig config = new ShowcaseConfig();
        config.setDelay(50); // 50ms between each showcase views
        config.setShapePadding(15);
        config.setRenderOverNavigationBar(true);

        MaterialShowcaseSequence sequence;
        sequence = new MaterialShowcaseSequence(this, MainActivity.id_showcase_playactivity);
        sequence.setConfig(config);

        sequence.addSequenceItem(new MaterialShowcaseView.Builder(this)
                .setTarget(multibutton)
                .setDismissText(getString(R.string.showcase_str_btn_1))
                .setContentText(getString(R.string.showcase_str_1))
                .setDismissOnTouch(true)
                .build()
        );
        sequence.addSequenceItem(new MaterialShowcaseView.Builder(this)
                .setTarget(play_original_button)
                .setDismissText(getString(R.string.showcase_str_btn_2))
                .setContentText(getString(R.string.showcase_str_2))
                .setDismissOnTouch(true)
                .build()
        );
        sequence.addSequenceItem(new MaterialShowcaseView.Builder(this)
                .setTarget(accent_button)
                .setDismissText(getString(R.string.showcase_str_btn_3))
                .setContentText(getString(R.string.showcase_str_3))
                .setDismissOnTouch(true)
                .build()
        );
        sequence.addSequenceItem(new MaterialShowcaseView.Builder(this)
                .setTarget(slow_button)
                .setDismissText(getString(R.string.showcase_str_btn_4))
                .setContentText(getString(R.string.showcase_str_4))
                .setDismissOnTouch(true)
                .build()
        );
        sequence.addSequenceItem(new MaterialShowcaseView.Builder(this)
                .setTarget(tts_settings_button)
                .setDismissText(getString(R.string.showcase_str_btn_5))
                .setContentText(getString(R.string.showcase_str_6))
                .setDismissOnTouch(true)
                .build()
        );
        sequence.start();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case RequestPermissionCode:
                if (grantResults.length > 0) {
                    boolean RecordPermission = grantResults[0] ==
                            PackageManager.PERMISSION_GRANTED;

                    if (RecordPermission) {
                        Toast.makeText(this, "Permission Granted", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(this, "Permission Denied", Toast.LENGTH_LONG).show();
                    }
                }
                break;
        }
    }

    private void requestNewInterstitial() {
        AdRequest adRequest = new AdRequest.Builder().addTestDevice(getString(R.string.test_device_oneplus_3)).addTestDevice(getString(R.string.test_device_honor_6)).addTestDevice(getString(R.string.test_device_htc_one_m8)).build();
        mInterstitialAd.loadAd(adRequest);
    }

    private void initTTS(Context context) {
        american_speaker_google = new TextToSpeech(context, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int i) {
                if (i == TextToSpeech.SUCCESS) {
                    american_speaker_google.setPitch(0.90f);
                    american_speaker_google.setSpeechRate(0.90f);
                    american_speaker_google.setVoice(MainActivity.voice_american_female);
                    american_speaker_google.speak("", QUEUE_ADD, null, null);
                } else {
                    if (MainActivity.isLoggingEnabled)
                        Log.e("error", "Initialization Failed!");
                }
            }
        }, MainActivity.google_tts);

        british_speaker_google = new TextToSpeech(context, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int i) {
                if (i == TextToSpeech.SUCCESS) {
                    british_speaker_google.setPitch(0.90f);
                    british_speaker_google.setSpeechRate(0.90f);
                    british_speaker_google.setVoice(MainActivity.voice_british_female);
                    british_speaker_google.speak("", QUEUE_ADD, null, null);
                } else {
                    if (MainActivity.isLoggingEnabled)
                        Log.e("error", "Initialization Failed!");
                }
            }
        }, MainActivity.google_tts);
    }

    private boolean isVolumeMuted() {
        int currentVolume = audio.getStreamVolume(AudioManager.STREAM_MUSIC);
        if (currentVolume == 0) return true;
        else return false;
    }
}