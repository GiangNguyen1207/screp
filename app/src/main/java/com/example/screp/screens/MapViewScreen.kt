package com.example.screp.screens

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.screp.R
import com.example.screp.viewModels.PhotoAndMapViewModel
import com.google.android.gms.location.FusedLocationProviderClient

@Composable
fun MapViewScreen(navController: NavHostController, photoAndMapViewModel: PhotoAndMapViewModel, fusedLocationProviderClient: FusedLocationProviderClient) {

    Column(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(fraction = 0.80f)
        ) {
            GoogleMap(navController,fusedLocationProviderClient, photoAndMapViewModel)
        }

        Row(
            modifier = Modifier.fillMaxWidth().padding(10.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "Time: 0s")

            IconButton(onClick = {
                Log.i("aaaaaa", "StepCount button clicked")
            }) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_step_count),
                    contentDescription = "",
                    modifier = Modifier.size(80.dp)
                )
            }

            Text(text = "Step: 0")
        }
    }
}