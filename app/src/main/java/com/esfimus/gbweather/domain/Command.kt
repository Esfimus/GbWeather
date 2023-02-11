package com.esfimus.gbweather.domain

data class Command(val location: String, val message: String) {
    var report = "Command \"$message\" was sent to $location"
}