package com.squad.musicmatters.feature.playlist

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.squad.musicmatters.core.data.repository.PlaylistsRepository
import com.squad.musicmatters.core.data.repository.SongsMetadataRepository
import com.squad.musicmatters.core.data.repository.SongsRepository
import com.squad.musicmatters.core.datastore.UserPreferencesRepository
import com.squad.musicmatters.core.media.connection.MusicMattersPlayer
import com.squad.musicmatters.core.model.Playlist
import com.squad.musicmatters.core.model.Song
import com.squad.musicmatters.core.model.SongMetadata
import com.squad.musicmatters.core.model.SortSongsBy
import com.squad.musicmatters.core.ui.BaseViewModel
import com.squad.musicmatters.feature.playlist.navigation.PlaylistRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class PlaylistScreenViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    playlistsRepository: PlaylistsRepository,
    songsRepository: SongsRepository,
    userPreferencesRepository: UserPreferencesRepository,
    songsMetadataRepository: SongsMetadataRepository,
    player: MusicMattersPlayer,
) : BaseViewModel(
    player = player,
    userPreferencesRepository = userPreferencesRepository,
    playlistsRepository = playlistsRepository
) {

    val playlistId = savedStateHandle.toRoute<PlaylistRoute>().playlistId

    val uiState: StateFlow<PlaylistScreenUiState> = com.squad.musicmatters.core.data.utils.combine(
        playlistsRepository.fetchPlaylistWithId( playlistId ),
        userPreferencesRepository.userData.flatMapLatest {
            songsRepository.fetchSongs(
                sortSongsBy = it.sortSongsBy,
                sortSongsInReverse = it.sortSongsReverse
            )
        },
        userPreferencesRepository.userData,
        playlistsRepository.fetchFavorites(),
        playlistsRepository.fetchPlaylists(),
        songsMetadataRepository.fetchMetadata()
    ) { playlist, songs, userData, favoriteSongsPlaylist, playlists, metadata ->
        PlaylistScreenUiState.Success(
            playlist = playlist,
            songsInPlaylist = songs.filter { it.id in playlist.songIds },
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
        initialValue = PlaylistScreenUiState.Loading,
    )

}

sealed interface PlaylistScreenUiState {
    data object Loading : PlaylistScreenUiState
    data class Success(
        val playlist: Playlist,
        val songsInPlaylist: List<Song>,
        val sortSongsBy: SortSongsBy,
        val sortSongsInReverse: Boolean,
        val playlists: List<Playlist>,
        val currentlyPlayingSongId: String,
        val songsMetadata: List<SongMetadata>,
        val favoriteSongIds: Set<String>,
    ): PlaylistScreenUiState
}