package com.squad.musicmatters.core.model


data class Playlist(
    val id: String,
    val title: String,
    val artworkUri: String? = null,
    val songIds: Set<String>
)

