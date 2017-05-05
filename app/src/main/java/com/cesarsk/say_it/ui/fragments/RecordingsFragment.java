package com.cesarsk.say_it.ui.fragments;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.os.Bundle;
import android.app.Fragment;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cesarsk.say_it.ui.MainActivity;
import com.cesarsk.say_it.R;
import com.cesarsk.say_it.ui.components.RecordingsAdapter;
import com.cesarsk.say_it.utility.UtilityRecordings;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

import uk.co.deanwild.materialshowcaseview.MaterialShowcaseView;


/**
 * A simple {@link Fragment} subclass.
 */
public class RecordingsFragment extends Fragment {

    private Snackbar snackbar;
    private RecyclerView recyclerView;
    private AudioManager audio;

    public RecordingsFragment() {
    }

    @Override
    public void onResume() {
        super.onResume();
        if (recyclerView != null) {
            UtilityRecordings.updateRecordings(getActivity());
            RecordingsAdapter adapter = (RecordingsAdapter) recyclerView.getAdapter();
            adapter.setRecordings(MainActivity.RECORDINGS);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.fragment_recordings, container, false);

        //Get audio service
        audio = (AudioManager) getActivity().getSystemService(Context.AUDIO_SERVICE);

        UtilityRecordings.updateRecordings(getActivity());
        ArrayList<File> recordings = MainActivity.RECORDINGS;
        Collections.sort(recordings);

        recyclerView = (RecyclerView) view.findViewById(R.id.recordings_list);
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getActivity(), linearLayoutManager.getOrientation());
        recyclerView.addItemDecoration(dividerItemDecoration);

        final RecordingsAdapter adapter = new RecordingsAdapter(this, recordings);
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
                snackbar = Snackbar.make(view.findViewById(R.id.recordings_fragment_coordinator), "Deleted Recording", (int) RecordingsAdapter.UNDO_TIMEOUT);
                snackbar.setAction("UNDO", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        FileOutputStream outputStream;
                        try {
                            outputStream = new FileOutputStream(adapter.getTemp_rec_file());
                            outputStream.write(adapter.getTemp_rec_bytes());
                            outputStream.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        adapter.setRecordings(UtilityRecordings.loadRecordingsfromStorage(getActivity()));
                        Collections.sort(adapter.getRecordings());
                        adapter.notifyItemInserted(adapter.getRecordings().indexOf(adapter.getTemp_rec_file()));
                    }
                });
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

    public void startTutorialPlayActivity(RecordingsAdapter.ViewHolder holder) {
        MainActivity.showCaseFragmentView = new MaterialShowcaseView.Builder(getActivity())
                .setTarget(holder.wordTextView)
                .setDismissText(getString(R.string.showcase_str_btn_5))
                .setContentText(getString(R.string.showcase_str_5))
                .setDelay(100) // optional but starting animations immediately in onCreate can make them choppy
                .singleUse(MainActivity.id_showcase_fragments) // provide a unique ID used to ensure it is only shown once
                .setDismissOnTouch(true)
                .withoutShape()
                .show();
    }

    public boolean isVolumeMuted() {
        int currentVolume = audio.getStreamVolume(AudioManager.STREAM_MUSIC);
        if (currentVolume == 0) return true;
        else return false;
    }
}
