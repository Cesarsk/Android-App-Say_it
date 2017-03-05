package com.example.cesarsk.say_it;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;

import java.util.Calendar;
import java.util.Random;

import static com.example.cesarsk.say_it.MainActivity.WordList;

/**
 * Created by Claudio on 05/03/2017.
 */

public class FadingTextView extends android.support.v7.widget.AppCompatTextView{

    ObjectAnimator fade_animator;
    Random rand;
    boolean repeated = true;

    public FadingTextView(Context context) {
        super(context);
        animSetUp();
    }


    public FadingTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        animSetUp();
    }

    public FadingTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        animSetUp();
    }

    private void animSetUp(){
        final FadingTextView current_instance = this;
        rand = new Random();

        fade_animator = ObjectAnimator.ofFloat(this, "alpha", 1f, 0f);
        fade_animator.setDuration((long)rand.nextInt((8000 - 5000) + 1) + 5000);
        fade_animator.setRepeatMode(ValueAnimator.REVERSE);
        fade_animator.setRepeatCount(ValueAnimator.INFINITE);
        long number = (long)rand.nextInt(7000+1);
        fade_animator.setStartDelay(number);
        fade_animator.addListener(new Animator.AnimatorListener() {

            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {

            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {
                if(repeated) {
                    current_instance.setText(Utility.getRandomWord((Activity)current_instance.getContext()));
                }
                repeated = !repeated;
            }
        });

        fade_animator.start();
    }
}