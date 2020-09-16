package com.example.texttotwitter

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.telephony.SmsManager
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import com.twitter.sdk.android.core.TwitterSession
import com.twitter.sdk.android.core.internal.oauth.OAuthResponse
import kotlinx.android.synthetic.main.activity_add_forward2.*

class AddForwardActivity2 : AppCompatActivity() {
    private var url: String = ""
    private var oauthResponse: OAuthResponse? = null
    private var newPhoneNumber: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_forward2)

        newPhoneNumber = intent.getStringExtra(PHONE_NUMBER_INTENT)

        Thread{
            OAuthHelper.oauthFirstLeg(this, { resp ->
                url = OAuthHelper.oauthSecondLegURL(resp)
                oauthResponse = resp
                loadingSpinner.visibility = View.GONE
                successLayout.visibility = View.VISIBLE

            }, { error ->
                Log.e("RYANH", error.toString())
            })
        }.start()

        codeEntry.addTextChangedListener {e ->
            saveCodeButton.isEnabled = e.toString().isNotEmpty()
        }
    }

    fun onClick(view: View) {
        when(view.id){
            // Visit Twitter to get the code
            visitTwitterButton.id -> {
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
            }
            // Send the URL to the admin phone number
            sendToAdminButton.id -> {
                Thread{
                    val adminNumber = PreferencesUtil.getAdminNumber()
                    val body = "Approve the request on this page and enter the code it gives you in the app\n$url"
                    SmsManager.getDefault().sendTextMessage(adminNumber, null, body, null, null)
                    this.runOnUiThread { Toast.makeText(
                        this,
                        "The link was sent to the admin number",
                        Toast.LENGTH_LONG
                    ).show() }
                }.start()
            }
            // Save the code
            saveCodeButton.id ->{
                Thread{
                    OAuthHelper.oauthThirdLeg(this, oauthResponse!!, codeEntry.text.toString(),
                        { resp ->
                            val session = TwitterSession(resp.authToken, resp.userId, resp.userName)
                            PreferencesUtil.addRegisteredNumber(newPhoneNumber!!, session)
                            setResult(RESULT_OK)
                            finish()
                        }, { error ->
                            runOnUiThread { Toast.makeText(this, "Failed to save, try typing the code again", Toast.LENGTH_LONG).show() }
                            Log.e("RYANH", error.toString())
                        })
                }.start()
            }
        }
    }
}