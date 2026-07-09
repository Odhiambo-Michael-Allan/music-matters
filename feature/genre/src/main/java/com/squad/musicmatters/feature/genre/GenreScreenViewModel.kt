package com.squad.musicmatters.feature.genre

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.squad.musicmatters.core.data.repository.PlaylistRepository
import com.squad.musicmatters.core.data.repository.SongsMetadataRepository
import com.squad.musicmatters.core.data.repository.SongsRepository
import com.squad.musicmatters.core.datastore.PreferencesDataSource
import com.squad.musicmatters.core.media.connection.MusicMattersPlayer
import com.squad.musicmatters.core.model.Playlist
import com.squad.musicmatters.core.model.Song
import com.squad.musicmatters.core.model.SongMetadata
import com.squad.musicmatters.core.model.SortSongsBy
import com.squad.musicmatters.core.ui.BaseViewModel
import com.squad.musicmatters.feature.genre.navigation.GenreRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class GenreScreenViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    songsMetadataRepository: SongsMetadataRepository,
    songsRepository: SongsRepository,
    player: MusicMattersPlayer,
    preferencesDataSource: PreferencesDataSource,
    playlistRepository: PlaylistRepository,
) : BaseViewModel(
    player = player,
    preferencesDataSource = preferencesDataSource,
    playlistRepository = playlistRepository
) {

    val genreName = savedStateHandle.toRoute<GenreRoute>().genreName

    val uiState: StateFlow<GenreScreenUiState> = combine(
        preferencesDataSource.userData.flatMapLatest {
            songsRepository.fetchSongs(
                sortSongsBy = it.sortSongsBy,
                sortSongsInReverse = it.sortSongsReverse
            )
        },
        preferencesDataSource.userData,
        playlistRepository.fetchFavorites(),
        playlistRepository.fetchPlaylists(),
        songsMetadataRepository.fetchMetadata()
    ) { songs, userData, favoriteSongsPlaylist, playlists, metadata ->
        GenreScreenUiState.Success(
            genreName = genreName,
            songsInGenre = songs.filter {
                it.id in ( metadata.filter { it.genre == genreName }
                    .map(SongMetadata::songId ) )
            },
            sortSongsBy = userData.sortSongsBy,
            sortSongsInReverse = userData.sortSongsReverse,
            currentlyPlayingSongId = userData.currentlyPlayingSongId,
            favoriteSongIds = favoriteSongsPlaylist?.songIds ?: emptySet(),
            playlists = playlists,
            songsMetadata = metadata
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed( 5_000 ),
        initialValue = GenreScreenUiState.Loading
    )
    
}

sealed interface GenreScreenUiState {
    data object Loading : GenreScreenUiState
    data class Success(
        val genreName: String,
        val songsInGenre: List<Song>,
        val sortSongsBy: SortSongsBy,
        val sortSongsInReverse: Boolean,
        val currentlyPlayingSongId: String,
        val favoriteSongIds: Set<String>,
        val playlists: List<Playlist>,
        val songsMetadata: List<SongMetadata>,
    ): GenreScreenUiState
}