package com.example.texttotwitter

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.view.View
import android.view.WindowManager
import kotlinx.android.synthetic.main.set_response_words.*
import java.util.*

class SetResponseWords : AppCompatActivity() {
    override fun onCreate(savedInstanceState:Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.set_response_words)

        // Show the keyboard
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)

        regenAutoResponseText()
    }

    fun onClick(view: View) {
        when(view.id) {
            saveButton.id -> {
                val inputWords = responseWordsText.text.toString().split(",")
                val saveWords = mutableSetOf<String>()
                for (word in inputWords) {
                    val trimWord = word.trim()
                    if (trimWord.isNotEmpty()) {
                        saveWords.add(trimWord.toUpperCase(Locale.ROOT))
                    }
                }
                PreferencesUtil.setResponseWords(saveWords)
                setResult(RESULT_OK)
                finish()
            }
        }
    }

    // regenAutoResponseText will put the current response words in the edit text box.
    private fun regenAutoResponseText() {
        responseWordsText.setText(PreferencesUtil.getResponseWords().joinToString(","))
    }
}