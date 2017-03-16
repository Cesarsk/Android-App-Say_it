package com.example.cesarsk.say_it;


import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import static android.speech.tts.TextToSpeech.QUEUE_FLUSH;
import static com.example.cesarsk.say_it.MainActivity.american_speaker_google;


/**
 * A simple {@link Fragment} subclass.
 */
public class HistoryFragment extends Fragment {


    public HistoryFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_history, container, false);

        Utility.loadHist(getActivity());
        ArrayList<String> SerializedHistory = new ArrayList<>(MainActivity.HISTORY);
        ArrayList<SayItPair> DeserializedHistory = new ArrayList<>();
        Gson gson = new Gson();

        for(String element : SerializedHistory){
            SayItPair pair = gson.fromJson(element, SayItPair.class);
            DeserializedHistory.add(pair);
        }

        Collections.sort(DeserializedHistory, new Comparator<SayItPair>() {
            @Override
            public int compare(SayItPair pair1, SayItPair pair2) {
                return pair2.getAdding_time().compareTo(pair1.getAdding_time());
            }
        });

        final ListView listView = (ListView) view.findViewById(R.id.history_list);
        HistoryListAdapter adapter = new HistoryListAdapter(getActivity(), DeserializedHistory);
        listView.setAdapter(adapter);

        return view;
    }

    private class HistoryListAdapter extends BaseAdapter {

        private Context context;
        private ArrayList<SayItPair> history;

        public HistoryListAdapter(Context context, ArrayList<SayItPair> history){
            this.context = context;
            this.history = history;
        }

        @Override
        public int getCount() {
            return history.size();
        }

        @Override
        public Object getItem(int i) {
            return history.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(final int i, View view, ViewGroup viewGroup) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            final HistoryElementViewHolder ViewHolder;

            if(view == null){

                ViewHolder = new HistoryElementViewHolder();
                view = inflater.inflate(R.layout.list_item_history, viewGroup, false);
                ViewHolder.wordTextView = (TextView) view.findViewById(R.id.list_item_first_line);
                ViewHolder.IPATextView = (TextView) view.findViewById(R.id.list_item_second_line);
                ViewHolder.QuickPlayBtn = (ImageButton) view.findViewById(R.id.list_item_quickplay);
                ViewHolder.AddtoFavsBtn = (ImageButton) view.findViewById(R.id.list_item_addToFavs);
                ViewHolder.DeleteFromHistoryBtn = (ImageButton) view.findViewById(R.id.list_item_removeFromHistory);

                view.setTag(ViewHolder);
            }
            else {
                ViewHolder = (HistoryElementViewHolder) view.getTag();
            }

            String word = history.get(i).first;
            String ipa = history.get(i).second;
            ViewHolder.wordTextView.setText(word);
            ViewHolder.IPATextView.setText(ipa);

            ViewHolder.QuickPlayBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Cliccando su Play Button nella search result tab riproduce play.
                    american_speaker_google.speak(ViewHolder.wordTextView.getText(), QUEUE_FLUSH, null, null);
                }
            });

            final boolean favorite_flag = Utility.checkFavs(getActivity(), history.get(i).first);
            if(favorite_flag) ViewHolder.AddtoFavsBtn.setColorFilter(getResources().getColor(R.color.RudolphsNose));

            ViewHolder.AddtoFavsBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(!favorite_flag) {
                        Utility.addFavs(context, new Pair<>(ViewHolder.wordTextView.getText().toString(), ViewHolder.IPATextView.getText().toString()));
                        Toast.makeText(context, "Added to Favorites", Toast.LENGTH_SHORT).show();
                    }

                    if(favorite_flag){
                        Utility.removeFavs(v.getContext(), history.get(i));
                        Toast.makeText(context, "Removed from Favorites", Toast.LENGTH_SHORT).show();
                    }
                }
            });

            ViewHolder.DeleteFromHistoryBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Utility.removeHist(view.getContext(), new SayItPair(history.get(i).first, history.get(i).second, history.get(i).getAdding_time()));
                    Toast.makeText(context, "Removed from History", Toast.LENGTH_SHORT).show();
                }
            });

            return view;
        }
    }

    private static class HistoryElementViewHolder{
        TextView wordTextView;
        TextView IPATextView;
        ImageButton QuickPlayBtn;
        ImageButton AddtoFavsBtn;
        ImageButton DeleteFromHistoryBtn;
    }
}
