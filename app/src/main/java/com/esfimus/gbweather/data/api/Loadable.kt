package com.esfimus.gbweather.data.api

import com.esfimus.gbweather.domain.WeatherFormatted

interface Loadable {
    fun loaded(weather: WeatherFormatted)
    fun failed(responseCode: Int)
}