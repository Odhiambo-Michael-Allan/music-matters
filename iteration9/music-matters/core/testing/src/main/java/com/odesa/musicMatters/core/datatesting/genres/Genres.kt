package com.odesa.musicMatters.core.datatesting.genres

import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import com.odesa.musicMatters.core.common.media.extensions.toGenre
import com.odesa.musicMatters.core.datatesting.songs.testSongs
import java.util.UUID

val testGenreMediaItems: List<MediaItem> = listOf(
    MediaItem.Builder().apply {
        setMediaId( UUID.randomUUID().toString() ).setMediaMetadata(
            MediaMetadata.Builder().apply {
                setTitle( "Hip Hop" )
            }.build()
        )
    }.build(),
    MediaItem.Builder().apply {
        setMediaId( UUID.randomUUID().toString() ).setMediaMetadata(
            MediaMetadata.Builder().apply {
                setTitle( "Pop" )
            }.build()
        )
    }.build(),
    MediaItem.Builder().apply {
        setMediaId( UUID.randomUUID().toString() ).setMediaMetadata(
            MediaMetadata.Builder().apply {
                setTitle( "Rock" )
            }.build()
        )
    }.build(),
)

val testGenres = testGenreMediaItems.map { it.toGenre( testSongs ) }