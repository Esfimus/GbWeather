package com.esfimus.gbweather.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import com.esfimus.gbweather.databinding.FragmentAddWeatherLocationBinding
import com.esfimus.gbweather.domain.SharedViewModel
import com.google.android.material.snackbar.Snackbar

class AddWeatherLocationFragment : Fragment() {

    private var bindingNullable: FragmentAddWeatherLocationBinding? = null
    private val binding get() = bindingNullable!!

    companion object {
        fun newInstance() = AddWeatherLocationFragment()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View {
        bindingNullable = FragmentAddWeatherLocationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initAction()
    }

    private fun initAction() {
        val searchView: TextView = binding.searchLocationText
        val model = ViewModelProvider(requireActivity())[SharedViewModel::class.java]
        binding.searchLocationButton.setOnClickListener {
            if (model.addWeatherLocation(searchView.text.toString()) == 1) {
                model.save()
                requireActivity().supportFragmentManager.popBackStack()
            } else if (model.addWeatherLocation(searchView.text.toString()) == 0){
                snackMessage("Location is already favorite")
            } else {
                snackMessage("Location is not found")
            }
        }
    }

    private fun snackMessage(text: String) {
        binding.searchLocationButton.let { Snackbar.make(it, text, Snackbar.LENGTH_SHORT).show() }
    }

    override fun onDestroy() {
        super.onDestroy()
        bindingNullable = null
    }

}