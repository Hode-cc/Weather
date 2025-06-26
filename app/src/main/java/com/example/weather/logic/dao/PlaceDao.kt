package com.example.weather.logic.dao

import android.content.Context
import com.example.weather.WeatherApplication
import com.example.weather.logic.model.Place
import com.google.gson.Gson

object PlaceDao {

    fun savePlace(place: Place) {
        sharedPreferences().edit().apply{
            putString("place", Gson().toJson(place))
        }
    }

    fun getSavedPlace(): Place {
        val placeJson = sharedPreferences().getString("place", "")
        return Gson().fromJson(placeJson, Place::class.java)
    }

    fun isPlaceSaved() = sharedPreferences().contains("place")

    private fun sharedPreferences() = WeatherApplication.context.getSharedPreferences("sunny_weather", Context.MODE_PRIVATE)
}