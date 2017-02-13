package com.example.cesarsk.say_it;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.app.FragmentManager;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import static android.speech.tts.TextToSpeech.QUEUE_ADD;
import static com.example.cesarsk.say_it.MainActivity.tts;
import static java.sql.DriverManager.println;

/**
 * Created by Claffo on 08/02/2017.
 */

public class ResultsListCustomAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<String> results;
    private int PlayButtonIconID;
    private int AddToFavsButtonIconID;
    private static ArrayList<CharSequence> favorites;
    //TODO SOSTITUIRE CON BUNDLE FRAGMENT
    static CharSequence selected_word_charseq;

    public ResultsListCustomAdapter(Context context, ArrayList<String> results, int PlayButtonIconID, int AddToFavsButtonIconID){
        this.context = context;
        this.results = results;
        this.PlayButtonIconID = PlayButtonIconID;
        this.AddToFavsButtonIconID = AddToFavsButtonIconID;
    }

    @Override
    public int getCount() {
        return results.size();
    }

    @Override
    public Object getItem(int position) {
        return results.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final FragmentManager fragmentManager= ((Activity)context).getFragmentManager();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View view = inflater.inflate(R.layout.fragment_settings,
                null, false);

        if(convertView == null) {
            convertView = inflater.inflate(R.layout.search_results_list_item, parent, false);
            final TextView word = (TextView) convertView.findViewById(R.id.Result_TextView);
            word.setText(results.get(position));

            word.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    selected_word_charseq = word.getText();
                    fragmentManager.beginTransaction().replace(R.id.fragment_container, newInstance(word.getText())).commit();
                }
            });

            //Pulsante QUICK PLAY
            ImageButton play_button = (ImageButton) convertView.findViewById(R.id.play_button);
            play_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Cliccando su Play Button nella search result tab riproduce play.
                    tts.speak(word.getText(), QUEUE_ADD, null, null);
                }
            });

            //Pulsante FAV
            ImageButton add_to_favs_button = (ImageButton) convertView.findViewById(R.id.add_to_favs_button);
            add_to_favs_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //TODO Inserisci ai preferiti
                    favorites.add(word.getText()); //Da continuare
                }
            });



        }

        return convertView;
    }

    //NON DEFAULT CONSTRUCTOR NON ACCEPTED BY FRAGMENTS, USED A STATIC VARIABLE INSTEAD
    public static PlayFragment newInstance(CharSequence word) {
        PlayFragment myFragment = new PlayFragment();

        Bundle args = new Bundle();
        args.putString("word",word.toString());
        myFragment.setArguments(args);

        return myFragment;
    }

}
