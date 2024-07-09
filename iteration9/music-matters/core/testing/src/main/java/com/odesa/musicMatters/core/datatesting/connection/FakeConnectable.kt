package com.odesa.musicMatters.core.datatesting.connection

import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import com.odesa.musicMatters.core.common.connection.Connectable
import com.odesa.musicMatters.core.common.media.library.MUSIC_MATTERS_ALBUMS_ROOT
import com.odesa.musicMatters.core.common.media.library.MUSIC_MATTERS_ARTISTS_ROOT
import com.odesa.musicMatters.core.common.media.library.MUSIC_MATTERS_GENRES_ROOT
import com.odesa.musicMatters.core.common.media.library.MUSIC_MATTERS_RECENT_SONGS_ROOT
import com.odesa.musicMatters.core.common.media.library.MUSIC_MATTERS_SUGGESTED_ALBUMS_ROOT
import com.odesa.musicMatters.core.common.media.library.MUSIC_MATTERS_SUGGESTED_ARTISTS_ROOT
import com.odesa.musicMatters.core.common.media.library.MUSIC_MATTERS_TRACKS_ROOT
import com.odesa.musicMatters.core.datatesting.albums.testAlbumMediaItems
import com.odesa.musicMatters.core.datatesting.artists.testArtistMediaItems
import com.odesa.musicMatters.core.datatesting.genres.testGenreMediaItems
import com.odesa.musicMatters.core.datatesting.media.FakePlayer
import com.odesa.musicMatters.core.datatesting.songs.testSongMediaItems

@UnstableApi
class FakeConnectable : Connectable {

    private val _player = FakePlayer()
    private val onDisconnectListeners: MutableList<()-> Unit> = mutableListOf()
    override val player = _player

    override suspend fun establishConnection() {}

    override suspend fun getChildren(parentId: String ): List<MediaItem> {
        return when ( parentId ) {
            MUSIC_MATTERS_TRACKS_ROOT -> testSongMediaItems
            MUSIC_MATTERS_ALBUMS_ROOT -> testAlbumMediaItems
            MUSIC_MATTERS_ARTISTS_ROOT -> testArtistMediaItems
            MUSIC_MATTERS_GENRES_ROOT -> testGenreMediaItems
            MUSIC_MATTERS_SUGGESTED_ALBUMS_ROOT -> testAlbumMediaItems
            MUSIC_MATTERS_SUGGESTED_ARTISTS_ROOT -> testArtistMediaItems
            MUSIC_MATTERS_RECENT_SONGS_ROOT -> testSongMediaItems
            else -> emptyList()
        }
    }

    override fun addDisconnectListener( disconnectListener: () -> Unit ) {
        onDisconnectListeners.add( disconnectListener )
    }
}