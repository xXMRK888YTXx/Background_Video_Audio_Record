package com.xxmrk888ytxx.backgroundvideovoicerecord.domain.appLock

import android.content.Context
import androidx.biometric.BiometricManager
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.xxmrk888ytxx.backgroundvideovoicerecord.di.AppScope
import com.xxmrk888ytxx.preferencesstorage.PreferencesStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.security.MessageDigest
import javax.inject.Inject

@AppScope
class AppLockManagerImpl @Inject constructor(
    private val preferencesStorage: PreferencesStorage
) : AppLockManager {

    private val isAppLockEnabledKey = booleanPreferencesKey("isAppLockEnabledKey")
    private val isBiometricEnabledKey = booleanPreferencesKey("isBiometricEnabledKey")
    private val pinCodeHashKey = stringPreferencesKey("pinCodeHashKey")

    private val _isSessionUnlocked = kotlinx.coroutines.flow.MutableStateFlow(false)
    override val isSessionUnlocked: Flow<Boolean> = _isSessionUnlocked

    override fun unlockSession() {
        _isSessionUnlocked.value = true
    }

    override fun lockSession() {
        _isSessionUnlocked.value = false
    }

    override val isAppLockEnabled: Flow<Boolean> =
        preferencesStorage.getProperty(isAppLockEnabledKey, false)

    override val isBiometricEnabled: Flow<Boolean> =
        preferencesStorage.getProperty(isBiometricEnabledKey, false)

    override val isPinCodeSet: Flow<Boolean> =
        preferencesStorage.getProperty(pinCodeHashKey, "").map { it.isNotEmpty() }

    override suspend fun setAppLockEnabled(enabled: Boolean) = withContext(Dispatchers.IO) {
        preferencesStorage.writeProperty(isAppLockEnabledKey, enabled)
    }

    override suspend fun setBiometricEnabled(enabled: Boolean) = withContext(Dispatchers.IO) {
        preferencesStorage.writeProperty(isBiometricEnabledKey, enabled)
    }

    override suspend fun setPinCode(pin: String) = withContext(Dispatchers.IO) {
        val hash = hashPin(pin)
        preferencesStorage.writeProperty(pinCodeHashKey, hash)
    }

    override suspend fun verifyPinCode(pin: String): Boolean = withContext(Dispatchers.IO) {
        val storedHash = preferencesStorage.getProperty(pinCodeHashKey, "").first()
        if (storedHash.isEmpty()) return@withContext true
        val incomingHash = hashPin(pin)
        return@withContext storedHash == incomingHash
    }

    override fun canAuthenticateWithBiometrics(context: Context): Boolean {
        return try {
            val biometricManager = BiometricManager.from(context)
            val canAuth = biometricManager.canAuthenticate(
                BiometricManager.Authenticators.BIOMETRIC_STRONG or BiometricManager.Authenticators.BIOMETRIC_WEAK
            )
            canAuth == BiometricManager.BIOMETRIC_SUCCESS
        } catch (_: Exception) {
            false
        }
    }

    private fun hashPin(pin: String): String {
        val salt = "FaradaySec_2026_Salt"
        val bytes = MessageDigest.getInstance("SHA-256").digest((pin + salt).toByteArray())
        return bytes.joinToString("") { "%02x".format(it) }
    }
}
