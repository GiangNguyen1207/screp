package com.example.screp.repository

import androidx.lifecycle.LiveData
import com.example.screp.dao.StepCountDao
import com.example.screp.data.StepCount

class StepCountRepository(private val stepCountDao: StepCountDao) {
    fun getStepCounts(start: Long, end: Long): LiveData<List<StepCount>> =
        stepCountDao.getStepCounts(start, end);

    suspend fun insertStepCount(stepCount: StepCount) = stepCountDao.insert(stepCount)
}