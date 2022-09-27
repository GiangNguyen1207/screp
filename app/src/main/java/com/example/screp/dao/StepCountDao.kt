package com.example.screp.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.screp.data.StepCount

@Dao
interface StepCountDao {
    @Query("SELECT * FROM stepcount WHERE startTime >= :start AND startTime <= :end")
    fun getStepCounts(start: Long, end: Long): LiveData<List<StepCount>>

    @Query("SELECT * FROM stepcount WHERE uid = :id")
    fun getStepCount(id: Long): LiveData<StepCount>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(stepCount: StepCount): Long
}