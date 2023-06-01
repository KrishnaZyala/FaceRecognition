package com.krishnaZyala.faceRecognition.lib

import android.content.Context
import android.graphics.Bitmap
import com.krishnaZyala.faceRecognition.data.model.ProcessedImage
import org.tensorflow.lite.Interpreter
import java.io.FileInputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel
import kotlin.math.sqrt

object AiModel {
    private const val FACE_NET_MODEL_PATH = "face_net_512.tflite"
    private const val ANTI_SPOOF_MODEL_PATH = "anti_spoof_model.tflite"
    private const val MOBILE_NET_MODEL_PATH = "mobile_net.tflite"

    const val FACE_NET_IMAGE_SIZE = 160
    const val FACE_NET_EMBEDDING_SIZE = 512
    const val MOBILE_NET_IMAGE_SIZE = 224

    private const val IMAGE_MEAN = 128.0f
    private const val IMAGE_STD = 128.0f
    const val DEFAULT_SIMILARITY = 0.8f
    private var isRunning = false

    val Context.faceNetInterpreter
        get(): Interpreter {
            val fileDescriptor = assets.openFd(FACE_NET_MODEL_PATH)
            val inputStream = FileInputStream(fileDescriptor.fileDescriptor)
            val fileChannel = inputStream.channel
            val startOffset = fileDescriptor.startOffset
            val declaredLength = fileDescriptor.declaredLength
            val modelBuffer: MappedByteBuffer = fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)
            return Interpreter(modelBuffer)
        }

    val Context.mobileNetInterpreter
        get(): Interpreter {
            val fileDescriptor = assets.openFd(MOBILE_NET_MODEL_PATH)
            val inputStream = FileInputStream(fileDescriptor.fileDescriptor)
            val fileChannel = inputStream.channel
            val startOffset = fileDescriptor.startOffset
            val declaredLength = fileDescriptor.declaredLength
            val modelBuffer: MappedByteBuffer = fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)
            return Interpreter(modelBuffer)
        }
    val Context.antiSpoofInterpreter
        get(): Interpreter {
            val fileDescriptor = assets.openFd(ANTI_SPOOF_MODEL_PATH)
            val inputStream = FileInputStream(fileDescriptor.fileDescriptor)
            val fileChannel = inputStream.channel
            val startOffset = fileDescriptor.startOffset
            val declaredLength = fileDescriptor.declaredLength
            val modelBuffer: MappedByteBuffer = fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)
            return Interpreter(modelBuffer)
        }

    fun Context.mobileNet(face: ProcessedImage, interpreter: Interpreter = mobileNetInterpreter): Result<Float> = runCatching {
        // Preprocess the reference bitmap
        val referenceInput = face.faceBitmap?.let { bitmap -> preprocessBitmap(bitmap, MOBILE_NET_IMAGE_SIZE).getOrNull()?.let { arrayOf(it) } }
            ?: throw Throwable("Unable to preprocess Bitmap")
        // Allocate output buffer for the reference embedding
        val referenceOutputBuffer = ByteBuffer.allocateDirect(4).apply { order(ByteOrder.nativeOrder()) }
        val referenceOutputs: MutableMap<Int, Any> = mutableMapOf(0 to referenceOutputBuffer)
        interpreter.runForMultipleInputsOutputs(referenceInput, referenceOutputs)
        referenceOutputBuffer.rewind()
        val data = referenceOutputBuffer.float
        data
    }.onFailure { LOG.e(it, it.message) }

    fun Context.validateFace(face: ProcessedImage, interpreter: Interpreter = antiSpoofInterpreter): Result<Float> = runCatching {
        // Preprocess the reference bitmap
        val referenceInput =
            face.faceBitmap?.let { bitmap -> preprocessBitmap(bitmap, FACE_NET_IMAGE_SIZE).getOrNull()?.let { arrayOf(it) } }
                ?: throw Throwable("Unable to preprocess Bitmap")
        // Allocate output buffer for the reference embedding
        val referenceOutputBuffer = ByteBuffer.allocateDirect(4).apply { order(ByteOrder.nativeOrder()) }
        val referenceOutputs: MutableMap<Int, Any> = mutableMapOf(0 to referenceOutputBuffer)
        interpreter.runForMultipleInputsOutputs(referenceInput, referenceOutputs)
        referenceOutputBuffer.rewind()
        val data = referenceOutputBuffer.float
        data
    }.onFailure { LOG.e(it, it.message) }

    fun Context.recognizeFace(face: ProcessedImage, faces: List<ProcessedImage>, interpreter: Interpreter = faceNetInterpreter): ProcessedImage? {
        synchronized(this) {
            if (isRunning) return@synchronized
            isRunning = true
        }
        return try {
            // Preprocess the reference bitmap
            val referenceInput =
                face.faceBitmap?.let { bitmap -> preprocessBitmap(bitmap, FACE_NET_IMAGE_SIZE).getOrNull()?.let { arrayOf(it) } }
                    ?: throw Throwable("Unable to preprocess Bitmap")
            // Allocate output buffer for the reference embedding
            val referenceOutputBuffer = ByteBuffer.allocateDirect(FACE_NET_EMBEDDING_SIZE * 4).apply { order(ByteOrder.nativeOrder()) }
            // Run inference for the reference bitmap
            val referenceOutputs: MutableMap<Int, Any> = mutableMapOf(0 to referenceOutputBuffer)
            interpreter.runForMultipleInputsOutputs(referenceInput, referenceOutputs)
            // Process test bitmaps
            var image: ProcessedImage? = null
            var minDistance = Float.MAX_VALUE

            for (data in faces) {
                // Preprocess the test face
                val testInputBuffer =
                    data.faceBitmap?.let { preprocessBitmap(it, FACE_NET_IMAGE_SIZE).getOrNull() } ?: throw Throwable("Unable to preprocess Test Bitmap")
                // Allocate output buffer for the test embedding
                val testOutputBuffer = ByteBuffer.allocateDirect(FACE_NET_EMBEDDING_SIZE * 4).apply { order(ByteOrder.nativeOrder()) }
                // Run inference for the test face
                val testInputs = arrayOf(testInputBuffer)
                val testOutputs: MutableMap<Int, Any> = mutableMapOf(0 to testOutputBuffer)
                interpreter.runForMultipleInputsOutputs(testInputs, testOutputs)
                // Calculate the Euclidean distance between the reference and test embeddings
                val distance = calculateDistance(referenceOutputBuffer, testOutputBuffer).getOrNull() ?: throw Throwable("Unable to calculate Distance")
                // Calculate the Cosine Similarity between the reference and test embeddings
                val similarity =
                    calculateCosineSimilarity(referenceOutputBuffer, testOutputBuffer).getOrNull() ?: throw Throwable("Unable to calculate Cosine Similarity")
                // Check if the distance is the smallest so far
                if (distance < minDistance) {
                    minDistance = distance
                    image = data.copy(distance = distance, similarity = similarity)
                }
            }
            // Cleanup
            interpreter.close()
            image
        } catch (th: Throwable) {
            LOG.e(th, th.message)
            null
        } finally {
            synchronized(this) { isRunning = false }
        }
    }

    // Preprocess the input bitmap for MobileFaceNet
    fun preprocessBitmap(bitmap: Bitmap, size: Int, isModelQuantized: Boolean = false): Result<ByteBuffer> = runCatching {
        val resizedBitmap = Bitmap.createScaledBitmap(bitmap, size, size, true)
        val inputBuffer = ByteBuffer.allocateDirect(size * size * 3 * 4).apply { order(ByteOrder.nativeOrder()) }
        for (y in 0 until size) {
            for (x in 0 until size) {
                val pixelValue = resizedBitmap.getPixel(x, y)
                if (isModelQuantized) {
                    // Quantized model
                    inputBuffer.put((pixelValue shr 16 and 0xFF).toByte())
                    inputBuffer.put((pixelValue shr 8 and 0xFF).toByte())
                    inputBuffer.put((pixelValue and 0xFF).toByte())
                } else {
                    // Float model
                    inputBuffer.putFloat(((pixelValue shr 16 and 0xFF) - IMAGE_MEAN) / IMAGE_STD)
                    inputBuffer.putFloat(((pixelValue shr 8 and 0xFF) - IMAGE_MEAN) / IMAGE_STD)
                    inputBuffer.putFloat(((pixelValue and 0xFF) - IMAGE_MEAN) / IMAGE_STD)
                }
            }
        }
        inputBuffer
    }.onFailure { LOG.e(it, it.message) }


    // Calculate the cosine similarity between two embeddings
    private fun calculateCosineSimilarity(embeddingBuffer1: ByteBuffer, embeddingBuffer2: ByteBuffer): Result<Float> = runCatching {
        var dotProduct = 0.0f
        var norm1 = 0.0f
        var norm2 = 0.0f

        for (i in 0 until FACE_NET_EMBEDDING_SIZE) {
            val value1 = embeddingBuffer1.getFloat(i * 4)
            val value2 = embeddingBuffer2.getFloat(i * 4)

            dotProduct += value1 * value2
            norm1 += value1 * value1
            norm2 += value2 * value2
        }

        norm1 = sqrt(norm1)
        norm2 = sqrt(norm2)

        dotProduct / (norm1 * norm2)
    }.onFailure { LOG.e(it, it.message) }

    // Calculate the Euclidean distance between two embeddings
    fun calculateDistance(embeddingBuffer1: ByteBuffer, embeddingBuffer2: ByteBuffer): Result<Float> = runCatching {
        var sum = 0.0f
        for (i in 0 until FACE_NET_EMBEDDING_SIZE) {
            val diff = embeddingBuffer1.getFloat(i * 4) - embeddingBuffer2.getFloat(i * 4)
            sum += diff * diff
        }
        sqrt(sum.toDouble()).toFloat()
    }.onFailure { LOG.e(it, it.message) }


    // Function to align a bitmap based on facial landmarks
    /*
        fun alignBitmapByLandmarks(bitmap: Bitmap, leftEye: PointF, rightEye: PointF): Result<Bitmap> = runCatching {
        val deltaX = rightEye.x - leftEye.x
        val deltaY = rightEye.y - leftEye.y
        val angle = Math.toDegrees(atan2(deltaY.toDouble(), deltaX.toDouble()))
        val matrix = Matrix()
        matrix.setRotate(angle.toFloat(), bitmap.width / 2f, bitmap.height / 2f)
        Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    }.onFailure { LOG.e(it, it.message) }

        fun alignBitmapByLandmarks(bitmap: Bitmap, landmarks: List<FaceLandmark>): Result<Bitmap> = runCatching {
            val leftEye = landmarks.find { it.landmarkType == FaceLandmark.LEFT_EYE }?.position
            val rightEye = landmarks.find { it.landmarkType == FaceLandmark.RIGHT_EYE }?.position
            val noseBase = landmarks.find { it.landmarkType == FaceLandmark.NOSE_BASE }?.position
            val mouthLeft = landmarks.find { it.landmarkType == FaceLandmark.MOUTH_LEFT }?.position
            if (leftEye == null || rightEye == null || noseBase == null || mouthLeft == null) return@runCatching bitmap

            val matrix = Matrix()

            // Calculate the angle between the eyes
            val dx = rightEye.x - leftEye.x
            val dy = rightEye.y - leftEye.y
            val angle = atan2(dy.toDouble(), dx.toDouble()) * 180 / Math.PI

            // Translate and rotate the bitmap to align the eyes horizontally
            matrix.postTranslate(-leftEye.x, -leftEye.y)
            matrix.postRotate(angle.toFloat(), 0f, 0f)

            // Calculate the scale factors for horizontal and vertical alignment
            val targetHeight = (mouthLeft.y - noseBase.y) * 1.5f // Adjust the vertical scale factor as per your requirements

            // Scale the bitmap to align the face based on the eye distance and vertical position
            matrix.postScale(dx / bitmap.width, targetHeight / bitmap.height)

            // Translate the bitmap to align the nose base at the center vertically
            matrix.postTranslate(dx * 0.5f, targetHeight * 0.5f - (noseBase.y - leftEye.y))

            // Apply the transformation matrix to the bitmap
            Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
        }.onFailure { LOG.e(it, it.message) }
    */


}