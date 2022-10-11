package com.example.screp.workManager

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.screp.R
import com.example.screp.data.HourlyWeather
import com.example.screp.repository.WeatherRepository

class FetchWeatherDataWorker(
    private val context: Context,
    private val workerParams: WorkerParameters
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

    override suspend fun doWork(): Result {

        return try {
            val weatherData = weatherRepository.fetchWeatherData()
            sortWeatherCondition(weatherData.hourly)
            val rainTimeString = getTimeString(rainTimeGroup)
            notificationMessage += "Rain: $rainTimeString"
            notifyWeatherCondition()
            Result.success()
        } catch (throwable: Throwable) {
            Result.failure()
        }
    }

    private fun sortWeatherCondition(hourlyWeatherData: List<HourlyWeather>) {
        hourlyWeatherData.forEach { hourlyWeather ->
            //if (hourlyWeather.dt * 1000L in notificationTime until currentDateEnd) {
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
    }

    private fun getTimeString(weatherTimeGroup: List<Long>): String {
        val result = mutableListOf<String>()
        var count = 1

        if (weatherTimeGroup.isEmpty()) return ""

        for (i in 1 until weatherTimeGroup.size + 1) {

            if (i == weatherTimeGroup.size || (weatherTimeGroup[i] - weatherTimeGroup[i - 1] != 3600L)) {

                if (count == 1) {
                    result.add(weatherTimeGroup[i - count].toString())
                } else {
                    result.add(weatherTimeGroup[i - count].toString() + " - " + weatherTimeGroup[i - 1]);
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