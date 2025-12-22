package com.squad.musicmatters.core.model

data class Artist(
    val name: String,
    val artworkUri: String?,
    val albumCount: Int = 0,
    val trackCount: Int = 0,
)
