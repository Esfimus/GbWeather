package com.esfimus.gbweather.domain

class FavoriteWeather {

    val favoriteWeatherList = mutableListOf<WeatherView>()

    fun addWeather(weatherView: WeatherView) {
        favoriteWeatherList.add(weatherView)
    }

    fun deleteWeather(index: Int) {
        if (index in 0 until favoriteWeatherList.size) favoriteWeatherList.removeAt(index)
    }

}