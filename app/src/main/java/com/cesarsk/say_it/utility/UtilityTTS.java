package com.cesarsk.say_it.utility;

import android.speech.tts.TextToSpeech;
import android.speech.tts.Voice;
import android.util.Log;

import com.cesarsk.say_it.ui.activities.MainActivity;

import static android.speech.tts.TextToSpeech.QUEUE_FLUSH;
import static com.cesarsk.say_it.ui.activities.MainActivity.american_speaker_google;

/**
 * Created by Claudio on 22/03/2017.
 */

@SuppressWarnings("ALL")
public class UtilityTTS {


    public static void pronounceWord(CharSequence word, float pitch, float speechRate, Voice accent) {
        //manual pronunciation of a word, never used.
        american_speaker_google.setPitch(pitch);
        american_speaker_google.setSpeechRate(speechRate);
        american_speaker_google.setVoice(accent);
        american_speaker_google.speak(word, QUEUE_FLUSH, null, null);
    }

    //IMPOSTAZIONE TEXT TO SPEECH
    public static Voice searchVoice(String voiceName, TextToSpeech tts) {
        if (MainActivity.isLoggingEnabled)
            Log.i("VOICES:", tts.getVoices().toString()); //stampa tutte le voci disponibili
        for (Voice tmpVoice : tts.getVoices()) {
            if (tmpVoice.getName().equals(voiceName)) {
                return tmpVoice;
            }
        }
        return null;
    }
}
