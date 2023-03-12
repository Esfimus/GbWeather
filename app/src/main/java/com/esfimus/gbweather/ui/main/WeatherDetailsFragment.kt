package com.esfimus.gbweather.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.ViewModelProvider
import coil.ImageLoader
import coil.decode.SvgDecoder
import coil.load
import coil.request.ImageRequest
import com.esfimus.gbweather.R
import com.esfimus.gbweather.data.room.WeatherEntity
import com.esfimus.gbweather.data.room.WeatherViewModel
import com.esfimus.gbweather.data.weather_icon_link
import com.esfimus.gbweather.databinding.FragmentWeatherDetailsBinding
import com.esfimus.gbweather.domain.Location
import com.esfimus.gbweather.domain.WeatherPresenter
import com.esfimus.gbweather.domain.api.WeatherFact
import com.esfimus.gbweather.domain.api.WeatherForecast
import com.esfimus.gbweather.domain.api.WeatherInfo
import com.esfimus.gbweather.domain.api.WeatherLoaded
import com.esfimus.gbweather.ui.SharedViewModel
import com.esfimus.gbweather.ui.favorite.FavoriteWeatherListFragment
import com.google.android.material.snackbar.Snackbar
import kotlin.random.Random

class WeatherDetailsFragment : Fragment() {

    private var _ui: FragmentWeatherDetailsBinding? = null
    private val ui get() = _ui!!
    private val model: SharedViewModel by lazy {
        ViewModelProvider(requireActivity())[SharedViewModel::class.java] }
    private val weatherViewModel: WeatherViewModel by lazy {
        ViewModelProvider(this)[WeatherViewModel::class.java]
    }
    var counter = 0
    var listItems = 0

    companion object {
        fun newInstance() = WeatherDetailsFragment()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        _ui = FragmentWeatherDetailsBinding.inflate(inflater, container, false)
        return ui.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initAction()
        startBroadcastService()
    }

    private fun initAction() {
        val weatherIcon: ImageView = ui.weatherIcon
        weatherIcon.load(weather_icon_link)
        weatherViewModel.weatherList.observe(viewLifecycleOwner) {
            listItems = it.size
        }
//        context?.let { model.load(it) }
//        listenWeatherList()
        ui.updateWeather.setOnClickListener {
            refreshWeather()
//            listenWeatherList()
        }
        ui.locationList.setOnClickListener {
            openFragment(FavoriteWeatherListFragment.newInstance())
        }
    }

    private fun startBroadcastService() {
//        var location: Location? = null
//        model.selectedWeatherLive.observe(viewLifecycleOwner) {
//            location = it.location
//        }
        ui.updateWeatherBroadcast.setOnClickListener {
            updateThisWeather()
//            context?.let {
//                it.startService(Intent(it, BroadcastService::class.java).apply {
//                    putExtra(WEATHER_LOCATION_EXTRA, location)
//                })
//            }
        }
    }

    private fun listenWeatherList() {
//        val currentWeatherIcon: ImageView = ui.currentWeatherIcon
//        model.selectedWeatherLive.observe(viewLifecycleOwner) {
//            with (ui) {
//                textFieldLocation.text = it.location.name
//                textFieldLatitude.text = it.location.lat.toString()
//                textFieldLongitude.text = it.location.lon.toString()
//                textFieldTemperature.text = it.temperatureView
//                textFieldFeelsLike.text = it.feelsLikeView
//                textFieldHumidity.text = it.humidityView
//                textFieldWind.text = it.windView
//                textFieldPressure.text = it.pressureView
//                currentTime.text = it.currentTimeView
//                currentWeatherIcon.loadSvg("https://yastatic.net/weather/i/icons/funky/dark/${it.weatherLoaded?.fact?.icon}.svg")
//            }
//        }
//        model.responseFailureLive.observe(viewLifecycleOwner) {
//            view?.snackMessage(it.toString())
//        }
    }

    private fun ImageView.loadSvg(url: String) {
        val imageLoader = ImageLoader.Builder(this.context)
            .components { add(SvgDecoder.Factory()) }
            .build()
        val request = ImageRequest.Builder(this.context)
            .crossfade(true)
            .crossfade(500)
            .data(url)
            .target(this)
            .build()
        imageLoader.enqueue(request)
    }

    private fun refreshWeather() {
//        Toast.makeText(context, "items: $listItems", Toast.LENGTH_SHORT).show()
        if (listItems > 0) {
//            updateThisWeather()
        } else {
            view?.snackMessage("Please, select location")
        }
    }

    private fun updateThisWeather() {
        val location = Location("$counter", (counter.toDouble() + 1) * 3.5847, (counter.toDouble() + 1) * 4.57936)
        counter++
        val weather = WeatherPresenter(location, WeatherLoaded(
            WeatherFact("condition", "daytime", Random.nextInt(-30,30),
                Random.nextInt(10,100), "icon", Random.nextInt(0, 10),
                true, Random.nextInt(735,745), Random.nextInt(100,200),
                "season", Random.nextInt(-30,30), "wind", 0.0, 0.0),
            WeatherForecast("date", 0, 0, "moon", listOf(), "sunrise", "sunset", 0),
            WeatherInfo(location.lat, location.lon, "url"),
            0,
            "nowDt")
        )
        val weatherEntity = WeatherEntity(
            weather.location.name,
            weather.location.lat.toString(),
            weather.location.lon.toString(),
            weather.currentTimeFormatted,
            weather.temperatureFormatted,
            weather.feelsLikeFormatted,
            weather.humidityFormatted,
            weather.windFormatted,
            weather.pressureFormatted
        )
        weatherViewModel.addWeather(weatherEntity)

        with (ui) {
            textFieldLocation.text = weatherEntity.locationName
            textFieldLatitude.text = weatherEntity.locationLat
            textFieldLongitude.text = weatherEntity.locationLon
            textFieldTemperature.text = weatherEntity.temperature
            textFieldFeelsLike.text = weatherEntity.feelsLike
            textFieldHumidity.text = weatherEntity.humidity
            textFieldWind.text = weatherEntity.wind
            textFieldPressure.text = weatherEntity.pressure
            currentTime.text = weatherEntity.currentTime
        }
    }

    private fun openFragment(fragment: Fragment) {
        requireActivity()
            .supportFragmentManager
            .beginTransaction()
            .replace(R.id.main_container, fragment)
            .addToBackStack(null)
            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
            .commit()
    }

    private fun View.snackMessage(text: String, length: Int = Snackbar.LENGTH_SHORT) {
        Snackbar.make(this, text, length).show()
    }

    override fun onDestroyView() {
        _ui = null
        super.onDestroyView()
    }
}