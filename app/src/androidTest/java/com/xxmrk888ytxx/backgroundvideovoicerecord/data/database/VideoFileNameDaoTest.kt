package com.xxmrk888ytxx.backgroundvideovoicerecord.data.database

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.xxmrk888ytxx.backgroundvideovoicerecord.data.database.Dao.AudioFileNameDao
import com.xxmrk888ytxx.backgroundvideovoicerecord.data.database.Dao.VideoFileNameDao
import com.xxmrk888ytxx.backgroundvideovoicerecord.data.database.Entity.AudioFileNameEntity
import com.xxmrk888ytxx.backgroundvideovoicerecord.data.database.Entity.VideoFileNameEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import kotlin.random.Random

class VideoFileNameDaoTest {

    val context: Context by lazy {
        ApplicationProvider.getApplicationContext<Context>()
    }

    lateinit var database:AppDatabase

    val dao: VideoFileNameDao
        get() = database.videoFileNameDao

    @Before
    fun init() {
        database = Room.inMemoryDatabaseBuilder(context,AppDatabase::class.java).build()
    }

    @After
    fun clear() {
        database.close()
    }

    @Test
    fun getAllNameFlow_putEntitiesExpectReturnIt() = runBlocking {
        val list = putList

        list.forEach { dao.insertName(it) }

        val expectedList : List<VideoFileNameEntity> =
            listOf(
                VideoFileNameEntity(0,"name0"),
                VideoFileNameEntity(1,"name1"),
                VideoFileNameEntity(2,"name2"),
                VideoFileNameEntity(3,"name3"),
            )


        Assert.assertEquals(expectedList,dao.getAllNameFlow().first())
    }

    @Test
    fun getNameById_putEntityAndGetItByIdExpectReturnsPutEntity() = runBlocking {
        val id = Random(System.currentTimeMillis()).nextLong(0,1000)
        val entity = VideoFileNameEntity(id,id.toString())

        dao.insertName(VideoFileNameEntity(id,id.toString()))

        Assert.assertEquals(entity,dao.getNameById(id))
    }

    @Test
    fun getNameById_GetItByIdExpectReturnsNull() = runBlocking {
        val id = Random(System.currentTimeMillis()).nextLong(0,1000)

        Assert.assertEquals(null,dao.getNameById(id))
    }

    @Test
    fun insertName_putTwoEntityWithEqualsIdExpectReturnsLastPuttedEntity() = runBlocking {
        val first = VideoFileNameEntity(1,"1")
        val second = VideoFileNameEntity(1,"2")

        dao.insertName(first)
        Assert.assertEquals(first,dao.getNameById(1))
        dao.insertName(second)

        Assert.assertEquals(second,dao.getNameById(1))
    }

    @Test
    fun removeName_putEntityAndRemoveItExpectGetNameByIdReturnsNull() = runBlocking {
        dao.insertName(VideoFileNameEntity(8,"8"))

        dao.removeName(8)

        Assert.assertEquals(null,dao.getNameById(8))
    }

    @Test
    fun removeName_removeEntityWithNotExistIdExpectNothingWasHappen() = runBlocking {
        dao.removeName(8)
    }


    val putList : List<VideoFileNameEntity>
        get() = listOf(
            VideoFileNameEntity(0,"name0"),
            VideoFileNameEntity(1,"name1"),
            VideoFileNameEntity(2,"name2"),
            VideoFileNameEntity(3,"name3"),
        )
}