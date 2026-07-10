package com.squad.musicmatters.feature.lyrics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.squad.musicmatters.core.data.repository.QueueRepository
import com.squad.musicmatters.core.data.repository.SongsRepository
import com.squad.musicmatters.core.datastore.UserPreferencesRepository
import com.squad.musicmatters.core.media.connection.MusicMattersPlayer
import com.squad.musicmatters.core.media.connection.PlaybackPosition
import com.squad.musicmatters.core.media.media.PlaybackPositionUpdater
import com.squad.musicmatters.core.model.Lyric
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class LyricsScreenViewModel @Inject constructor(
    songsRepository: SongsRepository,
    queueRepository: QueueRepository,
    userPreferencesRepository: UserPreferencesRepository,
    playbackPositionUpdater: PlaybackPositionUpdater,
    private val player: MusicMattersPlayer,
) : ViewModel() {

    val uiState: StateFlow<LyricsScreenUiState> = userPreferencesRepository.userData
        .map { it.currentlyPlayingSongId }
        .distinctUntilChanged()
        .flatMapLatest { songId ->
            queueRepository.fetchSongsSortedByCurrentPosition().map { queue ->
                queue.find { it.id == songId }
            }
        }.flatMapLatest { song ->
            songsRepository.fetchLyricsForSong( song )
        }.map { lyrics ->
            LyricsScreenUiState.Success( lyrics = lyrics )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed( 5_000 ),
            initialValue = LyricsScreenUiState.Loading,
        )

    val playbackPosition: StateFlow<PlaybackPosition> =
        playbackPositionUpdater.playbackPosition.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed( 5_000 ),
            initialValue = PlaybackPosition.ZERO
        )

    fun onSeekTo( position: Long ) { player.seekTo( position ) }
}

sealed interface LyricsScreenUiState {
    data object Loading: LyricsScreenUiState
    data class Success( val lyrics: List<Lyric> ) : LyricsScreenUiState
}