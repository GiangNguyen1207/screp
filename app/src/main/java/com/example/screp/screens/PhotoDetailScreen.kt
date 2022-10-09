package com.example.screp.screens

import android.graphics.BitmapFactory
import android.os.Environment
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.screp.R
import com.example.screp.bluetoothService.BluetoothServiceManager
import com.example.screp.bottomNavigation.BottomNavItem
import com.example.screp.data.Photo
import com.example.screp.viewModels.PhotoAndMapViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.BufferedInputStream
import java.io.File

@Composable
fun PhotoDetailScreen(navController: NavHostController,
                      photoName: String?,
                      photoAndMapViewModel: PhotoAndMapViewModel,
                      bluetoothServiceManager: BluetoothServiceManager,
                      imgPath: File?
    ) {

    val photo = photoName?.let { photoAndMapViewModel.getPhotoByName(it).observeAsState() }

    Log.d("BT_LOG", "in photo detail: bt service " + bluetoothServiceManager.toString())
    Log.d("BT_LOG", "in photo detail: img path" + imgPath.toString())


    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceEvenly,
        modifier = Modifier.fillMaxSize(),
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
                        onClick = {

                        }
                    )
                    .size(600.dp)
            )

            Text("Address: ${photo?.value?.address}")
            Text("Date: ${photo?.value?.time}")
            IconButton(onClick = {
                navController.navigate(BottomNavItem.Photos.screen_route)
            }) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_return),
                    contentDescription = "",
                    modifier = Modifier.size(50.dp)
                )
            }
        }

}