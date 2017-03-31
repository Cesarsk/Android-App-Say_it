package com.example.cesarsk.say_it.ui.fragments;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.app.Fragment;
import android.support.design.widget.FloatingActionButton;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cesarsk.say_it.ui.MainActivity;
import com.example.cesarsk.say_it.ui.PlayActivity;
import com.example.cesarsk.say_it.R;
import com.example.cesarsk.say_it.utility.Utility;
import com.example.cesarsk.say_it.utility.UtilityRecordings;
import com.example.cesarsk.say_it.utility.UtilitySharedPrefs;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;


/**
 * A simple {@link Fragment} subclass.
 */
public class RecordingsFragment extends Fragment {

    Snackbar snackbar;

    public RecordingsFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.fragment_recordings, container, false);

        //TODO CAMBIARE TUTTA LA ACQUISIZIONE DEI RECORDINGS ELIMINANDO LE SHAREDPREFS
        UtilityRecordings.updateRecordings();
        ArrayList<File> recordings = MainActivity.RECORDINGS;
        Collections.sort(recordings);

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recordings_list);
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getActivity(), linearLayoutManager.getOrientation());
        recyclerView.addItemDecoration(dividerItemDecoration);
        snackbar = Snackbar.make(view.findViewById(R.id.recordings_fragment_coordinator), "Deleted Recording", (int) RecordingsAdapter.UNDO_TIMEOUT);

        final RecordingsAdapter adapter = new RecordingsAdapter(recordings);
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

    private class RecordingsAdapter extends RecyclerView.Adapter<RecordingsAdapter.ViewHolder> {
        static final long UNDO_TIMEOUT = 3000;

        private ArrayList<File> recordings;
        private MediaPlayer mediaPlayer = new MediaPlayer();

        private File temp_rec_file;
        private byte[] temp_rec_bytes;

        RecordingsAdapter(ArrayList<File> recordings_list) {
            recordings = recordings_list;
        }

        class ViewHolder extends RecyclerView.ViewHolder {

            TextView wordTextView;
            ImageButton QuickPlayBtn;
            ImageButton DeleteRecording;

            ViewHolder(View itemView) {
                super(itemView);
                wordTextView = (TextView) itemView.findViewById(R.id.list_item_first_line);
                QuickPlayBtn = (ImageButton) itemView.findViewById(R.id.list_item_quickplay);
                DeleteRecording = (ImageButton) itemView.findViewById(R.id.list_item_deleteRecording);
            }
        }

        @Override
        public RecordingsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            LayoutInflater inflater = LayoutInflater.from(parent.getContext());

            View view = inflater.inflate(R.layout.list_item_recordings, parent, false);

            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(RecordingsAdapter.ViewHolder holder, int position) {

            final String recordingName = (recordings.get(position).getName()).substring(0, recordings.get(position).getName().lastIndexOf('.'));


            holder.wordTextView.setText(recordingName);

            holder.QuickPlayBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    UtilityRecordings.playRecording(mediaPlayer, recordingName + ".aac");
                }
            });

            holder.DeleteRecording.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                }
            });

            snackbar.setAction("UNDO", new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    FileOutputStream outputStream = null;
                    try {
                        outputStream = new FileOutputStream(temp_rec_file);
                        outputStream.write(temp_rec_bytes);
                        outputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    recordings = UtilityRecordings.loadRecordingsfromStorage();
                    Collections.sort(recordings);
                    notifyItemInserted(recordings.indexOf(temp_rec_file));
                }
            });

            final FloatingActionButton fab = (FloatingActionButton)getActivity().findViewById(R.id.floating_button_recordings);
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    new AlertDialog.Builder(getActivity())
                            .setTitle("Delete Recordings")
                            .setMessage("Are you sure you want to delete your recordings?")
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    Utility.delete_recordings();
                                    Toast.makeText(getActivity(), "Recordings Deleted!", Toast.LENGTH_SHORT).show();
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
            return recordings.size();
        }

        public void remove(int pos){
            temp_rec_file = recordings.get(pos);
            temp_rec_bytes = UtilityRecordings.getRecordingBytesfromFile(getActivity(), recordings.get(pos));

            //UtilitySharedPrefs.removeRecording(getActivity(), recordings.get(pos).getAbsolutePath());
            recordings.get(pos).delete();
            recordings.remove(pos);
            //UtilitySharedPrefs.loadRecordings(getActivity());
            recordings = UtilityRecordings.loadRecordingsfromStorage();
            Collections.sort(recordings);
            //createFileList();
            notifyItemRemoved(pos);
        }

        /*private void createFileList(){
            for(int i = 0; i<recordings.size(); i++){
                File current_recording = new File(recordings.get(i));
                recordings_files.add(current_recording);
            }
        }*/
    }
}
