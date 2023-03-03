package com.esfimus.gbweather.domain.broadcast

import android.app.Service
import android.content.Intent
import android.os.Build.VERSION
import android.os.IBinder
import android.widget.Toast
import com.esfimus.gbweather.data.WEATHER_LOCATION_EXTRA
import com.esfimus.gbweather.domain.Location

class BroadcastService : Service() {

    @Suppress("DEPRECATION")
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val location: Location? = if (VERSION.SDK_INT >= 33) {
            intent?.getParcelableExtra(WEATHER_LOCATION_EXTRA, Location::class.java)
        } else {
            intent?.getParcelableExtra(WEATHER_LOCATION_EXTRA)
        }
        Toast.makeText(
            this,
            "Location: ${location?.name} ${location?.lat} ${location?.lon}",
            Toast.LENGTH_SHORT).show()
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }
}