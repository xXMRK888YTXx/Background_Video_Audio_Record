package com.xxmrk888ytxx.backgroundvideovoicerecord.domain.hardwareTrigger

import android.accessibilityservice.AccessibilityService
import android.util.Log
import android.view.KeyEvent
import android.view.accessibility.AccessibilityEvent
import com.xxmrk888ytxx.coredeps.DepsProvider.getDepsByApplication
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class HardwareTriggerAccessibilityService : AccessibilityService() {

    private val hardwareTriggerManager: HardwareTriggerManager by lazy {
        getDepsByApplication()
    }

    private val volumePressTimestamps = mutableListOf<Long>()

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {}

    override fun onInterrupt() {}

    override fun onKeyEvent(event: KeyEvent): Boolean {
        if (event.keyCode == KeyEvent.KEYCODE_VOLUME_DOWN && event.action == KeyEvent.ACTION_DOWN) {
            val now = System.currentTimeMillis()
            // Clean older timestamps (> 1500ms)
            volumePressTimestamps.removeAll { now - it > 1500L }
            volumePressTimestamps.add(now)

            if (volumePressTimestamps.size >= 3) {
                volumePressTimestamps.clear()
                CoroutineScope(Dispatchers.Main).launch {
                    try {
                        if (hardwareTriggerManager.isVolumeTriggerEnabled.first()) {
                            Log.i("HardwareTrigger", "Triple Volume Down pressed! Toggling background recording.")
                            hardwareTriggerManager.toggleRecording(applicationContext)
                        }
                    } catch (e: Exception) {
                        Log.e("HardwareTrigger", "Failed to trigger recording on volume key", e)
                    }
                }
            }
        }
        return super.onKeyEvent(event)
    }
}
