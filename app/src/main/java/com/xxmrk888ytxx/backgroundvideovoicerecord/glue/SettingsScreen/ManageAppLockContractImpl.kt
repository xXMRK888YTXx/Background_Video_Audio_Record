package com.xxmrk888ytxx.backgroundvideovoicerecord.glue.SettingsScreen

import android.content.Context
import com.xxmrk888ytxx.backgroundvideovoicerecord.domain.appLock.AppLockManager
import com.xxmrk888ytxx.settingsscreen.contracts.ManageAppLockContract
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ManageAppLockContractImpl @Inject constructor(
    private val appLockManager: AppLockManager,
    private val context: Context
) : ManageAppLockContract {
    override val isAppLockEnabled: Flow<Boolean> = appLockManager.isAppLockEnabled
    override val isBiometricEnabled: Flow<Boolean> = appLockManager.isBiometricEnabled
    override val isPinCodeSet: Flow<Boolean> = appLockManager.isPinCodeSet
    override val isBiometricsSupported: Boolean
        get() = appLockManager.canAuthenticateWithBiometrics(context)

    override suspend fun setAppLockEnabled(enabled: Boolean) {
        appLockManager.setAppLockEnabled(enabled)
    }

    override suspend fun setBiometricEnabled(enabled: Boolean) {
        appLockManager.setBiometricEnabled(enabled)
    }

    override suspend fun setPinCode(pin: String) {
        appLockManager.setPinCode(pin)
    }
}
