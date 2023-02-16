package com.esfimus.gbweather.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.esfimus.gbweather.databinding.FragmentMainBinding
import java.time.LocalDateTime

class MainFragment : Fragment() {

    private var binding: FragmentMainBinding? = null

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
        binding?.buttonCheck?.setOnClickListener {
            binding?.textFieldLocation?.text = currentDateAndTime().first
            binding?.textFieldMessage?.text = currentDateAndTime().second
        }
    }

    private fun currentDateAndTime(): Pair<String, String> {
        val currentDate = LocalDateTime.now()
        val year = "%04d".format(currentDate.year)
        val month = "%02d".format(currentDate.monthValue)
        val day = "%02d".format(currentDate.dayOfMonth)
        val hour = "%02d".format(currentDate.hour)
        val minute = "%02d".format(currentDate.minute)
        val second = "%02d".format(currentDate.second)
        return Pair("$year/$month/$day", "$hour:$minute:$second")
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }
}