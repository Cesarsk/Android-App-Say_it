package com.example.cesarsk.say_it;


import android.app.Fragment;


/**
 * A simple {@link Fragment} subclass.
 */
public class HistoryFragment extends SlidingFragment {

    public HistoryFragment() {
    }

    //TODO IMPORTANTE CORREGGERE HISTORY
/*

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.fragment_history, container, false);
        ArrayList<String> sortedHistoryList = new ArrayList<>(MainActivity.HISTORY);
        Utility.loadFavs(getActivity());
        Collections.sort(sortedHistoryList);

        ListView listView = (ListView) view.findViewById(R.id.history_list);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, sortedHistoryList);
        listView.setAdapter(adapter);

        Log.i("SAY IT: HISTORY", HISTORY.toString());

        // Inflate the layout for this fragment
        return view;
    } */
}
