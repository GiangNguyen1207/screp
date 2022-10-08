package com.example.screp.screens


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
import com.example.screp.sensorService.SensorDataManager
import com.example.screp.viewModels.StepCountViewModel
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.livedata.observeAsState


@Composable
fun RecordStepCountComponent(stepCountViewModel: StepCountViewModel, dataManager: SensorDataManager) {
//    val scope = rememberCoroutineScope()

    var sensorStatusOn by remember { mutableStateOf(false) }
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





