package com.example.cesarsk.say_it;


import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.ObjectAnimator;
import android.app.Fragment;
import android.graphics.Point;
import android.view.Display;


/**
 * A simple {@link Fragment} subclass.
 */
public class SlidingFragment extends Fragment {


    public SlidingFragment() {
        // Required empty public constructor
    }


    @Override
    public Animator onCreateAnimator(int transit, boolean enter, int nextAnim) {

        Display display = getActivity().getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getRealSize(size);
        float displayWidth = size.x;

        ObjectAnimator animator = (ObjectAnimator) super.onCreateAnimator(transit, enter, nextAnim);

        if(nextAnim != 0)
            animator = (ObjectAnimator) AnimatorInflater.loadAnimator(getActivity(), nextAnim);

        switch (nextAnim){
            case R.animator.slide_from_left:
                animator.setFloatValues(-displayWidth, 0);
                break;

            case R.animator.slide_from_right:
                animator.setFloatValues(displayWidth, 0);
                break;

            case R.animator.slide_to_left:
                animator.setFloatValues(0, -displayWidth);
                break;

            case R.animator.slide_to_right:
                animator.setFloatValues(0, displayWidth);
                break;
        }


        return animator;
    }
}
