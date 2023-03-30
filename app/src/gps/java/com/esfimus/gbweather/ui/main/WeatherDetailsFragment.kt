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
import coil.ImageLoader
import coil.decode.SvgDecoder
import coil.load
import coil.request.ImageRequest
import com.esfimus.gbweather.R
import com.esfimus.gbweather.data.room.WeatherEntity
import com.esfimus.gbweather.data.room.WeatherViewModel
import com.esfimus.gbweather.databinding.FragmentWeatherDetailsBinding
import com.esfimus.gbweather.domain.CustomLocation
import com.esfimus.gbweather.ui.SharedViewModel
import com.esfimus.gbweather.data.broadcast.BroadcastService
import com.esfimus.gbweather.data.broadcast.WEATHER_LOCATION_EXTRA
import com.esfimus.gbweather.ui.content_provider.ContactsFragment
import com.esfimus.gbweather.ui.favorite.FavoriteWeatherListFragment
import com.esfimus.gbweather.ui.gps_map_location.GpsWeatherFragment
import com.google.android.material.snackbar.Snackbar

const val weather_icon_link = "https://cdn-icons-png.flaticon.com/512/1231/1231983.png?w=740&t=st=1678038447~exp=1678039047~hmac=a139563c6f772c7c2d7099fa7f027ffd7c68a46dcffad2dd955920c794bee33b"

class WeatherDetailsFragment : Fragment() {

    private var _ui: FragmentWeatherDetailsBinding? = null
    private val ui get() = _ui!!
    private val model: SharedViewModel by lazy {
        ViewModelProvider(requireActivity())[SharedViewModel::class.java] }
    private val weatherViewModel: WeatherViewModel by lazy {
        ViewModelProvider(this)[WeatherViewModel::class.java] }
    private lateinit var currentWeather: WeatherEntity

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
        initView()
        startBroadcastService()
    }

    private fun initView() {
        context?.let { model.load(it) }
        val weatherIcon: ImageView = ui.weatherIcon
        weatherIcon.load(weather_icon_link)
        model.selectedWeatherLive.observe(viewLifecycleOwner) { w ->
            ui.loadingProgressBar.visibility = View.GONE
            currentWeather = w
            with (ui) {
                textFieldLocation.text = w.locationName
                textFieldLatitude.text = w.locationLat
                textFieldLongitude.text = w.locationLon
                textFieldTemperature.text = w.temperature
                textFieldFeelsLike.text = w.feelsLike
                textFieldHumidity.text = w.humidity
                textFieldWind.text = w.wind
                textFieldPressure.text = w.pressure
                currentTime.text = w.currentTime
                currentWeatherIcon.loadSvg(w.iconLink)
            }
        }
        model.responseFailureLive.observe(viewLifecycleOwner) {
            view?.snackMessage(it.toString())
        }
        ui.updateWeatherFab.setOnClickListener {
            refreshWeather()
        }
        ui.favoriteLocationsListFab.setOnClickListener {
            openFragment(FavoriteWeatherListFragment.newInstance())
        }
        ui.getContactsContentProviderFab.setOnClickListener {
            openFragment(ContactsFragment.newInstance())
        }
        ui.getGpsWeatherFab.setOnClickListener {
            openFragment(GpsWeatherFragment.newInstance())
        }
    }

    private fun startBroadcastService() {
        var customLocation: CustomLocation? = null
        model.selectedWeatherLive.observe(viewLifecycleOwner) {
            customLocation = model.getLocation(it.locationName)
        }
        ui.updateWeatherBroadcastFab.setOnClickListener {
            context?.let {
                it.startService(Intent(it, BroadcastService::class.java).apply {
                    putExtra(WEATHER_LOCATION_EXTRA, customLocation)
                })
            }
        }
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
        try {
            if (currentWeather.locationName.isNotEmpty()) {
                ui.loadingProgressBar.visibility = View.VISIBLE
                model.loadWeatherRetrofit(currentWeather.locationName)
                weatherViewModel.updateWeather(currentWeather)
            } else {
                view?.snackMessage("Please, select location")
            }
        } catch (e: Exception) {
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