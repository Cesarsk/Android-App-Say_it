package com.cesarsk.say_it.ui.components;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.util.AttributeSet;

import com.cesarsk.say_it.utility.UtilityDictionary;

import java.util.Random;

public class FadingTextView extends android.support.v7.widget.AppCompatTextView {

    //TODO Istanziare un array di FadingTextView e generarle nell'HomeFragment

    private android.support.v4.util.Pair<String, String> pair;
    public String word;
    public String ipa;
    private boolean repeated = true;

    public FadingTextView(Context context) {
        super(context);
        if(!isInEditMode()) animSetUp();
    }

    public FadingTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        if(!isInEditMode()) animSetUp();
    }

    public FadingTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        if(!isInEditMode()) animSetUp();
    }

    private void animSetUp(){
        final FadingTextView current_instance = this;
        Random rand = new Random();
        pair = UtilityDictionary.getRandomWordWithIPA();
        word = pair.first; ipa = pair.second;
        current_instance.setText(word);
        ObjectAnimator fade_animator = ObjectAnimator.ofFloat(this, "alpha", 0f, 1f);
        fade_animator.setDuration((long) rand.nextInt((8000 - 5000) + 1) + 5000);
        fade_animator.setRepeatMode(ValueAnimator.REVERSE);
        fade_animator.setRepeatCount(ValueAnimator.INFINITE);
        long number = (long) rand.nextInt(7000+1);
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
                    pair = UtilityDictionary.getRandomWordWithIPA();
                    word = pair.first; ipa = pair.second;
                    current_instance.setText(pair.first);
                }
                repeated = !repeated;
            }
        });

        fade_animator.start();
    }
}