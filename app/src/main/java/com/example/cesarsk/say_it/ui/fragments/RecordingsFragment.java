package com.example.cesarsk.say_it.ui.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.example.cesarsk.say_it.ui.PlayActivity;
import com.example.cesarsk.say_it.R;
import com.example.cesarsk.say_it.utility.UtilityRecord;

import java.util.ArrayList;
import java.util.Collections;


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

        ArrayList<String> sortedRecordingsList = new ArrayList<>(UtilityRecord.loadRecordings());
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

    private class RecordingsAdapter extends RecyclerView.Adapter<RecordingsAdapter.ViewHolder> {

        private ArrayList<String> recordings;

        public RecordingsAdapter(ArrayList<String> recordings_list) {
            recordings = recordings_list;
        }

        class ViewHolder extends RecyclerView.ViewHolder {

            TextView wordTextView;
            TextView IPATextView;
            ImageButton QuickPlayBtn;
            ImageButton DeleteRecording;

            ViewHolder(View itemView) {
                super(itemView);
                wordTextView = (TextView) itemView.findViewById(R.id.list_item_first_line);
                IPATextView = (TextView) itemView.findViewById(R.id.list_item_second_line);
                QuickPlayBtn = (ImageButton) itemView.findViewById(R.id.list_item_quickplay);
                DeleteRecording = (ImageButton) itemView.findViewById(R.id.list_item_deleteRecording);
            }
        }

        @Override
        public RecordingsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            LayoutInflater inflater = LayoutInflater.from(parent.getContext());

            View view = inflater.inflate(R.layout.list_item_generic, parent, false);

            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final RecordingsAdapter.ViewHolder holder, int position) {

            /*
            holder.wordTextView.setText(recordings.get(position).first);
            holder.IPATextView.setText(recordings.get(position).second);
            */

            holder.QuickPlayBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
        }

        @Override
        public int getItemCount() {
            return recordings.size();
        }
    }
}
