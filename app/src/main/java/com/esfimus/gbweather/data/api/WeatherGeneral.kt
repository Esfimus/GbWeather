package com.esfimus.gbweather.data.api

import com.google.gson.annotations.SerializedName

data class WeatherGeneral(
    @SerializedName("fact")
    val fact: WeatherFact,
    @SerializedName("forecast")
    val forecast: WeatherForecast,
    @SerializedName("info")
    val info: WeatherInfo,
    @SerializedName("now")
    val now: Int,
    @SerializedName("now_dt")
    val nowDt: String
)