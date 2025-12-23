package com.squad.musicmatters.core.media.connection

import com.squad.musicmatters.core.model.Song
import kotlinx.coroutines.flow.StateFlow

interface MusicServiceConnection {

    val playerState: StateFlow<PlayerState>

    fun getCurrentPlaybackPosition(): PlaybackPosition
    suspend fun deleteSong( song: Song )
    fun playPause()
    fun play()
    fun playPreviousSong(): Boolean
    fun playNextSong(): Boolean
    fun seekBack()
    fun seekForward()
    fun seekTo( position: Long )
    suspend fun playSong(
        song: Song,
        songs: List<Song>,
        shuffle: Boolean,
    )
    suspend fun shuffleAndPlay( songs: List<Song> )
    suspend fun shuffleSongsInQueue()
    suspend fun moveSong( from: Int, to: Int )
    suspend fun playNext( song: Song )
    suspend fun addToQueue( song: Song )
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
) {
    val playedRatio: Float
        get() = ( played.toFloat() / total ).takeIf { it.isFinite() } ?: 0f

    val bufferedRatio: Float
        get() = ( buffered.toFloat() / total ).takeIf { it.isFinite() } ?: 0f

    companion object {
        val ZERO = PlaybackPosition( 0L, 0L, 0L )
    }
}