package com.example.cesarsk.say_it;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

public class PlayFragment extends Fragment {

    public PlayFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_play,
                container, false);

        view.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch(event.getAction()){
                    case MotionEvent.ACTION_UP:
                    {
                        Toast.makeText(container.getContext(), "HOLDING BUTTON", Toast.LENGTH_SHORT).show();
                        return true; //indicate we're done listening to this touch listener
                    }
                }
                return false;
            }
        });

        return view;
    }
}
