package com.example.screp.screens.settingsScreen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun SettingsCard(label: String, text: String) {
    Card(
        backgroundColor = MaterialTheme.colors.primaryVariant.copy(alpha = 0.15f),
        shape = RoundedCornerShape(15.dp),
        elevation = 0.dp,
        modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp)
    ) {
        Column(Modifier.padding(16.dp)) {
            Text(
                label,
                color = MaterialTheme.colors.primaryVariant,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )

            Text(
                text,
                color = MaterialTheme.colors.primary,
                fontSize = 36.sp,
                fontWeight = FontWeight.Bold
            )
        }

    }
}