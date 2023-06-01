package com.krishnaZyala.faceRecognition.ui.screen.faces

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import com.krishnaZyala.faceRecognition.data.Repository
import com.krishnaZyala.faceRecognition.data.model.AppState
import com.krishnaZyala.faceRecognition.data.model.FaceInfo
import com.krishnaZyala.faceRecognition.lib.LOG
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FaceViewModel @Inject constructor(private val repo: Repository) : ViewModel() {
    lateinit var homeHost: NavHostController
    lateinit var appState: AppState
    val faces: Flow<List<FaceInfo>> = repo.faces

    fun onCompose(state: AppState, home: NavHostController) = viewModelScope.launch(Dispatchers.IO) {
        runCatching {
            appState = state
            homeHost = home
            LOG.d("Add Face Screen Composed")
        }.onFailure { LOG.e(it, it.message) }
    }

    fun onDispose() = runCatching {
        LOG.d("Add Face Screen Disposed")
    }.onFailure { LOG.e(it, it.message) }

    fun onDeleteFace(face: FaceInfo) = viewModelScope.launch(Dispatchers.IO) {
        runCatching {
            repo.deleteFace(face).getOrNull()
            LOG.d("Deleted Face \t:\t$face")
        }.onFailure { LOG.e(it, it.message) }
    }
}
