package com.esfimus.gbweather.domain.api

interface Loadable {
    fun loaded(weather: WeatherGeneral)
    fun failed(responseCode: Int)
}