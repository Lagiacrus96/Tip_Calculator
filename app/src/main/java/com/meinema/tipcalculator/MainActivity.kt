package com.meinema.tipcalculator

import android.animation.ArgbEvaluator
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.EditText
import android.widget.SeekBar
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat

private const val TAG = "MainActivity"
private const val INITIAL_TIP_PERCENT = 15
private var isDarkTheme = true

class MainActivity : AppCompatActivity() {
    private lateinit var baseAmount: EditText
    private lateinit var tipPercentBar: SeekBar
    private lateinit var tipAmount: TextView
    private lateinit var tipPercent: TextView
    private lateinit var totalAmount: TextView
    private lateinit var tipDescription: TextView
    private lateinit var peopleLabel: TextView
    private lateinit var splitLabel: TextView
    private lateinit var peopleAmount: EditText
    private lateinit var splitAmount: TextView
    private lateinit var themeSwitch: Switch

    override fun onCreate(savedInstanceState: Bundle?) {
        applyTheme()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main) // UI of this screen is defined by activity_main.xml
        baseAmount = findViewById(R.id.baseAmount)
        tipPercentBar= findViewById(R.id.tipPercentBar)
        tipAmount = findViewById(R.id.tipAmount)
        tipPercent = findViewById(R.id.tipPercent)
        totalAmount = findViewById(R.id.totalAmount)
        tipDescription = findViewById(R.id.tipDescription)
        peopleLabel = findViewById(R.id.peopleLabel)
        splitLabel = findViewById(R.id.splitLabel)
        peopleAmount = findViewById(R.id.peopleAmount)
        peopleAmount.setText("1")
        splitAmount = findViewById(R.id.splitAmount)
        themeSwitch = findViewById(R.id.themeSwitch)

        tipPercentBar.progress = INITIAL_TIP_PERCENT
        tipPercent.text = "$INITIAL_TIP_PERCENT%"

        updateTipDescription(INITIAL_TIP_PERCENT)
        tipPercentBar.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                Log.i(TAG, "onProgressChanged $progress")
                tipPercent.text = "$progress%"
                computeTipAndTotal()
                updateTipDescription(progress)

            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}

            override fun onStopTrackingTouch(seekBar: SeekBar?) {}

        })
        baseAmount.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                Log.i(TAG, "afterTextChanged $s")
                computeTipAndTotal()
            }

        })
        peopleAmount.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                Log.i(TAG, "afterTextChanged $s")
                computeTipAndTotal()
            }

        })

        themeSwitch.setOnCheckedChangeListener { buttonView, isChecked ->
            isDarkTheme = isChecked

            if (isChecked) {
                // Set theme to dark
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                Log.i(TAG, "Night mode")
            } else {
                // Set theme to light
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                Log.i(TAG, "Light mode")
            }
        }
    }

    private fun updateTipDescription(tipPercent: Int) {
        val tipDesc = when (tipPercent) {
            in 0..9 -> "Poor"
            in 10..14 -> "Acceptable"
            in 15..19 -> "Good"
            in 20..24 -> "Great"
            else -> "Amazing"
        }
        tipDescription.text = tipDesc
        // Update the colour based on the tipPercent
        val color = ArgbEvaluator().evaluate(
            tipPercent.toFloat() / tipPercentBar.max,
            ContextCompat.getColor(this, R.color.worst_tip),
            ContextCompat.getColor(this, R.color.best_tip)
        ) as Int
        tipDescription.setTextColor(color)
    }

    private fun computeTipAndTotal() {
        if (baseAmount.text.isEmpty() || peopleAmount.text.isEmpty()) {
            tipAmount.text = ""
            totalAmount.text = ""
            return
        }



        // 1. Get the value of the base and tip percent
        val baseAmount = baseAmount.text.toString().toDouble()
        val tipPercent = tipPercentBar.progress
        val peopleNumber = peopleAmount.text.toString().toInt()

        // Validate the number of people
        if (peopleNumber <= 0) {
            Toast.makeText(this, "Number of people must be greater than zero", Toast.LENGTH_SHORT).show()
            return
        }

        // 2. Compute the tip and total
        val tipTotal = baseAmount * tipPercent / 100
        val totalAmounter = baseAmount + tipTotal
        val totalSplit = totalAmounter / peopleNumber
        // 3. Update UI
        val euroSign = "\u20AC" // Euro sign
        tipAmount.text = "$euroSign%.2f".format(tipTotal)
        totalAmount.text = "$euroSign%.2f".format(totalAmounter)
        splitAmount.text = "$euroSign%.2f".format(totalSplit)
    }

    private fun applyTheme() {
        val themeMode = if (isDarkTheme) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
        AppCompatDelegate.setDefaultNightMode(themeMode)
        setTheme(R.style.Base_Theme_TipCalculator_Dark)
    }
}