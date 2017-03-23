package com.example.cesarsk.say_it.ui.fragments;


import android.app.Fragment;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v4.util.Pair;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cesarsk.say_it.ui.MainActivity;
import com.example.cesarsk.say_it.R;
import com.example.cesarsk.say_it.utility.SayItPair;
import com.example.cesarsk.say_it.utility.UtilitySharedPrefs;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

import static android.speech.tts.TextToSpeech.QUEUE_FLUSH;
import static com.example.cesarsk.say_it.ui.MainActivity.american_speaker_google;


/**
 * A simple {@link Fragment} subclass.
 */
public class FavoritesFragment extends Fragment {

    ArrayList<Pair<String, String>> DeserializedFavs;

    public FavoritesFragment() {
        // Required empty public constructor
    }

    public static ArrayList<Pair<String, String>> loadDeserializedFavs(Context context) {

        UtilitySharedPrefs.loadFavs(context);
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
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getActivity(), linearLayoutManager.getOrientation());

        final FavoritesAdapter adapter = new FavoritesAdapter(DeserializedFavs);
        recyclerView.setAdapter(adapter);

        ItemTouchHelper touchHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {

            Drawable background;
            boolean initiated = false;

            private void init(){
                background = new ColorDrawable(ContextCompat.getColor(getActivity(), R.color.Red500));
                initiated = true;
            }

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                adapter.addToPendingRemoval(viewHolder.getAdapterPosition());
            }

            @Override
            public int getSwipeDirs(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
                int position = viewHolder.getAdapterPosition();
                if (adapter.isPendingRemoval(position)) {
                    return 0;
                }
                return super.getSwipeDirs(recyclerView, viewHolder);
            }

            @Override
            public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                View itemView = viewHolder.itemView;

                // not sure why, but this method get's called for viewholder that are already swiped away
                if (viewHolder.getAdapterPosition() == -1) {
                    // not interested in those
                    return;
                }

                if (!initiated) {
                    init();
                }

                // draw red background
                background.setBounds(itemView.getRight() + (int) dX, itemView.getTop(), itemView.getRight(), itemView.getBottom());
                background.draw(c);


                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
        });
        recyclerView.addItemDecoration(dividerItemDecoration);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setItemAnimator(defaultItemAnimator);
        touchHelper.attachToRecyclerView(recyclerView);

        return view;
    }

    private class FavoritesAdapter extends RecyclerView.Adapter<FavoritesAdapter.ViewHolder> {

        public static final long UNDO_TIMEOUT = 3000; //Timeout prima che l'elemento venga cancellato definitivamente

        private ArrayList<Pair<String, String>> favorites;
        private ArrayList<Pair<String, String>> pendingFavorites;
        private Handler handler = new Handler(); //Handler per gestire i Runnable per permettere l'UNDO con il Delay
        HashMap<Pair<String, String>, Runnable> pendingRunnables = new HashMap<>(); //HashMap che associa ad ogni elemento della lista un Runnable che aspetterà
        //3 secondi prima di cancellare l'elemento dalla lista.

        FavoritesAdapter(ArrayList<Pair<String, String>> favorites_list) {
            favorites = favorites_list;
            pendingFavorites = new ArrayList<>();
        }

        class ViewHolder extends RecyclerView.ViewHolder {

            TextView wordTextView;
            TextView IPATextView;
            ImageButton QuickPlayBtn;
            ImageButton AddtoFavsBtn;
            TextView UndoButton;

            ViewHolder(View itemView) {
                super(itemView);
                wordTextView = (TextView) itemView.findViewById(R.id.list_item_first_line);
                IPATextView = (TextView) itemView.findViewById(R.id.list_item_second_line);
                QuickPlayBtn = (ImageButton) itemView.findViewById(R.id.list_item_quickplay);
                AddtoFavsBtn = (ImageButton) itemView.findViewById(R.id.list_item_addToFavs);
                UndoButton = (TextView) itemView.findViewById(R.id.undo_btn);
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

            final Pair<String, String> current_item = favorites.get(position);

            if (pendingFavorites.contains(current_item)) {
                holder.itemView.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.Red500));
                holder.wordTextView.setVisibility(View.GONE);
                holder.IPATextView.setVisibility(View.GONE);
                holder.QuickPlayBtn.setVisibility(View.GONE);
                holder.AddtoFavsBtn.setVisibility(View.GONE);
                holder.UndoButton.setVisibility(View.VISIBLE);
                holder.UndoButton.setOnClickListener(new View.OnClickListener() {
                    //TODO UNDO Snackbar deve essere uguale? al codice dell'OnClick
                    @Override
                    public void onClick(View v) {
                        Runnable pendingRemovalRunnable = pendingRunnables.get(current_item);
                        pendingRunnables.remove(current_item);
                        if (pendingRemovalRunnable != null) {
                            handler.removeCallbacks(pendingRemovalRunnable);
                        }
                        pendingFavorites.remove(current_item);
                        // this will rebind the row in "normal" state
                        notifyItemChanged(favorites.indexOf(current_item));
                    }
                });
            } else {
                holder.wordTextView.setText(favorites.get(position).first);
                holder.IPATextView.setText(favorites.get(position).second);

                holder.QuickPlayBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //Cliccando su Play Button nella search result tab riproduce play.
                        american_speaker_google.speak(holder.wordTextView.getText(), QUEUE_FLUSH, null, null);
                    }
                });

                final boolean favorite_flag = UtilitySharedPrefs.checkFavs(getActivity(), favorites.get(position).first);
                if (favorite_flag)
                    holder.AddtoFavsBtn.setColorFilter(ContextCompat.getColor(getActivity(), R.color.RudolphsNose));

                holder.AddtoFavsBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!favorite_flag) {
                            add(holder);
                        }

                        if (favorite_flag) {
                            remove(holder.getAdapterPosition());
                        }
                    }
                });
            }

        }

        @Override
        public int getItemCount() {
            return favorites.size();
        }

        public void addToPendingRemoval(int position){
            final Pair<String, String> item = favorites.get(position);

            if (!pendingFavorites.contains(item)) {
                pendingFavorites.add(item);
                //Si notifica l'adapter in modo tale da ridisegnare la view
                notifyItemChanged(position);

                //Creazione del Runnable per l'attesa di 3 secondi
                Runnable pendingRemovalRunnable = new Runnable() {
                    @Override
                    public void run() {
                        remove(favorites.indexOf(item));
                    }
                };
                handler.postDelayed(pendingRemovalRunnable, UNDO_TIMEOUT);
                pendingRunnables.put(item, pendingRemovalRunnable);
            }
        }


        public boolean isPendingRemoval(int position) {
            return pendingFavorites.contains(favorites.get(position));
        }

        public void remove(int pos) {
            UtilitySharedPrefs.removeFavs(getActivity(), favorites.get(pos));
            favorites = loadDeserializedFavs(getActivity());
            notifyItemRemoved(pos);
            Toast.makeText(getActivity(), "Removed from Favorites", Toast.LENGTH_SHORT).show();
        }

        public void add(FavoritesAdapter.ViewHolder viewHolder) {
            UtilitySharedPrefs.addFavs(getActivity(), new Pair<>(viewHolder.wordTextView.getText().toString(), viewHolder.IPATextView.getText().toString()));
            Toast.makeText(getActivity(), "Added to Favorites", Toast.LENGTH_SHORT).show();
        }

        public ArrayList<Pair<String, String>> getFavorites() {
            return favorites;
        }

        public void setFavorites(ArrayList<Pair<String, String>> favorites) {
            this.favorites = favorites;
        }
    }
}
