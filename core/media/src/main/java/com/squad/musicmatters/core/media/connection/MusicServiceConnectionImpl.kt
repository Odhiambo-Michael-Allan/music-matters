package com.squad.musicmatters.core.media.connection

import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackParameters
import androidx.media3.common.Player
import com.squad.musicmatters.core.common.Dispatcher
import com.squad.musicmatters.core.common.MusicMattersDispatchers
import com.squad.musicmatters.core.data.repository.CompositeRepository
import com.squad.musicmatters.core.media.media.extensions.move
import com.squad.musicmatters.core.data.repository.QueueRepository
import com.squad.musicmatters.core.datastore.PreferencesDataSource
import com.squad.musicmatters.core.media.media.extensions.toMediaItem
import com.squad.musicmatters.core.model.LoopMode
import com.squad.musicmatters.core.model.Song
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.util.Collections
import javax.inject.Inject

class MusicServiceConnectionImpl @Inject constructor(
    private val connectable: Connectable,
    private val queueRepository: QueueRepository,
    private val userPreferencesDataSource: PreferencesDataSource,
    private val compositeRepository: CompositeRepository,
    private val songToMediaItemConverter: SongToMediaItemConverter,
    @Dispatcher( MusicMattersDispatchers.Main ) dispatcher: CoroutineDispatcher
) : MusicServiceConnection {

    private val scope = CoroutineScope( dispatcher + SupervisorJob() )
    private val playerListener: PlayerListener = PlayerListener()
    private var player: Player? = null

    private val _playerState = MutableStateFlow( PlayerState() )
    override val playerState = _playerState.asStateFlow()

    init {
        scope.launch {
            connectable.establishConnection()
            connectable.addDisconnectListener { scope.cancel() }
            player = connectable.player?.apply {
                initializePlayer( this )
            }
        }
        scope.launch { observeQueue() }
        scope.launch { observePlaybackSpeed() }
        scope.launch { observePlaybackPitch() }
        scope.launch { observeLoopMode() }
    }

    override fun getCurrentPlaybackPosition(): PlaybackPosition =
        player?.let {
            PlaybackPosition(
                played = it.currentPosition,
                buffered = it.bufferedPosition,
                total = it.duration
            )
        } ?: PlaybackPosition.ZERO

    private suspend fun initializePlayer( player: Player ) {
        player.apply {
            addListener( playerListener )
            val songsInQueue = queueRepository
                .fetchSongsInQueueSortedByPosition()
                .first()
            val previouslyPlayingSong = songsInQueue
                .find { it.id == userPreferencesDataSource.userData.first().currentlyPlayingSongId }
            previouslyPlayingSong?.let { song ->
                setMediaItems(
                    songsInQueue.map { songToMediaItemConverter.convert( it ) },
                    songsInQueue.indexOf( song ),
                    C.TIME_UNSET
                )
            }
            val playbackSpeed = userPreferencesDataSource.userData.first().playbackSpeed
            val playbackPitch = userPreferencesDataSource.userData.first().playbackPitch
            val loopMode = userPreferencesDataSource.userData.first().loopMode
            setPlaybackSpeed( playbackSpeed )
            setPlaybackPitch( playbackPitch )
            setRepeatMode( loopMode )
            prepare()
        }
    }

    private suspend fun observeQueue() {
        queueRepository.fetchSongsInQueueSortedByPosition().collect { newQueue ->
            player?.let { player ->
                player.syncWithQueue(
                    newItems = newQueue.map { it.toMediaItem() },
                    ignoreMetadataUpdates = false
                )
            }
        }
    }

    private suspend fun observePlaybackSpeed() {
        userPreferencesDataSource.userData.map { it.playbackSpeed }.collect {
            player?.setPlaybackSpeed( it )
        }
    }

    private suspend fun observePlaybackPitch() {
        userPreferencesDataSource.userData.map { it.playbackPitch }.collect {
            setPlaybackPitch( it )
        }
    }

    private suspend fun observeLoopMode() {
        userPreferencesDataSource.userData.map { it.loopMode }.collect { loopMode ->
            player?.setRepeatMode( loopMode )
        }
    }

    override suspend fun deleteSong( song: Song ) {
        compositeRepository.deleteSongWithId( song.id )
        updateNowPlayingMediaItemAfterDeleting( song )
    }

    private suspend fun updateNowPlayingMediaItemAfterDeleting( song: Song ) {
        val queue = queueRepository.fetchSongsInQueueSortedByPosition().first()
        if ( player?.currentMediaItem?.mediaId == song.id ) {
            if ( song.id == queue.last().id ) {
                player?.stop()
                player?.clearMediaItems()
            } else {
                playNextSong()
            }
        }
    }

    override suspend fun addToQueue( song: Song ) {
        val queue = queueRepository.fetchSongsInQueueSortedByPosition().first()
        if ( queue.contains( song ) ) return // NO DUPLICATES!
        if ( queue.isEmpty() ) {
            playSong(
                song = song,
                songs = listOf( song ),
                shuffle = false
            )
        } else {
            addSongToQueue(
                song = song,
                position = queue.size
            )
        }
    }

    override suspend fun playNext( song: Song ) {
        if ( player?.currentMediaItem?.mediaId == song.id ) return
        val queue = queueRepository.fetchSongsInQueueSortedByPosition().first()
        if ( queue.isEmpty() ) {
            playSong(
                song = song,
                songs = listOf( song ),
                shuffle = false
            )
        } else {
            addSongToQueue(
                song = song,
                position = getPositionToPlaceSongToPlayNext( song )
            )
        }
    }

    private suspend fun getPositionToPlaceSongToPlayNext( song: Song ): Int {
        val songsInQueue = queueRepository.fetchSongsInQueueSortedByPosition().first()
        val currentlyPlayingMediaItemIndex = player?.currentMediaItemIndex ?: 0
        val indexOfSongInQueue = songsInQueue.indexOf(song)
        return if ( songsInQueue.contains( song )
            && currentlyPlayingMediaItemIndex > indexOfSongInQueue ) {
            currentlyPlayingMediaItemIndex
        } else {
            currentlyPlayingMediaItemIndex + 1
        }
    }


    override suspend fun shuffleAndPlay( songs: List<Song> ) {
        playSong(
            song = songs.random(),
            songs = songs,
            shuffle = true
        )
    }

    private suspend fun addSongToQueue(
        song: Song,
        position: Int
    ) {
        val songsInQueue = queueRepository.fetchSongsInQueueSortedByPosition().first().toMutableList()
        if ( songsInQueue.contains( song ) ) {
            val currentPositionOfSongInQueue = songsInQueue.indexOf( song )
            moveSong( currentPositionOfSongInQueue, position )
        } else {
            songsInQueue.add( position, song )
            updateQueueWith( songsInQueue )
        }
    }

    override suspend fun playSong(
        song: Song,
        songs: List<Song>,
        shuffle: Boolean
    ) {
        player?.let { player ->
            val songsCopy = songs.toMutableList()
            if ( shuffle ) {
                songsCopy.apply {
                    remove( song )
                    shuffle()
                    add( 0, song )
                }
            }
            val indexOfSelectedMediaItem = songsCopy.indexOf( song )
            player.setMediaItems(
                songsCopy.map { songToMediaItemConverter.convert( it ) },
                indexOfSelectedMediaItem,
                C.TIME_UNSET
            )
            player.prepare()
            player.play()
            updateQueueWith( songsCopy )
        }
    }

    override suspend fun shuffleSongsInQueue() {
        player?.let { player ->
            player.currentMediaItem?.mediaId?.let { currentlyPlayingSongId ->
                val currentSongsInQueue = queueRepository
                    .fetchSongsInQueueSortedByPosition()
                    .first()
                    .shuffled()
                    .toMutableList()
                val indexOfCurrentlyPlayingSongInQueue = currentSongsInQueue
                    .indexOfFirst { it.id == currentlyPlayingSongId }
                currentSongsInQueue.move( indexOfCurrentlyPlayingSongInQueue, 0 )
                updateQueueWith( currentSongsInQueue )
            }
        }
    }

    override suspend fun moveSong( from: Int, to: Int ) {
        val currentQueue = queueRepository.fetchSongsInQueueSortedByPosition().first().toMutableList()
        currentQueue.move( from, to )
        updateQueueWith( currentQueue )
    }

    override fun playNextSong(): Boolean {
        player?.let {
            it.seekToNext()
            return true
        }
        return false
    }

    private fun setPlaybackPitch( playbackPitch: Float ) {
        player?.setPlaybackPitch( playbackPitch )
    }

    override fun playPause() {
        player?.let {
            if ( it.isPlaying ) it.pause() else it.play()
        }
    }

    override fun play() {
        player?.play()
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

    private suspend fun updateQueueWith( songs: List<Song> ) {
        queueRepository.saveQueue( songs )
    }


    private inner class PlayerListener : Player.Listener {
        override fun onEvents( player: Player, events: Player.Events ) {
            if ( events.contains( Player.EVENT_PLAY_WHEN_READY_CHANGED )
                || events.contains( Player.EVENT_PLAYBACK_STATE_CHANGED )
                || events.contains( Player.EVENT_MEDIA_ITEM_TRANSITION )
                || events.contains( Player.EVENT_PLAYLIST_METADATA_CHANGED )
                || events.contains( Player.EVENT_MEDIA_METADATA_CHANGED )
                ) {
                _playerState.value = _playerState.value.copy(
                    currentlyPlayingSongId = player.currentMediaItem?.mediaId
                )
            }
        }

        override fun onIsPlayingChanged( isPlaying: Boolean ) {
            super.onIsPlayingChanged( isPlaying )
            _playerState.value = _playerState.value.copy( isPlaying = isPlaying )
        }

        override fun onIsLoadingChanged( isLoading: Boolean ) {
            super.onIsLoadingChanged( isLoading )
            _playerState.value = _playerState.value.copy( isBuffering = isLoading )
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

private fun Player.setPlaybackPitch( pitch: Float ) {
    playbackParameters = PlaybackParameters(
        playbackParameters.speed,
        pitch
    )
}

private fun Player.setRepeatMode( loopMode: LoopMode ) {
    this.repeatMode = when ( loopMode ) {
        LoopMode.None -> Player.REPEAT_MODE_OFF
        LoopMode.Song -> Player.REPEAT_MODE_ONE
        LoopMode.Queue -> Player.REPEAT_MODE_ALL
    }
}

interface SongToMediaItemConverter {
    fun convert( song: Song ): MediaItem
}

class DefaultSongToMediaItemConverter @Inject constructor() : SongToMediaItemConverter {
    override fun convert( song: Song ): MediaItem = song.toMediaItem()
}


/**
 * Synchronizes the player playlist with a new list of songs.
 * This ensures that the currently playing item remains playing even if its
 * position in the queue changes.
 */
fun Player.syncWithQueue(
    newItems: List<MediaItem>,
    ignoreMetadataUpdates: Boolean = false
) {
    // 1. Remove items that no longer exist in the new list
    val newIds = newItems.map { it.mediaId }.toSet()
    var i = 0
    while ( i < mediaItemCount ) {
        val currentId = getMediaItemAt( i ).mediaId
        if ( !newIds.contains( currentId ) ) {
            removeMediaItem( i )
            // Don't increment i because the next item shifted into this index
        } else {
            i++
        }
    }

    // 2. Reorder and Add
    newItems.forEachIndexed { targetIndex, newItem ->
        val currentPlaylistIds = ( 0 until mediaItemCount )
            .map { getMediaItemAt( it ).mediaId }
        val currentIndex = currentPlaylistIds.indexOf( newItem.mediaId )

        if ( currentIndex != -1 ) {
            // Item exists, check if it needs to move
            if ( currentIndex != targetIndex ) {
                moveMediaItem( currentIndex, targetIndex )
            }

            // 3. Update metadata/URL if needed (The "Replace" step)
            if ( !ignoreMetadataUpdates ) {
                val existingItem = getMediaItemAt( targetIndex )
                if ( shouldUpdateItem( existingItem, newItem ) ) {
                    replaceMediaItem( targetIndex, newItem )
                }
            }
        } else {
            // Item is brand new, insert it
            addMediaItem( targetIndex, newItem )
        }
    }
}

/**
 * Helper to determine if the MediaItem data has changed
 * (e.g., a new stream URL or updated cover art).
 */
private fun shouldUpdateItem( old: MediaItem, new: MediaItem ): Boolean {
    return old.mediaMetadata != new.mediaMetadata ||
            old.localConfiguration?.uri != new.localConfiguration?.uri
}

val NOTHING_PLAYING = MediaItem.EMPTY
private const val TAG = "MUSIC-SERVICE-CONNECTION-TAG"
