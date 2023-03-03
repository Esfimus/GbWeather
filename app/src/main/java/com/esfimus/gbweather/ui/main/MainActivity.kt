package com.esfimus.gbweather.ui.main

import android.content.IntentFilter
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.esfimus.gbweather.R
import com.esfimus.gbweather.data.WEATHER_BROADCAST_INTENT
import com.esfimus.gbweather.databinding.ActivityMainBinding
import com.esfimus.gbweather.domain.broadcast.MyReceiver

class MainActivity : AppCompatActivity() {

    private lateinit var ui: ActivityMainBinding
    private val receiver = MyReceiver()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        ui = ActivityMainBinding.inflate(layoutInflater)
        setContentView(ui.root)

        runFragment()
        LocalBroadcastManager
            .getInstance(this)
            .registerReceiver(receiver, IntentFilter(WEATHER_BROADCAST_INTENT))
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