package com.squad.musicmatters.core.model

data class QueueEntry(
    val songId: String,
    val currentPositionInQueue: Int,
    val originalPositionInQueue: Int,
)
