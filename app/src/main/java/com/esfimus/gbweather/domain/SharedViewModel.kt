package com.esfimus.gbweather.domain

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.esfimus.gbweather.data.FavoriteWeather
import com.esfimus.gbweather.data.Repository
import com.esfimus.gbweather.data.Weather

class SharedViewModel : ViewModel() {

    // available for view
    val selectedWeather: MutableLiveData<Weather> = MutableLiveData()
    val favoritesListIsEmpty: MutableLiveData<Boolean> = MutableLiveData()

    // connection to data
    private val repositoryData = Repository()
    private val locationsList = FavoriteWeather()

    fun addWeatherLocation(requestLocation: String): Boolean {
        return if (repositoryData.isAvailable(requestLocation)) {
            val newWeather = Weather(Weather.Location(requestLocation.uppercase()))
            locationsList.addWeather(repositoryData.updateWeather(newWeather))
            true
        } else {
            false
        }
    }

    fun updateWeatherList() {
        for (weather in locationsList.favoriteWeatherList) {
            repositoryData.updateWeather(weather)
        }
    }

    fun favoritesAdded(): Boolean {
        return locationsList.favoriteWeatherList.isNotEmpty()
    }
}