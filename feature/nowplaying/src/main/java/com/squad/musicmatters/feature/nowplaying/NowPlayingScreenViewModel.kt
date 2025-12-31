package com.squad.musicmatters.feature.nowplaying

import androidx.lifecycle.viewModelScope
import com.squad.musicmatters.core.media.connection.MusicServiceConnection
import com.squad.musicmatters.core.data.repository.PlaylistRepository
import com.squad.musicmatters.core.data.repository.QueueRepository
import com.squad.musicmatters.core.data.repository.SongsAdditionalMetadataRepository
import com.squad.musicmatters.core.data.utils.combine
import com.squad.musicmatters.core.datastore.PreferencesDataSource
import com.squad.musicmatters.core.i8n.English
import com.squad.musicmatters.core.i8n.Language
import com.squad.musicmatters.core.media.connection.PlaybackPosition
import com.squad.musicmatters.core.media.connection.PlayerState
import com.squad.musicmatters.core.model.LoopMode
import com.squad.musicmatters.core.model.Playlist
import com.squad.musicmatters.core.model.Song
import com.squad.musicmatters.core.model.SongAdditionalMetadataInfo
import com.squad.musicmatters.core.model.UserData
import com.squad.musicmatters.core.ui.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NowPlayingScreenViewModel @Inject constructor(
    private val player: MusicServiceConnection,
    private val preferencesDataSource: PreferencesDataSource,
    private val playlistRepository: PlaylistRepository,
    private val playbackPositionUpdater: PlaybackPositionUpdater,
    songsAdditionalMetadataRepository: SongsAdditionalMetadataRepository,
    queueRepository: QueueRepository,
) : BaseViewModel(
    preferencesDataSource = preferencesDataSource,
    playlistRepository = playlistRepository,
    player = player,
    songsAdditionalMetadataRepository = songsAdditionalMetadataRepository,
) {

    val uiState: StateFlow<NowPlayingScreenUiState> =
        combine(
            player.playerState,
            preferencesDataSource.userData,
            queueRepository.fetchSongsInQueueSortedByPosition(),
            player.playerState.map { it.currentlyPlayingSongId }.flatMapLatest { songId ->
                playlistRepository.isFavorite( songId ?: "" )
            },
            playlistRepository.fetchPlaylists(),
            songsAdditionalMetadataRepository.fetchAdditionalMetadataEntries()
        ) {
            playerState,
                userData,
                queue,
                currentlyPlayingSongIsFavorite,
                playlists,
                metadata ->

            NowPlayingScreenUiState.Success(
                playerState = playerState,
                userData = userData,
                queue = queue,
                currentlyPlayingSongIsFavorite = currentlyPlayingSongIsFavorite,
                playlists = playlists,
                songsAdditionalMetadataList = metadata
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed( 5_000 ),
            initialValue = NowPlayingScreenUiState.Loading
        )

    val playbackPosition: StateFlow<PlaybackPosition> =
        playbackPositionUpdater.playbackPosition.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed( 5_000 ),
            initialValue = PlaybackPosition.ZERO
        )

    fun playPause() {
        player.playPause()
    }

    fun playPreviousSong(): Boolean {
        return player.playPreviousSong()
    }

    fun playNextSong(): Boolean {
        return player.playNextSong()
    }

    fun fastRewind() {
        player.seekBack()
    }

    fun fastForward() {
        player.seekForward()
    }

    fun onSeekStarted() {
        playbackPositionUpdater.stopPeriodicUpdates()
    }

    fun onSeekEnd( position: Long ) {
        playbackPositionUpdater.startPeriodicUpdates()
        player.seekTo( position )
    }

    fun setLoopMode( currentLoopMode: LoopMode ) {
        val currentLoopModePosition = LoopMode.entries.indexOf( currentLoopMode )
        val nextLoopModePosition = ( currentLoopModePosition + 1 ) % LoopMode.entries.size
        viewModelScope.launch {
            preferencesDataSource.setLoopMode( LoopMode.entries[ nextLoopModePosition ] )
        }
    }

    fun setShuffleMode( shuffle: Boolean ) {
        viewModelScope.launch {
            preferencesDataSource.setShuffle( shuffle )
        }
    }

    fun onPlayingSpeedChange( speed: Float ) {
        viewModelScope.launch {
            preferencesDataSource.setPlaybackSpeed( speed )
        }
    }

    fun onPlayingPitchChange( pitch: Float ) {
        viewModelScope.launch {
            preferencesDataSource.setPlaybackPitch( pitch )
        }
    }

    override fun onCleared() {
        super.onCleared()
        playbackPositionUpdater.cleanUp()
    }

}

sealed interface NowPlayingScreenUiState {
    data object Loading : NowPlayingScreenUiState
    data class Success(
        val userData: UserData,
        val queue: List<Song>,
        val currentlyPlayingSongIsFavorite: Boolean,
        val playerState: PlayerState,
        val playlists: List<Playlist>,
        val songsAdditionalMetadataList: List<SongAdditionalMetadataInfo>,
        val language: Language = English,
    ) : NowPlayingScreenUiState
}
