package com.esfimus.gbweather.ui

import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.esfimus.gbweather.domain.FavoriteWeather
import com.esfimus.gbweather.domain.Location
import com.esfimus.gbweather.domain.Repository
import com.esfimus.gbweather.domain.WeatherView
import com.esfimus.gbweather.domain.api.Loadable
import com.esfimus.gbweather.domain.api.LoadWeather
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type

private const val PREFERENCE_LIST = "preference list"
private const val PREFERENCE_INDEX = "preference index"
private const val LOCATION_LIST = "location list"
private const val SELECTED_WEATHER_INDEX = "selected weather index"

class SharedViewModel : ViewModel() {

    // available for view
    val selectedWeatherLive: MutableLiveData<WeatherView> = MutableLiveData()
    val weatherViewListLive: MutableLiveData<List<WeatherView>> = MutableLiveData()
    val responseFailureLive: MutableLiveData<String> = MutableLiveData()

    // connection to data
    private val repositoryData = Repository()
    private var locationsList = FavoriteWeather()
    private var selectedWeatherIndex: Int? = null

    // saving parameters
    private var saveList: SharedPreferences? = null
    private var saveIndex: SharedPreferences? = null

    private var loadedWeather: WeatherView? = null
    private fun loadWeather(location: Location) {
        val loadableWeather: Loadable = object : Loadable {
            override fun loaded(weather: WeatherView) {
                getWeather(weather)
            }
            override fun failed(responseCode: Int) {
                when (responseCode) {
                    in 300 until 400 -> responseFailureLive.value = "Redirection"
                    in 400 until 500 -> responseFailureLive.value = "Client Error"
                    in 500 until 600 -> responseFailureLive.value = "Server Error"
                }
            }
        }
        val loader = LoadWeather(location, loadableWeather)
        loader.loadWeather()
    }

    fun getWeather(weather: WeatherView) {
        loadedWeather = weather
    }

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
            weatherViewListLive.value = locationsList.favoriteWeatherList
        }
        if (retrievedWeatherIndex != null) {
            selectedWeatherIndex = retrievedWeatherIndex
            if (selectedWeatherIndex in 0 until locationsList.favoriteWeatherList.size) {
                selectedWeatherLive.value = locationsList.favoriteWeatherList[selectedWeatherIndex!!]
            }
        }
    }

    fun switchWeatherLocation(position: Int) {
        selectedWeatherIndex = position
        selectedWeatherLive.value = locationsList.favoriteWeatherList[selectedWeatherIndex ?: 0]
        save()
    }

    /**
     * Checks if location is valid and it is already in favorite list,
     * adds location and updates LiveData list and selected location index
     */
    fun addWeatherLocation(requestLocation: String): String {
        // location is valid and not in favorites list yet, permission to add location
        if (checkLocation(requestLocation)) {
            loadWeather(repositoryData.getLocation(requestLocation)!!)
            return if (loadedWeather != null) {
                locationsList.addWeather(loadedWeather!!)
                loadedWeather = null
                weatherViewListLive.value = locationsList.favoriteWeatherList
                selectedWeatherIndex = locationsList.favoriteWeatherList.size - 1
                selectedWeatherLive.value = locationsList.favoriteWeatherList[selectedWeatherIndex ?: 0]
                "ok"
            } else {
                "null"
            }
        // location is valid but already in favorites list, cannot be added
        } else if (locationIsFavorite(requestLocation)) {
            return "in list"
        // location is not valid, cannot be added
        } else {
            return "not found"
        }
    }

//    fun deleteWeatherLocation(position: Int) {
//        if (position in 0 until locationsList.favoriteWeatherListView.size) {
//            locationsList.deleteWeather(position)
//            weatherViewList.value = locationsList.favoriteWeatherListView
//            selectedWeatherIndex = locationsList.favoriteWeatherListView.size - 1
//            if (selectedWeatherIndex!! >= 0) {
//                selectedWeatherView.value = locationsList.favoriteWeatherListView[selectedWeatherIndex!!]
//            } else {
//                selectedWeatherView.value = WeatherView(emptyLocation)
//            }
//            save()
//        }
//    }

    /**
     * Checks if requested location name is valid and not in favorite list
     */
    private fun checkLocation(requestLocation: String): Boolean {
        return (repositoryData.getLocation(requestLocation) != null && !locationIsFavorite(requestLocation))
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
//    fun updateWeather() {
//        for (weather in locationsList.favoriteWeatherList) {
//            repositoryData.updateWeather(weather)
//        }
//        weatherViewListLive.value = locationsList.favoriteWeatherList
//        save()
//    }

    /**
     * Checks if favorite list is not empty
     */
    fun favoritesAdded(): Boolean {
        return locationsList.favoriteWeatherList.isNotEmpty()
    }
}