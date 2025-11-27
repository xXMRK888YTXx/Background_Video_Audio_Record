package com.xxmrk888ytxx.worker.model

enum class FileType(val id: Int) {
    VIDEO(0),
    AUDIO(1);

    companion object {
        fun fromId(id: Int): FileType {
            return FileType.entries.find { it.id == id }
                ?: throw IllegalArgumentException("Unknown id: $id")

        }
    }
}