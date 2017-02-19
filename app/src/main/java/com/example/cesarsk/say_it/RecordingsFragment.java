package com.example.cesarsk.say_it;


import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Collections;

import static com.example.cesarsk.say_it.Utility.loadRecordings;


/**
 * A simple {@link Fragment} subclass.
 */
public class RecordingsFragment extends SlidingFragment {


    public RecordingsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.fragment_recordings, container, false);

        Utility.loadFavs(getActivity());
        ArrayList<String> sortedRecordingsList = new ArrayList<>(Utility.loadRecordings());
        Collections.sort(sortedRecordingsList);

        ListView listView = (ListView) view.findViewById(R.id.recordings_list);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, sortedRecordingsList);
        listView.setAdapter(adapter);

        return view;
    }
}
