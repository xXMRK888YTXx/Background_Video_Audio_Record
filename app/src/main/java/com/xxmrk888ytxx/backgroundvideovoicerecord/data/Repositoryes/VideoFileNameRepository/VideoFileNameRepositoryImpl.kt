package com.xxmrk888ytxx.backgroundvideovoicerecord.data.Repositoryes.VideoFileNameRepository

import com.xxmrk888ytxx.backgroundvideovoicerecord.data.Repositoryes.VideoFileNameRepository.models.VideoNameModel
import com.xxmrk888ytxx.backgroundvideovoicerecord.data.database.Dao.AudioFileNameEntityDao
import com.xxmrk888ytxx.backgroundvideovoicerecord.data.database.Dao.VideoFileNameEntityDao
import com.xxmrk888ytxx.backgroundvideovoicerecord.data.database.Entity.VideoFileNameEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class VideoFileNameRepositoryImpl @Inject constructor(
    private val videoNameEntityDao: VideoFileNameEntityDao
) : VideoFileNameRepository {

    override val videoNamesMapFlow: Flow<Map<Long, VideoNameModel>> = videoNameEntityDao
        .getAllNameFlow()
        .map { list ->
            val map = mutableMapOf<Long,VideoNameModel>()

            list.forEach {
                map[it.id] = VideoNameModel(it.id,it.name)
            }

            map
        }

    override suspend fun getNameById(id: Long): VideoNameModel? {
        val entity = videoNameEntityDao.getNameById(id)

        return if(entity != null) VideoNameModel(entity.id,entity.name) else null
    }

    override suspend fun insertName(videoNameModel: VideoNameModel) {
        videoNameEntityDao.insertName(
            VideoFileNameEntity(videoNameModel.id,videoNameModel.name)
        )
    }

    override suspend fun removeName(id: Long) {
        videoNameEntityDao.removeName(id)
    }
}