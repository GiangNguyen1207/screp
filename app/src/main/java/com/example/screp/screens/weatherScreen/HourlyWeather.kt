package com.example.screp.screens.weatherScreen

import androidx.compose.foundation.Image
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.example.screp.data.Weather
import com.example.screp.helpers.CalendarUtil
import com.example.screp.helpers.Converter

@Composable
fun HourlyWeather(weatherData: Weather) {
    val calendarUtil = CalendarUtil()
    val nextDate = calendarUtil.getNextDate()
    val hourlyWeatherData = weatherData.hourly.filter { hourlyWeather ->
        hourlyWeather.dt * 1000L < nextDate
    }

    Card(
        backgroundColor = MaterialTheme.colors.background.copy(alpha = 0.2f),
        shape = RoundedCornerShape(15.dp),
        elevation = 0.dp,
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Row(modifier = Modifier.horizontalScroll(rememberScrollState())) {
            hourlyWeatherData.forEach { hw ->
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = calendarUtil.getTime(hw.dt * 1000L, hasMinute = false),
                        color = MaterialTheme.colors.onPrimary
                    )
                    Image(
                        painter = rememberAsyncImagePainter("http://openweathermap.org/img/wn/${hw.weather[0].icon}@2x.png"),
                        contentDescription = hw.weather[0].description,
                        modifier = Modifier.size(50.dp)
                    )
                    Text(
                        text = "${Converter().roundingTemperature(hw.temp)}\u2103",
                        color = MaterialTheme.colors.onPrimary
                    )
                }
            }
        }
    }
}