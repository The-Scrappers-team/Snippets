package com.scrappers.notepadsnippet.MainScreens.Features.TextNotes;

import android.speech.tts.TextToSpeech;
import android.text.Html;
import android.util.Log;
import android.view.View;

import java.util.Locale;

import androidx.appcompat.app.AppCompatActivity;

import static com.scrappers.notepadsnippet.MainScreens.EditPaneActivity.SPEAK_STATE;
import static com.scrappers.notepadsnippet.MainScreens.EditPaneActivity.noteBox;

public class speakNote implements View.OnClickListener {
    private final AppCompatActivity context;
    private TextToSpeech ttsEngine;

    /**
     * adds a speak notes listener to speak the noteBox
     * @param context the context of the holding class
     */
    public speakNote(AppCompatActivity context) {
        this.context=context;
    }
    @Override
    public void onClick(View v) {
        try {
            switch (SPEAK_STATE) {
                //if the SPEAK_STATE is 0 means not working then start the text to speech survey
                case 0:
                    //tts speech listener
                    ttsEngine = new TextToSpeech(context, status -> {
                        if ( status == TextToSpeech.SUCCESS ){
                            //tts bot language
                            int result = ttsEngine.setLanguage(Locale.UK);
                            //check if the language isn't supported display a message in the LogCat for not found stuff
                            if ( result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED ){
                                Log.e("TTS", "Bad Format");
                            } else {
                                //tts Engine on
                                try {
                                    ttsEngine.speak(String.valueOf(Html.fromHtml(noteBox.getHtml(),0)), TextToSpeech.QUEUE_FLUSH, null,null);
                                } catch (NullPointerException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    });
                    //set the speak state to true or 1 means that we ve started the tts survey
                    SPEAK_STATE = 1;
                    break;
                case 1:
                    //stop the survey &shutdown its background processes if its started & we are w/in it
                    ttsEngine.shutdown();
                    //stop the engine
                    ttsEngine.stop();
                    //set the speak state to false or 0 means that we ve stopped the tts survey
                    SPEAK_STATE = 0;
                    break;
            }
        } catch (NullPointerException | IllegalStateException error) {
            error.printStackTrace();
        }
    }
}
