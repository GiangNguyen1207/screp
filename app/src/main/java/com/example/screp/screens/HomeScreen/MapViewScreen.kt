package com.example.screp.screens

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.example.screp.services.SensorDataManager
import com.example.screp.viewModels.PhotoAndMapViewModel
import com.example.screp.viewModels.StepCountViewModel
import com.google.android.gms.location.FusedLocationProviderClient

@Composable
fun MapViewScreen(navController: NavHostController,
                  photoAndMapViewModel: PhotoAndMapViewModel,
                  stepCountViewModel: StepCountViewModel,
                  fusedLocationProviderClient: FusedLocationProviderClient,
                  dataManager: SensorDataManager
) {

    Column(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(fraction = 0.80f)
        ) {
            GoogleMap(navController,fusedLocationProviderClient, photoAndMapViewModel)
        }

        RecordStepCountComponent(stepCountViewModel, dataManager)
    }
}