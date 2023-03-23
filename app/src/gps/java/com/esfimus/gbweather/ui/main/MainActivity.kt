package com.esfimus.gbweather.ui.main

import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.esfimus.gbweather.R
import com.esfimus.gbweather.data.broadcast.MyReceiver
import com.esfimus.gbweather.data.broadcast.WEATHER_BROADCAST_INTENT
import com.esfimus.gbweather.data.firebase.FIREBASE
import com.esfimus.gbweather.databinding.ActivityMainBinding
import com.esfimus.gbweather.ui.gps_map_location.GpsWeatherFragment
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging

class MainActivity : AppCompatActivity() {

    private lateinit var ui: ActivityMainBinding
    private val receiver = MyReceiver()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        ui = ActivityMainBinding.inflate(layoutInflater)
        setContentView(ui.root)

        runFragment()
        getFirebaseToken()
        LocalBroadcastManager
            .getInstance(this)
            .registerReceiver(receiver, IntentFilter(WEATHER_BROADCAST_INTENT))
    }

    private fun runFragment() {
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.main_container, GpsWeatherFragment.newInstance())
            .commit()
    }

    override fun onDestroy() {
        unregisterReceiver(receiver)
        super.onDestroy()
    }

    private fun getFirebaseToken() {
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w(FIREBASE, "Fetching FCM registration token failed", task.exception)
                return@OnCompleteListener
            }
            val token = task.result
            Log.d(FIREBASE, "Firebase token: $token")
        })
    }
}