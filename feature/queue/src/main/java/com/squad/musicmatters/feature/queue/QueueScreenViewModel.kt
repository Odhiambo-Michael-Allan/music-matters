package com.squad.musicmatters.feature.queue

import androidx.lifecycle.viewModelScope
import com.squad.musicmatters.core.data.repository.PlaylistsRepository
import com.squad.musicmatters.core.data.repository.SongsMetadataRepository
import com.squad.musicmatters.core.data.repository.SongsRepository
import com.squad.musicmatters.core.media.connection.MusicMattersPlayer
import com.squad.musicmatters.core.datastore.PreferencesDataSource
import com.squad.musicmatters.core.model.Playlist
import com.squad.musicmatters.core.model.Song
import com.squad.musicmatters.core.model.SongMetadata
import com.squad.musicmatters.core.ui.BaseViewModel
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
    playlistsRepository: PlaylistsRepository,
    songsMetadataRepository: SongsMetadataRepository,
    private val songsRepository: SongsRepository,
    private val player: MusicMattersPlayer,
    private val preferencesDataSource: PreferencesDataSource,
) : BaseViewModel(
    player = player,
    preferencesDataSource = preferencesDataSource,
    playlistsRepository = playlistsRepository,
) {

    
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
            playlistsRepository.fetchFavorites(),
            playlistsRepository.fetchPlaylists(),
            songsMetadataRepository.fetchMetadata()
        ) {
            songsInQueue,
            userData,
            favoriteSongsPlaylist,
            playlists,
            metadata ->
            QueueScreenUiState.Success(
                songsInQueue = songsInQueue,
                currentlyPlayingSongId = userData.currentlyPlayingSongId,
                shuffle = userData.shuffle,
                favoriteSongIds = favoriteSongsPlaylist?.songIds ?: emptySet(),
                playlists = playlists,
                songsAdditionalMetadata = metadata,
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed( 5_000 ),
            initialValue = QueueScreenUiState.Loading
        )

    fun moveSong( from: Int, to: Int ) {
        viewModelScope.launch { player.moveSong( from, to ) }
    }

    fun onToggleShuffleMode( shuffle: Boolean ) {
        viewModelScope.launch {
            player.shuffleSongsInQueue( shuffle )
            preferencesDataSource.setShuffle( shuffle )
        }
    }

    fun clearQueue() { player.clearQueue() }

}

sealed interface QueueScreenUiState {
    data object Loading : QueueScreenUiState
    data class Success(
        val songsInQueue: List<Song>,
        val currentlyPlayingSongId: String,
        val shuffle: Boolean,
        val favoriteSongIds: Set<String>,
        val playlists: List<Playlist>,
        val songsAdditionalMetadata: List<SongMetadata>,
    ): QueueScreenUiState

}

