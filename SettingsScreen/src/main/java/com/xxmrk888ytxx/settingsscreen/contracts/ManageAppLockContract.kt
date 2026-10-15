package com.xxmrk888ytxx.settingsscreen.contracts

import kotlinx.coroutines.flow.Flow

interface ManageAppLockContract {
    val isAppLockEnabled: Flow<Boolean>
    val isBiometricEnabled: Flow<Boolean>
    val isPinCodeSet: Flow<Boolean>
    val isBiometricsSupported: Boolean

    suspend fun setAppLockEnabled(enabled: Boolean)
    suspend fun setBiometricEnabled(enabled: Boolean)
    suspend fun setPinCode(pin: String)
}
