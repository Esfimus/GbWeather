package com.esfimus.gbweather.ui.main

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.esfimus.gbweather.R
import com.esfimus.gbweather.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var ui: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        ui = ActivityMainBinding.inflate(layoutInflater)
        setContentView(ui.root)

        runFragment()
    }

    private fun runFragment() {
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.main_container, WeatherDetailsFragment.newInstance())
            .commit()
    }
}