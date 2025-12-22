package com.squad.musicmatters.core.model

enum class LoopMode {
    None,
    Song,
    Queue;

    companion object {
        val all = entries.toTypedArray()
    }
}
