package com.esfimus.gbweather.data

data class Weather(val location: Location) {
    var temperature: String? = null
    var feelsLike: String? = null
    var humidity: String? = null
    var wind: String? = null
    var pressure: String? = null
    var currentTime: String? = null
}