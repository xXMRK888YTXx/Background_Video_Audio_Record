package com.xxmrk888ytxx.backgroundvideovoicerecord.data.Repositoryes.VideoFileNameRepository

import androidx.room.Insert
import androidx.room.Query
import com.xxmrk888ytxx.backgroundvideovoicerecord.data.Repositoryes.AudioFileNameRepository.models.AudioNameModel
import com.xxmrk888ytxx.backgroundvideovoicerecord.data.Repositoryes.VideoFileNameRepository.models.VideoNameModel
import com.xxmrk888ytxx.backgroundvideovoicerecord.data.database.Entity.VideoFileNameEntity
import kotlinx.coroutines.flow.Flow

interface VideoFileNameRepository {

    val videoNamesMapFlow: Flow<Map<Long, VideoNameModel>>

    suspend fun getNameById(id:Long) : VideoNameModel?

    suspend fun insertName(videoNameModel: VideoNameModel)

    suspend fun removeName(id:Long)
}