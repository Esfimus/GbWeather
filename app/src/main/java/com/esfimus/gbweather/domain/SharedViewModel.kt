package com.esfimus.gbweather.domain

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.esfimus.gbweather.data.Location
import com.esfimus.gbweather.data.Repository
import com.esfimus.gbweather.data.Weather

class SharedViewModel : ViewModel() {
    val weather: MutableLiveData<Weather> = MutableLiveData()

    fun sendRequest(request: String) {
        val repositoryData = Repository(Location(request.lowercase()))
        val newWeather = repositoryData.updateWeather()
        weather.value = newWeather
    }
}