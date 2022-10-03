package com.example.screp.screens.weatherScreen

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.screp.data.Weather
import com.example.screp.helpers.CalendarUtil
import com.example.screp.helpers.Converter

@Composable
fun CurrentWeather(weatherData: Weather) {
    val currentCity = weatherData.timezone.split("/")[1]
    val icon = weatherData.current.weather[0].icon

    val sunriseTime =
        CalendarUtil().getTime((weatherData.current.sunrise) * 1000L)
    val sunsetTime =
        CalendarUtil().getTime((weatherData.current.sunset) * 1000L)

    var weatherDescription = ""

    for (description in weatherData.current.weather) {
        weatherDescription += "${description.main}, ${description.description}\n"
    }

    WeatherCard(
        title = currentCity,
        index = "${Converter().roundingTemperature(weatherData.current.temp)}\u2103",
        image = "http://openweathermap.org/img/wn/$icon@2x.png",
        description = weatherDescription,
        isMain = true,
    )

    Row(horizontalArrangement = Arrangement.SpaceEvenly) {
        Row(modifier = Modifier.weight(0.1f)) {
            WeatherCard(
                title = "Sunrise",
                index = sunriseTime,
            )
        }

        Row(modifier = Modifier.weight(0.1f)) {
            WeatherCard(
                title = "Sunset",
                index = sunsetTime,
            )
        }
    }

    Row {
        Row(modifier = Modifier.weight(0.1f)) {
            WeatherCard(
                title = "Feels like",
                index = "${Converter().roundingTemperature(weatherData.current.feels_like)}\u2103",
            )
        }

        Row(modifier = Modifier.weight(0.1f)) {
            WeatherCard(
                title = "Wind speed",
                index = "${weatherData.current.wind_speed} m/s"
            )
        }
    }

    Row {
        Row(modifier = Modifier.weight(0.1f)) {
            WeatherCard(
                title = "Humidity",
                index = "${weatherData.current.humidity}%",
            )
        }

        Row(modifier = Modifier.weight(0.1f)) {
            WeatherCard(
                title = "Visibility",
                index = "${weatherData.current.visibility/1000} km"
            )
        }
    }

}