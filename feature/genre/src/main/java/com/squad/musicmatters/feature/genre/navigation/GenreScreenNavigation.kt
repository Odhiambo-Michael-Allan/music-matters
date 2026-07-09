package com.squad.musicmatters.feature.genre.navigation

import kotlinx.serialization.Serializable

@Serializable data class GenreRoute(
    // The name of the genre to be displayed at this destination
    val genreName: String,
)