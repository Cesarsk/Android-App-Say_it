package com.cesarsk.say_it.ui.fragments;


import android.app.ActivityOptions;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.util.Pair;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.cesarsk.say_it.ui.MainActivity;
import com.cesarsk.say_it.R;
import com.cesarsk.say_it.ui.PlayActivity;
import com.cesarsk.say_it.utility.SayItPair;
import com.cesarsk.say_it.utility.UtilitySharedPrefs;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

import static android.speech.tts.TextToSpeech.QUEUE_FLUSH;


/**
 * A simple {@link Fragment} subclass.
 */
public class HistoryFragment extends Fragment {

    private ArrayList<SayItPair> DeserializedHistory;
    RecyclerView recyclerView;
    Snackbar snackbar;

    public HistoryFragment() {

    }

    @Override
    public void onResume() {
        super.onResume();
        if(recyclerView != null) {
            //TODO risettare la lista dell'adapter QUI con un metodo di Update (loadDeserializedHistory)
            HistoryAdapter adapter = (HistoryAdapter) recyclerView.getAdapter();
            adapter.setHistory(loadDeserializedHistory(getActivity()));
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_history, container, false);

        DeserializedHistory = loadDeserializedHistory(getActivity());

        snackbar = Snackbar.make(view.findViewById(R.id.history_fragment_coordinator), "Removed Element from Favorites", (int) HistoryAdapter.UNDO_TIMEOUT);
        recyclerView = (RecyclerView) view.findViewById(R.id.history_list);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        DefaultItemAnimator defaultItemAnimator = new DefaultItemAnimator();
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getActivity(), linearLayoutManager.getOrientation());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setItemAnimator(defaultItemAnimator);
        recyclerView.addItemDecoration(dividerItemDecoration);
        final HistoryAdapter adapter = new HistoryAdapter(DeserializedHistory);
        recyclerView.setAdapter(adapter);

        ItemTouchHelper touchHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {

            Drawable background;
            Drawable DeletedIcon;
            int DeletedIconMargin;
            boolean initiated;

            void init() {
                background = new ColorDrawable(ContextCompat.getColor(getActivity(), R.color.Red500));
                DeletedIcon = ContextCompat.getDrawable(getActivity(), R.drawable.ic_close_white_24dp);
                DeletedIconMargin = (int) getActivity().getResources().getDimension(R.dimen.deleted_icon_margin);
                initiated = true;
            }

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                adapter.remove(viewHolder.getAdapterPosition());
                snackbar.show();
            }

            @Override
            public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {

                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);

                View itemView = viewHolder.itemView;

                // not sure why, but this method get's called for viewholder that are already swiped away
                if (viewHolder.getAdapterPosition() == -1) {
                    // not interested in those
                    return;
                }

                if (!initiated) {
                    init();
                }

                // draw red background
                background.setBounds(itemView.getRight() + (int) dX, itemView.getTop(), itemView.getRight(), itemView.getBottom());
                background.draw(c);

                int itemHeight = itemView.getBottom() - itemView.getTop();
                int intrinsicWidth = DeletedIcon.getIntrinsicWidth();
                int intrinsicHeight = DeletedIcon.getIntrinsicWidth();

                int xMarkLeft = itemView.getRight() - DeletedIconMargin - intrinsicWidth;
                int xMarkRight = itemView.getRight() - DeletedIconMargin;
                int xMarkTop = itemView.getTop() + (itemHeight - intrinsicHeight) / 2;
                int xMarkBottom = xMarkTop + intrinsicHeight;
                DeletedIcon.setBounds(xMarkLeft, xMarkTop, xMarkRight, xMarkBottom);

                DeletedIcon.draw(c);
            }
        });

        recyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {

            // we want to cache this and not allocate anything repeatedly in the onDraw method
            Drawable background;
            boolean initiated;

            private void init() {
                background = new ColorDrawable(ContextCompat.getColor(getActivity(), R.color.Red500));
                initiated = true;
            }

            @Override
            public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {

                if (!initiated) {
                    init();
                }

                // only if animation is in progress
                if (parent.getItemAnimator().isRunning()) {

                    // some items might be animating down and some items might be animating up to close the gap left by the removed item
                    // this is not exclusive, both movement can be happening at the same time
                    // to reproduce this leave just enough items so the first one and the last one would be just a little off screen
                    // then remove one from the middle

                    // find first child with translationY > 0
                    // and last one with translationY < 0
                    // we're after a rect that is not covered in recycler-view views at this point in time
                    View lastViewComingDown = null;
                    View firstViewComingUp = null;

                    // this is fixed
                    int left = 0;
                    int right = parent.getWidth();

                    // this we need to find out
                    int top = 0;
                    int bottom = 0;

                    // find relevant translating views
                    int childCount = parent.getLayoutManager().getChildCount();
                    for (int i = 0; i < childCount; i++) {
                        View child = parent.getLayoutManager().getChildAt(i);
                        if (child.getTranslationY() < 0) {
                            // view is coming down
                            lastViewComingDown = child;
                        } else if (child.getTranslationY() > 0) {
                            // view is coming up
                            if (firstViewComingUp == null) {
                                firstViewComingUp = child;
                            }
                        }
                    }

                    if (lastViewComingDown != null && firstViewComingUp != null) {
                        // views are coming down AND going up to fill the void
                        top = lastViewComingDown.getBottom() + (int) lastViewComingDown.getTranslationY();
                        bottom = firstViewComingUp.getTop() + (int) firstViewComingUp.getTranslationY();
                    } else if (lastViewComingDown != null) {
                        // views are going down to fill the void
                        top = lastViewComingDown.getBottom() + (int) lastViewComingDown.getTranslationY();
                        bottom = lastViewComingDown.getBottom();
                    } else if (firstViewComingUp != null) {
                        // views are coming up to fill the void
                        top = firstViewComingUp.getTop();
                        bottom = firstViewComingUp.getTop() + (int) firstViewComingUp.getTranslationY();
                    }

                    background.setBounds(left, top, right, bottom);
                    background.draw(c);

                }
                super.onDraw(c, parent, state);
            }

        });


        touchHelper.attachToRecyclerView(recyclerView);

        return view;
    }

    private class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder> {

        public static final long UNDO_TIMEOUT = 3000; //Timeout prima che l'elemento venga cancellato definitivamente

        public ArrayList<SayItPair> getHistory() {
            return history;
        }

        public void setHistory(ArrayList<SayItPair> history) {
            this.history = history;
            notifyDataSetChanged();
        }

        public Pair<String, String> getTemp_hist() {
            return temp_hist;
        }

        private SayItPair temp_hist;

        public int getTemp_pos() {
            return temp_pos;
        }

        public void setTemp_pos(int temp_pos) {
            this.temp_pos = temp_pos;
        }

        private int temp_pos;

        private Date temp_adding_time;

        public void setTemp_hist(SayItPair temp_hist) {
            this.temp_hist = temp_hist;
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

            ViewHolder(View itemView) {
                super(itemView);
                wordTextView = (TextView) itemView.findViewById(R.id.list_item_first_line);
                IPATextView = (TextView) itemView.findViewById(R.id.list_item_second_line);
                QuickPlayBtn = (ImageButton) itemView.findViewById(R.id.list_item_quickplay);
                AddtoFavsBtn = (ImageButton) itemView.findViewById(R.id.list_item_addToFavs);
            }
        }

        @Override
        public HistoryAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            LayoutInflater inflater = LayoutInflater.from(parent.getContext());

            View view = inflater.inflate(R.layout.list_item_history, parent, false);

            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final HistoryAdapter.ViewHolder holder, final int position) {

                holder.QuickPlayBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(MainActivity.DEFAULT_ACCENT.equals("0")) {
                            MainActivity.american_speaker_google.speak(holder.wordTextView.getText(), QUEUE_FLUSH, null, null);
                            //Log.i("DEFAULT - HISTORY", MainActivity.DEFAULT_ACCENT);
                        }
                        else if(MainActivity.DEFAULT_ACCENT.equals("1")) {
                            MainActivity.british_speaker_google.speak(holder.wordTextView.getText(),QUEUE_FLUSH,null,null);
                            //Log.i("DEFAULT - HISTORY", MainActivity.DEFAULT_ACCENT);
                        }
                    }
                });

            holder.wordTextView.setText(history.get(position).first);
            holder.IPATextView.setText(history.get(position).second);

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final Intent play_activity_intent = new Intent(getActivity(), PlayActivity.class);
                    play_activity_intent.putExtra(PlayActivity.PLAY_WORD, holder.wordTextView.getText());
                    play_activity_intent.putExtra(PlayActivity.PLAY_IPA, holder.IPATextView.getText());
                    UtilitySharedPrefs.addHist(getActivity(), new SayItPair(holder.wordTextView.getText().toString(), holder.IPATextView.getText().toString()));
                    getActivity().startActivity(play_activity_intent, ActivityOptions.makeSceneTransitionAnimation(getActivity()).toBundle());
                }
            });

            if (UtilitySharedPrefs.checkFavs(getActivity(), history.get(position).first))
                holder.AddtoFavsBtn.setColorFilter(ContextCompat.getColor(getActivity(), R.color.RudolphsNose));

            holder.AddtoFavsBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!UtilitySharedPrefs.checkFavs(getActivity(), history.get(position).first)) {
                        UtilitySharedPrefs.addFavs(getActivity(), new Pair<>(holder.wordTextView.getText().toString(), holder.IPATextView.getText().toString()));
                        Toast.makeText(getActivity(), "Added to Favorites", Toast.LENGTH_SHORT).show();
                        holder.AddtoFavsBtn.setColorFilter(ContextCompat.getColor(getActivity(), R.color.RudolphsNose));
                    }

                    else if(UtilitySharedPrefs.checkFavs(getActivity(), history.get(position).first)) {
                        UtilitySharedPrefs.removeFavs(v.getContext(), history.get(holder.getAdapterPosition()));
                        Toast.makeText(getActivity(), "Removed from Favorites", Toast.LENGTH_SHORT).show();
                        holder.AddtoFavsBtn.setColorFilter(ContextCompat.getColor(getActivity(), R.color.primary_dark));
                    }
                }
            });

            snackbar.setAction("UNDO", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    UtilitySharedPrefs.addHist(getActivity(), temp_hist);
                    history = loadDeserializedHistory(getActivity());
                    notifyItemInserted(history.indexOf(temp_hist));
                }
            });

            final FloatingActionButton fab =(FloatingActionButton)getActivity().findViewById(R.id.floating_button_history);
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    new AlertDialog.Builder(getActivity())
                            .setTitle("Clear History")
                            .setMessage("Are you sure you want to clear your History?")
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    UtilitySharedPrefs.clearHistory(getActivity());
                                    setHistory(loadDeserializedHistory(getActivity()));
                                    Toast.makeText(getActivity(), "History Cleared!", Toast.LENGTH_SHORT).show();

                                }
                            })
                            .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    //do nothing
                                }
                            })
                            .show();
                }
            });

        }

        @Override
        public int getItemCount() {
            return history.size();
        }

        void remove(int pos) {
            temp_hist = history.get(pos);
            temp_pos = pos;
            temp_adding_time = temp_hist.getAdding_time();

            UtilitySharedPrefs.removeHist(getActivity(), new SayItPair(history.get(pos).first, history.get(pos).second, history.get(pos).getAdding_time()));
            history = loadDeserializedHistory(getActivity());
            notifyItemRemoved(pos);
        }

    }

    public static ArrayList<SayItPair> loadDeserializedHistory(Context context) {

        UtilitySharedPrefs.loadHist(context);
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
