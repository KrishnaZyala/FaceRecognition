package com.krishnaZyala.faceRecognition.ui.theme

import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontLoadingStrategy
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import com.krishnaZyala.faceRecognition.R

object Fonts {

    val Poppins = FontFamily(
        Font(R.font.poppins_thin, FontWeight.Thin, FontStyle.Normal, FontLoadingStrategy.Blocking),
        Font(R.font.poppins_extra_light, FontWeight.ExtraLight, FontStyle.Normal, FontLoadingStrategy.Blocking),
        Font(R.font.poppins_light, FontWeight.Light, FontStyle.Normal, FontLoadingStrategy.Blocking),
        Font(R.font.poppins_regular, FontWeight.Normal, FontStyle.Normal, FontLoadingStrategy.Blocking),
        Font(R.font.poppins_medium, FontWeight.Medium, FontStyle.Normal, FontLoadingStrategy.Blocking),
        Font(R.font.poppins_semi_bold, FontWeight.SemiBold, FontStyle.Normal, FontLoadingStrategy.Blocking),
        Font(R.font.poppins_bold, FontWeight.Bold, FontStyle.Normal, FontLoadingStrategy.Blocking),
        Font(R.font.poppins_extra_bold, FontWeight.ExtraBold, FontStyle.Normal, FontLoadingStrategy.Blocking),
        Font(R.font.poppins_black, FontWeight.Black, FontStyle.Normal, FontLoadingStrategy.Blocking),

        Font(R.font.poppins_thin_italic, FontWeight.Thin, FontStyle.Italic, FontLoadingStrategy.Blocking),
        Font(R.font.poppins_extra_light_italic, FontWeight.ExtraLight, FontStyle.Italic, FontLoadingStrategy.Blocking),
        Font(R.font.poppins_light_italic, FontWeight.Light, FontStyle.Italic, FontLoadingStrategy.Blocking),
        Font(R.font.poppins_italic, FontWeight.Normal, FontStyle.Italic, FontLoadingStrategy.Blocking),
        Font(R.font.poppins_medium_italic, FontWeight.Medium, FontStyle.Italic, FontLoadingStrategy.Blocking),
        Font(R.font.poppins_semi_bold_italic, FontWeight.SemiBold, FontStyle.Italic, FontLoadingStrategy.Blocking),
        Font(R.font.poppins_bold_italic, FontWeight.Bold, FontStyle.Italic, FontLoadingStrategy.Blocking),
        Font(R.font.poppins_extra_bold_italic, FontWeight.ExtraBold, FontStyle.Italic, FontLoadingStrategy.Blocking),
        Font(R.font.poppins_black_italic, FontWeight.Black, FontStyle.Italic, FontLoadingStrategy.Blocking),
    )

}