package com.example.screp.screens

import android.bluetooth.BluetoothDevice
import android.bluetooth.le.ScanResult
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Environment
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.screp.MainActivity
import com.example.screp.R
import com.example.screp.bluetoothService.BluetoothServiceManager
import com.example.screp.bottomNavigation.BottomNavItem
import com.example.screp.data.Photo
import com.example.screp.viewModels.PhotoAndMapViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.BufferedInputStream
import java.io.File

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun PhotoDetailScreen(
        navController: NavHostController,
        photoName: String?,
        photoAndMapViewModel: PhotoAndMapViewModel,
        bluetoothServiceManager: BluetoothServiceManager,
        imgPath: File?
    ) {
    val context = LocalContext.current
    val photo = photoName?.let { photoAndMapViewModel.getPhotoByName(it).observeAsState() }

    var sharingStarted by remember { mutableStateOf(false) }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceEvenly,
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        Image(
            BitmapFactory.decodeStream(BufferedInputStream(File("${imgPath}/${photoName}").inputStream()))
                .asImageBitmap(),
            null,
            modifier = Modifier
                .rotate(90F)
                .clickable(
                    enabled = true,
                    onClickLabel = "Clickable image",
                    onClick = {}
                )
                .size(400.dp)
        )
        Text("Address: ${photo?.value?.address}")
        Text("Date: ${photo?.value?.time}")
        Row(horizontalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier.fillMaxWidth()){
            Button(
                onClick = {
                    navController.navigate(BottomNavItem.Photos.screen_route)
                },
                modifier = Modifier
                    .size(50.dp)
                    .background(MaterialTheme.colors.onSecondary),
                shape = CircleShape,
                colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colors.onSecondary),
                ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_return),
                    contentDescription = "",
                    modifier = Modifier
                        .clip(CircleShape)
                        .fillMaxSize()
                )
            }
            Button(
                onClick = {
                    Log.d("BT_LOG", "photo details: BT adapter"+ bluetoothServiceManager.bluetoothAdapter.toString())
                    bluetoothServiceManager.scanDevices(context)
                    sharingStarted = true
                },
                modifier = Modifier
                    .size(50.dp)
                    .background(MaterialTheme.colors.onSecondary),
                shape = CircleShape,
                colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colors.onSecondary),
            ){
                Icon(painter = painterResource(id = R.drawable.ic_share),
                    contentDescription = stringResource(R.string.share),
                    modifier = Modifier
                        .clip(CircleShape)
                        .fillMaxSize()
                )
            }
        }
        if (sharingStarted){
            ShowDevices(bluetoothServiceManager)
        }
    }

}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ShowDevices(model: BluetoothServiceManager) {
    val context = LocalContext.current
    val devicesPaired: List<BluetoothDevice>? by model.scanResultsPaired.observeAsState(null)
    val devicesFound: List<BluetoothDevice>? by model.scanResultsFound.observeAsState(null)

    if (devicesPaired?.size == 0 || devicesPaired == null) {
        Toast.makeText(context, "Please pair a device to continue", Toast.LENGTH_SHORT).show()

    }
    val fScanning: Boolean by model.fScanning.observeAsState(false)
    Text(if (fScanning) "Scanning" else "")

    ListDevices(type = "Paired", listDevices = devicesPaired, model)
    ListDevices(type = "Found", listDevices = devicesFound, model)

}

@Composable
fun ListDevices(type: String, listDevices: List<BluetoothDevice>?, bluetoothServiceManager: BluetoothServiceManager){
    Column (
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("${type} devices", style = MaterialTheme.typography.h5)
        Text(
            buildAnnotatedString {
                if (listDevices?.size == 0 || listDevices == null ){
                    append("No devices ${type.lowercase()}.")
                    if (type == "Paired"){
                        append(" Please pair a device.")
                    }
                }
                else {
                    append("${listDevices?.size} ")
                    append(if (listDevices?.size > 1) "devices" else "device")
                    append(" ${type.lowercase()}")
                }}.toString())

        listDevices?.forEach {
            Text("Device: ${it.name} ${it.address}",
                modifier = Modifier
                    .padding(5.dp)
                    .selectable(true,
                        onClick = {
                            Log.d("BT_LOG", "selected item on list ${it.uuids}")
                            bluetoothServiceManager.pairDevices()
                        }
                    )
            )
        }
    }
}