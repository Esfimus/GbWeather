package com.esfimus.gbweather.data.room

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface WeatherDao {
    @Query("SELECT * FROM WeatherEntity")
    fun getAllWeatherItems(): LiveData<List<WeatherEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: WeatherEntity)

    @Update
    suspend fun update(entity: WeatherEntity)

    @Delete
    suspend fun delete(entity: WeatherEntity)
}