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
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.reflect.Type
import kotlin.random.Random

private const val PREFERENCE_WEATHER = "preference list"
private const val PREFERENCE_INDEX = "preference index"
private const val CURRENT_WEATHER = "location list"
private const val SELECTED_WEATHER_INDEX = "selected weather index"

class SharedViewModel : ViewModel() {

    // available for view
    val selectedWeatherLive: MutableLiveData<WeatherEntity> = MutableLiveData()
//    val weatherPresenterListLive: MutableLiveData<List<WeatherPresenter>> = MutableLiveData()
    val responseFailureLive: MutableLiveData<String> = MutableLiveData()

    // connection to data
    private val availableLocationsData = AvailableLocations()
//    private var locationsList = FavoriteWeather()
    private var selectedWeatherIndex = 0
    var numberOfItems = 0

    // saving parameters
    private var saveWeather: SharedPreferences? = null
    private var saveIndex: SharedPreferences? = null

    fun getSelectedWeatherIndex() = selectedWeatherIndex

    fun setSelectedWeatherIndex(position: Int) {
        selectedWeatherIndex = if (numberOfItems in 1..position) {
            numberOfItems - 1
        } else if (numberOfItems <= 0 || position < 0) {
            0
        } else {
            position
        }
        save()
    }

    fun setCurrentWeather(weather: WeatherEntity) {
        selectedWeatherLive.value = weather
        save()
    }

    /**
     * Sets current weather and selected item index if selected weather is deleted
     */
    fun setWeatherFromList(weatherList: List<WeatherEntity>, position: Int) {
        try {
            if (position == selectedWeatherIndex) {
                // selecting previous in list location if last weather location is selected and deleted
                if (position == weatherList.size - 1) {
                    selectedWeatherLive.value = weatherList[weatherList.size - 2]
                    selectedWeatherIndex = weatherList.size - 2
                // selecting next in list location if selected location is not last but is deleted
                } else {
                    selectedWeatherLive.value = weatherList[position + 1]
                }
            }
        } catch(e: Exception) {
            // clearing fields when all weather locations were deleted
            selectedWeatherLive.value = WeatherEntity()
        }
        save()
    }

    /**
     * Imitates weather update to test app functionality
     */
    private fun updateWeatherImitation(location: Location, position: Int) {
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
    }

    fun getEmptyWeather() = WeatherEntity()

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
            weatherPresenter.pressureFormatted,
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
        val currentWeatherJson = GsonBuilder().create().toJson(selectedWeatherLive.value)
        saveWeather?.edit()?.putString(CURRENT_WEATHER, currentWeatherJson)?.apply()
        saveIndex?.edit()?.putInt(SELECTED_WEATHER_INDEX, selectedWeatherIndex)?.apply()
    }

    /**
     * Loads list of favorite locations and index of selected location
     */
    fun load(context: Context) {
        saveIndex = context.getSharedPreferences(PREFERENCE_INDEX, Context.MODE_PRIVATE)
        val retrievedWeatherIndex = saveIndex?.getInt(SELECTED_WEATHER_INDEX, 0)
        if (retrievedWeatherIndex != null) {
            selectedWeatherIndex = retrievedWeatherIndex
        }
        saveWeather = context.getSharedPreferences(PREFERENCE_WEATHER, Context.MODE_PRIVATE)
        val retrievedWeather = saveWeather?.getString(CURRENT_WEATHER, null)
        if (retrievedWeather != null) {
            val type: Type = object : TypeToken<WeatherEntity>() {}.type
            selectedWeatherLive.value = GsonBuilder().create().fromJson(retrievedWeather, type)
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

    fun getCurrentWeatherPosition() = selectedWeatherIndex

    /**
     * Checks if location is valid and it is already in favorite list,
     * adds location and updates LiveData list and selected location index
     */
//    fun addWeatherLocation(requestLocation: String): String {
//        // location is valid and not in favorites list yet, permission to add location
//        return if (checkLocation(requestLocation)) {
//            val validLocation = availableLocationsData.getLocation(requestLocation)!!
////            locationsList.addWeather(WeatherPresenter(validLocation))
////            selectedWeatherIndex = locationsList.favoriteWeatherList.size - 1
////            updateWeatherImitation(validLocation, selectedWeatherIndex)
////            weatherPresenterListLive.value = locationsList.favoriteWeatherList
////            save()
//            "ok"
//            // location is valid but already in favorites list, cannot be added
//        } else if (locationIsFavorite(requestLocation)) {
//            "in list"
//            // location is not valid, cannot be added
//        } else {
//            "not found"
//        }
//    }

    /**
     * Deletes specified weather location
     */
//    fun deleteWeatherLocation(position: Int) {
////        if (position in 0 until locationsList.favoriteWeatherList.size) {
////            locationsList.deleteWeather(position)
////            weatherPresenterListLive.value = locationsList.favoriteWeatherList
////            selectedWeatherIndex = locationsList.favoriteWeatherList.size - 1
////            if (selectedWeatherIndex >= 0) {
////                selectedWeatherLive.value = locationsList.favoriteWeatherList[selectedWeatherIndex]
////            } else {
////                selectedWeatherLive.value = WeatherPresenter(Location("", 0.0, 0.0))
////            }
////            save()
////        }
//    }

    /**
     * Updates current selected weather view
     */
//    fun updateSelectedWeather() {
////        updateWeatherImitation(locationsList.favoriteWeatherList[selectedWeatherIndex].location, selectedWeatherIndex)
//    }

    /**
     * Checks if requested location name is valid and not in favorite list
     */
//    private fun checkLocation(requestLocation: String): Boolean {
//        return (availableLocationsData.getLocation(requestLocation) != null
//                && !locationIsFavorite(requestLocation))
//    }

    fun locationIsAvailable(requestLocation: String): Boolean {
        return availableLocationsData.getLocation(requestLocation) != null
    }

    /**
     * Checks if favorite list contains requested location
     */
//    private fun locationIsFavorite(requestLocation: String): Boolean {
////        for (favorites in locationsList.favoriteWeatherList) {
////            if (favorites.location.name.lowercase() == requestLocation) {
////                return true
////            }
////        }
//        return false
//    }

    /**
     * Checks if favorite list is not empty
     */
//    fun favoritesAdded(): Boolean {
//        return locationsList.favoriteWeatherList.isNotEmpty()
//    }
}