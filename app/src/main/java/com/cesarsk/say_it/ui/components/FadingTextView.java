package com.cesarsk.say_it.ui.components;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.cesarsk.say_it.R;
import com.cesarsk.say_it.ui.PlayActivity;
import com.cesarsk.say_it.utility.UtilityDictionary;

import java.util.Random;

public class FadingTextView extends android.support.v7.widget.AppCompatTextView {

    private android.support.v4.util.Pair<String, String> pair;
    public String word;
    public String ipa;
    private boolean repeated = true;
    OnClickListener onClickListener;

    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1);

    public FadingTextView(Context context) {
        super(context);
        setupListener();
        setOnClickListener(onClickListener);
        setClickable(true);
        setGravity(Gravity.CENTER);
        setTextColor(ContextCompat.getColor(context, R.color.white));
        setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
        setLayoutParams(layoutParams);
        setPaintFlags(getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        if(isAnimationScaleNull()) setAlpha(1f);
        else setAlpha(0f);
        if (!isInEditMode()) animSetUp();
    }

    public FadingTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setupListener();
        setOnClickListener(onClickListener);
        setClickable(true);
        setGravity(Gravity.CENTER);
        setTextColor(ContextCompat.getColor(context, R.color.white));
        setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
        setLayoutParams(layoutParams);
        setPaintFlags(getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        if (!isInEditMode()) animSetUp();
    }

    public FadingTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setupListener();
        setOnClickListener(onClickListener);
        setClickable(true);
        setGravity(Gravity.CENTER);
        setTextColor(ContextCompat.getColor(context, R.color.white));
        setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
        setLayoutParams(layoutParams);
        setPaintFlags(getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        if (!isInEditMode()) animSetUp();
    }

    private void animSetUp() {
        final FadingTextView current_instance = this;
        Random rand = new Random();
        pair = UtilityDictionary.getRandomWordWithIPA();
        word = pair.first;
        ipa = pair.second;
        current_instance.setText(word);
        final ObjectAnimator fade_animator = ObjectAnimator.ofFloat(this, "alpha", 0f, 1f);
        long duration = rand.nextInt((8000 - 5000) + 1) + 5000;
        fade_animator.setDuration(duration);
        fade_animator.setRepeatMode(ValueAnimator.REVERSE);
        fade_animator.setRepeatCount(ValueAnimator.INFINITE);
        long number = (long) rand.nextInt(7000 + 1);
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
                if (!repeated) {
                    pair = UtilityDictionary.getRandomWordWithIPA();
                    word = pair.first;
                    ipa = pair.second;
                    current_instance.setText(pair.first);
                }
                repeated = !repeated;
            }
        });

        if (!isAnimationScaleNull()) fade_animator.start();

    }

    private boolean isAnimationScaleNull() {
        //Check if in developer options animation scale is disabled. If so, do NOT animate to avoid glitches.
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