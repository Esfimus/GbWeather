package com.esfimus.gbweather.data

import java.time.LocalDateTime
import kotlin.random.Random

class Repository {

    fun updateWeather(weather: Weather): Weather {
        weather.currentTime = currentDateAndTime()
        weather.temperature = "${Random.nextInt(-30, 31)}°"
        weather.feelsLike = "${Random.nextInt(
            weather.temperature!!.substring(0, weather.temperature!!.length - 2).toInt() - 5,
            weather.temperature!!.substring(0, weather.temperature!!.length - 2).toInt() + 5)}°"
        weather.humidity = Random.nextInt(20, 100).toString()
        weather.wind = "${listOf("N", "NE", "E", "SE", "S", "SW", "W", "NW")[Random.nextInt(0, 8)]} ${Random.nextInt(0, 21)} m/s"
        weather.pressure = "${Random.nextInt(735, 745)} mmHg"
        return weather
    }

    private fun currentDateAndTime(): String {
        val currentDate = LocalDateTime.now()
        val year = "%04d".format(currentDate.year)
        val month = "%02d".format(currentDate.monthValue)
        val day = "%02d".format(currentDate.dayOfMonth)
        val hour = "%02d".format(currentDate.hour)
        val minute = "%02d".format(currentDate.minute)
        val second = "%02d".format(currentDate.second)
        return "$year/$month/$day $hour:$minute:$second"
    }

    private val availableLocations = listOf(
        Weather.Location("moscow"),
        Weather.Location("beijing"),
        Weather.Location("tokio"),
        Weather.Location("nairobi"),
        Weather.Location("new delhi"),
        Weather.Location("prague"),
        Weather.Location("tehran"),
        Weather.Location("athens"),
        Weather.Location("washington"),
        Weather.Location("mexico"),
        Weather.Location("stockholm"),
        Weather.Location("santiago"),
    )

    fun isAvailable(locationName: String): Boolean {
        return availableLocations.contains(Weather.Location(locationName.lowercase()))
    }
}