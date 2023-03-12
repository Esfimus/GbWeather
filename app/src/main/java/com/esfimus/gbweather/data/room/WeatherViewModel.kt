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

    fun deleteWeather(weather: WeatherEntity) = viewModelScope.launch {
        repository.deleteWeather(weather)
    }
}