package com.example.screp.screens


import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.*
import com.example.screp.R
import com.example.screp.helpers.CalendarUtil
import com.example.screp.sensorService.SensorDataManager
import com.example.screp.viewModels.StepCountViewModel
import kotlinx.coroutines.launch
import java.util.*
import kotlin.concurrent.schedule
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.livedata.observeAsState

@Composable
fun RecordStepCountComponent(stepCountViewModel: StepCountViewModel, dataManager: SensorDataManager) {
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

    var stepCount = dataManager.stepCountLiveData.observeAsState(0)


    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = "Time: ${dataManager.sessionTrackingTime} s")

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





