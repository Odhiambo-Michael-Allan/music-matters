package com.squad.musicmatters.core.testing.playlists

import com.squad.musicmatters.core.model.Playlist
import java.util.UUID

val testPlaylists = List( 20 ) {
    Playlist(
        id = UUID.randomUUID().toString() + "$it",
        title = "Playlist-$it",
        songIds = emptySet()
    )
}.toMutableList()

val testPlaylistsForSorting = List( 20 ) {
    Playlist(
        id = UUID.randomUUID().toString() + "$it",
        title = "Playlist-$it",
        songIds = List( it ) {
            UUID.randomUUID().toString()
        }.toSet()
    )
}.toMutableList()
