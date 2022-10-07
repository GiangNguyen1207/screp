package com.example.screp.sensorService

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.runtime.rememberCoroutineScope
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.screp.data.StepCount
import com.example.screp.helpers.CalendarUtil
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import java.util.*

class SensorDataManager (context: Context): SensorEventListener {

    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private val _stepCountLiveData: MutableLiveData<Int> = MutableLiveData(0)
    var stepCountLiveData: LiveData<Int> = _stepCountLiveData

    var stepCount: Int = 0
    var startTime: Long = 0
    private var endTime: Long = 0

    var sessionTrackingTime = 0L
    val scope = CoroutineScope(Dispatchers.Default)

    // Step count Data object for a particular record session
    var stepCountDTO: StepCount? = null



    fun init (){
        Log.d("SENSOR_LOG", " sensorDataManager init")
        val stepSensor: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR)
        Log.d("SENSOR_LOG", "sensorDataManager stepCounterSensor: ${stepSensor}")
        if (stepSensor != null){
            sensorManager.registerListener(this, stepSensor, SensorManager.SENSOR_DELAY_UI )
        }
        this.startTime = CalendarUtil().getCurrentTime()
        stepCountDTO?.startTime = this.startTime


        //TODO: format to minutes 
        val timerJob = scope.launch {
            while (isActive){
               timerTask()
                delay(1000L)
                Log.d("SENSOR_LOG", "sensorDataManager: timer ${sessionTrackingTime}")

            }
        }

        Log.d("SENSOR_LOG", "sensorDataManager: current time: ${CalendarUtil().getCurrentTime()}")
        Log.d("SENSOR_LOG", "sensorDataManager: step count data start time onInit: ${this.startTime}")
    }

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {}

    override fun onSensorChanged(p0: SensorEvent?) {
        p0 ?: return
        if (p0?.sensor?.type == Sensor.TYPE_STEP_DETECTOR){
            this.stepCount++
            _stepCountLiveData.value = stepCount
            stepCountDTO?.total = stepCount
        }
        Log.d("SENSOR_LOG", " sensorDataManager: step counts data onsensorChanged: ${this.stepCount}")
        Log.d("SENSOR_LOG", " sensorDataManager: step counts LIVE data onsensorChanged: ${this.stepCountLiveData.value}")

    }

    fun cancel(){
        Log.d("SENSOR_LOG", "cancel")
        this.endTime = CalendarUtil().getCurrentTime()
        this.stepCount++
        Log.d("SENSOR_LOG", "sensorDataManager: step count startTime onCancel: ${this.startTime}")
        Log.d("SENSOR_LOG", "sensorDataManager: step count stepcount onCancel: ${this.stepCount}")
        Log.d("SENSOR_LOG", "sensorDataManager: step count endTime onCancel: ${this.endTime}")
        sensorManager.unregisterListener(this)
        stepCountDTO?.endTime = this.endTime

        stepCountDTO = StepCount(uid = 0, startTime = startTime, endTime = endTime, total = stepCount)
        Log.d("SENSOR_LOG", " sensorDataManager: step counts LIVE data on cancel: ${this._stepCountLiveData.value}")

        // reset step counter
        this.stepCount = 0

        Log.d("SENSOR_LOG", "sensorDataManager: step count data OBJECT  onCancel: ${stepCountDTO?.startTime}, ${stepCountDTO?.endTime}, ${stepCountDTO?.total}")
    }


    fun timerTask(){
        if (startTime != 0L){
            Log.d("SENSOR_LOG", "1s tick")
            sessionTrackingTime = (CalendarUtil().getCurrentTime() - startTime)/1000
        }
    }

}

