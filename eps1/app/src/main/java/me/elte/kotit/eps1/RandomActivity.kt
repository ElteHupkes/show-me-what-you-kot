package me.elte.kotit.eps1

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import me.elte.kotit.eps1.databinding.ActivityRandomBinding
import kotlin.random.Random

class RandomActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRandomBinding

    /**
     * The number we'll be communicating back
     */
    private var _myRandomNumber = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityRandomBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        // Doing the exact same as in the fragment here
        _myRandomNumber = savedInstanceState?.getInt(NUMBER_KEY, -1) ?: -1;
        if (_myRandomNumber < 0) {
            generateNumber()
        }
        updateNumberText()

        binding.includedFragment.buttonRegenerate.setOnClickListener {
            generateNumber()
            updateNumberText()
        }
        binding.includedFragment.buttonSend.setOnClickListener { sendResult() }
    }

    /**
     * Generates a new random number and updates the text view
     */
    fun generateNumber() {
        _myRandomNumber = Random.nextInt(0, 1000)
    }

    fun updateNumberText() {
        binding.includedFragment.randomNumber.text =
            resources.getString(R.string.your_number, _myRandomNumber)
    }

    fun sendResult() {
        val intent = Intent().apply {
            putExtra(NUMBER_KEY, _myRandomNumber)
        }
        setResult(Activity.RESULT_OK, intent)
        finish()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(NUMBER_KEY, _myRandomNumber)
    }
}