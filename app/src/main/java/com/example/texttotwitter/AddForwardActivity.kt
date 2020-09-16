package com.example.texttotwitter

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.core.widget.addTextChangedListener
import kotlinx.android.synthetic.main.activity_add_forward.*

class AddForwardActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_add_forward)

        // Show the keyboard
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)

        // Only enable the next button if the user has entered text
        fromNumberInput.addTextChangedListener {e ->
            nextButton.isEnabled = e.toString().isNotEmpty()
        }
    }

    fun onClick(view: View) {
        when(view.id){
            nextButton.id -> {
                // This will start the next page, and set it up so that the result of the next page is
                // forwarded back to the main page.
                val intent = Intent(this, AddForwardActivity2::class.java)
                intent.putExtra(PHONE_NUMBER_INTENT, fromNumberInput.text.toString())
                intent.flags = Intent.FLAG_ACTIVITY_FORWARD_RESULT
                startActivity(intent)
                finish()
            }
        }
    }
}