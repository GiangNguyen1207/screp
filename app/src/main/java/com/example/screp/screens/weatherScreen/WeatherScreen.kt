package com.example.screp.screens.weatherScreen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll

import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import com.example.screp.data.Weather
import com.example.screp.viewModels.WeatherViewModel

@Composable
fun WeatherScreen(weatherViewModel: WeatherViewModel) {
    val weatherData: Weather? by weatherViewModel.weatherData.observeAsState(null)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colors.secondary,
                        MaterialTheme.colors.primaryVariant
                    )
                )
            )
    ) {
        weatherData?.let {
            CurrentWeather(it)
            HourlyWeather(it)
        }
    }
}