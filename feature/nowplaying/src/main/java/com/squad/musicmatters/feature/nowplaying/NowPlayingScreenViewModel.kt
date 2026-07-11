package com.squad.musicmatters.feature.nowplaying

import androidx.lifecycle.viewModelScope
import com.squad.musicmatters.core.media.connection.MusicMattersPlayer
import com.squad.musicmatters.core.data.repository.PlaylistsRepository
import com.squad.musicmatters.core.data.repository.QueueRepository
import com.squad.musicmatters.core.data.repository.SongsMetadataRepository
import com.squad.musicmatters.core.data.repository.SongsRepository
import com.squad.musicmatters.core.data.utils.combine
import com.squad.musicmatters.core.datastore.UserDataRepository
import com.squad.musicmatters.core.media.connection.PlaybackPosition
import com.squad.musicmatters.core.media.connection.PlayerState
import com.squad.musicmatters.core.media.connection.SleepTimer
import com.squad.musicmatters.core.media.media.PlaybackPositionUpdater
import com.squad.musicmatters.core.model.LoopMode
import com.squad.musicmatters.core.model.Lyric
import com.squad.musicmatters.core.model.Playlist
import com.squad.musicmatters.core.model.Song
import com.squad.musicmatters.core.model.SongMetadata
import com.squad.musicmatters.core.model.UserData
import com.squad.musicmatters.core.ui.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.time.Duration

@HiltViewModel
class NowPlayingScreenViewModel @Inject constructor(
    private val player: MusicMattersPlayer,
    private val userDataRepository: UserDataRepository,
    private val playlistsRepository: PlaylistsRepository,
    private val playbackPositionUpdater: PlaybackPositionUpdater,
    private val queueRepository: QueueRepository,
    songsMetadataRepository: SongsMetadataRepository,
    songsRepository: SongsRepository,
) : BaseViewModel(
    userDataRepository = userDataRepository,
    playlistsRepository = playlistsRepository,
    player = player,
) {

    val uiState: StateFlow<NowPlayingScreenUiState> =
        combine(
            player.playerState,
            player.playerState.map { it.currentlyPlayingSongId }.flatMapLatest { songId ->
                songsRepository.fetchSongs().map { songs -> songs.firstOrNull { it.id == songId } }
            },
            userDataRepository.userData,
            player.playerState.map { it.currentlyPlayingSongId }.flatMapLatest { songId ->
                playlistsRepository.isFavorite( songId ?: "" )
            },
            playlistsRepository.fetchPlaylists(),
            songsMetadataRepository.fetchMetadata(),
            player.sleepTimer
        ) {
            playerState,
            currentlyPlayingSong,
            userData,
            currentlyPlayingSongIsFavorite,
            playlists,
            metadata,
            sleepTimer ->
            NowPlayingScreenUiState.Success(
                playerState = playerState,
                currentlyPlayingSong = currentlyPlayingSong,
                userData = userData,
                currentlyPlayingSongIsFavorite = currentlyPlayingSongIsFavorite,
                playlists = playlists,
                songMetadata = metadata.find {
                    it.songId == playerState.currentlyPlayingSongId
                },
                sleepTimer = sleepTimer
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed( 5_000 ),
            initialValue = NowPlayingScreenUiState.Loading
        )

    val lyricsUiState: StateFlow<LyricsUiState> = player.playerState
        .map { it.currentlyPlayingSongId }
        .distinctUntilChanged()
        .flatMapLatest { songId ->
            queueRepository.fetchSongsSortedByCurrentPosition().map { queue ->
                queue.find { it.id == songId }
            }
        }
        .distinctUntilChanged()
        .flatMapLatest { song ->
            songsRepository.fetchLyricsForSong( song )
        }
        .map { lyrics ->
            LyricsUiState.Success( lyrics = lyrics )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed( 5_000 ),
            initialValue = LyricsUiState.Loading,
        )

    val playbackPosition: StateFlow<PlaybackPosition> =
        playbackPositionUpdater.playbackPosition.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed( 5_000 ),
            initialValue = PlaybackPosition.ZERO
        )

    fun playPause() {
        player.playPause()
    }

    fun playPreviousSong(): Boolean {
        return player.playPreviousSong()
    }

    fun playNextSong(): Boolean {
        return player.playNextSong()
    }

    fun fastRewind() {
        player.seekBack()
    }

    fun fastForward() {
        player.seekForward()
    }

    fun onSeekStarted() {
        playbackPositionUpdater.stopPeriodicUpdates()
    }

    fun onSeekEnd( position: Long ) {
        playbackPositionUpdater.startPeriodicUpdates()
        player.seekTo( position )
    }

    fun onShowLyrics( show: Boolean ) {
        viewModelScope.launch {
            userDataRepository.setShowLyrics( show )
        }
    }

    fun startSleepTimer( duration: Duration ) {
        player.setTimer( duration )
    }

    fun stopSleepTimer() {
        player.stopSleepTimer()
    }

    override fun onCleared() {
        super.onCleared()
        playbackPositionUpdater.cleanUp()
    }

    fun onToggleLoopMode( currentLoopMode: LoopMode ) {
        val currentLoopModePosition = LoopMode.entries.indexOf( currentLoopMode )
        val nextLoopModePosition = ( currentLoopModePosition + 1 ) % LoopMode.entries.size
        viewModelScope.launch {
            userDataRepository.setLoopMode( LoopMode.entries[ nextLoopModePosition ] )
        }
    }

    fun onToggleShuffleMode( shuffle: Boolean ) {
        viewModelScope.launch {
            player.shuffleSongsInQueue( shuffle )
            userDataRepository.setShuffle( shuffle )
        }
    }

}

sealed interface NowPlayingScreenUiState {
    data object Loading : NowPlayingScreenUiState
    data class Success(
        val userData: UserData,
        val currentlyPlayingSong: Song?,
        val currentlyPlayingSongIsFavorite: Boolean,
        val playerState: PlayerState,
        val playlists: List<Playlist>,
        val songMetadata: SongMetadata?,
        val sleepTimer: SleepTimer? = null,
    ) : NowPlayingScreenUiState
}

sealed interface LyricsUiState {
    data object Loading: LyricsUiState
    data class Success(
        val lyrics: List<Lyric>
    ): LyricsUiState
}
