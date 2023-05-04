package com.xxmrk888ytxx.backgroundvideovoicerecord.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.xxmrk888ytxx.backgroundvideovoicerecord.data.database.Dao.AudioFileNameEntityDao
import com.xxmrk888ytxx.backgroundvideovoicerecord.data.database.Dao.VideoFileNameEntityDao
import com.xxmrk888ytxx.backgroundvideovoicerecord.data.database.Entity.AudioFileNameEntity
import com.xxmrk888ytxx.backgroundvideovoicerecord.data.database.Entity.VideoFileNameEntity

@Database(
    entities = [
        AudioFileNameEntity::class,
        VideoFileNameEntity::class
    ],
    version = 1
)
abstract class AppDatabase : RoomDatabase() {

    abstract val audioFileNameEntityDao : AudioFileNameEntityDao

    abstract val videoFileNameEntityDao: VideoFileNameEntityDao
}