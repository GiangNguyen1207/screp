package com.example.screp.services.bluetoothService

import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothServerSocket
import android.bluetooth.BluetoothSocket
import android.content.Context
import android.graphics.BitmapFactory
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.util.Log
import com.example.screp.MainActivity
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
class BluetoothServer(activity: Activity, bluetoothAdapter: BluetoothAdapter): Thread(){
    private val activity = activity
    private val bluetoothAdapter = bluetoothAdapter

    private lateinit var mServerSocket: BluetoothServerSocket
    lateinit var sendReceive: SendReceive

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

        var socket: BluetoothSocket? = null
        // Keep listening until exception occurs or a socket is returned.
        while (socket == null) {
            try {
                Log.d("BT_TRANSFER", "server trying to get the socket" )
                mServerSocket.accept(10000)
            } catch (e: Exception) {
                Log.e("BT_TRANSFER", "Socket's accept() method failed", e)
            }
            Log.d("BT_TRANSFER", "got the socket ${socket}" )


            if (socket != null){
                Log.d("BT_TRANSFER", "got the socket" )
                sendReceive = SendReceive(socket)
                sendReceive.start()
                mServerSocket?.close()
                break
            }
        }

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
    lateinit var sendReceive: SendReceive

    override fun run() {
        Log.d("BT_TRANSFER", "client socket ${mSocket}")
        Log.d("BT_TRANSFER", "client device ${mSocket.remoteDevice}")

        Log.i("BT_TRANSFER", "client Connecting")

        this.mSocket.connect()
        Log.d("BT_TRANSFER", "client socket connected ${mSocket.isConnected} ")
        sendReceive = SendReceive(mSocket);
        sendReceive.start()


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
            STATE_CONNECTING -> sharingStatusText = "Connecting"
            STATE_CONNECTED -> sharingStatusText = "Connected"
            STATE_CONNECTION_FAILED -> sharingStatusText = "Connection Failed"
            STATE_MESSAGE_RECEIVED -> {
                val readBuff = msg.obj as ByteArray
                val bitmap = BitmapFactory.decodeByteArray(readBuff, 0, msg.arg1)
            }
        }
    }
}

