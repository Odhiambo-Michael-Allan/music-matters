package com.squad.musicmatters.feature.folder

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.squad.musicmatters.core.data.repository.PlaylistsRepository
import com.squad.musicmatters.core.data.repository.SongsMetadataRepository
import com.squad.musicmatters.core.data.repository.SongsRepository
import com.squad.musicmatters.core.datastore.UserDataRepository
import com.squad.musicmatters.core.media.connection.MusicMattersPlayer
import com.squad.musicmatters.core.model.Folder
import com.squad.musicmatters.core.model.Playlist
import com.squad.musicmatters.core.model.Song
import com.squad.musicmatters.core.model.SongMetadata
import com.squad.musicmatters.core.model.SortSongsBy
import com.squad.musicmatters.core.model.directoryName
import com.squad.musicmatters.core.ui.BaseViewModel
import com.squad.musicmatters.feature.folder.navigation.FolderRoute
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject
import kotlin.io.path.Path
import kotlin.io.path.name

class FolderScreenViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    songsRepository: SongsRepository,
    player: MusicMattersPlayer,
    userDataRepository: UserDataRepository,
    playlistsRepository: PlaylistsRepository,
    songsMetadataRepository: SongsMetadataRepository
) : BaseViewModel(
    player = player,
    userDataRepository = userDataRepository,
    playlistsRepository = playlistsRepository,
) {

    val path = savedStateHandle.toRoute<FolderRoute>().path

    val uiState: StateFlow<FolderScreenUiState> = combine(
        userDataRepository.userData.flatMapLatest { userData ->
            songsRepository.fetchSongs(
                sortSongsBy = userData.sortSongsBy,
                sortSongsInReverse = userData.sortSongsReverse
            )
        },
        userDataRepository.userData,
        playlistsRepository.fetchFavorites(),
        playlistsRepository.fetchPlaylists(),
        songsMetadataRepository.fetchMetadata()
    ) { songs, userData, favorites, playlists, metadata ->
        FolderScreenUiState.Success(
            name = Path( path ).name,
            songsInFolder = songs.filter { song ->
                Path( song.path ).directoryName() == path
            },
            sortSongsBy = userData.sortSongsBy,
            sortSongsInReverse = userData.sortSongsReverse,
            currentlyPlayingSongId = userData.currentlyPlayingSongId,
            favoriteSongIds = favorites?.songIds ?: emptySet(),
            playlists = playlists,
            songsMetadata = metadata
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed( 5_000 ),
        initialValue = FolderScreenUiState.Loading
    )

}

sealed interface FolderScreenUiState {
    data object Loading : FolderScreenUiState
    data class Success(
        val name: String,
        val songsInFolder: List<Song>,
        val sortSongsBy: SortSongsBy,
        val sortSongsInReverse: Boolean,
        val currentlyPlayingSongId: String,
        val favoriteSongIds: Set<String>,
        val playlists: List<Playlist>,
        val songsMetadata: List<SongMetadata>,
    ): FolderScreenUiState
}