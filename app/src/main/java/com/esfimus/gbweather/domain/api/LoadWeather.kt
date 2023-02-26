package com.esfimus.gbweather.domain.api

import android.os.Handler
import android.os.Looper
import android.util.Log
import com.esfimus.gbweather.BuildConfig
import com.esfimus.gbweather.domain.Location
import com.google.gson.Gson
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.URL
import javax.net.ssl.HttpsURLConnection

class LoadWeather(private val location: Location, private val loadable: Loadable) {

    fun loadWeather() {
        try {
            val uri = URL("https://api.weather.yandex.ru/v2/informers?lat=${location.lat}&lon=${location.lon}")
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
                    val weather: WeatherGeneral = Gson().fromJson(buffer, WeatherGeneral::class.java)
                    handler.post { loadable.loaded(weather) }
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