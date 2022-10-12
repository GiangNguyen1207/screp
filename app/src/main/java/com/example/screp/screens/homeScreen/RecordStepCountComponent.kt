package com.example.screp.screens


import android.annotation.SuppressLint
import android.os.Looper
import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.*
import com.example.screp.R
import com.example.screp.services.SensorDataManager
import com.example.screp.viewModels.StepCountViewModel
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.platform.LocalContext
import com.example.screp.data.RouteNumber
import com.example.screp.helpers.Converter
import com.example.screp.viewModels.PhotoAndMapViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import kotlinx.coroutines.launch


@SuppressLint("MissingPermission")
@Composable
fun RecordStepCountComponent(
    stepCountViewModel: StepCountViewModel,
    dataManager: SensorDataManager,
    fusedLocationProviderClient: FusedLocationProviderClient,
    photoAndMapViewModel: PhotoAndMapViewModel,
) {
//    val scope = rememberCoroutineScope()

    var sessionStepCount: Int by remember { mutableStateOf(0) }
    var sensorStatusOn: Boolean by remember {
        mutableStateOf(false)
    }
    var stepCount = dataManager.stepCountLiveData.observeAsState(0)
    Log.d("SENSOR_LOG", "Record component: onStart. Sensor is on ${dataManager.sensorStatusOn}")
    var trackingTime = dataManager.trackingTime.observeAsState(0)

    val scope = rememberCoroutineScope()
    val dataStore = RouteNumber(LocalContext.current)
    val savedNumber = dataStore.getRouteNumber.collectAsState(initial = "0")
    var newNumber = savedNumber.value?.toInt()?.plus(1).toString()

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = "Time: ${Converter().trackingTimeFormatter(trackingTime.value)}")

        Button(
            onClick = {
                sensorStatusOn = dataManager.sensorStatusOn
                Log.d(
                    "SENSOR_LOG",
                    "Record component: Clicked. Sensor is on ${dataManager.sensorStatusOn}"
                )
                if (!sensorStatusOn) {
                    dataManager.init()
                    sensorStatusOn = dataManager.sensorStatusOn
                    scope.launch {
                        dataStore.saveRouteNumber(newNumber)
                    }
                    fusedLocationProviderClient.requestLocationUpdates(
                        photoAndMapViewModel.travelRouteLocationRequest,
                        photoAndMapViewModel.travelRouteLocationCallback,
                        Looper.getMainLooper()
                    )

                } else {
                    dataManager.cancel()
                    sensorStatusOn = dataManager.sensorStatusOn

                    Log.d(
                        "SENSOR_LOG",
                        "Record component: Clicked. Sensor is on ${dataManager.sensorStatusOn}"
                    )

                    dataManager.stepCountDTO?.let { stepCountViewModel.insert(it) }
                    Log.d("SENSOR_LOG", "Record component: session step count ${sessionStepCount}")
                    Log.d(
                        "SENSOR_LOG",
                        "Record component: session step count live data ${dataManager.stepCountLiveData.value}"
                    )
                    fusedLocationProviderClient.removeLocationUpdates(photoAndMapViewModel.travelRouteLocationCallback)
                }
            },
            colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colors.primary),
            shape = CircleShape,
            modifier = Modifier.size(80.dp)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_walk),
                tint = if (sensorStatusOn) MaterialTheme.colors.onSecondary else MaterialTheme.colors.onPrimary,
                contentDescription = "",
                modifier = Modifier.fillMaxSize()
            )
        }

        Text(text = "Step: ${stepCount.value}")
    }
}