package com.example.cesarsk.say_it;


import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Collections;


/**
 * A simple {@link Fragment} subclass.
 */
public class HistoryFragment extends SlidingFragment {


    public HistoryFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_history, container, false);

        Utility.loadHist(getActivity());
        ArrayList<String> sortedHistoryList = new ArrayList<>(MainActivity.HISTORY);
        Collections.sort(sortedHistoryList);

        final ListView listView = (ListView) view.findViewById(R.id.history_list);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, sortedHistoryList);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1,int position, long arg3)
            {
                final Intent play_activity_intent = new Intent(getActivity(), PlayActivity.class);
                String selectedFromList = ((String)listView.getItemAtPosition(position));
                play_activity_intent.putExtra(PlayActivity.PLAY_WORD, selectedFromList);
                getActivity().startActivity(play_activity_intent);
                Utility.addHist(getActivity(), selectedFromList);
            }
        });


        return view;
    }
}
