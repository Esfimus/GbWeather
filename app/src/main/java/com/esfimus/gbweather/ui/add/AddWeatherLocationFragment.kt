package com.esfimus.gbweather.ui.add

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import com.esfimus.gbweather.databinding.FragmentAddWeatherLocationBinding
import com.esfimus.gbweather.ui.SharedViewModel
import com.google.android.material.snackbar.Snackbar

class AddWeatherLocationFragment : Fragment() {

    private var _ui: FragmentAddWeatherLocationBinding? = null
    private val ui get() = _ui!!

    companion object {
        fun newInstance() = AddWeatherLocationFragment()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View {
        _ui = FragmentAddWeatherLocationBinding.inflate(inflater, container, false)
        return ui.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initAction()
    }

    private fun initAction() {
        val searchView: TextView = ui.searchLocationText
        val model: SharedViewModel by lazy { ViewModelProvider(requireActivity())[SharedViewModel::class.java] }
        ui.searchLocationButton.setOnClickListener {
            view?.hideKeyboard()
            when (model.addWeatherLocation(searchView.text.toString())) {
                "ok" -> requireActivity().supportFragmentManager.popBackStack()
                "null" -> view?.snackMessage("Location is not available")
                "in list" -> view?.snackMessage("Location is already favorite")
                else -> view?.snackMessage("Location is not found")
            }
        }
    }

    private fun View.snackMessage(text: String, length: Int = Snackbar.LENGTH_SHORT) {
        Snackbar.make(this, text, length).show()
    }

    private fun View.hideKeyboard() {
        (context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager).run {
            hideSoftInputFromWindow(windowToken, 0)
        }
    }

    override fun onDestroyView() {
        _ui = null
        super.onDestroyView()
    }
}