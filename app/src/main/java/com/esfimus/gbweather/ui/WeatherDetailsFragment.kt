package com.esfimus.gbweather.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.esfimus.gbweather.databinding.FragmentWeatherDetailsBinding
import com.esfimus.gbweather.domain.SharedViewModel

class WeatherDetailsFragment : Fragment() {

    private var binding: FragmentWeatherDetailsBinding? = null
    private lateinit var model: SharedViewModel

    companion object {
        fun newInstance() = WeatherDetailsFragment()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = FragmentWeatherDetailsBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initAction()
    }

    private fun initAction() {
        model = ViewModelProvider(requireActivity())[SharedViewModel::class.java]
        model.weather.observe(viewLifecycleOwner) {
            val weatherLocation = it.location.name
            val weatherTemperature = "${it.temperature}°"
            val weatherHumidity = "${it.humidity}%"
            binding?.textFieldLocation?.text = weatherLocation
            binding?.textFieldTemperature?.text = weatherTemperature
            binding?.textFieldHumidity?.text = weatherHumidity
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }
}