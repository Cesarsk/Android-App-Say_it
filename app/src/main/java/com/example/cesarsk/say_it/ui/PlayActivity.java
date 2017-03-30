package com.example.cesarsk.say_it.ui;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.graphics.drawable.TransitionDrawable;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Handler;
import android.os.Vibrator;
import android.support.design.widget.Snackbar;
import android.support.v4.util.Pair;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cesarsk.say_it.R;
import com.example.cesarsk.say_it.utility.ShowTimer;
import com.example.cesarsk.say_it.utility.Utility;
import com.example.cesarsk.say_it.utility.UtilityRecord;
import com.example.cesarsk.say_it.utility.UtilitySharedPrefs;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

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
        final ImageButton search_meaning = (ImageButton) findViewById(R.id.search_meaning_button);
        final TextView timerTextView = (TextView) findViewById(R.id.recordingTimer);
        final Vibrator vibrator = (Vibrator) this.getSystemService(Context.VIBRATOR_SERVICE);
        selected_word = args.getString(PLAY_WORD);
        selected_ipa = args.getString(PLAY_IPA);


        if(MainActivity.DEFAULT_ACCENT.equals("0")){
            accent_button.setColorFilter(getResources().getColor(R.color.primary_light));
            accent_flag = false;
        }
        else if(MainActivity.DEFAULT_ACCENT.equals("1")){
            accent_button.setColorFilter(getResources().getColor(R.color.Yellow600));
            accent_flag = true;
        }

        timer = new ShowTimer(timerTextView);
        recorder = new MediaRecorder();
        mediaPlayer = new MediaPlayer();
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

        final View.OnClickListener play_listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mediaPlayer.isPlaying()) {
                    UtilityRecord.playRecording(mediaPlayer);
                    recplay_button.setBackground(getDrawable(R.drawable.circle_green_pressed));
                    vibrator.vibrate(50);
                    Log.i("SAY IT!", "" + mediaPlayer.getDuration());
                    new CountDownTimer(mediaPlayer.getDuration(), 1000) {

                        @Override
                        public void onTick(long millisUntilFinished) {

                        }

                        @Override
                        public void onFinish() {
                            recplay_button.setBackground(getDrawable(R.drawable.circle_green));
                        }
                    }.start();
                }
            }
        };

        final View.OnTouchListener rec_listener = new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                if (UtilityRecord.checkRecordAudioPermissions(view.getContext())) {
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            Log.i("Say it!", "Start Recording");
                            isMinimumDurationReached = false;
                            minDurationTimer.start();
                            isRecording = true;
                            vibrator.vibrate(50);
                            recplay_button.setBackground(getDrawable(R.drawable.circle_red_pressed));
                            timer.startTimer();
                            UtilityRecord.startRecording(recorder, output_formats, currentFormat, file_exts);
                            if (countDownTimer != null) {
                                countDownTimer.cancel();
                                countDownTimer.start();
                            }
                            return true;

                        case MotionEvent.ACTION_UP:
                            Log.i("Say it!", "Stop Recording");
                            timer.stopTimer();
                            if (countDownTimer != null) {
                                countDownTimer.cancel();
                            }
                            isRecording = false;
                            recplay_button.setBackground(getDrawable(R.drawable.circle_red));
                            if (isMinimumDurationReached) {
                                if (UtilityRecord.stopRecording(context, recorder, selected_word)) {
                                    recplay_button.setBackground(getDrawable(R.drawable.circle_color_anim_red_to_green));
                                    delete_button.startAnimation(delete_button_anim_reverse);
                                    recplay_button.setOnTouchListener(null);
                                    recplay_button.setOnClickListener(play_listener);
                                    TransitionDrawable transition = (TransitionDrawable) recplay_button.getBackground();
                                    transition.startTransition(durationMillis);
                                    isMinimumDurationReached = false;
                                    return true;
                                }
                            } else {
                                vibrator.vibrate(50);
                                Toast.makeText(context, "Minimum duration not reached.", Toast.LENGTH_SHORT).show();
                                timer.clearTimer();
                                UtilityRecord.deleteRecording(context, selected_word);
                                return true;
                            }
                    }
                } else {
                    UtilityRecord.requestRecordAudioPermissions(view.getContext());
                }
                return false;
            }
        };

        minDurationTimer = new CountDownTimer(1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {
                isMinimumDurationReached = true;
            }
        };

        countDownTimer = new CountDownTimer(10000, 1000) {
            public void onTick(long millisUntilFinished) {
                //mTextField.setText("seconds remaining: " + millisUntilFinished / 1000);
            }

            public void onFinish() {
                if (isRecording) {
                    timer.stopTimer();
                    Toast.makeText(context, "Maximum length duration reached.", Toast.LENGTH_SHORT).show();
                    vibrator.vibrate(50);
                    recplay_button.setBackground(getDrawable(R.drawable.circle_red));
                    if (UtilityRecord.stopRecording(context, recorder, selected_word)) {
                        recplay_button.setBackground(getDrawable(R.drawable.circle_color_anim_red_to_green));
                        delete_button.startAnimation(delete_button_anim_reverse);
                        recplay_button.setOnTouchListener(null);
                        recplay_button.setOnClickListener(play_listener);
                        TransitionDrawable transition = (TransitionDrawable) recplay_button.getBackground();
                        transition.startTransition(durationMillis);
                        vibrator.vibrate(50);
                        return;
                    }
                    return;
                }
            }
        };

        if (UtilityRecord.checkRecordingFile(selected_word)) {
            recplay_button.setBackground(getResources().getDrawable(R.drawable.circle_color_anim_green_to_red, null));
            recplay_button.setOnClickListener(play_listener);
            int millis = UtilityRecord.returnRecordingDuration(mediaPlayer);
            SimpleDateFormat formatter = new SimpleDateFormat("ss:SSS", Locale.UK);
            Date date = new Date(millis);
            String result = formatter.format(date);
            timerTextView.setText(result);
            delete_button.startAnimation(delete_button_anim_reverse);
        } else {
            recplay_button.setBackground(getResources().getDrawable(R.drawable.circle_red, null));
            recplay_button.setOnTouchListener(rec_listener);
            delete_button.setEnabled(false);
            delete_button.setVisibility(INVISIBLE);
        }

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

        //Gestione Snackbar + UNDO
        snackbar = Snackbar.make(findViewById(R.id.play_activity_coordinator), "Deleted Recording", (int) UNDO_TIMEOUT);
        final Context context = this;

        snackbar.setAction("UNDO", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //handler.removeCallbacks(pendingRemovalRunnable);
                File recovered_file = new File(Environment.getExternalStorageDirectory().getPath() + "/" + UtilityRecord.AUDIO_RECORDER_FOLDER + "/" + selected_word + ".aac");
                FileOutputStream outputStream = null;
                try {
                    outputStream = new FileOutputStream(recovered_file);
                    outputStream.write(temp_recording_bytes);
                    outputStream.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                UtilitySharedPrefs.addRecording(context, recovered_file.getAbsolutePath());
                timer.setTimer(timer.getOld_time());
                delete_button.startAnimation(delete_button_anim_reverse);
                recplay_button.setOnTouchListener(null);
                recplay_button.setOnClickListener(play_listener);
                recplay_button.setBackground(getDrawable(R.drawable.circle_color_anim_red_to_green));
                TransitionDrawable transition = (TransitionDrawable) recplay_button.getBackground();
                transition.startTransition(durationMillis);
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
            public void onClick(final View v) {
                timer.clearTimer();
                delete_button.startAnimation(delete_button_anim);
                recplay_button.setOnTouchListener(rec_listener);
                recplay_button.setBackground(getDrawable(R.drawable.circle_color_anim_green_to_red));
                TransitionDrawable transition = (TransitionDrawable) recplay_button.getBackground();
                transition.startTransition(durationMillis);
                temp_recording_bytes = UtilityRecord.getRecordingfromWord(context, selected_word);
                UtilityRecord.deleteRecording(context, selected_word);
                snackbar.show();
                //handler.post(pendingRemovalRunnable);
                //Toast.makeText(PlayActivity.this, "Deleted Recording", Toast.LENGTH_SHORT).show();

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
                }
                else if (accent_flag)
                    MainActivity.british_speaker_google.speak(selected_word, QUEUE_FLUSH, null, null);
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

    public void animateRecordButton(ImageButton delete_button, Button recplay_button, View.OnTouchListener rec_listener, boolean green_to_red) {

        delete_button.startAnimation(delete_button_anim);
        recplay_button.setOnTouchListener(rec_listener);

        if (green_to_red) {
            recplay_button.setBackground(getDrawable(R.drawable.circle_color_anim_green_to_red));
        } else {
            recplay_button.setBackground(getDrawable(R.drawable.circle_color_anim_red_to_green));
        }

        TransitionDrawable transition = (TransitionDrawable) recplay_button.getBackground();
        transition.startTransition(durationMillis);
    }
}