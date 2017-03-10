package com.example.cesarsk.say_it;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;

import static com.example.cesarsk.say_it.Utility.loadRecordings;


/**
 * A simple {@link Fragment} subclass.
 */
public class RecordingsFragment extends Fragment {

    public RecordingsFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.fragment_recordings, container, false);

        Utility.loadFavs(getActivity());
        ArrayList<String> sortedRecordingsList = new ArrayList<>(Utility.loadRecordings());
        Collections.sort(sortedRecordingsList);

        final ListView listView = (ListView) view.findViewById(R.id.recordings_list);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, sortedRecordingsList);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1,int position, long arg3)
            {
                final Intent play_activity_intent = new Intent(getActivity(), PlayActivity.class);
                String selectedFromList = ((String)listView.getItemAtPosition(position));
                play_activity_intent.putExtra(PlayActivity.PLAY_WORD, selectedFromList);
                getActivity().startActivity(play_activity_intent);
                //Utility.addHist(getActivity(), selectedFromList);
            }
        });

        return view;
    }
}
