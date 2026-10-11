package com.example.util

import android.content.Context
import android.speech.tts.TextToSpeech
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.util.Locale

class VoiceActingManager(context: Context) {
    private var tts: TextToSpeech? = null
    private var isReady = false

    private val _currentlySpeaking = MutableStateFlow<String?>(null)
    val currentlySpeaking: StateFlow<String?> = _currentlySpeaking

    init {
        tts = TextToSpeech(context.applicationContext) { status ->
            if (status == TextToSpeech.SUCCESS) {
                tts?.language = Locale.US
                isReady = true
            }
        }
    }

    fun speakLine(speaker: String, roleTag: String, text: String, enabled: Boolean = true) {
        _currentlySpeaking.value = "$speaker: $text"
        if (!enabled || !isReady) return

        val cleanText = text.replace(Regex("\\(.*?\\)"), "").trim()
        when (roleTag) {
            "HERO" -> {
                tts?.setPitch(1.18f)
                tts?.setSpeechRate(1.03f)
            }
            "PARTNER" -> {
                tts?.setPitch(0.86f)
                tts?.setSpeechRate(0.98f)
            }
            "K9" -> {
                tts?.setPitch(0.65f)
                tts?.setSpeechRate(1.15f)
            }
            "CRIMINAL" -> {
                tts?.setPitch(0.72f)
                tts?.setSpeechRate(0.93f)
            }
            else -> {
                tts?.setPitch(1.0f)
                tts?.setSpeechRate(1.05f)
            }
        }
        tts?.speak(cleanText, TextToSpeech.QUEUE_FLUSH, null, "line_${System.currentTimeMillis()}")
    }

    fun stop() {
        _currentlySpeaking.value = null
        tts?.stop()
    }

    fun shutdown() {
        tts?.stop()
        tts?.shutdown()
    }
}
