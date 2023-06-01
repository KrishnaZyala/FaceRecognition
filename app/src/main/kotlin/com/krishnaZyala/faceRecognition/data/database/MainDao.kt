package com.krishnaZyala.faceRecognition.data.database

import androidx.room.*
import com.krishnaZyala.faceRecognition.data.model.FaceInfo
import kotlinx.coroutines.flow.Flow

@Dao
interface MainDao {
    @Query("DELETE FROM FaceInfo")
    suspend fun clear()

    @Query("DELETE FROM FaceInfo WHERE id = :id")
    suspend fun delete(id: Int)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(data: FaceInfo)

    @Query("SELECT * FROM FaceInfo WHERE id = :id")
    fun face(id: Int): Flow<FaceInfo>

    @Query("SELECT * FROM FaceInfo ")
    fun faces(): Flow<List<FaceInfo>>

    @Query("SELECT * FROM FaceInfo ")
    suspend fun faceList(): List<FaceInfo>

}