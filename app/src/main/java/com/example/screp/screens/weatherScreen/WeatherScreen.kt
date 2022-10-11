package com.example.screp.screens.weatherScreen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp
import com.example.screp.data.Weather
import com.example.screp.viewModels.PhotoAndMapViewModel
import com.example.screp.viewModels.WeatherViewModel
import com.google.android.gms.location.FusedLocationProviderClient

@Composable
fun WeatherScreen(
    weatherViewModel: WeatherViewModel,
    photoAndMapViewModel: PhotoAndMapViewModel,
    fusedLocationProviderClient: FusedLocationProviderClient
) {
    val weatherData: Weather? by weatherViewModel.weatherData.observeAsState(null)
    val currentLocation: String by weatherViewModel.currentLocation.observeAsState("")
    var isLoading by remember { mutableStateOf(true) }

    photoAndMapViewModel.requestLocationResultCallback(fusedLocationProviderClient) { locationResult ->
        locationResult.lastLocation?.let { location ->
            weatherViewModel.fetchWeatherData(location.latitude, location.longitude)
        }
    }

    if (weatherData != null && currentLocation.isNotEmpty()) isLoading = false

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
        if (isLoading) {
            Column(
                modifier = Modifier.size(500.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CircularProgressIndicator(color = MaterialTheme.colors.onSecondary)
            }
        } else {
            weatherData?.let {
                CurrentWeather(it, currentLocation)
                HourlyWeather(it)
            }
        }
    }
}