package com.example.texttotwitter

import android.content.Context
import android.content.SharedPreferences
import com.twitter.sdk.android.core.TwitterAuthConfig
import com.twitter.sdk.android.core.TwitterAuthToken
import com.twitter.sdk.android.core.TwitterSession

// PreferencesUtil contains getters and setters for all of the data that this app stores.
// The keys are private so that all writes and reads go through this class for sanity.
// init must be called before this can be used.
object PreferencesUtil {
    // Keys for all of the values that are stored in the sharedPreferences file.
    private const val RegisteredNumbersKey = "REGISTERED_NUMBERS"
    private const val ResponseWordsKey = "RESPONSE_WORDS"
    private const val AdminNumberKey = "ADMIN_NUMBER"
    private const val TwitterHandleKey = "_TWITTER_HANDLE"
    private const val TwitterApiKey = "TWITTER_API_KEY"
    private const val TwitterSecretKey = "TWITTER_SECRET_KEY"
    private const val TwitterOAuthTokenKey = "_OAUTH_TOKEN"
    private const val TwitterOAuthTokenSecretKey = "_OAUTH_TOKEN_SECRET"
    private const val TwitterUserIdKey = "_USER_ID"

    private var preferences: SharedPreferences? = null

    fun init(context: Context) {
        if (preferences == null) {
            preferences = context.getSharedPreferences(context.packageName, Context.MODE_PRIVATE)
        }
    }

    fun getAdminNumber(): String? {
        return preferences!!.getString(AdminNumberKey, null)
    }

    fun setAdminNumber(adminNumber: String) {
        preferences!!.edit().putString(AdminNumberKey, adminNumber).apply()
    }

    fun getRegisteredNumbers(): Set<String> {
        return preferences!!.getStringSet(RegisteredNumbersKey, mutableSetOf<String>())!!
    }

    fun getResponseWords(): Set<String> {
        return preferences!!.getStringSet(ResponseWordsKey, mutableSetOf<String>())!!
    }

    fun removeRegisteredNumber(phoneNumber: String) {
        val editor = preferences!!.edit()
        editor.remove(phoneNumber + TwitterHandleKey)
        editor.remove(phoneNumber + TwitterOAuthTokenKey)
        editor.remove(phoneNumber + TwitterUserIdKey)
        val numbers = preferences!!.getStringSet(RegisteredNumbersKey, mutableSetOf<String>())
        numbers!!.remove(phoneNumber)
        editor.putStringSet(RegisteredNumbersKey, numbers)
        editor.apply()
    }

    fun addRegisteredNumber(phoneNumber: String, session: TwitterSession) {
        val prefEdit = preferences!!.edit()
        val numbers = preferences!!.getStringSet(RegisteredNumbersKey, mutableSetOf<String>())
        numbers?.add(phoneNumber)
        prefEdit.putStringSet(RegisteredNumbersKey, numbers)
        prefEdit.putString(phoneNumber + TwitterHandleKey, session.userName)
        prefEdit.putString(phoneNumber + TwitterOAuthTokenKey, session.authToken.token)
        prefEdit.putString(phoneNumber + TwitterOAuthTokenSecretKey, session.authToken.secret)
        prefEdit.putLong(phoneNumber + TwitterUserIdKey, session.userId)
        prefEdit.apply()
    }

    fun getTwitterSessionForNumber(phoneNumber: String): TwitterSession? {
        val authKey = preferences!!.getString(phoneNumber + TwitterOAuthTokenKey, null)
        val authSecret = preferences!!.getString(phoneNumber + TwitterOAuthTokenSecretKey,null)
        val userId = preferences!!.getLong(phoneNumber + TwitterUserIdKey, 0)
        val handle = preferences!!.getString(phoneNumber + TwitterHandleKey, null)
        if (authKey != null && authSecret != null && userId != 0L && handle != null) {
            return TwitterSession(TwitterAuthToken(authKey, authSecret), userId, handle)
        }
        return null
    }

    fun getTwitterAuthConfig(): TwitterAuthConfig? {
        val apiKey = preferences!!.getString(TwitterApiKey, null)
        val apiSecret = preferences!!.getString(TwitterSecretKey, null)
        if (apiKey != null && apiSecret != null) {
            return TwitterAuthConfig(apiKey, apiSecret)
        }
        return null
    }

    fun setTwitterAuthConfig(authConfig: TwitterAuthConfig) {
        preferences!!.edit().
        putString(TwitterApiKey, authConfig.consumerKey).
        putString(TwitterSecretKey, authConfig.consumerSecret).
        apply()
    }

    fun setResponseWords(words: Set<String>) {
        preferences!!.edit().
        putStringSet(ResponseWordsKey, words).
        apply()
    }
}