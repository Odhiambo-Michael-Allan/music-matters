package com.squad.musicmatters.core.model


data class PlaylistInfo(
    val id: String,
    val title: String,
    val songIds: Set<String>
)

