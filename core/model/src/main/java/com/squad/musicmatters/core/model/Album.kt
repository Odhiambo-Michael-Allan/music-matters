package com.squad.musicmatters.core.model


data class Album(
    val title: String,
    val artists: Set<String>,
    val trackCount: Int,
    val artworkUri: String?
)
