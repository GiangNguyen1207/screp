package com.example.screp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.ui.Modifier
import com.example.screp.screens.RecordStepCountScreen
import com.example.screp.ui.theme.ScrepTheme
import com.example.screp.viewModels.StepCountViewModel
import com.example.screp.views.exampleScreen.Greeting

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
                    Column {
                        Greeting("Screp")
                        RecordStepCountScreen(stepCountViewModel)
                    }
                }
            }
        }
    }
}