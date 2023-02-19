package com.esfimus.gbweather.domain

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.esfimus.gbweather.data.FavoriteWeather
import com.esfimus.gbweather.data.Repository
import com.esfimus.gbweather.data.Weather

class SharedViewModel : ViewModel() {

    // available for view
    val selectedWeather: MutableLiveData<Weather> = MutableLiveData()
    val selectedWeatherList: MutableLiveData<List<List<String?>>> = MutableLiveData()

    // connection to data
    private val repositoryData = Repository()
    private val locationsList = FavoriteWeather()
    private val locationNameList = mutableListOf<List<String?>>()

    fun addWeatherLocation(requestLocation: String): Boolean {
        return if (repositoryData.isAvailable(requestLocation)) {
            var newWeather = Weather(Weather.Location(requestLocation.uppercase()))
            newWeather = repositoryData.updateWeather(newWeather)
            locationsList.addWeather(newWeather)
            locationNameList.add(listOf(newWeather.location.name, newWeather.temperature))
            selectedWeatherList.value = locationNameList
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