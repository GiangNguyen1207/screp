package com.example.screp.sensorService

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.util.Log
import kotlinx.coroutines.channels.Channel

class SensorDataManager (context: Context): SensorEventListener {

    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager

    fun init (){
        Log.d("SensorDataManager", "init")
        val stepCounter: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)

        Log.d("SensorDataManager", "stepCounterSensor: ${stepCounter}")
        if (stepCounter != null){
            sensorManager.registerListener(this, stepCounter, SensorManager.SENSOR_DELAY_UI )
        }
    }

    private var stepCounts: FloatArray? = null

    val data: Channel<SensorData> = Channel(Channel.UNLIMITED)

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {}

    override fun onSensorChanged(p0: SensorEvent?) {
        if (p0?.sensor?.type == Sensor.TYPE_STEP_COUNTER){
            stepCounts = p0.values
        }
        Log.d("SensorDataManager", "step counts data: ${stepCounts?.get(0)}")
    }

    fun cancel(){
        Log.d("SensorDataManager", "cancel")
        stepCounts = FloatArray(1)
        sensorManager.unregisterListener(this)

    }

}

data class SensorData(
    val step: Int
    //TODO: add heart rate here if needed
)