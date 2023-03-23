package com.esfimus.gbweather.data.broadcast

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast

class MyReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        intent.getStringExtra(WEATHER_BROADCAST_EXTRA)?.let {
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
        }
    }
}