package com.esfimus.gbweather.domain

import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.esfimus.gbweather.data.FavoriteWeather
import com.esfimus.gbweather.data.Repository
import com.esfimus.gbweather.data.Weather
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type

private const val PREFERENCE_LIST = "preference list"
private const val PREFERENCE_INDEX = "preference index"
private const val LOCATION_LIST = "location list"
private const val SELECTED_WEATHER_INDEX = "selected weather index"

class SharedViewModel : ViewModel() {

    // available for view
    val selectedWeather: MutableLiveData<Weather> = MutableLiveData()
    val weatherList: MutableLiveData<List<Weather>> = MutableLiveData()

    // connection to data
    private val repositoryData = Repository()
    private var locationsList = FavoriteWeather()
    private var selectedWeatherIndex: Int? = null

    // saving parameters
    private var saveList: SharedPreferences? = null
    private var saveIndex: SharedPreferences? = null

    /**
     * Saves whole list of favorite locations and index of selected location
     */
    fun save() {
        val locationListJson = GsonBuilder().create().toJson(locationsList)
        saveList?.edit()?.putString(LOCATION_LIST, locationListJson)?.apply()
        saveIndex?.edit()?.putInt(SELECTED_WEATHER_INDEX, selectedWeatherIndex!!)?.apply()
    }

    /**
     * Loads list of favorite locations and index of selected location
     */
    fun load(context: Context) {
        saveList = context.getSharedPreferences(PREFERENCE_LIST, Context.MODE_PRIVATE)
        saveIndex = context.getSharedPreferences(PREFERENCE_INDEX, Context.MODE_PRIVATE)
        val retrievedList = saveList?.getString(LOCATION_LIST, null)
        val retrievedWeatherIndex = saveIndex?.getInt(SELECTED_WEATHER_INDEX, -100)
        if (retrievedList != null) {
            val type: Type = object : TypeToken<FavoriteWeather>() {}.type
            locationsList = GsonBuilder().create().fromJson(retrievedList, type)
            weatherList.value = locationsList.favoriteWeatherList
        }
        if (retrievedWeatherIndex != null) {
            selectedWeatherIndex = retrievedWeatherIndex
            if (selectedWeatherIndex in 0 until locationsList.favoriteWeatherList.size) {
                selectedWeather.value = locationsList.favoriteWeatherList[selectedWeatherIndex!!]
            }
        }
    }

    fun switchWeatherLocation(position: Int) {
        selectedWeatherIndex = position
        selectedWeather.value = locationsList.favoriteWeatherList[selectedWeatherIndex!!]
        save()
    }

    /**
     * Checks if location is valid and it is already in favorite list,
     * adds location and updates LiveData list and selected location index
     */
    fun addWeatherLocation(requestLocation: String): Int {
        // location is valid and not in favorites list yet, permission to add location
        return if (checkLocation(requestLocation)) {
            val newWeather = Weather(Weather.Location(requestLocation.uppercase()))
            repositoryData.updateWeather(newWeather)
            locationsList.addWeather(newWeather)
            weatherList.value = locationsList.favoriteWeatherList
            selectedWeatherIndex = locationsList.favoriteWeatherList.size - 1
            selectedWeather.value = locationsList.favoriteWeatherList[selectedWeatherIndex!!]
            1
        // location is valid but already in favorites list, cannot be added
        } else if (locationIsFavorite(requestLocation)) {
            0
        // location is not valid, cannot be added
        } else {
            -1
        }
    }

    fun deleteWeatherLocation(position: Int) {
        if (position in 0 until locationsList.favoriteWeatherList.size) {
            locationsList.deleteWeather(position)
            weatherList.value = locationsList.favoriteWeatherList
            selectedWeatherIndex = locationsList.favoriteWeatherList.size - 1
            if (selectedWeatherIndex!! >= 0) {
                selectedWeather.value = locationsList.favoriteWeatherList[selectedWeatherIndex!!]
            } else {
                selectedWeather.value = Weather(Weather.Location(""))
            }
            save()
        }
    }

    /**
     * Checks if requested location name is valid and not in favorite list
     */
    private fun checkLocation(requestLocation: String): Boolean {
        return (repositoryData.isAvailable(requestLocation) && !locationIsFavorite(requestLocation))
    }

    /**
     * Checks if favorite list contains requested location
     */
    private fun locationIsFavorite(requestLocation: String): Boolean {
        for (favorites in locationsList.favoriteWeatherList) {
            if (favorites.location.name.lowercase() == requestLocation) {
                return true
            }
        }
        return false
    }

    /**
     * Updates weather parameters
     */
    fun updateWeatherList() {
        for (weather in locationsList.favoriteWeatherList) {
            repositoryData.updateWeather(weather)
        }
        weatherList.value = locationsList.favoriteWeatherList
        save()
    }

    /**
     * Checks if favorite list is not empty
     */
    fun favoritesAdded(): Boolean {
        return locationsList.favoriteWeatherList.isNotEmpty()
    }
}