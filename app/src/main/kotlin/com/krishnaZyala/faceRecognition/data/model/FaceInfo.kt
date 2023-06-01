package com.krishnaZyala.faceRecognition.data.model

import android.content.Context
import android.graphics.Bitmap
import androidx.annotation.Keep
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.mlkit.vision.face.FaceLandmark
import com.krishnaZyala.faceRecognition.app.Utils
import com.krishnaZyala.faceRecognition.lib.FileUtils.readBitmap

@Keep
@Entity
data class FaceInfo(
    @PrimaryKey(autoGenerate = true)
    val id: Int? = null,
    val name: String,
    val width: Int = 0,
    val height: Int = 0,
    val faceWidth: Int = 0,
    val faceHeight: Int = 0,
    val top: Int = 0,
    val left: Int = 0,
    val right: Int = 0,
    val bottom: Int = 0,
    val landmarks: List<FaceLandmark> = listOf(),
    val smilingProbability: Float = 0f,
    val leftEyeOpenProbability: Float = 0f,
    val rightEyeOpenProbability: Float = 0f,
    val timestamp: String = Utils.timestamp(),
    val time: Long = System.currentTimeMillis(),
) {
    val pattern get(): String = "${name}_${time}.png"
    val faceFileName get(): String = "Face_${pattern}"
    val imageFileName get(): String = "Image_${pattern}"
    val frameFileName get(): String = "Frame_${pattern}"
    fun faceBitmap(context: Context): Bitmap? = context.readBitmap(faceFileName).getOrNull()
    fun imageBitmap(context: Context): Bitmap? = context.readBitmap(imageFileName).getOrNull()
    fun frameBitmap(context: Context): Bitmap? = context.readBitmap(frameFileName).getOrNull()
    fun processedImage(context: Context): ProcessedImage {
        val image = imageBitmap(context)
        val frame = frameBitmap(context)
        val face = faceBitmap(context)
        return ProcessedImage(id = id, name = name, image = image, frame = frame, faceBitmap = face, landmarks = landmarks)
    }

}
