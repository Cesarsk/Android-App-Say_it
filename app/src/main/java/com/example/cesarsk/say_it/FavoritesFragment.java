package com.example.cesarsk.say_it;


import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Collections;

import static com.example.cesarsk.say_it.MainActivity.FAVORITES;


/**
 * A simple {@link Fragment} subclass.
 */
public class FavoritesFragment extends SlidingFragment {


    public FavoritesFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.fragment_favorites, container, false);

        Utility.loadFavs(getActivity());
        ArrayList<String> sortedFavoritesList = new ArrayList<>(MainActivity.FAVORITES);
        Collections.sort(sortedFavoritesList);

        final ListView listView = (ListView) view.findViewById(R.id.favorites_list);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, sortedFavoritesList);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1,int position, long arg3)
            {
                final Intent play_activity_intent = new Intent(getActivity(), PlayActivity.class);
                String selectedFromList = ((String)listView.getItemAtPosition(position));
                play_activity_intent.putExtra(PlayActivity.PLAY_WORD, selectedFromList);
                getActivity().startActivity(play_activity_intent);
                Utility.addHist(getActivity(), selectedFromList);
            }
        });


        return view;
    }
}
