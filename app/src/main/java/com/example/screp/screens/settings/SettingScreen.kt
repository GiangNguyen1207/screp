package com.example.screp.screens.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.screp.R
import com.example.screp.bottomNavigation.BottomNavItem
import com.example.screp.data.Settings
import kotlinx.coroutines.flow.Flow

@Composable
fun SettingScreen(navController: NavHostController, settings: Flow<Settings>) {
    val edit = stringResource(R.string.edit)
    val steps = stringResource(R.string.label_steps)
    val notification = stringResource(R.string.label_notification)
    val savedSettings = settings.collectAsState(initial = Settings())

    Column {
        SettingsHeader()
        Text(
            text = edit,
            color = MaterialTheme.colors.primary,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(top = 16.dp, start = 36.dp).clickable {
                navController.navigate(BottomNavItem.SettingsEdit.screen_route )
            }
        )
        SettingsCard(label = steps, text = savedSettings.value.stepGoal)
        SettingsCard(label = notification, savedSettings.value.notificationTime)
    }
}