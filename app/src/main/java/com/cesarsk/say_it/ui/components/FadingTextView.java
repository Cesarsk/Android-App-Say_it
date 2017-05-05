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

        if (!isAnimationScaleNull()) fade_animator.start();
    }

    //Check if in developer options animation scale is disabled. If so, do NOT animate to avoid glitches.
    private boolean isAnimationScaleNull() {
        float animator_duration_scale = 0f, transition_animation_scale = 0f, window_animation_scale = 0f;
        try {
            animator_duration_scale = Settings.Global.getFloat(getContext().getContentResolver(), Settings.Global.ANIMATOR_DURATION_SCALE);
            transition_animation_scale = Settings.Global.getFloat(getContext().getContentResolver(), Settings.Global.TRANSITION_ANIMATION_SCALE);
            window_animation_scale = Settings.Global.getFloat(getContext().getContentResolver(), Settings.Global.WINDOW_ANIMATION_SCALE);

            if (animator_duration_scale == 0 || window_animation_scale == 0 || transition_animation_scale == 0) {
                return true;
            }

        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }
        return false;
    }

    private void setupListener()
    {
        onClickListener = new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent play_activity_intent = new Intent(getContext(), PlayActivity.class);
                play_activity_intent.putExtra(PlayActivity.PLAY_WORD, word);
                play_activity_intent.putExtra(PlayActivity.PLAY_IPA, ipa);
                v.getContext().startActivity(play_activity_intent, ActivityOptions.makeSceneTransitionAnimation((Activity) v.getContext()).toBundle());
            }
        };
    }
}