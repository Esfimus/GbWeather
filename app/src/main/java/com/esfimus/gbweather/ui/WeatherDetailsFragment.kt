package com.esfimus.gbweather.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.ViewModelProvider
import com.esfimus.gbweather.R
import com.esfimus.gbweather.databinding.FragmentWeatherDetailsBinding
import com.esfimus.gbweather.domain.SharedViewModel
import com.google.android.material.snackbar.Snackbar

class WeatherDetailsFragment : Fragment() {

    private var bindingNullable: FragmentWeatherDetailsBinding? = null
    private val binding get() = bindingNullable!!
    private lateinit var model: SharedViewModel

    companion object {
        fun newInstance() = WeatherDetailsFragment()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        bindingNullable = FragmentWeatherDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initAction()
    }

    private fun initAction() {
        model = ViewModelProvider(requireActivity())[SharedViewModel::class.java]
        context?.let { model.load(it) }
        listenWeatherList()
        binding.updateWeather.setOnClickListener {
            refreshWeather()
            listenWeatherList()
        }
        binding.locationList.setOnClickListener {
            openFragment(FavoriteWeatherListFragment.newInstance())
        }
    }

    private fun listenWeatherList() {
        model.selectedWeather.observe(viewLifecycleOwner) {
            binding.textFieldLocation.text = it.location.name
            binding.textFieldTemperature.text = it.temperature
            binding.textFieldFeelsLike.text = it.feelsLike
            binding.textFieldHumidity.text = it.humidity
            binding.textFieldWind.text = it.wind
            binding.textFieldPressure.text = it.pressure
            binding.currentTime.text = it.currentTime
        }
    }

    private fun refreshWeather() {
        if (model.favoritesAdded()) {
            model.updateWeatherList()
        } else {
            snackMessage("Please, select location")
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

    private fun snackMessage(text: String) {
        binding.updateWeather.let { Snackbar.make(it, text, Snackbar.LENGTH_SHORT).show() }
    }

    override fun onDestroy() {
        super.onDestroy()
        bindingNullable = null
    }
}