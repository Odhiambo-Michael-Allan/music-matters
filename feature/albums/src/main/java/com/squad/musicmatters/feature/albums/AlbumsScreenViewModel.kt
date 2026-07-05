package com.squad.musicmatters.feature.albums

import androidx.lifecycle.viewModelScope
import com.squad.musicmatters.core.data.repository.AlbumsRepository
import com.squad.musicmatters.core.data.repository.PlaylistRepository
import com.squad.musicmatters.core.datastore.PreferencesDataSource
import com.squad.musicmatters.core.media.connection.MusicMattersPlayer
import com.squad.musicmatters.core.model.Album
import com.squad.musicmatters.core.model.Playlist
import com.squad.musicmatters.core.model.SortAlbumsBy
import com.squad.musicmatters.core.ui.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class AlbumsScreenViewModel @Inject constructor(
    albumsRepository: AlbumsRepository,
    player: MusicMattersPlayer,
    preferencesDataSource: PreferencesDataSource,
    playlistRepository: PlaylistRepository,
) : BaseViewModel(
    player = player,
    preferencesDataSource = preferencesDataSource,
    playlistRepository = playlistRepository,
) {

    val uiState: StateFlow<AlbumsScreenUiState> =
        combine(
            preferencesDataSource.userData.flatMapLatest {
                albumsRepository.fetchAlbums(
                    sortAlbumsBy = it.sortAlbumsBy,
                    sortAlbumsInReverse = it.sortAlbumsReverse
                )
            },
            preferencesDataSource.userData,
            playlistRepository.fetchPlaylists()
        ) { albums, userData, playlists ->
            AlbumsScreenUiState.Success(
                albums = albums,
                sortAlbumsBy = userData.sortAlbumsBy,
                sortAlbumsInReverse = userData.sortAlbumsReverse,
                playlists = playlists
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed( 5_000 ),
            initialValue = AlbumsScreenUiState.Loading
        )

}

sealed interface AlbumsScreenUiState {
    data object Loading : AlbumsScreenUiState
    data class Success(
        val albums: List<Album>,
        val sortAlbumsBy: SortAlbumsBy,
        val sortAlbumsInReverse: Boolean,
        val playlists: List<Playlist>,
    ): AlbumsScreenUiState
}