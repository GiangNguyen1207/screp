package com.example.screp.services

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import com.example.screp.helpers.CalendarUtil

class NotificationManager(val context: Context, private val notificationTime: String) {
    @SuppressLint("UnspecifiedImmutableFlag")
    fun setScheduledNotification() {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, NotificationService::class.java)
        val pendingIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            PendingIntent.getBroadcast(context, 101, intent, PendingIntent.FLAG_MUTABLE)
        } else {
            PendingIntent.getBroadcast(context, 101, intent, PendingIntent.FLAG_ONE_SHOT)
        }

        alarmManager.setRepeating(
            AlarmManager.RTC_WAKEUP,
            CalendarUtil().getDateTime(notificationTime),
            AlarmManager.INTERVAL_DAY,
            pendingIntent
        )
    }
}