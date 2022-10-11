package com.example.screp.bluetoothService

import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothClass
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.companion.BluetoothDeviceFilter
import android.companion.CompanionDeviceManager
import android.content.*
import android.content.Context.BLUETOOTH_SERVICE
import android.content.Context.COMPANION_DEVICE_SERVICE
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.*
import java.util.*
import java.util.regex.Pattern


class BluetoothServiceManager (context: Context, activity: Activity): ViewModel() {

    companion object Config{
        const val SCAN_PERIOD: Long = 5000
    }

    val scope = CoroutineScope(Dispatchers.Default)

    private val context = context
    private val activity = activity

    private lateinit var bluetoothManager: BluetoothManager
    lateinit var bluetoothAdapter: BluetoothAdapter

    val scanResultsFound = MutableLiveData<List<BluetoothDevice>>(null)
    val scanResultsPaired = MutableLiveData<List<BluetoothDevice>>(null)

    val fScanning = MutableLiveData<Boolean>(false)
    private val mResultsFound = HashMap<String, BluetoothDevice>()
    private val mResultsPaired = HashMap<String, BluetoothDevice>()

    lateinit var deviceManager: CompanionDeviceManager

    private lateinit var timerJob: Job

    // Initiate the service, called in Main activity onCreate
    fun init(){
        bluetoothManager = context.getSystemService(BLUETOOTH_SERVICE) as BluetoothManager
        bluetoothAdapter = bluetoothManager.adapter
        Log.d("BT_LOG", "BT Service: init")
        if (bluetoothAdapter == null) {
            Toast.makeText(context, "Device doesn't support Bluetooth", Toast.LENGTH_SHORT).show()
        }
    }


    // Create a BroadcastReceiver for ACTION_FOUND.
    val receiver = object : BroadcastReceiver(){
        override fun onReceive(context: Context, intent: Intent) {
            val action: String = intent.action.toString()
            when(action) {
                BluetoothDevice.ACTION_FOUND -> {
                    // Discovery has found a device. Get the BluetoothDevice
                    // object and its info from the Intent.
                    val device: BluetoothDevice? =
                        intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)
                        val deviceName = device?.name
                        val deviceHardwareAddress = device?.address // MAC address
                        val deviceTypeIsSmartPhone = device?.bluetoothClass?.deviceClass == BluetoothClass.Device.PHONE_SMART
                        Log.d("BT_LOG", "broadcast receiver: " + deviceName + " .Is smart phone :" + deviceTypeIsSmartPhone )

                    // Filter smart phone
                        if (deviceTypeIsSmartPhone){
                            mResultsFound[device!!.address] = device!!
                            scanResultsFound.postValue(mResultsFound.values.toList())
                        }
                }
            }

        }
    }


    fun pairDevices(context: Context = this.context, device: BluetoothDevice){
        deviceManager =  context.getSystemService(COMPANION_DEVICE_SERVICE) as CompanionDeviceManager
        val deviceFilter: BluetoothDeviceFilter = BluetoothDeviceFilter.Builder()
            .setNamePattern(Pattern.compile(device.name))
            .build()


        // Create pairing request
        try {
            device.createBond()
            scanPairedDevices(context)
        } catch (e: java.lang.Exception){
            e.printStackTrace()
        }

    }

    // Scan paired devices
    fun scanPairedDevices(context: Context) {
//        scanResultsPaired.postValue(listOf())
        viewModelScope.launch {
            fScanning.postValue(true)
            val startDiscovery = bluetoothAdapter.startDiscovery()
            Log.d("BT_LOG", "BT device discovery")
            if (!startDiscovery){
                Log.d("BT_LOG", "BT device discovery unsuccesful")
            }
            val pairedDevices = bluetoothAdapter.bondedDevices
//            Log.d("BT_LOG", "paired device: ${pairedDevices.size}")
            if (pairedDevices.size == 0 || pairedDevices == null){
                scanResultsPaired.postValue(listOf())
            }
            else {
                pairedDevices.forEach { it -> mResultsPaired[it.address] = it }
                Log.d("BT_LOG", "mResults: ${mResultsPaired.size}")
                Log.d("BT_LOG", "mResults: ${mResultsPaired.values}")

                scanResultsPaired.postValue(mResultsPaired.values.toList())
                Log.d("BT_LOG", "scan results: ${scanResultsPaired.value?.size}")
            }
            fScanning.postValue(false)
        }
    }

    fun startTimerJob(){
        timerJob = scope.launch {
            while (isActive){
                scanPairedDevices(context)
                delay(SCAN_PERIOD)
            }
        }
    }

    fun stopTimerJob(){
        timerJob.cancel()
    }
}
