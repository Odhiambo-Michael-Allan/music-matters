package com.squad.musicmatters.core.model


data class Album(
    val id: Long,
    val title: String,
    val trackCount: Int,
    val artistId: Long,
    val artworkUri: String?,
    val artist: String? = null,
)
