package com.example.cesarsk.say_it;


import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.util.Pair;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import static android.speech.tts.TextToSpeech.QUEUE_FLUSH;
import static com.example.cesarsk.say_it.MainActivity.american_speaker_google;


/**
 * A simple {@link Fragment} subclass.
 */
public class HistoryFragment extends Fragment {

    private ArrayList<SayItPair> DeserializedHistory;

    public HistoryFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_history, container, false);

        DeserializedHistory = loadDeserializedHistory(getActivity());

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.history_list);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        DefaultItemAnimator defaultItemAnimator = new DefaultItemAnimator();
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getActivity(),linearLayoutManager.getOrientation());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setItemAnimator(defaultItemAnimator);
        recyclerView.addItemDecoration(dividerItemDecoration);
        final HistoryAdapter adapter = new HistoryAdapter(DeserializedHistory);
        recyclerView.setAdapter(adapter);

        ItemTouchHelper touchHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                Utility.removeHist(getActivity(), adapter.getHistory().get(viewHolder.getAdapterPosition()));
                adapter.setHistory(loadDeserializedHistory(getActivity()));
                adapter.notifyItemRemoved(viewHolder.getAdapterPosition());
            }
        });

        touchHelper.attachToRecyclerView(recyclerView);

        return view;
    }

    private class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder> {

        public ArrayList<SayItPair> getHistory() {
            return history;
        }

        public void setHistory(ArrayList<SayItPair> history) {
            this.history = history;
        }

        private ArrayList<SayItPair> history;

        public HistoryAdapter(ArrayList<SayItPair> history_list) {
            history = history_list;
        }

        class ViewHolder extends RecyclerView.ViewHolder {

            TextView wordTextView;
            TextView IPATextView;
            ImageButton QuickPlayBtn;
            ImageButton AddtoFavsBtn;
            ImageButton DeleteFromHistoryBtn;

            ViewHolder(View itemView) {
                super(itemView);
                wordTextView = (TextView) itemView.findViewById(R.id.list_item_first_line);
                ;
                IPATextView = (TextView) itemView.findViewById(R.id.list_item_second_line);
                ;
                QuickPlayBtn = (ImageButton) itemView.findViewById(R.id.list_item_quickplay);
                ;
                AddtoFavsBtn = (ImageButton) itemView.findViewById(R.id.list_item_addToFavs);
                ;
                DeleteFromHistoryBtn = (ImageButton) itemView.findViewById(R.id.list_item_removeFromHistory);
                ;
            }
        }

        @Override
        public HistoryAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            LayoutInflater inflater = LayoutInflater.from(parent.getContext());

            View view = inflater.inflate(R.layout.list_item_history, parent, false);

            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final HistoryAdapter.ViewHolder holder, int position) {

            holder.wordTextView.setText(history.get(position).first);
            holder.IPATextView.setText(history.get(position).second);

            holder.QuickPlayBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Cliccando su Play Button nella search result tab riproduce play.
                    american_speaker_google.speak(holder.wordTextView.getText(), QUEUE_FLUSH, null, null);
                }
            });

            final boolean favorite_flag = Utility.checkFavs(getActivity(), history.get(position).first);
            if (favorite_flag)
                holder.AddtoFavsBtn.setColorFilter(getResources().getColor(R.color.RudolphsNose));

            holder.AddtoFavsBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!favorite_flag) {
                        Utility.addFavs(getActivity(), new Pair<>(holder.wordTextView.getText().toString(), holder.IPATextView.getText().toString()));
                        Toast.makeText(getActivity(), "Added to Favorites", Toast.LENGTH_SHORT).show();
                        holder.AddtoFavsBtn.setColorFilter(getResources().getColor(R.color.RudolphsNose));
                    }

                    if (favorite_flag) {
                        Utility.removeFavs(v.getContext(), history.get(holder.getAdapterPosition()));
                        Toast.makeText(getActivity(), "Removed from Favorites", Toast.LENGTH_SHORT).show();
                        holder.AddtoFavsBtn.setColorFilter(getResources().getColor(R.color.colorPrimaryDark));
                    }
                }
            });

            holder.DeleteFromHistoryBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int pos = holder.getAdapterPosition();
                    Utility.removeHist(view.getContext(), new SayItPair(history.get(pos).first, history.get(pos).second, history.get(pos).getAdding_time()));
                    history = loadDeserializedHistory(getActivity());
                    notifyItemRemoved(pos);
                    Toast.makeText(getActivity(), "Removed from History", Toast.LENGTH_SHORT).show();
                }
            });

        }

        @Override
        public int getItemCount() {
            return history.size();
        }
    }

    public static ArrayList<SayItPair> loadDeserializedHistory(Context context) {

        Utility.loadHist(context);
        ArrayList<String> SerializedHistory = new ArrayList<>(MainActivity.HISTORY);
        ArrayList<SayItPair> DeserializedHistory = new ArrayList<>();
        Gson gson = new Gson();

        for (String element : SerializedHistory) {
            SayItPair pair = gson.fromJson(element, SayItPair.class);
            DeserializedHistory.add(pair);
        }

        Collections.sort(DeserializedHistory, new Comparator<SayItPair>() {
            @Override
            public int compare(SayItPair pair1, SayItPair pair2) {
                return pair2.getAdding_time().compareTo(pair1.getAdding_time());
            }
        });

        return DeserializedHistory;
    }
}
