package com.example.screp.screens


import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.*
import com.example.screp.R
import com.example.screp.helpers.CalendarUtil
import com.example.screp.services.SensorDataManager
import com.example.screp.viewModels.StepCountViewModel
import java.util.*
import kotlin.concurrent.schedule

@Composable
fun RecordStepCountComponent(stepCountViewModel: StepCountViewModel) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

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

    var sensorStatusOn by remember { mutableStateOf(false) }
    var trackingTime: Long by remember {
        mutableStateOf(0)
    }
    var sessionStepCount: Int by remember { mutableStateOf(0) }


    val dataManager = SensorDataManager(context)

    val stepCount = dataManager.stepCountLiveData.observeAsState()

    val timer = Timer("schedule", true)
    timer.schedule(1000){
        if (dataManager.startTime != 0L){
            Log.d("SENSOR_LOG", "1s tick")
            trackingTime = (CalendarUtil().getCurrentTime() - dataManager.startTime)/1000/60
//            sessionStepCount = dataManager.stepCount
        }
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        //TODO: Check calculate time
        Text(text = "Time: ${trackingTime} minute")

        Button(
            onClick = {
                sensorStatusOn = !sensorStatusOn
                Log.d("SENSOR_LOG", "Record component: Clicked. Sensor is on ${sensorStatusOn}")
                if (sensorStatusOn) {
                    dataManager.init()
                } else {
                    dataManager.cancel()
                    dataManager.stepCountDTO?.let { stepCountViewModel.insert(it) }
                    Log.d("SENSOR_LOG", "Record component: session step count ${sessionStepCount}")
                    Log.d("SENSOR_LOG", "Record component: session step count live data ${dataManager.stepCountLiveData.value}")
                }
            }
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_step_count),
                tint = if (sensorStatusOn) MaterialTheme.colors.onSecondary else MaterialTheme.colors.onPrimary,
                contentDescription = "",
                modifier = Modifier.size(80.dp)
            )
        }

        Text(text = "Step: ${stepCount.value}")
    }
}





