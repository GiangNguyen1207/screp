package com.example.screp.views.exampleScreen

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.screp.data.StepCount
import com.example.screp.database.AppDatabase
import com.example.screp.repository.StepCountRepository
import kotlinx.coroutines.launch

class StepCountViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: StepCountRepository =
        StepCountRepository(AppDatabase.get(application).stepCountDao())

    fun getStepCounts(start: Long, end: Long): LiveData<List<StepCount>> =
        repository.getStepCounts(start, end)

    fun insert(stepCount: StepCount) {
        viewModelScope.launch {
            repository.insertStepCount(stepCount)
        }
    }
}