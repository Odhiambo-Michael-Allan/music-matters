package com.squad.musicmatters.core.model


data class Album(
    val id: Long,
    val title: String,
    val trackCount: Int,
    val artworkUri: String?,
    val albumArtist: String? = null,
)
