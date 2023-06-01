package com.krishnaZyala.faceRecognition.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavDeepLink
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavOptions
import androidx.navigation.Navigator
import androidx.navigation.compose.composable
import com.krishnaZyala.faceRecognition.lib.LOG

interface Route {
    val name: String
    val path: String get() = "${name}Screen"
    val pattern: String get() = path
    val deepLinks: List<NavDeepLink> get() = listOf()
    val arguments: List<NamedNavArgument> get() = listOf()

    operator fun invoke(navGraphBuilder: NavGraphBuilder, content: @Composable (NavBackStackEntry) -> Unit) =
        navGraphBuilder.composable(pattern, arguments, deepLinks, content)

    fun navigate(
        navHostController: NavHostController,
        key: String,
        value: String,
        navOptions: NavOptions? = null,
        navigatorExtras: Navigator.Extras? = null,
        popBackStack: Boolean = false,
        route: String = path.plus("?${key}=${value}"),
    ) = navigate(navHostController, navOptions, navigatorExtras, popBackStack, route)

    fun navigate(
        controller: NavHostController,
        navOptions: NavOptions? = null,
        navigatorExtras: Navigator.Extras? = null,
        popBackStack: Boolean = false,
        route: String = path,
    ) {
        if (popBackStack) controller.popBackStack()
        val routes = controller.backQueue.map { it.destination.route }
        LOG.i("Navigation Backstack Routes [${routes.size}]", routes)
        controller.navigate(route, navOptions, navigatorExtras)
    }
}

