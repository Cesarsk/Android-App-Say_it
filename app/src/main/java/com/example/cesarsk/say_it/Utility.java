package com.example.cesarsk.say_it;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

import static android.content.Context.MODE_PRIVATE;
import static com.example.cesarsk.say_it.MainActivity.PREFS_NAME;
import static com.example.cesarsk.say_it.MainActivity.PREFS_WORDS_FAVORITES;

/**
 * Created by cesarsk on 17/02/2017.
 */

public class Utility {

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
    public static void savePrefs(Context context, Set<String> favorites_word)
    {
        SharedPreferences settings = context.getSharedPreferences(MainActivity.PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();

        editor.putStringSet(MainActivity.PREFS_WORDS_FAVORITES, favorites_word);
        editor.apply();
    }

    public static void addFavs(Context context, String word)
    {
        Set<String> new_favs = new TreeSet<>();
        loadFavs(context);
        if(MainActivity.favorites_word != null){
            for (String element: MainActivity.favorites_word) {
                new_favs.add(element);
            }
        }
        new_favs.add(word);
        savePrefs(context, new_favs);
    }

    public static void loadFavs(Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences(MainActivity.PREFS_NAME, MODE_PRIVATE);
        MainActivity.favorites_word = sharedPreferences.getStringSet(PREFS_WORDS_FAVORITES, new TreeSet<String>());
    }
}

//Old two buttons used to manual TTS a word.
/*Button button = (Button) view.findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                EditText et = (EditText)view.findViewById(R.id.editText);
                tts.setPitch((float)0.85);
                tts.setSpeechRate((float)0.90);
                tts.setVoice(voice_british_female);
                tts.speak(et.getEditableText().toString(), QUEUE_ADD, null, null);
            }
        });

        Button button1 = (Button) view.findViewById(R.id.button2);
        button1.setOnClickListener(new View.OnClickListener()
        {
           @Override
           public void onClick(View v)
           {
               EditText et = (EditText)view.findViewById(R.id.editText);
               tts.setPitch((float)0.85);
               tts.setSpeechRate((float)0.70);
               tts.setVoice(voice_american_female);
               tts.speak(et.getEditableText().toString(), QUEUE_ADD, null, null);
           }
        }); */