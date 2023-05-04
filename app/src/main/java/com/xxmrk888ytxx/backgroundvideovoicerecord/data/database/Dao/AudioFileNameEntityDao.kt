package com.xxmrk888ytxx.backgroundvideovoicerecord.data.database.Dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.xxmrk888ytxx.backgroundvideovoicerecord.data.database.Entity.AudioFileNameEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AudioFileNameEntityDao {

    @Query("SELECT * FROM AUDIOFILENAMEENTITY")
    fun getAllNameFlow() : Flow<List<AudioFileNameEntity>>

    @Query("SELECT * FROM AudioFileNameEntity WHERE id = :id")
    suspend fun getNameById(id:Long) : AudioFileNameEntity?

    @Insert
    suspend fun insertName(audioFileNameEntity: AudioFileNameEntity)

    @Query("DELETE FROM AUDIOFILENAMEENTITY WHERE id = :id")
    suspend fun removeName(id:Long)
}