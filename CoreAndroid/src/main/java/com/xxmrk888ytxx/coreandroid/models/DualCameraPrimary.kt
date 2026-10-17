package com.xxmrk888ytxx.coreandroid.models

enum class DualCameraPrimary(val id: Int) {
    BACK(0),
    FRONT(1);

    companion object {
        fun fromId(id: Int): DualCameraPrimary = entries.firstOrNull { it.id == id } ?: BACK
    }
}
