package com.esfimus.gbweather.data.room

import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData

class WeatherRepository(private val weatherDao: WeatherDao) {

    val allWeatherItems: LiveData<List<WeatherEntity>> = weatherDao.getAllWeatherItems()

    @WorkerThread
    suspend fun insertWeather(weather: WeatherEntity) {
        weatherDao.insert(weather)
    }

    @WorkerThread
    suspend fun updateWeather(weather: WeatherEntity) {
        weatherDao.update(weather)
    }

    @WorkerThread
    suspend fun deleteWeather(weather: WeatherEntity) {
        weatherDao.delete(weather)
    }
}