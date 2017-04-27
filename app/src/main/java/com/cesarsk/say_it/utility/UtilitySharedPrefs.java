package com.cesarsk.say_it.utility;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.v4.util.Pair;
import android.widget.Toast;

import com.cesarsk.say_it.ui.MainActivity;
import com.cesarsk.say_it.R;
import com.cesarsk.say_it.ui.fragments.HistoryFragment;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Set;
import java.util.TreeSet;

import static android.content.Context.MODE_PRIVATE;
import static com.cesarsk.say_it.ui.MainActivity.DEFAULT_ACCENT_KEY;
import static com.cesarsk.say_it.ui.MainActivity.DEFAULT_NOTIFICATION_HOUR_KEY;
import static com.cesarsk.say_it.ui.MainActivity.DEFAULT_NOTIFICATION_MINUTE_KEY;
import static com.cesarsk.say_it.ui.MainActivity.DEFAULT_NOTIFICATION_RATE_KEY;
import static com.cesarsk.say_it.ui.MainActivity.FAVORITES_PREFS_KEY;
import static com.cesarsk.say_it.ui.MainActivity.HISTORY_PREFS_KEY;

/**
 * Created by Claudio on 22/03/2017.
 */

@SuppressWarnings("ALL")
public class UtilitySharedPrefs {

    public static void loadAdsStatus(Context context){
        SharedPreferences preferences = context.getSharedPreferences(MainActivity.PREFS_NAME, MODE_PRIVATE);
        MainActivity.NO_ADS = preferences.getBoolean(MainActivity.NO_ADS_STATUS_KEY, false);
    }

    //Gestione Preferences
    public static void savePrefs(Context context, Set<String> set, String prefs_key) {
        SharedPreferences settings = context.getSharedPreferences(MainActivity.PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();

        editor.putStringSet(prefs_key, set);
        editor.apply();
    }

    public static void savePrefs(Context context, String value, String prefs_key){
        SharedPreferences settings = context.getSharedPreferences(MainActivity.PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();

        editor.putString(prefs_key, value);
        editor.apply();
    }

    public static void savePrefs(Context context, int value, String prefs_key){
        SharedPreferences settings = context.getSharedPreferences(MainActivity.PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();

        editor.putInt(prefs_key, value);
        editor.apply();
    }

    public static void savePrefs(Context context, boolean value, String prefs_key){
        SharedPreferences settings = context.getSharedPreferences(MainActivity.PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();

        editor.putBoolean(prefs_key, value);
        editor.apply();
    }


    public static void addFavs(Context context, Pair<String, String> pair) {
        Set<String> new_favs = new TreeSet<>();
        loadFavs(context);
        if (MainActivity.FAVORITES != null) {
            for (String element : MainActivity.FAVORITES) {
                new_favs.add(element);
            }
        }
        Gson gson = new Gson();
        String SerializedPair = gson.toJson(new SayItPair(pair.first, pair.second));
        new_favs.add(SerializedPair);
        savePrefs(context, new_favs, MainActivity.FAVORITES_PREFS_KEY);
    }

    public static ArrayList<SayItPair> getRecentHistory(Context context, int items){

        ArrayList<SayItPair> recentHistory = new ArrayList<>();
        ArrayList<SayItPair> DeserializedHistory = HistoryFragment.loadDeserializedHistory(context);

        if(DeserializedHistory != null && !DeserializedHistory.isEmpty()) {
            if(DeserializedHistory.size() >= items) {
                recentHistory = new ArrayList<>(DeserializedHistory.subList(0, items));
            }

            else{
                return DeserializedHistory;
            }
        }

        return recentHistory;
    }

    public static void removeFavs(Context context, Pair<String, String> pair) {
        Set<String> new_favs = new TreeSet<>();
        loadFavs(context);
        if (MainActivity.FAVORITES != null) {
            for (String element : MainActivity.FAVORITES) {
                new_favs.add(element);
            }
        }
        Gson gson = new Gson();
        new_favs.remove(gson.toJson(new SayItPair(pair.first, pair.second)));
        savePrefs(context, new_favs, MainActivity.FAVORITES_PREFS_KEY);
    }

    public static void clearHistory(Context context){
        savePrefs(context, new TreeSet<String>(), MainActivity.HISTORY_PREFS_KEY);
        loadHist(context); //refreshing the view
    }

    public static void clearFavorites(Context context){
        savePrefs(context, new TreeSet<String>(), MainActivity.FAVORITES_PREFS_KEY);
        loadFavs(context);
    }

    /*public static void removeRecording(Context context, String recordingFilename){
        Set<String> new_recs = new TreeSet<>();
        loadRecordings(context);
        if (MainActivity.RECORDINGS != null) {
            for (String element : MainActivity.RECORDINGS) {
                new_recs.add(element);
            }
        }
        new_recs.remove(recordingFilename);
        savePrefs(context, new_recs, MainActivity.RECORDINGS_PREFS_KEY);
    }*/

    public static void removeHist(Context context, SayItPair pair) {
        Set<String> new_favs = new TreeSet<>();
        loadHist(context);
        if (MainActivity.HISTORY != null) {
            for (String element : MainActivity.HISTORY) {
                new_favs.add(element);
            }
        }
        Gson gson = new Gson();
        new_favs.remove(gson.toJson(pair));
        savePrefs(context, new_favs, MainActivity.HISTORY_PREFS_KEY);
    }

    public static boolean checkFavs(Context context, String word) {
        Set<String> new_favs = new TreeSet<>();
        loadFavs(context);
        if (MainActivity.FAVORITES != null) {
            for (String element : MainActivity.FAVORITES) {
                new_favs.add(element);
            }
        }

        ArrayList<Pair<String, String>> DeserializedFavs = new ArrayList<>();
        Gson gson = new Gson();
        for (String element : new_favs) {
            SayItPair pair = gson.fromJson(element, SayItPair.class);
            DeserializedFavs.add(pair);
        }

        for (Pair<String, String> element : DeserializedFavs) {
            if (element.first.equals(word)) {
                return true;
            }
        }

        return false;
    }

    public static void addHist(Context context, SayItPair pair) {
        Set<String> new_hist = new TreeSet<>();
        loadHist(context);
        if (MainActivity.HISTORY != null) {
            for (String element : MainActivity.HISTORY) {
                new_hist.add(element);
            }
        }

        ArrayList<SayItPair> DeserializedHistory = new ArrayList<>();

        Gson gson = new Gson();

        for (String element : new_hist) {
            SayItPair current_pair = gson.fromJson(element, SayItPair.class);
            DeserializedHistory.add(current_pair);
        }

        //ciclo per evitare duplicati
        for(int i=0; i<DeserializedHistory.size(); i++){
            if(DeserializedHistory.get(i).first.equalsIgnoreCase(pair.first)){
                new_hist.remove(gson.toJson(DeserializedHistory.get(i)));
                new_hist.add(gson.toJson(pair));
            }
        }
        String SerializedPair = gson.toJson(pair);
        new_hist.add(SerializedPair);
        savePrefs(context, new_hist, MainActivity.HISTORY_PREFS_KEY);
    }

    /*public static void addRecording(Context context, String recordingFilename){
        Set<String> new_recs = new TreeSet<>();
        loadRecordings(context);
        if (MainActivity.RECORDINGS != null) {
            for (String element : MainActivity.RECORDINGS) {
                new_recs.add(element);
            }
        }

        new_recs.add(recordingFilename);
        savePrefs(context, new_recs, MainActivity.RECORDINGS_PREFS_KEY);
    }*/

    /*public static boolean checkRecording(Context context, String word){
        Set<String> new_recs = new TreeSet<>();
        loadRecordings(context);
        if (MainActivity.RECORDINGS != null) {
            for (String element : MainActivity.RECORDINGS) {
                new_recs.add(element);
            }
        }

        String filename = Environment.getExternalStorageDirectory().getPath() + "/" + UtilityRecordings.AUDIO_RECORDER_FOLDER + "/" + word + ".aac";

        if(new_recs.contains(filename)) {

            if (!(new File(filename).exists())) {
                removeRecording(context, filename);
                return false;
            }

            else
                return true;
        }

        return false;
    }*/

    public static void loadFavs(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(MainActivity.PREFS_NAME, MODE_PRIVATE);
        MainActivity.FAVORITES = sharedPreferences.getStringSet(FAVORITES_PREFS_KEY, new TreeSet<String>());
    }

    /*public static void loadRecordings(Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences(MainActivity.PREFS_NAME, MODE_PRIVATE);
        MainActivity.RECORDINGS = sharedPreferences.getStringSet(RECORDINGS_PREFS_KEY, new TreeSet<String>());
    }*/

    public static void loadHist(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(MainActivity.PREFS_NAME, MODE_PRIVATE);
        MainActivity.HISTORY = sharedPreferences.getStringSet(HISTORY_PREFS_KEY, new TreeSet<String>());
    }

    public static void loadSettingsPrefs(Context context)
    {
        //Caricamento preferenze
        SharedPreferences sharedPreferences = context.getSharedPreferences(MainActivity.PREFS_NAME, MODE_PRIVATE);
        MainActivity.DEFAULT_ACCENT = sharedPreferences.getString(DEFAULT_ACCENT_KEY, "0");
        MainActivity.DEFAULT_NOTIFICATION_RATE = sharedPreferences.getString(DEFAULT_NOTIFICATION_RATE_KEY, "2");
        MainActivity.DEFAULT_NOTIFICATION_HOUR = sharedPreferences.getString(DEFAULT_NOTIFICATION_HOUR_KEY, "12");
        MainActivity.DEFAULT_NOTIFICATION_MINUTE = sharedPreferences.getString(DEFAULT_NOTIFICATION_MINUTE_KEY, "00");
    }


    public static void deletePreferences(Context context) {
        //TODO AGGIUNGERE IN IMPOSTAZIONI
        //delete all preferences
        SharedPreferences settings = context.getSharedPreferences(MainActivity.PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.clear();
        editor.apply();
        Toast.makeText(context, "Preferences deleted!", Toast.LENGTH_SHORT).show();
    }

    public static void loadQuotes(Activity activity) throws IOException {

        //Getting Buffered Readers linked to the two txt files in the raw folder
        BufferedReader quote_line_reader = new BufferedReader(new InputStreamReader(activity.getResources().openRawResource(R.raw.quotes)));

        //Temporary wordlist that stores the "current" element in the loop
        ArrayList<String> temp_wordlist = new ArrayList<>();

        //If the line is not the * character keep creating Pairs of values and store them into
        //the temporary list. When the * is reached a sublist has been completed so it is stored
        //in the Wordlist_Map that contains all the sublists.
        String dictionary_line;
        while (((dictionary_line = quote_line_reader.readLine()) != null)) {

            if (!(dictionary_line.equalsIgnoreCase("*"))) {
                String current_word = new String(dictionary_line);
                temp_wordlist.add(current_word);
            } else {
                MainActivity.Quotes.add(temp_wordlist.get(0));
                temp_wordlist = new ArrayList<>();
            }
        }
    }

}
