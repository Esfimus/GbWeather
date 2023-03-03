package com.esfimus.gbweather.domain.broadcast

import android.app.Service
import android.content.Intent
import android.os.Build.VERSION
import android.os.IBinder
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.esfimus.gbweather.data.WEATHER_BROADCAST_EXTRA
import com.esfimus.gbweather.data.WEATHER_BROADCAST_INTENT
import com.esfimus.gbweather.data.WEATHER_LOCATION_EXTRA
import com.esfimus.gbweather.domain.Location
import com.esfimus.gbweather.domain.WeatherView
import com.esfimus.gbweather.domain.api.LoadWeather
import com.esfimus.gbweather.domain.api.Loadable

class BroadcastService : Service() {

    @Suppress("DEPRECATION")
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val location: Location? = if (VERSION.SDK_INT >= 33) {
            intent?.getParcelableExtra(WEATHER_LOCATION_EXTRA, Location::class.java)
        } else {
            intent?.getParcelableExtra(WEATHER_LOCATION_EXTRA)
        }
        updateWeather(location ?: Location("North Pole", 90.0, 0.0))
        return super.onStartCommand(intent, flags, startId)
    }

    private fun updateWeather(location: Location) {
        val loadableWeather: Loadable = object : Loadable {
            override fun loaded(weather: WeatherView) {
                sendBroadcast("""
                    ${weather.location.name}
                    ${weather.currentTimeView}
                """.trimIndent())
            }
            override fun failed(responseCode: Int) {
                when (responseCode) {
                    in 300 until 400 -> sendBroadcast("Redirection")
                    in 400 until 500 -> sendBroadcast("Client Error")
                    in 500 until 600 -> sendBroadcast("Server Error")
                }
            }
        }
        val loader = LoadWeather(location, loadableWeather)
        loader.loadWeather()
    }

    private fun sendBroadcast(text: String) {
        val broadcastIntent = Intent(WEATHER_BROADCAST_INTENT)
        broadcastIntent.putExtra(WEATHER_BROADCAST_EXTRA, text)
        LocalBroadcastManager.getInstance(this).sendBroadcast(broadcastIntent)
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }
}