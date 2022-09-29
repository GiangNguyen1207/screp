package com.example.screp.screens

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import com.example.screp.helpers.CalendarUtil
import com.example.screp.viewModels.StepCountViewModel
import java.util.*

@Composable
fun RecordStepCountScreen(stepCountViewModel: StepCountViewModel) {
    //val startTime = 1664236800000 //beginning of day (27.09)
    //val endTime = 1664323199059 //end of day (27.09)
    val startTime = CalendarUtil().getCurrentDateStart("2020-04-30")
    val endTime = CalendarUtil().getCurrentDateEnd(null)
    println(Date(startTime))
    println(Date(endTime))

    val stepCounts = stepCountViewModel.getStepCounts(startTime, endTime).observeAsState(listOf())

    LazyColumn {
        items(stepCounts.value) {
            Text("$it")
        }
    }
}