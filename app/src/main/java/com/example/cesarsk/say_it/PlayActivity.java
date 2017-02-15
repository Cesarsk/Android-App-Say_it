package com.example.cesarsk.say_it;

import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import java.io.File;
import java.io.IOException;

import static android.Manifest.permission.RECORD_AUDIO;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.speech.tts.TextToSpeech.QUEUE_ADD;
import static com.example.cesarsk.say_it.MainActivity.tts;
import static com.example.cesarsk.say_it.MainActivity.voice_american_female;
import static com.example.cesarsk.say_it.MainActivity.voice_british_female;
import static com.example.cesarsk.say_it.ResultsListCustomAdapter.selected_word_charseq;

public class PlayActivity extends AppCompatActivity {

    private static final String AUDIO_RECORDER_FILE_EXT_AAC = ".aac";
    private static final String AUDIO_RECORDER_FOLDER = "Say it";
    private MediaRecorder recorder = null;
    private int currentFormat = 0;
    private int output_formats[] = {MediaRecorder.OutputFormat.DEFAULT};
    private String file_exts[] = {AUDIO_RECORDER_FILE_EXT_AAC};
    public static final int RequestPermissionCode = 1;
    MediaPlayer mediaPlayer;
    int N = 10;
    private CharSequence[] history;
    int testa = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);

        //TODO SISTEMARE FRAGMENT
        recorder = new MediaRecorder();
        mediaPlayer = new MediaPlayer();
        history = new CharSequence[N];
        //word = new String(getArguments().getCharSequence("word").toString());

        TextView selected_word = (TextView)findViewById(R.id.selected_word);
        selected_word.setText(selected_word_charseq);

        //TODO Controllare e NO DUPLICATI (AGGIUNGERE SOLO SE NON C'E')
        for(int i = 0; i < N; i++)
        {
            if(selected_word_charseq == history[i]) break;
            else
            {
                history[testa] = selected_word_charseq;
                testa = (testa+1)%N;
            }
        }

        ImageButton play_original_button = (ImageButton)findViewById(R.id.play_original);
        play_original_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tts.speak(selected_word_charseq, QUEUE_ADD, null, null);
            }
        });

        ImageButton play_rec_button = (ImageButton)findViewById(R.id.play_rec);
        play_rec_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playRecording();
            }
        });

        ImageButton rec_button = (ImageButton)findViewById(R.id.rec_button);
        rec_button.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {

                if (checkPermission())
                {
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            Log.i("Say it!", "Start Recording");
                            startRecording();
                            break;

                        case MotionEvent.ACTION_UP:
                            Log.i("Say it!", "Stop Recording");
                            stopRecording();
                            break;
                    }
                }

                else
                {
                    requestPermission();
                }
                return false;
            }
        });


        final Switch switch_slow = (Switch)findViewById(R.id.switch_slow);
        switch_slow.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                //TODO SISTEMARE, AMERICAN SLOW MODE SHOULD BE SLOWER THAN BRITISH'S ONE
                if(isChecked) tts.setSpeechRate((float)0.40);
                else if(!isChecked) tts.setSpeechRate((float)0.90);
            }
        });

        final Switch switch_accent = (Switch)findViewById(R.id.switch_accent);
        switch_accent.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) tts.setVoice(voice_british_female);
                else if(!isChecked) tts.setVoice(voice_american_female);
            }
        });
    }





    //FUNZIONI DI REGISTRAZIONE
    private String getFilename() {
        String filepath = Environment.getExternalStorageDirectory().getPath();
        File file = new File(filepath, AUDIO_RECORDER_FOLDER);

        if (!file.exists()) {
            file.mkdirs();
            File nomedia = new File(Environment.getExternalStorageDirectory().getPath()+"/"+AUDIO_RECORDER_FOLDER+"/.nomedia");
            try { nomedia.createNewFile(); } catch (IOException e) {Log.i("LOG:",".nomedia not created");}
        }

        return (file.getAbsolutePath() + "/" + selected_word_charseq + file_exts[currentFormat]);
    }

    private void startRecording() {
        //TODO SE IL FILE GIA' ESISTE, CANCELLALO E REGISTRA NUOVAMENTE
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(output_formats[currentFormat]);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.HE_AAC);
        recorder.setAudioEncodingBitRate(16);
        recorder.setAudioSamplingRate(44100);
        recorder.setOutputFile(getFilename());
        recorder.setOnErrorListener(errorListener);
        recorder.setOnInfoListener(infoListener);

        try {
            recorder.prepare();
            recorder.start();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private MediaRecorder.OnErrorListener errorListener = new MediaRecorder.OnErrorListener() {
        @Override
        public void onError(MediaRecorder mr, int what, int extra) {
            Log.i("Say it!","Error: " + what + ", " + extra);
        }
    };

    private MediaRecorder.OnInfoListener infoListener = new MediaRecorder.OnInfoListener() {
        @Override
        public void onInfo(MediaRecorder mr, int what, int extra) {
            Log.i("Say it!","Warning: " + what + ", " + extra);
        }
    };

    private void stopRecording() {
        if (null != recorder) {
            recorder.stop();
            recorder.reset();
            recorder.release();

            recorder = null;
        }
    }

    private void playRecording() {
        try {
            //TODO CHECK IF RECORDING ALREADY EXISTS. IF DOES NOT, DO NOT PLAY.
            Log.i("Say it!","Playing recording: "+Environment.getExternalStorageDirectory().getPath()+"/"+AUDIO_RECORDER_FOLDER+"/"+selected_word_charseq+".aac");
            mediaPlayer.setDataSource(Environment.getExternalStorageDirectory().getPath()+"/"+AUDIO_RECORDER_FOLDER+"/"+selected_word_charseq+".aac");
            mediaPlayer.prepare();
            mediaPlayer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }





    //FUNZIONI PER RICHIESTA PERMESSI
    public boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(this,
                WRITE_EXTERNAL_STORAGE);
        int result1 = ContextCompat.checkSelfPermission(this,
                RECORD_AUDIO);
        return result == PackageManager.PERMISSION_GRANTED &&
                result1 == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(this, new
                String[]{WRITE_EXTERNAL_STORAGE, RECORD_AUDIO}, RequestPermissionCode);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case RequestPermissionCode:
                if (grantResults.length> 0) {
                    boolean StoragePermission = grantResults[0] ==
                            PackageManager.PERMISSION_GRANTED;
                    boolean RecordPermission = grantResults[1] ==
                            PackageManager.PERMISSION_GRANTED;

                    if (StoragePermission && RecordPermission) {
                        Toast.makeText(this, "Permission Granted", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(this,"Permission Denied",Toast.LENGTH_LONG).show();
                    }
                }
                break;
        }
    }
}