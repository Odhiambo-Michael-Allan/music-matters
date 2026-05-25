package com.squad.musicmatters.feature.queue

import androidx.lifecycle.viewModelScope
import com.squad.musicmatters.core.media.connection.MusicMattersPlayer
import com.squad.musicmatters.core.data.repository.PlaylistRepository
import com.squad.musicmatters.core.data.repository.QueueRepository
import com.squad.musicmatters.core.data.repository.SongsAdditionalMetadataRepository
import com.squad.musicmatters.core.datastore.PreferencesDataSource
import com.squad.musicmatters.core.model.LoopMode
import com.squad.musicmatters.core.model.Playlist
import com.squad.musicmatters.core.model.Song
import com.squad.musicmatters.core.model.SongAdditionalMetadata
import com.squad.musicmatters.core.model.UserData
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
    private val queueRepository: QueueRepository,
    private val preferencesDataSource: PreferencesDataSource,
    private val player: MusicMattersPlayer,
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
            preferencesDataSource.userData,
            playlistRepository.fetchFavorites(),
            playlistRepository.fetchPlaylists(),
            metadataRepository.fetchAdditionalMetadataEntries()
        ) {
            songsInQueue,
            userData,
            favoriteSongsPlaylist,
            playlists,
            metadata ->
            QueueScreenUiState.Success(
                songsInQueue = songsInQueue,
                currentlyPlayingSongId = userData.currentlyPlayingSongId,
                loopMode = userData.loopMode,
                shuffle = userData.shuffle,
                favoriteSongIds = favoriteSongsPlaylist?.songIds ?: emptySet(),
                playlists = playlists,
                songsAdditionalMetadata = metadata
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed( 5_000 ),
            initialValue = QueueScreenUiState.Loading
        )

    fun saveQueue( queue: List<Song> ) {
        viewModelScope.launch { queueRepository.saveQueue( queue ) }
    }

    fun toggleLoopMode( currentLoopMode: LoopMode ) {
        val currentLoopModePosition = LoopMode.entries.indexOf( currentLoopMode )
        val nextLoopModePosition = ( currentLoopModePosition + 1 ) % LoopMode.entries.size
        viewModelScope.launch {
            preferencesDataSource.setLoopMode( LoopMode.entries[ nextLoopModePosition ] )
        }
    }

    fun setShuffleMode( shuffle: Boolean ) {
        viewModelScope.launch {
            preferencesDataSource.setShuffle( shuffle )
            if ( shuffle ) player.shuffleSongsInQueue()
        }
    }

}

sealed interface QueueScreenUiState {
    data object Loading : QueueScreenUiState
    data class Success(
        val songsInQueue: List<Song>,
        val currentlyPlayingSongId: String,
        val loopMode: LoopMode,
        val shuffle: Boolean,
        val favoriteSongIds: Set<String>,
        val playlists: List<Playlist>,
        val songsAdditionalMetadata: List<SongAdditionalMetadata>
    ): QueueScreenUiState

}

