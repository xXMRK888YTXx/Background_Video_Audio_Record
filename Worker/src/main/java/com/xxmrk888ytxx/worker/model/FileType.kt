package com.xxmrk888ytxx.worker.model

enum class FileType(val id: Int, val mineType: String) {
    VIDEO(0,"video/mp4"),
    AUDIO(1,"audio/mp3");

    companion object {
        fun fromId(id: Int): FileType {
            return FileType.entries.find { it.id == id }
                ?: throw IllegalArgumentException("Unknown id: $id")

        }
    }
}