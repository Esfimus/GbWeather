package com.esfimus.gbweather.ui.main

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.ViewModelProvider
import coil.api.load
import com.esfimus.gbweather.R
import com.esfimus.gbweather.data.WEATHER_LOCATION_EXTRA
import com.esfimus.gbweather.data.weather_icon_link
import com.esfimus.gbweather.databinding.FragmentWeatherDetailsBinding
import com.esfimus.gbweather.domain.Location
import com.esfimus.gbweather.domain.broadcast.BroadcastService
import com.esfimus.gbweather.ui.SharedViewModel
import com.esfimus.gbweather.ui.favorite.FavoriteWeatherListFragment
import com.google.android.material.snackbar.Snackbar

class WeatherDetailsFragment : Fragment() {

    private var _ui: FragmentWeatherDetailsBinding? = null
    private val ui get() = _ui!!
    private val model: SharedViewModel by lazy {
        ViewModelProvider(requireActivity())[SharedViewModel::class.java] }

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
        context?.let { model.load(it) }
        listenWeatherList()
        ui.updateWeather.setOnClickListener {
            refreshWeather()
            listenWeatherList()
        }
        ui.locationList.setOnClickListener {
            openFragment(FavoriteWeatherListFragment.newInstance())
        }
    }

    private fun startBroadcastService() {
        var location: Location? = null
        model.selectedWeatherLive.observe(viewLifecycleOwner) {
            location = it.location
        }
        ui.updateWeatherBroadcast.setOnClickListener {
            context?.let {
                it.startService(Intent(it, BroadcastService::class.java).apply {
                    putExtra(WEATHER_LOCATION_EXTRA, location)
                })
            }
        }
    }

    private fun listenWeatherList() {
        val weatherIcon: ImageView = ui.weatherIcon
        weatherIcon.load(weather_icon_link)
        val currentWeatherIcon: ImageView = ui.currentWeatherIcon
        model.selectedWeatherLive.observe(viewLifecycleOwner) {
            with (ui) {
                textFieldLocation.text = it.location.name
                textFieldLatitude.text = it.location.lat.toString()
                textFieldLongitude.text = it.location.lon.toString()
                textFieldTemperature.text = it.temperatureView
                textFieldFeelsLike.text = it.feelsLikeView
                textFieldHumidity.text = it.humidityView
                textFieldWind.text = it.windView
                textFieldPressure.text = it.pressureView
                currentTime.text = it.currentTimeView

            }
        }
        model.responseFailureLive.observe(viewLifecycleOwner) {
            view?.snackMessage(it.toString())
        }
    }

    private fun refreshWeather() {
        if (model.favoritesAdded()) {
            model.updateSelectedWeather()
        } else {
            view?.snackMessage("Please, select location")
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