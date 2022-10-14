package com.example.screp.helpers

import android.util.Log
import kotlin.math.ceil

class Converter {

    //convert Double to Integer temperature
    fun roundingTemperature(temp: Double): Int {
        return ceil(temp).toInt()
    }

    //convert tracking duration in miliseconds (Long) to second and minute format
    fun trackingTimeFormatter(duration: Long): String {
        val durationInSec = duration/1000
        val durationInMin = duration/1000/60
        if (durationInMin < 1) return "${durationInSec}s"
        else if (durationInMin >= 1 && durationInMin < 60) return  "${durationInSec/60}m ${durationInSec%60}s"
        else return "${durationInMin/60}h ${durationInMin%60}m"
    }

    fun convertDateFormat(dateGiven: Long?): String {
        var year = ""
        var month = ""
        var date = ""

        if (dateGiven != null) {
            year = dateGiven.toString().substring(0, 4)
            month = dateGiven.toString().substring(4, 6)
            date = dateGiven.toString().substring(6, 8)
        }

        return "$year.$month.$date"
    }
}