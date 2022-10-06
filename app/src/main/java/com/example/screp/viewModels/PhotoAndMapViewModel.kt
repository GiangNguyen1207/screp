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
import androidx.core.content.ContextCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.screp.data.Photo
import com.example.screp.database.AppDatabase
import com.example.screp.repository.PhotoRepository
import com.google.android.gms.location.*
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.launch


class PhotoAndMapViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: PhotoRepository =
        PhotoRepository(AppDatabase.get(application).photoDao())
    private val context = getApplication<Application>().applicationContext


    fun getPhotos(): LiveData<List<Photo>> =
        repository.getPhotos()

    fun getPhotoByName(photoName: String): LiveData<Photo> =
        repository.getPhotoByName(photoName)

    fun getPhotoByCity(cityName: String): LiveData<List<Photo>> =
        repository.getPhotoByCity(cityName)

    fun insertPhoto(photo: Photo) {
        viewModelScope.launch {
            repository.insertPhoto(photo)
        }
    }

    fun getAddress(lat: Double, lng: Double): String {
        var address = ""
        viewModelScope.launch {
            val geocoder = Geocoder(context)
            if (Build.VERSION.SDK_INT >= 33) {
                geocoder.getFromLocation(lat, lng, 1)
            } else {
                address = geocoder.getFromLocation(lat, lng, 1)?.first()?.getAddressLine(0) ?: ""
            }

        }
        return address
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

    fun isLocationPermissionGranted(context: Context) : Boolean {
        return (ContextCompat.checkSelfPermission(
            context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
    }
}