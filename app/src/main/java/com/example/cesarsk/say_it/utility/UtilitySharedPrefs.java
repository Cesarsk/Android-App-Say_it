package com.example.cesarsk.say_it.utility;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.v4.util.Pair;
import android.widget.Toast;

import com.example.cesarsk.say_it.ui.MainActivity;
import com.example.cesarsk.say_it.R;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;

import static android.content.Context.MODE_PRIVATE;
import static com.example.cesarsk.say_it.ui.MainActivity.FAVORITES_PREFS_KEY;
import static com.example.cesarsk.say_it.ui.MainActivity.HISTORY_PREFS_KEY;

/**
 * Created by Claudio on 22/03/2017.
 */

@SuppressWarnings("ALL")
public class UtilitySharedPrefs {
    //Gestione Preferences
    public static void savePrefs(Context context, Set<String> set, String prefs_key) {
        SharedPreferences settings = context.getSharedPreferences(MainActivity.PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();

        editor.putStringSet(prefs_key, set);
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

    public static void addHist(Context context, Pair<String, String> pair) {
        Set<String> new_hist = new TreeSet<>();
        loadHist(context);
        if (MainActivity.HISTORY != null) {
            for (String element : MainActivity.HISTORY) {
                new_hist.add(element);
            }
        }
        //TODO INFINITI ELEMENTI NELLA HISTORY? NON VA BENE!!!
        Calendar c = Calendar.getInstance();
        Gson gson = new Gson();
        String SerializedPair = gson.toJson(new SayItPair(pair.first, pair.second, c.getTime()));
        new_hist.add(SerializedPair);
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

    public static void deletePreferences(Context context) {
        //TODO AGGIUNGERE IN IMPOSTAZIONI
        //delete all preferences
        SharedPreferences settings = context.getSharedPreferences(MainActivity.PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.clear();
        editor.apply();
        Toast.makeText(context, "Deleted preferences!", Toast.LENGTH_SHORT).show();
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

    public static String getRandomQuote() {

        Random rand = new Random();

        //Creating a List from the WordList_Map values
        ArrayList<String> quotes = new ArrayList<>(MainActivity.Quotes);

        //Getting a random sublist and then extracting a random word from it
        String random_quote = quotes.get(rand.nextInt(quotes.size()));
        return random_quote;
    }
}
