package com.example.screp

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.screp.bottomNavigation.BottomNavigation
import com.example.screp.bottomNavigation.NavigationGraph
import com.example.screp.data.Settings
import com.example.screp.ui.theme.ScrepTheme
import com.example.screp.viewModels.PhotoAndMapViewModel
import com.example.screp.viewModels.StepCountViewModel
import com.example.screp.viewModels.WeatherViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class MainActivity : ComponentActivity() {
    companion object {
        private lateinit var stepCountViewModel: StepCountViewModel
        private lateinit var weatherViewModel: WeatherViewModel
        private lateinit var photoAndMapViewModel: PhotoAndMapViewModel
        private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")
        val STEP_GOAL = stringPreferencesKey("stepGoal")
        val NOTIFICATION_TIME = stringPreferencesKey("notificationTime")
    }

    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        hasPermissions()
        super.onCreate(savedInstanceState)
        stepCountViewModel = StepCountViewModel(application)
        weatherViewModel = WeatherViewModel()
        weatherViewModel.fetchWeatherData()
        photoAndMapViewModel = PhotoAndMapViewModel(application)
        val imgPath = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val context = this

        val settings: Flow<Settings> = context.dataStore.data.map { preferences ->
            Settings(
                stepGoal = preferences[STEP_GOAL] ?: "5000",
                notificationTime = preferences[NOTIFICATION_TIME] ?: "5:00"
            )
        }

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
                    val navBackStackEntry by navController.currentBackStackEntryAsState()

                    Scaffold(
                        bottomBar = {
                            if (navBackStackEntry?.destination?.route != "edit") {
                                BottomNavigation(navController = navController) }
                            }
                    ) { innerPadding ->
                        Box(modifier = Modifier.padding(innerPadding)) {
                            NavigationGraph(
                                navController = navController,
                                stepCountViewModel = stepCountViewModel,
                                weatherViewModel = weatherViewModel,
                                photoAndMapViewModel = photoAndMapViewModel,
                                imgPath = imgPath,
                                fusedLocationProviderClient = fusedLocationProviderClient,
                                preferenceDataStore = context.dataStore,
                                settings = settings,
                                STEP_GOAL = STEP_GOAL,
                                NOTIFICATION_TIME = NOTIFICATION_TIME
                            )
                        }
                    }
                }
            }
        }
    }
    @RequiresApi(Build.VERSION_CODES.M)
    private fun hasPermissions(): Boolean {
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.d("aaaaaa", "No gps access")
            requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1);
            return true // assuming that the user grants permission
        }else if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            Log.d("aaaaaa", "gps access")
        }
        return true
    }
}