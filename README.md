# Text To Twitter
This is an Android app that receives text messages (SMS) and posts their contents to Twitter.

I created this app to forward texts from 88022 (Trump Campaign) to [@TrumpSMS](https://twitter.com/TrumpSMS) and 30330
(Biden Campaign) to [@BidenTexts](https://twitter.com/BidenTexts). Presumably this will be useful for other purposes.

![Example Image](/images/screenshot.png)

## Features
- Forwards SMS from multiple numbers to multiple Twitter accounts
- Auto respond to texts that contain a keyword (YES etc.)
- Remote text message from admin number
- Remote API credential updates from admin number
- Reports all errors and SMS from unknown numbers to the admin number

## Requires
- Android phone with at least Android 4.4 KitKat
- Active SMS plan
- Twitter account
- Twitter API credentials (which now requires submitting an application)

## Admin Feature
This has a remote admin feature so that all of the settings can be controlled via SMS. This prevents
you from having to walk to your closet where you keep your Android phone to change the settings.
You can also send text messages from the device's number to other numbers, from your admin number.
This is useful because sometimes automated accounts ask you to opt-in for even more text messages 
(Biden's Campaign asked for permission to send extra fact check messages during the RNC).
This remote feature allows opting in to these prompts when you do not have physical access to the phone.
This does, of course, require you to be paying attention. To avoid this issue you can add common keywords to 
the auto response feature.

## Cell Plan Requirement
This app requires the Android device to have an SMS plan. MMS require both an SMS plan and a data plan.
You can't use a clone of your SIM. You can not use Twilio numbers to [send to or receive from short codes](https://support.twilio.com/hc/en-us/articles/223181668-Can-Twilio-numbers-receive-SMS-from-a-short-code-),
which basically every automated account uses. Only Ma Bell can communicate with short codes. 
I personally found that [US Mobile](https://usmobile.com) is a great phone service for this purpose.
The SIM card is free with a promo code that is found on their home page, and you can get SMS plans as 
small as 50 texts per month for $1.50 up to unlimited for $6 and data plans as small as 50 MB per month
for another $1.50. There is a $2 monthly service fee and you have to pay some taxes as well. Additionally,
you can pause your plan very easily and I have found that subscription changes take effect instantly,
which is convenient during testing.

## Technologies
This Android app is written in Kotlin. I use parts of [Twitter Kit for Android](https://github.com/twitter-archive/twitter-kit-android)
to communicate with Twitter, even though it is no longer maintained. I also use [twitter-text](https://github.com/twitter/twitter-text/tree/master/java)
to split the SMS into separate Tweet-sized messages.
I used the source code of [QKSMS](https://github.com/moezbhatti/qksms), an open source SMS app, as a
reference for how to interact with Android SMS capabilities, though I did not copy any code.