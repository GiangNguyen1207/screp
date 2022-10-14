package com.example.screp.screens.settingsScreen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.screp.R

@Composable
fun SettingsHeader() {
    val painter = painterResource(R.drawable.settings_header)
    val title = stringResource(R.string.settings)
    val settingsHeader = stringResource(R.string.settings_header)
    val settingsSubtitle = stringResource(R.string.settings_subtitle)

    Box(modifier = Modifier.fillMaxWidth()) {
        Image(
            painter = painter,
            contentDescription = settingsHeader,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxWidth()
        )
        Column(modifier = Modifier.padding(vertical = 50.dp, horizontal = 30.dp)) {
            Text(
                text = title,
                color = MaterialTheme.colors.onPrimary,
                fontSize = 40.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = settingsSubtitle,
                color = MaterialTheme.colors.onPrimary,
                fontSize = 16.sp,
                fontStyle = FontStyle.Italic,
                modifier = Modifier.padding(top = 16.dp)
            )
        }
    }

}