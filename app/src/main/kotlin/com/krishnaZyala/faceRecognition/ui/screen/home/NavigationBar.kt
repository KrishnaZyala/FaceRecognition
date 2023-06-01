package com.krishnaZyala.faceRecognition.ui.screen.home

import androidx.annotation.DrawableRes
import androidx.annotation.Keep
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemColors
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import com.krishnaZyala.faceRecognition.ui.navigation.Route

@Composable
fun BottomBar(
    currentItem: NavBarItem,
    items: List<NavBarItem>,
    modifier: Modifier = Modifier,
    iconModifier: Modifier = Modifier,
    labelModifier: Modifier = Modifier,
    tonalElevation: Dp = NavigationBarDefaults.Elevation,
    containerColor: Color = NavigationBarDefaults.containerColor,
    windowInsets: WindowInsets = NavigationBarDefaults.windowInsets,
    contentColor: Color = MaterialTheme.colorScheme.contentColorFor(containerColor),
    colors: NavigationBarItemColors = NavigationBarItemDefaults.colors(
        selectedIconColor = MaterialTheme.colorScheme.primary,
        selectedTextColor = MaterialTheme.colorScheme.primary,
    ),
    onClick: (NavBarItem) -> Unit,
) = NavigationBar(modifier, containerColor, contentColor, tonalElevation, windowInsets) {
    items.forEach { item ->
        NavigationBarItem(
            selected = currentItem == item,
            onClick = { onClick(item) },
            colors = colors,
            enabled = item.enabled,
            modifier = iconModifier,
            alwaysShowLabel = item.showLabel,
            label = { Text(text = item.text, modifier = labelModifier) },
            icon = { Icon(painter = painterResource(item.icon), contentDescription = item.description, modifier = iconModifier) },
            interactionSource = remember { MutableInteractionSource() }
        )
    }
}

@Keep
data class NavBarItem(
    val route: Route,
    @DrawableRes val icon: Int,
    val text: String = route.name,
    val description: String = "$text Navigation Bar Icon",
    val showLabel: Boolean = true,
    val enabled: Boolean = true,
)