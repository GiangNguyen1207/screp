package com.example.screp.sensorService

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.example.screp.data.StepCount
import com.example.screp.helpers.CalendarUtil
import com.example.screp.viewModels.StepCountViewModel
import kotlinx.coroutines.channels.Channel
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter.ISO_LOCAL_DATE

class SensorDataManager (context: Context): SensorEventListener {

    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private var stepCount: Int = 0
    private var stepCountDTO: StepCount? = null
    private lateinit var stepCountViewModel: StepCountViewModel

    @RequiresApi(Build.VERSION_CODES.O)
    fun init (){
        Log.d("SensorDataManager", "init")
        val stepSensor: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR)
        stepCount = 0
        Log.d("SensorDataManager", "stepCounterSensor: ${stepSensor}")
        if (stepSensor != null){
            sensorManager.registerListener(this, stepSensor, SensorManager.SENSOR_DELAY_UI )
        }
        stepCountDTO?.startTime = CalendarUtil().getCurrentDateStart(LocalDateTime.now().format(ISO_LOCAL_DATE))

    }

    val data: Channel<SensorDataDTO> = Channel(Channel.UNLIMITED)

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {}

    override fun onSensorChanged(p0: SensorEvent?) {
        p0 ?: return
        if (p0?.sensor?.type == Sensor.TYPE_STEP_DETECTOR){
            stepCount++
        }
        Log.d("SensorDataManager", "step counts data onsensorChanged: ${stepCount}")
    }

    fun cancel(){
        Log.d("SensorDataManager", "cancel")
        sensorManager.unregisterListener(this)
        stepCountDTO?.endTime = CalendarUtil().getCurrentDateStart(LocalDateTime.now().format(ISO_LOCAL_DATE))
        stepCount = 0
        Log.d("SensorDataManager", "step counts data onCancel: ${stepCount}")

    }

}

data class SensorDataDTO(
    val stepSensorData: Int
    //TODO: add heart rate here if needed
)