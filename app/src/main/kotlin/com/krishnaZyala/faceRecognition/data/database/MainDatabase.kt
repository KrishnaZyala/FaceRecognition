package com.krishnaZyala.faceRecognition.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.krishnaZyala.faceRecognition.data.model.FaceInfo

@Database(entities = [FaceInfo::class], version = 1, exportSchema = false)
@TypeConverters(ListConverter::class)
abstract class MainDatabase : RoomDatabase() {
    abstract val dao: MainDao
}

