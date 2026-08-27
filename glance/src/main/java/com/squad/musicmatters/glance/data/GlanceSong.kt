package com.squad.musicmatters.glance.data

data class GlanceSong(
    val id: String,
    val mediaStoreId: Long,
    val title: String,
    val artist: String,
    val artworkUri: String? = null,
)
