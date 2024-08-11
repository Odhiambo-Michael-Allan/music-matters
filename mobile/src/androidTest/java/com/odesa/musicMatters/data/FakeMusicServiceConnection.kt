package com.odesa.musicMatters.data

//import androidx.media3.common.MediaItem
//import androidx.media3.common.Player
//import com.odesa.musicMatters.services.media.Song
//import com.odesa.musicMatters.services.media.connection.MusicServiceConnection
//import com.odesa.musicMatters.services.media.connection.NOTHING_PLAYING
//import com.odesa.musicMatters.services.media.connection.PlaybackState
//import kotlinx.coroutines.flow.MutableStateFlow
//import kotlinx.coroutines.flow.StateFlow
//import kotlinx.coroutines.flow.asStateFlow
//
//class FakeMusicServiceConnection : MusicServiceConnection {
//
//    private val _nowPlaying = MutableStateFlow( NOTHING_PLAYING )
//    override val nowPlaying = _nowPlaying.asStateFlow()
//
//    override val playbackState: StateFlow<PlaybackState>
//        get() = TODO("Not yet implemented")
//    override val currentlyPlayingMediaItemIndex: StateFlow<Int>
//        get() = TODO("Not yet implemented")
//    override val mediaIsCurrentlyPlaying: StateFlow<Boolean>
//        get() = TODO("Not yet implemented")
//    override val player: Player?
//        get() = TODO("Not yet implemented")
//    override val mediaItemsInQueue: StateFlow<List<MediaItem>>
//        get() = TODO("Not yet implemented")
//
//    override val cachedSongs: StateFlow<List<Song>> = MutableStateFlow( emptyList() )
//    override val cachedGenres: StateFlow<List<Genre>> = MutableStateFlow( emptyList() )
//    override val cachedRecentlyAddedSongs: StateFlow<List<Song>> = MutableStateFlow( emptyList() )
//    override val cachedArtists: StateFlow<List<Artist>> = MutableStateFlow( emptyList() )
//    override val cachedSuggestedArtists: StateFlow<List<Artist>> = MutableStateFlow( emptyList() )
//    override val cachedAlbums: StateFlow<List<Album>> = MutableStateFlow( emptyList() )
//    override val cachedSuggestedAlbums: StateFlow<List<Album>> = MutableStateFlow( emptyList() )
//    override var isInitializing: StateFlow<Boolean> = MutableStateFlow( true )
//
//    override suspend fun playMediaItem(
//        mediaItem: MediaItem,
//        mediaItems: List<MediaItem>,
//        shuffle: Boolean
//    ) {
//        TODO("Not yet implemented")
//    }
//
//    override suspend fun shuffleAndPlay( mediaItems: List<MediaItem> ) {
//        TODO("Not yet implemented")
//    }
//
//    override fun setPlaybackSpeed(playbackSpeed: Float) {
//        TODO("Not yet implemented")
//    }
//
//    override fun setPlaybackPitch(playbackPitch: Float) {
//        TODO("Not yet implemented")
//    }
//
//    override fun setRepeatMode(repeatMode: Int) {
//        TODO("Not yet implemented")
//    }
//
//    override fun shuffleSongsInQueue() {
//        TODO("Not yet implemented")
//    }
//
//    override fun moveMediaItem(from: Int, to: Int) {
//        TODO("Not yet implemented")
//    }
//
//    override fun clearQueue() {
//        TODO("Not yet implemented")
//    }
//
//    override fun mediaItemIsPresentInQueue(mediaItem: MediaItem): Boolean {
//        TODO("Not yet implemented")
//    }
//
//    override suspend fun playNext(mediaItem: MediaItem) {
//        TODO("Not yet implemented")
//    }
//
//    override suspend fun addToQueue(mediaItem: MediaItem) {
//        TODO("Not yet implemented")
//    }
//
//    override fun searchSongsMatching(query: String): List<Song> {
//        TODO("Not yet implemented")
//    }
//
//}