package com.esfimus.gbweather.ui.broadcast

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast
import com.esfimus.gbweather.data.WEATHER_BROADCAST_EXTRA

class MyReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        intent.getStringExtra(WEATHER_BROADCAST_EXTRA)?.let {
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
        }
    }
}