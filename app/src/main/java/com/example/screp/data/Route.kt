package com.example.screp.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Route(
    @PrimaryKey(autoGenerate = true)
    val uid: Long,
    val routeId: Int,
    val latitude: Double,
    val longitude: Double
)
