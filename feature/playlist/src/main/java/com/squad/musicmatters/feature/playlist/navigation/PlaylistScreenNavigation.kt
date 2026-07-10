package com.squad.musicmatters.feature.playlist.navigation

import kotlinx.serialization.Serializable

@Serializable data class PlaylistRoute(
    // The id of the playlist to be displayed at this destination
    val playlistId: String
)