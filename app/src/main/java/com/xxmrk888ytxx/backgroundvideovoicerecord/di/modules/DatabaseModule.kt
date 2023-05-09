package com.xxmrk888ytxx.backgroundvideovoicerecord.di.modules

import android.content.Context
import androidx.room.Room
import com.xxmrk888ytxx.backgroundvideovoicerecord.data.database.AppDatabase
import com.xxmrk888ytxx.backgroundvideovoicerecord.di.AppScope
import dagger.Module
import dagger.Provides

@Module
class DatabaseModule {

    @Provides
    @AppScope
    fun providesDatabase(context: Context) : AppDatabase {
        return Room.databaseBuilder(context,AppDatabase::class.java,"database.db").build()
    }

    @Provides
    @AppScope
    fun providesAudioFileNameEntityDao(appDatabase: AppDatabase) = appDatabase.audioFileNameDao

    @Provides
    @AppScope
    fun providesVideoFileNameEntityDao(appDatabase: AppDatabase) = appDatabase.videoFileNameDao
}