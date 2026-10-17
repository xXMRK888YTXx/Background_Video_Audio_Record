package com.xxmrk888ytxx.coreandroid.models

enum class PipPosition(val id: Int) {
    TOP_RIGHT(0),
    TOP_LEFT(1),
    BOTTOM_RIGHT(2),
    BOTTOM_LEFT(3);

    companion object {
        fun fromId(id: Int): PipPosition = entries.firstOrNull { it.id == id } ?: TOP_RIGHT
    }
}
