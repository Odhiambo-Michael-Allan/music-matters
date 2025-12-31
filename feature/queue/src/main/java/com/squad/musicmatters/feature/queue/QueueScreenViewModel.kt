package com.squad.musicmatters.feature.queue

import androidx.lifecycle.viewModelScope
import com.squad.musicmatters.core.media.connection.MusicServiceConnection
import com.squad.musicmatters.core.data.repository.PlaylistRepository
import com.squad.musicmatters.core.data.repository.QueueRepository
import com.squad.musicmatters.core.data.repository.SongsAdditionalMetadataRepository
import com.squad.musicmatters.core.datastore.PreferencesDataSource
import com.squad.musicmatters.core.i8n.English
import com.squad.musicmatters.core.i8n.Language
import com.squad.musicmatters.core.model.Playlist
import com.squad.musicmatters.core.model.Song
import com.squad.musicmatters.core.model.SongAdditionalMetadataInfo
import com.squad.musicmatters.core.ui.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
internal class QueueScreenViewModel @Inject constructor(
    private val player: MusicServiceConnection,
    private val queueRepository: QueueRepository,
    preferencesDataSource: PreferencesDataSource,
    playlistRepository: PlaylistRepository,
    metadataRepository: SongsAdditionalMetadataRepository,
) : BaseViewModel(
    player = player,
    preferencesDataSource = preferencesDataSource,
    playlistRepository = playlistRepository,
    songsAdditionalMetadataRepository = metadataRepository,
) {

    
    val uiState: StateFlow<QueueScreenUiState> =
        combine(
            queueRepository.fetchSongsInQueueSortedByPosition(),
            preferencesDataSource.userData.map { it.currentlyPlayingSongId },
            playlistRepository.fetchFavorites(),
            playlistRepository.fetchPlaylists(),
            metadataRepository.fetchAdditionalMetadataEntries()
        ) {
            songsInQueue,
            currentlyPlayingSongId,
            favoriteSongsPlaylist,
            playlists,
            metadata ->
            QueueScreenUiState.Success(
                songsInQueue = songsInQueue,
                currentlyPlayingSongId = currentlyPlayingSongId,
                favoriteSongIds = favoriteSongsPlaylist?.songIds ?: emptySet(),
                playlists = playlists,
                songsAdditionalMetadata = metadata
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed( 5_000 ),
            initialValue = QueueScreenUiState.Loading
        )

    fun clearQueue() {
        viewModelScope.launch { queueRepository.clearQueue() }
    }

    fun saveQueue( queue: List<Song> ) {
        viewModelScope.launch { queueRepository.saveQueue( queue ) }
    }

}

sealed interface QueueScreenUiState {
    data object Loading : QueueScreenUiState
    data class Success(
        val songsInQueue: List<Song>,
        val currentlyPlayingSongId: String,
        val language: Language = English,
        val favoriteSongIds: Set<String>,
        val playlists: List<Playlist>,
        val songsAdditionalMetadata: List<SongAdditionalMetadataInfo>
    ): QueueScreenUiState

}

