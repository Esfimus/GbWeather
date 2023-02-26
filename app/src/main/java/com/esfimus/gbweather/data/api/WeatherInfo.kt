package com.esfimus.gbweather.data.api

import com.google.gson.annotations.SerializedName

data class WeatherInfo(
    @SerializedName("lat")
    val lat: Double,
    @SerializedName("lon")
    val lon: Double,
    @SerializedName("url")
    val url: String
)