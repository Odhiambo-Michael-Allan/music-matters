package com.squad.musicmatters.core.media.connection

import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import com.squad.musicmatters.core.common.Dispatcher
import com.squad.musicmatters.core.common.MusicMattersDispatchers
import com.squad.musicmatters.core.media.media.extensions.contains
import com.squad.musicmatters.core.media.media.extensions.getMediaItems
import com.squad.musicmatters.core.media.media.extensions.toMediaItem
import com.squad.musicmatters.core.model.Song
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import java.time.Instant
import java.util.Date
import java.util.Timer
import javax.inject.Inject
import kotlin.time.Duration
import kotlin.time.DurationUnit
import kotlin.time.toDuration

class MusicMattersPlayerImpl @Inject constructor(
    private val playerConnector: PlayerConnector,
    private val songToMediaItemConverter: SongToMediaItemConverter,
    @Dispatcher( MusicMattersDispatchers.Main ) dispatcher: CoroutineDispatcher,
) : MusicMattersPlayer {

    private val scope = CoroutineScope( dispatcher + SupervisorJob() )
    private val playerListener: PlayerListener = PlayerListener()
    private var player: Player? = null

    private val _playerState = MutableStateFlow( PlayerState() )
    override val playerState = _playerState.asStateFlow()

    private var _idsOfSongsInQueue = MutableStateFlow<List<String>>( emptyList() )
    override val idsOfSongsInQueue = _idsOfSongsInQueue.asStateFlow()

    private var _sleepTimer = MutableStateFlow<SleepTimer?>( null )
    override val sleepTimer = _sleepTimer.asStateFlow()

    init {
        scope.launch {
            playerConnector.establishConnection()
            playerConnector.addDisconnectListener {
                scope.cancel()
            }
            player = playerConnector.player?.apply {
                addListener( playerListener )
            }
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

    override fun addToQueue( songToAdd: Song ) {
        player?.let {
            if ( it.contains( songToMediaItemConverter.convert( songToAdd ) ) ) return // NO DUPS
            if ( it.mediaItemCount == 0 ) {
                playSong(
                    song = songToAdd,
                    songs = listOf( songToAdd ),
                )
            } else {
                addSongToQueue(
                    song = songToAdd,
                    position = it.mediaItemCount
                )
            }
        }
    }

    override fun playSong(
        song: Song,
        songs: List<Song>,
    ) {
        player?.let { player ->
            Timber.tag( TAG ).d( "INDEX OF SONG TO PLAY: ${songs.indexOf( song )}" )
            player.setMediaItems(
                songs.map { songToMediaItemConverter.convert( it ) },
                songs.indexOf( song ),
                C.TIME_UNSET
            )
        }
    }

    override fun playSongNext( song: Song ) {
        player?.let { player ->
            if ( player.currentMediaItem?.mediaId == song.id ) return // SONG IS ALREADY PLAYING
            if ( player.mediaItemCount == 0 ) {
                playSong(
                    song = song,
                    songs = listOf( song ),
                )
            } else {
                addSongToQueue(
                    song = song,
                    position = getPositionToPlaceSongToPlayNext( song, player )
                )
            }
        }
    }

    private fun getPositionToPlaceSongToPlayNext( song: Song, player: Player ): Int {
        val indexOfSongInQueue = player.getMediaItems().indexOfFirst { it.mediaId == song.id }
        val songIsPresentInQueue = indexOfSongInQueue != -1
        return if ( songIsPresentInQueue && player.currentMediaItemIndex > indexOfSongInQueue ) {
            player.currentMediaItemIndex
        } else {
            player.currentMediaItemIndex + 1
        }
    }

    private fun addSongToQueue(
        song: Song,
        position: Int
    ) {
        player?.let { player ->
            val mediaItem = songToMediaItemConverter.convert( song )
            if ( player.contains( mediaItem ) ) {
                val currentPositionInQueue =
                    player.getMediaItems()
                        .indexOfFirst { mediaItem -> mediaItem.mediaId == song.id }
                player.moveMediaItem( currentPositionInQueue, position )
            } else {
                player.addMediaItem( position, mediaItem )
            }
        }
    }

    override fun shuffleAndPlay( songs: List<Song> ) {
        playSong(
            song = songs.random(),
            songs = songs.shuffled(),
        )
    }

    override suspend fun shuffleSongsInQueue( shuffle: Boolean ) {
        player?.let { it.shuffleModeEnabled = shuffle }
    }

    override fun playNextSong(): Boolean {
        player?.seekToNext()
        return true
    }

    override fun clearQueue() {
        player?.clearMediaItems()
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
        player?.seekToPrevious()
        return true
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
        player?.moveMediaItem( from, to )
    }

    override fun contains( song: Song ): Boolean = player?.getMediaItems()
        ?.firstOrNull { mediaItem -> mediaItem.mediaId == song.id } != null

    override fun remove( song: Song ) {
        player?.let {
            val indexOfDeletedSong = it.getMediaItems()
                .indexOfFirst { mediaItem -> mediaItem.mediaId == song.id }
            if ( indexOfDeletedSong != -1 ) it.removeMediaItem( indexOfDeletedSong )
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
            _idsOfSongsInQueue.value = player.getMediaItems().map { it.mediaId }
            if ( events.contains( Player.EVENT_PLAY_WHEN_READY_CHANGED )
                || events.contains( Player.EVENT_PLAYBACK_STATE_CHANGED )
                || events.contains( Player.EVENT_MEDIA_ITEM_TRANSITION )
                || events.contains( Player.EVENT_PLAYLIST_METADATA_CHANGED )
                || events.contains( Player.EVENT_MEDIA_METADATA_CHANGED )
                        || events.contains( Player.EVENT_TRACKS_CHANGED )
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

interface SongToMediaItemConverter {
    fun convert( song: Song ): MediaItem
}

class DefaultSongToMediaItemConverter @Inject constructor() : SongToMediaItemConverter {
    override fun convert( song: Song ): MediaItem = song.toMediaItem()
}

private const val TAG = "MUSIC-MATTERS-PLAYER"
