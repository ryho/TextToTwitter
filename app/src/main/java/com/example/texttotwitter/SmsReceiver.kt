package com.example.texttotwitter

import android.Manifest
import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.provider.Telephony.Sms
import android.telephony.SmsManager
import android.telephony.TelephonyManager
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.twitter.sdk.android.core.TwitterApiClient
import com.twitter.sdk.android.core.TwitterAuthConfig
import com.twitter.twittertext.TwitterTextParser
import java.util.*
import kotlin.math.min

class SmsReceiver : BroadcastReceiver() {

    // onReceive is called by the OS when the phone receives an SMS.
    override fun onReceive(context: Context, intent: Intent) {
        val messages = Sms.Intents.getMessagesFromIntent(intent)
        if (messages.isEmpty()) {
            return
        }
        PreferencesUtil.init(context)

        // I can't find good documentation on this, but QKSMS always assumes that every message in
        // the same intent has the same originating address and timestamp, and it merges all of the
        // message bodies together as if they are all one message. I have observed this same
        // behavior when testing with messages that are longer than the SMS message limit.
        val fromNumber = messages[0].displayOriginatingAddress
        var fullBody = ""
        for (msg in messages) {
            fullBody += msg.displayMessageBody
        }

        // First attempt to interpret the message as a code from admin number
        val adminNumber = PreferencesUtil.getAdminNumber()
        if (adminNumber != null && fromNumber == adminNumber) {
            val parts = fullBody.split("\n")
            val code = parts[0].toUpperCase(Locale.ROOT)
            var param1: String? = null
            var param2: String? = null
            if (parts.size > 1 && parts[1].isNotEmpty()) {
                param1 = parts[1]
                if (parts.size > 2 && parts[2].isNotEmpty()) {
                    param2 = parts[2]
                }
            }
            when (code) {
                "API KEY" -> {
                    if (param2 != null) {
                        val authConfig = TwitterAuthConfig(param1, param2)
                        PreferencesUtil.setTwitterAuthConfig(authConfig)
                        context.sendBroadcast(Intent(KeyReceivedAction))
                        Toast.makeText(
                            context,
                            "API Key was set from admin number",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                    return
                }
                "SEND" -> {
                    if (param2 != null) {
                        reportMessage(param1, param2)
                        Toast.makeText(
                            context,
                            "Sending a text message from the admin number",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                    return
                }
            }
        }

        // Second, if there is a forward for this number, post the contents to Twitter
        val twitterSession = PreferencesUtil.getTwitterSessionForNumber(fromNumber)
        if (twitterSession != null) {
            // If we are recording texts from this number, then launch a thread to post the Tweet.
            // We are not allowed to make network requests from the SMS receiver thread.
            Thread {
                // Split the message into separate Tweets
                val tweetTexts = splitIntoTweets(fullBody)

                // Send the Tweets, with each replying the previous Tweet
                val client = TwitterApiClient(twitterSession)
                var inReplyToStatusId: Long? = null
                for (text in tweetTexts) {
                    val req = client.statusesService.update(
                        text,
                        inReplyToStatusId,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null
                    )

                    val resp = req.execute()
                    if (resp.isSuccessful) {
                        val tweet = resp.body()
                        inReplyToStatusId = tweet?.id
                    } else {
                        reportMessage(adminNumber, "Failed to post Tweet: ${resp.code()} ${resp.errorBody()?.byteStream().toString()}")
                        break
                    }
                }

                // Auto respond to action keywords
                val words = fullBody.split(" ")
                val keywords = PreferencesUtil.getResponseWords()
                for (word in words) {
                    for (keyword in keywords) {
                        if (word == keyword) {
                            SmsManager.getDefault().sendTextMessage(fromNumber, null, keyword, null, null)
                        }
                    }
                }
            }.start()
        } else {
            // Send the message to the admin if there was no mapping, since this is probably a mistake.
            // Ignore text messages from the device number. You can end up with a thundering herd of
            // messages if the carrier responds from your phone number when you send a bad message.
            // I experienced this by setting the admin number to an invalid number.
            if (fromNumber != devicePhoneNumber(context)) {
                val unknownNumberMessage =
                    "Message from unknown number " + messages[0].displayOriginatingAddress + "\ncontent:\n" + fullBody
                reportMessage(adminNumber, unknownNumberMessage)
            }
        }
    }

    // reportMessage sends a message to the Admin number if configured, or logs a message.
    private fun reportMessage(adminNumber: String?, body: String) {
        if (!adminNumber.isNullOrEmpty()) {
            // Start a thread, in case we are in a context where we are not allowed to send SMS.
            Thread {
                var texts = splitIntoTexts(body)
                // Prevent spamming in case the error is very large.
                texts = texts.subList(0, min(texts.size, MAX_SMS_FOR_ERROR))
                for (part in texts) {
                    SmsManager.getDefault().sendTextMessage(adminNumber, null, part, null, null)
                }
            }.start()
        } else {
            Log.e("ERROR", body)
        }
    }
}

// splitIntoTweets will take a message and will split it into multiple strings, each of which will
// fit in a Tweet. It starts with the Twitter Kit's determination of the max size of the Tweet, then
// moves it back to the start of the last word so that no words are broken.
fun splitIntoTweets(body: String): List<String> {
    val out = arrayListOf<String>()
    var currentText = body
    while (currentText.isNotEmpty()) {
        val res = TwitterTextParser.parseTweet(currentText)
        var realEnd = 0
        if (res.validTextRange.end+1 != currentText.length) {
            for (x in res.validTextRange.end+1 downTo 0) {
                if (currentText[x]== ' ') {
                    realEnd = x+1
                    break
                }
            }
        }

        if (realEnd == 0) {
            // If the whole thing contained no spaces, ship the whole thing. Otherwise this will never end.
            realEnd = res.validTextRange.end+1
        }
        val tweetText = currentText.substring(res.validTextRange.start, realEnd)
        currentText = currentText.substring(realEnd, currentText.length)
        out.add(tweetText)
    }
    return out
}

// splitIntoTexts will split the input after every 160 chars with no fanciness.
fun splitIntoTexts(body: String): List<String> {
    val out = arrayListOf<String>()
    var index = 0
    while (index < body.length) {
        out.add(body.substring(index, min(index + MAX_SMS_LENGTH, body.length)))
        index += MAX_SMS_LENGTH
    }
    return out
}

// This is a best effort way to get the device phone number sometimes. It is not always possible to get it,
// some carriers don't even tell the phone what its number is. It might be wrong if the number has changed.
// It might also contain garbage.
// https://stackoverflow.com/questions/2480288/programmatically-obtain-the-phone-number-of-the-android-phone
@SuppressLint("HardwareIds")
private fun devicePhoneNumber(context: Context): String {
    val manager = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
    if (ActivityCompat.checkSelfPermission(
            context,
            Manifest.permission.READ_SMS
        ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
            context,
            Manifest.permission.READ_PHONE_NUMBERS
        ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
            context,
            Manifest.permission.READ_PHONE_STATE
        ) != PackageManager.PERMISSION_GRANTED
    ) {
        // TODO: Consider calling
        //    ActivityCompat#requestPermissions
        // here to request the missing permissions, and then overriding
        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
        //                                          int[] grantResults)
        // to handle the case where the user grants the permission. See the documentation
        // for ActivityCompat#requestPermissions for more details.
        return ""
    }
    return manager.line1Number
}