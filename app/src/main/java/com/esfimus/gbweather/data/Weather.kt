package com.esfimus.gbweather.data

data class Weather(val location: Location) {
    var temperature: Int = 0
    var humidity: Int = 50
}