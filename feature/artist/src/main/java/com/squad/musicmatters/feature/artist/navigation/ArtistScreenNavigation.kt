package com.squad.musicmatters.feature.artist.navigation

import kotlinx.serialization.Serializable

@Serializable data class ArtistRoute(
    // The id of the artist to be displayed at this destination
    val artistId: Long
)
