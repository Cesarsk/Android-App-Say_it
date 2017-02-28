package com.example.cesarsk.say_it;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import static android.speech.tts.TextToSpeech.QUEUE_ADD;
import static com.example.cesarsk.say_it.MainActivity.WordList;
import static com.example.cesarsk.say_it.MainActivity.tts;

/**
 * Created by Claffo on 08/02/2017.
 */

public class ResultsListCustomAdapter extends BaseAdapter implements Filterable {

    private Context context;
    private ArrayList<String> results;
    SearchResultsFilter resultsFilter;

    public ResultsListCustomAdapter(Context context, ArrayList<String> results){
        this.context = context;
        this.results = results;
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

        if(convertView == null) {
            convertView = inflater.inflate(R.layout.search_results_list_item, parent, false);
            final TextView word = (TextView) convertView.findViewById(R.id.Result_TextView);
            word.setText(results.get(position));

            word.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final Intent play_activity_intent = new Intent(context, PlayActivity.class);
                    play_activity_intent.putExtra(PlayActivity.PLAY_WORD, word.getText());
                    Utility.addHist(context, word.getText().toString());
                    context.startActivity(play_activity_intent, ActivityOptions.makeSceneTransitionAnimation((Activity)context).toBundle());
                }
            });

            //Pulsante QUICK PLAY
            ImageButton play_button = (ImageButton) convertView.findViewById(R.id.quick_play_button);
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
                    Utility.addFavs(context, word.getText().toString());
                    Toast.makeText(context, "Added to favorites", Toast.LENGTH_SHORT).show();
                }
            });

        }

        return convertView;
    }

    @Override
    public Filter getFilter() {

        if(resultsFilter == null){
            resultsFilter = new SearchResultsFilter();
        }

        return resultsFilter;
    }

    private class SearchResultsFilter extends Filter{

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            FilterResults filterResults = new FilterResults();

            ArrayList<String> found = new ArrayList<>();

            if(constraint != null){
                for(String word:WordList){
                    if(word.contains(constraint.toString().toLowerCase())){
                        found.add(word);
                    }
                }
            }

            filterResults.values = found;
            filterResults.count = found.size();

            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults filterResults) {
            results.clear();
            results.addAll((ArrayList<String>) filterResults.values);
            notifyDataSetChanged();
        }
    }

}