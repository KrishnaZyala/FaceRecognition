package com.krishnaZyala.faceRecognition.data

import android.app.Application
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.Paint
import androidx.camera.core.CameraSelector
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.lifecycle.ProcessCameraProvider
import com.google.common.util.concurrent.ListenableFuture
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.Face
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetector
import com.google.mlkit.vision.face.FaceDetectorOptions
import com.google.mlkit.vision.face.FaceLandmark
import com.krishnaZyala.faceRecognition.data.database.MainDatabase
import com.krishnaZyala.faceRecognition.data.model.FaceInfo
import com.krishnaZyala.faceRecognition.data.model.ProcessedImage
import com.krishnaZyala.faceRecognition.lib.AiModel.recognizeFace
import com.krishnaZyala.faceRecognition.lib.FileUtils.writeBitmap
import com.krishnaZyala.faceRecognition.lib.LOG
import com.krishnaZyala.faceRecognition.lib.MediaUtils.bitmap
import com.krishnaZyala.faceRecognition.lib.MediaUtils.crop
import com.krishnaZyala.faceRecognition.lib.MediaUtils.flip
import kotlinx.coroutines.flow.Flow
import java.util.concurrent.Executor
import java.util.concurrent.Executors
import kotlin.math.atan2

class Repository(private val app: Application, private val db: MainDatabase) {
    val faces: Flow<List<FaceInfo>> = db.dao.faces()
    fun faceInfo(id: Int): Flow<FaceInfo> = db.dao.face(id)
    suspend fun faceList(): List<FaceInfo> = db.dao.faceList()
    suspend fun saveFace(image: ProcessedImage) = runCatching {
        val info = image.faceInfo
        val images = db.dao.faceList().map { it.processedImage(app) }
        if (image.faceBitmap == null) throw Throwable("Face is empty")
        if (images.find { image.name == it.name } != null) throw Throwable("Name Already Exist.")
        if ((app.recognizeFace(image, images)?.matchesCriteria == true)) throw Throwable("Face Already Exist.")
        image.faceBitmap.let { app.writeBitmap(info.faceFileName, it).getOrNull() }
        image.frame?.let { app.writeBitmap(info.frameFileName, it).getOrNull() }
        image.image?.let { app.writeBitmap(info.imageFileName, it).getOrNull() }
        db.dao.insert(info)
    }.onFailure { LOG.e(it, it.message) }

    suspend fun deleteFace(face: FaceInfo) = runCatching {
        if (face.id == null) throw Throwable("Invalid Face Id")
        db.dao.delete(face.id)
        app.deleteFile(face.faceFileName)
        app.deleteFile(face.frameFileName)
        app.deleteFile(face.imageFileName)
    }.onFailure { LOG.e(it, it.message) }

    suspend fun clearAllFaces() = runCatching {
        db.dao.clear()
    }.onFailure { LOG.e(it, it.message) }

    val cameraExecutor: Executor by lazy { Executors.newSingleThreadExecutor() }
    val cameraProviderFuture: ListenableFuture<ProcessCameraProvider> by lazy { ProcessCameraProvider.getInstance(app) }
    val faceDetector: FaceDetector by lazy {
        val options = FaceDetectorOptions.Builder()
            .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_ALL)
            .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_ACCURATE)
            .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_ALL)
            .build()
        FaceDetection.getClient(options)
    }

    fun biggestFace(faces: MutableList<Face>): Face? {
        var biggestFace: Face? = null
        var biggestFaceSize = 0
        for (face in faces) {
            val faceSize = face.boundingBox.height() * face.boundingBox.width()
            if (faceSize > biggestFaceSize) {
                biggestFaceSize = faceSize
                biggestFace = face
            }
        }
        return biggestFace
    }

    fun cameraSelector(lensFacing: Int): CameraSelector = CameraSelector.Builder().requireLensFacing(lensFacing).build()

    fun imageAnalysis(lensFacing: Int, paint: Paint, onData: (Result<ProcessedImage>) -> Unit): ImageAnalysis {
        val imageAnalyzer: ImageAnalysis.Analyzer = imageAnalyzer(lensFacing, paint, onData)
        val imageAnalysis = ImageAnalysis.Builder().apply {
            setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            setOutputImageFormat(ImageAnalysis.OUTPUT_IMAGE_FORMAT_YUV_420_888)
        }.build()
        imageAnalysis.setAnalyzer(cameraExecutor, imageAnalyzer)
        return imageAnalysis
    }

    fun imageAnalyzer(lensFacing: Int, paint: Paint, onFaceInfo: (Result<ProcessedImage>) -> Unit): ImageAnalysis.Analyzer =
        ImageAnalysis.Analyzer { imageProxy ->
            runCatching {
                @ExperimentalGetImage
                val mediaImage = imageProxy.image ?: throw Throwable("Unable to get Media Image")
                val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
                val bitmap = imageProxy.bitmap.getOrNull() ?: throw Throwable("Unable to get Bitmap")
                faceDetector.process(image)
                    .addOnSuccessListener(cameraExecutor) { onFaceInfo(processImage(lensFacing, it, bitmap, paint)) }
                    .addOnFailureListener(cameraExecutor) { LOG.e(it) }
                    .addOnCompleteListener { imageProxy.close() }
            }.onFailure { LOG.e(it, it.message) }
        }

    fun processImage(
        lensFacing: Int,
        data: MutableList<Face>,
        bitmap: Bitmap,
        paint: Paint
    ): Result<ProcessedImage> = runCatching {
        paint.style = Paint.Style.STROKE
        val face = biggestFace(data)
        var frame = Bitmap.createBitmap(bitmap.width, bitmap.height, bitmap.config)
        var faceBitmap = face?.boundingBox?.let { bitmap.crop(it.left, it.top, it.width(), it.height()).getOrNull() }
        val canvas = Canvas(frame)
        canvas.drawBitmap(bitmap, 0f, 0f, null)
        data.forEach { canvas.drawRect(it.boundingBox, paint) }
        face?.allLandmarks?.forEach { canvas.drawPoint(it.position.x, it.position.y, paint) }
        if (lensFacing == CameraSelector.LENS_FACING_FRONT) {
            frame = frame.flip(horizontal = true).getOrNull()
            faceBitmap = faceBitmap?.flip(horizontal = true)?.getOrNull()
        }
        faceBitmap = faceBitmap?.let { alignBitmapByLandmarks(bitmap = it, face?.allLandmarks ?: listOf()).getOrNull() }
        return@runCatching ProcessedImage(image = bitmap, frame = frame, face = face, trackingId = face?.trackingId, faceBitmap = faceBitmap)
    }.onFailure { LOG.e(it, it.message) }


    // Function to align a bitmap based on facial landmarks
    fun alignBitmapByLandmarks(bitmap: Bitmap, landmarks: List<FaceLandmark>, noseRatio: Float = 0.4f, eyeDistanceRatio: Float = 0.3f): Result<Bitmap> = runCatching {
        val leftEye = landmarks.find { it.landmarkType == FaceLandmark.LEFT_EYE }?.position
        val rightEye = landmarks.find { it.landmarkType == FaceLandmark.RIGHT_EYE }?.position
        val noseBase = landmarks.find { it.landmarkType == FaceLandmark.NOSE_BASE }?.position

        if (leftEye == null || rightEye == null || noseBase == null) return@runCatching bitmap

        val matrix = Matrix()

        val eyeCenterX = (leftEye.x + rightEye.x) / 2f
        val eyeCenterY = (leftEye.y + rightEye.y) / 2f
        val dx = rightEye.x - leftEye.x
        val dy = rightEye.y - leftEye.y
        val angle = atan2(dy.toDouble(), dx.toDouble()) * 180 / Math.PI

        matrix.postTranslate(-eyeCenterX, -eyeCenterY)
        matrix.postRotate(angle.toFloat(), 0f, 0f)

        // Calculate the desired eye distance based on a fixed ratio
        val desiredEyeDistance = bitmap.width * eyeDistanceRatio

        val scale = desiredEyeDistance / dx
        matrix.postScale(scale, scale)

        // Calculate the translation to bring the nose base to a fixed position
        val targetNoseY = bitmap.height * noseRatio
        val translationY = targetNoseY - noseBase.y * scale
        matrix.postTranslate(0f, translationY)

        // Apply the transformation matrix to the bitmap
        Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    }.onFailure { LOG.e(it, it.message) }

}

