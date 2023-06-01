package com.krishnaZyala.faceRecognition.lib

object StringUtils {
    val String.spaced get():String = "(?<=.)[A-Z]".toRegex().replace(this) { " ${it.value}" }
    fun cleanPhoneNumber(string: String, telCode: String = "+91") = string.replace(" ", "").removePrefix(telCode).removePrefix("0")
}