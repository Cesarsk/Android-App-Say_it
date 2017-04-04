package com.example.cesarsk.say_it.ui;

import android.animation.Animator;
import android.animation.TimeInterpolator;
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
import android.support.design.widget.Snackbar;
import android.support.v4.util.Pair;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.BounceInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cesarsk.say_it.R;
import com.example.cesarsk.say_it.utility.ShowTimer;
import com.example.cesarsk.say_it.utility.UtilityRecordings;
import com.example.cesarsk.say_it.utility.Utility;
import com.example.cesarsk.say_it.utility.UtilitySharedPrefs;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import uk.co.deanwild.materialshowcaseview.MaterialShowcaseSequence;
import uk.co.deanwild.materialshowcaseview.ShowcaseConfig;

import static android.speech.tts.TextToSpeech.QUEUE_FLUSH;
import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);

        Intent intent = getIntent();
        Bundle args = intent.getExtras();

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

        recorder = new MediaRecorder();
        mediaPlayer = new MediaPlayer();

        if (MainActivity.DEFAULT_ACCENT.equals("0")) {
            accent_button.setColorFilter(getResources().getColor(R.color.primary_light));
            accent_flag = false;
        } else if (MainActivity.DEFAULT_ACCENT.equals("1")) {
            accent_button.setColorFilter(getResources().getColor(R.color.Yellow600));
            accent_flag = true;
        }

        //Setting Up Chronometer
        final Chronometer chronometer = (Chronometer) findViewById(R.id.recording_timer);
        chronometer.setBase(SystemClock.elapsedRealtime());

        //Setting Up Record/Play Buttons
        rec_button.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                if(UtilityRecordings.checkRecordAudioPermissions(view.getContext())) {
                    switch (motionEvent.getAction()) {
                        case MotionEvent.ACTION_DOWN:

                            vibrator.vibrate(50);
                            rec_button.setBackground(getDrawable(R.drawable.circle_red_pressed));
                            rec_button.animate().setDuration(200).setInterpolator(new OvershootInterpolator()).scaleX(0.8f).scaleY(0.8f);
                            chronometer.setBase(SystemClock.elapsedRealtime());
                            chronometer.start();
                            UtilityRecordings.startRecording(context, recorder, selected_word);
                            return true;

                        case MotionEvent.ACTION_UP:
                            vibrator.vibrate(50);
                            rec_button.setBackground(getDrawable(R.drawable.circle_red));
                            chronometer.stop();
                            UtilityRecordings.stopRecording(view.getContext(), recorder, selected_word);

                            if(checkDuration(chronometer.getText().toString())){
                                play_button.setVisibility(VISIBLE);
                                rec_button.animate().setDuration(100).scaleX(1).scaleY(1);
                                rec_button.animate().setDuration(100).scaleX(0).scaleY(0).withEndAction(new Runnable() {
                                    @Override
                                    public void run() {
                                        rec_button.setVisibility(View.INVISIBLE);
                                        play_button.animate().setDuration(300).setInterpolator(new OvershootInterpolator()).scaleX(1).scaleY(1);
                                        delete_button.setAlpha(0f);
                                        delete_button.setVisibility(VISIBLE);
                                        delete_button.animate().setDuration(300).alpha(1);
                                    }
                                });
                            }
                            else{
                                rec_button.animate().setDuration(100).scaleX(1).scaleY(1);
                                rec_button.setBackground(getDrawable(R.drawable.circle_red_pressed));
                                UtilityRecordings.deleteRecording(view.getContext(), selected_word + ".aac");
                            }
                            return false;
                    }
                }

                return false;
            }
        });

        play_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UtilityRecordings.playRecording(view.getContext(), mediaPlayer, selected_word + ".aac");
            }
        });

        if(UtilityRecordings.checkRecordingFile(this, selected_word)){
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

        history = new CharSequence[N];

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

        remove_ad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });

        search_meaning.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utility.searchMeaning(context, selected_word);
            }
        });

        //TODO
        //Gestione Snackbar + UNDO
        snackbar = Snackbar.make(findViewById(R.id.play_activity_coordinator), "Deleted Recording", (int) UNDO_TIMEOUT);
        final Context context = this;

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

                delete_button.setAlpha(0f);
                delete_button.setVisibility(VISIBLE);
                delete_button.animate().setDuration(300).alpha(1);
                play_button.setScaleX(0);
                play_button.setScaleY(0);
                play_button.setVisibility(VISIBLE);
                rec_button.animate().setDuration(100).scaleX(0).scaleY(0).withEndAction(new Runnable() {
                    @Override
                    public void run() {
                        rec_button.setVisibility(View.INVISIBLE);
                        play_button.animate().setDuration(300).setInterpolator(new OvershootInterpolator()).scaleX(1).scaleY(1);
                    }
                });

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
            public void onClick(View view) {
                delete_button.animate().setDuration(300).alpha(0).withEndAction(new Runnable() {
                    @Override
                    public void run() {
                        delete_button.setVisibility(View.GONE);
                    }
                });
                rec_button.setScaleY(0);
                rec_button.setScaleX(0);
                rec_button.setVisibility(VISIBLE);
                play_button.animate().setDuration(100).scaleX(0).scaleY(0).withEndAction(new Runnable() {
                    @Override
                    public void run() {
                        play_button.setVisibility(View.INVISIBLE);
                        rec_button.animate().setDuration(300).setInterpolator(new OvershootInterpolator()).scaleX(1).scaleY(1);
                    }
                });
                String filename = context.getFilesDir().getAbsolutePath() + "/" + selected_word + ".aac";
                File recording_file = new File(filename);
                temp_recording_bytes = UtilityRecordings.getRecordingBytesfromFile(recording_file);
                UtilityRecordings.deleteRecording(context, recording_file.getName());
                snackbar.show();
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
                    MainActivity.american_speaker_google.speak(selected_word, QUEUE_FLUSH, null, null);
                    vibrator.vibrate(50);
                } else if (accent_flag) {
                    MainActivity.british_speaker_google.speak(selected_word, QUEUE_FLUSH, null, null);
                    vibrator.vibrate(50);
                }
            }
        });

        startTutorialPlayActivity(rec_button, play_original_button, accent_button, slow_button);
    }

    private boolean checkDuration(String time){

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
        MaterialShowcaseSequence sequence = new MaterialShowcaseSequence(this, "utente");
        sequence.setConfig(config);

        sequence.addSequenceItem(multibutton,
                "HOLD this button to record a word. Release and PRESS it to listen to your recording.", "[DISMISS]");
        sequence.addSequenceItem(play_original_button,
                "PRESS this button to listen to the original pronunciation.", Utility.underlineText("[GO AWAY!]").toString());
        sequence.addSequenceItem(accent_button,
                "Want to switch accent? PRESS this one!", Utility.underlineText("[SERIOUSLY, I'VE GOT THIS!]").toString());
        sequence.addSequenceItem(slow_button,
                "Okay... this is the last one. If you want to slower the word, use the SLOW BUTTON.", Utility.underlineText("[...I will]").toString());
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

    private class SimpleTapListener extends GestureDetector.SimpleOnGestureListener{
        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            return true;
        }
    }
}