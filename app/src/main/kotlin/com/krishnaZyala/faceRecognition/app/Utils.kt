package com.krishnaZyala.faceRecognition.app

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Context.BATTERY_SERVICE
import android.graphics.Insets
import android.icu.text.SimpleDateFormat
import android.os.BatteryManager
import android.os.Build
import android.provider.Settings
import android.util.DisplayMetrics
import android.util.Size
import android.view.WindowInsets
import androidx.core.content.ContextCompat
import java.util.*
import java.util.concurrent.Executor

object Utils {
    val Context.batteryManager get():BatteryManager = (getSystemService(BATTERY_SERVICE) as BatteryManager)
    val Context.batteryLevel get():Int = batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY)
    val Context.isCharging get():Boolean = batteryManager.isCharging

    val Context.executor get(): Executor = if (sdk(28)) this.mainExecutor else ContextCompat.getMainExecutor(this)
    val Context.deviceId @SuppressLint("HardwareIds") get() = Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID)
    val Activity.displaySize
        get() = if (sdk(30)) {
            val windowMetrics = windowManager.currentWindowMetrics
            val insets: Insets = windowMetrics.windowInsets.getInsetsIgnoringVisibility(WindowInsets.Type.systemBars())
            val width = windowMetrics.bounds.width() - insets.left - insets.right
            val height = windowMetrics.bounds.height() - insets.top - insets.bottom
            Size(width, height)
        } else {
            val displayMetrics = DisplayMetrics()
            windowManager.defaultDisplay.getMetrics(displayMetrics)
            Size(displayMetrics.widthPixels, displayMetrics.heightPixels)
        }


    fun sdk(version: Int): Boolean = Build.VERSION.SDK_INT >= version
    fun timestamp(pattern: String = "yyyy-MM-dd HH:mm:ss", date: Date = Date()): String = SimpleDateFormat(pattern, Locale.getDefault()).format(date)
}



