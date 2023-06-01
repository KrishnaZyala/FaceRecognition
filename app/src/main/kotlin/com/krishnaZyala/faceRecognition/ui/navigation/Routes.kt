package com.krishnaZyala.faceRecognition.ui.navigation

import androidx.navigation.NamedNavArgument
import androidx.navigation.NavType
import androidx.navigation.navArgument

enum class Routes : Route {
    Welcome, Home, AddFace, Recognise, Faces, Settings, Notifications {
        override val pattern: String get() = "${name}Screen?id={id}"
        override val arguments: List<NamedNavArgument> get() = listOf(navArgument("id") { defaultValue = "";type = NavType.StringType; })
    }
}