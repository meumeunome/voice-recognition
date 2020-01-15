package com.pedrotlf.voicerecognition

interface VoiceRecognitionIntentHandler {

    fun handle(intent: Int)

    fun getIntent(string: String): Int
}