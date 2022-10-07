package com.example.screp.screens

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.screp.R
import com.example.screp.sensorService.SensorDataManager
import com.example.screp.viewModels.PhotoAndMapViewModel
import com.example.screp.viewModels.StepCountViewModel
import com.google.android.gms.location.FusedLocationProviderClient

@Composable
fun MapViewScreen(navController: NavHostController,
                  photoAndMapViewModel: PhotoAndMapViewModel,
                  stepCountViewModel: StepCountViewModel,
                  fusedLocationProviderClient: FusedLocationProviderClient) {

    val context = LocalContext.current
    val dataManager = SensorDataManager(context)

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