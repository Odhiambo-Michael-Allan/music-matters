package com.squad.musicmatters.core.model

data class SongMetadata(
    val songId: String,
    val codec: String,
    val bitsPerSample: Long,
    val bitrate: Long,
    val samplingRate: Float,
    val genreName: String
)
