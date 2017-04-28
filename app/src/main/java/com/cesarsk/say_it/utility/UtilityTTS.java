package com.cesarsk.say_it.utility;

import android.content.Context;
import android.speech.tts.TextToSpeech;
import android.speech.tts.Voice;
import android.util.Log;

import com.cesarsk.say_it.ui.MainActivity;

import static android.speech.tts.TextToSpeech.QUEUE_FLUSH;
import static com.cesarsk.say_it.ui.MainActivity.american_speaker_google;
import static com.cesarsk.say_it.ui.MainActivity.voice_american_female;
import static com.cesarsk.say_it.ui.MainActivity.voice_british_female;

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
        //Log.i("VOICES:", textToSpeech.getVoices().toString()); //stampa tutte le voci disponibili
        for (Voice tmpVoice : tts.getVoices()) {
            if (tmpVoice.getName().equals(voiceName)) {
                return tmpVoice;
            }
        }
        return null;
    }

    public static TextToSpeech initTTS(Context context, final boolean accent) {
        TextToSpeech.OnInitListener onInitListener = null;

        final TextToSpeech tts_speaker = new TextToSpeech(context, onInitListener);
        onInitListener = new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                tts_speaker.setPitch((float) 0.90);
                tts_speaker.setSpeechRate((float) 0.90);
                if(accent)tts_speaker.setVoice(voice_british_female);
                else if(!accent)tts_speaker.setVoice(voice_british_female);
            }
        };

        return tts_speaker;
    }
}
