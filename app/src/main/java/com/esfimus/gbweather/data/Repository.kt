package com.esfimus.gbweather.data

import java.time.LocalDateTime
import kotlin.random.Random

class Repository {

    fun updateWeather(weather: Weather): Weather {
        val temperatureInt = Random.nextInt(-30, 31)
        return weather.apply {
            currentTime = currentDateAndTime()
            temperature = "$temperatureInt°"
            feelsLike = "Feels like ${Random.nextInt(temperatureInt - 5,temperatureInt + 5)}°"
            humidity = "Humidity ${Random.nextInt(20, 100)}%"
            wind = "Wind ${Random.nextInt(0, 21)} m/s  ${listOf("N", "NE", "E", "SE", "S", "SW", "W", "NW")[Random.nextInt(0, 8)]}"
            pressure = "Pressure ${Random.nextInt(735, 745)} mmHg"
        }
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
        "moscow",
        "beijing",
        "tokio",
        "nairobi",
        "new delhi",
        "prague",
        "tehran",
        "athens",
        "washington",
        "mexico",
        "stockholm",
        "santiago",
    )

    fun isAvailable(locationName: String): Boolean {
        return availableLocations.contains(locationName.lowercase())
    }
}