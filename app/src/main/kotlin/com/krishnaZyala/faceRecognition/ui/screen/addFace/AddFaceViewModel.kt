package com.krishnaZyala.faceRecognition.ui.screen.addFace

import android.content.Context
import android.graphics.Color
import android.graphics.Paint
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.krishnaZyala.faceRecognition.data.Repository
import com.krishnaZyala.faceRecognition.data.model.ProcessedImage
import com.krishnaZyala.faceRecognition.lib.AiModel.mobileNet
import com.krishnaZyala.faceRecognition.lib.LOG
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class AddFaceViewModel @Inject constructor(private val repo: Repository) : ViewModel() {
    lateinit var snackbarHost: SnackbarHostState
    val cameraProvider: ProcessCameraProvider by lazy { repo.cameraProviderFuture.get() }
    val showSaveDialog: MutableState<Boolean> = mutableStateOf(false)
    val image: MutableState<ProcessedImage> = mutableStateOf(ProcessedImage())
    val lensFacing: MutableState<Int> = mutableStateOf(CameraSelector.LENS_FACING_FRONT)
    val cameraSelector get(): CameraSelector = repo.cameraSelector(lensFacing.value)
    val paint = Paint().apply {
        strokeWidth = 3f
        color = Color.GREEN
    }
    val Context.imageAnalysis
        get() = repo.imageAnalysis(lensFacing.value, paint) { result ->
            runCatching {
                val data = result.getOrNull() ?: return@runCatching
                data.landmarks = data.face?.allLandmarks ?: listOf()
                data.spoof = mobileNet(data).getOrNull()
                image.value = data
            }.onFailure { LOG.e(it, it.message) }
        }

    fun onCompose(context: Context, lifecycleOwner: LifecycleOwner, snackbar: SnackbarHostState) = viewModelScope.launch {
        runCatching {
            snackbarHost = snackbar
            if (showSaveDialog.value) return@runCatching
            bindCamera(lifecycleOwner, context.imageAnalysis)
            delay(1000)
            bindCamera(lifecycleOwner, context.imageAnalysis)
            LOG.d("Add Face Screen Composed")
        }.onFailure { LOG.e(it, it.message) }
    }

    fun onDispose() = runCatching {
        cameraProvider.unbindAll()
        LOG.d("Add Face Screen Disposed")
    }.onFailure { LOG.e(it, it.message) }


    fun onFlipCamera() = runCatching {
        lensFacing.value = if (lensFacing.value == CameraSelector.LENS_FACING_BACK) CameraSelector.LENS_FACING_FRONT else CameraSelector.LENS_FACING_BACK
        LOG.d("Camera Flipped lensFacing\t:\t${lensFacing.value}")
    }.onFailure { LOG.e(it, it.message) }

    fun bindCamera(lifecycleOwner: LifecycleOwner, imageAnalysis: ImageAnalysis) = runCatching {
        cameraProvider.unbindAll()
        cameraProvider.bindToLifecycle(lifecycleOwner, cameraSelector, imageAnalysis)
        LOG.d("Camera is bound to lifecycle.")
    }.onFailure { LOG.e(it, it.message) }

    fun onNameChange(value: String) = runCatching { image.value = image.value.copy(name = value) }.onFailure { LOG.e(it, it.message) }

    fun saveFace() = viewModelScope.launch {
        runCatching {
            hideSaveDialog()
            val error = withContext(Dispatchers.IO) { repo.saveFace(image.value).exceptionOrNull() }
            snackbarHost.showSnackbar(error?.message ?: "Face Saved Successfully")
        }.onFailure { LOG.e(it, it.message) }
    }

    fun showSaveDialog() = runCatching {
        showSaveDialog.value = true
        cameraProvider.unbindAll()
    }.onFailure { LOG.e(it, it.message) }

    fun hideSaveDialog() = runCatching { showSaveDialog.value = false }.onFailure { LOG.e(it, it.message) }

}
