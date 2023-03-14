package com.esfimus.gbweather.domain

import com.esfimus.gbweather.domain.api.WeatherLoaded
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

data class WeatherPresenter(
    val location: Location,
    val weatherLoaded: WeatherLoaded? = null
    ) {
    val currentTimeFormatted = currentDateAndTime()
    var temperatureFormatted = ""
    var feelsLikeFormatted = ""
    var humidityFormatted = ""
    var windFormatted = ""
    var pressureFormatted = ""
    var iconLink = ""

    init {
        if (weatherLoaded != null) {
            temperatureFormatted = "${weatherLoaded.fact.temp}°"
            feelsLikeFormatted = "Feels like ${weatherLoaded.fact.feelsLike}°"
            humidityFormatted = "Humidity ${weatherLoaded.fact.humidity}%"
            windFormatted = "Wind ${weatherLoaded.fact.windSpeed} m/s ${weatherLoaded.fact.windDir.uppercase()}"
            pressureFormatted = "Pressure ${weatherLoaded.fact.pressureMm} mmHg"
            iconLink = "https://yastatic.net/weather/i/icons/funky/dark/${weatherLoaded.fact.icon}.svg"
        }
    }
}

private fun currentDateAndTime(): String {
    return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss"))
}