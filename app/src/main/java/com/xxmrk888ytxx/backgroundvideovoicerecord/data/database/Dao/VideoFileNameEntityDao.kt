package com.xxmrk888ytxx.backgroundvideovoicerecord.data.database.Dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.xxmrk888ytxx.backgroundvideovoicerecord.data.database.Entity.AudioFileNameEntity
import com.xxmrk888ytxx.backgroundvideovoicerecord.data.database.Entity.VideoFileNameEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface VideoFileNameEntityDao {

    @Query("SELECT * FROM VideoFileNameEntity")
    fun getAllNameFlow() : Flow<List<VideoFileNameEntity>>

    @Query("SELECT * FROM VIDEOFILENAMEENTITY WHERE id = :id")
    suspend fun getNameById(id:Long) : VideoFileNameEntity?

    @Insert
    suspend fun insertName(videoFileNameEntity: VideoFileNameEntity)

    @Query("DELETE FROM VIDEOFILENAMEENTITY WHERE id = :id")
    suspend fun removeName(id:Long)
}