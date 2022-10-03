package com.example.screp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.example.screp.bottomNavigation.BottomNavigation
import com.example.screp.bottomNavigation.NavigationGraph
import com.example.screp.ui.theme.ScrepTheme
import com.example.screp.viewModels.StepCountViewModel
import com.example.screp.viewModels.WeatherViewModel

class MainActivity : ComponentActivity() {
    companion object {
        private lateinit var stepCountViewModel: StepCountViewModel
        private lateinit var weatherViewModel: WeatherViewModel
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        stepCountViewModel = StepCountViewModel(application)
        weatherViewModel = WeatherViewModel()
        weatherViewModel.fetchWeatherData()

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
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    val navController = rememberNavController()
                    Scaffold(
                        bottomBar = { BottomNavigation(navController = navController) }
                    ) { innerPadding ->
                        Box(modifier = Modifier.padding(innerPadding)) {
                            NavigationGraph(
                                navController = navController,
                                stepCountViewModel = stepCountViewModel,
                                weatherViewModel
                            )
                        }
                    }
                }
            }
        }
    }
}