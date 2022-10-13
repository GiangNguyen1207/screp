package com.example.screp.screens

import android.bluetooth.BluetoothDevice
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log

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
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.navigation.NavHostController
import com.example.screp.BuildConfig
import com.example.screp.R
import com.example.screp.services.bluetoothService.BluetoothServiceManager
import com.example.screp.bottomNavigation.BottomNavItem
import com.example.screp.viewModels.PhotoAndMapViewModel
import java.io.BufferedInputStream
import java.io.File

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

    val imageInputStream = BitmapFactory.decodeStream(BufferedInputStream(File("${imgPath}/${photoName}").inputStream()))
        .asImageBitmap()

    val uri: Uri? = FileProvider.getUriForFile(
        context, BuildConfig.APPLICATION_ID + ".provider", File(("${imgPath}/${photoName}"))
    )
    val sendIntent: Intent = Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(Intent.EXTRA_STREAM, uri)
        type = "image/*"
    }
    val shareIntent = Intent.createChooser(sendIntent, null)

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceEvenly,
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        Image(
            imageInputStream,
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
                    if (sharingStarted){
                        bluetoothServiceManager.stopTimerJob()
                    }
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
                    bluetoothServiceManager.startTimerJob()
                    sharingStarted = true
                },
                modifier = Modifier
                    .size(50.dp)
                    .background(MaterialTheme.colors.onSecondary),
                shape = CircleShape,
                colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colors.onSecondary),
            ){
                Icon(painter = painterResource(id = R.drawable.ic_bluetooth),
                    contentDescription = stringResource(R.string.share),
                    modifier = Modifier
                        .clip(CircleShape)
                        .fillMaxSize()
                )
            }
            Button(onClick = { context.startActivity(shareIntent)},
                modifier = Modifier
                    .size(50.dp)
                .background(MaterialTheme.colors.onSecondary),
                shape = CircleShape,
                colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colors.onSecondary),)
            {
                Icon(painter = painterResource(id = R.drawable.ic_share),
                    contentDescription = stringResource(R.string.share),
                    modifier = Modifier
                        .clip(CircleShape)
                        .fillMaxSize()
                )
            }
        }
        if (sharingStarted){
            ShowDevices(bluetoothServiceManager, imageInputStream)
        }
    }

}

@Composable
fun ShowDevices(model: BluetoothServiceManager, imgBitmap: ImageBitmap) {
    val context = LocalContext.current
    val devicesPaired: List<BluetoothDevice>? by model.scanResultsPaired.observeAsState(null)
    val devicesFound: List<BluetoothDevice>? by model.scanResultsFound.observeAsState(null)
    val fScanning: Boolean by model.fScanning.observeAsState(false)
    Text(if(fScanning) "Scanning..." else "")


    ListDevices(type = "Paired", listDevices = devicesPaired, model, imgBitmap)
    ListDevices(type = "Found", listDevices = devicesFound, model, imgBitmap)

}

@Composable
fun ListDevices(type: String, listDevices: List<BluetoothDevice>?, bluetoothServiceManager: BluetoothServiceManager,
                imgBitmap: ImageBitmap){

    var listSize by remember{ mutableStateOf(0)}

    Column (
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        listSize = listDevices?.size ?: 0
        Text("${type} devices", style = MaterialTheme.typography.h5)
        Text(
            buildAnnotatedString {
                if (listSize == 0 || listDevices == null){
                    append("No devices ${type.lowercase()}.\n")
                    if (type == "Paired"){
                        append("Please pair a device.")
                    }
                    if (type == "Found"){
                        append("Make sure you have an unlocked smartphone in range.")
                    }
                }
                else {
                    append("${listDevices?.size} ")
                    append(if (listDevices?.size > 1) "devices" else "device")
                    append(" ${type.lowercase()}")
                }}.toString(),
            textAlign = TextAlign.Center)

        listDevices?.forEach {
            Text("Device: ${it.name} ${it.address}",
                modifier = Modifier
                    .padding(5.dp)
                    .selectable(true,
                        onClick = {
                            Log.d("BT_TRANSFER", "selected item on list ${it.name}")
                            if (type == "Found"){
                                bluetoothServiceManager.pairDevices(device = it)
                            }
                            if (type == "Paired"){
                                bluetoothServiceManager.handleImageTransfer(imgBitmap = imgBitmap, device = it)
                            }
                        }
                    )
            )
        }
    }
}
