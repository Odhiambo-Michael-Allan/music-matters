package com.squad.musicmatters.feature.search

import androidx.lifecycle.viewModelScope
import com.squad.musicmatters.core.data.SearchRepository
import com.squad.musicmatters.core.data.repository.AlbumsRepository
import com.squad.musicmatters.core.data.repository.ArtistsRepository
import com.squad.musicmatters.core.data.repository.GenresRepository
import com.squad.musicmatters.core.data.repository.PlaylistsRepository
import com.squad.musicmatters.core.data.repository.SongsRepository
import com.squad.musicmatters.core.datastore.UserDataRepository
import com.squad.musicmatters.core.media.connection.MusicMattersPlayer
import com.squad.musicmatters.core.model.Album
import com.squad.musicmatters.core.model.Artist
import com.squad.musicmatters.core.model.Genre
import com.squad.musicmatters.core.model.Playlist
import com.squad.musicmatters.core.model.SearchFilter
import com.squad.musicmatters.core.model.Song
import com.squad.musicmatters.core.ui.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class SearchScreenViewModel @Inject constructor(
    private val searchRepository: SearchRepository,
    playlistsRepository: PlaylistsRepository,
    userDataRepository: UserDataRepository,
    player: MusicMattersPlayer
) : BaseViewModel(
    player = player,
    userDataRepository = userDataRepository,
    playlistsRepository = playlistsRepository,
) {

    private val _currentSearchQuery = MutableStateFlow( "" )
    val currentSearchQuery = _currentSearchQuery.asStateFlow()

    private val _currentSearchFilter = MutableStateFlow(SearchFilter.ALL )
    val currentSearchFilter = _currentSearchFilter.asStateFlow()

    @Suppress( "UNCHECKED_CAST" )
    val uiState: StateFlow<SearchScreenUiState> = combine(
        _currentSearchQuery,
        _currentSearchFilter,
        userDataRepository.userData
    ) { query, filter, userData ->
        Triple( query, filter, userData )
    }.flatMapLatest { ( query, filter, userData ) ->
        searchRepository.search(
            query = query,
            selectedSearchFilter = filter,
            userData = userData
        ).map { resultsMap ->
            SearchScreenUiState.Success(
                songs = ( resultsMap[ SearchFilter.SONGS ] as? List<Song> ) ?: emptyList(),
                albums = ( resultsMap[ SearchFilter.ALBUMS ] as? List<Album> ) ?: emptyList(),
                artists = ( resultsMap[ SearchFilter.ARTISTS ] as? List<Artist> ) ?: emptyList(),
                genres = ( resultsMap[ SearchFilter.GENRES ] as? List<Genre> ) ?: emptyList(),
                playlists = ( resultsMap[ SearchFilter.PLAYLISTS ] as? List<Playlist> )
                    ?: emptyList()
            )
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed( 5_000 ),
        initialValue = SearchScreenUiState.Loading,
    )

    fun onSearch( query: String ) {
        _currentSearchQuery.value = query
    }

    fun onSearchFilterSelected( searchFilter: SearchFilter ) {
        _currentSearchFilter.value = searchFilter
    }

}

sealed interface SearchScreenUiState {
    data object Loading : SearchScreenUiState
    data class Success(
        val songs: List<Song>,
        val albums: List<Album>,
        val artists: List<Artist>,
        val genres: List<Genre>,
        val playlists: List<Playlist>,
    ) : SearchScreenUiState
}

