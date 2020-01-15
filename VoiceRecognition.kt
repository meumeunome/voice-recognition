package com.pedrotlf.voicerecognition

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

//Your activity layout needs input_audio view to have a click listener
class VoiceRecognition(private val activity: Activity, language: String = "pt_BR") : RecognitionListener {

    private val AudioLogTag = "AudioInput"

    var voiceRecognitionIntentHandler: VoiceRecognitionIntentHandler? = null
    var voiceRecognitionOnResultListener: VoiceRecognitionOnResultListener? = null //Must have this
    var voiceRecognitionLayoutChanger: VoiceRecognitionLayoutChanger? = null

    var isListening = false

    private val intent: Intent
    private var speech: SpeechRecognizer = SpeechRecognizer.createSpeechRecognizer(activity)

    init {
        speech.setRecognitionListener(this)

        intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        intent.putExtra(
            RecognizerIntent.EXTRA_LANGUAGE_MODEL,
            RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
        )
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, language)
    }

    //It is important to put this function inside a clickListener
    fun listen(): Boolean {
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, arrayOf(Manifest.permission.RECORD_AUDIO), 1)
            return false
        }

        speech.startListening(intent)

        Log.i(AudioLogTag, "startListening")

        return true
    }

    //Use this if you want to stop listening but still get recognition results
    fun stopListening(){
        Log.i(AudioLogTag, "stopListening")

        speech.stopListening()
        isListening = false
    }

    override fun onReadyForSpeech(p0: Bundle?) {
        Log.i(AudioLogTag, "onReadyForSpeech")

        voiceRecognitionLayoutChanger?.startListeningChangeLayout()
        isListening = true
    }

    override fun onRmsChanged(p0: Float) {
//        Log.i(LOG_TAG, "onRmsChanged: $p0")
//        progressBar.setProgress((Int) p0)
    }

    override fun onBufferReceived(p0: ByteArray?) {
        Log.i(AudioLogTag, "onBufferReceived: $p0")
    }

    override fun onPartialResults(p0: Bundle?) {
        Log.i(AudioLogTag, "onPartialResults")
    }

    override fun onEvent(p0: Int, p1: Bundle?) {
        Log.i(AudioLogTag, "onEvent")
    }

    override fun onBeginningOfSpeech() {
        Log.i(AudioLogTag, "onBeginningOfSpeech")
    }

    override fun onEndOfSpeech() {
        Log.i(AudioLogTag, "onEndOfSpeech")

        voiceRecognitionLayoutChanger?.endListeningChangeLayout()
        isListening = false
    }

    override fun onError(p0: Int) {
        speech.cancel()
        val errorMessage = getErrorText(p0)
        Log.d(AudioLogTag, "FAILED: $errorMessage")
        voiceRecognitionLayoutChanger?.endListeningChangeLayout()
        isListening = false
    }

    override fun onResults(p0: Bundle?) {
        Log.i(AudioLogTag, "onResults")

        val results: ArrayList<String> = p0?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION) as ArrayList<String>

        val voiceIntent: Int? = voiceRecognitionIntentHandler?.getIntent(results[0])
        if (voiceIntent != null && voiceIntent != 0) {
            voiceRecognitionIntentHandler?.handle(voiceIntent)
            return
        }

        voiceRecognitionOnResultListener!!.onResult(results[0])
    }

    private fun getErrorText(errorCode: Int): String {
        val message: String
        when (errorCode) {
            SpeechRecognizer.ERROR_AUDIO -> message = "Audio recording error"

            SpeechRecognizer.ERROR_CLIENT -> message = "Client side error"

            SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> message = "Insufficient permissions"

            SpeechRecognizer.ERROR_NETWORK -> message = "Network error"

            SpeechRecognizer.ERROR_NETWORK_TIMEOUT -> message = "Network timeout"

            SpeechRecognizer.ERROR_NO_MATCH -> message = "No match"

            SpeechRecognizer.ERROR_RECOGNIZER_BUSY -> message = "RecognitionService busy"

            SpeechRecognizer.ERROR_SERVER -> message = "Error from server"

            SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> message = "No speech input"

            else -> message = "Didn't understand, please try again."
        }
        return message
    }

    //Use it in your overriden onPause function.
    fun onPause() {
        voiceRecognitionLayoutChanger?.endListeningChangeLayout()
        isListening = false

        speech.cancel()
        Log.i(AudioLogTag, "pause")
    }

    //Use it in your overriden onDestroy function.
    fun onDestroy() {
        speech.destroy()
    }
}