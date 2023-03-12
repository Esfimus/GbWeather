package com.esfimus.gbweather.data.room

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class WeatherViewModel(application: Application) : AndroidViewModel(application) {
    val weatherList: LiveData<List<WeatherEntity>>
    private val repository: WeatherRepository

    init {
        val weatherDao = WeatherDatabase.getDatabase(application).weatherDao()
        repository = WeatherRepository(weatherDao)
        weatherList = repository.allWeatherItems
    }

    fun addWeather(weather: WeatherEntity) = viewModelScope.launch {
        repository.insertWeather(weather)
    }

    fun updateWeather(weather: WeatherEntity) = viewModelScope.launch {
        repository.updateWeather(weather)
    }

    private fun deleteWeather(weather: WeatherEntity) = viewModelScope.launch {
        repository.deleteWeather(weather)
    }

    fun deleteByPosition(position: Int) {
        deleteWeather(weatherList.value?.get(position) ?: WeatherEntity())
    }

    fun itemsCount(): Int {
        var check = -1
        viewModelScope.launch {
            val count =  repository.itemsCount()
            check = count
        }
        return check
    }

}