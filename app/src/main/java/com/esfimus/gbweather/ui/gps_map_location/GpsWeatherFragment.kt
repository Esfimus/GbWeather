package com.esfimus.gbweather.ui.gps_map_location

import android.Manifest
import android.app.AlertDialog
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import coil.ImageLoader
import coil.decode.SvgDecoder
import coil.request.ImageRequest
import com.esfimus.gbweather.R
import com.esfimus.gbweather.databinding.FragmentGpsWeatherBinding
import com.google.android.material.snackbar.Snackbar

private const val REQUEST_CODE = 333
private const val LOCATION_REFRESH_PERIOD = 3000L
private const val LOCATION_DISTANCE = 100f

class GpsWeatherFragment : Fragment() {

    private var _ui: FragmentGpsWeatherBinding? = null
    private val ui get() = _ui!!
    private val gpsModel: GpsWeatherViewModel by lazy {
        ViewModelProvider(requireActivity())[GpsWeatherViewModel::class.java] }
    private val locationListener = object : LocationListener {
        override fun onLocationChanged(location: Location) {
            initView(location)
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
        context?.let { context ->
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED) {
                val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
                if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    @Suppress("deprecation")
                    val providerGps = locationManager.getProvider(LocationManager.GPS_PROVIDER)
                    providerGps?.let {
                        locationManager.requestLocationUpdates(
                            LocationManager.GPS_PROVIDER,
                            LOCATION_REFRESH_PERIOD,
                            LOCATION_DISTANCE,
                            locationListener
                        )
                    }
                } else {
                    val location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                    if (location == null) {
                        view?.snackMessage("Location cannot be detected")
                    } else {
                        initView(location)
                        view?.snackMessage("No signal to detect location")
                    }
                }
            } else {
                explanationDialog(true)
            }
        }
    }

    private fun initView(location: Location) {
        val latitude = "Lat: " + "%.5f".format(location.latitude)
        val longitude = "Lon: " + "%.5f".format(location.longitude)

        getAddress(requireContext(), location)
        gpsModel.loadWeatherRetrofit(location)

        with (ui) {
            textFieldLatitude.text = latitude
            textFieldLongitude.text = longitude
        }
        gpsModel.weatherLive.observe(viewLifecycleOwner) {
            with (ui) {
                textFieldTemperature.text = it.temperatureFormatted
                textFieldFeelsLike.text = it.feelsLikeFormatted
                textFieldHumidity.text = it.humidityFormatted
                textFieldWind.text = it.windFormatted
                textFieldPressure.text = it.pressureFormatted
                currentTime.text = it.currentTimeFormatted
                currentWeatherIcon.loadSvg(it.iconLink)
            }
        }
        gpsModel.responseFailureLive.observe(viewLifecycleOwner) {
            view?.snackMessage(it)
        }
    }

    private fun getAddress(context: Context, location: Location) {
        val geocoder = Geocoder(context)
        try {
            Thread {
                @Suppress("deprecation")
                val addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1)
                requireActivity().runOnUiThread {
                    ui.textFieldLocation.text = addresses?.get(0)?.getAddressLine(0)
                }
            }.start()
        } catch (e: Exception) {
            view?.snackMessage("Cannot acquire address")
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

    private fun ImageView.loadSvg(url: String) {
        val imageLoader = ImageLoader.Builder(this.context)
            .components { add(SvgDecoder.Factory()) }
            .build()
        val request = ImageRequest.Builder(this.context)
            .crossfade(true)
            .crossfade(500)
            .data(url)
            .target(this)
            .build()
        imageLoader.enqueue(request)
    }

    private fun View.snackMessage(text: String, length: Int = Snackbar.LENGTH_SHORT) {
        Snackbar.make(this, text, length).show()
    }

    override fun onDestroyView() {
        _ui = null
        super.onDestroyView()
    }

}