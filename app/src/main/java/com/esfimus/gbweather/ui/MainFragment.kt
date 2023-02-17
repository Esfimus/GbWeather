package com.esfimus.gbweather.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.esfimus.gbweather.databinding.FragmentMainBinding
import com.esfimus.gbweather.domain.SharedViewModel

class MainFragment : Fragment() {

    private var binding: FragmentMainBinding? = null
    private lateinit var model: SharedViewModel

    companion object {
        @JvmStatic
        fun newInstance() = MainFragment()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        binding = FragmentMainBinding.inflate(inflater, container, false)
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

        binding?.buttonCheck?.setOnClickListener {
            val inputLocation = binding?.edittextInputLocation?.text.toString()
            if (inputLocation.isNotEmpty()) {
                model.sendRequest(inputLocation)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }
}