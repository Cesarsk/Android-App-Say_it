package com.example.cesarsk.say_it;

import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.widget.TextView;

import org.w3c.dom.Text;

/**
 * Created by cesarsk on 15/03/2017.
 */

public class ShowTimer {

    private long startTime = 0L;
    private Handler customHandler = new Handler();
    long timeInMilliseconds = 0L;
    long timeSwapBuff = 0L;
    long updatedTime = 0L;
    TextView textView;

    public long GetDuration()
    {
        Log.i("DEBUG",""+updatedTime);
        return updatedTime;
    }

    public ShowTimer(TextView timerTextView) {
        this.textView = timerTextView;
    }

    public void StartTimer() {
        startTime = SystemClock.uptimeMillis();
        customHandler.postDelayed(updateTimerThread, 0);
    }

    public void StopTimer() {
        timeSwapBuff += timeInMilliseconds;
        customHandler.removeCallbacks(updateTimerThread);
    }

    public void StartTimerWithDuration(int duration) {

    }

    public void ClearTimer() {
        textView.setText("");
    }

    private Runnable updateTimerThread = new Runnable() {

        public void run() {

            timeInMilliseconds = SystemClock.uptimeMillis() - startTime;

            updatedTime = timeSwapBuff + timeInMilliseconds;

            int secs = (int) (timeInMilliseconds / 1000);
            int mins = secs / 60;
            secs = secs % 60;
            int hours = mins / 60;
            mins = mins % 60;
            int milliseconds = (int) (updatedTime % 1000);

            //String timer = "" + String.format("%02d", hours) + ":" + String.format("%02d", mins) + ":" + String.format("%02d", secs);
            //String timer = "" + String.format("%02d", mins) + ":" + String.format("%02d", secs) + ":" + String.format("%03d", milliseconds);
            String timer = "" + String.format("%02d", secs) + ":" + String.format("%03d", milliseconds);
            textView.setText(timer);
            customHandler.postDelayed(this, 0);
        }

    };


}
