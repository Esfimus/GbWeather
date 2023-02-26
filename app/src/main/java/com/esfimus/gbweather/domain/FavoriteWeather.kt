package com.esfimus.gbweather.domain

class FavoriteWeather {

    val favoriteWeatherList = mutableListOf<Weather>()

    fun addWeather(weather: Weather) {
        favoriteWeatherList.add(weather)
    }

    fun deleteWeather(index: Int) {
        if (index in 0 until favoriteWeatherList.size) favoriteWeatherList.removeAt(index)
    }

}