package com.krishnaZyala.faceRecognition.lib

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageFormat
import android.graphics.Matrix
import android.graphics.Rect
import android.graphics.YuvImage
import androidx.camera.core.ImageProxy
import java.io.ByteArrayOutputStream


object MediaUtils {
    val ImageProxy.bitmap: Result<Bitmap?>
        get(): Result<Bitmap?> = runCatching {
            val yBuffer = planes[0].buffer // Y
            val vuBuffer = planes[2].buffer // VU

            val ySize = yBuffer.remaining()
            val vuSize = vuBuffer.remaining()

            val nv21 = ByteArray(ySize + vuSize)

            yBuffer.get(nv21, 0, ySize)
            vuBuffer.get(nv21, ySize, vuSize)

            val yuvImage = YuvImage(nv21, ImageFormat.NV21, this.width, this.height, null)
            val out = ByteArrayOutputStream()
            yuvImage.compressToJpeg(Rect(0, 0, yuvImage.width, yuvImage.height), 50, out)
            val imageBytes = out.toByteArray()

            val bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
            bitmap.rotate(imageInfo.rotationDegrees.toFloat()).getOrNull()
        }.onFailure { LOG.e(it, it.message) }


    fun Bitmap.rotate(rotation: Float): Result<Bitmap> = runCatching {
        val matrix = Matrix()
        matrix.postRotate(rotation)
        Bitmap.createBitmap(this, 0, 0, width, height, matrix, true)
    }.onFailure { LOG.e(it, it.message) }

    fun Bitmap.flip(vertical: Boolean = false, horizontal: Boolean = false): Result<Bitmap> = runCatching {
        val matrix = Matrix()
        if (vertical) matrix.postScale(1f, -1f)
        if (horizontal) matrix.postScale(-1f, 1f)
        Bitmap.createBitmap(this, 0, 0, width, height, matrix, true)
    }.onFailure { LOG.e(it, it.message) }

    fun Bitmap.crop(left: Int, top: Int, width: Int, height: Int): Result<Bitmap> = runCatching {
        Bitmap.createBitmap(this, left, top, width, height)
    }.onFailure { LOG.e(it, it.message) }
}