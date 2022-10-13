package com.example.screp.viewModels

import android.Manifest
import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.os.Build
import android.os.Looper
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.screp.data.Photo
import com.example.screp.data.Route
import com.example.screp.data.RouteNumber
import com.example.screp.database.AppDatabase
import com.example.screp.repository.PhotoRepository
import com.example.screp.repository.RouteRepository
import com.google.android.gms.location.*
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*


class PhotoAndMapViewModel(application: Application) : AndroidViewModel(application) {
    private val photoRepository: PhotoRepository =
        PhotoRepository(AppDatabase.get(application).photoDao())
    private val routeRepository: RouteRepository =
        RouteRepository(AppDatabase.get(application).RouteDao())
    private val context = getApplication<Application>().applicationContext

    var travelRouteLocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            locationResult ?: return
            //loop through all the locations in the track
            for (location in locationResult.locations) {
                val dataStore = RouteNumber(context)
                viewModelScope.launch(Dispatchers.Default){
                    dataStore.getRouteNumber.collect { value ->
                        if (value != null) {
                            val route = Route(
                                uid = 0,
                                routeId = value.toInt(),
                                latitude = location.latitude,
                                longitude = location.longitude
                            )
                            insertRoute(route)
                        }
                    }
                }
            }
        }
    }

    val travelRouteLocationRequest = LocationRequest
        .create()
        .setInterval(1000)
        .setPriority(Priority.PRIORITY_HIGH_ACCURACY)



    fun getRoute(): LiveData<List<Route>> =
        routeRepository.getRoute()

    fun getRouteLatAndLong(routeId: Int): LiveData<List<LatLng>> =
        routeRepository.getRouteLatAndLong(routeId)

    fun insertRoute(route: Route) {
        viewModelScope.launch(Dispatchers.Default) {
            routeRepository.insertRoute(route)
        }
    }

    fun getPhotos(): LiveData<List<Photo>> =
        photoRepository.getPhotos()

    fun getPhotoByName(photoName: String): LiveData<Photo> =
        photoRepository.getPhotoByName(photoName)

    fun getPhotoByCity(cityName: String): LiveData<List<Photo>> =
        photoRepository.getPhotoByCity(cityName)

    fun insertPhoto(photo: Photo) {
        viewModelScope.launch(Dispatchers.Default) {
            photoRepository.insertPhoto(photo)
        }
    }

    fun savePhoto(lat: Double, lng: Double, photoName: String) {

        viewModelScope.launch(Dispatchers.IO) {
            var address = ""
            val time: Long = SimpleDateFormat("yyyyMMdd").format(Date()).toLong()
            val geocoder = Geocoder(context)
            try {
                address = geocoder.getFromLocation(lat, lng, 1)?.first()?.getAddressLine(0) ?: ""
            } catch (e: Exception) {
                Log.i("aaaaaa", "Exception ${e}")
            }
            var cityName = "Unknown city"
            if (address != ""){
                cityName = address.split(",").toMutableList().get(1).split(" ").toMutableList()
                    .lastOrNull().toString()
            }
            val photo = cityName?.let {
                Photo(
                    uid = 0,
                    photoName = photoName,
                    latitude = lat,
                    longitude = lng,
                    address = address,
                    cityName = it,
                    time = time
                )
            }
            if (photo != null) {
                insertPhoto(photo)
            }
        }
    }

    fun getDefaultLocation(): Location {
        val location = Location(LocationManager.GPS_PROVIDER)
        val espoo = LatLng(52.20000076293945, 24.6559)
        location.latitude = espoo.latitude
        location.longitude = espoo.longitude
        return location
    }

    fun getPosition(location: Location): LatLng {
        return LatLng(
            location.latitude,
            location.longitude
        )
    }


    @SuppressLint("MissingPermission")
    fun requestLocationResultCallback(
        fusedLocationProviderClient: FusedLocationProviderClient,
        locationResultCallback: (LocationResult) -> Unit
    ) {

        val locationCallback = object : LocationCallback() {

            override fun onLocationResult(locationResult: LocationResult) {
                super.onLocationResult(locationResult)

                locationResultCallback(locationResult)
                fusedLocationProviderClient.removeLocationUpdates(this)
            }
        }

        val locationRequest = LocationRequest.create().apply {
            interval = 0
            fastestInterval = 0
            priority = Priority.PRIORITY_HIGH_ACCURACY
        }
        Looper.myLooper()?.let { looper ->
            fusedLocationProviderClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                looper
            )
        }
    }

    fun isLocationPermissionGranted(context: Context): Boolean {
        return (ContextCompat.checkSelfPermission(
            context, Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED)
    }
}