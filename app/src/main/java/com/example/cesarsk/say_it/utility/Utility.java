package com.example.cesarsk.say_it.utility;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by cesarsk on 17/02/2017.
 */

@SuppressWarnings("ALL")
public class Utility {

    public static boolean delete_recordings() {
        //load all recordings, needs to be used in order to build the HistoryFragment
        ArrayList<String> recordings = new ArrayList<>();
        String path = Environment.getExternalStorageDirectory().getPath() + "/" + UtilityRecord.AUDIO_RECORDER_FOLDER;
        Log.d("Files", "Path: " + path);
        File directory = new File(path);
        File[] files = directory.listFiles();
        if (files != null) {
            for (int i = 0; i < files.length; i++) {
                Log.d("Files", "FileName:" + files[i].getName());
                if (!files[i].getName().equals(".nomedia"))
                    files[i].delete();
            }
        }
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

    public static void shareWord(String word, String ipa, Context context) {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, "Say It! just taught me that the word '" + word + "'" + " is pronounced " + ipa + " !!");
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


}