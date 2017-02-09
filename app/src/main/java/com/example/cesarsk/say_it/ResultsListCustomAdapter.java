package com.example.cesarsk.say_it;

import android.content.Context;
import android.speech.tts.TextToSpeech;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;

import static android.speech.tts.TextToSpeech.QUEUE_ADD;
import static com.example.cesarsk.say_it.MainActivity.tts;
import static java.sql.DriverManager.println;

/**
 * Created by Claudio on 08/02/2017.
 */

public class ResultsListCustomAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<String> results;
    private int PlayButtonIconID;
    private int AddToFavsButtonIconID;

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

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View view = inflater.inflate(R.layout.fragment_settings,
                null, false);

        if(convertView == null) {
            convertView = inflater.inflate(R.layout.search_results_list_item, parent, false);
            final TextView word = (TextView) convertView.findViewById(R.id.Result_TextView);
            ImageButton play_button = (ImageButton) convertView.findViewById(R.id.play_button);
            play_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Cliccando su Play Button nella search result tab riproduce play.
                    tts.speak(word.getText(), QUEUE_ADD, null, null);
                }
            });

            ImageButton add_to_favs_button = (ImageButton) convertView.findViewById(R.id.add_to_favs_button);

            word.setText(results.get(position));
        }

        return convertView;
    }
}
