package com.esfimus.gbweather.data.room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class WeatherEntity (
    @PrimaryKey @ColumnInfo(name = "location_name") val locationName: String = "",
    @ColumnInfo(name = "location_latitude") val locationLat: String = "",
    @ColumnInfo(name = "location_longitude") val locationLon: String = "",
    @ColumnInfo(name = "time") val currentTime: String = "",
    @ColumnInfo(name = "temperature") val temperature: String = "",
    @ColumnInfo(name = "feels") val feelsLike: String = "",
    @ColumnInfo(name = "humidity") val humidity: String = "",
    @ColumnInfo(name = "wind") val wind: String = "",
    @ColumnInfo(name = "pressure") val pressure: String = "",
    @ColumnInfo(name = "icon") val iconLink: String = "",
)