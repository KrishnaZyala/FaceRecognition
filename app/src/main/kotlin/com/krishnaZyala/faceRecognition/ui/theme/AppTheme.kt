package com.krishnaZyala.faceRecognition.ui.theme

import android.app.Activity
import android.view.View
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import com.krishnaZyala.faceRecognition.app.Utils

@Composable
fun AppTheme(dynamicColors: Boolean, statusBar: Boolean, darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {
    val view: View = LocalView.current
    val ctx: Activity = LocalContext.current as Activity
    val useDynamicColors: Boolean = Utils.sdk(31) && dynamicColors
    val colorScheme = when {
        useDynamicColors && darkTheme -> dynamicDarkColorScheme(ctx)
        useDynamicColors && !darkTheme -> dynamicLightColorScheme(ctx)
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }
    if (statusBar && !view.isInEditMode) ctx.window?.let {
        SideEffect { it.statusBarColor = colorScheme.background.toArgb() }
        WindowCompat.getInsetsController(it, view).isAppearanceLightStatusBars = !darkTheme
    }
    CompositionLocalProvider(LocalSpacing provides Spacing()) {
        MaterialTheme(colorScheme = colorScheme, typography = AppTypography, content = content)
    }
}
