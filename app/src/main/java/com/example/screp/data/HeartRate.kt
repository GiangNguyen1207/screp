package com.example.screp.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class HeartRate(
    @PrimaryKey(autoGenerate = true)
    val uid: Long,
    val startTime: Long,
    val endTime: Long,
    val rate: Int
) {
    override fun toString() = "Heart rate is: $rate"
}