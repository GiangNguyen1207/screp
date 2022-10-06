package com.example.screp.sensorService

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.screp.data.StepCount
import com.example.screp.helpers.CalendarUtil
import kotlinx.coroutines.channels.Channel

class SensorDataManager (context: Context): SensorEventListener {

    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
//    private val _stepCount: MutableLiveData<Int> = MutableLiveData(0)
//    val stepCount: LiveData<Int> = _stepCount

    var stepCount: Int = 0
    private var startTime: Long = 3
    private var endTime: Long = 0

    // Step count Data object for a particular record session
    var stepCountDTO: StepCount? = null
//
//    fun updateValue(value: Int){
//        _stepCount.value = value
//    }

    fun init (){
        Log.d("SENSOR_LOG", " sensorDataManager init")
        val stepSensor: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR)
        Log.d("SENSOR_LOG", "sensorDataManager stepCounterSensor: ${stepSensor}")
        if (stepSensor != null){
            sensorManager.registerListener(this, stepSensor, SensorManager.SENSOR_DELAY_UI )
        }
        this.startTime = CalendarUtil().getCurrentTime()
        stepCountDTO?.startTime = this.startTime
        Log.d("SENSOR_LOG", "sensorDataManager: current time: ${CalendarUtil().getCurrentTime()}")

        Log.d("SENSOR_LOG", "sensorDataManager: step count data OBJECT onInit: ${this.startTime}")

    }

//    val data: Channel<SensorData> = Channel(Channel.UNLIMITED)

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {}

    override fun onSensorChanged(p0: SensorEvent?) {
        p0 ?: return
        if (p0?.sensor?.type == Sensor.TYPE_STEP_DETECTOR){
            this.stepCount++
            stepCountDTO?.total = stepCount
        }
        Log.d("SENSOR_LOG", " sensorDataManager: step counts data onsensorChanged: ${this.stepCount}")
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

        // reset step counter
        this.stepCount = 0

        Log.d("SENSOR_LOG", "sensorDataManager: step count data OBJECT  onCancel: ${stepCountDTO?.startTime}, ${stepCountDTO?.endTime}, ${stepCountDTO?.total}")


    }

}



data class SensorData(
    val stepSensorData: Int
    //TODO: add heart rate here if needed
)