package com.example.screp.bluetoothService

import android.Manifest
import android.app.Activity
import android.app.Activity.RESULT_OK
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothClass
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.bluetooth.le.BluetoothLeScanner
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanSettings
import android.companion.AssociationRequest
import android.companion.BluetoothDeviceFilter
import android.companion.BluetoothLeDeviceFilter
import android.companion.CompanionDeviceManager
import android.content.*
import android.content.Context.BLUETOOTH_SERVICE
import android.content.Context.COMPANION_DEVICE_SERVICE
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.runtime.rememberCoroutineScope
import androidx.core.app.ActivityCompat.startIntentSenderForResult
import androidx.core.content.ContextCompat.getSystemService
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.*


class BluetoothServiceManager (context: Context, activity: Activity): ViewModel() {

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
    private val SELECT_DEVICE_REQUEST_CODE = 0


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


    fun pairDevices(context: Context = this.context){
        deviceManager =  context.getSystemService(COMPANION_DEVICE_SERVICE) as CompanionDeviceManager

        // Create pairing request
        val pairingRequest: AssociationRequest =
            AssociationRequest.Builder()
                .setSingleDevice(true)
                .build()
        deviceManager.associate(pairingRequest,
            object : CompanionDeviceManager.Callback(){
                override fun onDeviceFound(p0: IntentSender?) {
                    activity.startIntentSenderForResult(p0,
                        SELECT_DEVICE_REQUEST_CODE, null, 0, 0, 0)
                }
                override fun onFailure(p0: CharSequence?) {
                    Toast.makeText(context, "Failed to pair", Toast.LENGTH_SHORT).show()
                }
            }, null)

    }


    // Scan paired devices
    fun scanDevices(context: Context) {
        viewModelScope.launch(Dispatchers.IO) {
            fScanning.postValue(true)
            val startDiscovery = bluetoothAdapter.startDiscovery()
            Log.d("BT_LOG", "BT device discovery")
            if (!startDiscovery){
                Log.d("BT_LOG", "BT device discovery unsuccesful")
            }
            val pairedDevices = bluetoothAdapter.bondedDevices
            Log.d("BT_LOG", "paired device: ${pairedDevices.size}")

            pairedDevices.forEach { it -> mResultsPaired[it.address] = it }
            Log.d("BT_LOG", "mResults: ${mResultsPaired.size}")
            Log.d("BT_LOG", "mResults: ${mResultsPaired.values}")

            scanResultsPaired.postValue(mResultsPaired.values.toList())
            Log.d("BT_LOG", "scan results: ${scanResultsPaired.value?.size}")

            fScanning.postValue(false)
        }
    }

}
