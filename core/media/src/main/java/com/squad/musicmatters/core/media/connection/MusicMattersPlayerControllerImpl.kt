package com.squad.musicmatters.core.media.connection

import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackParameters
import androidx.media3.common.Player
import com.squad.musicmatters.core.common.Dispatcher
import com.squad.musicmatters.core.common.MusicMattersDispatchers
import com.squad.musicmatters.core.data.repository.CompositeRepository
import com.squad.musicmatters.core.data.repository.QueueRepository
import com.squad.musicmatters.core.datastore.PreferencesDataSource
import com.squad.musicmatters.core.media.media.extensions.move
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
import java.time.Instant
import java.util.Date
import java.util.Timer
import javax.inject.Inject
import kotlin.time.Duration
import kotlin.time.DurationUnit
import kotlin.time.toDuration

class MusicMattersPlayerControllerImpl @Inject constructor(
    private val playerConnector: PlayerConnector,
    private val queueRepository: QueueRepository,
    private val userPreferencesDataSource: PreferencesDataSource,
    private val songToMediaItemConverter: SongToMediaItemConverter,
    @Dispatcher( MusicMattersDispatchers.Main ) dispatcher: CoroutineDispatcher,
) : MusicMattersPlayerController {

    private val scope = CoroutineScope( dispatcher + SupervisorJob() )
    private val playerListener: PlayerListener = PlayerListener()
    private var player: Player? = null

    private val _playerState = MutableStateFlow( PlayerState() )
    override val playerState = _playerState.asStateFlow()

    private var _queue = MutableStateFlow<List<Song>>( emptyList() )
    override val queue = _queue.asStateFlow()

    private var currentPositionInQueue: Int = -1

    private var _sleepTimer = MutableStateFlow<SleepTimer?>( null )
    override val sleepTimer = _sleepTimer.asStateFlow()

    init {
        scope.launch {
            playerConnector.establishConnection()
            playerConnector.addDisconnectListener {
                scope.cancel()
            }
            player = playerConnector.player?.apply {
                initializePlayer( this )
            }
            launch { observeQueue() }
            launch { observeLoopMode() }
        }
    }

    private suspend fun observeQueue() {
        queue.collect {
            scope.launch {
                queueRepository.saveQueue( it )
            }
        }
    }

    private suspend fun initializePlayer( player: Player ) {
        player.apply {
            addListener( playerListener )
            val songsInQueue = queueRepository
                .fetchSongsInQueueSortedByPosition()
                .first()
            _queue.value = songsInQueue
            val previouslyPlayingSong = songsInQueue
                .find { it.id == userPreferencesDataSource.userData.first().currentlyPlayingSongId }
            currentPositionInQueue = songsInQueue.indexOf( previouslyPlayingSong )
            previouslyPlayingSong?.let { song ->
                setMediaItem( songToMediaItemConverter.convert( song ) )
            }
            val loopMode = userPreferencesDataSource.userData.first().loopMode
            setRepeatMode( loopMode )
            prepare()
        }
    }

    override fun getCurrentPlaybackPosition(): PlaybackPosition =
        player?.let {
            val sleepTimerDurationLeft = sleepTimer.value?.let { sleepTimer ->
                val now = Instant.now()
                    .toEpochMilli()
                    .toDuration( DurationUnit.MILLISECONDS )
                sleepTimer.endsAt.minus( now )
            }
            PlaybackPosition(
                played = it.currentPosition,
                buffered = it.bufferedPosition,
                total = it.duration,
                sleepTimerDurationLeft = sleepTimerDurationLeft,
            )
        } ?: PlaybackPosition.ZERO

    private suspend fun observeLoopMode() {
        userPreferencesDataSource.userData.map { it.loopMode }.collect { loopMode ->
            player?.setRepeatMode( loopMode )
        }
    }

    override suspend fun deleteSong( song: Song ) {
        updateNowPlayingMediaItemAfterDeleting( song )
    }

    private fun updateNowPlayingMediaItemAfterDeleting( song: Song ) {
        player?.let {
            if ( it.currentMediaItem?.mediaId == song.id ) {
                if ( queue.value.last().id == song.id ) {
                    player?.stop()
                    player?.clearMediaItems()
                } else {
                    playNextSong()
                }
            }
        }
    }

    override fun addToQueue( songToAdd: Song ) {
        for ( song in queue.value ) {
            if ( song.id == songToAdd.id ) return // NO DUPLICATES!
        }
        if ( queue.value.isEmpty() ) {
            playSong(
                song = songToAdd,
                songs = listOf( songToAdd ),
                shuffle = false
            )
        } else {
            addSongToQueue(
                song = songToAdd,
                position = queue.value.size
            )
        }
    }

    override fun playSong(
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
            currentPositionInQueue = songsCopy.indexOf( song )
            player.setMediaItem(
                songToMediaItemConverter.convert( songsCopy[ currentPositionInQueue ] )
            )
            _queue.value = songsCopy
            player.prepare()
            player.play()
        }
    }

    override fun playSongNext( song: Song ) {
        if ( player?.currentMediaItem?.mediaId == song.id ) return // SONG IS ALREADY PLAYING
        if ( queue.value.isEmpty() ) {
            playSong(
                song = song,
                songs = listOf( song ),
                shuffle = false,
            )
        } else {
            addSongToQueue(
                song = song,
                position = getPositionToPlaceSongToPlayNext( song )
            )
        }
    }

    private fun getPositionToPlaceSongToPlayNext( song: Song ): Int {
        val indexOfSongInQueue = queue.value.indexOfFirst { it.id == song.id }
        val songIsPresentInQueue = indexOfSongInQueue != -1
        return if ( songIsPresentInQueue && currentPositionInQueue > indexOfSongInQueue ) {
            currentPositionInQueue
        } else {
            currentPositionInQueue + 1
        }
    }

    private fun addSongToQueue(
        song: Song,
        position: Int
    ) {
        val queueCopy = queue.value.toMutableList()
        if ( queue.value.contains( song ) ) {
            val currentPositionOfSongInQueue = queue.value.indexOf( song )
            queueCopy.move( currentPositionOfSongInQueue, position )
        } else {
            queueCopy.add( position, song )
        }
        _queue.value = queueCopy
    }

    override fun shuffleAndPlay( songs: List<Song> ) {
        playSong(
            song = songs.random(),
            songs = songs,
            shuffle = true
        )
    }

    override suspend fun shuffleSongsInQueue() {
        val queue = _queue.value.toMutableList()
        val currentlyPlayingSong = queue.removeAt( currentPositionInQueue )
        queue.shuffle()
        queue.add( 0, currentlyPlayingSong )
        currentPositionInQueue = 0
        _queue.value = queue
    }

    override fun playNextSong( ignoreLoopMode: Boolean ): Boolean {
        if ( currentPositionInQueue >= queue.value.size - 1 ) {
            currentPositionInQueue = -1
        }
        val songToPlay = getSongToPlay( ignoreLoopMode )
        playSong(
            song = songToPlay,
            songs = queue.value,
            shuffle = false,
        )
        return true
    }

    private fun getSongToPlay( ignoreLoopMode: Boolean ): Song = if ( ignoreLoopMode ) {
        queue.value[ ++currentPositionInQueue ]
    } else {
        player?.let {
            when ( it.repeatMode ) {
                Player.REPEAT_MODE_ONE -> queue.value[ currentPositionInQueue ]
                else -> queue.value[ ++currentPositionInQueue ]
            }
        } ?: Song.EMPTY
    }

    override fun clearQueue() {
        _queue.value = emptyList()
        player?.clearMediaItems()
        currentPositionInQueue = -1
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
            if ( it.currentPosition < 2000L && currentPositionInQueue > 0 ) {
                playSong(
                    song = queue.value[ --currentPositionInQueue ],
                    songs = queue.value,
                    shuffle = false,
                )
            } else {
                it.seekTo( 0L )
            }
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

    override fun moveSong( from: Int, to: Int ) {
        val queue = queue.value.toMutableList()
        queue.move( from, to )
        _queue.value = queue
        playerState.value.currentlyPlayingSongId?.let { songId ->
            currentPositionInQueue = queue.indexOfFirst { it.id == songId }
        }
    }

    override fun setTimer( duration: Duration ) {
        val endsAt = System.currentTimeMillis().toDuration( DurationUnit.MILLISECONDS )
            .plus( duration )
        val timer = Timer()
        timer.schedule(
            kotlin.concurrent.timerTask {
                scope.launch {
                    stopSleepTimer()
                    player?.pause()
                }
            },
            Date.from(
                Instant.ofEpochMilli(
                    endsAt.inWholeMilliseconds
                )
            )
        )
        stopSleepTimer()
        _sleepTimer.value = SleepTimer(
            duration = duration,
            endsAt = endsAt,
            timer = timer
        )
    }

    override fun stopSleepTimer() {
        _sleepTimer.value?.timer?.cancel()
        _sleepTimer.value = null
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

        override fun onPlaybackStateChanged( playbackState: Int ) {
            if ( playbackState == Player.STATE_ENDED ) {
                playNextSong()
            }
        }
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

private const val TAG = "MUSIC-MATTERS-PLAYER"
