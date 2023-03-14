package com.esfimus.gbweather.domain.api

import com.esfimus.gbweather.domain.WeatherPresenter

interface Loadable {
    fun loaded(weather: WeatherPresenter)
    fun failed(responseCode: Int)
}