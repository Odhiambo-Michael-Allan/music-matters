package com.squad.musicmatters.feature.album

import androidx.lifecycle.ViewModel
import com.squad.musicmatters.core.data.repository.PlaylistRepository
import com.squad.musicmatters.core.datastore.PreferencesDataSource
import com.squad.musicmatters.core.media.connection.MusicMattersPlayer
import com.squad.musicmatters.core.model.Album
import com.squad.musicmatters.core.model.Playlist
import com.squad.musicmatters.core.model.Song
import com.squad.musicmatters.core.model.SongAdditionalMetadata
import com.squad.musicmatters.core.model.SortSongsBy
import com.squad.musicmatters.core.ui.BaseViewModel

class AlbumScreenViewModel(
    player: MusicMattersPlayer,
    preferencesDataSource: PreferencesDataSource,
    playlistRepository: PlaylistRepository,
) : BaseViewModel(
    player = player,
    preferencesDataSource = preferencesDataSource,
    playlistRepository = playlistRepository,
) {}

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
        val songsAdditionalMetadata: List<SongAdditionalMetadata>,
    ) : AlbumScreenUiState
}