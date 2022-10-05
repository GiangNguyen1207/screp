package com.example.screp.screens

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.util.Log
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.Star
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.*
import com.example.screp.helpers.CalendarUtil
import com.example.screp.sensorService.SensorData
import com.example.screp.sensorService.SensorDataManager
import com.example.screp.viewModels.StepCountViewModel
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import java.util.*


@Composable
fun RecordStepCountScreen(stepCountViewModel: StepCountViewModel) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var data by remember {
        mutableStateOf<SensorData?>(null)
    }
    val dataManager = SensorDataManager(context)
//    DisposableEffect(Unit) {
//        val dataManager = SensorDataManager(context)
//        dataManager.init()
//
//        val job = scope.launch {
//            dataManager.data
//                .receiveAsFlow()
//                .onEach { data = it }
//                .collect()
//        }
//
//        onDispose {
//            dataManager.cancel()
//            job.cancel()
//        }
//    }

    var sensorStatusOn by remember {mutableStateOf(false)}

    val startTime = CalendarUtil().getCurrentDateStart("2020-04-30")
    val endTime = CalendarUtil().getCurrentDateEnd(null)
    println(Date(startTime))
    println(Date(endTime))

    val stepCounts = stepCountViewModel.getStepCounts(startTime, endTime).observeAsState(listOf())

    Button(
        onClick = {
            sensorStatusOn = !sensorStatusOn
            Log.d("SENSOR_LOG", "Clicked. Sensor is on ${sensorStatusOn}")

            if (sensorStatusOn){
                dataManager.init()
            } else {
                dataManager.cancel()
            }
        }
    ){
        Text(if (sensorStatusOn) "Stop" else "Start step count",
            fontSize = 30.sp)
//        Icon(Icons.Outlined.Star, contentDescription = "Start step count",
//        modifier = Modifier.size(50.dp))
    }
    LazyColumn {
        items(stepCounts.value) {
            Text("$it")
        }
    }
}


fun startSensor(sensorManager: SensorManager,
                stepCounterSensor: Sensor?,
                stepCountSensorEventListener: SensorEventListener)
{
    sensorManager.registerListener (
        stepCountSensorEventListener,
        stepCounterSensor,
        SensorManager.SENSOR_DELAY_NORMAL //specifying sensor manager as delay normal
    )
}


