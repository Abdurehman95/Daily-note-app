package com.efoy.money.lab.utils

import android.content.Context
import android.speech.tts.TextToSpeech
import android.util.Log
import java.util.Locale

class TextToSpeechHelper(context: Context) : TextToSpeech.OnInitListener {
    
    private var tts: TextToSpeech? = null
    private var isReady = false

    init {
        // Instantiate the system service
        tts = TextToSpeech(context.applicationContext, this)
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val result = tts?.setLanguage(Locale.US)
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("TextToSpeechHelper", "Language is not supported or missing data")
            } else {
                isReady = true
            }
        } else {
            Log.e("TextToSpeechHelper", "TTS Initialization failed")
        }
    }

    fun speak(text: String) {
        if (isReady && tts != null) {
            // Stop any playing speech and flush queue with new quote
            tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "MINDFLOW_TTS_ID")
        }
    }

    fun stop() {
        if (isReady) {
            tts?.stop()
        }
    }

    fun shutdown() {
        tts?.stop()
        tts?.shutdown()
        tts = null
        isReady = false
    }
}
