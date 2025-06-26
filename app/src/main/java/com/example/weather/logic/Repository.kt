package com.example.weather.logic

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.example.weather.logic.dao.PlaceDao
import com.example.weather.logic.model.Place
import com.example.weather.logic.network.WeatherNetWork
import com.sunnyweather.android.logic.model.Weather
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import okhttp3.OkHttpClient
import kotlin.coroutines.CoroutineContext

object Repository {

    fun savePlaces(place: Place) = PlaceDao.savePlace(place)

    fun getSavedPlaces() = PlaceDao.getSavedPlace()

    fun isPlaceSaved() = PlaceDao.isPlaceSaved()

    fun searchPlaces(query: String) = fire(Dispatchers.IO) {
        val placeResponse = WeatherNetWork.searchPlaces(query)
        if (placeResponse.status == "ok") {
            val places = placeResponse.places
            Result.success(places)
        } else {
            Result.failure(RuntimeException("response status is ${placeResponse.status}"))
        }

    }

    fun refreshWeather(lng: String, lat: String) = fire(Dispatchers.IO){
        coroutineScope {
            val deferredRealtime = async {
                WeatherNetWork.getRealtimeWeather(lng, lat)
            }

            val deferredDaily = async {
                WeatherNetWork.getDailyWeather(lng, lat)
            }

            val realtimeResponse = deferredRealtime.await()
            val dailyResponse = deferredDaily.await()

            if (realtimeResponse.status == "ok" && dailyResponse.status == "ok") {
                val weather = Weather(realtimeResponse.result.realtime, dailyResponse.result.daily)

                Result.success(weather)
            } else {
                Result.failure(RuntimeException(
                    "realtime response status is ${realtimeResponse.status}" +
                            "daily response status is ${dailyResponse.status}"))
            }
        }
    }

    private fun <T> fire(context: CoroutineContext, block: suspend () -> Result<T>) =
        liveData(context) {
            val result = try {
                block()
            } catch (e: Exception) {
                Result.failure(e)
            }
            emit(result)
        }
}