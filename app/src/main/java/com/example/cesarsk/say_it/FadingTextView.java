package com.example.cesarsk.say_it;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Pair;

import java.util.Random;

/**
 * Created by Claudio on 05/03/2017.
 */

public class FadingTextView extends android.support.v7.widget.AppCompatTextView {

    ObjectAnimator fade_animator;
    Random rand;
    android.support.v4.util.Pair<String, String> pair;
    String word; String ipa;
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
        pair = Utility.getRandomWordWithIPA();
        word = pair.first; ipa = pair.second;
        current_instance.setText(word);
        fade_animator = ObjectAnimator.ofFloat(this, "alpha", 0f, 1f);
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
                if(!repeated) {
                    pair = Utility.getRandomWordWithIPA();
                    word = pair.first; ipa = pair.second;
                    current_instance.setText(pair.first);
                }
                repeated = !repeated;
            }
        });

        fade_animator.start();
    }
}