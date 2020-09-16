package com.example.texttotwitter

import android.content.Context
import android.net.Uri
import android.util.Log
import com.android.volley.AuthFailureError
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.twitter.sdk.android.core.Twitter
import com.twitter.sdk.android.core.internal.oauth.OAuth1aHeaders
import com.twitter.sdk.android.core.internal.oauth.OAuth1aService
import com.twitter.sdk.android.core.internal.oauth.OAuthConstants
import com.twitter.sdk.android.core.internal.oauth.OAuthResponse
import java.nio.charset.StandardCharsets

// OAuthHelper uses some parts of the Twitter Kit SDK and does some things manually.
// I don't think that you can do can get the Twitter Kit to do all of this because I am using
// the oob (out of band, i.e. PIN-based) feature.
object OAuthHelper {
    fun oauthFirstLeg(context: Context, onSuccess: (OAuthResponse) -> Unit, onError: (VolleyError) -> Unit) {
        val volleyQueue = Volley.newRequestQueue(context)

        val getRequest: StringRequest = object : StringRequest(
            Method.POST, "https://api.twitter.com/oauth/request_token",
            Response.Listener { response ->
                val oauthResponse = OAuth1aService.parseAuthResponse(response)
                onSuccess(oauthResponse)
            },
            Response.ErrorListener { error ->
                val errorMessage = String((error as AuthFailureError).networkResponse.data, StandardCharsets.UTF_8)
                Log.e("ERROR", "error => $error message => $errorMessage")
                onError(error)
            }
        ) {
            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {
                val headerString = OAuth1aHeaders().getAuthorizationHeader(
                    Twitter.getInstance().twitterAuthConfig, null, "oob", "POST", "https://api.twitter.com/oauth/request_token", null
                )
                return hashMapOf(OAuthConstants.HEADER_AUTHORIZATION to headerString)
            }
        }
        volleyQueue.add(getRequest)
    }

    // oauthSecondLegURL creates the URL that you need to send the user to based on the first leg response.
    fun oauthSecondLegURL(resp: OAuthResponse): String {
        val uri = Uri.parse("https://api.twitter.com/oauth/authenticate").buildUpon()
        uri.appendQueryParameter(OAuthConstants.PARAM_TOKEN, resp.authToken.token)
        return uri.toString()
    }

    fun oauthThirdLeg(context: Context, resp: OAuthResponse, code: String, onSuccess: (OAuthResponse) -> Unit, onError: (VolleyError)-> Unit) {
        val volleyQueue = Volley.newRequestQueue(context)
        val uri = Uri.parse("https://api.twitter.com/oauth/access_token").buildUpon()
        uri.appendQueryParameter(OAuthConstants.PARAM_TOKEN, resp.authToken.token)
        uri.appendQueryParameter(OAuthConstants.PARAM_VERIFIER, code)

        val getRequest = StringRequest(
            Request.Method.POST, uri.toString(),
            { response ->
                val oauthResponse = OAuth1aService.parseAuthResponse(response)
                onSuccess(oauthResponse)
            },
            { error ->
                val errorMessage = String((error as AuthFailureError).networkResponse.data, StandardCharsets.UTF_8)
                Log.e("ERROR", "error => $error message => $errorMessage")
                onError(error)
            }
        )
        volleyQueue.add(getRequest)
    }
}
