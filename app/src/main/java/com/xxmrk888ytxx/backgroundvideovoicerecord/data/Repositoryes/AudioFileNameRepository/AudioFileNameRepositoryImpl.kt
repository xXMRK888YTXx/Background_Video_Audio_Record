package com.xxmrk888ytxx.backgroundvideovoicerecord.data.Repositoryes.AudioFileNameRepository

import com.xxmrk888ytxx.backgroundvideovoicerecord.data.Repositoryes.AudioFileNameRepository.models.AudioNameModel
import com.xxmrk888ytxx.backgroundvideovoicerecord.data.database.Dao.AudioFileNameDao
import com.xxmrk888ytxx.backgroundvideovoicerecord.data.database.Entity.AudioFileNameEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject

class AudioFileNameRepositoryImpl @Inject constructor(
    private val audioFileNameDao: AudioFileNameDao
) : AudioFileNameRepository {

    override val audioNamesMapFlow: Flow<Map<Long, AudioNameModel>> = audioFileNameDao
        .getAllNameFlow()
        .map { list ->
            val map = mutableMapOf<Long,AudioNameModel>()

            list.forEach {
                map[it.id] = AudioNameModel(it.id,it.name)
            }

            map
        }

    override suspend fun getNameById(id: Long): AudioNameModel? = withContext(Dispatchers.IO) {
        val entity = audioFileNameDao.getNameById(id)

        return@withContext if(entity != null) AudioNameModel(entity.id,entity.name) else null
    }

    override suspend fun insertName(audioNameModel: AudioNameModel) = withContext(Dispatchers.IO) {
        audioFileNameDao.insertName(
            AudioFileNameEntity(audioNameModel.id,audioNameModel.name)
        )
    }

    override suspend fun removeName(id: Long) = withContext(Dispatchers.IO) {
        audioFileNameDao.removeName(id)
    }
}