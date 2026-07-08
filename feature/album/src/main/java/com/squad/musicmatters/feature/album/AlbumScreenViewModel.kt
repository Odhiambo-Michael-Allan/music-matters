package com.squad.musicmatters.feature.album

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.squad.musicmatters.core.data.repository.AlbumsRepository
import com.squad.musicmatters.core.data.repository.PlaylistRepository
import com.squad.musicmatters.core.data.repository.SongsMetadataRepository
import com.squad.musicmatters.core.data.repository.SongsRepository
import com.squad.musicmatters.core.datastore.PreferencesDataSource
import com.squad.musicmatters.core.media.connection.MusicMattersPlayer
import com.squad.musicmatters.core.model.Album
import com.squad.musicmatters.core.model.Playlist
import com.squad.musicmatters.core.model.Song
import com.squad.musicmatters.core.model.SongMetadata
import com.squad.musicmatters.core.model.SortSongsBy
import com.squad.musicmatters.core.ui.BaseViewModel
import com.squad.musicmatters.feature.album.navigation.AlbumRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class AlbumScreenViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    albumsRepository: AlbumsRepository,
    songsRepository: SongsRepository,
    player: MusicMattersPlayer,
    preferencesDataSource: PreferencesDataSource,
    playlistRepository: PlaylistRepository,
    songsMetadataRepository: SongsMetadataRepository,
) : BaseViewModel(
    player = player,
    preferencesDataSource = preferencesDataSource,
    playlistRepository = playlistRepository,
) {

    val albumId = savedStateHandle.toRoute<AlbumRoute>().albumId

    val uiState: StateFlow<AlbumScreenUiState> = com.squad.musicmatters.core.data.utils.combine(
         albumsRepository.fetchAlbumWithId( albumId ),
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
    ) { album, songs, userData, favoriteSongsPlaylist, playlists, metadata ->
        AlbumScreenUiState.Success(
            album = album,
            songsInAlbum = songs.filter { it.albumId == albumId },
            sortSongsBy = userData.sortSongsBy,
            sortSongsInReverse = userData.sortSongsReverse,
            currentlyPlayingSongId = userData.currentlyPlayingSongId,
            favoriteSongIds = favoriteSongsPlaylist?.songIds ?: emptySet(),
            playlists = playlists,
            songsMetadata = metadata,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed( 5_000 ),
        initialValue = AlbumScreenUiState.Loading,
    )

}

sealed interface AlbumScreenUiState {
    data object Loading : AlbumScreenUiState
    data class Success(
        val album: Album,
        val songsInAlbum: List<Song>,
        val sortSongsBy: SortSongsBy,
        val currentlyPlayingSongId: String,
        val favoriteSongIds: Set<String>,
        val sortSongsInReverse: Boolean,
        val playlists: List<Playlist>,
        val songsMetadata: List<SongMetadata>,
    ) : AlbumScreenUiState
}