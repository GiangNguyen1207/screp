package com.example.screp.screens.HomeScreen

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.example.screp.screens.GoogleMap
import com.example.screp.screens.RecordStepCountComponent
import com.example.screp.viewModels.PhotoAndMapViewModel
import com.example.screp.viewModels.StepCountViewModel
import com.google.android.gms.location.FusedLocationProviderClient

@Composable
fun MapViewScreen(navController: NavHostController,
                  photoAndMapViewModel: PhotoAndMapViewModel,
                  stepCountViewModel: StepCountViewModel,
                  fusedLocationProviderClient: FusedLocationProviderClient) {

    Column(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(fraction = 0.80f)
        ) {
            GoogleMap(navController,fusedLocationProviderClient, photoAndMapViewModel)
        }

        RecordStepCountComponent(stepCountViewModel)
    }
}