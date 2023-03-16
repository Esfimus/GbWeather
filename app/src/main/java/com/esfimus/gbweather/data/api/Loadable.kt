package com.esfimus.gbweather.data.api

import com.esfimus.gbweather.domain.WeatherPresenter

interface Loadable {
    fun loaded(weather: WeatherPresenter)
    fun failed(responseCode: Int)
}