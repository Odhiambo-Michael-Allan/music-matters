package com.squad.musicmatters.feature.albums

import androidx.lifecycle.viewModelScope
import com.squad.musicmatters.core.data.repository.AlbumsRepository
import com.squad.musicmatters.core.data.repository.PlaylistsRepository
import com.squad.musicmatters.core.data.repository.SongsRepository
import com.squad.musicmatters.core.datastore.UserDataRepository
import com.squad.musicmatters.core.media.connection.MusicMattersPlayer
import com.squad.musicmatters.core.model.Album
import com.squad.musicmatters.core.model.Playlist
import com.squad.musicmatters.core.model.Song
import com.squad.musicmatters.core.model.SortAlbumsBy
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
class AlbumsScreenViewModel @Inject constructor(
    private val userDataRepository: UserDataRepository,
    albumsRepository: AlbumsRepository,
    player: MusicMattersPlayer,
    playlistsRepository: PlaylistsRepository,
    songsRepository: SongsRepository,
) : BaseViewModel(
    player = player,
    userDataRepository = userDataRepository,
    playlistsRepository = playlistsRepository,
) {

    val uiState: StateFlow<AlbumsScreenUiState> =
        combine(
            userDataRepository.userData.flatMapLatest {
                albumsRepository.fetchAlbums(
                    sortAlbumsBy = it.sortAlbumsBy,
                    sortAlbumsInReverse = it.sortAlbumsReverse
                )
            },
            userDataRepository.userData,
            playlistsRepository.fetchPlaylists(),
            songsRepository.fetchSongs(),
        ) { albums, userData, playlists, songs ->
            AlbumsScreenUiState.Success(
                albums = albums,
                sortAlbumsBy = userData.sortAlbumsBy,
                sortAlbumsInReverse = userData.sortAlbumsReverse,
                playlists = playlists,
                songs = songs,
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed( 5_000 ),
            initialValue = AlbumsScreenUiState.Loading
        )

    fun onSortTypeChange( by: SortAlbumsBy ) {
        viewModelScope.launch { userDataRepository.setSortAlbumsBy( by ) }
    }

    fun onSortInReverseChange( reverse: Boolean ) {
        viewModelScope.launch { userDataRepository.setSortAlbumsInReverse( reverse ) }
    }

}

sealed interface AlbumsScreenUiState {
    data object Loading : AlbumsScreenUiState
    data class Success(
        val albums: List<Album>,
        val sortAlbumsBy: SortAlbumsBy,
        val sortAlbumsInReverse: Boolean,
        val playlists: List<Playlist>,
        val songs: List<Song>,
    ): AlbumsScreenUiState
}