package com.example.screp.viewModels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.screp.data.Weather
import com.example.screp.repository.WeatherRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class WeatherViewModel : ViewModel() {
    private val weatherRepository: WeatherRepository = WeatherRepository()
    val weatherData = MutableLiveData<Weather>()

    fun fetchWeatherData() {
        viewModelScope.launch(Dispatchers.IO) {
            val data = weatherRepository.fetchWeatherData()
            weatherData.postValue(data)
        }
    }
}