package com.odesa.musicMatters.core.datatesting.artists

import android.net.Uri
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import com.odesa.musicMatters.core.common.media.extensions.toArtist
import com.odesa.musicMatters.core.datatesting.albums.testAlbums
import com.odesa.musicMatters.core.datatesting.songs.testSongs
import com.odesa.musicMatters.core.model.Artist

val testArtistsForSorting = listOf(
    Artist(
        name = "Jemand",
        artworkUri = Uri.EMPTY,
        albumCount = 5,
        trackCount = 32
    ),
    Artist(
        name = "7 Developers and a Pastry Chef",
        artworkUri = Uri.EMPTY,
        albumCount = 2,
        trackCount = 23
    ),
    Artist(
        name = "Michael Jackson",
        artworkUri = Uri.EMPTY,
        albumCount = 84,
        trackCount = 343
    ),
    Artist(
        name = "Public Enemy",
        artworkUri = Uri.EMPTY,
        albumCount = 8,
        trackCount = 53
    )
)

val testArtistMediaItems: List<MediaItem> = listOf(
    MediaItem.Builder().apply {
        setMediaMetadata(
            MediaMetadata.Builder().apply {
                setTitle( "Jemand" )
                setArtworkUri( Uri.EMPTY )
            }.build()
        )
    }.build(),
    MediaItem.Builder().apply {
        setMediaMetadata(
            MediaMetadata.Builder().apply {
                setTitle( "Michael Jackson" )
                setArtworkUri( Uri.EMPTY )
            }.build()
        )
    }.build(),
    MediaItem.Builder().apply {
        setMediaMetadata(
            MediaMetadata.Builder().apply {
                setTitle( "The Stooges" )
                setArtworkUri( Uri.EMPTY )
            }.build()
        )
    }.build(),
    MediaItem.Builder().apply {
        setMediaMetadata(
            MediaMetadata.Builder().apply {
                setTitle( "Public Enemy" )
            }.build()
        )
    }.build(),
    MediaItem.Builder().apply {
        setMediaMetadata(
            MediaMetadata.Builder().apply {
                setTitle( "7 Developers and a Pastry Chef" )
            }.build()
        )
    }.build()
)

val testArtists = testArtistMediaItems.map { it.toArtist( testSongs, testAlbums ) }