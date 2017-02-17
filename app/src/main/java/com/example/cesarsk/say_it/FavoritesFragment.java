package com.example.cesarsk.say_it;


import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import static com.example.cesarsk.say_it.MainActivity.favorites_word;


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

        ListView listView = (ListView) view.findViewById(R.id.favorites_list);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.activity_list_item, favorites_word.toArray(new String[favorites_word.size()]));
        listView.setAdapter(adapter);

        // Inflate the layout for this fragment
        return view;
    }

}
