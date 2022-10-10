package com.example.screp.bluetoothService

import android.Manifest
import android.app.Activity.RESULT_OK
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.bluetooth.le.BluetoothLeScanner
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanSettings
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Context.BLUETOOTH_SERVICE
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.runtime.rememberCoroutineScope
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.*


class BluetoothServiceManager (context: Context): ViewModel() {

    private val context = context

    private lateinit var bluetoothManager: BluetoothManager
    lateinit var bluetoothAdapter: BluetoothAdapter

    private val scope = CoroutineScope(Dispatchers.IO)

    companion object GattAttributes {
        const val SCAN_PERIOD: Long = 5000
    }

//    val scanResults = MutableLiveData<List<ScanResult>>(null)
    val scanResultsFound = MutableLiveData<List<BluetoothDevice>>(null)
    val scanResultsPaired = MutableLiveData<List<BluetoothDevice>>(null)

    val fScanning = MutableLiveData<Boolean>(false)
//    private val mResults = java.util.HashMap<String, ScanResult>()
    private val mResultsFound = java.util.HashMap<String, BluetoothDevice>()
    private val mResultsPaired = java.util.HashMap<String, BluetoothDevice>()

    fun init(){
        bluetoothManager = context.getSystemService(BLUETOOTH_SERVICE) as BluetoothManager
        bluetoothAdapter = bluetoothManager.adapter
        Log.d("BT_LOG", "BT Service: init")
        if (bluetoothAdapter == null) {
            Toast.makeText(context, "Device doesn't support Bluetooth", Toast.LENGTH_SHORT)
        }
    }

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
                    mResultsFound[device!!.address] = device!!
                    scanResultsFound.postValue(mResultsFound.values.toList())
                    Log.d("BT_LOG", "broadcast receiver: " + deviceName + deviceHardwareAddress )
                }
            }

        }
    }



    fun scanDevices(context: Context) {
        viewModelScope.launch(Dispatchers.IO) {
            fScanning.postValue(true)
            val startDiscovery = bluetoothAdapter.startDiscovery()
            if (!startDiscovery){
                Log.d("BT_LOG", "BT device discovery unsuccesful")
            }
            val pairedDevices = bluetoothAdapter.bondedDevices
            if (pairedDevices.size == 0 || pairedDevices == null ) {
                Toast.makeText(context, "Please pair a device to continue", Toast.LENGTH_LONG)

            } else {
                pairedDevices.forEach { it -> mResultsPaired[it.address] = it }
                Log.d("BT_LOG", "mResults: ${mResultsPaired.size}")
                Log.d("BT_LOG", "mResults: ${mResultsPaired.values}")

                scanResultsPaired.postValue(mResultsPaired.values.toList())
                Log.d("BT_LOG", "scan results: ${scanResultsPaired.value?.size}")

                fScanning.postValue(false)
            }
        }

    }




}
