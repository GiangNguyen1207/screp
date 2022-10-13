package com.example.screp.screens

import android.util.Log
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.material.MaterialTheme
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.*
import com.example.screp.data.Settings
import com.example.screp.helpers.CalendarUtil
import com.example.screp.viewModels.StepCountViewModel
import kotlinx.coroutines.flow.Flow
import java.util.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import com.example.screp.screens.graphScreen.RecordCard
import com.example.screp.R
import com.example.screp.data.StepCount

@Composable
fun GraphScreen(stepCountViewModel: StepCountViewModel, settings: Flow<Settings>) {
    val context = LocalContext.current

    // Get start and end time, with start time depends on the current state of selected option
    val todayDate = CalendarUtil().getTodayDate()
    val lastWeekDate = CalendarUtil().getCalculatedDate(days = -7)

    var startTime by remember { mutableStateOf(CalendarUtil().getCurrentDateStart(todayDate)) }
    val endTime = CalendarUtil().getCurrentDateEnd(todayDate)

    // Init step count in period
    var totalStepCountInPeriod: Int = 0

    // init variable and state for the graph display option
    val options = listOf("Day", "Week")
    var selectedOption by remember { mutableStateOf("Day") }
    val onSelectionChange = { text: String -> selectedOption = text }

    // Fetch data from database based on selected period
    val stepCounts = stepCountViewModel.getStepCounts(startTime, endTime).observeAsState(listOf())

    // Parse step count data to Int for graph generation
    var listRecordsInPeriod: List<Int> = stepCounts.value.map { it -> it.total }
    if (listRecordsInPeriod.size > 0) {
        totalStepCountInPeriod = listRecordsInPeriod.reduce { acc, i -> acc + i }
    }

    //get daily step goal from user setting (default = 5000/day)
    val savedSettings = settings.collectAsState(initial = Settings())
    val stepGoal = savedSettings.value.stepGoal.toInt()

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceAround,
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colors.secondary,
                        MaterialTheme.colors.primaryVariant
                    )
                )
            )
    ) {
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .clip(RoundedCornerShape(50.dp))
                .background(Color.LightGray)
        ) {
            options.forEach { text ->
                Row(
                    modifier = Modifier
                        .weight(0.5f)
                ) {
                    Text(
                        text,
                        color = MaterialTheme.colors.primary,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                onSelectionChange(text)
                                if (selectedOption == "Week") {
                                    startTime = CalendarUtil().getCurrentDateStart(lastWeekDate)
                                }
                            }
                            .background(
                                if (text == selectedOption) {
                                    MaterialTheme.colors.background
                                } else {
                                    Color.LightGray
                                }
                            )
                            .padding(10.dp),
                    )
                }
            }
        }
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .padding(16.dp)
                .size(LocalConfiguration.current.screenHeightDp.dp / 3)
        ) {
            if (selectedOption == "Day") {
                CircularProgressBar(
                    percentage = (totalStepCountInPeriod.toFloat() / stepGoal),
                    number = stepGoal
                )
            } else if (selectedOption == "Week") {
                CircularProgressBar(
                    percentage = (totalStepCountInPeriod.toFloat() / (stepGoal * 7)),
                    number = stepGoal * 7
                )
            }
        }
        Text(
            stringResource(id = R.string.recordList_title),
            fontSize = 25.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colors.onPrimary,
        )
        LazyColumn() {
            items(stepCounts.value.sortedByDescending { it.startTime }) {
                val timeString = CalendarUtil().formatTimeForRecordCard(it.startTime, it.endTime)
                RecordCard(time = timeString, stepCount = it.total.toString())
            }
        }

    }
}


@Composable
fun CircularProgressBar(
    percentage: Float,
    number: Int,
    fontSize: TextUnit = 25.sp,
    radius: Dp = 125.dp,
    color: Color = MaterialTheme.colors.onSecondary,
    strokeWidth: Dp = 15.dp,
    animDuration: Int = 1000,
    animDelay: Int = 0
) {
    var animationPlayed by remember { mutableStateOf(false) }
    val currentPercentage = animateFloatAsState(
        targetValue = if (animationPlayed) percentage else 0f,
        animationSpec = tween(
            durationMillis = animDuration,
            delayMillis = animDelay
        )
    )
    // start animation only gets triggered for the first composition:
    LaunchedEffect(key1 = true) {
        animationPlayed = true
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .size(radius * 2.1f)
            .clip(CircleShape)
            .background(MaterialTheme.colors.onPrimary)
    ) {
        Canvas(modifier = Modifier.size(radius * 2f)) {
            drawArc(
                color = color,
                startAngle = -90f,
                sweepAngle = 360 * currentPercentage.value,
                useCenter = false,
                style = Stroke(width = strokeWidth.toPx(), cap = StrokeCap.Round),
            )
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                buildAnnotatedString {
                    append("Total\n")
                    withStyle(
                        style = SpanStyle(
                            fontSize = 50.sp,
                            fontWeight = FontWeight.Bold
                        )
                    )
                    {
                        append((currentPercentage.value * number).toInt().toString())
                    }
                },
                color = color,
                fontSize = fontSize,
                textAlign = TextAlign.Center
            )
            Text(
                "Goal: ${number}",
                color = MaterialTheme.colors.primary,
                fontSize = fontSize
            )
        }
    }
}
