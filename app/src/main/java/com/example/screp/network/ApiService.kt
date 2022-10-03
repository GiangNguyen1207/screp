package com.example.screp.network

import com.example.screp.data.Weather
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Retrofit
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.converter.moshi.MoshiConverterFactory

private const val WEATHER_API = "https://api.openweathermap.org/data/2.5/"

private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()

private val retrofit = Retrofit.Builder()
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .baseUrl(WEATHER_API)
    .build()

interface ApiService {
    @GET("onecall")
    suspend fun fetchWeatherData(
        @Query("lat") latitude: Double,
        @Query("lon") longitude: Double,
        @Query("units") units: String,
        @Query("exclude") exclude: String,
        @Query("appid") apiKey: String
    ): Weather
}

object WeatherApi {
    val retrofitService: ApiService by lazy {
        retrofit.create(ApiService::class.java)
    }
}