package com.squad.musicmatters.feature.playlists

import androidx.lifecycle.viewModelScope
import com.squad.musicmatters.core.data.repository.PlaylistsRepository
import com.squad.musicmatters.core.data.repository.SongsRepository
import com.squad.musicmatters.core.datastore.UserPreferencesRepository
import com.squad.musicmatters.core.media.connection.MusicMattersPlayer
import com.squad.musicmatters.core.model.Playlist
import com.squad.musicmatters.core.model.Song
import com.squad.musicmatters.core.model.SortPlaylistsBy
import com.squad.musicmatters.core.ui.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlaylistsScreenViewModel @Inject constructor(
    private val userPreferencesRepository: UserPreferencesRepository,
    private val playlistsRepository: PlaylistsRepository,
    songsRepository: SongsRepository,
    player: MusicMattersPlayer,
) : BaseViewModel(
    player = player,
    userPreferencesRepository = userPreferencesRepository,
    playlistsRepository = playlistsRepository
) {

    val uiState: StateFlow<PlaylistsScreenUiState> =
        combine(
            userPreferencesRepository.userData.flatMapLatest {
                playlistsRepository.fetchPlaylists(
                    sortPlaylistsBy = it.sortPlaylistsBy,
                    sortInReverse = it.sortPlaylistsReverse
                )
            },
            userPreferencesRepository.userData,
            songsRepository.fetchSongs()
        ) { playlists, userData, songs ->
            PlaylistsScreenUiState.Success(
                playlists = playlists.map {
                    it.copy(
                        artworkUri = songs
                            .filter { song -> song.id in it.songIds }
                            .firstOrNull { song -> !song.artworkUri.isNullOrBlank() }
                            ?.artworkUri
                    )
                },
                sortPlaylistsBy = userData.sortPlaylistsBy,
                sortPlaylistsInReverse = userData.sortPlaylistsReverse,
                songs = songs,
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed( 5_000 ),
            initialValue = PlaylistsScreenUiState.Loading
        )

    fun onSortTypeChange( by: SortPlaylistsBy ) {
        viewModelScope.launch { userPreferencesRepository.setSortPlaylistsBy( by ) }
    }

    fun onSortInReverseChange( reverse: Boolean ) {
        viewModelScope.launch { userPreferencesRepository.setSortPlaylistsInReverse( reverse ) }
    }

    fun deletePlaylist( playlist: Playlist ) {
        viewModelScope.launch { playlistsRepository.deletePlaylist( playlist ) }
    }

}

sealed interface PlaylistsScreenUiState {
    data object Loading : PlaylistsScreenUiState
    data class Success(
        val playlists: List<Playlist>,
        val sortPlaylistsBy: SortPlaylistsBy,
        val sortPlaylistsInReverse: Boolean,
        val songs: List<Song>,
    ): PlaylistsScreenUiState
}