package com.example.screp.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class StepCount(
    @PrimaryKey(autoGenerate = true)
    val uid: Long,
    var startTime: Long,
    var endTime: Long,
    val total: Int
) {
    override fun toString() = "Total steps are: $total"
}