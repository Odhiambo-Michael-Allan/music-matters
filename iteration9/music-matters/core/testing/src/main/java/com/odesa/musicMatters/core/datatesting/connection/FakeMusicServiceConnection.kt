package com.odesa.musicMatters.core.datatesting.connection

import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import com.odesa.musicMatters.core.common.connection.MusicServiceConnection
import com.odesa.musicMatters.core.common.connection.PlaybackState
import com.odesa.musicMatters.core.data.utils.subListNonStrict
import com.odesa.musicMatters.core.datatesting.albums.testAlbums
import com.odesa.musicMatters.core.datatesting.artists.testArtists
import com.odesa.musicMatters.core.datatesting.genres.testGenres
import com.odesa.musicMatters.core.datatesting.songs.testSongs
import com.odesa.musicMatters.core.model.Artist
import com.odesa.musicMatters.core.model.Song
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow


class FakeMusicServiceConnection : MusicServiceConnection {

    private val _nowPlaying = MutableStateFlow( MediaItem.EMPTY )
    override val nowPlayingMediaItem = _nowPlaying.asStateFlow()

    private val _playbackState = MutableStateFlow( PlaybackState() )
    override val playbackState = _playbackState.asStateFlow()

    private val _currentlyPlayingMediaItemIndex = MutableStateFlow( 0 )
    override val currentlyPlayingMediaItemIndex = _currentlyPlayingMediaItemIndex.asStateFlow()

    private val _mediaItemsInQueue = MutableStateFlow( emptyList<MediaItem>() )
    override val mediaItemsInQueue = _mediaItemsInQueue.asStateFlow()

    private val _cachedSongs = MutableStateFlow( testSongs )
    override val cachedSongs = _cachedSongs.asStateFlow()

    private val _cachedGenres = MutableStateFlow( testGenres )
    override val cachedGenres = _cachedGenres.asStateFlow()

    private val _cachedRecentlyAddedSongs = MutableStateFlow( testSongs.subListNonStrict( 5 ) )
    override val cachedRecentlyAddedSongs = _cachedRecentlyAddedSongs.asStateFlow()

    private val _cachedArtists = MutableStateFlow( testArtists )
    override val cachedArtists = _cachedArtists.asStateFlow()

    private val _cachedSuggestedArtists = MutableStateFlow( testArtists.subListNonStrict( 6 ) )
    override val cachedSuggestedArtists = _cachedSuggestedArtists.asStateFlow()

    private val _cachedAlbums = MutableStateFlow( testAlbums )
    override val cachedAlbums = _cachedAlbums.asStateFlow()

    private val _cachedSuggestedAlbums = MutableStateFlow( testAlbums.subListNonStrict( 6 ) )
    override val cachedSuggestedAlbums = _cachedSuggestedAlbums.asStateFlow()

    override val currentPlaybackPosition = 0L

    override fun playPause() {
        TODO("Not yet implemented")
    }

    override fun playPreviousSong(): Boolean {
        TODO("Not yet implemented")
    }

    override fun playNextSong(): Boolean {
        TODO("Not yet implemented")
    }

    override fun seekBack() {
        TODO("Not yet implemented")
    }

    override fun seekForward() {
        TODO("Not yet implemented")
    }

    override fun seekTo(position: Long) {
        TODO("Not yet implemented")
    }

    private val _isInitializing = MutableStateFlow( true )
    override var isInitializing = _isInitializing.asStateFlow()

    fun setIsInitialized() {
        _isInitializing.value = false
    }

    fun setSongs( songs: List<Song> ) {
        _cachedSongs.value = songs
    }

    fun setArtists( artists: List<Artist> ) {
        _cachedArtists.value = artists
    }

    override fun playMediaItem(
        mediaItem: MediaItem,
        mediaItems: List<MediaItem>,
        shuffle: Boolean ) {
        _mediaItemsInQueue.value = mediaItems
    }

    override fun shuffleAndPlay(mediaItems: List<MediaItem> ) {
        _mediaItemsInQueue.value = mediaItems
    }

    override fun setPlaybackSpeed( playbackSpeed: Float ) {}

    override fun setPlaybackPitch( playbackPitch: Float ) {}
    override fun setRepeatMode( repeatMode: Int ) {}
    override fun shuffleSongsInQueue() {}
    override fun moveMediaItem( from: Int, to: Int ) {}
    override fun clearQueue() {
        _mediaItemsInQueue.value = emptyList()
    }

    override fun playNext(mediaItem: MediaItem ) {
        TODO("Not yet implemented")
    }

    override fun addToQueue(mediaItem: MediaItem ) {
        TODO("Not yet implemented")
    }

    fun setNowPlaying( mediaItem: MediaItem ) {
        _nowPlaying.value = mediaItem
    }

    fun setPlaybackState( playbackState: PlaybackState ) {
        _playbackState.value = playbackState
    }

    fun setMediaItems( mediaItemList: List<MediaItem> ) {
        _mediaItemsInQueue.value = mediaItemList
    }

    fun setCurrentMediaItemIndex( index: Int ) {
        _currentlyPlayingMediaItemIndex.value = index
    }

    fun setIsPlaying( isPlaying: Boolean ) {
        when {
            isPlaying -> {
                _playbackState.value = PlaybackState(
                    playbackState = Player.STATE_READY,
                    playWhenReady = true,
                    duration = 0L
                )
            }
            else -> {
                _playbackState.value = PlaybackState()
            }
        }
    }

}












