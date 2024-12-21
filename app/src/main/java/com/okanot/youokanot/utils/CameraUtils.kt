package com.okanot.youokanot.utils

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun CameraButton(modifier: Modifier, onPhotoCaptured: (Bitmap?) -> Unit) {
    // State to track permission result
    var hasPermission by remember { mutableStateOf(false) }

    // Camera permission request launcher
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasPermission = isGranted
    }

    // Camera activity launcher
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) {
        // Handle the result if needed (e.g., check if photo was taken)
        val photoCaptured = if (it.resultCode == Activity.RESULT_OK) {
            it.data?.extras?.get("data") as? Bitmap
        } else {
            null
        }
        onPhotoCaptured(photoCaptured)

    }

    Column(
        modifier = modifier
            .wrapContentSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(onClick = {
            if (!hasPermission) {
                // Request camera permission
                permissionLauncher.launch(Manifest.permission.CAMERA)
            } else {
                // Launch the camera
                val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                cameraLauncher.launch(cameraIntent)
            }
        }) {
            Text("Open Camera")
        }

        if (!hasPermission) {
            Spacer(modifier = Modifier.height(8.dp))
            Text("Camera permission is required.", color = MaterialTheme.colorScheme.error)
        }
    }
}
