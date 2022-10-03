package com.example.screp.repository

import com.example.screp.BuildConfig
import com.example.screp.data.Weather
import com.example.screp.network.WeatherApi

class WeatherRepository {
    var apiKey = BuildConfig.API_KEY

    suspend fun fetchWeatherData(): Weather = WeatherApi.retrofitService.fetchWeatherData(
        60.22949143644944,
        24.825288623238496,
        "metric",
        "minutely",
        apiKey
    )
}