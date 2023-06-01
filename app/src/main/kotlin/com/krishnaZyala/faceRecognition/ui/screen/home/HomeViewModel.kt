package com.krishnaZyala.faceRecognition.ui.screen.home

import android.os.Bundle
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.NavHostController
import com.krishnaZyala.faceRecognition.data.Repository
import com.krishnaZyala.faceRecognition.data.model.AppState
import com.krishnaZyala.faceRecognition.lib.LOG
import com.krishnaZyala.faceRecognition.ui.screen.permission.PermissionProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import com.krishnaZyala.faceRecognition.ui.screen.home.HomeScreenState.Companion.defaultBottomBarItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(repo: Repository) : ViewModel() {
    val appState: MutableState<AppState?> = mutableStateOf(null)
    val state: MutableState<HomeScreenState> = mutableStateOf(HomeScreenState())
    val NavHostController?.canGoBack get() = (this?.backQueue?.size ?: 0) > 2
    val navChangeListener = NavController.OnDestinationChangedListener { controller: NavController, destination: NavDestination, _: Bundle? ->
        runCatching {
            val bottomBarItem = state.value.bottomBarItems.find { it.route.path == destination.route } ?: defaultBottomBarItem
            state.value = state.value.copy(bottomBarItem = bottomBarItem)
        }.onFailure { LOG.e(it, it.message) }
    }

    fun onCompose(appState: AppState, home: NavHostController) = viewModelScope.launch(Dispatchers.IO) {
        runCatching {
            this@HomeViewModel.appState.value = appState
            state.value = state.value.copy(host = home)
            state.value.host?.addOnDestinationChangedListener(navChangeListener)
            initPermissions()
            LOG.d("Home Screen Composed")
        }.onFailure { LOG.e(it, it.message) }
    }

    fun onDispose() = runCatching {
        state.value.host?.removeOnDestinationChangedListener(navChangeListener)
        LOG.d("Home Screen Disposed")
    }.onFailure { LOG.e(it, it.message) }

    fun navigate(value: NavBarItem) = runCatching {
        state.value.host?.let {
            LOG.d("canGoBack\t:\t${it.canGoBack}", "value\t:\t${value}", "defaultBottomBarItem\t:\t${defaultBottomBarItem}")
            if (value == state.value.bottomBarItem) return@runCatching
            if (it.canGoBack && value == defaultBottomBarItem) it.popBackStack(defaultBottomBarItem.route.path, false)
            else value.route.navigate(it, popBackStack = it.canGoBack || value == defaultBottomBarItem)
        }
    }.onFailure { LOG.e(it, it.message) }

    fun initPermissions() = runCatching {
        val permissions = mutableMapOf<PermissionProvider, Boolean>()
        state.value.permissions.keys.forEach { permissions[it] = false }
        state.value = state.value.copy(permissions = permissions)
    }.onFailure { LOG.e(it, it.message) }

    fun onPermissionDeny(provider: PermissionProvider, isDenied: Boolean) = runCatching {
        val permissions = mutableMapOf<PermissionProvider, Boolean>()
        state.value.permissions.keys.forEach { if (it.name == provider.name) permissions[it] = isDenied else permissions[it] = false }
        state.value = state.value.copy(permissions = permissions)
    }.onFailure { LOG.e(it, it.message) }


}
