package com.krishnaZyala.faceRecognition.data.database

import androidx.room.ProvidedTypeConverter
import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.google.mlkit.vision.face.FaceLandmark
import com.krishnaZyala.faceRecognition.data.model.FaceInfo

@ProvidedTypeConverter
class ListConverter {
    private val stringListType = object : TypeToken<List<String>>() {}.type
    private val faceLandmarkListType = object : TypeToken<List<FaceLandmark>>() {}.type
    private val faceInfoListType = object : TypeToken<List<FaceInfo>>() {}.type

    @TypeConverter
    fun toString(value: List<String>): String = Gson().toJson(value, stringListType)

    @TypeConverter
    fun toStringList(value: String): List<String> = try {
        Gson().fromJson(value, stringListType)
    } catch (e: Exception) {
        listOf()
    }

    @TypeConverter
    fun toFaceLandmark(value: List<FaceLandmark>): String = Gson().toJson(value, faceLandmarkListType)

    @TypeConverter
    fun toFaceLandmarkList(value: String): List<FaceLandmark> = try {
        Gson().fromJson(value, faceLandmarkListType)
    } catch (e: Exception) {
        listOf()
    }

    @TypeConverter
    fun toFaceInfo(value: List<FaceInfo>): String = Gson().toJson(value, faceInfoListType)

    @TypeConverter
    fun toFaceInfo(value: String): List<FaceInfo> = try {
        Gson().fromJson(value, faceInfoListType)
    } catch (e: Exception) {
        listOf()
    }
}