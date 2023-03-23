package com.esfimus.gbweather.data.firebase

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.util.Log
import androidx.core.app.NotificationCompat
import com.esfimus.gbweather.R
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

const val FIREBASE = "FirebaseToken"

class FirebaseService : FirebaseMessagingService() {

    companion object {
        private const val TITLE = "title"
        private const val MESSAGE = "message"
        private const val CHANNEL = "channel"
        private const val NOTIFICATION_ID = 797
    }

    override fun onNewToken(token: String) {
        Log.d(FIREBASE, "Firebase token: $token")
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        if (remoteMessage.data.isNotEmpty()) {
            val dataMap = remoteMessage.data.toMap()
            val title = dataMap[TITLE]
            val message = dataMap[MESSAGE]
            if (!title.isNullOrBlank() && !message.isNullOrBlank()) {
                showNotification(title, message)
            }
        }
    }

    private fun showNotification(title: String, message: String) {
        val notificationBuilder = NotificationCompat.Builder(applicationContext, CHANNEL).apply {
            setSmallIcon(R.drawable.weather_icon)
            setContentTitle(title)
            setContentText(message)
            priority = NotificationCompat.PRIORITY_DEFAULT
        }
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationChannel(notificationManager)
        notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build())
    }

    private fun notificationChannel(notificationManager: NotificationManager) {
        val name = "Channel name"
        val descriptionText = "Channel description"
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val channel = NotificationChannel(CHANNEL, name, importance).apply {
            description = descriptionText
        }
        notificationManager.createNotificationChannel(channel)
    }
}