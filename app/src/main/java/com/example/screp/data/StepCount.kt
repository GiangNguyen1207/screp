package com.example.screp.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.screp.helpers.CalendarUtil

@Entity
data class StepCount(
    @PrimaryKey(autoGenerate = true)
    val uid: Long,
    var startTime: Long,
    var endTime: Long,
    var total: Int,
    var trackingTimeInSeconds: Long = (endTime - startTime)/1000
) {
    override fun toString() = "Start time: ${CalendarUtil().convertLongToTime(time=startTime)}. " +
            "End time: ${CalendarUtil().convertLongToTime(time=endTime)}." +
            "Total tracking time: ${trackingTimeInSeconds} seconds" +
            " Total steps: $total"
}