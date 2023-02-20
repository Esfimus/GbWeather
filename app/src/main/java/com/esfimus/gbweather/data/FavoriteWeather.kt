package com.esfimus.gbweather.data

class FavoriteWeather {

    val favoriteWeatherList = mutableListOf<Weather>()

    fun addWeather(weather: Weather) {
        favoriteWeatherList.add(weather)
    }

    fun deleteWeather(index: Int) {
        if (index in 0 until favoriteWeatherList.size) favoriteWeatherList.removeAt(index)
    }

}