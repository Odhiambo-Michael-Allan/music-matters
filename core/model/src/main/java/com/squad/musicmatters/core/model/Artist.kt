package com.squad.musicmatters.core.model

data class Artist(
    val id: Long,
    val name: String,
    val artworkUri: String?,
    val trackCount: Int,
)
