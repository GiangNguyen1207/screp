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
    var total: Int
) {
    override fun toString() = "Start time: ${CalendarUtil().convertLongToTime(startTime)}. End time: ${CalendarUtil().convertLongToTime(endTime)}. Total steps are: $total"
}