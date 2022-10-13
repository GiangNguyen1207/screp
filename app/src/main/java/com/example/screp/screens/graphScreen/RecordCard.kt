package com.example.screp.screens.graphScreen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import com.example.screp.R


@Composable
fun RecordCard(time: String, stepCount: String) {
    Card(
        backgroundColor = MaterialTheme.colors.background.copy(alpha = 0.5f),
        shape = RoundedCornerShape(15.dp),
        elevation = 0.dp,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 10.dp)
    ) {
        Column(Modifier.padding(16.dp)) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top,
                modifier = Modifier.padding(5.dp).fillMaxWidth()
            ){
                Text(
                    stringResource(R.string.recordcard_title),
                    color = MaterialTheme.colors.primary,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    time,
                    color = MaterialTheme.colors.primary,
                    fontSize = 16.sp,
                )
            }

            Text(
                stepCount,
                color = MaterialTheme.colors.primary,
                fontSize = 40.sp,
                fontWeight = FontWeight.Bold
            )
        }

    }
}