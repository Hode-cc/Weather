package com.sunnyweather.android.logic.model

import com.example.weather.logic.model.DailyResponse
import com.example.weather.logic.model.RealtimeResponse

class Weather(val realtime: RealtimeResponse.Realtime, val daily: DailyResponse.Daily)