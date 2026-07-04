package com.squad.musicmatters.core.testing.connection

import com.squad.musicmatters.core.media.connection.MusicMattersPlayer
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


class FakeMusicMattersPlayer : MusicMattersPlayer {

    private val _playerState = MutableStateFlow( PlayerState() )
    override val playerState = _playerState.asStateFlow()

    private val _idsOfSongsInQueue = MutableStateFlow( emptyList<String>() )
    override val idsOfSongsInQueue = _idsOfSongsInQueue.asStateFlow()

    private val _sleepTimer = MutableStateFlow<SleepTimer?>( null )
    override val sleepTimer = _sleepTimer.asStateFlow()

    fun sendPlayerState( state: PlayerState ) {
        _playerState.value = state
    }

    fun sendSongs( songs: List<Song> ) {
        _idsOfSongsInQueue.value = songs.map { it.id }
    }

    override fun getCurrentPlaybackPosition(): PlaybackPosition {
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

    override fun playSong(
        song: Song,
        songs: List<Song>,
    ) {
        TODO("Not yet implemented")
    }

    override fun shuffleAndPlay(songs: List<Song>) {
        TODO("Not yet implemented")
    }

    override suspend fun shuffleSongsInQueue( shuffle: Boolean ) {
        TODO("Not yet implemented")
    }

    override fun playSongNext(song: Song) {
        TODO("Not yet implemented")
    }

    override fun addToQueue(song: Song) {
        TODO("Not yet implemented")
    }

    override fun clearQueue() {
        _idsOfSongsInQueue.value = emptyList<String>()
    }

    override fun moveSong( from: Int, to: Int ) {
        TODO("Not yet implemented")
    }

    override fun contains(song: Song): Boolean {
        TODO("Not yet implemented")
    }

    override fun remove(song: Song) {
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












