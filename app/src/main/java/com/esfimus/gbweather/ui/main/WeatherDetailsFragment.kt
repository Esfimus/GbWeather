package com.esfimus.gbweather.ui.main

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.ViewModelProvider
import com.esfimus.gbweather.R
import com.esfimus.gbweather.databinding.FragmentWeatherDetailsBinding
import com.esfimus.gbweather.domain.Location
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

    private fun listenWeatherList() {
        model.selectedWeatherLive.observe(viewLifecycleOwner) {
            with (ui) {
                textFieldLocation.text = it.location.name
                textFieldLatitude.text = it.weatherLoaded.info.lat.toString()
                textFieldLongitude.text = it.weatherLoaded.info.lon.toString()
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
//            model.updateWeather() // TODO check index
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