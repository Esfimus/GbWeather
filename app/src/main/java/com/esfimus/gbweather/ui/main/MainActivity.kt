package com.esfimus.gbweather.ui.main

import android.content.Intent
import android.content.IntentFilter
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.esfimus.gbweather.R
import com.esfimus.gbweather.databinding.ActivityMainBinding
import com.esfimus.gbweather.domain.MyReceiver

class MainActivity : AppCompatActivity() {

    private lateinit var ui: ActivityMainBinding
    private val receiver = MyReceiver()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        ui = ActivityMainBinding.inflate(layoutInflater)
        setContentView(ui.root)

        runFragment()
        registerReceiver(receiver, IntentFilter(Intent.ACTION_SCREEN_OFF))
    }

    private fun runFragment() {
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.main_container, WeatherDetailsFragment.newInstance())
            .commit()
    }

    override fun onDestroy() {
        unregisterReceiver(receiver)
        super.onDestroy()
    }
}