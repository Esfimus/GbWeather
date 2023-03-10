package com.esfimus.gbweather.domain

import com.esfimus.gbweather.domain.api.WeatherLoaded
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

data class WeatherPresenter(
    val location: Location,
    val weatherLoaded: WeatherLoaded? = null
    ) {
    val currentTimeView = currentDateAndTime()
    var temperatureView = ""
    var feelsLikeView = ""
    var humidityView = ""
    var windView = ""
    var pressureView = ""

    init {
        if (weatherLoaded != null) {
            temperatureView = "${weatherLoaded.fact.temp}°"
            feelsLikeView = "Feels like ${weatherLoaded.fact.feelsLike}°"
            humidityView = "Humidity ${weatherLoaded.fact.humidity}%"
            windView = "Wind ${weatherLoaded.fact.windSpeed} m/s ${weatherLoaded.fact.windDir.uppercase()}"
            pressureView = "Pressure ${weatherLoaded.fact.pressureMm} mmHg"
        }
    }
}

private fun currentDateAndTime(): String {
    return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss"))
}