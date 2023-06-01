package com.krishnaZyala.faceRecognition.ui.screen.permission

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

fun Activity.shouldShowRationale(permission: String): Boolean = ActivityCompat.shouldShowRequestPermissionRationale(this, permission)
fun Activity.shouldShowRationale(permissions: Array<String>): Boolean = permissions.map { shouldShowRationale(it) }.contains(true)
fun Context.hasPermission(permission: String): Boolean = ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED
fun Context.hasPermission(permissions: Array<String>): Boolean = !permissions.map { hasPermission(it) }.contains(false)
fun Context.openAppSettings() =
    Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.fromParts("package", packageName, null)).also(::startActivity)
