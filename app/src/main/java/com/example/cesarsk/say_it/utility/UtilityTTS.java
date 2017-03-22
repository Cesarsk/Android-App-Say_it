package com.example.cesarsk.say_it.utility;

import android.speech.tts.TextToSpeech;
import android.speech.tts.Voice;

import static android.speech.tts.TextToSpeech.QUEUE_FLUSH;
import static com.example.cesarsk.say_it.ui.MainActivity.american_speaker_google;

/**
 * Created by Claudio on 22/03/2017.
 */

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
        //Log.i("VOICES:", textToSpeech.getVoices().toString()); //stampa tutte le voci disponibili
        for (Voice tmpVoice : tts.getVoices()) {
            if (tmpVoice.getName().equals(voiceName)) {
                return tmpVoice;
            }
        }
        return null;
    }
}
