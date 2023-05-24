package com.xxmrk888ytxx.preferencesstorage

import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Assert.*
import org.junit.Before


@RunWith(AndroidJUnit4::class)
class PreferencesStorageTest {

    object PreferencesStorageHolder {
        private val context by lazy {InstrumentationRegistry.getInstrumentation().targetContext}
        val preferencesStorage: PreferencesStorage = PreferencesStorage.Factory().create("test", context)
    }

    private val testKey = stringPreferencesKey("test")

    private val preferencesStorage = PreferencesStorageHolder.preferencesStorage

    @Before
    fun init() = runBlocking {
        preferencesStorage.removeProperty(testKey)
    }

    @Test
    fun callPreferencesStorageMethodsExpectThisWorkCorrent() = runBlocking {
        assertEquals(null,preferencesStorage.getPropertyOrNull(testKey).first())
        val testString = "test"

        preferencesStorage.writeProperty(testKey,testString)

        Assert.assertEquals(true,preferencesStorage.isPropertyExist(testKey).first())

        Assert.assertEquals(testString,preferencesStorage.getProperty(testKey,"").first())

        preferencesStorage.removeProperty(testKey)

        Assert.assertEquals(false,preferencesStorage.isPropertyExist(testKey).first())

    }
}