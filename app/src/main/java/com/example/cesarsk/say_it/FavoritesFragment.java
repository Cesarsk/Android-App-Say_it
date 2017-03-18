package com.example.cesarsk.say_it;


import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.util.Pair;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.LinearLayout;
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
public class FavoritesFragment extends Fragment {

    ArrayList<Pair<String, String>> DeserializedFavs;

    public FavoritesFragment() {
        // Required empty public constructor
    }

    public static ArrayList<Pair<String, String>> loadDeserializedFavs(Context context) {

        Utility.loadFavs(context);
        ArrayList<String> SerializedFavs = new ArrayList<>(MainActivity.FAVORITES);
        ArrayList<Pair<String, String>> DeserializedFavs = new ArrayList<>();
        Gson gson = new Gson();
        for (String element : SerializedFavs) {
            SayItPair pair = gson.fromJson(element, SayItPair.class);
            DeserializedFavs.add(pair);
        }

        Collections.sort(DeserializedFavs, new Comparator<Pair<String, String>>() {
            @Override
            public int compare(Pair<String, String> pair1, Pair<String, String> pair2) {
                return pair1.first.compareTo(pair2.first);
            }
        });

        return DeserializedFavs;

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.fragment_favorites, container, false);

        DeserializedFavs = loadDeserializedFavs(getActivity());

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.favorites_list);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        DefaultItemAnimator defaultItemAnimator = new DefaultItemAnimator();
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getActivity(),linearLayoutManager.getOrientation());

        final FavoritesAdapter adapter = new FavoritesAdapter(DeserializedFavs);
        recyclerView.setAdapter(adapter);

        ItemTouchHelper touchHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                Utility.removeFavs(getActivity(), adapter.getFavorites().get(viewHolder.getAdapterPosition()));
                adapter.setFavorites(loadDeserializedFavs(getActivity()));
                adapter.notifyItemRemoved(viewHolder.getAdapterPosition());
            }
        });
        recyclerView.addItemDecoration(dividerItemDecoration);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setItemAnimator(defaultItemAnimator);
        touchHelper.attachToRecyclerView(recyclerView);

        return view;
    }

    private class FavoritesAdapter extends RecyclerView.Adapter<FavoritesAdapter.ViewHolder> {

        public ArrayList<Pair<String, String>> getFavorites() {
            return favorites;
        }

        public void setFavorites(ArrayList<Pair<String, String>> favorites) {
            this.favorites = favorites;
        }

        private ArrayList<Pair<String, String>> favorites;

        FavoritesAdapter(ArrayList<Pair<String, String>> favorites_list) {
            favorites = favorites_list;
        }

        class ViewHolder extends RecyclerView.ViewHolder {

            TextView wordTextView;
            TextView IPATextView;
            ImageButton QuickPlayBtn;
            ImageButton AddtoFavsBtn;

            ViewHolder(View itemView) {
                super(itemView);
                wordTextView = (TextView) itemView.findViewById(R.id.list_item_first_line);
                IPATextView = (TextView) itemView.findViewById(R.id.list_item_second_line);
                QuickPlayBtn = (ImageButton) itemView.findViewById(R.id.list_item_quickplay);
                AddtoFavsBtn = (ImageButton) itemView.findViewById(R.id.list_item_addToFavs);
            }
        }

        @Override
        public FavoritesAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            LayoutInflater inflater = LayoutInflater.from(parent.getContext());

            View view = inflater.inflate(R.layout.list_item_generic, parent, false);

            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final FavoritesAdapter.ViewHolder holder, int position) {

            holder.wordTextView.setText(favorites.get(position).first);
            holder.IPATextView.setText(favorites.get(position).second);

            holder.QuickPlayBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Cliccando su Play Button nella search result tab riproduce play.
                    american_speaker_google.speak(holder.wordTextView.getText(), QUEUE_FLUSH, null, null);
                }
            });

            final boolean favorite_flag = Utility.checkFavs(getActivity(), favorites.get(position).first);
            if (favorite_flag)
                holder.AddtoFavsBtn.setColorFilter(getResources().getColor(R.color.RudolphsNose));

            holder.AddtoFavsBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!favorite_flag) {
                        Utility.addFavs(getActivity(), new Pair<>(holder.wordTextView.getText().toString(), holder.IPATextView.getText().toString()));
                        Toast.makeText(getActivity(), "Added to Favorites", Toast.LENGTH_SHORT).show();
                    }

                    if (favorite_flag) {
                        Utility.removeFavs(v.getContext(), favorites.get(holder.getAdapterPosition()));
                        favorites = loadDeserializedFavs(getActivity());
                        notifyItemRemoved(holder.getAdapterPosition());
                        Toast.makeText(getActivity(), "Removed from Favorites", Toast.LENGTH_SHORT).show();
                    }
                }
            });

        }

        @Override
        public int getItemCount() {
            return favorites.size();
        }
    }
}
