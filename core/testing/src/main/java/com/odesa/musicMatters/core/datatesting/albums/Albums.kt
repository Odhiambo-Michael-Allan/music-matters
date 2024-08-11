package com.odesa.musicMatters.core.datatesting.albums

import android.net.Uri
import android.os.Bundle
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import com.odesa.musicMatters.core.common.media.extensions.ARTIST_KEY
import com.odesa.musicMatters.core.common.media.extensions.toAlbum
import com.odesa.musicMatters.core.datatesting.songs.testSongs

val testAlbumMediaItems = listOf(
    MediaItem.Builder().apply {
        setMediaMetadata(
            MediaMetadata.Builder().apply {
                setTitle( "Speechless" )
                setArtworkUri( Uri.EMPTY )
                setExtras(
                    Bundle().apply {
                        putString( ARTIST_KEY, "Jemand" )
                    }
                )
            }.build()
        )
    }.build(),
    MediaItem.Builder().apply {
        setMediaMetadata(
            MediaMetadata.Builder().apply {
                setTitle( "Tales from the Render Farm" )
                setArtworkUri( Uri.EMPTY )
                setExtras(
                    Bundle().apply {
                        putString( ARTIST_KEY, "7 Developers and a Pastry Chef" )
                    }
                )
            }.build()
        )
    }.build(),
    MediaItem.Builder().apply {
        setMediaMetadata(
            MediaMetadata.Builder().apply {
                setTitle( "Thriller" )
                setArtworkUri( Uri.EMPTY )
                setExtras(
                    Bundle().apply {
                        putString( ARTIST_KEY, "Michael Jackson" )
                    }
                )
            }.build()
        )
    }.build(),
    MediaItem.Builder().apply {
        setMediaMetadata(
            MediaMetadata.Builder().apply {
                setTitle( "the Stooges" )
                setExtras(
                    Bundle().apply {
                        putString( ARTIST_KEY, "The Stooges" )
                    }
                )
            }.build()
        )
    }.build(),
    MediaItem.Builder().apply {
        setMediaMetadata(
            MediaMetadata.Builder().apply {
                setTitle( "It Takes a Nation of Millions to Hold Us Back" )
                setExtras(
                    Bundle().apply {
                        putString( ARTIST_KEY, "Public Enemy" )
                    }
                )
            }.build()
        )
    }.build()
)

val testAlbums = testAlbumMediaItems.map { it.toAlbum( testSongs ) }