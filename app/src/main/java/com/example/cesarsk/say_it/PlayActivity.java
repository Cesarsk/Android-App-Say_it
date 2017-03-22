package com.example.cesarsk.say_it;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.TransitionDrawable;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v4.content.ContextCompat;
import android.support.v4.util.Pair;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import static android.speech.tts.TextToSpeech.QUEUE_ADD;
import static android.speech.tts.TextToSpeech.QUEUE_FLUSH;
import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;
import static com.example.cesarsk.say_it.MainActivity.american_speaker_google;
import static com.example.cesarsk.say_it.MainActivity.british_speaker_google;
import static com.example.cesarsk.say_it.R.id.textView;

public class PlayActivity extends AppCompatActivity {

    public final static String PLAY_WORD = "com.example.cesarsk.say_it.WORD";
    public final static String PLAY_IPA = "com.example.cesarsk.say_it.IPA";
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);

        Intent intent = getIntent();
        Bundle args = intent.getExtras();

        final Button recplay_button = (Button) findViewById(R.id.recplay_button);
        final TextView selected_word_view = (TextView) findViewById(R.id.selected_word);
        final TextView selected_ipa_view = (TextView) findViewById(R.id.selected_word_ipa);
        final ImageButton delete_button = (ImageButton) findViewById(R.id.delete_button);
        final ImageButton favorite_button = (ImageButton) findViewById(R.id.favorite_button);
        final ImageButton slow_button = (ImageButton) findViewById(R.id.slow_button);
        final ImageButton accent_button = (ImageButton) findViewById(R.id.accent_button);
        final Button play_original_button = (Button) findViewById(R.id.play_original);
        final ImageButton your_recordings = (ImageButton) findViewById(R.id.recordings_button);
        final ImageButton remove_ad = (ImageButton) findViewById(R.id.remove_ads_button);
        final TextView timerTextView = (TextView) findViewById(R.id.recordingTimer);
        selected_word = args.getString(PLAY_WORD);
        selected_ipa = args.getString(PLAY_IPA);

        timer = new ShowTimer(timerTextView);
        recorder = new MediaRecorder();
        mediaPlayer = new MediaPlayer();
        history = new CharSequence[N];

        Typeface plainItalic = Typeface.createFromAsset(getAssets(), "fonts/GentiumPlus-I.ttf");
        Typeface plainRegular = Typeface.createFromAsset(getAssets(), "fonts/GentiumPlus-R.ttf");
        selected_word_view.setTypeface(plainRegular);
        selected_ipa_view.setTypeface(plainItalic);
        selected_word_view.setText(selected_word);
        selected_ipa_view.setText(selected_ipa);

        final View.OnClickListener play_listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mediaPlayer.isPlaying()) Utility.playRecording(mediaPlayer);
            }
        };

        final View.OnTouchListener rec_listener = new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                if (Utility.checkPermission(view.getContext())) {
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            Log.i("Say it!", "Start Recording");
                            timer.StartTimer();
                            Utility.startRecording(recorder, output_formats, currentFormat, file_exts);
                            break;

                        case MotionEvent.ACTION_UP:
                            Log.i("Say it!", "Stop Recording");
                            timer.StopTimer();
                            Utility.stopRecording(recorder);
                            if (timer.GetDuration() > 5000) {
                                recplay_button.setBackground(getDrawable(R.drawable.coloranimreverse));
                                delete_button.setVisibility(VISIBLE);
                                delete_button.setEnabled(true);
                                recplay_button.setOnTouchListener(null);
                                recplay_button.setOnClickListener(play_listener);
                                TransitionDrawable transition = (TransitionDrawable) recplay_button.getBackground();
                                transition.reverseTransition(durationMillis);
                                return true;
                            } else return false;
                    }
                } else {
                    Utility.requestPermission(view.getContext());
                }
                return false;
            }
        };


        if (Utility.checkFile(selected_word)) {
            recplay_button.setBackground(getResources().getDrawable(R.drawable.coloranim, null));
            recplay_button.setOnClickListener(play_listener);
            int millis = Utility.returnDurationRecording(mediaPlayer);
            SimpleDateFormat formatter = new SimpleDateFormat("mm:ss:SSS", Locale.UK);
            Date date = new Date(millis);
            String result = formatter.format(date);
            timerTextView.setText(result);

            delete_button.setEnabled(true);
            delete_button.setVisibility(VISIBLE);
        } else {
            recplay_button.setBackground(getResources().getDrawable(R.drawable.coloranimreverse, null));
            recplay_button.setOnTouchListener(rec_listener);
            delete_button.setEnabled(false);
            delete_button.setVisibility(INVISIBLE);
        }

        remove_ad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        //Gestione AD (TEST AD)
        MobileAds.initialize(getApplicationContext(), "ca-app-pub-3940256099942544/6300978111");
        AdView mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

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

        delete_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utility.deleteRecording(v.getContext(), selected_word);
                timer.ClearTimer();
                //TODO CAMBIO ICONA CESTINO VUOTO CESTINO PIENO
                Toast.makeText(PlayActivity.this, "Deleted Recording", Toast.LENGTH_SHORT).show();
                delete_button.setEnabled(false);
                delete_button.setVisibility(INVISIBLE);
                recplay_button.setOnTouchListener(rec_listener);
                TransitionDrawable transition = (TransitionDrawable) recplay_button.getBackground();
                transition.reverseTransition(durationMillis);
            }
        });

        favorite_flag = Utility.checkFavs(this, selected_word);
        if (favorite_flag)
            favorite_button.setColorFilter(getResources().getColor(R.color.RudolphsNose));

        favorite_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!favorite_flag) {
                    Utility.addFavs(v.getContext(), new Pair<>(selected_word, selected_ipa));
                    favorite_flag = !favorite_flag;
                    Toast.makeText(PlayActivity.this, "Added to favorites!", Toast.LENGTH_SHORT).show();
                    favorite_button.setColorFilter(getResources().getColor(R.color.RudolphsNose));
                } else {
                    favorite_button.setColorFilter(getResources().getColor(R.color.primary_light));
                    Toast.makeText(PlayActivity.this, "Removed from favorites!", Toast.LENGTH_SHORT).show();
                    Utility.removeFavs(v.getContext(), new Pair<>(selected_word, selected_ipa));
                    favorite_flag = !favorite_flag;
                }
            }
        });

        slow_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!slow_mode) {
                    american_speaker_google.setSpeechRate((float) 0.30);
                    british_speaker_google.setSpeechRate((float) 0.30);
                    slow_mode = !slow_mode;
                    Toast.makeText(PlayActivity.this, "Slow Mode Activated", Toast.LENGTH_SHORT).show();
                    slow_button.setColorFilter(getResources().getColor(R.color.Yellow600));
                } else {
                    american_speaker_google.setSpeechRate((float) 0.90);
                    british_speaker_google.setSpeechRate((float) 0.90);
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
                    // american_speaker_google.setVoice(voice_british_female);
                    accent_button.setColorFilter(getResources().getColor(R.color.Yellow600));
                    Toast.makeText(PlayActivity.this, "British Accent selected", Toast.LENGTH_SHORT).show();
                    accent_flag = !accent_flag;
                } else {
                    // american_speaker_google.setVoice(voice_american_female);
                    accent_button.setColorFilter(getResources().getColor(R.color.primary_light));
                    Toast.makeText(PlayActivity.this, "American English selected", Toast.LENGTH_SHORT).show();
                    accent_flag = !accent_flag;
                }
            }
        });

        play_original_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!accent_flag)
                    american_speaker_google.speak(selected_word, QUEUE_FLUSH, null, null);
                else british_speaker_google.speak(selected_word, QUEUE_FLUSH, null, null);
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case RequestPermissionCode:
                if (grantResults.length > 0) {
                    boolean StoragePermission = grantResults[0] ==
                            PackageManager.PERMISSION_GRANTED;
                    boolean RecordPermission = grantResults[1] ==
                            PackageManager.PERMISSION_GRANTED;

                    if (StoragePermission && RecordPermission) {
                        Toast.makeText(this, "Permission Granted", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(this, "Permission Denied", Toast.LENGTH_LONG).show();
                    }
                }
                break;
        }
    }
}