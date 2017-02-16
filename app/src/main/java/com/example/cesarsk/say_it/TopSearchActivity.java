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
    public static String clicked_word;

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

            final ListView result_listView = (ListView) findViewById(R.id.result_list_view);



            //TODO: Implementare algoritmo di stemming

            if(!lookup.isEmpty()) {
                int found_index = Collections.binarySearch(MainActivity.WordList, lookup);
                if (found_index >= 0) {
                    String found_word = MainActivity.WordList.get(found_index);
                    ArrayList<String> found = new ArrayList<>();
                    found.add(found_word);
                    int range_index = found_index - 10;
                    if (found_index < 10) range_index = 0;
                    while(range_index < found_index+10) {
                        if(MainActivity.WordList.get(range_index).equals(found_word));
                        else if(MainActivity.WordList.get(range_index).contains(found_word))found.add(MainActivity.WordList.get(range_index));
                        range_index++;
                    }
                    Collections.sort(found);

                    ResultsListCustomAdapter adapter = new ResultsListCustomAdapter(this, found);
                    result_listView.setAdapter(adapter);
                    result_listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        }
                    });
                }
                else{
                    Toast.makeText(this, "Parola non trovata!", Toast.LENGTH_SHORT).show();
                }
            }
            else{
                Toast.makeText(this, "Campo di testo vuoto!", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
