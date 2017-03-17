package com.example.cesarsk.say_it;


import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.util.Pair;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
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
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        FavoritesAdapter adapter = new FavoritesAdapter(DeserializedFavs);
        recyclerView.setAdapter(adapter);

        return view;
    }

    private class FavoritesAdapter extends RecyclerView.Adapter<FavoritesAdapter.ViewHolder> {

        private ArrayList<Pair<String, String>> favorites;

        public FavoritesAdapter(ArrayList<Pair<String, String>> favorites_list) {
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

    private class FavoritesListAdapter extends BaseAdapter {

        private Context context;
        private ArrayList<Pair<String, String>> favorites;

        public FavoritesListAdapter(Context context, ArrayList<Pair<String, String>> favorites) {
            this.context = context;
            this.favorites = favorites;
        }

        @Override
        public int getCount() {
            return favorites.size();
        }

        @Override
        public Object getItem(int i) {
            return favorites.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(final int i, View view, ViewGroup viewGroup) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            final FavoritesElementViewHolder ViewHolder;

            if (view == null) {

                ViewHolder = new FavoritesElementViewHolder();
                view = inflater.inflate(R.layout.list_item_generic, viewGroup, false);
                ViewHolder.wordTextView = (TextView) view.findViewById(R.id.list_item_first_line);
                ViewHolder.IPATextView = (TextView) view.findViewById(R.id.list_item_second_line);
                ViewHolder.QuickPlayBtn = (ImageButton) view.findViewById(R.id.list_item_quickplay);
                ViewHolder.ToggleFavs = (ImageButton) view.findViewById(R.id.list_item_addToFavs);

                view.setTag(ViewHolder);
            } else {
                ViewHolder = (FavoritesElementViewHolder) view.getTag();
            }

            String word = favorites.get(i).first;
            String ipa = favorites.get(i).second;
            ViewHolder.wordTextView.setText(word);
            ViewHolder.IPATextView.setText(ipa);

            //TODO Listener pulsanti
            ViewHolder.QuickPlayBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Cliccando su Play Button nella search result tab riproduce play.
                    american_speaker_google.speak(ViewHolder.wordTextView.getText(), QUEUE_FLUSH, null, null);
                }
            });

            final boolean favorite_flag = Utility.checkFavs(getActivity(), favorites.get(i).first);
            if (favorite_flag)
                ViewHolder.ToggleFavs.setColorFilter(getResources().getColor(R.color.RudolphsNose));

            ViewHolder.ToggleFavs.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!favorite_flag) {
                        Utility.addFavs(context, new Pair<>(ViewHolder.wordTextView.getText().toString(), ViewHolder.IPATextView.getText().toString()));
                        Toast.makeText(context, "Added to Favorites", Toast.LENGTH_SHORT).show();
                    }

                    if (favorite_flag) {
                        Utility.removeFavs(v.getContext(), favorites.get(i));
                        Toast.makeText(context, "Removed from Favorites", Toast.LENGTH_SHORT).show();
                    }
                }
            });


            return view;
        }
    }

    private static class FavoritesElementViewHolder {
        TextView wordTextView;
        TextView IPATextView;
        ImageButton QuickPlayBtn;
        ImageButton ToggleFavs;
    }
}
