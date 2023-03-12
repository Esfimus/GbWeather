package com.esfimus.gbweather.ui

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.esfimus.gbweather.data.AvailableLocations
import com.esfimus.gbweather.data.room.WeatherEntity
import com.esfimus.gbweather.domain.Location
import com.esfimus.gbweather.domain.WeatherPresenter
import com.esfimus.gbweather.domain.api.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.random.Random

private const val PREFERENCE_LIST = "preference list"
private const val PREFERENCE_INDEX = "preference index"
private const val LOCATION_LIST = "location list"
private const val SELECTED_WEATHER_INDEX = "selected weather index"

class SharedViewModel : ViewModel() {

    // available for view
//    val selectedWeatherLive: MutableLiveData<WeatherPresenter> = MutableLiveData()
//    val weatherPresenterListLive: MutableLiveData<List<WeatherPresenter>> = MutableLiveData()
    val responseFailureLive: MutableLiveData<String> = MutableLiveData()

    // connection to data
    private val availableLocationsData = AvailableLocations()
//    private var locationsList = FavoriteWeather()
    private var selectedWeatherIndex = 0

    // saving parameters
//    private var saveList: SharedPreferences? = null
    private var saveIndex: SharedPreferences? = null

    /**
     * Imitates weather update to test app functionality
     */
//    private fun updateWeatherImitation(location: Location, position: Int) {
//        val weather = WeatherPresenter(location, WeatherLoaded(
//            WeatherFact("condition", "daytime", Random.nextInt(-30,30),
//                Random.nextInt(10,100), "icon", Random.nextInt(0, 10),
//                true, Random.nextInt(735,745), Random.nextInt(100,200),
//                "season", Random.nextInt(-30,30), "wind", 0.0, 0.0),
//            WeatherForecast("date", 0, 0, "moon", listOf(), "sunrise", "sunset", 0),
//            WeatherInfo(location.lat, location.lon, "url"),
//            0,
//            "nowDt")
//        )
//        locationsList.favoriteWeatherList[position] = weather
//        selectedWeatherLive.value = locationsList.favoriteWeatherList[selectedWeatherIndex]
//        save()
//    }

    fun getWeatherImitation(requestLocation: String): WeatherEntity {
        val location = availableLocationsData.getLocation(requestLocation) ?: Location("", 0.0, 0.0)
        val weatherPresenter = WeatherPresenter(location, WeatherLoaded(
            WeatherFact("condition", "daytime", Random.nextInt(-30,30),
                Random.nextInt(10,100), "icon", Random.nextInt(0, 10),
                true, Random.nextInt(735,745), Random.nextInt(100,200),
                "season", Random.nextInt(-30,30), "wind", 0.0, 0.0),
            WeatherForecast("date", 0, 0, "moon", listOf(), "sunrise", "sunset", 0),
            WeatherInfo(location.lat, location.lon, "url"),
            0,
            "nowDt")
        )
        return WeatherEntity(
            weatherPresenter.location.name,
            weatherPresenter.location.lat.toString(),
            weatherPresenter.location.lon.toString(),
            weatherPresenter.currentTimeFormatted,
            weatherPresenter.temperatureFormatted,
            weatherPresenter.feelsLikeFormatted,
            weatherPresenter.humidityFormatted,
            weatherPresenter.windFormatted,
            weatherPresenter.pressureFormatted
        )
    }

    /**
     * Basic way of uploading data from weather API
     */
    private fun updateWeather(location: Location, position: Int) {
        val loadableWeather: Loadable = object : Loadable {
            override fun loaded(weather: WeatherPresenter) {
//                locationsList.favoriteWeatherList[position] = weather
//                selectedWeatherLive.value = locationsList.favoriteWeatherList[selectedWeatherIndex]
//                save()
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

    /**
     * Uploading data from weather API using Retrofit
     */
    private fun updateWeatherRetrofit(location: Location, position: Int) {
        val callBack = object : Callback<WeatherLoaded> {
            override fun onResponse(call: Call<WeatherLoaded>, response: Response<WeatherLoaded>) {
                val weatherLoaded: WeatherLoaded? = response.body()
                val weather = WeatherPresenter(location, weatherLoaded)
//                locationsList.favoriteWeatherList[position] = weather
//                selectedWeatherLive.value = locationsList.favoriteWeatherList[selectedWeatherIndex]
                when (response.code()) {
                    in 300 until 400 -> responseFailureLive.value = "Redirection"
                    in 400 until 500 -> responseFailureLive.value = "Client Error"
                    in 500 until 600 -> responseFailureLive.value = "Server Error"
                }
                save()
            }
            override fun onFailure(call: Call<WeatherLoaded>, t: Throwable) {
                Log.d("RetrofitFailure", "${t.message}")
            }
        }
        val loadRetrofitWeather = LoadRetrofitWeather()
        loadRetrofitWeather.getWeather(location.lat, location.lon, callBack)
    }

    /**
     * Saves whole list of favorite locations and index of selected location
     */
    private fun save() {
//        val locationListJson = GsonBuilder().create().toJson(locationsList)
//        saveList?.edit()?.putString(LOCATION_LIST, locationListJson)?.apply()
        saveIndex?.edit()?.putInt(SELECTED_WEATHER_INDEX, selectedWeatherIndex)?.apply()
    }

    /**
     * Loads list of favorite locations and index of selected location
     */
    fun load(context: Context) {
//        saveList = context.getSharedPreferences(PREFERENCE_LIST, Context.MODE_PRIVATE)
        saveIndex = context.getSharedPreferences(PREFERENCE_INDEX, Context.MODE_PRIVATE)
//        val retrievedList = saveList?.getString(LOCATION_LIST, null)
        val retrievedWeatherIndex = saveIndex?.getInt(SELECTED_WEATHER_INDEX, 0)
//        if (retrievedList != null) {
//            val type: Type = object : TypeToken<FavoriteWeather>() {}.type
//            locationsList = GsonBuilder().create().fromJson(retrievedList, type)
//            weatherPresenterListLive.value = locationsList.favoriteWeatherList
//        }
        if (retrievedWeatherIndex != null) {
            selectedWeatherIndex = retrievedWeatherIndex
//            if (selectedWeatherIndex in 0 until locationsList.favoriteWeatherList.size) {
//                selectedWeatherLive.value = locationsList.favoriteWeatherList[selectedWeatherIndex]
//            }
        }
    }

    /**
     * Changes selected weather view by index from list
     */
    fun switchWeatherLocation(position: Int) {
        selectedWeatherIndex = position
//        selectedWeatherLive.value = locationsList.favoriteWeatherList[selectedWeatherIndex]
//        save()
    }

    /**
     * Checks if location is valid and it is already in favorite list,
     * adds location and updates LiveData list and selected location index
     */
    fun addWeatherLocation(requestLocation: String): String {
        // location is valid and not in favorites list yet, permission to add location
        return if (checkLocation(requestLocation)) {
            val validLocation = availableLocationsData.getLocation(requestLocation)!!
//            locationsList.addWeather(WeatherPresenter(validLocation))
//            selectedWeatherIndex = locationsList.favoriteWeatherList.size - 1
//            updateWeatherImitation(validLocation, selectedWeatherIndex)
//            weatherPresenterListLive.value = locationsList.favoriteWeatherList
//            save()
            "ok"
            // location is valid but already in favorites list, cannot be added
        } else if (locationIsFavorite(requestLocation)) {
            "in list"
            // location is not valid, cannot be added
        } else {
            "not found"
        }
    }

    /**
     * Deletes specified weather location
     */
    fun deleteWeatherLocation(position: Int) {
//        if (position in 0 until locationsList.favoriteWeatherList.size) {
//            locationsList.deleteWeather(position)
//            weatherPresenterListLive.value = locationsList.favoriteWeatherList
//            selectedWeatherIndex = locationsList.favoriteWeatherList.size - 1
//            if (selectedWeatherIndex >= 0) {
//                selectedWeatherLive.value = locationsList.favoriteWeatherList[selectedWeatherIndex]
//            } else {
//                selectedWeatherLive.value = WeatherPresenter(Location("", 0.0, 0.0))
//            }
//            save()
//        }
    }

    /**
     * Updates current selected weather view
     */
    fun updateSelectedWeather() {
//        updateWeatherImitation(locationsList.favoriteWeatherList[selectedWeatherIndex].location, selectedWeatherIndex)
    }

    /**
     * Checks if requested location name is valid and not in favorite list
     */
    private fun checkLocation(requestLocation: String): Boolean {
        return (availableLocationsData.getLocation(requestLocation) != null
                && !locationIsFavorite(requestLocation))
    }

    fun locationIsAvailable(requestLocation: String): Boolean {
        return availableLocationsData.getLocation(requestLocation) != null
    }

    /**
     * Checks if favorite list contains requested location
     */
    private fun locationIsFavorite(requestLocation: String): Boolean {
//        for (favorites in locationsList.favoriteWeatherList) {
//            if (favorites.location.name.lowercase() == requestLocation) {
//                return true
//            }
//        }
        return false
    }

    /**
     * Checks if favorite list is not empty
     */
//    fun favoritesAdded(): Boolean {
//        return locationsList.favoriteWeatherList.isNotEmpty()
//    }
}