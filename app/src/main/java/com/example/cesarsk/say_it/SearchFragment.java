package com.example.cesarsk.say_it;


import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;


/**
 * A simple {@link Fragment} subclass.
 */
public class SearchFragment extends Fragment {


    public SearchFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_search, container, false);
        final FragmentManager fragmentManager = getFragmentManager();

        Button Say_It_Button = (Button) view.findViewById(R.id.Say_It_Button);

        Say_It_Button.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                EditText searchbox = (EditText) view.findViewById(R.id.SearchBox);
                ListView result_listView = (ListView) view.findViewById(R.id.result_list_view);

                String lookup = searchbox.getText().toString().toLowerCase();

                //TODO: Implementare algoritmo di stemming

                if(!lookup.isEmpty()) {
                    int found_index = Collections.binarySearch(MainActivity.WordList, lookup);
                    if (found_index >= 0) {
                        ArrayList<String> found = new ArrayList<>();
                        for(int i=0; i<10; i++){
                            found.add(MainActivity.WordList.get(found_index+i));
                            Collections.sort(found);
                        }
                        ResultsListCustomAdapter adapter = new ResultsListCustomAdapter(view.getContext(), found, R.id.play_button, R.id.add_to_favs_button);
                        result_listView.setAdapter(adapter);
                        result_listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                Toast.makeText(container.getContext(), "Elemento cliccato", Toast.LENGTH_SHORT).show();
                                fragmentManager.beginTransaction().replace(R.id.fragment_container, new PlayFragment()).commit();
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
            }
        });

        return view;
    }
}
