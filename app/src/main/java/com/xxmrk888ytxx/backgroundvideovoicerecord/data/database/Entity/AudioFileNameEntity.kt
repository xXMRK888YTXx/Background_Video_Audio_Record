package com.xxmrk888ytxx.backgroundvideovoicerecord.data.database.Entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    indices = [Index("name", unique = true)]
)
data class AudioFileNameEntity(
    @PrimaryKey val id:Long,
    val name:String
)
