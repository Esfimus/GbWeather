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
    val responseFailureLive: MutableLiveData<String> = MutableLiveData()

    // connection to data
    private val availableLocationsData = AvailableLocations()
    private var selectedWeatherIndex = 0
    var numberOfItems = 0
    private val emptyLocation = Location("", 0.0, 0.0)

    // saving parameters
    private var saveWeather: SharedPreferences? = null
    private var saveIndex: SharedPreferences? = null

    /**
     * Keeps weather index positive and in range
     */
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

    /**
     * Updates live weather data with given weather
     */
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
            } else {
                // moving selected index if any other item is deleted
                selectedWeatherIndex--
            }
        } catch(e: Exception) {
            // clearing fields when all weather locations were deleted
            selectedWeatherLive.value = WeatherEntity()
        }
        save()
    }

    /**
     * Imitating received data to check app functionality
     */
    fun loadWeatherImitation(requestLocation: String) {
        val location = availableLocationsData.getLocation(requestLocation) ?: emptyLocation
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
        setCurrentWeather(weatherObjectConverter(weatherPresenter))
    }

    /**
     * Uploading data from weather API using Retrofit
     */
    fun loadWeatherRetrofit(requestLocation: String) {
        val location = availableLocationsData.getLocation(requestLocation) ?: Location("", 0.0, 0.0)
        val callBack = object : Callback<WeatherLoaded> {
            override fun onResponse(call: Call<WeatherLoaded>, response: Response<WeatherLoaded>) {
                val weatherLoaded: WeatherLoaded? = response.body()
                val weatherPresenter = WeatherPresenter(location, weatherLoaded)
                setCurrentWeather(weatherObjectConverter(weatherPresenter))
                when (response.code()) {
                    in 300 until 400 -> responseFailureLive.value = "Redirection"
                    in 400 until 500 -> responseFailureLive.value = "Client Error"
                    in 500 until 600 -> responseFailureLive.value = "Server Error"
                }
            }
            override fun onFailure(call: Call<WeatherLoaded>, t: Throwable) {
                Log.d("RetrofitFailure", "${t.message}")
            }
        }
        val loadRetrofitWeather = LoadRetrofitWeather()
        loadRetrofitWeather.getWeather(location.lat, location.lon, callBack)
    }

    /**
     * Converts loaded weather object for storage in database
     */
    private fun weatherObjectConverter(weatherPresenter: WeatherPresenter): WeatherEntity {
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
            weatherPresenter.iconLink
        )
    }

    /**
     * Saves current live weather and index of selected location
     */
    private fun save() {
        val currentWeatherJson = GsonBuilder().create().toJson(selectedWeatherLive.value)
        saveWeather?.edit()?.putString(CURRENT_WEATHER, currentWeatherJson)?.apply()
        saveIndex?.edit()?.putInt(SELECTED_WEATHER_INDEX, selectedWeatherIndex)?.apply()
    }

    /**
     * Loads current live data and index of selected location
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
     * Checks location's name
     */
    fun locationIsAvailable(requestLocation: String): Boolean {
        return availableLocationsData.getLocation(requestLocation) != null
    }

    /**
     * Returns location by name if it is available
     */
    fun getLocation(requestLocation: String): Location {
        return availableLocationsData.getLocation(requestLocation) ?: emptyLocation
    }
}