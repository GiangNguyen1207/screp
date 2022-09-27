package com.example.screp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import com.example.screp.helpers.CalendarUtil
import com.example.screp.ui.theme.ScrepTheme
import com.example.screp.views.exampleScreen.StepCountViewModel
import java.util.*

class MainActivity : ComponentActivity() {
    companion object {
        private lateinit var stepCountViewModel: StepCountViewModel
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        stepCountViewModel = StepCountViewModel(application)

        //insert hardcode data into db
//        stepCountViewModel.insert(
//            com.example.screp.data.StepCount(
//                0,
//                1664273400000,
//                1664276736059,
//                1000
//            )
//        )

        setContent {
            ScrepTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    StepCount(stepCountViewModel)
                }
            }
        }
    }
}

@Composable
fun StepCount(stepCountViewModel: StepCountViewModel) {
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