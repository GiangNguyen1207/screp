package com.example.screp.repository

import com.example.screp.BuildConfig
import com.example.screp.data.Geocode
import com.example.screp.data.Weather
import com.example.screp.network.WeatherApi

class WeatherRepository {
    var apiKey = BuildConfig.API_KEY

    suspend fun fetchWeatherData(latitude: Double, longitude: Double): Weather =
        WeatherApi.retrofitService.fetchWeatherData(
            latitude,
            longitude,
            "metric",
            "minutely",
            apiKey
        )


    suspend fun fetchGeoCode(latitude: Double, longitude: Double): List<Geocode> =
        WeatherApi.retrofitService.fetchGeoCode(
            latitude,
            longitude,
            apiKey
        )
}