package com.xxmrk888ytxx.backgroundvideovoicerecord.domain.appLock

import android.content.Context
import kotlinx.coroutines.flow.Flow

interface AppLockManager {
    val isAppLockEnabled: Flow<Boolean>
    val isBiometricEnabled: Flow<Boolean>
    val isPinCodeSet: Flow<Boolean>

    val isSessionUnlocked: Flow<Boolean>

    fun unlockSession()
    fun lockSession()
    suspend fun setAppLockEnabled(enabled: Boolean)
    suspend fun setBiometricEnabled(enabled: Boolean)
    suspend fun setPinCode(pin: String)
    suspend fun verifyPinCode(pin: String): Boolean
    fun canAuthenticateWithBiometrics(context: Context): Boolean
}
