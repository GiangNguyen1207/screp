package com.example.screp.viewModels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.screp.data.Weather
import com.example.screp.repository.WeatherRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*

class WeatherViewModel() : ViewModel() {
    private val weatherRepository: WeatherRepository = WeatherRepository()
    val weatherData = MutableLiveData<Weather>()
    var currentLocation = MutableLiveData<String>()

    fun fetchWeatherData(latitude: Double, longitude: Double) {
        viewModelScope.launch(Dispatchers.IO) {
            val data = weatherRepository.fetchWeatherData(latitude, longitude)
            val geoCode = weatherRepository.fetchGeoCode(latitude, longitude)
            val locale = Locale("", geoCode[0].country)

            weatherData.postValue(data)
            currentLocation.postValue("${geoCode[0].name}, ${locale.displayCountry}")
        }
    }
}