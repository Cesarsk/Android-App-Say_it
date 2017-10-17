package com.cesarsk.say_it.ui.components;

import android.app.ActivityOptions;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.util.Pair;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.cesarsk.say_it.R;
import com.cesarsk.say_it.ui.activities.MainActivity;
import com.cesarsk.say_it.ui.activities.PlayActivity;
import com.cesarsk.say_it.ui.fragments.HistoryFragment;
import com.cesarsk.say_it.utility.SayItPair;
import com.cesarsk.say_it.utility.Utility;
import com.cesarsk.say_it.utility.UtilitySharedPrefs;

import java.util.ArrayList;

import static android.speech.tts.TextToSpeech.QUEUE_FLUSH;

/**
 * Created by Claudio on 05/05/2017.
 */
public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder> {

    public static final long UNDO_TIMEOUT = 3000; //Timeout prima che l'elemento venga cancellato definitivamente

    private HistoryFragment historyFragment;

    public ArrayList<SayItPair> getHistory() {
        return history;
    }

    public void setHistory(ArrayList<SayItPair> history) {
        this.history = history;
        notifyDataSetChanged();
    }

    public SayItPair getTemp_hist() {
        return temp_hist;
    }

    private SayItPair temp_hist;

    private ArrayList<SayItPair> history;

    public HistoryAdapter(HistoryFragment historyFragment, ArrayList<SayItPair> history_list) {
        this.historyFragment = historyFragment;

        history = history_list;
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

        View view = inflater.inflate(R.layout.list_item_history, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {

        holder.QuickPlayBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!historyFragment.isVolumeMuted()) {
                    if (MainActivity.DEFAULT_ACCENT.equals("0")) {
                        MainActivity.american_speaker_google.speak(holder.wordTextView.getText(), QUEUE_FLUSH, null, null);
                        if (MainActivity.isLoggingEnabled)
                            Log.i("DEFAULT - HISTORY", MainActivity.DEFAULT_ACCENT);
                    } else if (MainActivity.DEFAULT_ACCENT.equals("1")) {
                        MainActivity.british_speaker_google.speak(holder.wordTextView.getText(), QUEUE_FLUSH, null, null);
                        if (MainActivity.isLoggingEnabled)
                            Log.i("DEFAULT - HISTORY", MainActivity.DEFAULT_ACCENT);
                    }
                } else {
                    Toast toast = Toast.makeText(historyFragment.getActivity(), "Please turn the volume up", Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                }
            }
        });

        holder.wordTextView.setText(history.get(position).first);
        holder.IPATextView.setText(history.get(position).second);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Intent play_activity_intent = new Intent(historyFragment.getActivity(), PlayActivity.class);
                play_activity_intent.putExtra(PlayActivity.PLAY_WORD, holder.wordTextView.getText());
                play_activity_intent.putExtra(PlayActivity.PLAY_IPA, holder.IPATextView.getText());
                UtilitySharedPrefs.addHist(historyFragment.getActivity(), new SayItPair(holder.wordTextView.getText().toString(), holder.IPATextView.getText().toString()));
                historyFragment.getActivity().startActivity(play_activity_intent, ActivityOptions.makeSceneTransitionAnimation(historyFragment.getActivity()).toBundle());
            }
        });

        if (UtilitySharedPrefs.checkFavs(historyFragment.getActivity(), history.get(position).first))
            holder.AddtoFavsBtn.setColorFilter(Utility.setColorByTheme(R.attr.favoriteButton, historyFragment.getActivity()));

        holder.AddtoFavsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!UtilitySharedPrefs.checkFavs(historyFragment.getActivity(), history.get(holder.getAdapterPosition()).first)) {
                    UtilitySharedPrefs.addFavs(historyFragment.getActivity(), new Pair<>(holder.wordTextView.getText().toString(), holder.IPATextView.getText().toString()));
                    Toast.makeText(historyFragment.getActivity(), "Added to Favorites", Toast.LENGTH_SHORT).show();
                    holder.AddtoFavsBtn.setColorFilter(Utility.setColorByTheme(R.attr.favoriteButton, historyFragment.getActivity()));
                } else if (UtilitySharedPrefs.checkFavs(historyFragment.getActivity(), history.get(holder.getAdapterPosition()).first)) {
                    UtilitySharedPrefs.removeFavs(v.getContext(), history.get(holder.getAdapterPosition()));
                    Toast.makeText(historyFragment.getActivity(), "Removed from Favorites", Toast.LENGTH_SHORT).show();
                    holder.AddtoFavsBtn.setColorFilter(Utility.setColorByTheme(R.attr.primaryDark, historyFragment.getActivity()));
                }
            }
        });

        final FloatingActionButton fab = (FloatingActionButton) historyFragment.getActivity().findViewById(R.id.floating_button_history);
        //historyFragment.startTutorialPlayActivity(holder);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(historyFragment.getActivity())
                        .setTitle("Clear History")
                        .setMessage("Are you sure you want to clear your History?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                UtilitySharedPrefs.clearHistory(historyFragment.getActivity());
                                setHistory(HistoryFragment.loadDeserializedHistory(historyFragment.getActivity()));
                                Toast.makeText(historyFragment.getActivity(), "History Cleared!", Toast.LENGTH_SHORT).show();

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
        return history.size();
    }

    public void remove(int pos) {
        temp_hist = history.get(pos);

        UtilitySharedPrefs.removeHist(historyFragment.getActivity(), new SayItPair(history.get(pos).first, history.get(pos).second, history.get(pos).getAdding_time()));
        history = HistoryFragment.loadDeserializedHistory(historyFragment.getActivity());
        notifyItemRemoved(pos);
    }

}
