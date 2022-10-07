package com.example.screp.screens


import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.*
import com.example.screp.R
import com.example.screp.data.Settings
import com.example.screp.helpers.CalendarUtil
import com.example.screp.sensorService.SensorDataManager
import com.example.screp.viewModels.StepCountViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import java.util.*

@Composable
fun GraphScreen(stepCountViewModel: StepCountViewModel, settings: Flow<Settings>) {
    val context = LocalContext.current

    val startTime = CalendarUtil().getCurrentDateStart("2020-04-30")
    val endTime = CalendarUtil().getCurrentDateEnd(null)

    val stepCounts = stepCountViewModel.getStepCounts(startTime, endTime).observeAsState(listOf())

    val savedSettings = settings.collectAsState(initial = Settings())
    //get step goal: val stepGoal = savedSettings.value.stepGoal

        LazyColumn {
        items(stepCounts.value) {
            Text("$it")
        }
    }
    }




