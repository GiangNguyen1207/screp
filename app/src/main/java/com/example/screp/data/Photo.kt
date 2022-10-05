package com.example.screp.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Photo(
    @PrimaryKey(autoGenerate = true)
    val uid: Long,
    val photoName: String,
    val latitude: Double,
    val longitude: Double,
    val address: String,
    val cityName: String,
    val time: Long

)  {
    override fun toString() = "Address: $address"
}
