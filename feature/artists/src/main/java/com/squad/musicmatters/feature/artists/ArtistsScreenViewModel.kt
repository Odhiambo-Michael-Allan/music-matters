package com.squad.musicmatters.feature.artists

import androidx.lifecycle.viewModelScope
import com.squad.musicmatters.core.data.repository.ArtistsRepository
import com.squad.musicmatters.core.data.repository.PlaylistsRepository
import com.squad.musicmatters.core.data.repository.SongsRepository
import com.squad.musicmatters.core.datastore.UserDataRepository
import com.squad.musicmatters.core.media.connection.MusicMattersPlayer
import com.squad.musicmatters.core.model.Artist
import com.squad.musicmatters.core.model.Playlist
import com.squad.musicmatters.core.model.Song
import com.squad.musicmatters.core.model.SortArtistsBy
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
class ArtistsScreenViewModel @Inject constructor(
    private val userDataRepository: UserDataRepository,
    artistsRepository: ArtistsRepository,
    player: MusicMattersPlayer,
    playlistsRepository: PlaylistsRepository,
    songsRepository: SongsRepository
) : BaseViewModel(
    player = player,
    userDataRepository = userDataRepository,
    playlistsRepository = playlistsRepository
) {

    val uiState: StateFlow<ArtistsScreenUiState> =
        combine(
            userDataRepository.userData.flatMapLatest {
                artistsRepository.fetchArtists(
                    sortArtistsBy = it.sortArtistsBy,
                    sortArtistsInReverse = it.sortArtistsReverse
                )
            },
            userDataRepository.userData,
            playlistsRepository.fetchPlaylists(),
            songsRepository.fetchSongs()
        ) { artists, userData, playlists, songs ->
            ArtistsScreenUiState.Success(
                artists = artists,
                sortArtistsBy = userData.sortArtistsBy,
                sortArtistsInReverse = userData.sortArtistsReverse,
                playlists = playlists,
                songs = songs
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed( 5_000 ),
            initialValue = ArtistsScreenUiState.Loading
        )

    fun onSortTypeChange( by: SortArtistsBy ) {
        viewModelScope.launch { userDataRepository.setSortArtistsBy( by ) }
    }

    fun onSortInReverseChange( reverse: Boolean ) {
        viewModelScope.launch { userDataRepository.setSortArtistsInReverse( reverse ) }
    }

}

sealed interface ArtistsScreenUiState {
    data object Loading : ArtistsScreenUiState
    data class Success(
        val artists: List<Artist>,
        val sortArtistsBy: SortArtistsBy,
        val sortArtistsInReverse: Boolean,
        val playlists: List<Playlist>,
        val songs: List<Song>,
    ): ArtistsScreenUiState
}