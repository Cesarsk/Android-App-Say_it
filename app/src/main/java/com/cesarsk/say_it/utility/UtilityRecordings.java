package com.cesarsk.say_it.utility;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import com.cesarsk.say_it.ui.MainActivity;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;

import static android.Manifest.permission.RECORD_AUDIO;
import static com.cesarsk.say_it.ui.PlayActivity.RequestPermissionCode;

public class UtilityRecordings {


    public static void deleteRecording(Context context, String filename) {

        File file = new File(context.getFilesDir().getAbsolutePath() + "/" + filename);

        if (file.delete()) {
            updateRecordings(context);
        }
    }

    public static ArrayList<File> loadRecordingsfromStorage(Context context) {
        ArrayList<File> recordings = new ArrayList<>();

        File dir = context.getFilesDir();

        File[] files = dir.listFiles();
        if (files != null && files.length != 0) {
            for (File file : files) {
                if (file.getName().substring(file.getName().lastIndexOf('.')).equalsIgnoreCase(".aac")) {
                    recordings.add(file);
                }
            }
        }
        return recordings;
    }

    public static boolean checkRecordingFile(Context context, String word) {

        File file = new File(context.getFilesDir().getAbsolutePath() + "/" + word + ".aac");

        return file.exists();
    }

    //FUNZIONI PER RICHIESTA PERMESSI
    public static boolean checkRecordAudioPermissions(Context context) {
        return ContextCompat.checkSelfPermission(context, RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED;
    }

    public static void requestRecordAudioPermissions(Context context) {
        ActivityCompat.requestPermissions((Activity) context, new
                String[]{RECORD_AUDIO}, RequestPermissionCode);
    }

    public static void startRecording(Context context, MediaRecorder recorder, String word) {
        recorder.reset();
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
        recorder.setMaxDuration(10000);
        recorder.setAudioSamplingRate(16000);
        recorder.setAudioEncodingBitRate(16000*4*1);
        recorder.setAudioSamplingRate(44100);
        recorder.setAudioChannels(1);
        recorder.setOutputFile(context.getFilesDir().getAbsolutePath() + "/" + word + ".aac");
        try {
            recorder.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
        recorder.start();
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static void stopRecording(Context context, MediaRecorder recorder, String word) {
        if (recorder != null) {
            File recording_file = new File(context.getFilesDir().getAbsolutePath() + "/" + word + ".aac");

            try {
                recorder.stop();
            } catch (RuntimeException stopException) {
                recording_file.delete();
                recorder.reset();
                return;
            }
            recorder.reset();
        }
    }

    public static long getRecordingDuration(Context context, MediaPlayer mediaPlayer, String word) {

        File file = new File(context.getFilesDir().getAbsolutePath() + "/" + word + ".aac");
        long duration = 0;

        if (mediaPlayer == null) {
            mediaPlayer = new MediaPlayer();
        }

        if (file.exists()) {
            mediaPlayer.reset();
            try {
                mediaPlayer.setDataSource(file.getAbsolutePath());
                mediaPlayer.prepare();
                duration = (long) mediaPlayer.getDuration();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return duration;
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static byte[] getRecordingBytesfromFile(File file) {

        byte[] bytes = new byte[0];
        try {
            FileInputStream inputStream = new FileInputStream(file);
            bytes = new byte[(int) file.length()];
            inputStream.read(bytes);
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return bytes;
    }

    public static void playRecording(Context context, MediaPlayer mediaPlayer, String recordingName) {
        try {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
                mediaPlayer.reset(); //Before a setDataSource call, you need to reset MP obj.
                mediaPlayer.setDataSource(context.getFilesDir().getAbsolutePath() + "/" + recordingName);
                mediaPlayer.prepare();
                mediaPlayer.start();
            } else {
                mediaPlayer.reset(); //Before a setDataSource call, you need to reset MP obj.
                mediaPlayer.setDataSource(context.getFilesDir().getAbsolutePath() + "/" + recordingName);
                mediaPlayer.prepare();
                mediaPlayer.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void updateRecordings(Context context) {
        MainActivity.RECORDINGS = loadRecordingsfromStorage(context);
    }

    @SuppressWarnings({"unused", "UnusedAssignment"})
    public static void stopPlaying(MediaPlayer mediaPlayer) {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}