package com.example.screp.screens

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.navigation.NavHostController
import com.example.screp.BuildConfig
import com.example.screp.R
import com.example.screp.bottomNavigation.BottomNavItem
import com.example.screp.data.Photo
import com.example.screp.viewModels.PhotoAndMapViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.BufferedInputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

@Composable
fun PhotoDetailScreen(navController: NavHostController, photoName: String?, photoAndMapViewModel: PhotoAndMapViewModel) {


    val photo = photoName?.let { photoAndMapViewModel.getPhotoByName(it).observeAsState() }
    val context = LocalContext.current

    val uri: Uri? = FileProvider.getUriForFile(
        context, BuildConfig.APPLICATION_ID + ".provider", File(("/storage/emulated/0/Android/data/com.example.screp/files/Pictures/" + photoName))
    )
    val sendIntent: Intent = Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(Intent.EXTRA_STREAM, uri)
        type = "image/*"
    }
    val shareIntent = Intent.createChooser(sendIntent, null)
    
    Box(
        modifier = Modifier.fillMaxSize(),
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                BitmapFactory.decodeStream(BufferedInputStream(File("/storage/emulated/0/Android/data/com.example.screp/files/Pictures/" + photoName).inputStream()))
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
            Button(onClick = {
                context.startActivity(shareIntent)
            }){
                Text("Share")
            }
        }
    }
}