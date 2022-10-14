package com.example.screp.screens.settingsScreen

import android.app.TimePickerDialog
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.navigation.NavHostController
import com.example.screp.R
import com.example.screp.bottomNavigation.BottomNavItem
import com.example.screp.data.Settings
import com.example.screp.helpers.CalendarUtil
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

@Composable
fun SettingEditScreen(
    navController: NavHostController,
    preferenceDataStore: DataStore<Preferences>,
    settings: Flow<Settings>,
    STEP_GOAL: Preferences.Key<String>,
    NOTIFICATION_TIME: Preferences.Key<String>
) {
    val context = LocalContext.current
    val back = stringResource(R.string.back)
    val save = stringResource(R.string.save)
    val setStepGoal = stringResource(R.string.set_label_steps)
    val setNotificationTime = stringResource(R.string.set_label_notification)
    val hour = CalendarUtil().getCurrentHour()
    val minute = CalendarUtil().getCurrentMinute()
    val coroutineScope = rememberCoroutineScope()

    val savedSettings = settings.collectAsState(initial = Settings())
    var totalStepsGoal by remember { mutableStateOf("") }
    var notificationTime by remember { mutableStateOf("") }

    val timePickerDialog = TimePickerDialog(
        context,
        { _, hour: Int, minute: Int ->
            notificationTime = if (minute < 10) "$hour:0$minute"
            else "$hour:$minute"
        }, hour, minute, false
    )

    suspend fun saveToDataStore() {
        preferenceDataStore.edit { preferences ->
            preferences[STEP_GOAL] = totalStepsGoal.ifEmpty { savedSettings.value.stepGoal }
            preferences[NOTIFICATION_TIME] =
                notificationTime.ifEmpty { savedSettings.value.notificationTime }
        }
    }

    Column(
        modifier = Modifier.fillMaxWidth(),
    ) {
        SettingsHeader()

        Column(
            modifier = Modifier
                .padding(32.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = setStepGoal,
                    color = MaterialTheme.colors.primary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                OutlinedTextField(
                    value = totalStepsGoal,
                    placeholder = { Text(savedSettings.value.stepGoal) },
                    onValueChange = { totalStepsGoal = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )

                Text(
                    text = setNotificationTime,
                    color = MaterialTheme.colors.primary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                OutlinedTextField(
                    value = notificationTime,
                    placeholder = { Text(savedSettings.value.notificationTime) },
                    onValueChange = { notificationTime = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            timePickerDialog.show()
                        },
                    enabled = false
                )
            }

            Column {
                Button(colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colors.onPrimary),
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        navController.navigate(BottomNavItem.Settings.screen_route)
                    }) {
                    Text(text = back, color = MaterialTheme.colors.primary)
                }

                Button(
                    colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colors.onSecondary),
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        coroutineScope.launch {
                            saveToDataStore()
                        }
                        navController.navigate(BottomNavItem.Settings.screen_route)
                    }) {
                    Text(text = save, color = MaterialTheme.colors.onPrimary)
                }
            }
        }
    }
}