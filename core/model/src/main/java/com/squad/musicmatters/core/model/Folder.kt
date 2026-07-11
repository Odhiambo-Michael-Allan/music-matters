package com.squad.musicmatters.core.model

data class Folder(
    val path: String,
    val artworkUri: String? = null,
    val trackCount: Int,
)