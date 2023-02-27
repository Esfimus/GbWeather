package com.esfimus.gbweather.domain.api

import com.esfimus.gbweather.domain.WeatherView

interface Loadable {
    fun loaded(weather: WeatherView)
    fun failed(responseCode: Int)
}