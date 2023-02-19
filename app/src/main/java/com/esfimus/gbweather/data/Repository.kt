package com.esfimus.gbweather.data

import java.time.LocalDateTime
import kotlin.random.Random

class Repository(private val location: Location) {
    fun updateWeather(): Weather {
        val weather = Weather(location)
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

    val availableLocations = listOf(
        Location("Moscow"),
        Location("Beijing"),
        Location("Tokio"),
        Location("Nairobi"),
        Location("New Delhi"),
        Location("Prague"),
        Location("Tehran"),
        Location("Athens"),
        Location("Washington"),
        Location("Mexico"),
        Location("Stockholm"),
        Location("Santiago"),
    )
}