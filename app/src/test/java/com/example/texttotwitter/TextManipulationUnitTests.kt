package com.example.texttotwitter

import org.junit.Test

import org.junit.Assert.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class TextManipulationUnitTests {
    @Test
    fun splitIntoTweets_isCorrect() {
        val chars279 = "123456789 123456789 123456789 123456789 123456789 123456789 123456789 123456789 123456789 123456789 123456789 123456789 123456789 123456789 123456789 123456789 123456789 123456789 123456789 123456789 123456789 123456789 123456789 123456789 123456789 123456789 123456789 123456789"
        val chars279MinusLastWord = "123456789 123456789 123456789 123456789 123456789 123456789 123456789 123456789 123456789 123456789 123456789 123456789 123456789 123456789 123456789 123456789 123456789 123456789 123456789 123456789 123456789 123456789 123456789 123456789 123456789 123456789 123456789 "
        val emojis140 = "👍👍👍👍👍👍👍👍👍👍👍👍👍👍👍👍👍👍👍👍👍👍👍👍👍👍👍👍👍👍👍👍👍👍👍👍👍👍👍👍👍👍👍👍👍👍👍👍👍👍👍👍👍👍👍👍👍👍👍👍👍👍👍👍👍👍👍👍👍👍👍👍👍👍👍👍👍👍👍👍👍👍👍👍👍👍👍👍👍👍👍👍👍👍👍👍👍👍👍👍👍👍👍👍👍👍👍👍👍👍👍👍👍👍👍👍👍👍👍👍👍👍👍👍👍👍👍👍👍👍👍👍👍👍👍👍👍👍👍👍"
        val emojis139 = "👍👍👍👍👍👍👍👍👍👍👍👍👍👍👍👍👍👍👍👍👍👍👍👍👍👍👍👍👍👍👍👍👍👍👍👍👍👍👍👍👍👍👍👍👍👍👍👍👍👍👍👍👍👍👍👍👍👍👍👍👍👍👍👍👍👍👍👍👍👍👍👍👍👍👍👍👍👍👍👍👍👍👍👍👍👍👍👍👍👍👍👍👍👍👍👍👍👍👍👍👍👍👍👍👍👍👍👍👍👍👍👍👍👍👍👍👍👍👍👍👍👍👍👍👍👍👍👍👍👍👍👍👍👍👍👍👍👍👍"
        val skinToneEmojis140 = "\uD83D\uDC4D\uD83C\uDFFB\uD83D\uDC4D\uD83C\uDFFB\uD83D\uDC4D\uD83C\uDFFB\uD83D\uDC4D\uD83C\uDFFB\uD83D\uDC4D\uD83C\uDFFB\uD83D\uDC4D\uD83C\uDFFB\uD83D\uDC4D\uD83C\uDFFB\uD83D\uDC4D\uD83C\uDFFB\uD83D\uDC4D\uD83C\uDFFB\uD83D\uDC4D\uD83C\uDFFB\uD83D\uDC4D\uD83C\uDFFB\uD83D\uDC4D\uD83C\uDFFB\uD83D\uDC4D\uD83C\uDFFB\uD83D\uDC4D\uD83C\uDFFB\uD83D\uDC4D\uD83C\uDFFB\uD83D\uDC4D\uD83C\uDFFB\uD83D\uDC4D\uD83C\uDFFB\uD83D\uDC4D\uD83C\uDFFB\uD83D\uDC4D\uD83C\uDFFB\uD83D\uDC4D\uD83C\uDFFB\uD83D\uDC4D\uD83C\uDFFB\uD83D\uDC4D\uD83C\uDFFB\uD83D\uDC4D\uD83C\uDFFB\uD83D\uDC4D\uD83C\uDFFB\uD83D\uDC4D\uD83C\uDFFB\uD83D\uDC4D\uD83C\uDFFB\uD83D\uDC4D\uD83C\uDFFB\uD83D\uDC4D\uD83C\uDFFB\uD83D\uDC4D\uD83C\uDFFB\uD83D\uDC4D\uD83C\uDFFB\uD83D\uDC4D\uD83C\uDFFB\uD83D\uDC4D\uD83C\uDFFB\uD83D\uDC4D\uD83C\uDFFB\uD83D\uDC4D\uD83C\uDFFB\uD83D\uDC4D\uD83C\uDFFB\uD83D\uDC4D\uD83C\uDFFB\uD83D\uDC4D\uD83C\uDFFB\uD83D\uDC4D\uD83C\uDFFB\uD83D\uDC4D\uD83C\uDFFB\uD83D\uDC4D\uD83C\uDFFB\uD83D\uDC4D\uD83C\uDFFB\uD83D\uDC4D\uD83C\uDFFB\uD83D\uDC4D\uD83C\uDFFB\uD83D\uDC4D\uD83C\uDFFB\uD83D\uDC4D\uD83C\uDFFB\uD83D\uDC4D\uD83C\uDFFB\uD83D\uDC4D\uD83C\uDFFB\uD83D\uDC4D\uD83C\uDFFB\uD83D\uDC4D\uD83C\uDFFB\uD83D\uDC4D\uD83C\uDFFB\uD83D\uDC4D\uD83C\uDFFB\uD83D\uDC4D\uD83C\uDFFB\uD83D\uDC4D\uD83C\uDFFB\uD83D\uDC4D\uD83C\uDFFB\uD83D\uDC4D\uD83C\uDFFB\uD83D\uDC4D\uD83C\uDFFB\uD83D\uDC4D\uD83C\uDFFB\uD83D\uDC4D\uD83C\uDFFB\uD83D\uDC4D\uD83C\uDFFB\uD83D\uDC4D\uD83C\uDFFB\uD83D\uDC4D\uD83C\uDFFB\uD83D\uDC4D\uD83C\uDFFB\uD83D\uDC4D\uD83C\uDFFB\uD83D\uDC4D\uD83C\uDFFB\uD83D\uDC4D\uD83C\uDFFB\uD83D\uDC4D\uD83C\uDFFB\uD83D\uDC4D\uD83C\uDFFB\uD83D\uDC4D\uD83C\uDFFB\uD83D\uDC4D\uD83C\uDFFB\uD83D\uDC4D\uD83C\uDFFB\uD83D\uDC4D\uD83C\uDFFB\uD83D\uDC4D\uD83C\uDFFB\uD83D\uDC4D\uD83C\uDFFB\uD83D\uDC4D\uD83C\uDFFB\uD83D\uDC4D\uD83C\uDFFB\uD83D\uDC4D\uD83C\uDFFB\uD83D\uDC4D\uD83C\uDFFB\uD83D\uDC4D\uD83C\uDFFB\uD83D\uDC4D\uD83C\uDFFB\uD83D\uDC4D\uD83C\uDFFB\uD83D\uDC4D\uD83C\uDFFB\uD83D\uDC4D\uD83C\uDFFB\uD83D\uDC4D\uD83C\uDFFB\uD83D\uDC4D\uD83C\uDFFB\uD83D\uDC4D\uD83C\uDFFB\uD83D\uDC4D\uD83C\uDFFB\uD83D\uDC4D\uD83C\uDFFB\uD83D\uDC4D\uD83C\uDFFB\uD83D\uDC4D\uD83C\uDFFB\uD83D\uDC4D\uD83C\uDFFB\uD83D\uDC4D\uD83C\uDFFB\uD83D\uDC4D\uD83C\uDFFB\uD83D\uDC4D\uD83C\uDFFB\uD83D\uDC4D\uD83C\uDFFB\uD83D\uDC4D\uD83C\uDFFB\uD83D\uDC4D\uD83C\uDFFB\uD83D\uDC4D\uD83C\uDFFB\uD83D\uDC4D\uD83C\uDFFB\uD83D\uDC4D\uD83C\uDFFB\uD83D\uDC4D\uD83C\uDFFB\uD83D\uDC4D\uD83C\uDFFB\uD83D\uDC4D\uD83C\uDFFB\uD83D\uDC4D\uD83C\uDFFB\uD83D\uDC4D\uD83C\uDFFB\uD83D\uDC4D\uD83C\uDFFB\uD83D\uDC4D\uD83C\uDFFB\uD83D\uDC4D\uD83C\uDFFB\uD83D\uDC4D\uD83C\uDFFB\uD83D\uDC4D\uD83C\uDFFB\uD83D\uDC4D\uD83C\uDFFB\uD83D\uDC4D\uD83C\uDFFB\uD83D\uDC4D\uD83C\uDFFB\uD83D\uDC4D\uD83C\uDFFB\uD83D\uDC4D\uD83C\uDFFB\uD83D\uDC4D\uD83C\uDFFB\uD83D\uDC4D\uD83C\uDFFB\uD83D\uDC4D\uD83C\uDFFB\uD83D\uDC4D\uD83C\uDFFB\uD83D\uDC4D\uD83C\uDFFB\uD83D\uDC4D\uD83C\uDFFB\uD83D\uDC4D\uD83C\uDFFB\uD83D\uDC4D\uD83C\uDFFB\uD83D\uDC4D\uD83C\uDFFB\uD83D\uDC4D\uD83C\uDFFB\uD83D\uDC4D\uD83C\uDFFB\uD83D\uDC4D\uD83C\uDFFB\uD83D\uDC4D\uD83C\uDFFB\uD83D\uDC4D\uD83C\uDFFB\uD83D\uDC4D\uD83C\uDFFB\uD83D\uDC4D\uD83C\uDFFB\uD83D\uDC4D\uD83C\uDFFB\uD83D\uDC4D\uD83C\uDFFB\uD83D\uDC4D\uD83C\uDFFB\uD83D\uDC4D\uD83C\uDFFB\uD83D\uDC4D\uD83C\uDFFB\uD83D\uDC4D\uD83C\uDFFB\uD83D\uDC4D\uD83C\uDFFB\uD83D\uDC4D\uD83C\uDFFB\uD83D\uDC4D\uD83C\uDFFB\uD83D\uDC4D\uD83C\uDFFB"

        class testCase (
            val description: String,
            val input: String,
            val expected :ArrayList<String>
        )

        val testCases = arrayOf(
            testCase(
                "279 characters is fine",
                chars279,
                arrayListOf(chars279)
            ),
            testCase(
                "280 characters is fine",
                chars279+"1",
                arrayListOf(chars279+"1")
            ),
            testCase(
                "280 characters but ends with space",
                chars279+" ",
                arrayListOf(chars279+" ")
            ),
            testCase(
                "281 characters gets split",
                chars279+"12",
                arrayListOf(chars279MinusLastWord,"123456789"+"12")
            ),
            testCase(
                "Makes three tweets",
                chars279+" "+chars279+"12",
                arrayListOf(chars279 + " ", chars279MinusLastWord, "123456789"+"12")
            ),
            testCase(
                "Emojis count as two chars and they never get split",
                "a"+emojis140,
                arrayListOf("a"+emojis139,"\uD83D\uDC4D")
            ),
            testCase(
                "Skin tone emojis count as two chars too, same as a regular emoji",
                skinToneEmojis140,
                arrayListOf(skinToneEmojis140)
            )
        )
        for (testCase in testCases) {
            val output = splitIntoTweets(testCase.input)
            assertEquals(testCase.expected, output)
        }
    }

    @Test
    fun splitIntoTexts_isCorrect() {
        val chars160Long = "1234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890"

        class testCase (
            val description: String,
            val input: String,
            val expected :ArrayList<String>
        )

        val testCases = arrayOf(
            testCase(
                "Exactly 160 chars long - one text",
                chars160Long,
                arrayListOf(chars160Long)
            ),
            testCase(
                "161 chars long - two texts",
                chars160Long +"A",
                arrayListOf(chars160Long, "A")
            )
        )
        for (testCase in testCases) {
            val output = splitIntoTexts(testCase.input)
            assertEquals(testCase.expected, output)
        }
    }
}