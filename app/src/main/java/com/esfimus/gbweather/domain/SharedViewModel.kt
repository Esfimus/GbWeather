package com.esfimus.gbweather.domain

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.esfimus.gbweather.data.FavoriteWeather
import com.esfimus.gbweather.data.Repository
import com.esfimus.gbweather.data.Weather
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type

private const val PREFERENCE = "preference"
private const val PREF_KEY = "preferences"

class SharedViewModel : ViewModel() {

    // available for view
    val selectedWeather: MutableLiveData<Weather> = MutableLiveData()
    val weatherList: MutableLiveData<List<Weather>> = MutableLiveData()

    // connection to data
    private val repositoryData = Repository()
    private var locationsList = FavoriteWeather()

    fun selectWeatherLocation(index: Int) {
        selectedWeather.value = locationsList.favoriteWeatherList[index]
    }

    private var preferences: SharedPreferences? = null
    fun save() {
        val locationListJson = GsonBuilder().create().toJson(locationsList)
        preferences?.edit()?.putString(PREF_KEY, locationListJson)?.apply()
        Log.d("shit", "object saved")
    }
    fun load(context: Context) {
        preferences = context.getSharedPreferences(PREFERENCE, Context.MODE_PRIVATE)
        val retrievedList = preferences?.getString(PREF_KEY, null)
        if (retrievedList != null) {
            val type: Type = object : TypeToken<FavoriteWeather>() {}.type
            locationsList = GsonBuilder().create().fromJson(retrievedList, type)
            weatherList.value = locationsList.favoriteWeatherList
            Log.d("shit", "object is not null")
        } else {
            Log.d("shit", "null")
        }
    }

    fun addWeatherLocation(requestLocation: String): Int {
        return if (checkLocation(requestLocation)) {
            var newWeather = Weather(Weather.Location(requestLocation.uppercase()))
            newWeather = repositoryData.updateWeather(newWeather)
            locationsList.addWeather(newWeather)
            weatherList.value = locationsList.favoriteWeatherList
            selectedWeather.value = newWeather
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