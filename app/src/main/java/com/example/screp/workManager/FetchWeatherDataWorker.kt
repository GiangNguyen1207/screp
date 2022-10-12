package com.example.screp.workManager

import android.annotation.SuppressLint
import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.screp.R
import com.example.screp.data.HourlyWeather
import com.example.screp.helpers.CalendarUtil
import com.example.screp.repository.WeatherRepository
import com.example.screp.viewModels.WeatherViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices

class FetchWeatherDataWorker(
    private val context: Context, workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {
    private val channelId: String = "notificationService"
    private val channelName: String = "NotificationScrep"
    private val channelDescription: String = "Description"
    private val title = "Today Weather Condition"
    private var notificationMessage: String = ""

    private val thunderstormTimeGroup: MutableList<Long> = mutableListOf()
    private val drizzleTimeGroup: MutableList<Long> = mutableListOf()
    private val rainTimeGroup: MutableList<Long> = mutableListOf()
    private val snowTimeGroup: MutableList<Long> = mutableListOf()

    private val weatherRepository: WeatherRepository = WeatherRepository()
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private val weatherViewModel: WeatherViewModel = WeatherViewModel(context.applicationContext as Application)

    @SuppressLint("MissingPermission")
    override suspend fun doWork(): Result {
        var latitude = 0.0
        var longitude = 0.0

        return try {
            fusedLocationProviderClient =
                LocationServices.getFusedLocationProviderClient(context)
            val task = fusedLocationProviderClient.lastLocation

            task.addOnSuccessListener { location ->
                latitude = location.latitude
                longitude = location.longitude
            }

            val currentTime = CalendarUtil().getCurrentTime()
            val currentDateEnd = CalendarUtil().getCurrentDateEnd()
            val weatherData = weatherRepository.fetchWeatherData(latitude, longitude)
            val todayWeatherData =
                weatherData.hourly.filter { hourlyWeather -> hourlyWeather.dt in currentTime..currentDateEnd }

            sortWeatherCondition(todayWeatherData)
            notifyWeatherCondition()

            Result.success()
        } catch (throwable: Throwable) {
            Result.retry()
        }
    }

    private fun sortWeatherCondition(hourlyWeatherData: List<HourlyWeather>) {
        hourlyWeatherData.forEach { hourlyWeather ->
            when (hourlyWeather.weather[0].main) {
                "Thunderstorm" -> thunderstormTimeGroup.add(hourlyWeather.dt)
                "Drizzle" -> drizzleTimeGroup.add(hourlyWeather.dt)
                "Rain" -> rainTimeGroup.add(hourlyWeather.dt)
                "Snow" -> snowTimeGroup.add(hourlyWeather.dt)
                else -> {
                    print("no category found")
                }
            }
        }

        notificationMessage += if (thunderstormTimeGroup.size != 0) "Thunderstorm: ${
            getTimeString(
                thunderstormTimeGroup
            )
        }"
        else if (drizzleTimeGroup.size != 0) "Drizzle: ${getTimeString(drizzleTimeGroup)}"
        else if (snowTimeGroup.size != 0) "Snow: ${getTimeString(snowTimeGroup)}"
        else if (rainTimeGroup.size != 0) "Rain: ${getTimeString(rainTimeGroup)}"
        else "Today weather is good for going out!"
    }

    private fun getTimeString(weatherTimeGroup: List<Long>): String {
        val result = mutableListOf<String>()
        val calendar = CalendarUtil()
        var count = 1

        if (weatherTimeGroup.isEmpty()) return ""

        for (i in 1 until weatherTimeGroup.size + 1) {
            if (i == weatherTimeGroup.size || (weatherTimeGroup[i] - weatherTimeGroup[i - 1] != 3600L)) {
                if (count == 1) {
                    result.add(calendar.getTime(weatherTimeGroup[i - count] * 1000L))
                } else {
                    result.add(
                        calendar.getTime(weatherTimeGroup[i - count] * 1000L) + " - " + calendar.getTime(
                            weatherTimeGroup[i - 1] * 1000L
                        )
                    );
                }
                count = 1
            } else {
                count++
            }
        }

        return result.joinToString(", ")
    }

    private fun notifyWeatherCondition() {
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