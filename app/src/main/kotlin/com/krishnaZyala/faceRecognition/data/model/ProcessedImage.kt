package com.krishnaZyala.faceRecognition.data.model

import android.graphics.Bitmap
import androidx.annotation.Keep
import com.google.mlkit.vision.face.Face
import com.google.mlkit.vision.face.FaceLandmark
import com.krishnaZyala.faceRecognition.app.Utils
import com.krishnaZyala.faceRecognition.lib.AiModel.DEFAULT_SIMILARITY
import java.nio.ByteBuffer

@Keep
data class ProcessedImage(
    var id: Int? = null,
    var name: String = "",
    val face: Face? = null,
    var spoof: Float? = null,
    val image: Bitmap? = null,
    var frame: Bitmap? = null,
    val distance: Float? = null,
    val trackingId: Int? = null,
    val similarity: Float? = null,
    val faceBitmap: Bitmap? = null,
    val faceSignature: ByteBuffer? = null,
    var landmarks: List<FaceLandmark> = listOf(),
    val timestamp: String = Utils.timestamp(),
    val time: Long = System.currentTimeMillis(),
) {
    val matchesCriteria get():Boolean = (similarity ?: 0F) > DEFAULT_SIMILARITY
    val faceInfo
        get():FaceInfo = FaceInfo(
            name = name,
            width = image?.width ?: 0,
            height = image?.height ?: 0,
            top = face?.boundingBox?.top ?: 0,
            faceWidth = faceBitmap?.width ?: 0,
            left = face?.boundingBox?.left ?: 0,
            faceHeight = faceBitmap?.height ?: 0,
            right = face?.boundingBox?.right ?: 0,
            bottom = face?.boundingBox?.bottom ?: 0,
            landmarks = face?.allLandmarks ?: listOf(),
            smilingProbability = face?.smilingProbability ?: 0f,
            leftEyeOpenProbability = face?.leftEyeOpenProbability ?: 0f,
            rightEyeOpenProbability = face?.rightEyeOpenProbability ?: 0f,
        )
}
