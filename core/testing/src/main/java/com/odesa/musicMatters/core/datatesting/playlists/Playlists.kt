package com.odesa.musicMatters.core.datatesting.playlists

import com.odesa.musicMatters.core.model.PlaylistInfo
import java.util.UUID

val testPlaylistInfos = List( 20 ) {
    PlaylistInfo(
        id = UUID.randomUUID().toString() + "$it",
        title = "Playlist-$it",
        songIds = emptyList()
    )
}.toMutableList()

val testPlaylistsForSorting = List( 20 ) {
    PlaylistInfo(
        id = UUID.randomUUID().toString() + "$it",
        title = "Playlist-$it",
        songIds = List( it ) {
            UUID.randomUUID().toString()
        }
    )
}.toMutableList()

val testPlaylistEntities = List( 20 ) {

}