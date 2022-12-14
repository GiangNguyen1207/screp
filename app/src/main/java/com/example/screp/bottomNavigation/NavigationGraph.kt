package com.example.screp.bottomNavigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.screp.services.bluetoothService.BluetoothServiceManager
import com.example.screp.data.Settings
import com.example.screp.data.Weather
import com.example.screp.screens.PhotosScreen
import com.example.screp.screens.weatherScreen.WeatherScreen
import com.example.screp.viewModels.StepCountViewModel
import com.example.screp.viewModels.WeatherViewModel
import com.example.screp.screens.*
import com.example.screp.screens.settingsScreen.SettingEditScreen
import com.example.screp.screens.settingsScreen.SettingScreen
import com.example.screp.services.SensorDataManager
import com.example.screp.viewModels.PhotoAndMapViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import kotlinx.coroutines.flow.Flow
import java.io.File

@RequiresApi(Build.VERSION_CODES.N)
@Composable
fun NavigationGraph(
    navController: NavHostController,
    stepCountViewModel: StepCountViewModel,
    weatherViewModel: WeatherViewModel,
    photoAndMapViewModel: PhotoAndMapViewModel,
    imgPath: File?,
    fusedLocationProviderClient: FusedLocationProviderClient,
    dataManager: SensorDataManager,
    preferenceDataStore: DataStore<Preferences>,
    settings: Flow<Settings>,
    STEP_GOAL: Preferences.Key<String>,
    NOTIFICATION_TIME: Preferences.Key<String>,
    bluetoothServiceManager: BluetoothServiceManager
) {
    NavHost(navController, startDestination = BottomNavItem.Record.screen_route) {
        composable(BottomNavItem.Record.screen_route) {
            MapViewScreen(
                navController = navController,
                photoAndMapViewModel = photoAndMapViewModel,
                stepCountViewModel = stepCountViewModel,
                fusedLocationProviderClient = fusedLocationProviderClient,
                dataManager = dataManager
            )
        }
        composable(BottomNavItem.Graph.screen_route) {
            GraphScreen(stepCountViewModel = stepCountViewModel, settings = settings)
        }
        composable(BottomNavItem.Weather.screen_route) {
            WeatherScreen(weatherViewModel)
        }
        composable(BottomNavItem.Photos.screen_route) {
            PhotosScreen(
                photoAndMapViewModel = photoAndMapViewModel,
                imgPath = imgPath,
                fusedLocationProviderClient = fusedLocationProviderClient,
                navController = navController,
                bluetoothServiceManager = bluetoothServiceManager
            )
        }
        composable(BottomNavItem.PhotoDetail.screen_route + "/{photoName}") {
            val photoName = it.arguments?.getString("photoName")
            PhotoDetailScreen(
                navController = navController,
                photoName = photoName,
                photoAndMapViewModel = photoAndMapViewModel,
                bluetoothServiceManager = bluetoothServiceManager,
                imgPath = imgPath
            )
        }
        composable(BottomNavItem.Settings.screen_route) {
            SettingScreen(
                navController = navController,
                settings = settings
            )
        }
        composable(BottomNavItem.SettingsEdit.screen_route) {
            SettingEditScreen(
                navController = navController,
                preferenceDataStore = preferenceDataStore,
                settings = settings,
                STEP_GOAL,
                NOTIFICATION_TIME
            )
        }
    }
}