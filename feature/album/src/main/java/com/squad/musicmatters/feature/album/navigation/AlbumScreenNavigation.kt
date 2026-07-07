package com.squad.musicmatters.feature.album.navigation

import kotlinx.serialization.Serializable

@Serializable data class AlbumRoute(
    // The id of the album to be displayed at this destination.
    val albumId: Long
)
