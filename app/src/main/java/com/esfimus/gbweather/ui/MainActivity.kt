package com.esfimus.gbweather.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.esfimus.gbweather.R
import com.esfimus.gbweather.domain.Command

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initAction()
    }

    private fun initAction() {
        val locationView = findViewById<TextView>(R.id.text_field_location)
        val messageView = findViewById<TextView>(R.id.text_field_message)
        val sendButton = findViewById<Button>(R.id.button_send)
        sendButton.setOnClickListener {
            val location = locationView.text.toString()
            val message = messageView.text.toString()
            if (!"""\s*""".toRegex().matches(location) && !"""\s*""".toRegex().matches(message)) {
                val command = Command(location, message)
                toast(command.report)
            }
        }

        var numberToCountFrom = 17
        while (numberToCountFrom > 0) {
            Log.d("counter", "count while $numberToCountFrom")
            numberToCountFrom--
        }

        for (i in 587..595) {
            Log.d("counter", "count for $i")
        }
    }

    private fun toast(text: String) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show()
    }
}