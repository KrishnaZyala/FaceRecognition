package com.krishnaZyala.faceRecognition.ui.composable

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.krishnaZyala.faceRecognition.R

@Composable
fun FrameView(frame: Bitmap, modifier: Modifier = Modifier, onFlipCamera: (() -> Unit)? = null) = Box(
    contentAlignment = Alignment.BottomEnd,
    modifier = modifier
        .background(MaterialTheme.colorScheme.secondaryContainer)
        .fillMaxWidth(),
) {
    Image(frame.asImageBitmap(), "Frame Bitmap", Modifier.fillMaxSize())
    if (onFlipCamera != null) IconButton(onFlipCamera) {
        Icon(painterResource(R.drawable.ic_flip_camera), "Flip Camera Icon", Modifier.size(48.dp), MaterialTheme.colorScheme.primary)
    }
}