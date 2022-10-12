package com.example.screp.services.bluetoothService

import android.app.Activity
import android.bluetooth.*
import android.companion.BluetoothDeviceFilter
import android.companion.CompanionDeviceManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Context.BLUETOOTH_SERVICE
import android.content.Context.COMPANION_DEVICE_SERVICE
import android.content.Intent
import android.graphics.Bitmap
import android.util.Log
import android.widget.Toast
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.*
import java.io.ByteArrayOutputStream
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
    lateinit var pairedDevice: BluetoothDevice

    var sendReceive: SendReceive? = null
    lateinit var server: BluetoothServer

    private lateinit var timerJob: Job

    // Initiate the service, called in Main activity onCreate
    fun init(){
        bluetoothManager = context.getSystemService(BLUETOOTH_SERVICE) as BluetoothManager
        bluetoothAdapter = bluetoothManager.adapter
        Log.d("BT_LOG", "BT Service: init")
        if (bluetoothAdapter == null) {
            Toast.makeText(context, "Device doesn't support Bluetooth", Toast.LENGTH_SHORT).show()
        }

        bluetoothAdapter.startDiscovery()
        server = BluetoothServer(activity, bluetoothAdapter)
        server.start()

    }


    // Create a BroadcastReceiver for ACTION_FOUND to find devices
    val receiver = object : BroadcastReceiver(){
        override fun onReceive(context: Context, intent: Intent) {
            val action: String = intent.action.toString()
            when(action) {
                BluetoothDevice.ACTION_FOUND -> {
                    val device: BluetoothDevice? =
                        intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)
                        val deviceTypeIsSmartPhone = device?.bluetoothClass?.deviceClass == BluetoothClass.Device.PHONE_SMART

                    // Filter smart phone
                        if (deviceTypeIsSmartPhone){
                            mResultsFound[device!!.address] = device!!
                            scanResultsFound.postValue(mResultsFound.values.toList())
                        }
                }
            }

        }
    }

    // Pairing devices
    fun pairDevices(context: Context = this.context, device: BluetoothDevice){
        deviceManager =  context.getSystemService(COMPANION_DEVICE_SERVICE) as CompanionDeviceManager
        val deviceFilter: BluetoothDeviceFilter = BluetoothDeviceFilter.Builder()
            .setNamePattern(Pattern.compile(device.name))
            .build()

        // Create pairing request
        try {
            device.createBond()
            pairedDevice = device
            scanPairedDevices(context)
        } catch (e: java.lang.Exception){
            e.printStackTrace()
        }

    }

    // Scan paired devices
    fun scanPairedDevices(context: Context) {
        viewModelScope.launch {
            fScanning.postValue(true)
            val startDiscovery = bluetoothAdapter.startDiscovery()
//            Log.d("BT_LOG", "BT device discovery")
            if (!startDiscovery){
                Log.d("BT_LOG", "BT device discovery unsuccesful")
            }
            val pairedDevices = bluetoothAdapter.bondedDevices
            if (pairedDevices.size == 0 || pairedDevices == null){
                scanResultsPaired.postValue(listOf())
            }
            else {
                pairedDevices.forEach { it -> mResultsPaired[it.address] = it }
                scanResultsPaired.postValue(mResultsPaired.values.toList())

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

    fun handleImageTransfer(imgBitmap: ImageBitmap, device: BluetoothDevice){

        server = BluetoothServer(activity, bluetoothAdapter)
        server.start()

        val client = BluetoothClient(device)
        client.start()

        if (server.sendReceive == null || client.sendReceive == null){
            Toast.makeText(context, "Cannot set up connection for sharing", Toast.LENGTH_SHORT).show()
            Log.d("BT_TRANSFER", "Not found any sendReceive service")
        }
        else {
            sendReceive = server.sendReceive
            Log.d("BT_TRANSFER", "in handle image transfer")
            val bitmap = imgBitmap.asAndroidBitmap()
            var oStream: ByteArrayOutputStream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 40, oStream)
            val imgBytes: ByteArray = oStream.toByteArray()

            Log.d("BT_TRANSFER", "img bytes total ${imgBytes.size} bytes")


            val subArraySize = 400
            var i = 0
            while (i < imgBytes.size) {
                var tempArray: ByteArray?
                tempArray =
                    Arrays.copyOfRange(imgBytes, i, Math.min(imgBytes.size, i + subArraySize))
                Log.d("BT_TRANSFER", "temp array ${tempArray.size}")
                Log.d("BT_TRANSFER", "send receive obj ${server.sendReceive}")

                server.sendReceive!!.write(tempArray!!)
                Log.d("BT_TRANSFER", "passed send receive")

                i += subArraySize
            }
        }

    }



}
