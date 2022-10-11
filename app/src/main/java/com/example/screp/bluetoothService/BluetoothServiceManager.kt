package com.example.screp.bluetoothService

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
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.core.content.ContextCompat.checkSelfPermission
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.*
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
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

    lateinit var sendReceive: SendImage
    var sharingStatusText = ""

    val STATE_LISTENING = 1
    val STATE_CONNECTING = 2
    val STATE_CONNECTED = 3
    val STATE_CONNECTION_FAILED = 4
    val STATE_MESSAGE_RECEIVED = 5
    private val MY_UUID = UUID.fromString("8ce255c0-223a-11e0-ac64-0803450c9a66")


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


    // Create a BroadcastReceiver for ACTION_FOUND to find devices
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
//                        Log.d("BT_LOG", "broadcast receiver: " + deviceName + " .Is smart phone :" + deviceTypeIsSmartPhone )

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

        val server: BluetoothServer = BluetoothServer()
        server.start()
        val client: BluetoothClient = BluetoothClient(device)
        client.start()
        Log.d("BT_TRANSFER", "send receive obj ${sendReceive}")

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
                Log.d("BT_TRANSFER", "send receive obj ${sendReceive}")

                sendReceive!!.write(tempArray!!)
                Log.d("BT_TRANSFER", "passed send receive")

                i += subArraySize
            }

    }


    // Bluetooth connection as a server
    private inner class BluetoothServer(): Thread(){
        private lateinit var mmServerSocket: BluetoothServerSocket

        init {
            try {
                mmServerSocket = bluetoothAdapter?.listenUsingRfcommWithServiceRecord("my service", MY_UUID)
                Log.d("BT_TRANSFER", "BT server socket ${mmServerSocket}")
            } catch (e: IOException){
                Log.e("BT_TRANSFER", "BT server socket failed to init")

                e.printStackTrace()
            }
        }

        @RequiresApi(Build.VERSION_CODES.S)
        override fun run() {
            Log.d("BT_TRANSFER", "run BT Server")
            Log.d("BT_TRANSFER", "BLUETOOTH_CONNECT ${checkSelfPermission(context, android.Manifest.permission.BLUETOOTH_CONNECT)}")

            // Keep listening until exception occurs or a socket is returned.
            var shouldLoop = true
            while (shouldLoop) {
                val socket: BluetoothSocket? = try {
                    mmServerSocket?.accept()
                } catch (e: IOException) {
                    Log.e("BT_TRANSFER", "Socket's accept() method failed", e)
                    shouldLoop = false
                    null
                }
                socket?.also {
                    sendReceive = SendImage(it)
                    sendReceive.start()
                    mmServerSocket?.close()
                    shouldLoop = false
                }
            }

        }
        // Closes the connect socket and causes the thread to finish.
        fun cancel() {
            try {
                mmServerSocket?.close()
            } catch (e: IOException) {
                Log.e("BT_LOG", "Could not close the connect socket", e)
            }
        }

    }

    private inner class BluetoothClient(device: BluetoothDevice): Thread() {
        private val mSocket = device.createRfcommSocketToServiceRecord(MY_UUID)

        override fun run() {
            bluetoothAdapter?.cancelDiscovery()
            Log.i("client", "Connecting")
            mSocket?.let{ socket ->
                socket.connect()
            }

        }
    }

     inner class SendImage(socket: BluetoothSocket): Thread(){
        private lateinit var inputStream: InputStream
        private lateinit var outputStream: OutputStream

        var bluetoothSocket = socket
         init {
            var tempIn: InputStream? = null
            var tempOut: OutputStream? = null

            try {
                tempIn = bluetoothSocket.inputStream
                tempOut = bluetoothSocket.outputStream
            } catch (e: IOException){
                e.printStackTrace()
            }
            if (tempIn != null) {
                inputStream = tempIn
            }
            if (tempOut != null) {
                outputStream = tempOut
            }
        }

         override fun run (){
             var buffer: ByteArray = ByteArray(1024)
             var numberOfBytes: Int = 0
             var index: Int = 0
             var flag: Boolean = true

             while (true) {
                 if (flag) {
                     try {
                         val temp = ByteArray(inputStream.available())
                         if (inputStream.read(temp) > 0) {
                             numberOfBytes = String(temp, Charsets.UTF_8).toInt()
                             buffer = ByteArray(numberOfBytes)
                             flag = false
                         }
                     } catch (e: IOException) {
                         e.printStackTrace()
                     }
                 } else {
                     try {
                         val data = ByteArray(inputStream.available())
                         val numbers = inputStream.read(data)
                         System.arraycopy(data, 0, buffer, index, numbers)
                         index = index + numbers
                         if (index === numberOfBytes) {
                             handler.obtainMessage(
                                 STATE_MESSAGE_RECEIVED,
                                 numberOfBytes,
                                 -1,
                                 buffer
                             ).sendToTarget()
                             flag = true
                         }
                     } catch (e: IOException) {
                         e.printStackTrace()
                     }
                 }
             }


         }

         fun write(bytes: ByteArray){
             try {
                 outputStream.write(bytes)
                 outputStream.flush()
                 Log.d("BT_TRANSFER", "writing done ${outputStream}")

             }
             catch (e: IOException){
                 e.printStackTrace()
                 Log.d("BT_TRANSFER", "failed to write")

             }
         }
    }

    // Message handler
    var handler: Handler = object: Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            when (msg.what) {
                STATE_LISTENING -> sharingStatusText = "Listening"
                STATE_CONNECTING -> sharingStatusText= "Connecting"
                STATE_CONNECTED -> sharingStatusText = "Connected"
                STATE_CONNECTION_FAILED -> sharingStatusText = "Connection Failed"
                STATE_MESSAGE_RECEIVED -> {
                    val readBuff = msg.obj as ByteArray
                    val bitmap = BitmapFactory.decodeByteArray(readBuff, 0, msg.arg1)
                }
            }
        }
    }
}
