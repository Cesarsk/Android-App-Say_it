package com.example.cesarsk.say_it;


import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class SettingsFragment extends SlidingFragment {

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

}