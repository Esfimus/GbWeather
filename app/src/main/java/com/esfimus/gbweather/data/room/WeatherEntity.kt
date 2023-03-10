package com.esfimus.gbweather.data.room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class WeatherEntity (
    @PrimaryKey (autoGenerate = true) val id: Long,
    @ColumnInfo(name = "location_name") val locationName: String?,
    @ColumnInfo(name = "location_latitude") val locationLat: Double?,
    @ColumnInfo(name = "location_longitude") val locationLon: Double?,
    @ColumnInfo(name = "time") val time: String?,
    @ColumnInfo(name = "temperature") val temperature: String?,
    @ColumnInfo(name = "feels_like") val feelsLike: String?,
    @ColumnInfo(name = "humidity") val humidity: String?,
    @ColumnInfo(name = "wind") val wind: String?,
    @ColumnInfo(name = "pressure") val pressure: String?,
)