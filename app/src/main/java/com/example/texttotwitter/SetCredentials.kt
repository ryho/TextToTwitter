package com.example.texttotwitter

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.widget.addTextChangedListener
import com.twitter.sdk.android.core.TwitterAuthConfig
import kotlinx.android.synthetic.main.content_set_credentials.*

class SetCredentials : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_set_credentials)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true);
        // Only enable the next button if the user has entered text in both boxes
        val addTextChangedListener = apiKeyInput.addTextChangedListener {
            saveButton.isEnabled =
                apiKeyInput.text.isNotEmpty() && apiSecretKeyInput.text.isNotEmpty()
        }
        apiSecretKeyInput.addTextChangedListener(addTextChangedListener)
    }

    fun onClick(view: View) {
        when(view.id){
            saveButton.id -> {
                PreferencesUtil.setTwitterAuthConfig(TwitterAuthConfig(apiKeyInput.text.toString(), apiSecretKeyInput.text.toString()))
                setResult(RESULT_OK)
                finish()
            }
        }
    }
}