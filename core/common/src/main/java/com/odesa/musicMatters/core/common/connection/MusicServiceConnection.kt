package com.odesa.musicMatters.core.common.connection

import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import com.odesa.musicMatters.core.model.Album
import com.odesa.musicMatters.core.model.Artist
import com.odesa.musicMatters.core.model.Genre
import com.odesa.musicMatters.core.model.Song
import kotlinx.coroutines.flow.StateFlow

interface MusicServiceConnection {
    val isInitializing: StateFlow<Boolean>
    val isLoadingGenres: StateFlow<Boolean>
    val nowPlayingMediaItem: StateFlow<MediaItem>
    val playbackState: StateFlow<PlaybackState>
    val currentlyPlayingMediaItemIndex: StateFlow<Int>
    val mediaItemsInQueue: StateFlow<List<MediaItem>>
    val cachedSongs: StateFlow<List<Song>>
    val cachedGenres: StateFlow<List<Genre>>
    val cachedRecentlyAddedSongs: StateFlow<List<Song>>
    val cachedArtists: StateFlow<List<Artist>>
    val cachedSuggestedArtists: StateFlow<List<Artist>>
    val cachedAlbums: StateFlow<List<Album>>
    val cachedSuggestedAlbums: StateFlow<List<Album>>
    val currentPlaybackPosition: Long
    fun deleteSong( song: Song )
    fun playPause()
    fun play()
    fun playPreviousSong(): Boolean
    fun playNextSong(): Boolean
    fun seekBack()
    fun seekForward()
    fun seekTo( position: Long )
    fun playMediaItem(
        mediaItem: MediaItem,
        mediaItems: List<MediaItem>,
        shuffle: Boolean,
    )
    fun shuffleAndPlay( mediaItems: List<MediaItem> )
    fun setPlaybackSpeed( playbackSpeed: Float )
    fun setPlaybackPitch( playbackPitch: Float )
    fun setRepeatMode( @Player.RepeatMode repeatMode: Int )
    fun shuffleSongsInQueue()
    fun moveMediaItem( from: Int, to: Int )
    fun clearQueue()
    fun playNext( mediaItem: MediaItem )
    fun addToQueue( mediaItem: MediaItem )
}