package com.esfimus.gbweather.data

class Repository(private val location: Location) {
    fun updateWeather(): Weather {
        val weather = Weather(location)
        weather.temperature = location.name.length
        weather.humidity = if (location.name.length * 4 < 100) location.name.length * 4 else 100
        return weather
    }
}