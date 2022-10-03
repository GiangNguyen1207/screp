package com.example.screp.screens.weatherScreen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter

@Composable
fun WeatherCard(
    title: String,
    index: String,
    image: String? = null,
    description: String? = null,
    isMain: Boolean? = false
) {
    Card(
        backgroundColor = MaterialTheme.colors.background.copy(alpha = 0.2f),
        shape = RoundedCornerShape(15.dp),
        elevation = 0.dp,
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Column(Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                title,
                color = MaterialTheme.colors.onPrimary,
                fontSize = if (isMain == true) 30.sp else 15.sp
            )
            Text(
                index, color = MaterialTheme.colors.onPrimary,
                fontSize = if (isMain == true) 60.sp else 25.sp
            )
            if (image != null) {
                Image(
                    painter = rememberAsyncImagePainter(image),
                    contentDescription = description,
                    modifier = Modifier.size(100.dp)
                )
            }
            if (description != null) {
                Text(description, color = MaterialTheme.colors.onPrimary, fontSize = 16.sp)
            }
        }
    }
}