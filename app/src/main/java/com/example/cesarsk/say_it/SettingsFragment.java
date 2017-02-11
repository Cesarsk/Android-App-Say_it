package com.example.cesarsk.say_it;


import android.app.Activity;
import android.app.Fragment;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;

import java.util.List;

import static com.example.cesarsk.say_it.MainActivity.tts;
import static com.example.cesarsk.say_it.MainActivity.voice_american_female;
import static com.example.cesarsk.say_it.MainActivity.voice_british_female;


/**
 * A simple {@link Fragment} subclass.
 */
public class SettingsFragment extends Fragment {

    private String emails[] = {"luca.cesarano1@gmail.com"};
    public SettingsFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_settings,
                container, false);

        Button contact_us = (Button) view.findViewById(R.id.contact_us);
        contact_us.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareToMail(emails, "[CONTACT US - SAY IT!]");
            }
        });

        Button bug_report = (Button) view.findViewById(R.id.bug_report);
        bug_report.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareToMail(emails, "[BUG REPORT - SAY IT!]");
            }
        });

        Button rate_us = (Button) view.findViewById(R.id.rate_us);
        rate_us.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rateUs();
            }
        });

        final Spinner default_voice = (Spinner) view.findViewById((R.id.default_voice));

        //Spinner default voice
        default_voice.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //TODO GESTIRE DEFAULT VOICE ALL'AVVIO
                if (default_voice.getSelectedItem().toString().compareTo("British English") == 0) tts.setVoice(voice_british_female);
                else if(default_voice.getSelectedItem().toString().compareTo("American English") == 0) tts.setVoice(voice_american_female);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        return view;
    }

    //Method used for BUG_REPORT and CONTACT_US Modules
    public void shareToMail(String[] email, String subject) {
        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.putExtra(Intent.EXTRA_EMAIL, email);
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
        //emailIntent.putExtra(Intent.EXTRA_TEXT, content);
        emailIntent.setType("text/plain");
        startActivity(emailIntent);
    }

    //Rate-Us Module
    public void rateUs(){
        Uri uri = Uri.parse("market://details?id=" + getActivity().getPackageName());
        Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
        // To count with Play market backstack, After pressing back button,
        // to taken back to our application, we need to add following flags to intent.
        goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                Intent.FLAG_ACTIVITY_NEW_DOCUMENT |
                Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
        try {
            startActivity(goToMarket);
        } catch (ActivityNotFoundException e) {
            startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("http://play.google.com/store/apps/details?id=" + getActivity().getPackageName())));
        }

    }

    public boolean delete_recordings() {
        //TODO DELETE ALL FILES IN THE FOLDER EXCEPT FOR .NOMEDIA FILE
        return true;
    }

}