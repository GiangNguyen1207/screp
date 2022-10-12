package com.example.screp.screens

import android.Manifest
import android.app.Activity
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat.requestPermissions
import androidx.core.content.FileProvider
import androidx.navigation.NavHostController
import com.example.screp.R
import com.example.screp.services.bluetoothService.BluetoothServiceManager
import com.example.screp.bottomNavigation.BottomNavItem
import com.example.screp.data.Photo
import com.example.screp.viewModels.PhotoAndMapViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.BufferedInputStream
import java.io.File
import java.io.IOException


@Composable
fun PhotosScreen(
    navController: NavHostController,
    photoAndMapViewModel: PhotoAndMapViewModel,
    imgPath: File?,
    fusedLocationProviderClient: FusedLocationProviderClient,
    bluetoothServiceManager: BluetoothServiceManager
) {

    val coroutineScope: CoroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    //get photos from database
    val photos: State<List<Photo>>? = photoAndMapViewModel.getPhotos().observeAsState(listOf())
    var state by remember { mutableStateOf(true) }
    val cityNameList by remember { mutableStateOf(mutableSetOf<String>()) }

    photos?.value?.forEach {
        cityNameList.add(it.cityName)
    }
    if (photoAndMapViewModel.isLocationPermissionGranted(context)) {
        Log.i("aaaaaa", "location permission granted")
    } else {
        Log.i("aaaaaa", "location permission not granted")
    }

    //create photo uri
    val fileName = "photo"
    val imageFile = File.createTempFile(fileName, ".jpg", imgPath)
    val photoURI: Uri = FileProvider.getUriForFile(
        context,
        "com.example.screp.provider",
        imageFile
    )
    val currentPhotoPath = imageFile!!.absolutePath

    val result = remember { mutableStateOf<Bitmap?>(null) }
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) {
        if (it) {
            var photoName = ""
            result.value = BitmapFactory.decodeFile(currentPhotoPath)
            photoURI.toString().split("/").toMutableList().lastOrNull()
                ?.let {
                    photoName = it
                }
            //get the current location data and save the photo info to database
            photoAndMapViewModel.requestLocationResultCallback(fusedLocationProviderClient) { locationResult ->

                locationResult.lastLocation?.let { location ->
                    try {
                        photoAndMapViewModel.savePhoto(
                            location.latitude,
                            location.longitude,
                            photoName
                        )
                        Log.i("aaaaaa", "photo saved")
                        state = !state
                    } catch (e: IOException) {
                        Log.i("aaaaaa", "IOException ${e}")
                        Toast.makeText(context, "Photo not saved due to Geocoder service not available", Toast.LENGTH_LONG).show()
                    }
                }
            }
        } else
            Log.i("aaaaaa", "Picture not taken")
    }
    Log.i("aaaaaa", "state is ${state}")
    Column(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(fraction = 0.75f)
                .padding(20.dp)
        ) {
            Column() {
                Spacer(Modifier.height(20.0.dp))
                Text(text = "Photo gallery", fontSize = 30.sp)
                Spacer(Modifier.height(20.0.dp))
                Column(
                    modifier = Modifier
                        .verticalScroll(rememberScrollState())
                ) {

                    cityNameList.forEach { cityName ->
                        val photo: State<List<Photo>> =
                            photoAndMapViewModel.getPhotoByCity(cityName).observeAsState(listOf())
                        Text(text = cityName, fontSize = 20.sp)
                        Spacer(Modifier.height(20.0.dp))
                        LazyRow {
                            items(photo.value) {
                                val uri = "${imgPath}/" + it.photoName
                                Image(
                                    BitmapFactory.decodeStream(BufferedInputStream(File(uri).inputStream()))
                                        .asImageBitmap(),
                                    null,
                                    modifier = Modifier
                                        .rotate(90F)
                                        .clickable(
                                            enabled = true,
                                            onClickLabel = "Clickable image",
                                            onClick = {
                                                Log.i("aaaaaa", "${imgPath}")
                                                navController.navigate(BottomNavItem.PhotoDetail.screen_route + "/${it.photoName}")
                                            }
                                        )
                                        .size(100.dp)
                                )
                                Spacer(Modifier.width(5.0.dp))
                            }
                        }
                        Spacer(Modifier.height(20.0.dp))
                    }
                }
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = {
                if (photoAndMapViewModel.isLocationPermissionGranted(context)) {
                    coroutineScope.launch(Dispatchers.Default) {
                        launcher.launch(photoURI)
                    }
                } else {
                    Toast.makeText(
                        context,
                        "Photo no taken, because location permission was denied",
                        Toast.LENGTH_LONG
                    ).show()
                    requestPermissions(
                        context as Activity,
                        arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                        1
                    )
                }

            }) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_camera),
                    contentDescription = "",
                    modifier = Modifier.size(80.dp)
                )
            }

        }
    }
}