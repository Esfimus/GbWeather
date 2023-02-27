package com.esfimus.gbweather.domain

import com.esfimus.gbweather.domain.api.WeatherLoaded
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

data class WeatherView(
    val location: Location,
    val weatherLoaded: WeatherLoaded
    ) {
    val currentTimeView = currentDateAndTime()
    val temperatureView = "${weatherLoaded.fact.temp}°"
    val feelsLikeView = "Feels like ${weatherLoaded.fact.feelsLike}°"
    val humidityView = "Humidity ${weatherLoaded.fact.humidity}%"
    val windView = "Wind ${weatherLoaded.fact.windSpeed} m/s ${weatherLoaded.fact.windDir.uppercase()}"
    val pressureView = "Pressure ${weatherLoaded.fact.pressureMm} mmHg"
}

private fun currentDateAndTime(): String {
    return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss"))
}