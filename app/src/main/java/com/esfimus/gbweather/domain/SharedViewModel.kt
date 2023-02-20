package com.esfimus.gbweather.domain

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.esfimus.gbweather.data.FavoriteWeather
import com.esfimus.gbweather.data.Repository
import com.esfimus.gbweather.data.Weather

class SharedViewModel : ViewModel() {

    // available for view
    val selectedWeather: MutableLiveData<Weather> = MutableLiveData()
    val selectedWeatherList: MutableLiveData<List<Weather>> = MutableLiveData()

    // connection to data
    private val repositoryData = Repository()
    private val locationsList = FavoriteWeather()

    fun addWeatherLocation(requestLocation: String): Int {
        return if (checkLocation(requestLocation)) {
            var newWeather = Weather(Weather.Location(requestLocation.uppercase()))
            newWeather = repositoryData.updateWeather(newWeather)
            locationsList.addWeather(newWeather)
            selectedWeatherList.value = locationsList.favoriteWeatherList
            1
        } else if (locationIsFavorite(requestLocation)) {
            0
        } else {
            -1
        }
    }

    private fun checkLocation(requestLocation: String): Boolean {
        return (repositoryData.isAvailable(requestLocation) && !locationIsFavorite(requestLocation))
    }

    private fun locationIsFavorite(requestLocation: String): Boolean {
        for (favorites in locationsList.favoriteWeatherList) {
            if (favorites.location.name.lowercase() == requestLocation) {
                return true
            }
        }
        return false
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