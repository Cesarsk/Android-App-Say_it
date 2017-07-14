package com.cesarsk.say_it.ui.components;

import android.app.ActivityOptions;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v4.util.Pair;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.cesarsk.say_it.R;
import com.cesarsk.say_it.ui.MainActivity;
import com.cesarsk.say_it.ui.PlayActivity;
import com.cesarsk.say_it.ui.fragments.RecordingsFragment;
import com.cesarsk.say_it.utility.SayItPair;
import com.cesarsk.say_it.utility.Utility;
import com.cesarsk.say_it.utility.UtilityDictionary;
import com.cesarsk.say_it.utility.UtilityRecordings;
import com.cesarsk.say_it.utility.UtilitySharedPrefs;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by Claudio on 05/05/2017.
 */
public class RecordingsAdapter extends RecyclerView.Adapter<RecordingsAdapter.ViewHolder> {
    public static final long UNDO_TIMEOUT = 3000;

    private RecordingsFragment recordingsFragment;

    public ArrayList<File> getRecordings() {
        return recordings;
    }

    public void setRecordings(ArrayList<File> recordings) {
        this.recordings = recordings;
        notifyDataSetChanged();
    }

    private ArrayList<File> recordings;
    private final MediaPlayer mediaPlayer = new MediaPlayer();

    public File getTemp_rec_file() {
        return temp_rec_file;
    }

    public byte[] getTemp_rec_bytes() {
        return temp_rec_bytes;
    }

    private File temp_rec_file;
    private byte[] temp_rec_bytes;

    public RecordingsAdapter(RecordingsFragment recordingsFragment, ArrayList<File> recordings_list) {
        this.recordingsFragment = recordingsFragment;
        recordings = recordings_list;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public final TextView wordTextView;
        final TextView IPATextView;
        final ImageButton QuickPlayBtn;
        final ImageButton AddtoFavsBtn;

        ViewHolder(View itemView) {
            super(itemView);
            wordTextView = (TextView) itemView.findViewById(R.id.list_item_first_line);
            IPATextView = (TextView) itemView.findViewById(R.id.list_item_second_line);
            QuickPlayBtn = (ImageButton) itemView.findViewById(R.id.list_item_quickplay);
            AddtoFavsBtn = (ImageButton) itemView.findViewById(R.id.list_item_addToFavs);

        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        View view = inflater.inflate(R.layout.list_item_recordings, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {

        final String recordingName = (recordings.get(position).getName()).substring(0, recordings.get(position).getName().lastIndexOf('.'));

        holder.wordTextView.setText(recordingName);
        holder.IPATextView.setText(UtilityDictionary.getIPAfromWord(recordingName));

        holder.QuickPlayBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!recordingsFragment.isVolumeMuted()) {
                    UtilityRecordings.playRecording(recordingsFragment.getActivity(), mediaPlayer, recordingName + ".aac");
                } else {
                    Toast toast = Toast.makeText(recordingsFragment.getActivity(), "Please turn the volume up", Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                }
            }
        });

        if (UtilitySharedPrefs.checkFavs(recordingsFragment.getActivity(), holder.wordTextView.getText().toString()))
            holder.AddtoFavsBtn.setColorFilter(ContextCompat.getColor(recordingsFragment.getActivity(), R.color.RudolphsNose));

        holder.AddtoFavsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!UtilitySharedPrefs.checkFavs(recordingsFragment.getActivity(), holder.wordTextView.getText().toString())) {
                    UtilitySharedPrefs.addFavs(recordingsFragment.getActivity(), new Pair<>(holder.wordTextView.getText().toString(), holder.IPATextView.getText().toString()));
                    Toast.makeText(recordingsFragment.getActivity(), "Added to Favorites", Toast.LENGTH_SHORT).show();
                    holder.AddtoFavsBtn.setColorFilter(ContextCompat.getColor(recordingsFragment.getActivity(), R.color.RudolphsNose));
                } else if (UtilitySharedPrefs.checkFavs(recordingsFragment.getActivity(), holder.wordTextView.getText().toString())) {
                    UtilitySharedPrefs.removeFavs(v.getContext(), new Pair<>(holder.wordTextView.getText().toString(), holder.IPATextView.getText().toString()));
                    Toast.makeText(recordingsFragment.getActivity(), "Removed from Favorites", Toast.LENGTH_SHORT).show();
                    holder.AddtoFavsBtn.setColorFilter(ContextCompat.getColor(recordingsFragment.getActivity(), R.color.primary_dark));
                }
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Intent play_activity_intent = new Intent(recordingsFragment.getActivity(), PlayActivity.class);
                play_activity_intent.putExtra(PlayActivity.PLAY_WORD, holder.wordTextView.getText());
                play_activity_intent.putExtra(PlayActivity.PLAY_IPA, holder.IPATextView.getText());
                UtilitySharedPrefs.addHist(recordingsFragment.getActivity(), new SayItPair(holder.wordTextView.getText().toString(), holder.IPATextView.getText().toString()));
                recordingsFragment.getActivity().startActivity(play_activity_intent, ActivityOptions.makeSceneTransitionAnimation(recordingsFragment.getActivity()).toBundle());
            }
        });

        final FloatingActionButton fab = (FloatingActionButton) recordingsFragment.getActivity().findViewById(R.id.floating_button_recordings);
        //recordingsFragment.startTutorialPlayActivity(holder);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(recordingsFragment.getActivity())
                        .setTitle("Delete Recordings")
                        .setMessage("Are you sure you want to delete your recordings?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                Utility.delete_recordings(recordingsFragment.getActivity());
                                UtilityRecordings.updateRecordings(recordingsFragment.getActivity());
                                setRecordings(MainActivity.RECORDINGS);
                                Toast.makeText(recordingsFragment.getActivity(), "Recordings Deleted!", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                //do nothing
                            }
                        })
                        .show();
            }
        });

    }

    @Override
    public int getItemCount() {
        return recordings.size();
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public void remove(int pos) {
        temp_rec_file = recordings.get(pos);
        temp_rec_bytes = UtilityRecordings.getRecordingBytesfromFile(recordings.get(pos));
        recordings.get(pos).delete();
        recordings.remove(pos);
        recordings = UtilityRecordings.loadRecordingsfromStorage(recordingsFragment.getActivity());
        Collections.sort(recordings);
        notifyItemRemoved(pos);
    }
}
