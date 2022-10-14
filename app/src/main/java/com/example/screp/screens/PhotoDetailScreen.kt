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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import androidx.navigation.NavHostController
import com.example.screp.BuildConfig
import com.example.screp.R
import com.example.screp.services.bluetoothService.BluetoothServiceManager
import com.example.screp.bottomNavigation.BottomNavItem
import com.example.screp.helpers.Converter
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

    val imageInputStream =
        BitmapFactory.decodeStream(BufferedInputStream(File("${imgPath}/${photoName}").inputStream()))
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
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 20.dp)
                .clickable {
                    navController.navigate(BottomNavItem.Photos.screen_route)
                },
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Rounded.ArrowBack,
                tint = MaterialTheme.colors.secondary,
                contentDescription = "back",
                modifier = Modifier.size(16.dp)
            )
            Text(
                text = stringResource(R.string.back),
                color = MaterialTheme.colors.secondary,
                fontSize = 16.sp,
                modifier = Modifier.padding(end = 2.dp)
            )
        }

        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "Date: ${Converter().convertDateFormat(photo?.value?.time)}",
                color = MaterialTheme.colors.primary,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
            Text(
                "Taken at: ${photo?.value?.address}",
                color = MaterialTheme.colors.primary.copy(0.8f),
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
        }

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

        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier.fillMaxWidth()
        ) {
            Button(
                onClick = {
                    bluetoothServiceManager.startTimerJob()
                    sharingStarted = true
                },
                modifier = Modifier.size(50.dp),
                shape = CircleShape,
                colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colors.secondary),
            ) {
                Icon(
                    Icons.Rounded.Bluetooth,
                    contentDescription = stringResource(R.string.share),
                    tint = MaterialTheme.colors.onPrimary,
                )
            }

            Button(
                onClick = { context.startActivity(shareIntent) },
                modifier = Modifier.size(50.dp),
                shape = CircleShape,
                colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colors.secondary),
            )
            {
                Icon(
                    Icons.Rounded.Share,
                    contentDescription = stringResource(R.string.share),
                    tint = MaterialTheme.colors.onPrimary,
                )
            }
        }
        if (sharingStarted) {
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
    Text(if (fScanning) "Scanning..." else "")

    ListDevices(type = "Paired", listDevices = devicesPaired, model, imgBitmap)
    ListDevices(type = "Found", listDevices = devicesFound, model, imgBitmap)

}

@Composable
fun ListDevices(
    type: String,
    listDevices: List<BluetoothDevice>?,
    bluetoothServiceManager: BluetoothServiceManager,
    imgBitmap: ImageBitmap
) {

    var listSize by remember { mutableStateOf(0) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp),
    ) {
        listSize = listDevices?.size ?: 0
        Text(
            "${type} devices",
            style = MaterialTheme.typography.h5,
            color = MaterialTheme.colors.primary
        )
        Text(
            buildAnnotatedString {
                if (listSize == 0 || listDevices == null) {
                    append("No devices ${type.lowercase()}.\n")
                    if (type == "Paired") {
                        append("Please pair a device.")
                    }
                    if (type == "Found") {
                        append("Make sure you have an unlocked smartphone in range.")
                    }
                } else {
                    append("${listDevices?.size} ")
                    append(if (listDevices?.size > 1) "devices" else "device")
                    append(" ${type.lowercase()}")
                }
            }.toString(),
            color = MaterialTheme.colors.primary.copy(alpha = 0.7f)
        )

        listDevices?.forEach {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp)
                    .border(1.dp, MaterialTheme.colors.primary, shape = RoundedCornerShape(10.dp))
                    .selectable(true,
                        onClick = {
                            Log.d("BT_TRANSFER", "selected item on list ${it.name}")
                            if (type == "Found") {
                                bluetoothServiceManager.pairDevices(device = it)
                            }
                            if (type == "Paired") {
                                bluetoothServiceManager.handleImageTransfer(
                                    imgBitmap = imgBitmap,
                                    device = it
                                )
                            }
                        }
                    )
                    .padding(10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    "${it.name} ${it.address}",
                    color = MaterialTheme.colors.primary,
                )
                Icon(
                    Icons.Rounded.ArrowForward,
                    contentDescription = stringResource(R.string.click_here),
                    tint = MaterialTheme.colors.primary,
                )
            }
        }
    }
}
