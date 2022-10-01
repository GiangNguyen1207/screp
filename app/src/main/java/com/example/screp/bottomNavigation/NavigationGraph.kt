package com.example.screp.bottomNavigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.screp.screens.MapViewScreen
import com.example.screp.screens.PhotosScreen
import com.example.screp.screens.RecordStepCountScreen
import com.example.screp.screens.WeatherScreen
import com.example.screp.viewModels.StepCountViewModel

@Composable
fun NavigationGraph(navController: NavHostController, stepCountViewModel: StepCountViewModel) {
    NavHost(navController, startDestination = BottomNavItem.Record.screen_route) {
        composable(BottomNavItem.Record.screen_route) {
            MapViewScreen()
        }
        composable(BottomNavItem.Graph.screen_route) {
            RecordStepCountScreen(stepCountViewModel = stepCountViewModel )
        }
        composable(BottomNavItem.Weather.screen_route) {
            WeatherScreen()
        }
        composable(BottomNavItem.Photos.screen_route) {
            PhotosScreen()
        }
    }
}