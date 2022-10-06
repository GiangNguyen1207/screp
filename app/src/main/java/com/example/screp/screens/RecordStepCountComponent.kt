package com.example.screp.screens


import androidx.compose.material.Icon
import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.*
import com.example.screp.R
import com.example.screp.helpers.CalendarUtil
import com.example.screp.sensorService.SensorData
import com.example.screp.sensorService.SensorDataManager
import com.example.screp.viewModels.StepCountViewModel
import kotlinx.coroutines.launch
import java.util.*

@Composable
fun RecordStepCountComponent(stepCountViewModel: StepCountViewModel) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var data by remember {
        mutableStateOf<SensorData?>(null)
    }
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

    val stepCounts = stepCountViewModel.getStepCounts(startTime, endTime).observeAsState(listOf())
    val dataManager = SensorDataManager(context)

    Column(modifier = Modifier.fillMaxSize().padding(10.dp)) {
        Button(
            onClick = {
                sensorStatusOn = !sensorStatusOn
                Log.d("SENSOR_LOG", "Record component: Clicked. Sensor is on ${sensorStatusOn}")

                if (sensorStatusOn){
                    scope.launch {
                        dataManager.init()
                    }
                } else {
                    dataManager.cancel()
//                Log.d("SENSOR_LOG", "Record component: sensor start time ${dataManager.startTime}")

                    dataManager.stepCountDTO?.let { stepCountViewModel.insert(it) }
                }
            }
        ){

            Icon(
                painterResource(R.drawable.ic_record),
                contentDescription = "Start step count",
                tint = if (sensorStatusOn) MaterialTheme.colors.onSecondary else MaterialTheme.colors.onPrimary,
                modifier = Modifier.size(50.dp).padding(10.dp)
            )
        }
        LazyColumn {
            items(stepCounts.value) {
                Text("$it")
            }
        }
    }

}


