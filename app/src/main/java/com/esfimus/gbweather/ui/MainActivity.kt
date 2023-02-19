package com.esfimus.gbweather.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.esfimus.gbweather.R
import com.esfimus.gbweather.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        runFragment()
    }

    private fun runFragment() {
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.main_container, WeatherDetailsFragment.newInstance())
            .commit()
    }
}