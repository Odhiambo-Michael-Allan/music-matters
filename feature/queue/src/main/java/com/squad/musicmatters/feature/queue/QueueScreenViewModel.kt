package com.squad.musicmatters.feature.queue

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.squad.musicmatters.core.data.repository.SongsRepository
import com.squad.musicmatters.core.media.connection.MusicMattersPlayer
import com.squad.musicmatters.core.datastore.PreferencesDataSource
import com.squad.musicmatters.core.model.Song
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "QUEUE-SCREEN-VIEW-MODEL"

@HiltViewModel
internal class QueueScreenViewModel @Inject constructor(
    private val songsRepository: SongsRepository,
    private val player: MusicMattersPlayer,
    private val preferencesDataSource: PreferencesDataSource,
) : ViewModel() {

    
    val uiState: StateFlow<QueueScreenUiState> =
        combine(
            player.idsOfSongsInQueue.flatMapLatest { ids ->
                songsRepository.fetchSongs().map { songs ->
                    val songsInQueue = mutableListOf<Song>()
                    ids.forEach { id ->
                        songs.firstOrNull { it.id == id }?.let { songsInQueue.add( it ) }
                    }
                    songsInQueue
                }
            },
            preferencesDataSource.userData,
        ) {
            songsInQueue,
            userData ->
            QueueScreenUiState.Success(
                songsInQueue = songsInQueue,
                currentlyPlayingSongId = userData.currentlyPlayingSongId,
                shuffle = userData.shuffle
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

    fun onToggleShuffleMode( shuffle: Boolean ) {
        viewModelScope.launch {
            player.shuffleSongsInQueue( shuffle )
            preferencesDataSource.setShuffle( shuffle )
        }
    }

}

sealed interface QueueScreenUiState {
    data object Loading : QueueScreenUiState
    data class Success(
        val songsInQueue: List<Song>,
        val currentlyPlayingSongId: String,
        val shuffle: Boolean,
    ): QueueScreenUiState

}

