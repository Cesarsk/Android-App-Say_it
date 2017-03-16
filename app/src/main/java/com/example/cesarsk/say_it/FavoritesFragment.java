package com.example.cesarsk.say_it;


import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;


/**
 * A simple {@link Fragment} subclass.
 */
public class FavoritesFragment extends Fragment {


    public FavoritesFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.fragment_favorites, container, false);

        Utility.loadFavs(getActivity());
        ArrayList<String> SerializedFavs = new ArrayList<>(MainActivity.FAVORITES);
        ArrayList<Pair<String, String>> DeserializedFavs = new ArrayList<>();
        Gson gson = new Gson();
        for(String element : SerializedFavs){
            SayItPair pair = gson.fromJson(element, SayItPair.class);
            DeserializedFavs.add(pair);
        }

        Collections.sort(DeserializedFavs, new Comparator<Pair<String, String>>() {
            @Override
            public int compare(Pair<String, String> pair1, Pair<String, String> pair2) {
                return pair1.first.compareTo(pair2.first);
            }
        });

        final ListView listView = (ListView) view.findViewById(R.id.favorites_list);
        //ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, sortedFavoritesList);
        FavoritesListAdapter adapter = new FavoritesListAdapter(getActivity(), DeserializedFavs);
        listView.setAdapter(adapter);


        return view;
    }

    private class FavoritesListAdapter extends BaseAdapter {

        private Context context;
        private ArrayList<Pair<String, String>> favorites;

        public FavoritesListAdapter(Context context, ArrayList<Pair<String, String>> favorites){
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
        public View getView(int i, View view, ViewGroup viewGroup) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            final FavoritesElementViewHolder ViewHolder;

            if(view == null){

                ViewHolder = new FavoritesElementViewHolder();
                view = inflater.inflate(R.layout.search_results_list_item, viewGroup, false);
                ViewHolder.wordTextView = (TextView) view.findViewById(R.id.result_first_line);
                ViewHolder.IPATextView = (TextView) view.findViewById(R.id.result_second_line);
                ViewHolder.QuickPlayBtn = (ImageButton) view.findViewById(R.id.quick_play_listbutton);
                ViewHolder.DeleteFromFavoritesBtn = (ImageButton) view.findViewById(R.id.add_to_favs_button);

                view.setTag(ViewHolder);
            }
            else {
                ViewHolder = (FavoritesElementViewHolder) view.getTag();
            }

            String word = favorites.get(i).first;
            String ipa = favorites.get(i).second;
            ViewHolder.wordTextView.setText(word);
            ViewHolder.IPATextView.setText(ipa);

            //TODO Listener pulsanti

            return view;
        }
    }

    private static class FavoritesElementViewHolder{
        TextView wordTextView;
        TextView IPATextView;
        ImageButton QuickPlayBtn;
        ImageButton DeleteFromFavoritesBtn;
    }
}
