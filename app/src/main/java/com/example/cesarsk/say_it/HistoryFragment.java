package com.example.cesarsk.say_it;


import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;


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
        public View getView(int i, View view, ViewGroup viewGroup) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            final HistoryElementViewHolder ViewHolder;

            if(view == null){

                ViewHolder = new HistoryElementViewHolder();
                view = inflater.inflate(R.layout.search_results_list_item, viewGroup, false);
                ViewHolder.wordTextView = (TextView) view.findViewById(R.id.result_first_line);
                ViewHolder.IPATextView = (TextView) view.findViewById(R.id.result_second_line);
                ViewHolder.QuickPlayBtn = (ImageButton) view.findViewById(R.id.quick_play_listbutton);
                ViewHolder.DeleteFromHistoryBtn = (ImageButton) view.findViewById(R.id.add_to_favs_button);

                view.setTag(ViewHolder);
            }
            else {
                ViewHolder = (HistoryElementViewHolder) view.getTag();
            }

            String word = history.get(i).first;
            String ipa = history.get(i).second;
            ViewHolder.wordTextView.setText(word);
            ViewHolder.IPATextView.setText(ipa);

            //TODO Listener pulsanti

            return view;
        }
    }

    private static class HistoryElementViewHolder{
        TextView wordTextView;
        TextView IPATextView;
        ImageButton QuickPlayBtn;
        ImageButton DeleteFromHistoryBtn;
    }
}
