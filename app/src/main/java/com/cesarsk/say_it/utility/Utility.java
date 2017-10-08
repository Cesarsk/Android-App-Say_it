package com.cesarsk.say_it.utility;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.util.TypedValue;

import java.io.File;

/**
 * Created by cesarsk on 17/02/2017.
 */

@SuppressWarnings("ALL")
public class Utility {

    public static boolean delete_recordings(Context context) {
        File dir = new File(context.getFilesDir().getAbsolutePath());
        File[] files = dir.listFiles();
        if (files != null) {
            for (int i = 0; i < files.length; i++) {
                if (!files[i].getName().equals(".nomedia"))
                    files[i].delete();
            }
        }
        return true;
    }

    public static CharSequence underlineText(CharSequence text) {
        SpannableString content = new SpannableString(text);
        content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
        return content;
    }

    public static void searchMeaning(Context context, String word) {
        Uri uri = Uri.parse("http://www.google.com/#q=" + word + "+meaning");
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        context.startActivity(intent);
    }

    public static void openURL(Context context, String url) {
        Uri uri = Uri.parse(url);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        context.startActivity(intent);
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

    public static int setColorByTheme(int attribute, Context context)
    {
        TypedValue typedValue = new TypedValue();
        if (context.getTheme().resolveAttribute(attribute, typedValue, true))
            return typedValue.data;
        else
            return Color.TRANSPARENT;
    }


}