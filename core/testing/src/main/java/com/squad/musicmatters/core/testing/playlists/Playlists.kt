package com.squad.musicmatters.core.testing.playlists

import com.squad.musicmatters.core.model.PlaylistInfo
import java.util.UUID

val testPlaylistInfos = List( 20 ) {
    PlaylistInfo(
        id = UUID.randomUUID().toString() + "$it",
        title = "Playlist-$it",
        songIds = emptySet()
    )
}.toMutableList()

val testPlaylistsForSorting = List( 20 ) {
    PlaylistInfo(
        id = UUID.randomUUID().toString() + "$it",
        title = "Playlist-$it",
        songIds = List( it ) {
            UUID.randomUUID().toString()
        }.toSet()
    )
}.toMutableList()
