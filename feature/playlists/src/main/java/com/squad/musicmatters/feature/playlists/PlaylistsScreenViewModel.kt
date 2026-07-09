package com.squad.musicmatters.feature.playlists

import com.squad.musicmatters.core.data.repository.PlaylistsRepository
import com.squad.musicmatters.core.data.repository.SongsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class PlaylistsScreenViewModel @Inject constructor(
    private val songsRepository: SongsRepository,
    private val playlistsRepository: PlaylistsRepository,
) {
}