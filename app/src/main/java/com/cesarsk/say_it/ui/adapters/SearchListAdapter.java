package com.cesarsk.say_it.ui.adapters;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.util.Pair;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cesarsk.say_it.ui.MainActivity;
import com.cesarsk.say_it.R;
import com.cesarsk.say_it.ui.PlayActivity;

import com.cesarsk.say_it.utility.SayItPair;

import com.cesarsk.say_it.utility.UtilitySharedPrefs;

import java.util.ArrayList;
import java.util.Calendar;

import static android.speech.tts.TextToSpeech.QUEUE_FLUSH;

/**
 * Created by Claffo on 08/02/2017.
 */

@SuppressWarnings("ALL")
public class SearchListAdapter extends BaseAdapter implements Filterable {

    private Context context;
    private ArrayList<Pair<String, String>> results;
    private SearchResultsFilter resultsFilter;
    private AudioManager audio;

    public SearchListAdapter(Context context) {
        this.context = context;
        results = new ArrayList<>();
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

        //Get audio service
        audio = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);

        final SearchResultViewHolder viewHolder;

        if (convertView == null) {

            viewHolder = new SearchResultViewHolder();
            convertView = inflater.inflate(R.layout.list_item_generic, parent, false);
            viewHolder.wordTextView = (TextView) convertView.findViewById(R.id.list_item_first_line);
            viewHolder.ipaTextView = (TextView) convertView.findViewById(R.id.list_item_second_line);
            viewHolder.quickPlayImgButton = (ImageButton) convertView.findViewById(R.id.list_item_quickplay);
            viewHolder.addToFavsImgButton = (ImageButton) convertView.findViewById(R.id.list_item_addToFavs);
            viewHolder.textLayout = (LinearLayout) convertView.findViewById(R.id.list_item_generic_layout);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (SearchResultViewHolder) convertView.getTag();
        }

        String current_word = results.get(position).first.substring(0,1).toUpperCase() + results.get(position).first.substring(1);
        viewHolder.wordTextView.setText(current_word);

        String current_ipa = results.get(position).second;
        viewHolder.ipaTextView.setText(current_ipa);

        viewHolder.textLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Intent play_activity_intent = new Intent(context, PlayActivity.class);
                play_activity_intent.putExtra(PlayActivity.PLAY_WORD, viewHolder.wordTextView.getText());
                play_activity_intent.putExtra(PlayActivity.PLAY_IPA, viewHolder.ipaTextView.getText());
                UtilitySharedPrefs.addHist(context, new SayItPair(viewHolder.wordTextView.getText().toString(), viewHolder.ipaTextView.getText().toString(), Calendar.getInstance().getTime()));
                context.startActivity(play_activity_intent, ActivityOptions.makeSceneTransitionAnimation((Activity) context).toBundle());
            }
        });

        //Pulsante QUICK PLAY
        viewHolder.quickPlayImgButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isVolumeMuted()) {
                    if(MainActivity.DEFAULT_ACCENT.equals("0")) {
                        MainActivity.american_speaker_google.setVoice(MainActivity.voice_american_female);
                        MainActivity.american_speaker_google.speak(viewHolder.wordTextView.getText(), QUEUE_FLUSH, null, null);
                        if(MainActivity.isLoggingEnabled) Log.i("DEFAULT - SEARCHLIST", MainActivity.DEFAULT_ACCENT);
                    }
                    else if(MainActivity.DEFAULT_ACCENT.equals("1")) {
                        MainActivity.british_speaker_google.setVoice(MainActivity.voice_british_female);
                        MainActivity.british_speaker_google.speak(viewHolder.wordTextView.getText(),QUEUE_FLUSH,null,null);
                        if(MainActivity.isLoggingEnabled) Log.i("DEFAULT - SEARCHLIST", MainActivity.DEFAULT_ACCENT);
                    }
                } else {
                    Toast toast = Toast.makeText(context, "Please turn the volume up", Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                }
            }
        });

        //Pulsante FAV
        if (UtilitySharedPrefs.checkFavs(context, viewHolder.wordTextView.getText().toString()))
            viewHolder.addToFavsImgButton.setColorFilter(ContextCompat.getColor(context, R.color.Red700));

        viewHolder.addToFavsImgButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!UtilitySharedPrefs.checkFavs(context, viewHolder.wordTextView.getText().toString())) {
                    UtilitySharedPrefs.addFavs(context, new Pair<>(viewHolder.wordTextView.getText().toString(), viewHolder.ipaTextView.getText().toString()));
                    Toast.makeText(context, "Added to Favorites", Toast.LENGTH_SHORT).show();
                    viewHolder.addToFavsImgButton.setColorFilter(ContextCompat.getColor(context, R.color.Red700));
                }

                else if(UtilitySharedPrefs.checkFavs(context, viewHolder.wordTextView.getText().toString())) {
                    UtilitySharedPrefs.removeFavs(v.getContext(), new Pair<>(viewHolder.wordTextView.getText().toString(), viewHolder.ipaTextView.getText().toString()));
                    Toast.makeText(context, "Removed from Favorites", Toast.LENGTH_SHORT).show();
                    viewHolder.addToFavsImgButton.setColorFilter(ContextCompat.getColor(context, R.color.Blue800));
                }
            }
        });

        return convertView;
    }

    @Override
    public Filter getFilter() {

        if (resultsFilter == null) {
            resultsFilter = new SearchResultsFilter();
        }

        return resultsFilter;
    }

    private class SearchResultsFilter extends Filter {

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            FilterResults filterResults = new FilterResults();

            ArrayList<Pair<String, String>> temp_list = null;
            ArrayList<Pair<String, String>> found = new ArrayList<>();

            if (constraint != null) {
                if (!(constraint.toString().isEmpty())) {
                    temp_list = MainActivity.Wordlists_Map.get(constraint.toString().substring(0,1).toLowerCase());

                    if(temp_list != null){
                        for(Pair<String, String> element : temp_list){
                            if(element.first.startsWith(constraint.toString().toLowerCase())){
                                found.add(element);
                            }
                        }
                    }
                }
            }

            filterResults.values = found;
            filterResults.count = found.size();

            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults filterResults) {
            if (filterResults.count > 0) {
                results.clear();
                results.addAll((ArrayList<Pair<String, String>>) filterResults.values);
                notifyDataSetChanged();
            } else {
                results.clear();
                notifyDataSetInvalidated();
            }
        }
    }

    private static class SearchResultViewHolder {
        TextView wordTextView;
        TextView ipaTextView;
        LinearLayout textLayout;
        ImageButton quickPlayImgButton;
        ImageButton addToFavsImgButton;
    }

    private boolean isVolumeMuted() {
        int currentVolume = audio.getStreamVolume(AudioManager.STREAM_MUSIC);
        if (currentVolume == 0) return true;
        else return false;
    }
}