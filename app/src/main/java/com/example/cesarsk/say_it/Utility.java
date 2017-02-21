package com.example.cesarsk.say_it;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Environment;
import android.speech.tts.Voice;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Set;
import java.util.TreeSet;

import static android.Manifest.permission.RECORD_AUDIO;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.content.Context.MODE_PRIVATE;
import static android.speech.tts.TextToSpeech.QUEUE_ADD;
import static com.example.cesarsk.say_it.MainActivity.FAVORITES_PREFS_KEY;
import static com.example.cesarsk.say_it.MainActivity.HISTORY_PREFS_KEY;
import static com.example.cesarsk.say_it.MainActivity.WordList;
import static com.example.cesarsk.say_it.MainActivity.tts;
import static com.example.cesarsk.say_it.MainActivity.voice_american_female;
import static com.example.cesarsk.say_it.MainActivity.voice_british_female;
import static com.example.cesarsk.say_it.PlayActivity.RequestPermissionCode;
import static com.example.cesarsk.say_it.PlayActivity.selected_word;

/**
 * Created by cesarsk on 17/02/2017.
 */

public class Utility {

    private static final String AUDIO_RECORDER_FOLDER = "Say it";

    public static boolean delete_recordings() {
        //TODO DELETE ALL FILES IN THE FOLDER EXCEPT FOR .NOMEDIA FILE
        return true;
    }

    //Method used for BUG_REPORT and CONTACT_US Modules
    public static void shareToMail(String[] email, String subject, Context context) {
        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.putExtra(Intent.EXTRA_EMAIL, email);
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
        //emailIntent.putExtra(Intent.EXTRA_TEXT, content);
        emailIntent.setType("text/plain");
        context.startActivity(emailIntent);
    }

    //Rate-Us Module
    public static void rateUs(Context context){
        Uri uri = Uri.parse("market://details?id=" + context.getPackageName());
        Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
        // To count with Play market backstack, After pressing back button,
        // to taken back to our application, we need to add following flags to intent.
        goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                Intent.FLAG_ACTIVITY_NEW_DOCUMENT |
                Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
        try {
            context.startActivity(goToMarket);
        } catch (ActivityNotFoundException e) {
            context.startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("http://play.google.com/store/apps/details?id=" + context.getPackageName())));
        }

    }

    //Gestione Preferences
    public static void savePrefs(Context context, Set<String> set, String prefs_key)
    {
        SharedPreferences settings = context.getSharedPreferences(MainActivity.PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();

        editor.putStringSet(prefs_key, set);
        editor.apply();
    }

    public static void addFavs(Context context, String word)
    {
        Set<String> new_favs = new TreeSet<>();
        loadFavs(context);
        if(MainActivity.FAVORITES != null){
            for (String element: MainActivity.FAVORITES) {
                new_favs.add(element);
            }
        }
        new_favs.add(word);
        savePrefs(context, new_favs, MainActivity.FAVORITES_PREFS_KEY);
    }

    public static void addHist(Context context, String word)
    {
        Set<String> new_hist = new TreeSet<>();
        loadHist(context);
        if(MainActivity.HISTORY != null){
            for (String element: MainActivity.HISTORY) {
                new_hist.add(element);
            }
        }
        new_hist.add(word);
        savePrefs(context, new_hist, MainActivity.HISTORY_PREFS_KEY);
    }

    public static void loadFavs(Context context)
    {
        SharedPreferences sharedPreferences = context.getSharedPreferences(MainActivity.PREFS_NAME, MODE_PRIVATE);
        MainActivity.FAVORITES = sharedPreferences.getStringSet(FAVORITES_PREFS_KEY, new TreeSet<String>());
    }

    public static void loadHist(Context context)
    {
        SharedPreferences sharedPreferences = context.getSharedPreferences(MainActivity.PREFS_NAME, MODE_PRIVATE);
        MainActivity.HISTORY = sharedPreferences.getStringSet(HISTORY_PREFS_KEY, new TreeSet<String>());

    }

    public static void pronunciateWord(CharSequence word, float pitch, float speechRate, Voice accent)
    {
        //manual pronunciation of a word, never used.
        tts.setPitch(pitch);
        tts.setSpeechRate(speechRate);
        tts.setVoice(accent);
        tts.speak(word, QUEUE_ADD, null, null);
    }

    public static ArrayList<String> loadRecordings()
    {
        //load all recordings, needs to be used in order to build the HistoryFragment
        ArrayList<String> recordings = new ArrayList<>();
        String path = Environment.getExternalStorageDirectory().getPath()+"/"+AUDIO_RECORDER_FOLDER;
        Log.d("Files", "Path: " + path);
        File directory = new File(path);
        File[] files = directory.listFiles();
        if(files != null) {
            for (int i = 0; i < files.length; i++) {
                Log.d("Files", "FileName:" + files[i].getName());
                if (!files[i].getName().equals(".nomedia"))
                    recordings.add(files[i].getName().substring(0, files[i].getName().lastIndexOf(".")));
            }
        }
        return recordings;
    }

    public static boolean checkFile(String word) {
        String path = Environment.getExternalStorageDirectory().getPath()+"/"+AUDIO_RECORDER_FOLDER;
        Log.i("DEBUG FILE: ", path+"/"+word+".aac");
        File f = new File(path+"/"+word+".aac");
        if(f.exists() && !f.isDirectory()) {
            Log.i("DEBUG FILE:", "FILE ESISTE! RITORNO TRUE");
            return true;
        } else return false;
    }

    public static void loadDictionary(Activity activity) {
        //loading wordslist from file.
        BufferedReader line_reader = new BufferedReader(new InputStreamReader(activity.getResources().openRawResource(R.raw.wordlist)));
        String line;
        try {
            while ((line = line_reader.readLine()) != null) {
                WordList.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        Collections.sort(WordList);
    }

    public static void deleteRecordings()
    {
        //delete all recordings
        String path = Environment.getExternalStorageDirectory().getPath()+"/"+AUDIO_RECORDER_FOLDER;
        File directory = new File(path);
        File[] files = directory.listFiles();
        if(files != null) {
            for (int i = 0; i < files.length; i++) {
                if (files[i].getName().equals(".nomedia")) ;
                else files[i].delete();
            }
        }
    }

    public static void deleteRecording(Context context, String word)
    {
        //delete a recording
        String path = Environment.getExternalStorageDirectory().getPath()+"/"+AUDIO_RECORDER_FOLDER;
        File directory = new File(path);
        File[] files = directory.listFiles();
        if(files != null) {
            for (int i = 0; i < files.length; i++) {
                if (files[i].getName().equals(word + ".aac")){
                    files[i].delete();
                }
            }
        }
    }


    //FUNZIONI PER RICHIESTA PERMESSI
    public static boolean checkPermission(Context context) {
        int result = ContextCompat.checkSelfPermission(context,
                WRITE_EXTERNAL_STORAGE);
        int result1 = ContextCompat.checkSelfPermission(context,
                RECORD_AUDIO);
        return result == PackageManager.PERMISSION_GRANTED &&
                result1 == PackageManager.PERMISSION_GRANTED;
    }

    public static void requestPermission(Context context) {
        ActivityCompat.requestPermissions((Activity) context, new
                String[]{WRITE_EXTERNAL_STORAGE, RECORD_AUDIO}, RequestPermissionCode);
    }


    //FUNZIONI DI REGISTRAZIONE
    public static String getFilename(String file_exts[], int currentFormat) {
        String filepath = Environment.getExternalStorageDirectory().getPath();
        File file = new File(filepath, AUDIO_RECORDER_FOLDER);

        if (!file.exists()) {
            file.mkdirs();
            File nomedia = new File(Environment.getExternalStorageDirectory().getPath()+"/"+AUDIO_RECORDER_FOLDER+"/.nomedia");
            try { nomedia.createNewFile(); } catch (IOException e) {Log.i("LOG:",".nomedia not created");}
        }

        return (file.getAbsolutePath() + "/" + selected_word + file_exts[currentFormat]);
    }

    public static void startRecording(MediaRecorder recorder, int output_formats[], int currentFormat, String file_exts[]) {
        //TODO SE IL FILE GIA' ESISTE, CANCELLALO E REGISTRA NUOVAMENTE
        try {

        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(output_formats[currentFormat]);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.HE_AAC);
        recorder.setAudioEncodingBitRate(16);
        recorder.setAudioSamplingRate(44100);
        recorder.setOutputFile(getFilename(file_exts, currentFormat));
        recorder.setOnErrorListener(errorListener);
        recorder.setOnInfoListener(infoListener);


            recorder.prepare();
            recorder.start();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void stopRecording(MediaRecorder recorder) {
        if (null != recorder) {
            recorder.stop();
            recorder.reset();
        }
    }

    public static void playRecording(MediaPlayer mediaPlayer) {
        try {
            //TODO CHECK IF RECORDING ALREADY EXISTS. IF DOES NOT, DO NOT PLAY.
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
                Log.i("Say it!","Playing recording: "+Environment.getExternalStorageDirectory().getPath()+"/"+AUDIO_RECORDER_FOLDER+"/"+selected_word+".aac");
                mediaPlayer.reset(); //Before a setDataSource call, you need to reset MP obj.
                mediaPlayer.setDataSource(Environment.getExternalStorageDirectory().getPath()+"/"+AUDIO_RECORDER_FOLDER+"/"+selected_word+".aac");
                mediaPlayer.prepare();
                mediaPlayer.start();
            }
            else {
                Log.i("Say it!", "Playing recording: " + Environment.getExternalStorageDirectory().getPath() + "/" + AUDIO_RECORDER_FOLDER + "/" + selected_word + ".aac");
                mediaPlayer.reset(); //Before a setDataSource call, you need to reset MP obj.
                mediaPlayer.setDataSource(Environment.getExternalStorageDirectory().getPath() + "/" + AUDIO_RECORDER_FOLDER + "/" + selected_word + ".aac");
                mediaPlayer.prepare();
                mediaPlayer.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static MediaRecorder.OnErrorListener errorListener = new MediaRecorder.OnErrorListener() {
        @Override
        public void onError(MediaRecorder mr, int what, int extra) {
            Log.i("Say it!","Error: " + what + ", " + extra);
        }
    };

    private static MediaRecorder.OnInfoListener infoListener = new MediaRecorder.OnInfoListener() {
        @Override
        public void onInfo(MediaRecorder mr, int what, int extra) {
            Log.i("Say it!","Warning: " + what + ", " + extra);
        }
    };
}