package com.esfimus.gbweather.data.api

import android.os.Handler
import android.os.Looper
import android.util.Log
import com.esfimus.gbweather.BuildConfig
import com.esfimus.gbweather.domain.CustomLocation
import com.esfimus.gbweather.domain.WeatherFormatted
import com.google.gson.Gson
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.URL
import javax.net.ssl.HttpsURLConnection

class LoadWeather(private val customLocation: CustomLocation, private val loadable: Loadable) {

    fun loadWeather() {
        try {
            val uri = URL("https://api.weather.yandex.ru/v2/informers?lat=${customLocation.lat}&lon=${customLocation.lon}")
            val handler = Handler(Looper.getMainLooper())
            Thread {
                lateinit var urlConnection: HttpsURLConnection
                try {
                    urlConnection = uri.openConnection() as HttpsURLConnection
                    with(urlConnection) {
                        requestMethod = "GET"
                        addRequestProperty("X-Yandex-API-Key", BuildConfig.WEATHER_API_KEY)
                        readTimeout = 5000
                    }
                    val buffer = BufferedReader(InputStreamReader(urlConnection.inputStream))
                    val weatherLoaded: WeatherLoaded = Gson().fromJson(buffer, WeatherLoaded::class.java)
                    val weatherFormatted = WeatherFormatted(customLocation, weatherLoaded)
                    handler.post { loadable.loaded(weatherFormatted) }
                } catch (e: Exception) {
                    Log.e("@@@", "Connection failed ${urlConnection.responseCode}", e)
                    handler.post { loadable.failed(urlConnection.responseCode) }
                } finally {
                    urlConnection.disconnect()
                }
            }.start()
        } catch(e: Exception) {
            Log.e("@@@", "Something's wrong", e)
        }
    }

}