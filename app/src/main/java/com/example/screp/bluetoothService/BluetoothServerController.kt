package com.example.screp.bluetoothService

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothServerSocket
import android.bluetooth.BluetoothSocket
import android.content.Context
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.util.*

val MY_UUID = UUID.fromString("8989063a-c9af-463a-b3f1-f21d9b2b827b")

val STATE_LISTENING = 1
val STATE_CONNECTING = 2
val STATE_CONNECTED = 3
val STATE_CONNECTION_FAILED = 4
val STATE_MESSAGE_RECEIVED = 5

var sharingStatusText = ""


// Bluetooth connection as a server
class BluetoothServer(context: Context, bluetoothAdapter: BluetoothAdapter): Thread(){
    private val context = context
    private val bluetoothAdapter = bluetoothAdapter

    private lateinit var mServerSocket: BluetoothServerSocket

    init {
        try {
            mServerSocket = bluetoothAdapter.listenUsingRfcommWithServiceRecord("BTService", MY_UUID)
            Log.d("BT_TRANSFER", "BT server socket init ${mServerSocket}")
        } catch (e: Exception){
            Log.e("BT_TRANSFER", "BT server socket failed to init")

            e.printStackTrace()
        }
    }

    override fun run() {
        Log.d("BT_TRANSFER", "run BT Server")
//        Log.d("BT_TRANSFER", "BLUETOOTH_CONNECT ${
//            ContextCompat.checkSelfPermission(
//                context,
//                Manifest.permission.BLUETOOTH_CONNECT
//            )
//        }")

        // Keep listening until exception occurs or a socket is returned.
        var shouldLoop = true
        while (shouldLoop) {
            val socket: BluetoothSocket? = try {
                Log.d("BT_TRANSFER", "server trying to get the socket" )
                Log.d("BT_TRANSFER", "BT server socket ${mServerSocket.toString()}")

                mServerSocket.accept()
            } catch (e: Exception) {
                Log.e("BT_TRANSFER", "Socket's accept() method failed", e)
                shouldLoop = false
                null
            }
            Log.d("BT_TRANSFER", "got the socket ${socket}" )

            socket?.also {
                Log.d("BT_TRANSFER", "got the socket" )
                val sendReceive = SendReceive(it)
                sendReceive.start()
                mServerSocket?.close()
                shouldLoop = false
            }
        }
        Log.d("BT_TRANSFER", "shouldLoop ${shouldLoop}" )


    }
    // Closes the connect socket and causes the thread to finish.
    fun cancel() {
        try {
            mServerSocket?.close()
        } catch (e: IOException) {
            Log.e("BT_TRANSFER", "Could not close the connect socket", e)
        }
    }

}

class BluetoothClient(device: BluetoothDevice): Thread() {
    private val mSocket = device.createInsecureRfcommSocketToServiceRecord(MY_UUID)

    override fun run() {
        Log.d("BT_TRANSFER", "client socket ${mSocket}")
        Log.d("BT_TRANSFER", "client device ${mSocket.remoteDevice}")

//            bluetoothAdapter?.cancelDiscovery()
        Log.i("BT_TRANSFER", "client Connecting")
        mSocket?.let{ socket ->
            socket.connect()
            Log.d("BT_TRANSFER", "client socket connected ${socket.isConnected} ")

        }

    }
}

class SendReceive(socket: BluetoothSocket): Thread(){
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
private var handler: Handler = object: Handler(Looper.getMainLooper()) {
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

