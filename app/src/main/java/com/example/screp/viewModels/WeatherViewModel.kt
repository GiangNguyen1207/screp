package com.example.screp.viewModels

import android.annotation.SuppressLint
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.screp.data.Weather
import com.example.screp.repository.WeatherRepository
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*

class WeatherViewModel(application: Application) : AndroidViewModel(application) {
    private val weatherRepository: WeatherRepository = WeatherRepository()
    val weatherData = MutableLiveData<Weather>()
    var currentLocation = MutableLiveData<String>()
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    @SuppressLint("MissingPermission")
    fun fetchWeatherData() {
        fusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(getApplication<Application>().applicationContext)
        val task = fusedLocationProviderClient.lastLocation

        task.addOnSuccessListener { location ->
            if (location != null) {
                viewModelScope.launch(Dispatchers.IO) {
                    val data = weatherRepository.fetchWeatherData(location.latitude, location.longitude)
                    val geoCode = weatherRepository.fetchGeoCode(location.latitude, location.longitude)
                    val locale = Locale("", geoCode[0].country)

                    weatherData.postValue(data)
                    currentLocation.postValue("${geoCode[0].name}, ${locale.displayCountry}")
                }
            }
        }

    }
}