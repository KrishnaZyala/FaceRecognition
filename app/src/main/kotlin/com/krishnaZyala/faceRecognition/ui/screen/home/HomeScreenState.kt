package com.krishnaZyala.faceRecognition.ui.screen.home

import android.app.Activity
import androidx.activity.OnBackPressedDispatcher
import androidx.annotation.Keep
import androidx.navigation.NavHostController
import com.krishnaZyala.faceRecognition.R
import com.krishnaZyala.faceRecognition.ui.navigation.Routes
import com.krishnaZyala.faceRecognition.ui.screen.permission.Permission
import com.krishnaZyala.faceRecognition.ui.screen.permission.PermissionProvider

@Keep
data class HomeScreenState(
    val host: NavHostController? = null,
    val backPressDispatcher: OnBackPressedDispatcher? = null,
    val bottomBarItem: NavBarItem = defaultBottomBarItem,
    val bottomBarItems: List<NavBarItem> = defaultBottomBarItems,
    val permissions: MutableMap<PermissionProvider, Boolean> = defaultPermissions,
) {
    fun firstPermission(activity: Activity): PermissionProvider? = permissions.keys.firstOrNull { !it.hasPermission(activity) }

    companion object {
        val defaultPermissions: MutableMap<PermissionProvider, Boolean> = mutableMapOf(Permission.Camera to false)
        val defaultBottomBarItem: NavBarItem = NavBarItem(Routes.Recognise, R.drawable.ic_home)
        val defaultBottomBarItems: List<NavBarItem> = listOf(
            defaultBottomBarItem,
            NavBarItem(Routes.AddFace, R.drawable.ic_add_face),
            NavBarItem(Routes.Faces, R.drawable.ic_faces),
//        NavBarItem(Routes.Settings, R.drawable.ic_settings),
        )
    }
}
