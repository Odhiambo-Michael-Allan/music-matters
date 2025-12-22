package com.squad.musicmatters.core.model

data class Genre(
    val name: String,
    val numberOfTracks: Int,
    val songsInGenre: List<Song> = emptyList()
)

