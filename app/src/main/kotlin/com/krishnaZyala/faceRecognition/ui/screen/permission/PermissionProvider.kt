package com.krishnaZyala.faceRecognition.ui.screen.permission

import android.Manifest
import android.app.Activity
import android.content.Context
import android.os.Build
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.window.DialogProperties
import com.krishnaZyala.faceRecognition.lib.StringUtils.spaced

enum class Permission : PermissionProvider {
    Camera {
        override val message: String get() = "This app needs access to your Camera to capture photos, when you add new faces or recognise faces."
        override val deniedMessage: String get() = "$message ${super.deniedMessage}"
        override val permissions get() = arrayOf(Manifest.permission.CAMERA)
    },
    Notification {
        override val permissions
            @RequiresApi(Build.VERSION_CODES.TIRAMISU) get() = arrayOf(Manifest.permission.POST_NOTIFICATIONS)
    },
}

interface PermissionProvider {
    val name: String
    val permissions get(): Array<String> = arrayOf()
    val permissionName: String get():String = name.spaced
    val title get(): String = "$permissionName Permission Required"
    val message get(): String = "This app needs access to your $permissionName permission."
    val deniedMessage
        get() :String = """It seems you permanently declined $permissionName permission. 
        |This app need it and will not work without it. You can go to the app settings to grant it.
    """.trimMargin().replace("\n", "")
    val properties get(): DialogProperties = DialogProperties(dismissOnBackPress = false, dismissOnClickOutside = false)
    val heading get(): @Composable () -> Unit = { Text(title) }
    val trueBtnText @Composable get(): String = "Proceed"
    val falseBtnText @Composable get(): String? = null
    fun body(isDenied: Boolean): String = if (isDenied) deniedMessage else message
    fun hasPermission(context: Context): Boolean = context.hasPermission(permissions)
    fun isDenied(activity: Activity): Boolean = !activity.shouldShowRationale(permissions)
    fun request(launcher: ManagedActivityResultLauncher<Array<String>, Map<String, Boolean>>) = launcher.launch(permissions)

    @Composable
    operator fun invoke(isDenied: Boolean, modifier: Modifier = Modifier, onDeny: (Boolean) -> Unit, onClick: (() -> Unit)? = null) {
        val activity = LocalContext.current as Activity
        val launcher = rememberLauncherForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { onDeny(isDenied(activity)) }
        if (!hasPermission(activity)) PermissionDialog(isDenied, this, modifier) {
            onClick?.invoke()
            request(launcher)
            if (isDenied) activity.openAppSettings()
        }
    }

    @Composable
    operator fun invoke(
        isDenied: Boolean,
        launcher: ManagedActivityResultLauncher<Array<String>, Map<String, Boolean>>,
        modifier: Modifier = Modifier,
        onClick: (Boolean) -> Unit = { request(launcher) }
    ) = PermissionDialog(isDenied, this, modifier, onClick)
}