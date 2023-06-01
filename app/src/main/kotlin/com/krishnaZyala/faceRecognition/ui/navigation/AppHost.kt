package com.krishnaZyala.faceRecognition.ui.navigation

import android.app.Activity
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.krishnaZyala.faceRecognition.data.model.AppState
import com.krishnaZyala.faceRecognition.ui.screen.home.HomeScreen
import kotlinx.coroutines.CoroutineScope

@Composable
fun AppHost(
    startDestination: String,
    modifier: Modifier = Modifier,
    route: String? = null,
    scope: CoroutineScope = rememberCoroutineScope(),
    host: NavHostController = rememberNavController(),
    activity: Activity = LocalContext.current as Activity,
    snackbar: SnackbarHostState = remember { SnackbarHostState() },
    state: AppState = AppState(activity, scope, host, snackbar),
    builder: NavGraphBuilder.() -> Unit = appNavGraphBuilder(state),
) = NavHost(host, startDestination, modifier, route, builder)

fun appNavGraphBuilder(state: AppState): NavGraphBuilder.() -> Unit = {
    Routes.Home(this) { HomeScreen(state) }
}

