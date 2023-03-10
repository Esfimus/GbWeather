package com.esfimus.gbweather.domain

class FavoriteWeather {

    val favoriteWeatherList = mutableListOf<WeatherPresenter>()

    fun addWeather(weatherPresenter: WeatherPresenter) {
        favoriteWeatherList.add(weatherPresenter)
    }

    fun deleteWeather(index: Int) {
        if (index in 0 until favoriteWeatherList.size) favoriteWeatherList.removeAt(index)
    }

}