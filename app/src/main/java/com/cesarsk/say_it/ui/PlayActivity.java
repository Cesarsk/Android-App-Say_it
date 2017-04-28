package com.cesarsk.say_it.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.graphics.drawable.TransitionDrawable;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.SystemClock;
import android.os.Vibrator;
import android.speech.tts.TextToSpeech;
import android.speech.tts.Voice;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.util.Pair;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
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
import com.cesarsk.say_it.utility.ShowTimer;
import com.cesarsk.say_it.utility.UtilityRecordings;
import com.cesarsk.say_it.utility.Utility;
import com.cesarsk.say_it.utility.UtilitySharedPrefs;
import com.cesarsk.say_it.utility.utility_aidl.IabHelper;
import com.cesarsk.say_it.utility.utility_aidl.IabResult;
import com.cesarsk.say_it.utility.utility_aidl.Inventory;
import com.cesarsk.say_it.utility.utility_aidl.Purchase;
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
import java.util.Locale;

import uk.co.deanwild.materialshowcaseview.MaterialShowcaseSequence;
import uk.co.deanwild.materialshowcaseview.MaterialShowcaseView;
import uk.co.deanwild.materialshowcaseview.ShowcaseConfig;

import static android.speech.tts.TextToSpeech.QUEUE_FLUSH;
import static android.speech.tts.Voice.LATENCY_VERY_LOW;
import static android.speech.tts.Voice.QUALITY_VERY_HIGH;
import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;
import static com.cesarsk.say_it.utility.LCSecurity.base64EncodedPublicKey;

public class PlayActivity extends AppCompatActivity {
    public final static String PLAY_WORD = "com.example.cesarsk.say_it.WORD";
    public final static String PLAY_IPA = "com.example.cesarsk.say_it.IPA";
    public static final long UNDO_TIMEOUT = 3000;
    private static final String AUDIO_RECORDER_FILE_EXT_AAC = ".aac";
    private static final String AUDIO_RECORDER_FOLDER = "Say it";
    private MediaRecorder recorder = null;
    private ShowTimer timer;
    private int currentFormat = 0;
    private int output_formats[] = {MediaRecorder.OutputFormat.DEFAULT};
    private String file_exts[] = {AUDIO_RECORDER_FILE_EXT_AAC};
    public static final int RequestPermissionCode = 1;
    MediaPlayer mediaPlayer;
    int N = 10;
    private CharSequence[] history;
    int testa = 0;
    public static String selected_word;
    public static String selected_ipa;
    private boolean slow_mode = false;
    private boolean accent_flag = false;
    private boolean favorite_flag = false;
    Context context = this;
    final int durationMillis = 500;
    AlphaAnimation delete_button_anim, delete_button_anim_reverse;
    Snackbar snackbar;
    Handler handler = new Handler();
    Runnable pendingRemovalRunnable;
    byte[] temp_recording_bytes;
    private boolean isRecording = false;
    CountDownTimer countDownTimer;
    CountDownTimer minDurationTimer;
    private boolean isMinimumDurationReached = false;
    Button rec_button;
    Vibrator vibrator;
    TransitionDrawable green_animation;
    private long scaleAnimationDuration = 200;
    private long scaleAnimationDurationLong = 300;
    boolean maxDurationReached = false;
    IabHelper mHelper;
    IabHelper.QueryInventoryFinishedListener mQueryFinishedListener;
    IabHelper.OnIabPurchaseFinishedListener mIabPurchaseFinishedListener;
    public static String no_ads_in_app = "no_ads";
    private InterstitialAd mInterstitialAd;
    //private boolean hasInterstitialDisplayed = false;

    //Definizione variabile TTS
    private TextToSpeech tts_speaker;
    public static TextToSpeech american_speaker_google;
    public static TextToSpeech british_speaker_google;
    public static Voice voice_american_female = new Voice("American Language", Locale.US, QUALITY_VERY_HIGH, LATENCY_VERY_LOW, false, null);
    public static Voice voice_british_female = new Voice("British Language", Locale.UK, QUALITY_VERY_HIGH, LATENCY_VERY_LOW, false, null);


    @Override
    public void onDestroy() {
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
        super.onBackPressed();
        if (mInterstitialAd != null) {
            if (mInterstitialAd.isLoaded()) {
                mInterstitialAd.show();
                //Do not launch this thread because ADMob should automatically load ADs every X minutes.
                /*hasInterstitialDisplayed = true;
                new Handler().postDelayed(  new Runnable() {
                    @Override
                    public void run() {
                        hasInterstitialDisplayed = false;
                    }
                }, 45000);*/
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);

        // compute your public key and store it in base64EncodedPublicKey
        mHelper = new IabHelper(this, base64EncodedPublicKey);
        mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
            public void onIabSetupFinished(IabResult result) {
                if (!result.isSuccess()) {
                    // Oh no, there was a problem.
                    if(MainActivity.isLoggingEnabled) Log.d("Say It!", "Problem setting up In-app Billing: " + result);
                }
                // Hooray, IAB is fully set up!
                if(MainActivity.isLoggingEnabled) Log.d("Say It!", "Hooray. IAB is fully set up!" + result);
            }
        });

        Intent intent = getIntent();
        Bundle args = intent.getExtras();

        if ((MainActivity.american_speaker_google != null || british_speaker_google != null)) {
            //bind TTs
            american_speaker_google = MainActivity.american_speaker_google;
            british_speaker_google = MainActivity.british_speaker_google;
        } else {
            //init TTSs
            american_speaker_google = initTTS(this, true);
            british_speaker_google = initTTS(this, false);
        }

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
        //final TextView timerTextView = (TextView) findViewById(R.id.recordingTimer);
        vibrator = (Vibrator) this.getSystemService(Context.VIBRATOR_SERVICE);
        selected_word = args.getString(PLAY_WORD);
        selected_ipa = args.getString(PLAY_IPA);

        UtilitySharedPrefs.loadAdsStatus(this);
        if (!MainActivity.NO_ADS) {

            mInterstitialAd = new InterstitialAd(this);
            mInterstitialAd.setAdUnitId(getString(R.string.ad_unit_id_interstitial_playactivity_back));
            mInterstitialAd.setAdListener(new AdListener() {
                @Override
                public void onAdClosed() {
                    requestNewInterstitial();
                }
            });

            requestNewInterstitial();
        } else if (MainActivity.NO_ADS) {
            remove_ad.setVisibility(View.GONE);
        }

        //Setting Up Chronometer
        final Chronometer chronometer = (Chronometer) findViewById(R.id.recording_timer);
        chronometer.setBase(SystemClock.elapsedRealtime());

        //Setting Up Recorder/Player
        recorder = new MediaRecorder();
        recorder.setOnInfoListener(new MediaRecorder.OnInfoListener() {
            @Override
            public void onInfo(MediaRecorder mediaRecorder, int what, int extra) {

                if (what == MediaRecorder.MEDIA_RECORDER_INFO_MAX_DURATION_REACHED) {
                    maxDurationReached = true;
                    vibrator.vibrate(100);
                    rec_button.setBackground(getDrawable(R.drawable.circle_red));
                    chronometer.stop();

                    UtilityRecordings.stopRecording(context, recorder, selected_word);

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
                        UtilityRecordings.deleteRecording(context, selected_word + ".aac");
                    }
                    Toast.makeText(context, "Maximum duration reached!", Toast.LENGTH_SHORT).show();
                }

            }
        });
        mediaPlayer = new MediaPlayer();

        UtilitySharedPrefs.loadSettingsPrefs(this); //Caricamento dei Settings prima di controllare il DEFAULT_ACCENT (Arresto Anomalo)
        if (MainActivity.DEFAULT_ACCENT.equals("0")) {
            accent_button.setColorFilter(ContextCompat.getColor(this, R.color.primary_light));
            accent_flag = false;
        } else if (MainActivity.DEFAULT_ACCENT.equals("1")) {
            accent_button.setColorFilter(ContextCompat.getColor(this, R.color.Yellow600));
            accent_flag = true;
        }

        //Setting Up Record/Play Buttons
        rec_button.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (UtilityRecordings.checkRecordAudioPermissions(view.getContext())) {
                    switch (motionEvent.getAction()) {

                        case MotionEvent.ACTION_DOWN:
                            vibrator.vibrate(100);
                            rec_button.setBackground(getDrawable(R.drawable.circle_red_pressed));
                            //Scale animation
                            rec_button.animate().setDuration(scaleAnimationDuration).setInterpolator(new OvershootInterpolator()).scaleX(0.8f).scaleY(0.8f);
                            chronometer.setBase(SystemClock.elapsedRealtime());
                            chronometer.start();
                            UtilityRecordings.startRecording(context, recorder, selected_word);
                            return true;

                        case MotionEvent.ACTION_UP:
                            if (!maxDurationReached) {
                                vibrator.vibrate(100);
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

        //Gestione Snackbar + UNDO
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

        delete_button_anim = new AlphaAnimation(1.0f, 0.0f);
        delete_button_anim_reverse = new AlphaAnimation(0.0f, 1.0f);
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
                    return;
                } else if (info.getSku().equals(no_ads_in_app)) {
                    UtilitySharedPrefs.loadAdsStatus(PlayActivity.this);
                    UtilitySharedPrefs.savePrefs(PlayActivity.this, true, MainActivity.NO_ADS_STATUS_KEY);
                }
            }
        };

        mQueryFinishedListener = new IabHelper.QueryInventoryFinishedListener() {
            public void onQueryInventoryFinished(IabResult result, Inventory inventory) {
                if (result.isFailure()) {
                    Toast.makeText(PlayActivity.this, "Query Failed!", Toast.LENGTH_SHORT).show();
                    return;
                }

                //Open Purchase Dialog
                try {
                    mHelper.flagEndAsync();
                    mHelper.launchPurchaseFlow(PlayActivity.this, no_ads_in_app, 64000, mIabPurchaseFinishedListener);
                } catch (IabHelper.IabAsyncInProgressException e) {
                    e.printStackTrace();
                }
                // update the UI
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

        //Gestione AD (TEST AD)
        UtilitySharedPrefs.loadAdsStatus(this);
        AdView mAdView = (AdView) findViewById(R.id.adView);
        if (MainActivity.NO_ADS) {
            mAdView.setVisibility(View.GONE);
        } else {
            MobileAds.initialize(getApplicationContext(), getResources().getString(R.string.ad_unit_id_test));
            AdRequest adRequest = new AdRequest.Builder().addTestDevice(getString(R.string.test_device_oneplus_3)).addTestDevice(getString(R.string.test_device_honor_6)).addTestDevice(getString(R.string.test_device_htc_one_m8)).build();
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
            favorite_button.setColorFilter(getResources().getColor(R.color.RudolphsNose));

        favorite_button.setOnClickListener(new View.OnClickListener()

        {
            @Override
            public void onClick(View v) {
                if (!favorite_flag) {
                    UtilitySharedPrefs.addFavs(v.getContext(), new Pair<>(selected_word, selected_ipa));
                    favorite_flag = !favorite_flag;
                    Toast.makeText(PlayActivity.this, "Added to favorites!", Toast.LENGTH_SHORT).show();
                    favorite_button.setColorFilter(getResources().getColor(R.color.RudolphsNose));
                } else {
                    favorite_button.setColorFilter(getResources().getColor(R.color.primary_light));
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
                    MainActivity.american_speaker_google.setSpeechRate((float) 0.30);
                    MainActivity.british_speaker_google.setSpeechRate((float) 0.30);
                    slow_mode = !slow_mode;
                    Toast.makeText(PlayActivity.this, "Slow Mode Activated", Toast.LENGTH_SHORT).show();
                    slow_button.setColorFilter(getResources().getColor(R.color.Yellow600));
                } else {
                    MainActivity.american_speaker_google.setSpeechRate((float) 0.90);
                    MainActivity.british_speaker_google.setSpeechRate((float) 0.90);
                    Toast.makeText(PlayActivity.this, "Slow Mode Deactivated", Toast.LENGTH_SHORT).show();
                    slow_button.setColorFilter(getResources().getColor(R.color.primary_light));
                    slow_mode = !slow_mode;
                }
            }
        });

        accent_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!accent_flag) {
                    accent_button.setColorFilter(getResources().getColor(R.color.Yellow600));
                    Toast.makeText(PlayActivity.this, "British Accent selected", Toast.LENGTH_SHORT).show();
                    accent_flag = !accent_flag;
                } else {
                    accent_button.setColorFilter(getResources().getColor(R.color.primary_light));
                    Toast.makeText(PlayActivity.this, "American English selected", Toast.LENGTH_SHORT).show();
                    accent_flag = !accent_flag;
                }
            }

        });

        play_original_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!accent_flag) {
                    american_speaker_google.speak(selected_word, QUEUE_FLUSH, null, null);
                    vibrator.vibrate(100);
                } else if (accent_flag) {
                    british_speaker_google.speak(selected_word, QUEUE_FLUSH, null, null);
                    vibrator.vibrate(100);
                }
            }
        });

        startTutorialPlayActivity(rec_button, play_original_button, accent_button, slow_button);
    }

    private void setupSnackbar(final Chronometer chronometer, final ImageButton delete_button, final Button play_button) {
        snackbar = Snackbar.make(findViewById(R.id.play_activity_coordinator), "Deleted Recording", (int) UNDO_TIMEOUT);
        snackbar.setAction("UNDO", new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                File recovered_file = new File(view.getContext().getFilesDir().getAbsolutePath() + "/" + selected_word + ".aac");
                FileOutputStream outputStream = null;
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

        String[] time_units = time.split(":");
        int seconds = Integer.parseInt(time_units[1]);
        if (seconds < 1) {
            Toast.makeText(context, "Minimum not reached!", Toast.LENGTH_SHORT).show();
            UtilityRecordings.deleteRecording(context, selected_word + ".aac");
            return false;
        }
        return true;
    }

    private void startTutorialPlayActivity(Button multibutton, Button play_original_button, ImageButton accent_button, ImageButton slow_button) {
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
        sequence.start();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
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

    private TextToSpeech initTTS(Context context, final boolean accent) {
        TextToSpeech.OnInitListener onInitListener = null;
        tts_speaker = new TextToSpeech(context, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                tts_speaker.setPitch((float) 0.90);
                tts_speaker.setSpeechRate((float) 0.90);
                if (accent) tts_speaker.setVoice(MainActivity.voice_american_female);
                else if (!accent) tts_speaker.setVoice(MainActivity.voice_british_female);
            }
        });

        return tts_speaker;
    }

}