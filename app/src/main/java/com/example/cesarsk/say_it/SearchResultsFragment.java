package com.example.cesarsk.say_it;


import android.app.Activity;
import android.app.FragmentManager;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;


/**
 * A simple {@link Fragment} subclass.
 */
public class SearchResultsFragment extends Fragment {

    public static String LOOKUP = "LOOKUP_STRING";

    public SearchResultsFragment() {
        // Required empty public constructor
    }

    public static SearchResultsFragment newInstance(String lookup) {

        Bundle args = new Bundle();

        SearchResultsFragment fragment = new SearchResultsFragment();
        args.putString(LOOKUP, lookup);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_searchresults, container, false);

        final ListView result_listView = (ListView) view.findViewById(R.id.result_list_view);

        String lookup = getArguments().getString(LOOKUP);
        //TODO: Implementare algoritmo di stemming

        if(!lookup.isEmpty()) {
            int found_index = Collections.binarySearch(MainActivity.WordList, lookup);
            if (found_index >= 0) {
                ArrayList<String> found = new ArrayList<>();
                for(int i=0; i<10; i++){
                    found.add(MainActivity.WordList.get(found_index+i));
                    Collections.sort(found);
                }


                ResultsListCustomAdapter adapter = new ResultsListCustomAdapter(view.getContext(), found, R.id.quick_play_button, R.id.add_to_favs_button);
                result_listView.setAdapter(adapter);
                result_listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Log.i("Say It:","OnItemClick della list_view");
                        FragmentManager fragmentManager = getFragmentManager();
                        fragmentManager.beginTransaction().replace(R.id.fragment_container_searchresults, new PlayFragment());
                    }
                });
            }
            else{
                Toast.makeText(view.getContext(), "Parola non trovata!", Toast.LENGTH_SHORT).show();
            }
        }
        else{
            Toast.makeText(view.getContext(), "Campo di testo vuoto!", Toast.LENGTH_SHORT).show();
        }

        return view;
    }

}
