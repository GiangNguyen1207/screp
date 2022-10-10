package com.example.screp.services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.compose.runtime.State
import androidx.core.app.NotificationCompat
import com.example.screp.R
import com.example.screp.data.Weather
import com.example.screp.helpers.CalendarUtil

class NotificationService : BroadcastReceiver() {
    private val channelId: String = "notificationService"
    private val channelName: String = "NotificationScrep"
    private val channelDescription: String = "Description"
    private val title = "Today Weather Condition"
    private val currentDateEnd = CalendarUtil().getCurrentDateEnd()

    private val thunderstormTimeGroup: MutableList<Long> = mutableListOf()
    private val drizzleTimeGroup: MutableList<Long> = mutableListOf()
    private val rainTimeGroup: MutableList<Long> = mutableListOf()
    private val snowTimeGroup: MutableList<Long> = mutableListOf()
    private val clouds: MutableList<Long> = mutableListOf()

    private var notificationMessage: String = "Hello world"

    override fun onReceive(p0: Context?, p1: Intent?) {
        try {
            if (p0 != null) {
                notifyWeatherCondition(p0)
            }
        } catch (e: Exception) {
            Log.i("exception", e.message.toString())
        }
    }

    private fun createNotificationMessage() {
        //notificationMessage += "Thunderstorm: ${getTimeString(thunderstormTimeGroup)}\n"
        //notificationMessage += "Drizzle: ${getTimeString(drizzleTimeGroup)}\n"
        //notificationMessage += "Rain: ${getTimeString(rainTimeGroup)}\n"
        //notificationMessage += "Snow: ${getTimeString(snowTimeGroup)}\n"
    }

//    private fun sortWeatherCondition() {
//        weatherData.value?.hourly?.forEach { hourlyWeather ->
//            if (hourlyWeather.dt * 1000L in notificationTime until currentDateEnd) {
//                when (hourlyWeather.weather[0].main) {
//                    "Thunderstorm" -> thunderstormTimeGroup.add(hourlyWeather.dt)
//                    "Drizzle" -> drizzleTimeGroup.add(hourlyWeather.dt)
//                    "Rain" -> rainTimeGroup.add(hourlyWeather.dt)
//                    "Snow" -> snowTimeGroup.add(hourlyWeather.dt)
//                    "Clouds" -> clouds.add(hourlyWeather.dt)
//                    else -> {
//                        print("no category found")
//                    }
//                }
//            }
//        }
//    }

    private fun getTimeString(weatherTimeGroup: List<Long>): String {
        val result = mutableListOf<String>()
        val calendarUtil = CalendarUtil()
        var count = 1

        if (weatherTimeGroup.isEmpty()) return ""

        for (i in weatherTimeGroup.indices) {
            if (i == weatherTimeGroup.size || (weatherTimeGroup[i] - weatherTimeGroup[i - 1] != 3600L)) {
                if (count == 1) {
                    result.add(calendarUtil.getTime(weatherTimeGroup[i - count]))
                } else {
                    result.add(
                        calendarUtil.getTime(weatherTimeGroup[i - count]) + " - " + calendarUtil.getTime(
                            weatherTimeGroup[i - 1]
                        )
                    )
                }
                count = 1
            } else {
                count++
            }
        }

        println(result.joinToString(", "))
        return result.joinToString(", ")
    }

    private fun notifyWeatherCondition(context: Context) {
        //sortWeatherCondition()
        //createNotificationMessage()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                channelName,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = channelDescription
            }
            val notificationManager =
                context.applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            notificationManager.createNotificationChannel(channel)

            //Log.d("clouds", clouds.toString())
            //Log.d("clouds string", getTimeString(clouds))

            val notification = NotificationCompat.Builder(context, channelId)
                .setSmallIcon(R.drawable.ic_notifications)
                .setContentTitle(title)
                .setContentText(notificationMessage)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .build()

            notificationManager.notify(100, notification)
        }
    }
}