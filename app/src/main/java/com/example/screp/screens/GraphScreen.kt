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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle

@Composable
fun GraphScreen(stepCountViewModel: StepCountViewModel, settings: Flow<Settings>) {
    val context = LocalContext.current

    val todayDate = CalendarUtil().getTodayDate()
    val startTimeToday = CalendarUtil().getCurrentDateStart(todayDate)

    val startTime = CalendarUtil().getCurrentDateStart(todayDate)
    val endTime = CalendarUtil().getCurrentDateEnd(todayDate)

    // get all step count record of all time
    val stepCounts = stepCountViewModel.getStepCounts(startTime, endTime).observeAsState(listOf())
    var totalStepCountInPeriod: Int = 0

    var listRecordsInPeriod: List<Int> = stepCounts.value.map { it -> it.total }
    Log.d("GRAPH_LOG", "list records in period ${listRecordsInPeriod.size.toString()}")
    if (listRecordsInPeriod.size > 0){
        totalStepCountInPeriod= listRecordsInPeriod.reduce { acc, i ->  acc + i}
    }



    val savedSettings = settings.collectAsState(initial = Settings())
    //get step goal:
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
    ){
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.padding(30.dp)
        ){
            CircularProgressBar(percentage = (totalStepCountInPeriod.toFloat()/stepGoal), number = stepGoal)
        }
        LazyColumn (){
            items(stepCounts.value) {
                Text("$it")
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
){
    var animationPlayed by remember{ mutableStateOf(false) }
    val currentPercentage = animateFloatAsState(
        targetValue = if(animationPlayed) percentage else 0f,
        animationSpec = tween(
            durationMillis = animDuration,
            delayMillis = animDelay
        )
    )
    // start animation only gets triggered for the first composition:
    LaunchedEffect(key1 = true){
        animationPlayed = true
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .size(radius * 2.1f)
            .clip(CircleShape)
            .background(Color.White)
    ){
        Canvas(modifier = Modifier.size(radius*2f)){
            drawArc(
                color = color,
                startAngle = -90f,
                sweepAngle = 360 * currentPercentage.value,
                useCenter = false,
                style = Stroke(width = strokeWidth.toPx(), cap = StrokeCap.Round),
            )
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally){
            Text (
                buildAnnotatedString {
                    append("Total \n")
                    withStyle(
                        style = SpanStyle(fontSize = 50.sp,
                        fontWeight = FontWeight.Bold))
                    {
                        append((currentPercentage.value * number).toInt().toString())
                    }
                },
                color = color,
                fontSize = fontSize)
            Text ("Goal: ${number}",
                color = color,
                fontSize = fontSize)
        }



    }
}



