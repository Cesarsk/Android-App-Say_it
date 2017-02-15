package com.example.cesarsk.say_it;

import android.app.Fragment;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;

import static android.speech.tts.TextToSpeech.QUEUE_ADD;
import static com.example.cesarsk.say_it.MainActivity.tts;

public class TopSearchActivity extends AppCompatActivity {

    private final FragmentManager fragmentManager = this.getFragmentManager();
    final PlayFragment play_frag = new PlayFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_top_search);
        HandleIntent(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        HandleIntent(intent);
    }

    private void HandleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String lookup = intent.getStringExtra(SearchManager.QUERY).toLowerCase();

            fragmentManager.beginTransaction().add(R.id.fragment_container_searchresults, SearchResultsFragment.newInstance(lookup)).commit();

            /*final ListView result_listView = (ListView) findViewById(R.id.result_list_view);

            //TODO: Implementare algoritmo di stemming

            if(!lookup.isEmpty()) {
                int found_index = Collections.binarySearch(MainActivity.WordList, lookup);
                if (found_index >= 0) {
                    ArrayList<String> found = new ArrayList<>();
                    for(int i=0; i<10; i++){
                        found.add(MainActivity.WordList.get(found_index+i));
                        Collections.sort(found);
                    }


                    ResultsListCustomAdapter adapter = new ResultsListCustomAdapter(this, found, R.id.quick_play_button, R.id.add_to_favs_button);
                    result_listView.setAdapter(adapter);
                    result_listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                            Log.i("Say it:", "Entrato in onItemClick della lista");


                            Log.i("Say it:", "View Name = " + view.getClass().getName());
                            fragmentManager.beginTransaction().replace(R.id.fragment_container_searchresults, play_frag).commit();
                        }
                    });
                }
                else{
                    Toast.makeText(this, "Parola non trovata!", Toast.LENGTH_SHORT).show();
                }
            }
            else{
                Toast.makeText(this, "Campo di testo vuoto!", Toast.LENGTH_SHORT).show();
            }*/
        }
    }
}
