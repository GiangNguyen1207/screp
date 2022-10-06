package com.example.screp.screens.settings

import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.screp.R
import com.example.screp.bottomNavigation.BottomNavItem

@Composable
fun SettingEditScreen(navController: NavHostController) {
    val back = stringResource(R.string.back)
    val save = stringResource(R.string.save)

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        SettingsHeader()

        Button(onClick = {
            navController.navigate(BottomNavItem.Settings.screen_route)
        }) {
            Text(text = back)
        }

        Button(onClick = {
            navController.navigate(BottomNavItem.Settings.screen_route)
        }) {
            Text(text = save)
        }
    }
}