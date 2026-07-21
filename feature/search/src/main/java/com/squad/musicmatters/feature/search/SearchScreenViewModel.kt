package com.squad.musicmatters.feature.search

import androidx.lifecycle.viewModelScope
import com.squad.musicmatters.core.data.SearchRepository
import com.squad.musicmatters.core.data.repository.PlaylistsRepository
import com.squad.musicmatters.core.data.repository.SongsMetadataRepository
import com.squad.musicmatters.core.data.utils.combine
import com.squad.musicmatters.core.datastore.UserDataRepository
import com.squad.musicmatters.core.media.connection.MusicMattersPlayer
import com.squad.musicmatters.core.model.Album
import com.squad.musicmatters.core.model.Artist
import com.squad.musicmatters.core.model.Genre
import com.squad.musicmatters.core.model.Playlist
import com.squad.musicmatters.core.model.SearchFilter
import com.squad.musicmatters.core.model.Song
import com.squad.musicmatters.core.model.SongMetadata
import com.squad.musicmatters.core.model.UserData
import com.squad.musicmatters.core.ui.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class SearchScreenViewModel @Inject constructor(
    private val searchRepository: SearchRepository,
    playlistsRepository: PlaylistsRepository,
    userDataRepository: UserDataRepository,
    metadataRepository: SongsMetadataRepository,
    player: MusicMattersPlayer
) : BaseViewModel(
    player = player,
    userDataRepository = userDataRepository,
    playlistsRepository = playlistsRepository,
) {

    private val currentSearchQuery = MutableStateFlow( "" )
    private val _currentSearchFilter = MutableStateFlow(SearchFilter.ALL )
    val currentSearchFilter = _currentSearchFilter.asStateFlow()

    @Suppress( "UNCHECKED_CAST" )
    val uiState: StateFlow<SearchScreenUiState> = combine(
        currentSearchQuery.distinctUntilChanged { old, new ->  old == new },
        _currentSearchFilter,
        userDataRepository.userData,
        playlistsRepository.fetchFavorites(),
        playlistsRepository.fetchPlaylists(),
        metadataRepository.fetchMetadata(),
    ) { query, filter, userData, favorites, playlists, metadata ->
        SearchQueryParams(
            query = query,
            filter = filter,
            userData = userData,
            favoriteSongsIds = favorites?.songIds ?: emptySet(),
            playlists = playlists,
            metadata = metadata
        )
    }.flatMapLatest { params ->
        searchRepository.search(
            query = params.query,
            selectedSearchFilter = params.filter,
            userData = params.userData
        ).map { resultsMap ->
            SearchScreenUiState.Success(
                songs = ( resultsMap[ SearchFilter.SONGS ] as? List<Song> ) ?: emptyList(),
                albums = ( resultsMap[ SearchFilter.ALBUMS ] as? List<Album> ) ?: emptyList(),
                artists = ( resultsMap[ SearchFilter.ARTISTS ] as? List<Artist> ) ?: emptyList(),
                genres = ( resultsMap[ SearchFilter.GENRES ] as? List<Genre> ) ?: emptyList(),
                playlists = ( resultsMap[ SearchFilter.PLAYLISTS ] as? List<Playlist> )
                    ?: emptyList(),
                currentlyPlayingSongId = params.userData.currentlyPlayingSongId,
                favoriteSongIds = params.favoriteSongsIds,
                savedPlaylists = params.playlists,
                metadata = params.metadata
            )
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed( 5_000 ),
        initialValue = SearchScreenUiState.Loading,
    )

    fun onSearch( query: String ) {
        currentSearchQuery.value = query
    }

    fun onSearchFilterSelected( searchFilter: SearchFilter ) {
        _currentSearchFilter.value = searchFilter
    }

    fun onClearSearch() {
        currentSearchQuery.value = ""
    }

}

private data class SearchQueryParams(
    val query: String,
    val filter: SearchFilter,
    val userData: UserData,
    val favoriteSongsIds: Set<String>,
    val playlists: List<Playlist>,
    val metadata: List<SongMetadata>,
)

sealed interface SearchScreenUiState {
    data object Loading : SearchScreenUiState
    data class Success(
        val songs: List<Song>,
        val albums: List<Album>,
        val artists: List<Artist>,
        val genres: List<Genre>,
        val favoriteSongIds: Set<String>,
        val playlists: List<Playlist>,
        val metadata: List<SongMetadata>,
        val savedPlaylists: List<Playlist>,
        val currentlyPlayingSongId: String,
    ) : SearchScreenUiState
}

