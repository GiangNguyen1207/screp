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
import com.example.screp.viewModels.StepCountViewModel
import java.util.*


@Composable
fun RecordStepCountScreen(stepCountViewModel: StepCountViewModel) {
    val context = LocalContext.current
    val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    val stepCounterSensor: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)

    var sensorStatusOn by remember {mutableStateOf(false)}

    // initiate sensor event listener
    val stepCountSensorEventListener = object : SensorEventListener {
        override fun onAccuracyChanged(p0: Sensor?, p1: Int) {
            Log.d("SENSOR_LOG", "onAccuracyChanged ${p0?.name}: $p1")
        }

        override fun onSensorChanged(p0: SensorEvent?) {
            p0 ?: return
            if (p0.sensor == stepCounterSensor){
                Log.d("SENSOR_LOG", "onSensorChanged ${p0?.values}:")
            }
        }
    }


    //val startTime = 1664236800000 //beginning of day (27.09)
    //val endTime = 1664323199059 //end of day (27.09)
    val startTime = CalendarUtil().getCurrentDateStart("2020-04-30")
    val endTime = CalendarUtil().getCurrentDateEnd(null)
    println(Date(startTime))
    println(Date(endTime))

    val stepCounts = stepCountViewModel.getStepCounts(startTime, endTime).observeAsState(listOf())
    Log.d("SENSOR_LOG", "stepCountSensor ${stepCounterSensor.toString()}")
    Log.d("SENSOR_LOG", "sensorEventListener ${stepCountSensorEventListener.toString()}")

    Log.d("SENSOR_LOG", "Sensor is on ${sensorStatusOn}")

    Button(
        onClick = {
            sensorStatusOn = !sensorStatusOn
            Log.d("SENSOR_LOG", "Clicked. Sensor is on ${sensorStatusOn}")

            if (sensorStatusOn){

                stepCounterSensor?.also {
                    sensorManager.registerListener (
                        stepCountSensorEventListener,
                        stepCounterSensor,
                        SensorManager.SENSOR_DELAY_NORMAL //specifying sensor manager as delay normal
                    )
                }
            } else {
                sensorManager.unregisterListener(stepCountSensorEventListener)
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


