package com.xxmrk888ytxx.backgroundvideovoicerecord.data.Repositoryes.AudioFileNameRepository

import androidx.room.Insert
import androidx.room.Query
import com.xxmrk888ytxx.backgroundvideovoicerecord.data.Repositoryes.AudioFileNameRepository.models.AudioNameModel
import com.xxmrk888ytxx.backgroundvideovoicerecord.data.database.Entity.AudioFileNameEntity
import kotlinx.coroutines.flow.Flow

interface AudioFileNameRepository {

    val audioNamesMapFlow: Flow<Map<Long,AudioNameModel>>

    suspend fun getNameById(id:Long) : AudioNameModel?

    suspend fun insertName(audioNameModel: AudioNameModel)

    suspend fun removeName(id:Long)
}