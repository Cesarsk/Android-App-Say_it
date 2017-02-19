package com.example.cesarsk.say_it;


import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

        ListView listView = (ListView) view.findViewById(R.id.history_list);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, sortedHistoryList);
        listView.setAdapter(adapter);

        return view;
    }
}
