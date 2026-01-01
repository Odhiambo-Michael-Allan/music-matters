package com.squad.musicmatters.core.testing.connection

import com.squad.musicmatters.core.media.connection.MusicServiceConnection
import com.squad.musicmatters.core.media.connection.PlaybackPosition
import com.squad.musicmatters.core.media.connection.PlayerState
import com.squad.musicmatters.core.media.connection.SleepTimer
import com.squad.musicmatters.core.model.Song
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Timer
import kotlin.time.Duration
import kotlin.time.DurationUnit
import kotlin.time.toDuration


class TestMusicServiceConnection : MusicServiceConnection {

    private val _playerState = MutableStateFlow( PlayerState() )
    override val playerState = _playerState.asStateFlow()

    private val _sleepTimer = MutableStateFlow<SleepTimer?>( null )
    override val sleepTimer = _sleepTimer.asStateFlow()

    fun sendPlayerState( state: PlayerState ) {
        _playerState.value = state
    }

    override fun getCurrentPlaybackPosition(): PlaybackPosition {
        TODO("Not yet implemented")
    }

    override suspend fun deleteSong(song: Song) {
        TODO("Not yet implemented")
    }

    override fun playPause() {
        TODO("Not yet implemented")
    }

    override fun play() {
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

    override suspend fun playSong(
        song: Song,
        songs: List<Song>,
        shuffle: Boolean
    ) {
        TODO("Not yet implemented")
    }

    override suspend fun shuffleAndPlay(songs: List<Song>) {
        TODO("Not yet implemented")
    }

    override suspend fun shuffleSongsInQueue() {
        TODO("Not yet implemented")
    }

    override suspend fun playNext(song: Song) {
        TODO("Not yet implemented")
    }

    override suspend fun addToQueue(song: Song) {
        TODO("Not yet implemented")
    }

    override fun setTimer( duration: Duration ) {
        val endsAt = System.currentTimeMillis().toDuration( DurationUnit.MILLISECONDS )
            .plus( duration )
        _sleepTimer.value = SleepTimer(
            duration = duration,
            endsAt = endsAt,
            timer = Timer()
        )
    }

    override fun stopSleepTimer() {
        _sleepTimer.value = null
    }

}












