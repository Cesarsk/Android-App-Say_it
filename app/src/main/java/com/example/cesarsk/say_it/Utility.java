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
import android.speech.tts.TextToSpeech;
import android.speech.tts.Voice;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.util.Pair;
import android.util.Log;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;

import static android.Manifest.permission.RECORD_AUDIO;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.content.Context.MODE_PRIVATE;
import static android.speech.tts.TextToSpeech.QUEUE_ADD;
import static com.example.cesarsk.say_it.MainActivity.FAVORITES_PREFS_KEY;
import static com.example.cesarsk.say_it.MainActivity.HISTORY_PREFS_KEY;
import static com.example.cesarsk.say_it.MainActivity.IPAofTheDay;
import static com.example.cesarsk.say_it.MainActivity.Wordlists_Map;
import static com.example.cesarsk.say_it.MainActivity.american_speaker_google;
import static com.example.cesarsk.say_it.MainActivity.wordOfTheDay;
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

    public static void share(String word, String ipa, Context context) {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, "Say It! just taught me that the word '"+word+"'"+" is pronounced "+ipa+" !!");
        sendIntent.setType("text/plain");
        context.startActivity(sendIntent);
    }

    //Rate-Us Module
    public static void rateUs(Context context) {
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
    public static void savePrefs(Context context, Set<String> set, String prefs_key) {
        SharedPreferences settings = context.getSharedPreferences(MainActivity.PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();

        editor.putStringSet(prefs_key, set);
        editor.apply();
    }

    public static void addFavs(Context context, String word) {
        Set<String> new_favs = new TreeSet<>();
        loadFavs(context);
        if (MainActivity.FAVORITES != null) {
            for (String element : MainActivity.FAVORITES) {
                new_favs.add(element);
            }
        }
        new_favs.add(word);
        savePrefs(context, new_favs, MainActivity.FAVORITES_PREFS_KEY);
    }

    public static void removeFavs(Context context, String word) {
        Set<String> new_favs = new TreeSet<>();
        loadFavs(context);
        if (MainActivity.FAVORITES != null) {
            for (String element : MainActivity.FAVORITES) {
                new_favs.add(element);
            }
        }
        new_favs.remove(word);
        savePrefs(context, new_favs, MainActivity.FAVORITES_PREFS_KEY);
    }

    public static boolean checkFavs(Context context, String word) {
        Set<String> new_favs = new TreeSet<>();
        loadFavs(context);
        if (MainActivity.FAVORITES != null) {
            for (String element : MainActivity.FAVORITES) {
                new_favs.add(element);
            }
        }
        if (new_favs.contains(word)) return true;
        else return false;
    }

    public static void addHist(Context context, String word) {
        Set<String> new_hist = new TreeSet<>();
        loadHist(context);
        if (MainActivity.HISTORY != null) {
            for (String element : MainActivity.HISTORY) {
                new_hist.add(element);
            }
        }
        //TODO INFINITI ELEMENTI NELLA HISTORY? NON VA BENE!!!
        new_hist.add(word);
        savePrefs(context, new_hist, MainActivity.HISTORY_PREFS_KEY);
    }

    public static void loadFavs(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(MainActivity.PREFS_NAME, MODE_PRIVATE);
        MainActivity.FAVORITES = sharedPreferences.getStringSet(FAVORITES_PREFS_KEY, new TreeSet<String>());
    }

    public static void loadHist(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(MainActivity.PREFS_NAME, MODE_PRIVATE);
        MainActivity.HISTORY = sharedPreferences.getStringSet(HISTORY_PREFS_KEY, new TreeSet<String>());

    }

    public static void pronunciateWord(CharSequence word, float pitch, float speechRate, Voice accent) {
        //manual pronunciation of a word, never used.
        american_speaker_google.setPitch(pitch);
        american_speaker_google.setSpeechRate(speechRate);
        american_speaker_google.setVoice(accent);
        american_speaker_google.speak(word, QUEUE_ADD, null, null);
    }

    public static ArrayList<String> loadRecordings() {
        //load all recordings, needs to be used in order to build the HistoryFragment
        ArrayList<String> recordings = new ArrayList<>();
        String path = Environment.getExternalStorageDirectory().getPath() + "/" + AUDIO_RECORDER_FOLDER;
        Log.d("Files", "Path: " + path);
        File directory = new File(path);
        File[] files = directory.listFiles();
        if (files != null) {
            for (int i = 0; i < files.length; i++) {
                Log.d("Files", "FileName:" + files[i].getName());
                if (!files[i].getName().equals(".nomedia"))
                    recordings.add(files[i].getName().substring(0, files[i].getName().lastIndexOf(".")));
            }
        }
        return recordings;
    }

    public static boolean checkFile(String word) {
        String path = Environment.getExternalStorageDirectory().getPath() + "/" + AUDIO_RECORDER_FOLDER;
        Log.i("DEBUG FILE: ", path + "/" + word + ".aac");
        File f = new File(path + "/" + word + ".aac");
        if (f.exists() && !f.isDirectory()) {
            Log.i("DEBUG FILE:", "FILE ESISTE! RITORNO TRUE");
            return true;
        } else return false;
    }

    public static String getRandomWord() {

        Random rand = new Random();

        //Creating a List from the WordList_Map values
        ArrayList<ArrayList<Pair<String, String>>> MapValues =  new ArrayList<>(MainActivity.Wordlists_Map.values());

        //Getting a random sublist and then extracting a random word from it
        ArrayList<Pair<String, String>> random_list = MapValues.get(rand.nextInt(MapValues.size()));
        Pair<String, String> random_pair = random_list.get(rand.nextInt(random_list.size()));

        //String random_word = WordList.get(new Random().nextInt(WordList.size()));
        return random_pair.first;
    }

    public static String getRandomWord(long seed, boolean ipa) {

        Random rand = new Random(seed);

        //Creating a List from the WordList_Map values
        ArrayList<ArrayList<Pair<String, String>>> MapValues =  new ArrayList<>(MainActivity.Wordlists_Map.values());

        //Getting a random sublist and then extracting a random word from it
        ArrayList<Pair<String, String>> random_list = MapValues.get(rand.nextInt(MapValues.size()));
        Pair<String, String> random_pair = random_list.get(rand.nextInt(random_list.size()));

        if(ipa) {
            return random_pair.second;
        }

        else if(!ipa){
            return random_pair.first;
        }

        return null;
    }



    public static void loadDictionary(Activity activity) throws IOException {

        //Getting Buffered Readers linked to the two txt files in the raw folder
        BufferedReader dictionary_line_reader = new BufferedReader(new InputStreamReader(activity.getResources().openRawResource(R.raw.dictionary_utf8)));
        BufferedReader ipa_line_reader = new BufferedReader(new InputStreamReader(activity.getResources().openRawResource(R.raw.ipa_utf16le), Charset.forName("UTF-16LE")));

        //Temporary wordlist that stores the "current" element in the loop
        ArrayList<Pair<String, String>> temp_wordlist = new ArrayList<>();

        //Skip until line 38 (Copyright message)
        for (int i = 0; i < 38; i++) {
            dictionary_line_reader.readLine();
            ipa_line_reader.readLine();
        }

        //If the line is not the # character keep creating Pairs of values and store them into
        //the temporary list. When the # is reached a sublist has been completed so it is stored
        //in the Wordlist_Map that contains all the sublists.
        String dictionary_line, ipa_line;
        while (((dictionary_line = dictionary_line_reader.readLine()) != null) && ((ipa_line = ipa_line_reader.readLine()) != null)) {

            if (!(dictionary_line.equalsIgnoreCase("#")) && !(ipa_line.equalsIgnoreCase("#"))) {

                Pair<String, String> current_word = new Pair<>(dictionary_line, ipa_line);
                temp_wordlist.add(current_word);
            } else {
                MainActivity.Wordlists_Map.put(temp_wordlist.get(0).first.substring(0, 1).toLowerCase(), temp_wordlist);
                temp_wordlist = new ArrayList<>();
            }
        }

        Calendar c = Calendar.getInstance();
        Long seed = Long.parseLong(getDate(c.getTimeInMillis()));
        wordOfTheDay = getRandomWord(seed, false);
        IPAofTheDay = getRandomWord(seed, true);
    }

    private static String getDate(long timeStamp) {

        try {
            DateFormat sdf = new SimpleDateFormat("yyyyMMdd");
            Date netDate = (new Date(timeStamp));
            return sdf.format(netDate);
        } catch (Exception ex) {
            return "xx";
        }
    }

    public static void deleteRecordings() {
        //delete all recordings
        String path = Environment.getExternalStorageDirectory().getPath() + "/" + AUDIO_RECORDER_FOLDER;
        File directory = new File(path);
        File[] files = directory.listFiles();
        if (files != null) {
            for (int i = 0; i < files.length; i++) {
                if (files[i].getName().equals(".nomedia")) ;
                else files[i].delete();
            }
        }
    }

    public static void deleteRecording(Context context, String word) {
        //delete a recording
        String path = Environment.getExternalStorageDirectory().getPath() + "/" + AUDIO_RECORDER_FOLDER;
        File directory = new File(path);
        File[] files = directory.listFiles();
        if (files != null) {
            for (int i = 0; i < files.length; i++) {
                if (files[i].getName().equals(word + ".aac")) {
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
            File nomedia = new File(Environment.getExternalStorageDirectory().getPath() + "/" + AUDIO_RECORDER_FOLDER + "/.nomedia");
            try {
                nomedia.createNewFile();
            } catch (IOException e) {
                Log.i("LOG:", ".nomedia not created");
            }
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
                Log.i("Say it!", "Playing recording: " + Environment.getExternalStorageDirectory().getPath() + "/" + AUDIO_RECORDER_FOLDER + "/" + selected_word + ".aac");
                mediaPlayer.reset(); //Before a setDataSource call, you need to reset MP obj.
                mediaPlayer.setDataSource(Environment.getExternalStorageDirectory().getPath() + "/" + AUDIO_RECORDER_FOLDER + "/" + selected_word + ".aac");
                mediaPlayer.prepare();
                mediaPlayer.start();
            } else {
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
            Log.i("Say it!", "Error: " + what + ", " + extra);
        }
    };

    private static MediaRecorder.OnInfoListener infoListener = new MediaRecorder.OnInfoListener() {
        @Override
        public void onInfo(MediaRecorder mr, int what, int extra) {
            Log.i("Say it!", "Warning: " + what + ", " + extra);
        }
    };

    //IMPOSTAZIONE TEXT TO SPEECH
    public static Voice searchVoice(String voiceName, TextToSpeech tts) {
        //Log.i("VOICES:", textToSpeech.getVoices().toString()); //stampa tutte le voci disponibili
        for (Voice tmpVoice : tts.getVoices()) {
            if (tmpVoice.getName().equals(voiceName)) {
                return tmpVoice;
            }
        }
        return null;
    }

}