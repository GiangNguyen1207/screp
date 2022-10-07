package com.example.screp.screens

import android.Manifest
import android.app.Activity
import android.content.IntentSender
import android.content.pm.PackageManager
import android.location.Location
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityCompat.requestPermissions
import androidx.core.content.ContextCompat.checkSelfPermission
import androidx.navigation.NavHostController
import com.example.screp.bottomNavigation.BottomNavItem
import com.example.screp.data.Photo
import com.example.screp.viewModels.PhotoAndMapViewModel
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*

@Composable
fun GoogleMap(navController: NavHostController, fusedLocationProviderClient: FusedLocationProviderClient, photoAndMapViewModel: PhotoAndMapViewModel) {

    var currentLocation by remember { mutableStateOf(photoAndMapViewModel.getDefaultLocation()) }
    var context = LocalContext.current

    // ask user for location permission
    val requestLocationPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()) {
    }
    SideEffect {
        requestLocationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
    }
    //ask user to turn on location service
    val enableLocationSettingLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartIntentSenderForResult()
    ) { activityResult ->
        if (activityResult.resultCode == Activity.RESULT_OK)
            Log.i("aaaaaa", ("location service granted"))

        else {
            Log.i("aaaaaa", ("location service denied"))
        }
    }
    val locationRequest = LocationRequest.create().apply {
        priority = Priority.PRIORITY_HIGH_ACCURACY
    }
    val locationRequestBuilder = LocationSettingsRequest.Builder()
        .addLocationRequest(locationRequest)
    val locationSettingsResponseTask = LocationServices.getSettingsClient(context)
        .checkLocationSettings(locationRequestBuilder.build())

    locationSettingsResponseTask.addOnFailureListener { exception ->
        if (exception is ResolvableApiException){
            try {
                val intentSenderRequest =
                    IntentSenderRequest.Builder(exception.resolution).build()
                enableLocationSettingLauncher.launch(intentSenderRequest)

            } catch (sendEx: IntentSender.SendIntentException) {
                sendEx.printStackTrace()
            }
        }
    }

    // set map properties like map type and enable Location button
    var properties by remember {
        mutableStateOf(
            MapProperties(
                mapType = MapType.NORMAL,
                isMyLocationEnabled = false
            )
        )
    }

    val cameraPositionState = rememberCameraPositionState()

    //set currentLocation as google map cameraPosition
    cameraPositionState.position = CameraPosition.fromLatLngZoom(
        photoAndMapViewModel.getPosition(currentLocation), 12f
    )

    var trackingButtonClickingState by remember { mutableStateOf(true) }

    //request location if tracking button clicked
    if (trackingButtonClickingState) {
        if (ActivityCompat.checkSelfPermission(
                LocalContext.current,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            properties = MapProperties(mapType = MapType.NORMAL, isMyLocationEnabled = true)
        }
        trackingButtonClickingState = false
        photoAndMapViewModel.requestLocationResultCallback(fusedLocationProviderClient) { locationResult ->

            locationResult.lastLocation?.let { location ->
                currentLocation = location
            }
        }
    }
    //add google map composable
    MyGoogleMap(
        navController,
        properties,
        photoAndMapViewModel,
        currentLocation,
        cameraPositionState,
        trackingButtonClick = { trackingButtonClickingState = true }
    )
}

@Composable
private fun MyGoogleMap(
    navController: NavHostController,
    properties: MapProperties,
    photoAndMapViewModel: PhotoAndMapViewModel,
    currentLocation: Location,
    cameraPositionState: CameraPositionState,
    trackingButtonClick: () -> Unit
) {
    // enable zoom button on the map
    val mapUiSettings by remember {
        mutableStateOf(
            MapUiSettings(zoomControlsEnabled = true)
        )
    }
    var photos : State<List<Photo>>? = photoAndMapViewModel.getPhotos().observeAsState(listOf())
    val context = LocalContext.current


    GoogleMap(
        modifier = Modifier.fillMaxSize(),
        cameraPositionState = cameraPositionState,
        uiSettings = mapUiSettings,
        properties = properties
    ) {
        //add marker to every photo location
        photos?.value?.forEach{
            val photoName = it.photoName
            Log.i("aaaaaa","map photo name: ${photoName}")
            Marker(
                onClick = {
                    false
                },
                state = MarkerState(position = LatLng(it.latitude,it.longitude)),
                title = "Photo taken at: ${it.time} ",
                onInfoWindowClick = {
//                    navController.navigate(BottomNavItem.PhotoDetail.screen_route + "/${photoName}")
                },
                snippet = "${it.address} "
            )
        }
    }

    //add tracking button
    TrackingButton(onTrackingButtonClick = trackingButtonClick)
}

@Composable
private fun TrackingButton(onTrackingButtonClick: () -> Unit) {

    Row(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp),
        horizontalArrangement = Arrangement.End


    ) {
        Button(onClick = onTrackingButtonClick) {
            Text(text = "Tracking")
        }
    }
}
