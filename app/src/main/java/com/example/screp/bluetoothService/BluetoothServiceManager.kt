package com.example.screp.bluetoothService

import android.Manifest
import android.app.Activity.RESULT_OK
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.Context.BLUETOOTH_SERVICE
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts

class BluetoothServiceManager (context: Context) {

    private val context = context

    private lateinit var bluetoothManager: BluetoothManager
    private lateinit var bluetoothAdapter: BluetoothAdapter
    private lateinit var bluetoothViewModel: BluetoothDataViewModel
    lateinit var takePermissions: ActivityResultLauncher<Array<String>>
    lateinit var takeResultLauncher: ActivityResultLauncher<Intent>

    val REQUEST_ENABLE_BT = 1

    fun init(){
        bluetoothManager = context.getSystemService(BLUETOOTH_SERVICE) as BluetoothManager
        bluetoothAdapter = bluetoothManager.adapter
        bluetoothViewModel = BluetoothDataViewModel()



    }

}