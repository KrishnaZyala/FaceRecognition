package com.krishnaZyala.faceRecognition.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

val tileFontFamily = Fonts.Poppins
val bodyFontFamily = Fonts.Poppins
val labelFontFamily = Fonts.Poppins
val displayFontFamily = Fonts.Poppins
val headlineFontFamily = Fonts.Poppins

val AppTypography = Typography(
    displayLarge = TextStyle(fontFamily = displayFontFamily, fontSize = 48.sp, fontWeight = FontWeight.ExtraBold),
    displayMedium = TextStyle(fontFamily = displayFontFamily, fontSize = 30.sp, fontWeight = FontWeight.ExtraBold),
    displaySmall = TextStyle(fontFamily = displayFontFamily, fontSize = 24.sp, fontWeight = FontWeight.ExtraBold),
    headlineLarge = TextStyle(fontFamily = headlineFontFamily, fontSize = 30.sp, fontWeight = FontWeight.Bold),
    headlineMedium = TextStyle(fontFamily = headlineFontFamily, fontSize = 24.sp, fontWeight = FontWeight.Bold),
    headlineSmall = TextStyle(fontFamily = headlineFontFamily, fontSize = 18.sp, fontWeight = FontWeight.Bold),
    titleLarge = TextStyle(fontFamily = tileFontFamily, fontSize = 18.sp, fontWeight = FontWeight.SemiBold),
    titleMedium = TextStyle(fontFamily = tileFontFamily, fontSize = 16.sp, fontWeight = FontWeight.SemiBold),
    titleSmall = TextStyle(fontFamily = tileFontFamily, fontSize = 14.sp, fontWeight = FontWeight.SemiBold),
    bodyLarge = TextStyle(fontFamily = bodyFontFamily, fontSize = 16.sp),
    bodyMedium = TextStyle(fontFamily = bodyFontFamily, fontSize = 14.sp),
    bodySmall = TextStyle(fontFamily = bodyFontFamily, fontSize = 12.sp),
    labelLarge = TextStyle(fontFamily = labelFontFamily, fontSize = 14.sp),
    labelMedium = TextStyle(fontFamily = labelFontFamily, fontSize = 12.sp),
    labelSmall = TextStyle(fontFamily = labelFontFamily, fontSize = 11.sp),
)