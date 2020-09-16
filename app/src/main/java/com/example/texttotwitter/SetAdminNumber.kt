package com.example.texttotwitter

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.core.widget.addTextChangedListener
import kotlinx.android.synthetic.main.set_admin_number.*

class SetAdminNumber : AppCompatActivity() {
    override fun onCreate(savedInstanceState:Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.set_admin_number)

        // Show the keyboard
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)

        // Only enable the next button if the user has entered text
        adminNumberInput.addTextChangedListener {e ->
            saveButton.isEnabled = e.toString().isNotEmpty()
        }
    }

    fun onClick(view: View) {
        when(view.id) {
            saveButton.id -> {
                PreferencesUtil.setAdminNumber(adminNumberInput.text.toString())
                setResult(RESULT_OK)
                finish()
            }
        }
    }
}