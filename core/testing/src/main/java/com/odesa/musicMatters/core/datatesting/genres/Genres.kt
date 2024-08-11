package com.odesa.musicMatters.core.datatesting.genres

import com.odesa.musicMatters.core.datatesting.songs.testSongs
import com.odesa.musicMatters.core.model.Genre

val testGenres = listOf(
    Genre(
        name = "Hip Hop",
        numberOfTracks = 50,
        songsInGenre = testSongs
    ),
    Genre(
        name = "Pop",
        numberOfTracks = 35,
        songsInGenre = testSongs
    ),
    Genre(
        name = "Rock",
        numberOfTracks = 37,
        songsInGenre = testSongs
    )
)