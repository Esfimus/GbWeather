package com.esfimus.gbweather.ui.gps_map_location

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.esfimus.gbweather.databinding.FragmentGpsWeatherBinding

class GpsWeatherFragment : Fragment() {

    private var _ui: FragmentGpsWeatherBinding? = null
    private val ui get() = _ui!!

    companion object {
        fun newInstance() = GpsWeatherFragment()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View {
        _ui = FragmentGpsWeatherBinding.inflate(inflater, container, false)
        return ui.root
    }

    override fun onDestroyView() {
        _ui = null
        super.onDestroyView()
    }

}