# voice-recognition
Voice Recognition Android Package, ready to use

## Usage
```
val voiceRecognition: VoiceRecognition = VoiceRecognition(this@YourActivity)
```

### OnResultListener
You need to create a class that implments the **VoiceRecognitionOnResultListener** interface overriding its **onResult(result: String)** method (called when listening ends).

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

### Layout Changer

If you want your layout to change while the app is listening, you can create an implementation to **VoiceRecognitionLayoutChanger** interface (You can do anything here, not only changing layout, but it will only happens when it starts or end listening, without collecting any information from user speech). You just need to override the **startListeningChangeLayout()** and **endListeningChangeLayout()** functions.

##### Example
```
class CustomVoiceRecognitionLayoutChanger(private val activity: ChatActivity):
  VoiceRecognitionLayoutChanger {

    override fun startListeningChangeLayout() {
        activity.editText.visibility = View.GONE
        activity.sendButton.visibility = View.GONE
        
        activity.speakingView.visibility = View.VISIBLE
    }

    override fun endListeningChangeLayout() {        
        activity.editText.visibility = View.VISIBLE
        activity.sendButton.visibility = View.VISIBLE
        
        activity.speakingView.visibility = View.GONE
    }
}
```

### Intent Handler

And finally, if you want your app to handle certain words or sentences, you can create an implementation to **VoiceRecognitionIntentHandler** interface. You'll need to override the **getIntent(string: String): Int** and **handle(intent: Int)** functions:

#### Get Intent

The **getIntent(string: String): Int** function is where you will capture user's intent given it's speech result (string parameter). After you capture the intent, you will return an integer representing it, where you will handle at the **handle(intent: Int)** function. If no intent was found, you must return 0, because onResult is just called when no Intent was found. I recommend you to use a Kotlin Object file (maybe called "Constants") where you can storage those integer constants. **ATTENTION! If no intent was found, the return must be 0, otherwise, onResult won't be called!** 

#### Handle

Now that you have the intent, you can finally handle it with the **handle(intent: Int)** function. You can do anything here with the captured intent. It's highly recommended to use **when(intent){}** inside it.

##### Example

```
class BotVoiceRecognitionIntentHandler(private val activity: BotActivity):
    VoiceRecognitionIntentHandler {

    override fun handle(intent: Int){
        when(intent) {
            Constants.INNER_INTENT_SEND_LOCATION -> {
                activity.botLocationProvider.getLastLocation(send = true)
            }
        }
    }

    override fun getIntent(string: String): Int {
        if(Utils.containsStringFromList(string, listOf("send", "sends"))){
            if(Utils.containsStringFromList(string, listOf("location", "local"))){
                return Constants.INNER_INTENT_SEND_LOCATION
            }
        }

        return 0
    }
}
