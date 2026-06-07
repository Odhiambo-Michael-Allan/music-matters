package com.squad.musicmatters.feature.queue

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.squad.musicmatters.core.media.connection.MusicMattersPlayerController
import com.squad.musicmatters.core.datastore.PreferencesDataSource
import com.squad.musicmatters.core.model.Song
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "QUEUE-SCREEN-VIEW-MODEL"

@HiltViewModel
internal class QueueScreenViewModel @Inject constructor(
//    private val queueRepository: QueueRepository,
    private val player: MusicMattersPlayerController,
    preferencesDataSource: PreferencesDataSource,
) : ViewModel() {

    
    val uiState: StateFlow<QueueScreenUiState> =
        combine(
            player.queue,
            preferencesDataSource.userData.map { it.currentlyPlayingSongId },
        ) {
            songsInQueue,
            currentlyPlayingSongId ->
            QueueScreenUiState.Success(
                songsInQueue = songsInQueue,
                currentlyPlayingSongId = currentlyPlayingSongId,
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed( 5_000 ),
            initialValue = QueueScreenUiState.Loading
        )

    fun moveSong( from: Int, to: Int ) {
        viewModelScope.launch { player.moveSong( from, to ) }
    }

    fun playSongs(
        selectedSong: Song,
        songsInPlaylist: List<Song>
    ) {
        viewModelScope.launch {
            player.playSong(
                song = selectedSong,
                songs = songsInPlaylist,
                shuffle = false,
            )
        }
    }

    fun shuffle() {
        viewModelScope.launch { player.shuffleSongsInQueue() }
    }

}

sealed interface QueueScreenUiState {
    data object Loading : QueueScreenUiState
    data class Success(
        val songsInQueue: List<Song>,
        val currentlyPlayingSongId: String,
    ): QueueScreenUiState

}

