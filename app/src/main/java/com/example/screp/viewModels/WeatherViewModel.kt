package com.example.screp.viewModels

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.*
import com.example.screp.data.Weather
import com.example.screp.repository.WeatherRepository
import com.example.screp.workManager.FetchWeatherDataWorker
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

class WeatherViewModel(application: Application) : ViewModel() {
    private val tag = "FETCH_WEATHER_DATA"
    private val weatherRepository: WeatherRepository = WeatherRepository()
    private val workManager = WorkManager.getInstance(application)
    internal val workInfos: LiveData<List<WorkInfo>> =
        workManager.getWorkInfosByTagLiveData(tag)
    val weatherData = MutableLiveData<Weather>()

    fun fetchWeatherData() {
        viewModelScope.launch(Dispatchers.IO) {
            val data = weatherRepository.fetchWeatherData()
            weatherData.postValue(data)
        }
    }
}