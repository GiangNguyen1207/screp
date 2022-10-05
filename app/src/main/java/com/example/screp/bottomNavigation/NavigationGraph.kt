package com.example.screp.bottomNavigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.screp.screens.*
import com.example.screp.viewModels.PhotoAndMapViewModel
import com.example.screp.viewModels.StepCountViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import java.io.File

@RequiresApi(Build.VERSION_CODES.N)
@Composable
fun NavigationGraph(
    navController: NavHostController,
    stepCountViewModel: StepCountViewModel,
    photoAndMapViewModel: PhotoAndMapViewModel,
    imgPath: File?,
    fusedLocationProviderClient: FusedLocationProviderClient
) {
    NavHost(navController, startDestination = BottomNavItem.Record.screen_route) {
        composable(BottomNavItem.Record.screen_route) {
            MapViewScreen(
                navController = navController,
                photoAndMapViewModel = photoAndMapViewModel,
                fusedLocationProviderClient = fusedLocationProviderClient
            )
        }
        composable(BottomNavItem.Graph.screen_route) {
            RecordStepCountScreen(
                stepCountViewModel = stepCountViewModel
            )
        }
        composable(BottomNavItem.Weather.screen_route) {
            WeatherScreen()
        }
        composable(BottomNavItem.Photos.screen_route) {
            PhotosScreen(
                photoAndMapViewModel = photoAndMapViewModel,
                imgPath = imgPath,
                fusedLocationProviderClient = fusedLocationProviderClient,
                navController = navController
            )
        }
        composable(BottomNavItem.PhotoDetail.screen_route + "/{photoName}") {
            val photoName = it.arguments?.getString("photoName")
            PhotoDetailScreen(
                navController = navController,
                photoName = photoName,
                photoAndMapViewModel = photoAndMapViewModel
            )
        }
        composable(BottomNavItem.Setting.screen_route) {
            SettingScreen(
                navController = navController
            )
        }
        composable(BottomNavItem.SettingEdit.screen_route) {
            SettingEditScreen(
                navController = navController
            )
        }
    }
}