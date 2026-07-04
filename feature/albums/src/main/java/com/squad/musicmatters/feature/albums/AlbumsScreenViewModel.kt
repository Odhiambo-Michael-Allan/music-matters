package com.squad.musicmatters.feature.albums

import androidx.lifecycle.ViewModel
import com.squad.musicmatters.core.data.repository.PlaylistRepository
import com.squad.musicmatters.core.datastore.PreferencesDataSource
import com.squad.musicmatters.core.media.connection.MusicMattersPlayer
import com.squad.musicmatters.core.model.Album
import com.squad.musicmatters.core.ui.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AlbumsScreenViewModel @Inject constructor(
    player: MusicMattersPlayer,
    preferencesDataSource: PreferencesDataSource,
    playlistRepository: PlaylistRepository,
) : BaseViewModel(
    player = player,
    preferencesDataSource = preferencesDataSource,
    playlistRepository = playlistRepository,
) {



}

sealed interface AlbumsScreenUiState {
    data object Loading : AlbumsScreenUiState
    data class Success(
        val albums: List<Album>
    ): AlbumsScreenUiState
}