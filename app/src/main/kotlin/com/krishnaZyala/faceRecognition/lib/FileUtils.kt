package com.krishnaZyala.faceRecognition.lib

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Environment
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import java.io.File

object FileUtils {
    val Context.externalStorageVolumes get(): Array<File> = ContextCompat.getExternalFilesDirs(applicationContext, null)
    val Context.primaryExternalStorage get():File = externalStorageVolumes[0]
    val isExternalStorageWritable get(): Boolean = Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED
    val isExternalStorageReadable get(): Boolean = Environment.getExternalStorageState() in setOf(Environment.MEDIA_MOUNTED, Environment.MEDIA_MOUNTED_READ_ONLY)

    fun Context.externalCacheFile(fileName: String) = File(externalCacheDir, fileName)
    fun Context.externalFile(fileName: String, type: String? = null) = File(getExternalFilesDir(type), fileName)
    fun Context.internalFile(fileName: String) = File(filesDir, fileName)
    fun Context.privateFile(fileName: String) = if (isExternalStorageWritable) externalFile(fileName) else internalFile(fileName)

    fun Context.isFileExist(fileName: String) = privateFile(fileName).exists()
    fun Context.writeFile(fileName: String, content: String): Result<Boolean> = runCatching {
        val file = privateFile(fileName)
        LOG.d("File Uri\t:\t${file.toUri()}")
        if (!file.exists()) file.createNewFile()
        if (!file.canWrite()) throw Throwable("Unable to Write")
        file.bufferedWriter().use { it.write(content) }
        return@runCatching file.exists()
    }.onFailure { LOG.e(it, it.message) }

    fun Context.readFile(fileName: String): Result<String> = runCatching {
        val file = privateFile(fileName)
        LOG.d("File Uri\t:\t${file.toUri()}")
        if (!file.canRead()) throw Throwable("Unable to Read")
        return@runCatching file.bufferedReader().use { it.readText() }
    }.onFailure { LOG.e(it, it.message) }

    fun Context.writeBitmap(fileName: String, bitmap: Bitmap) = runCatching {
        val file = privateFile(fileName)
        LOG.d("File Uri\t:\t${file.toUri()}")
        if (!file.exists()) file.createNewFile()
        if (!file.canWrite()) throw Throwable("Unable to Write")
        val outputStream = file.outputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
        outputStream.close()
        return@runCatching file.exists()
    }.onFailure { LOG.e(it, it.message) }

    fun Context.readBitmap(fileName: String): Result<Bitmap> = runCatching {
        val file = privateFile(fileName)
        LOG.d("File Uri\t:\t${file.toUri()}")
        if (!file.canRead()) throw Throwable("Unable to Read")
        val inputStream = file.inputStream()
        val bitmap = BitmapFactory.decodeStream(inputStream)
        inputStream.close()
        return@runCatching bitmap
    }.onFailure { LOG.e(it, it.message) }

}