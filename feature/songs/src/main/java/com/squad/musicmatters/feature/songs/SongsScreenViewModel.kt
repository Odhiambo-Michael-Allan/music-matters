package com.squad.musicmatters.feature.songs

import androidx.lifecycle.viewModelScope
import com.squad.musicmatters.core.media.connection.MusicServiceConnection
import com.squad.musicmatters.core.data.repository.PlaylistRepository
import com.squad.musicmatters.core.data.repository.SongsAdditionalMetadataRepository
import com.squad.musicmatters.core.data.repository.SongsRepository
import com.squad.musicmatters.core.datastore.PreferencesDataSource
import com.squad.musicmatters.core.model.Playlist
import com.squad.musicmatters.core.model.Song
import com.squad.musicmatters.core.model.SongAdditionalMetadata
import com.squad.musicmatters.core.model.SortSongsBy
import com.squad.musicmatters.core.model.ThemeMode
import com.squad.musicmatters.core.ui.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class SongsScreenViewModel @Inject constructor(
    songsRepository: SongsRepository,
    preferencesDataSource: PreferencesDataSource,
    playlistRepository: PlaylistRepository,
    musicServiceConnection: MusicServiceConnection,
    songsAdditionalMetadataRepository: SongsAdditionalMetadataRepository
) : BaseViewModel(
    player = musicServiceConnection,
    preferencesDataSource = preferencesDataSource,
    playlistRepository = playlistRepository,
    songsAdditionalMetadataRepository = songsAdditionalMetadataRepository
) {

    val uiState: StateFlow<SongsScreenUiState> = com.squad.musicmatters.core.data.utils.combine(
        preferencesDataSource.userData.flatMapLatest {
            songsRepository.fetchSongs(
                sortSongsBy = it.sortSongsBy,
                sortSongsInReverse = it.sortSongsReverse
            )
        },
        preferencesDataSource.userData,
        musicServiceConnection.playerState,
        playlistRepository.fetchFavorites(),
        playlistRepository.fetchPlaylists(),
        songsAdditionalMetadataRepository.fetchAdditionalMetadataEntries()
    ) { songs,
        userData,
        playerState,
        favorites,
        playlists,
        metadata
        ->

        SongsScreenUiState.Success(
            songs = songs,
            sortSongsBy = userData.sortSongsBy,
            sortSongsInReverse = userData.sortSongsReverse,
            themeMode = userData.themeMode,
            currentlyPlayingSongId = playerState.currentlyPlayingSongId ?: "",
            favoriteSongIds = favorites?.songIds ?: emptySet(),
            playlists = playlists,
            songsAdditionalMetadata = metadata
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed( 5_000 ),
        initialValue = SongsScreenUiState.Loading
    )

}

sealed interface SongsScreenUiState {
    data object Loading : SongsScreenUiState
    data class Success(
        val songs: List<Song>,
        val sortSongsBy: SortSongsBy,
        val sortSongsInReverse: Boolean,
        val themeMode: ThemeMode,
        val currentlyPlayingSongId: String,
        val favoriteSongIds: Set<String>,
        val playlists: List<Playlist>,
        val songsAdditionalMetadata: List<SongAdditionalMetadata>
    ) : SongsScreenUiState
}

