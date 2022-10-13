package com.example.screp.screens.settingsScreen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.DirectionsWalk
import androidx.compose.material.icons.rounded.Edit
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

    Column(Modifier.verticalScroll(rememberScrollState())) {
        SettingsHeader()
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 10.dp, end = 20.dp),
            horizontalArrangement = Arrangement.End
        ) {
            Button(
                colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colors.secondary),
                onClick = { navController.navigate(BottomNavItem.SettingsEdit.screen_route) },
            )
            {
                Text(
                    text = edit,
                    color = MaterialTheme.colors.background,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(end = 2.dp)
                )
                Icon(
                    Icons.Rounded.Edit,
                    tint = MaterialTheme.colors.background,
                    contentDescription = "edit settings",
                    modifier = Modifier.size(16.dp)
                )
            }
        }

        SettingsCard(label = steps, text = savedSettings.value.stepGoal)
        SettingsCard(label = notification, savedSettings.value.notificationTime)
    }
}