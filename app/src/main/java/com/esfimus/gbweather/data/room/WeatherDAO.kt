package com.esfimus.gbweather.data.room

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface WeatherDAO {
    @Query("SELECT * FROM WeatherEntity")
    fun getAll(): List<WeatherEntity>

    @Insert
    fun insert(entity: WeatherEntity)

    @Delete
    fun delete(entity: WeatherEntity)
}