package com.odesa.musicMatters.core.common.connection

import android.os.Bundle
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.PlaybackParameters
import androidx.media3.common.Player
import com.odesa.musicMatters.core.common.media.CUSTOM_COMMAND_DELETE_SONG
import com.odesa.musicMatters.core.common.media.extensions.artistTagSeparators
import com.odesa.musicMatters.core.common.media.extensions.move
import com.odesa.musicMatters.core.common.media.extensions.toAlbum
import com.odesa.musicMatters.core.common.media.extensions.toArtist
import com.odesa.musicMatters.core.common.media.extensions.toSong
import com.odesa.musicMatters.core.common.media.library.MUSIC_MATTERS_ALBUMS_ROOT
import com.odesa.musicMatters.core.common.media.library.MUSIC_MATTERS_ARTISTS_ROOT
import com.odesa.musicMatters.core.common.media.library.MUSIC_MATTERS_RECENT_SONGS_ROOT
import com.odesa.musicMatters.core.common.media.library.MUSIC_MATTERS_SUGGESTED_ALBUMS_ROOT
import com.odesa.musicMatters.core.common.media.library.MUSIC_MATTERS_SUGGESTED_ARTISTS_ROOT
import com.odesa.musicMatters.core.common.media.library.MUSIC_MATTERS_TRACKS_ROOT
import com.odesa.musicMatters.core.data.database.model.SongAdditionalMetadata
import com.odesa.musicMatters.core.data.repository.PlaylistRepository
import com.odesa.musicMatters.core.data.repository.SongsAdditionalMetadataRepository
import com.odesa.musicMatters.core.data.settings.SettingsRepository
import com.odesa.musicMatters.core.model.Album
import com.odesa.musicMatters.core.model.Artist
import com.odesa.musicMatters.core.model.Genre
import com.odesa.musicMatters.core.model.Song
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber

class MusicServiceConnectionImpl(
    private val connectable: Connectable,
    private val playlistRepository: PlaylistRepository,
    private val settingsRepository: SettingsRepository,
    private val songsAdditionalMetadataRepository: SongsAdditionalMetadataRepository,
    private val initialPlaybackParameters: PlaybackParameters,
    private val initialRepeatMode: @Player.RepeatMode Int,
    dispatcher: CoroutineDispatcher
) : MusicServiceConnection {

    private val scope = CoroutineScope( dispatcher + SupervisorJob() )

    private val _isInitializing = MutableStateFlow( true )
    override val isInitializing = _isInitializing.asStateFlow()

    private val _isLoadingGenres = MutableStateFlow( true )
    override val isLoadingGenres = _isLoadingGenres.asStateFlow()

    private val _nowPlayingMediaItem = MutableStateFlow( NOTHING_PLAYING )
    override val nowPlayingMediaItem = _nowPlayingMediaItem.asStateFlow()

    private val _playbackState = MutableStateFlow( EMPTY_PLAYBACK_STATE )
    override val playbackState = _playbackState.asStateFlow()

    private val _currentlyPlayingMediaItemIndex = MutableStateFlow( CURRENTLY_PLAYING_MEDIA_ITEM_INDEX_UNDEFINED )
    override val currentlyPlayingMediaItemIndex = _currentlyPlayingMediaItemIndex.asStateFlow()

    private val playerListener: PlayerListener = PlayerListener()
    private var player: Player? = null

    private var _mediaItemsInQueue = MutableStateFlow( emptyList<MediaItem>() )
        set( value ) {
            field = value
            saveCurrentQueue() // Whenever the queue changes, save it..
        }
    override val mediaItemsInQueue = _mediaItemsInQueue.asStateFlow()

    private val _cachedSongs = MutableStateFlow( emptyList<Song>() )
    override val cachedSongs = _cachedSongs.asStateFlow()

    private val _cachedGenres = MutableStateFlow( emptyList<Genre>() )
    override val cachedGenres = _cachedGenres.asStateFlow()

    private val _cachedRecentlyAddedSongs = MutableStateFlow( emptyList<Song>() )
    override val cachedRecentlyAddedSongs = _cachedRecentlyAddedSongs.asStateFlow()

    private val _cachedArtists = MutableStateFlow( emptyList<Artist>() )
    override val cachedArtists = _cachedArtists.asStateFlow()

    private val _cachedSuggestedArtists = MutableStateFlow( emptyList<Artist>() )
    override val cachedSuggestedArtists = _cachedSuggestedArtists.asStateFlow()

    private val _cachedAlbums = MutableStateFlow( emptyList<Album>() )
    override val cachedAlbums = _cachedAlbums.asStateFlow()

    private val _cachedSuggestedAlbums = MutableStateFlow( emptyList<Album>() )
    override val cachedSuggestedAlbums = _cachedSuggestedAlbums.asStateFlow()

    override val currentPlaybackPosition: Long
        get() = player?.currentPosition ?: 0L

    private var currentQueueSavingJob: Job? = null

    init {
        scope.launch {
            connectable.establishConnection()
            connectable.addDisconnectListener { release() }
            player = connectable.player
            player?.addListener( playerListener )
            player?.playbackParameters = initialPlaybackParameters
            player?.repeatMode = initialRepeatMode
            updateMediaCaches()
            updateMediaItemsInQueueWith( fetchPreviouslySavedQueue() )
            _nowPlayingMediaItem.value = fetchNowPlayingMediaItem()
            _currentlyPlayingMediaItemIndex.value = getCurrentMediaItemIndex()
            initializePlayer()
            _isInitializing.value = false
            observeSongsAdditionalMetadata()
        }
    }

    private fun initializePlayer() {
        player?.let {
            it.setMediaItems(
                mediaItemsInQueue.value,
                currentlyPlayingMediaItemIndex.value,
                C.TIME_UNSET
            )
            it.prepare()
        }
    }

    private suspend fun updateMediaCaches() {
        _cachedSongs.value = connectable.getChildren( MUSIC_MATTERS_TRACKS_ROOT )
            .map { it.toSong( artistTagSeparators ) }
        _cachedRecentlyAddedSongs.value = connectable.getChildren( MUSIC_MATTERS_RECENT_SONGS_ROOT )
            .map { it.toSong( artistTagSeparators ) }
        _cachedAlbums.value = connectable.getChildren( MUSIC_MATTERS_ALBUMS_ROOT )
            .map { it.toAlbum( cachedSongs.value ) }
        _cachedSuggestedAlbums.value = connectable.getChildren( MUSIC_MATTERS_SUGGESTED_ALBUMS_ROOT )
            .map { it.toAlbum( cachedSongs.value ) }
        _cachedArtists.value = connectable.getChildren( MUSIC_MATTERS_ARTISTS_ROOT )
            .map { it.toArtist( _cachedSongs.value, _cachedAlbums.value ) }
        _cachedSuggestedArtists.value = connectable.getChildren( MUSIC_MATTERS_SUGGESTED_ARTISTS_ROOT )
            .map { it.toArtist( _cachedSongs.value, _cachedAlbums.value ) }
    }

    private fun updateMediaItemsInQueueWith( newMediaItemsInQueue: List<MediaItem> ) {
        _mediaItemsInQueue.value = newMediaItemsInQueue
        saveCurrentQueue()
    }

    private suspend fun observeSongsAdditionalMetadata() {
        songsAdditionalMetadataRepository.fetchAdditionalMetadataEntries().collect {
            if ( it.size >= cachedSongs.value.size ) {
                // At this point, we are sure all additional metadata for each song has been loaded.
                _cachedGenres.value = constructGenresUsing( it )
                _isLoadingGenres.value = false
            }
        }
    }

    private fun constructGenresUsing(
        songsAdditionalMetadata: List<SongAdditionalMetadata>
    ): List<Genre> {
        val songsAdditionalMetadataGroupedByGenre = songsAdditionalMetadata.groupBy { it.genre }
        return songsAdditionalMetadataGroupedByGenre.map { ( genreName, songsInGenre ) ->
            Genre(
                name = genreName,
                songsInGenre = cachedSongs
                    .value
                    .filter { song -> songsInGenre.map { it.songId }.contains( song.id ) },
                numberOfTracks = songsInGenre.size
            )

        }
    }

    private fun fetchPreviouslySavedQueue(): List<MediaItem> {
        val songIdsInPreviouslySavedQueue = playlistRepository.currentPlayingQueuePlaylistInfo.value.songIds
        return _cachedSongs.value
            .filter { songIdsInPreviouslySavedQueue.contains( it.id ) }
            .map { it.mediaItem }
    }

    private fun fetchNowPlayingMediaItem(): MediaItem {
        val currentlyPlayingMediaItem = player?.currentMediaItem
            ?: _mediaItemsInQueue.value
                .find { it.mediaId == settingsRepository.currentlyPlayingSongId.value }
            ?: NOTHING_PLAYING
        Timber.tag( TAG ).d( "NOW PLAYING MEDIA ITEM TITLE: ${currentlyPlayingMediaItem.mediaMetadata.title}" )
        Timber.tag( TAG ).d( "NUMBER OF MEDIA ITEMS IN QUEUE: ${mediaItemsInQueue.value.size}" )
        return currentlyPlayingMediaItem
    }

    private fun getCurrentMediaItemIndex() = mediaItemsInQueue.value.indexOf( nowPlayingMediaItem.value )

    override fun deleteSong( song: Song ) {
        scope.launch {
            _isInitializing.value = true
            sendDeleteCommandToMediaSessionUsing( song )
            updateNowPlayingMediaItemUsing( song )
            updatePlaylistRepositoryUsing( song )
            updateSongsAdditionalMetadataRepositoryUsing( song )
            updateMediaItemsInQueueAfterDeleting( song )
            updateMediaCaches()
            _isInitializing.value = false
        }
    }

    private suspend fun sendDeleteCommandToMediaSessionUsing( song: Song ) {
        connectable.sendCustomCommand(
            command = CUSTOM_COMMAND_DELETE_SONG,
            parameters = Bundle()
                .apply {
                    putString( SONG_ID_KEY, song.id )
                },
        )
    }

    private fun updateNowPlayingMediaItemUsing( song: Song ) {
        if ( nowPlayingMediaItem.value.mediaId == song.mediaItem.mediaId ) {
            if ( song.mediaItem.mediaId == mediaItemsInQueue.value.last().mediaId ) {
                player?.stop()
                player?.clearMediaItems()
                _nowPlayingMediaItem.value = NOTHING_PLAYING
            } else {
                playNextSong()
            }
        }
    }

    private suspend fun updatePlaylistRepositoryUsing( song: Song ) {
        withContext( Dispatchers.IO ) {
            playlistRepository.apply {
                playlists.value.forEach {
                    removeSongIdFromPlaylist( song.id, it.id )
                }
                removeSongIdFromMostPlayedPlaylist( song.id )
            }
        }
    }

    private suspend fun updateSongsAdditionalMetadataRepositoryUsing( song: Song ) {
        withContext( Dispatchers.IO ) {
            songsAdditionalMetadataRepository.deleteEntryWithId( song.id )
        }
    }

    private fun updateMediaItemsInQueueAfterDeleting( song: Song ) {
        val mediaItemsInQueueCopy = _mediaItemsInQueue.value.toMutableList()
        mediaItemsInQueueCopy.removeIf { it.mediaId == song.mediaItem.mediaId }
        updateMediaItemsInQueueWith( mediaItemsInQueueCopy )
    }

    override fun addToQueue( mediaItem: MediaItem ) {
        if ( mediaItem.isPresentIn( _mediaItemsInQueue.value ) ) return // NO DUPLICATES!
        if ( queueIsEmpty() ) {
            playMediaItem(
                mediaItem = mediaItem,
                mediaItems = listOf( mediaItem ),
                shuffle = false
            )
        } else {
            addMediaItemToQueue(
                mediaItem = mediaItem,
                position = _mediaItemsInQueue.value.size
            )
        }
    }

    override fun playNext( mediaItem: MediaItem ) {

        if ( nowPlayingMediaItem.value.mediaId == mediaItem.mediaId ) return
        if ( queueIsEmpty() ) {
            playMediaItem(
                mediaItem = mediaItem,
                mediaItems = listOf( mediaItem ),
                shuffle = false
            )
        } else {
            addMediaItemToQueue(
                mediaItem = mediaItem,
                position = getPositionToPlaceMediaItemToPlayNext( mediaItem )
            )
        }
    }

    private fun getPositionToPlaceMediaItemToPlayNext( mediaItem: MediaItem ) =
        if ( mediaItem.isPresentIn( _mediaItemsInQueue.value ) &&
            _currentlyPlayingMediaItemIndex.value > _mediaItemsInQueue.value.indexOf( mediaItem )
            ) _currentlyPlayingMediaItemIndex.value
        else _currentlyPlayingMediaItemIndex.value + 1


    override fun shuffleAndPlay( mediaItems: List<MediaItem> ) {
        playMediaItem(
            mediaItem = mediaItems.random(),
            mediaItems = mediaItems,
            shuffle = true
        )
    }

    private fun queueIsEmpty() = _mediaItemsInQueue.value.isEmpty()

    private fun addMediaItemToQueue(
        mediaItem: MediaItem,
        position: Int
    ) {
        Timber.tag( TAG ).d( "ADDING MEDIA ITEM TO QUEUE TITLED: ${mediaItem.mediaMetadata.title}" )
        if ( mediaItem.isPresentIn( _mediaItemsInQueue.value ) ) {
            val currentPositionOfMediaItemInQueue = _mediaItemsInQueue.value.indexOf( mediaItem )
            moveMediaItem( currentPositionOfMediaItemInQueue, position )
        } else {
            player?.addMediaItem( position, mediaItem )
            val mediaItemsInQueueCopy = _mediaItemsInQueue.value.toMutableList()
            mediaItemsInQueueCopy.add( position, mediaItem )
            updateMediaItemsInQueueWith( mediaItemsInQueueCopy )
        }
    }

    override fun playMediaItem(
        mediaItem: MediaItem,
        mediaItems: List<MediaItem>,
        shuffle: Boolean
    ) {
        player?.let { player ->
            val mediaItemsCopy = mediaItems.toMutableList()
            if ( shuffle ) {
                mediaItemsCopy.apply {
                    remove( mediaItem )
                    shuffle()
                    add( 0, mediaItem )
                }
            }
            val indexOfSelectedMediaItem = mediaItemsCopy.indexOf( mediaItem )
            updateMediaItemsInQueueWith( mediaItemsCopy )
            player.setMediaItems( mediaItemsCopy, indexOfSelectedMediaItem, C.TIME_UNSET )
            player.prepare()
            player.play()
            _currentlyPlayingMediaItemIndex.value = indexOfSelectedMediaItem
            Timber.tag( TAG ).d( "PLAYING MEDIA ITEM TITLED: ${mediaItem.mediaMetadata.title}" )
            Timber.tag( TAG ).d( "CURRENTLY PLAYING MEDIA ITEM INDEX: ${_currentlyPlayingMediaItemIndex.value}" )
        }
    }

    override fun shuffleSongsInQueue() {
        player?.let {
            val newMediaItemsInQueue = _mediaItemsInQueue.value
                .filter { mediaItem -> mediaItem.mediaId != nowPlayingMediaItem.value.mediaId }
                .shuffled().toMutableList()
            it.moveMediaItem( it.currentMediaItemIndex, 0 )
            it.replaceMediaItems( 1, newMediaItemsInQueue.size, newMediaItemsInQueue )
            newMediaItemsInQueue.add( 0, nowPlayingMediaItem.value )
            updateMediaItemsInQueueWith( newMediaItemsInQueue )
        }
    }

    override fun moveMediaItem( from: Int, to: Int ) {
        player?.let {
            it.moveMediaItem( from, to )
            _currentlyPlayingMediaItemIndex.value = it.currentMediaItemIndex
            val newQueue = _mediaItemsInQueue.value.toMutableList()
            newQueue.move( from, to )
            updateMediaItemsInQueueWith( newQueue )
        }
    }

    override fun playNextSong(): Boolean {
        player?.let {
            it.seekToNext()
            return true
        }
        return false
    }

    override fun clearQueue() {
        player?.let {
            it.clearMediaItems()
            updateMediaItemsInQueueWith( emptyList() )
            _nowPlayingMediaItem.value = MediaItem.EMPTY
            _currentlyPlayingMediaItemIndex.value = CURRENTLY_PLAYING_MEDIA_ITEM_INDEX_UNDEFINED
        }
    }

    override fun setPlaybackSpeed( playbackSpeed: Float ) {
        player?.setPlaybackSpeed( playbackSpeed )
    }

    override fun setPlaybackPitch( playbackPitch: Float ) {
        player?.setPlaybackPitch( playbackPitch )
    }

    override fun setRepeatMode( @Player.RepeatMode repeatMode: Int ) {
        player?.repeatMode = repeatMode
    }

    override fun playPause() {
        player?.let {
            if ( it.isPlaying ) it.pause() else it.play()
        }
    }

    override fun playPreviousSong(): Boolean {
        player?.let {
            it.seekToPrevious()
            return true
        }
        return false
    }

    override fun seekBack() {
        player?.seekBack()
    }

    override fun seekForward() {
        player?.seekForward()
    }

    override fun seekTo( position: Long ) {
        player?.seekTo( position )
    }

    private fun release() {
        _nowPlayingMediaItem.value = NOTHING_PLAYING
        scope.cancel( message = "CONNECTION TO CONNECTABLE UNAVAILABLE" )
        currentQueueSavingJob?.cancel()
        instance = null
    }

    private fun saveCurrentQueue() {
        currentQueueSavingJob?.cancel()
        currentQueueSavingJob = scope.launch {
            Timber.tag( TAG ).d( "SAVING CURRENT QUEUE.." )
            playlistRepository.clearCurrentPlayingQueuePlaylist()
            playlistRepository.saveCurrentlyPlayingQueue( mediaItemsInQueue.value.map{ it.mediaId } )
        }
    }

    companion object {
        @Volatile
        private var instance: MusicServiceConnection? = null

        fun getInstance(
            connectable: Connectable,
            playlistRepository: PlaylistRepository,
            settingsRepository: SettingsRepository,
            songsAdditionalMetadataRepository: SongsAdditionalMetadataRepository,
            dispatcher: CoroutineDispatcher,
            playbackParameters: PlaybackParameters,
            repeatMode: Int,
        ) =
            instance ?: synchronized( this ) {
                instance ?: MusicServiceConnectionImpl(
                    connectable = connectable,
                    playlistRepository = playlistRepository,
                    settingsRepository = settingsRepository,
                    songsAdditionalMetadataRepository = songsAdditionalMetadataRepository,
                    dispatcher = dispatcher,
                    initialPlaybackParameters = playbackParameters,
                    initialRepeatMode = repeatMode,
                )
            }
    }

    private inner class PlayerListener : Player.Listener {
        override fun onEvents( player: Player, events: Player.Events ) {
            if ( events.contains( Player.EVENT_PLAY_WHEN_READY_CHANGED )
                || events.contains( Player.EVENT_PLAYBACK_STATE_CHANGED )
                || events.contains( Player.EVENT_MEDIA_ITEM_TRANSITION )
                || events.contains( Player.EVENT_PLAYLIST_METADATA_CHANGED )
                || events.contains( Player.EVENT_MEDIA_METADATA_CHANGED )
                ) {
                updateNowPlaying( player )
                updatePlaybackState( player )
                updateCurrentlyPlayingMediaItemIndex( player )
            }
        }

        override fun onIsPlayingChanged( isPlaying: Boolean ) {}

        override fun onPlayerErrorChanged( error: PlaybackException? ) {
            when ( error?.errorCode ) {
                PlaybackException.ERROR_CODE_IO_BAD_HTTP_STATUS,
                PlaybackException.ERROR_CODE_IO_INVALID_HTTP_CONTENT_TYPE,
                PlaybackException.ERROR_CODE_IO_CLEARTEXT_NOT_PERMITTED,
                PlaybackException.ERROR_CODE_IO_NETWORK_CONNECTION_FAILED,
                PlaybackException.ERROR_CODE_IO_NETWORK_CONNECTION_TIMEOUT -> {}
            }
        }

        private fun updatePlaybackState( player: Player ) {
            _playbackState.value = PlaybackState(
                playbackState = player.playbackState,
                playWhenReady = player.playWhenReady,
                duration = player.duration
            )
        }

        private fun updateCurrentlyPlayingMediaItemIndex( player: Player ) {
            _currentlyPlayingMediaItemIndex.value = player.currentMediaItemIndex
            Timber.tag( TAG ).d( "UPDATING CURRENTLY PLAYING MEDIA ITEM INDEX TO: ${player.currentMediaItemIndex}" )
        }

        private fun updateNowPlaying( player: Player ) {
            var mediaItem = player.currentMediaItem ?: NOTHING_PLAYING
            Timber.tag( TAG ).d( "UPDATING NOW PLAYING MEDIA ITEM TO: ${mediaItem.mediaMetadata.title}.." )
            if ( mediaItem != NOTHING_PLAYING ) {
                // The current media item from the CastPlayer may have lost some information
                mediaItem = cachedSongs.value.find { it.mediaItem.mediaId == mediaItem.mediaId }!!.mediaItem
            }
            _nowPlayingMediaItem.value = mediaItem
        }
    }
}

class PlaybackState(
    private val playbackState: Int = Player.STATE_IDLE,
    private val playWhenReady: Boolean = false,
    val duration: Long = C.TIME_UNSET
) {
    val isPlaying: Boolean
        get() {
            return ( playbackState == Player.STATE_BUFFERING
                    || playbackState == Player.STATE_READY )
                    && playWhenReady
        }
}

private fun MediaItem.isPresentIn( mediaItemList: List<MediaItem> ) =
    mediaItemList.contains( this )

private fun Player.setPlaybackPitch( pitch: Float ) {
    playbackParameters = PlaybackParameters(
        playbackParameters.speed,
        pitch
    )
}

const val CURRENTLY_PLAYING_MEDIA_ITEM_INDEX_UNDEFINED = -1
val EMPTY_PLAYBACK_STATE: PlaybackState = PlaybackState()
val NOTHING_PLAYING = MediaItem.EMPTY
const val SONG_ID_KEY = "--SONG-ID-KEY--"
private const val TAG = "MUSIC-SERVICE-CONNECTION-TAG"
