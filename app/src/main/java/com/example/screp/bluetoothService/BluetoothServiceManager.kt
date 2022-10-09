package com.example.screp.bluetoothService

import android.Manifest
import android.app.Activity.RESULT_OK
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.bluetooth.le.BluetoothLeScanner
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanSettings
import android.content.Context
import android.content.Context.BLUETOOTH_SERVICE
import android.content.Intent
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

    val scanResults = MutableLiveData<List<ScanResult>>(null)
    val fScanning = MutableLiveData<Boolean>(false)
    private val mResults = java.util.HashMap<String, ScanResult>()

    fun init(){
        bluetoothManager = context.getSystemService(BLUETOOTH_SERVICE) as BluetoothManager
        bluetoothAdapter = bluetoothManager.adapter
        Log.d("BT_LOG", "BT Service: init")
    }

    fun scanDevices(scanner: BluetoothLeScanner, context: Context) {
        viewModelScope.launch(Dispatchers.IO) {
            fScanning.postValue(true)

            val settings = ScanSettings.Builder()
                .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
                .setReportDelay(0)
                .build()
            Log.d("BT_LOG", "BT Service: BT adapter "+ bluetoothAdapter.toString())

            scanner.startScan(null, settings, leScanCallback)
            Log.d("BT_LOG", "BT Service: start scanning")

            delay(GattAttributes.SCAN_PERIOD)
            scanner.stopScan(leScanCallback)
            Log.d("BT_LOG", "mResults: ${mResults.size}")
            Log.d("BT_LOG", "mResults: ${mResults.values}")

            scanResults.postValue(mResults.values.toList())
            Log.d("BT_LOG", "scan results: ${scanResults.value?.size}")

            fScanning.postValue(false)
        }
    }

    private val leScanCallback: ScanCallback = object : ScanCallback() {
        @RequiresApi(Build.VERSION_CODES.O)
        override fun onScanResult(callbackType: Int, result: ScanResult) {
            super.onScanResult(callbackType, result)
            val device = result.device
            val deviceAddress = device.address
            mResults!![deviceAddress] = result
//            Log.d("BT_LOG", "Device address: $deviceAddress (${result.isConnectable})")
        }
    }

}