package com.krishnaZyala.faceRecognition.ui.screen.faces

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScaffoldDefaults
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.Text
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.krishnaZyala.faceRecognition.data.model.AppState
import com.krishnaZyala.faceRecognition.data.model.FaceInfo
import com.krishnaZyala.faceRecognition.ui.theme.spacing

@Composable
fun FacesScreen(appState: AppState, host: NavHostController, vm: FaceViewModel = hiltViewModel()) {
    val faces by vm.faces.collectAsState(mutableListOf())

    DisposableEffect(appState, host) {
        vm.onCompose(appState, host)
        onDispose { vm.onDispose() }
    }
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        floatingActionButtonPosition = FabPosition.End,
        snackbarHost = { SnackbarHost(appState.snackbar) },
        containerColor = MaterialTheme.colorScheme.background,
        contentColor = contentColorFor(MaterialTheme.colorScheme.background),
        contentWindowInsets = ScaffoldDefaults.contentWindowInsets,
        content = { padding ->
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize(),
            ) {
                Text(text = "Saved Faces", style = MaterialTheme.typography.headlineLarge, color = MaterialTheme.colorScheme.primary)
                LazyColumn(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(MaterialTheme.spacing.Normal),
                ) { items(faces) { item -> FaceInfoItem(item, Modifier.padding(MaterialTheme.spacing.Small), vm::onDeleteFace) } }
            }
        }
    )
}


@Composable
private fun FaceInfoItem(face: FaceInfo, modifier: Modifier = Modifier, onDelete: (FaceInfo) -> Unit) = Card(modifier) {
    val ctx = LocalContext.current
    Row(Modifier.padding(MaterialTheme.spacing.Small), Arrangement.SpaceBetween) {
        face.faceBitmap(ctx)?.asImageBitmap()?.let { Image(it, "Face Bitmap", Modifier.size(50.dp)) }
        Column(Modifier.weight(1f), Arrangement.Center) {
            Text(text = face.name, style = MaterialTheme.typography.titleMedium)
            Text(text = face.timestamp, style = MaterialTheme.typography.labelSmall)
        }
        IconButton({ onDelete(face) }) { Icon(Icons.Default.Delete, null, Modifier.size(48.dp), MaterialTheme.colorScheme.error) }
    }
}