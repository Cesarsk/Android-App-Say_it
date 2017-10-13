package com.cesarsk.say_it.ui.components;

import android.app.ActivityOptions;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
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
import com.cesarsk.say_it.ui.activities.MainActivity;
import com.cesarsk.say_it.ui.activities.PlayActivity;
import com.cesarsk.say_it.ui.fragments.FavoritesFragment;
import com.cesarsk.say_it.utility.SayItPair;
import com.cesarsk.say_it.utility.UtilitySharedPrefs;

import java.util.ArrayList;

import static android.speech.tts.TextToSpeech.QUEUE_FLUSH;

/**
 * Created by Claudio on 05/05/2017.
 */
public class FavoritesAdapter extends RecyclerView.Adapter<FavoritesAdapter.ViewHolder> {

    public static final long UNDO_TIMEOUT = 3000; //Timeout prima che l'elemento venga cancellato definitivamente

    private FavoritesFragment favoritesFragment;
    private ArrayList<Pair<String, String>> favorites;

    public Pair<String, String> getTemp_fav() {
        return temp_fav;
    }

    private Pair<String, String> temp_fav;

    public FavoritesAdapter(FavoritesFragment favoritesFragment, ArrayList<Pair<String, String>> favorites_list) {
        this.favoritesFragment = favoritesFragment;
        favorites = favorites_list;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public final TextView wordTextView;
        final TextView IPATextView;
        final ImageButton QuickPlayBtn;

        ViewHolder(View itemView) {
            super(itemView);
            wordTextView = (TextView) itemView.findViewById(R.id.list_item_first_line);
            IPATextView = (TextView) itemView.findViewById(R.id.list_item_second_line);
            QuickPlayBtn = (ImageButton) itemView.findViewById(R.id.list_item_quickplay);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        View view = inflater.inflate(R.layout.list_item_favorites, parent, false);

        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.QuickPlayBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!favoritesFragment.isVolumeMuted()) {
                    //Cliccando su Play Button nella search result tab riproduce play.
                    if (MainActivity.DEFAULT_ACCENT.equals("0")) {
                        MainActivity.american_speaker_google.speak(holder.wordTextView.getText(), QUEUE_FLUSH, null, null);

                    } else if (MainActivity.DEFAULT_ACCENT.equals("1")) {
                        MainActivity.british_speaker_google.speak(holder.wordTextView.getText(), QUEUE_FLUSH, null, null);

                    }
                } else {
                    Toast toast = Toast.makeText(favoritesFragment.getActivity(), "Please turn the volume up", Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                }
            }
        });

        holder.wordTextView.setText(favorites.get(position).first);
        holder.IPATextView.setText(favorites.get(position).second);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Intent play_activity_intent = new Intent(favoritesFragment.getActivity(), PlayActivity.class);
                play_activity_intent.putExtra(PlayActivity.PLAY_WORD, holder.wordTextView.getText());
                play_activity_intent.putExtra(PlayActivity.PLAY_IPA, holder.IPATextView.getText());
                UtilitySharedPrefs.addHist(favoritesFragment.getActivity(), new SayItPair(holder.wordTextView.getText().toString(), holder.IPATextView.getText().toString()));
                favoritesFragment.getActivity().startActivity(play_activity_intent, ActivityOptions.makeSceneTransitionAnimation(favoritesFragment.getActivity()).toBundle());
            }
        });

        final FloatingActionButton fab = (FloatingActionButton) favoritesFragment.getActivity().findViewById(R.id.floating_button_history);
        //favoritesFragment.startTutorialPlayActivity(holder);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(favoritesFragment.getActivity())
                        .setTitle("Clear Favorites")
                        .setMessage("Are you sure you want to clear your Favorites?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                //Clear Favorites
                                UtilitySharedPrefs.clearFavorites(favoritesFragment.getActivity());
                                setFavorites(FavoritesFragment.loadDeserializedFavs(favoritesFragment.getActivity()));
                                Toast.makeText(favoritesFragment.getActivity(), "Favorites Cleared!", Toast.LENGTH_SHORT).show();

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
        return favorites.size();
    }

    public void remove(int pos) {

        temp_fav = favorites.get(pos);

        UtilitySharedPrefs.removeFavs(favoritesFragment.getActivity(), favorites.get(pos));
        favorites = FavoritesFragment.loadDeserializedFavs(favoritesFragment.getActivity());
        notifyItemRemoved(pos);
    }

    public ArrayList<Pair<String, String>> getFavorites() {
        return favorites;
    }

    public void setFavorites(ArrayList<Pair<String, String>> favorites) {
        this.favorites = favorites;
        notifyDataSetChanged();
    }
}
