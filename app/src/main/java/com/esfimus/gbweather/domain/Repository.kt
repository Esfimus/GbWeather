package com.esfimus.gbweather.domain

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
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
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss"))
    }

    private val availableLocations = listOf(
        Location("MOSCOW", 55.735069, 37.406704),
        Location("BEIJING", 39.901850, 116.391441),
        Location("TOKIO", 35.681729, 139.753927),
        Location("NAIROBI", -1.272749, 36.827872),
        Location("NEW DELHI", 28.614243, 77.202788),
        Location("PRAGUE", 50.080345, 14.428974),
        Location("TEHRAN", 35.699706, 51.337425),
        Location("ATHENS", 37.975534, 23.734855),
        Location("WASHINGTON", 38.899513, -77.036536),
        Location("MEXICO", 19.432595, -99.133313),
        Location("STOCKHOLM", 59.335530, 18.057745),
        Location("SANTIAGO", -33.445255, -70.653422),
        )

    fun getLocation(locationName: String): Location? {
        for (location in availableLocations) {
            if (location.name.lowercase() == locationName.lowercase()) return location
        }
        return null
    }
}