package com.example.cesarsk.say_it;


import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class SearchResultsFragment extends Fragment {


    public SearchResultsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search_results, container, false);

        final ListView result_list = (ListView) view.findViewById(R.id.result_list_view);
        final ResultsListCustomAdapter adapter = new ResultsListCustomAdapter(view.getContext());
        result_list.setAdapter(adapter);

        EditText search_bar_edit_text = (EditText) getActivity().findViewById(R.id.search_bar_edit_text);
        search_bar_edit_text.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //new AsyncFiltering().execute(adapter, s);
                adapter.getFilter().filter(s);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        return view;
    }

    private class AsyncFiltering extends AsyncTask{

        @Override
        protected Object doInBackground(Object[] objects) {

            ResultsListCustomAdapter adapter = (ResultsListCustomAdapter) objects[0];
            CharSequence s = (CharSequence) objects[1];

            adapter.getFilter().filter(s);

            return null;
        }
    }

}
