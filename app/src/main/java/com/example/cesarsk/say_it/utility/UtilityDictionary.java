package com.example.cesarsk.say_it.utility;

import android.app.Activity;
import android.content.Context;
import android.support.v4.util.Pair;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cesarsk.say_it.ui.MainActivity;
import com.example.cesarsk.say_it.R;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

import static com.example.cesarsk.say_it.ui.MainActivity.IPAofTheDay;
import static com.example.cesarsk.say_it.ui.MainActivity.wordOfTheDay;

/**
 * Created by Claudio on 22/03/2017.
 */

@SuppressWarnings("ALL")
public class UtilityDictionary {
    public static String getRandomWord() {

        Random rand = new Random();

        //Creating a List from the WordList_Map values
        ArrayList<ArrayList<Pair<String, String>>> MapValues = new ArrayList<>(MainActivity.Wordlists_Map.values());

        //Getting a random sublist and then extracting a random word from it
        ArrayList<Pair<String, String>> random_list = MapValues.get(rand.nextInt(MapValues.size()));
        Pair<String, String> random_pair = random_list.get(rand.nextInt(random_list.size()));

        //String random_word = WordList.get(new Random().nextInt(WordList.size()));
        return random_pair.first;
    }

    public static Pair<String, String> getRandomWordWithIPA() {

        Random rand = new Random();

        //Creating a List from the WordList_Map values
        ArrayList<ArrayList<Pair<String, String>>> MapValues = new ArrayList<>(MainActivity.Wordlists_Map.values());

        //Getting a random sublist and then extracting a random word from it
        ArrayList<Pair<String, String>> random_list = MapValues.get(rand.nextInt(MapValues.size()));
        Pair<String, String> random_pair = random_list.get(rand.nextInt(random_list.size()));

        //String random_word = WordList.get(new Random().nextInt(WordList.size()));
        return random_pair;
    }

    public static String getRandomWord(long seed, boolean ipa) {
        Random rand = new Random(seed);

        //Creating a List from the WordList_Map values
        ArrayList<ArrayList<Pair<String, String>>> MapValues = new ArrayList<>(MainActivity.Wordlists_Map.values());

        //Getting a random sublist and then extracting a random word from it
        ArrayList<Pair<String, String>> random_list = MapValues.get(rand.nextInt(MapValues.size()));
        Pair<String, String> random_pair = random_list.get(rand.nextInt(random_list.size()));

        if (ipa) {
            return random_pair.second;
        } else if (!ipa) {
            return random_pair.first;
        }

        return null;
    }

    public static void loadDictionary(Context context) throws IOException {

        //Getting Buffered Readers linked to the two txt files in the raw folder
        BufferedReader dictionary_line_reader = new BufferedReader(new InputStreamReader(context.getResources().openRawResource(R.raw.dictionary_utf8)));
        BufferedReader ipa_line_reader = new BufferedReader(new InputStreamReader(context.getResources().openRawResource(R.raw.ipa_utf16le), Charset.forName("UTF-16LE")));

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

    public static String getDate(long timeStamp) {

        try {
            DateFormat sdf = new SimpleDateFormat("yyyyMMdd");
            Date netDate = (new Date(timeStamp));
            return sdf.format(netDate);
        } catch (Exception ex) {
            return "xx";
        }
    }

    public static String getDailyRandomQuote() {
        Calendar c = Calendar.getInstance();
        Long seed = Long.parseLong(getDate(c.getTimeInMillis()));
        Random rand = new Random(seed);

        //Creating a List from the WordList_Map values
        ArrayList<String> quotes = new ArrayList<>(MainActivity.Quotes);

        //Getting a random sublist and then extracting a random word from it
        String random_quote = quotes.get(rand.nextInt(quotes.size()));
        return random_quote;
    }

    public static StringBuilder load_textfile(Context context,int resFile) throws IOException {
        StringBuilder text = new StringBuilder();
        BufferedReader reader = null;

        try {
            reader = new BufferedReader(
                    new InputStreamReader(context.getResources().openRawResource(resFile)));

            String mLine;
            while ((mLine = reader.readLine()) != null) {
                text.append(mLine);
                text.append('\n');
            }
            reader.close();
        } catch (IOException e) {
            Toast.makeText(context, "Error reading file!", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }

        return text;
    }

}
