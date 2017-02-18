package com.example.cesarsk.say_it;


import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.example.cesarsk.say_it.MainActivity.favorites_word;
import static com.example.cesarsk.say_it.Utility.savePrefs;
import static java.sql.DriverManager.println;


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
        ArrayList<String> sortedFavoritesList = new ArrayList<>(MainActivity.favorites_word);
        Collections.sort(sortedFavoritesList);

        ListView listView = (ListView) view.findViewById(R.id.favorites_list);
        //ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, favorites_word.toArray(new String[favorites_word.size()]));
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, sortedFavoritesList);
        listView.setAdapter(adapter);

        Log.i("SAY IT: FAVORITES", favorites_word.toString());

        // Inflate the layout for this fragment
        return view;
    }
}
