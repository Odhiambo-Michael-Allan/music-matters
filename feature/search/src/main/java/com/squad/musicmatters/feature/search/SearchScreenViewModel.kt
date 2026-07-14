package com.squad.musicmatters.feature.search

import androidx.lifecycle.viewModelScope
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
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class SearchScreenViewModel @Inject constructor(
    private val player: MusicMattersPlayer,
    private val songsRepository: SongsRepository,
    private val albumsRepository: AlbumsRepository,
    private val artistsRepository: ArtistsRepository,
    private val genresRepository: GenresRepository,
    private val playlistsRepository: PlaylistsRepository,
    private val userDataRepository: UserDataRepository,
) : BaseViewModel(
    player = player,
    userDataRepository = userDataRepository,
    playlistsRepository = playlistsRepository,
) {

    private val _currentSearchQuery = MutableStateFlow( "" )
    val currentSearchQuery = _currentSearchQuery.asStateFlow()

    private val _currentSearchFilters = MutableStateFlow<List<SearchFilter>>( emptyList() )
    val currentSearchFilters = _currentSearchFilters.asStateFlow()

    val uiState: StateFlow<SearchScreenUiState> = combine(
        _currentSearchQuery,
        _currentSearchFilters,
        userDataRepository.userData
    ) { query, filters, userData ->
        Triple( query, filters, userData )
    }.flatMapLatest { ( query, filters, userData ) ->
        if ( query.isBlank() ) {
            return@flatMapLatest flowOf(
                SearchScreenUiState.Success(
                    songs = emptyList(),
                    albums = emptyList(),
                    artists = emptyList(),
                    genres = emptyList(),
                    playlists = emptyList()
                )
            )
        }

        val songsFlow = if ( filters.isEmpty() || filters.contains( SearchFilter.SONGS ) ) {
            songsRepository.searchSongsMatching(
                query = query,
                sortSongsBy = userData.sortSongsBy,
                sortSongsInReverse = userData.sortSongsReverse,
            )
        } else {
            flowOf( emptyList() )
        }
        val albumsFlow = if ( filters.isEmpty() || filters.contains( SearchFilter.ALBUMS ) ) {
            albumsRepository.searchAlbumsMatching(
                query = query,
                sortAlbumsBy = userData.sortAlbumsBy,
                sortAlbumsInReverse = userData.sortAlbumsReverse,
            )
        } else {
            flowOf( emptyList() )
        }
        val artistsFlow = if ( filters.isEmpty() || filters.contains( SearchFilter.ARTISTS ) ) {
            artistsRepository.searchArtistsMatching(
                query = query,
                sortArtistsBy = userData.sortArtistsBy,
                sortArtistsInReverse = userData.sortArtistsReverse,
            )
        } else {
            flowOf( emptyList() )
        }

        val genresFlow = if ( filters.isEmpty() || filters.contains( SearchFilter.GENRES ) ) {
            genresRepository.searchGenresMatching(
                query = query,
                sortGenresBy = userData.sortGenresBy,
                reverse = userData.sortGenresReverse,
            )
        } else {
            flowOf( emptyList() )
        }

        val playlistsFlow = if ( filters.isEmpty() || filters.contains( SearchFilter.PLAYLISTS ) ) {
            playlistsRepository.searchPlaylistsMatchingQuery(
                query = query,
                sortPlaylistsBy = userData.sortPlaylistsBy,
                sortPlaylistsInReverse = userData.sortPlaylistsReverse
            )
        } else {
            flowOf( emptyList() )
        }

        combine(
            songsFlow,
            albumsFlow,
            artistsFlow,
            genresFlow,
            playlistsFlow
        ) { songs, albums, artists, genres, playlists ->
            SearchScreenUiState.Success(
                songs = songs,
                albums = albums,
                artists = artists,
                genres = genres,
                playlists = playlists,
            )
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed( 5_000 ),
        initialValue = SearchScreenUiState.Loading,
    )

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

