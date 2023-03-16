package com.esfimus.gbweather.ui.gps_map_location

import android.Manifest
import android.app.AlertDialog
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.esfimus.gbweather.R
import com.esfimus.gbweather.databinding.FragmentGpsWeatherBinding
import com.google.android.material.snackbar.Snackbar

private const val REQUEST_CODE = 333
private const val LOCATION_REFRESH_PERIOD = 60000L
private const val LOCATION_DISTANCE = 1000f

class GpsWeatherFragment : Fragment() {

    private var _ui: FragmentGpsWeatherBinding? = null
    private val ui get() = _ui!!
    private val locationListener = object : LocationListener {
        override fun onLocationChanged(location: Location) {
            val latitude = location.latitude
            val longitude = location.longitude
            view?.snackMessage("Lat: $latitude Lon: $longitude")
        }
        override fun onProviderEnabled(provider: String) {
            view?.snackMessage("Location is enabled")
        }
        override fun onProviderDisabled(provider: String) {
            view?.snackMessage("Location is disabled")
        }
    }

    companion object {
        fun newInstance() = GpsWeatherFragment()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View {
        _ui = FragmentGpsWeatherBinding.inflate(inflater, container, false)
        return ui.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        checkPermission()
    }

    private fun checkPermission() {
        context?.let {
            when {
                ContextCompat.checkSelfPermission(it, Manifest.permission.ACCESS_FINE_LOCATION) ==
                        PackageManager.PERMISSION_GRANTED -> {
                            getLocation()
                        }
                shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION) -> {
                    explanationDialog(true)
                }
                else -> mRequestPermission()
            }
        }
    }

    private fun getLocation() {
        context?.let {
            val locationManager = it.getSystemService(Context.LOCATION_SERVICE) as LocationManager
            if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                @Suppress("deprecation")
                val providerGps = locationManager.getProvider(LocationManager.GPS_PROVIDER)
                providerGps?.let { _ ->
                    if (ActivityCompat.checkSelfPermission(
                            it,
                            Manifest.permission.ACCESS_FINE_LOCATION
                        ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                            it,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                        ) != PackageManager.PERMISSION_GRANTED
                    ) {
                        return
                    }
                    locationManager.requestLocationUpdates(
                        LocationManager.GPS_PROVIDER,
                        LOCATION_REFRESH_PERIOD,
                        LOCATION_DISTANCE,
                        locationListener
                    )
                }
            }
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            REQUEST_CODE -> {
                var grantedPermissions = 0
                if (grantResults.isNotEmpty()) {
                    for (result in grantResults) {
                        if (result == PackageManager.PERMISSION_GRANTED) {
                            grantedPermissions++
                        }
                    }
                    if (grantResults.size == grantedPermissions) {
                        getLocation()
                    } else {
                        explanationDialog(false)
                    }
                } else {
                    explanationDialog(false)
                }
                return
            }
        }
    }

    private fun explanationDialog(selection: Boolean) {
        context?.let {
            if (selection) {
                AlertDialog.Builder(it)
                    .setTitle(getString(R.string.location_access_title))
                    .setMessage(getString(R.string.location_access_message))
                    .setPositiveButton(getString(R.string.access_yes)) {
                            _, _ -> mRequestPermission() }
                    .setNegativeButton(getString(R.string.access_no)) { dialog, _ ->
                        dialog.cancel()
                        requireActivity().supportFragmentManager.popBackStack()
                    }
                    .create()
                    .show()
            } else {
                AlertDialog.Builder(it)
                    .setTitle(getString(R.string.location_access_title))
                    .setMessage(getString(R.string.location_access_message))
                    .setNegativeButton(getString(R.string.access_ok)) { dialog, _ -> dialog.cancel() }
                    .create()
                    .show()
            }

        }
    }

    private fun mRequestPermission() {
        @Suppress("deprecation")
        requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), REQUEST_CODE)
    }

    private fun View.snackMessage(text: String, length: Int = Snackbar.LENGTH_SHORT) {
        Snackbar.make(this, text, length).show()
    }

    override fun onDestroyView() {
        _ui = null
        super.onDestroyView()
    }

}