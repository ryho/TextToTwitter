package com.example.texttotwitter

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.telephony.PhoneNumberUtils
import android.util.Log
import android.view.*
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.setPadding
import com.twitter.sdk.android.core.Twitter
import com.twitter.sdk.android.core.TwitterConfig
import com.twitter.sdk.android.core.internal.UserUtils
import kotlinx.android.synthetic.main.activity_main.*


const val ACTIVITY_RESULT__NEW_FORWARD = 1
const val ACTIVITY_RESULT__SET_ADMIN_NUMBER = 2

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        PreferencesUtil.init(this)

        // Initialize admin number text
        regenAdminNumberDisplay()

        // Sets up the Twitter API client from the saved preferences
        regenTwitterAPI()

        // Sets up the table with the list of forwards from the saved preferences
        regenForwardsTable()

        // Register the key received BroadcastReceiver
        // This allows the code that receives the text message with the new API key to call the MainActivity
        val intentFilter = IntentFilter(KeyReceivedAction)
        registerReceiver(APIKeyReceiver(), intentFilter)
    }

    private fun regenAdminNumberDisplay() {
        val adminNumber = PreferencesUtil.getAdminNumber()
        if (adminNumberTextView == null) {
            adminNumberTextView.text = getString(R.string.admin_not_set)
        } else {
            @Suppress("DEPRECATION")
            adminNumberTextView.text = PhoneNumberUtils.formatNumber(adminNumber)
        }
    }

    fun regenTwitterAPI() {
        val authConfig = PreferencesUtil.getTwitterAuthConfig()
        if (authConfig != null ) {
            credentialStatus.text = getString(R.string.yes)
            val twitterConfig = TwitterConfig.Builder(this)
                .twitterAuthConfig(authConfig)
                //.debug(true)
                .build()
            Twitter.initialize(twitterConfig)

        } else {
            credentialStatus.text = getString(R.string.no)
        }
    }

    // regenForwardsTable will clear the forwards table in the UI and fill it in based on the
    // settings stored in the shared preferences.
    private fun regenForwardsTable() {
        // Start from scratch
        forwardsTable.removeAllViews()
        val phoneNumbers = PreferencesUtil.getRegisteredNumbers()
        if (phoneNumbers.isEmpty()) {
            // If there are no forwards, show "None"
            val row = TableRow(this)
            val none = TextView(this)
            none.text = getString(R.string.none)
            row.addView(none)
            forwardsTable.addView(row)
        } else {
            // Initialize each of the registered number-to-Twitter forwards
            for (phoneNumber in phoneNumbers) {
                val session = PreferencesUtil.getTwitterSessionForNumber(phoneNumber)
                if (session == null) {
                    Log.e(
                        "ERROR",
                        "Expected to find a Twitter session for number $phoneNumber but did not"
                    )
                    continue
                }

                // First initialize the entry in the table
                val row = TableRow(this)
                row.layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )

                val numberView = TextView(this)
                @Suppress("DEPRECATION")
                numberView.text = PhoneNumberUtils.formatNumber(phoneNumber)
                numberView.gravity = Gravity.BOTTOM.or(Gravity.START)
                row.addView(numberView)

                val twitterHandleView = TextView(this)
                twitterHandleView.text = UserUtils.formatScreenName(session.userName)
                twitterHandleView.gravity = Gravity.BOTTOM.or(Gravity.START)
                row.addView(twitterHandleView)

                val deleteButton = ImageButton(this)
                deleteButton.tag = phoneNumber
                deleteButton.setImageResource(android.R.drawable.ic_delete)
                deleteButton.setPadding(5)
                deleteButton.setOnClickListener(this::deleteForwardButtonListener)
                row.addView(deleteButton)
                forwardsTable.addView(row)
            }
        }
    }

    fun onClick(view: View) {
        when(view.id) {
            (editAdminButton.id) -> {
                val intent = Intent(this, SetAdminNumber::class.java)
                startActivityForResult(intent, ACTIVITY_RESULT__SET_ADMIN_NUMBER)
            }
            (addButton.id) -> {
                val intent = Intent(this, AddForwardActivity::class.java)
                startActivityForResult(intent, ACTIVITY_RESULT__NEW_FORWARD)
            }
            (changeApiCredentials.id) -> {
                val intent = Intent(this, SetCredentials::class.java)
                startActivityForResult(intent, ACTIVITY_RESULT__NEW_FORWARD)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            ACTIVITY_RESULT__NEW_FORWARD -> {
                if (resultCode == RESULT_OK) {
                    // TODO Extract the data returned from the child Activity.
//                    val returnValue = data?.getStringExtra("some_key")
                    regenForwardsTable()
                    Toast.makeText(this, "Added a new forward!", Toast.LENGTH_LONG).show()
                }
            }

            ACTIVITY_RESULT__SET_ADMIN_NUMBER -> {
                if (resultCode == RESULT_OK) {
                    regenAdminNumberDisplay()
                    Toast.makeText(this, "Updated the admin number!", Toast.LENGTH_LONG).show()
                }
            }
        }
        // Hide the keyboard
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)
    }

    // deleteForwardButtonListener deletes the forward from the preferences, from the in memory
    // phoneNumberToSessionId map and from the UI.
    private fun deleteForwardButtonListener(button: View) {
        val phoneNumber = button.tag.toString()
        val session = PreferencesUtil.getTwitterSessionForNumber(phoneNumber)
        AlertDialog.Builder(this)
            .setTitle("Delete")
            .setMessage("Do you really want to delete the forward from " + phoneNumber + " to @" + session!!.userName + "?")
            .setIcon(android.R.drawable.ic_dialog_alert)
            .setPositiveButton(
                android.R.string.yes
            ) { _, _->
                PreferencesUtil.removeRegisteredNumber(phoneNumber)
                regenForwardsTable()
                Toast.makeText(
                    this@MainActivity,
                    "Deleted the forward for " + phoneNumber + " to " + session.userName + ".",
                    Toast.LENGTH_LONG
                ).show()
            }
            .setNegativeButton(android.R.string.no, null).show()
    }
}

// This allows the code that receives the text message with the API key to call the MainActivity
// so that the API Key status indication can be updated
internal class APIKeyReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, arg1: Intent) {
        (context as MainActivity).regenTwitterAPI()
    }
}