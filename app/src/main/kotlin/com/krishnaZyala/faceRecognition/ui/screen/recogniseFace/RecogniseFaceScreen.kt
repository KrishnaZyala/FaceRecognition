package com.krishnaZyala.faceRecognition.ui.screen.recogniseFace

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScaffoldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.NavHostController
import com.krishnaZyala.faceRecognition.data.model.AppState
import com.krishnaZyala.faceRecognition.data.model.ProcessedImage
import com.krishnaZyala.faceRecognition.ui.composable.FaceAnalytics
import com.krishnaZyala.faceRecognition.ui.composable.FaceView
import com.krishnaZyala.faceRecognition.ui.composable.FrameView
import com.krishnaZyala.faceRecognition.ui.theme.spacing

@Composable
fun RecogniseFaceScreen(appState: AppState, host: NavHostController, vm: RecogniseFaceViewModel = hiltViewModel()) {
    val currentFace: ProcessedImage by vm.image
    val lensFacing: Int by vm.lensFacing
    val showDialog: Boolean by vm.showDialog
    val recognizedFace: ProcessedImage? by vm.recognizedFace
    val lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lensFacing) {
        vm.onCompose(appState.activity, lifecycleOwner)
        onDispose { vm.onDispose() }
    }
    LaunchedEffect(recognizedFace) { if (recognizedFace?.matchesCriteria == true) vm.showDialog() }
    val content: @Composable (PaddingValues) -> Unit = { padding ->
        Column(
            verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
        ) {
            currentFace.frame?.let { FrameView(frame = it, onFlipCamera = vm::onFlipCamera, modifier = Modifier.weight(1f)) }
            Row(
                Modifier
                    .fillMaxWidth()
                    .weight(0.5f)
            ) {
                recognizedFace?.let { image ->
                    image.faceBitmap?.let { FaceView(bitmap = it, modifier = Modifier.weight(0.5f)) }
                    FaceAnalytics(
                        image = image.copy(face = currentFace.face),
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .padding(start = MaterialTheme.spacing.ExtraSmall)
                    )
                }
                currentFace.faceBitmap?.let { FaceView(bitmap = it, modifier = Modifier.weight(0.5f)) }
            }
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        contentColor = contentColorFor(MaterialTheme.colorScheme.background),
        contentWindowInsets = ScaffoldDefaults.contentWindowInsets,
        content = content
    )
    if (showDialog) recognizedFace?.let { RecogniseFaceDialog(currentFace, it, onCancel = vm::hideDialog) }

}

@Composable
private fun RecogniseFaceDialog(
    frame: ProcessedImage,
    recognised: ProcessedImage,
    modifier: Modifier = Modifier,
    title: String = "Recognised Face",
    onCancel: () -> Unit,
) = Dialog(onCancel) {
    Card(modifier = modifier) {
        Text(
            text = title,
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.primary,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = MaterialTheme.spacing.Small),
        )
        Row(modifier = Modifier.height(250.dp)) {
            recognised.faceBitmap?.let { FaceView(bitmap = it, modifier = Modifier.weight(0.5f)) }
            frame.faceBitmap?.let { FaceView(bitmap = it, modifier = Modifier.weight(0.5f)) }
        }
        FaceAnalytics(image = recognised.copy(face = frame.face), modifier = Modifier.padding(MaterialTheme.spacing.Small))
        Text(
            text = "Close".uppercase(),
            fontWeight = FontWeight.ExtraBold,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .background(MaterialTheme.colorScheme.tertiaryContainer)
                .padding(vertical = MaterialTheme.spacing.Small)
                .clickable(onClick = onCancel)
                .fillMaxWidth()
        )
    }
}
