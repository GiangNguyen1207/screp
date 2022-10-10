package com.example.screp.services

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.screp.data.StepCount
import com.example.screp.helpers.CalendarUtil
import kotlinx.coroutines.*

// Handle sensor data in thread
//    1)Create a service.
//    2)Have the service listen for sensor events.
//    3)In the service, create a thread.
//    4)In the thread, create a message loop
//    5)When you get sensor events, send them to the thread's message loop.
//    6)Have the thread wait for an incoming event.
//    7)When the service is stopped, cancel the thread.

class SensorDataManager (context: Context): SensorEventListener {

    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private val _stepCountLiveData: MutableLiveData<Int> = MutableLiveData(0)
    var stepCountLiveData: LiveData<Int> = _stepCountLiveData

    private val _trackingTime: MutableLiveData<Long> = MutableLiveData(0)
    var trackingTime: LiveData<Long> = _trackingTime

    var sensorStatusOn: Boolean = false
    var stepCount: Int = 0
    var startTime: Long = 0
    private var endTime: Long = 0

    private var sessionTrackingTime = 0L
    val scope = CoroutineScope(Dispatchers.Default)

    // Step count Data object for a particular record session
    var stepCountDTO: StepCount? = null

    private val mHandler: Handler = object :
        Handler(Looper.getMainLooper()){
        override fun handleMessage(msg: Message) {
            if (msg.what == 0){
                stepCount = msg.obj as Int
            }
        }
    }
    val mRunnable = Conn(mHandler, stepCountLiveData)
    var mThread = Thread(mRunnable)

    fun init (){
        Log.d("SENSOR_LOG", " sensorDataManager init")

        // register sensor listener
        val stepSensor: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR)
        Log.d("SENSOR_LOG", "sensorDataManager stepCounterSensor: ${stepSensor}")
        if (stepSensor != null){
            sensorManager.registerListener(this, stepSensor, SensorManager.SENSOR_DELAY_UI )
        }
        this.startTime = CalendarUtil().getCurrentTime()
        this.sensorStatusOn = true
        stepCountDTO?.startTime = this.startTime

        // start the thread to handle message loop
        mThread = Thread(mRunnable)
        mThread.start()

        // timer job to update session's tracking time
        val timerJob = scope.launch {
            while (isActive){
                timerTask()
                delay(1000L)
            }
        }
    }

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {}

    override fun onSensorChanged(p0: SensorEvent?) {
        p0 ?: return
        if (p0?.sensor?.type == Sensor.TYPE_STEP_DETECTOR){
            this.stepCount++
            _stepCountLiveData.value = stepCount
            stepCountDTO?.total = stepCount
            mRunnable.run()
        }
//        Log.d("SENSOR_LOG", " sensorDataManager: step counts data onsensorChanged: ${this.stepCount}")
//        Log.d("SENSOR_LOG", " sensorDataManager: step counts LIVE data onsensorChanged: ${this.stepCountLiveData.value}")

    }

    fun cancel(){
        Log.d("SENSOR_LOG", "sensor cancel")

        sensorManager.unregisterListener(this)
        this.endTime = CalendarUtil().getCurrentTime()
        this.stepCount++
        stepCountDTO?.endTime = this.endTime


        stepCountDTO = StepCount(uid = 0, startTime = startTime, endTime = endTime, total = stepCount)
//        Log.d("SENSOR_LOG", " sensorDataManager: step counts LIVE data on cancel: ${this._stepCountLiveData.value}")

        // reset step counter
        this.stepCount = 0
        _stepCountLiveData.value = stepCount
        _trackingTime.value = 0

        Log.d("SENSOR_LOG", "sensorDataManager: step count data OBJECT  onCancel: ${stepCountDTO?.startTime}, ${stepCountDTO?.endTime}, ${stepCountDTO?.total}")
        Log.d("SENSOR_LOG", "now interrupt thread")
        this.sensorStatusOn = false
        mThread.interrupt()

    }


    fun timerTask(){
        if (sensorStatusOn){
            sessionTrackingTime = (CalendarUtil().getCurrentTime() - startTime)
            _trackingTime.postValue(sessionTrackingTime)
        } else {
            sessionTrackingTime = 0
        }
    }
}


class Conn(
    mHand: Handler,
    val stepData: LiveData<Int>
): Runnable {
    private val mHandler = mHand

    override fun run(){
        try {
            val msg = mHandler.obtainMessage()
            msg.what = 0
            msg.obj = stepData.value
            mHandler.sendMessage(msg)
            Log.d("SENSOR_LOG", "sensorDataManager: runnable obj send message")
            Log.d("SENSOR_LOG", "sensorDataManager: runnable obj ${stepData.value}")

        }
        catch (e: Exception){
            Log.d("SENSOR_LOG", e?.message!!)
        }
    }
}