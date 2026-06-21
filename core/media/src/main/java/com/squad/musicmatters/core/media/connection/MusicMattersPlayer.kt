package com.squad.musicmatters.core.media.connection

import com.squad.musicmatters.core.model.Song
import kotlinx.coroutines.flow.StateFlow
import java.util.Timer
import kotlin.time.Duration

interface MusicMattersPlayer {

    val playerState: StateFlow<PlayerState>
    val idsOfSongsInQueue: StateFlow<List<String>>
    val sleepTimer: StateFlow<SleepTimer?>

    fun getCurrentPlaybackPosition(): PlaybackPosition
    fun playPause()
    fun play()
    fun playPreviousSong(): Boolean
    fun playNextSong(): Boolean
    fun seekBack()
    fun seekForward()
    fun seekTo( position: Long )
    fun setTimer( duration: Duration )
    fun stopSleepTimer()
    fun playSong(
        song: Song,
        songs: List<Song>,
    )
    fun shuffleAndPlay( songs: List<Song> )
    suspend fun shuffleSongsInQueue( shuffle: Boolean )
    fun playSongNext(song: Song )
    fun addToQueue( songToAdd: Song )
    fun clearQueue()
    fun moveSong( from: Int, to: Int )
    fun contains( song: Song ): Boolean
    fun remove( song: Song )

}

data class PlayerState(
    val currentlyPlayingSongId: String? = null,
    val isBuffering: Boolean = false,
    val isPlaying: Boolean = false,
)

data class PlaybackPosition(
    val played: Long,
    val buffered: Long,
    val total: Long,
    val sleepTimerDurationLeft: Duration? = null
) {
    val playedRatio: Float
        get() = ( played.toFloat() / total ).takeIf { it.isFinite() } ?: 0f

    val bufferedRatio: Float
        get() = ( buffered.toFloat() / total ).takeIf { it.isFinite() } ?: 0f

    companion object {
        val ZERO = PlaybackPosition( 0L, 0L, 0L )
    }
}

data class SleepTimer(
    val duration: Duration,
    val endsAt: Duration,
    val timer: Timer,
)