package com.example.screp.data

data class CurrentWeather(
    val dt: Long,
    val sunrise: Long,
    val sunset: Long,
    val temp: Double,
    val feels_like: Double,
    val pressure: Int,
    val humidity: Int,
    val visibility: Long,
    val wind_speed: Double,
    val weather: List<WeatherDescription>
)