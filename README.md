# voice-recognition
Voice Recognition Android Package, ready to use

### Usage
```
val voiceRecognition: VoiceRecognition = VoiceRecognition(this)
```

You need to create a class that implments the interface **VoiceRecognitionOnResultListener** overriding its onResult(result: String) method (called when listening ends).

```
voiceRecognition.voiceRecognitionOnResultListener = CustomVoiceRecognitionOnResultListener()
```

##### Example
```
class CustomVoiceRecognitionOnResultListener(private val activity: ChatActivity): 
  VoiceRecognitionOnResultListener{
  
  override fun onResult(result: String){
    val upperString = result.substring(0,1).toUpperCase() + result.substring(1)
    activity.insertMessageAtChat(Message(upperString,
      Utils.getNowTime(),
      Constants.MESSAGE_TYPE_SENT
    ))
  }
}
```

