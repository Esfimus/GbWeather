package com.esfimus.gbweather.ui.gps_map_location

import android.location.Location
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.esfimus.gbweather.data.api.LoadRetrofitWeather
import com.esfimus.gbweather.data.api.WeatherLoaded
import com.esfimus.gbweather.domain.CustomLocation
import com.esfimus.gbweather.domain.WeatherFormatted
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class GpsWeatherViewModel : ViewModel() {

    val weatherLive: MutableLiveData<WeatherFormatted> = MutableLiveData()
    val responseFailureLive: MutableLiveData<String> = MutableLiveData()

    fun loadWeatherRetrofit(location: Location) {
        val callBack = object : Callback<WeatherLoaded> {
            override fun onResponse(call: Call<WeatherLoaded>, response: Response<WeatherLoaded>) {
                val weatherLoaded: WeatherLoaded? = response.body()
                val weatherFormatted = WeatherFormatted(
                    CustomLocation("", location.latitude, location.longitude),
                    weatherLoaded)
                weatherLive.value = weatherFormatted
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
        loadRetrofitWeather.getWeather(location.latitude, location.longitude, callBack)
    }
}