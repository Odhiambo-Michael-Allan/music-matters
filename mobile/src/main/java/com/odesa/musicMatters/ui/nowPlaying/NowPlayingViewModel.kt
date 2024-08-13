package com.odesa.musicMatters.ui.nowPlaying

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.odesa.musicMatters.core.common.connection.MusicServiceConnection
import com.odesa.musicMatters.core.common.connection.NOTHING_PLAYING
import com.odesa.musicMatters.core.common.media.extensions.artistTagSeparators
import com.odesa.musicMatters.core.common.media.extensions.toSong
import com.odesa.musicMatters.core.data.preferences.LoopMode
import com.odesa.musicMatters.core.data.preferences.impl.SettingsDefaults
import com.odesa.musicMatters.core.data.preferences.toRepeatMode
import com.odesa.musicMatters.core.data.repository.PlaylistRepository
import com.odesa.musicMatters.core.data.repository.SongsAdditionalMetadataRepository
import com.odesa.musicMatters.core.data.settings.SettingsRepository
import com.odesa.musicMatters.core.datatesting.songs.testSongs
import com.odesa.musicMatters.core.designsystem.theme.ThemeMode
import com.odesa.musicMatters.core.i8n.Language
import com.odesa.musicMatters.core.model.PlaylistInfo
import com.odesa.musicMatters.core.model.Song
import com.odesa.musicMatters.core.model.SongAdditionalMetadataInfo
import com.odesa.musicMatters.ui.BaseViewModel
import com.odesa.musicMatters.ui.components.PlaybackPosition
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber

class NowPlayingViewModel(
    private val musicServiceConnection: MusicServiceConnection,
    private val settingsRepository: SettingsRepository,
    private val playlistRepository: PlaylistRepository,
    songsAdditionalMetadataRepository: SongsAdditionalMetadataRepository,
) : BaseViewModel(
    settingsRepository = settingsRepository,
    playlistRepository = playlistRepository,
    musicServiceConnection = musicServiceConnection,
    songsAdditionalMetadataRepository = songsAdditionalMetadataRepository,
) {

    private val handler = Handler( Looper.getMainLooper() )
    private val _uiState = MutableStateFlow(
        NowPlayingScreenUiState(
            currentlyPlayingSong = getCurrentlyPlayingSong(),
            currentlyPlayingSongIndex = musicServiceConnection.currentlyPlayingMediaItemIndex.value,
            playbackPosition = PlaybackPosition.zero,
            queueSize = musicServiceConnection.mediaItemsInQueue.value.size,
            language = settingsRepository.language.value,
            currentlyPlayingSongIsFavorite = true,
            controlsLayoutIsDefault = settingsRepository.controlsLayoutIsDefault.value,
            isPlaying = musicServiceConnection.playbackState.value.isPlaying,
            miniPlayerShowTrackControls = settingsRepository.miniPlayerShowTrackControls.value,
            miniPlayerShowSeekControls = settingsRepository.miniPlayerShowSeekControls.value,
            nowPlayingShowSeekControls = settingsRepository.showNowPlayingSeekControls.value,
            showLyrics = settingsRepository.showLyrics.value,
            shuffle = settingsRepository.shuffle.value,
            currentLoopMode = settingsRepository.currentLoopMode.value,
            currentPlayingSpeed = settingsRepository.currentPlaybackSpeed.value,
            currentPlayingPitch = settingsRepository.currentPlaybackPitch.value,
            themeMode = settingsRepository.themeMode.value,
            textMarquee = settingsRepository.miniPlayerTextMarquee.value,
            showSamplingInfo = settingsRepository.showNowPlayingAudioInformation.value,
            playlistInfos = emptyList(),
            songsAdditionalMetadataList = emptyList(),
        )
    )

    val uiState = _uiState.asStateFlow()

    private val _updatePlaybackPosition = MutableStateFlow( false )
    var updatePlaybackPosition = _updatePlaybackPosition.asStateFlow()

    init {
        viewModelScope.launch { observeNowPlaying() }
        viewModelScope.launch { observePlaybackState() }
        viewModelScope.launch { observeUpdatePlaybackPosition() }
        viewModelScope.launch { observeQueueSize() }
        viewModelScope.launch { observeCurrentlyPlayingMediaItemIndex() }
        viewModelScope.launch { observeLanguage() }
        viewModelScope.launch { observeIsPlaying() }
        viewModelScope.launch { observeThemeMode() }
        viewModelScope.launch { observeTextMarquee() }
        viewModelScope.launch { observeShowTrackControls() }
        viewModelScope.launch { observeShowSeekControls() }
        viewModelScope.launch { observeNowPlayingShowSeekControls() }
        viewModelScope.launch { observeControlsLayoutIsDefault() }
        viewModelScope.launch { observeShowLyrics() }
        viewModelScope.launch { observeShuffle() }
        viewModelScope.launch { observeLoopMode() }
        viewModelScope.launch { observeCurrentPlayingSpeed() }
        viewModelScope.launch { observeCurrentPlayingPitch() }
        viewModelScope.launch { observeFavoriteSongs() }
        viewModelScope.launch { observeShowNowPlayingAudioInformation() }
        addOnPlaylistsChangeListener {
            _uiState.value = _uiState.value.copy(
                playlistInfos = it
            )
        }
        addOnSongsMetadataListChangeListener {
            _uiState.value = _uiState.value.copy(
                songsAdditionalMetadataList = it
            )
        }
    }

    private suspend fun observeNowPlaying() {
        musicServiceConnection.nowPlayingMediaItem.collect {
            val song = getCurrentlyPlayingSong()
            _uiState.value = _uiState.value.copy(
                currentlyPlayingSong = song,
                currentlyPlayingSongIsFavorite = playlistRepository.isFavorite(
                    song?.id ?: ""
                )
            )
        }
    }

    private suspend fun observePlaybackState() {
        musicServiceConnection.playbackState.collect {
            if ( it.isPlaying ) {
                _updatePlaybackPosition.value = true
                updatePlaybackState( musicServiceConnection.currentPlaybackPosition, it.duration )
            } else
                _updatePlaybackPosition.value = false
        }
    }

    private fun updatePlaybackState( played: Long, total: Long ) {
        _uiState.value = _uiState.value.copy(
            playbackPosition = PlaybackPosition( played, total )
        )
    }

    private suspend fun observeUpdatePlaybackPosition() {
        viewModelScope.launch {
            updatePlaybackPosition.collect {
                if ( it )
                    checkPlaybackPosition( 1000 )

            }
        }
    }

    /**
     * Internal function that recursively calls itself to check the current playback position and
     * updates the corresponding state value
     */
    private fun checkPlaybackPosition( delayMs: Long ): Boolean = handler.postDelayed( {
        val currentPosition = musicServiceConnection.currentPlaybackPosition
        Timber.tag( "NOW-PLAYING-VIEW-MODEL" ).d( "CURRENT POSITION: $currentPosition" )
        if ( _uiState.value.playbackPosition.played != currentPosition ) {
            updatePlaybackState(
                currentPosition,
                _uiState.value.playbackPosition.total
            )
        }
        if ( updatePlaybackPosition.value )
            checkPlaybackPosition( 1000 - ( currentPosition % 1000 ) )
    }, delayMs )


    private suspend fun observeQueueSize() {
        musicServiceConnection.mediaItemsInQueue.collect {
            _uiState.value = _uiState.value.copy(
                queueSize = it.size
            )
        }
    }

    private suspend fun observeCurrentlyPlayingMediaItemIndex() {
        musicServiceConnection.currentlyPlayingMediaItemIndex.collect {
            _uiState.value = _uiState.value.copy(
                currentlyPlayingSongIndex = it
            )
        }
    }

    private suspend fun observeLanguage() {
        settingsRepository.language.collect {
            _uiState.value = _uiState.value.copy(
                language = it
            )
        }
    }

    private suspend fun observeIsPlaying() {
        musicServiceConnection.playbackState.collect {
            _uiState.value = _uiState.value.copy(
                isPlaying = it.isPlaying
            )
        }
    }

    private suspend fun observeThemeMode() {
        settingsRepository.themeMode.collect {
            _uiState.value = _uiState.value.copy(
                themeMode = it
            )
        }
    }

    private suspend fun observeTextMarquee() {
        settingsRepository.miniPlayerTextMarquee.collect {
            _uiState.value = _uiState.value.copy(
                textMarquee = it
            )
        }
    }

    private suspend fun observeShowTrackControls() {
        settingsRepository.miniPlayerShowTrackControls.collect {
            _uiState.value = _uiState.value.copy(
                miniPlayerShowTrackControls = it
            )
        }
    }

    private suspend fun observeShowSeekControls() {
        settingsRepository.miniPlayerShowSeekControls.collect {
            _uiState.value = _uiState.value.copy(
                miniPlayerShowSeekControls = it
            )
        }
    }

    private suspend fun observeNowPlayingShowSeekControls() {
        settingsRepository.showNowPlayingSeekControls.collect {
            _uiState.value = _uiState.value.copy(
                nowPlayingShowSeekControls = it
            )
        }
    }

    private suspend fun observeControlsLayoutIsDefault() {
        settingsRepository.controlsLayoutIsDefault.collect {
            _uiState.value = _uiState.value.copy(
                controlsLayoutIsDefault = it
            )
        }
    }

    private suspend fun observeShowLyrics() {
        settingsRepository.showLyrics.collect {
            _uiState.value = _uiState.value.copy(
                showLyrics = it
            )
        }
    }

    private suspend fun observeShuffle() {
        settingsRepository.shuffle.collect { shuffle ->
            _uiState.value = _uiState.value.copy(
                shuffle = shuffle
            )
            if ( shuffle ) musicServiceConnection.shuffleSongsInQueue()
        }
    }

    private suspend fun observeLoopMode() {
        settingsRepository.currentLoopMode.collect {
            _uiState.value = _uiState.value.copy(
                currentLoopMode = it
            )
            musicServiceConnection.setRepeatMode( it.toRepeatMode() )
        }
    }

    private suspend fun observeCurrentPlayingSpeed() {
        settingsRepository.currentPlaybackSpeed.collect {
            _uiState.value = _uiState.value.copy(
                currentPlayingSpeed = it
            )
            musicServiceConnection.setPlaybackSpeed( it )
        }
    }

    private suspend fun observeCurrentPlayingPitch() {
        settingsRepository.currentPlaybackPitch.collect {
            _uiState.value = _uiState.value.copy(
                currentPlayingPitch = it
            )
            musicServiceConnection.setPlaybackPitch( it )
        }
    }

    private suspend fun observeFavoriteSongs() {
        playlistRepository.favoritesPlaylistInfo.collect {
            _uiState.value = _uiState.value.copy(
                currentlyPlayingSongIsFavorite = playlistRepository.isFavorite(
                    getCurrentlyPlayingSong()?.id ?: ""  )
            )
        }
    }

    private suspend fun observeShowNowPlayingAudioInformation() {
        settingsRepository.showNowPlayingAudioInformation.collect {
            _uiState.value = _uiState.value.copy(
                showSamplingInfo = it
            )
        }
    }

    fun playPause() {
        musicServiceConnection.playPause()
    }

    fun playPreviousSong(): Boolean {
        return musicServiceConnection.playPreviousSong()
    }

    fun playNextSong(): Boolean {
        return musicServiceConnection.playNextSong()
    }

    fun fastRewind() {
        musicServiceConnection.seekBack()
    }

    fun fastForward() {
        musicServiceConnection.seekForward()
    }

    fun onSeekStarted() {
        _updatePlaybackPosition.value = false
    }

    fun onSeekEnd( position: Long ) {
        musicServiceConnection.seekTo( position )
        _updatePlaybackPosition.value = true
    }

    fun toggleLoopMode() {
        val currentLoopModePosition = LoopMode.entries.indexOf(
            settingsRepository.currentLoopMode.value )
        val nextLoopModePosition = ( currentLoopModePosition + 1 ) % LoopMode.entries.size
        viewModelScope.launch {
            settingsRepository.setCurrentLoopMode( LoopMode.entries[ nextLoopModePosition ] )
        }
    }

    fun toggleShuffleMode() {
        val currentShuffleMode = _uiState.value.shuffle
        viewModelScope.launch {
            settingsRepository.setShuffle( !currentShuffleMode )
        }
    }

    fun onPlayingSpeedChange( playingSpeed: Float ) {
        viewModelScope.launch {
            settingsRepository.setCurrentPlayingSpeed( playingSpeed )
        }
    }

    fun onPlayingPitchChange( playingPitch: Float ) {
        viewModelScope.launch {
            settingsRepository.setCurrentPlayingPitch( playingPitch )
        }
    }

    override fun onCleared() {
        super.onCleared()
        _updatePlaybackPosition.value = false // Stop updating the position.
    }

    private fun getCurrentlyPlayingSong() =
        musicServiceConnection.nowPlayingMediaItem.value.toSong( artistTagSeparators )

}

class NowPlayingViewModelFactory(
    private val musicServiceConnection: MusicServiceConnection,
    private val settingsRepository: SettingsRepository,
    private val playlistRepository: PlaylistRepository,
    private val songsAdditionalMetadataRepository: SongsAdditionalMetadataRepository,
) : ViewModelProvider.NewInstanceFactory() {

    @Suppress( "unchecked_cast" )
    override fun <T : ViewModel> create( modelClass: Class<T> ): T {
        return NowPlayingViewModel(
            musicServiceConnection = musicServiceConnection,
            settingsRepository = settingsRepository,
            playlistRepository = playlistRepository,
            songsAdditionalMetadataRepository = songsAdditionalMetadataRepository,
        ) as T
    }
}

data class NowPlayingScreenUiState(
    val currentlyPlayingSong: Song,
    val playbackPosition: PlaybackPosition,
    val currentlyPlayingSongIndex: Int,
    val queueSize: Int,
    val language: Language,
    val currentlyPlayingSongIsFavorite: Boolean,
    val controlsLayoutIsDefault: Boolean,
    val isPlaying: Boolean,
    val miniPlayerShowTrackControls: Boolean,
    val miniPlayerShowSeekControls: Boolean,
    val nowPlayingShowSeekControls: Boolean,
    val showLyrics: Boolean,
    val shuffle: Boolean,
    val currentLoopMode: LoopMode,
    val currentPlayingSpeed: Float,
    val currentPlayingPitch: Float,
    val themeMode: ThemeMode,
    val textMarquee: Boolean,
    val showSamplingInfo: Boolean,
    val playlistInfos: List<PlaylistInfo>,
    val songsAdditionalMetadataList: List<SongAdditionalMetadataInfo>
)

internal val testNowPlayingScreenUiState = NowPlayingScreenUiState(
    currentlyPlayingSong = testSongs.first(),
    playbackPosition = PlaybackPosition( 3, 5 ),
    currentlyPlayingSongIndex = 100,
    queueSize = 150,
    language = SettingsDefaults.language,
    currentlyPlayingSongIsFavorite = true,
    controlsLayoutIsDefault = false,
    isPlaying = false,
    miniPlayerShowTrackControls = true,
    miniPlayerShowSeekControls = true,
    nowPlayingShowSeekControls = true,
    showLyrics = false,
    shuffle = true,
    currentLoopMode = LoopMode.Song,
    currentPlayingSpeed = 1f,
    currentPlayingPitch = 1f,
    themeMode = SettingsDefaults.themeMode,
    textMarquee = true,
    showSamplingInfo = true,
    playlistInfos = emptyList(),
    songsAdditionalMetadataList = emptyList(),
)


private const val POSITION_UPDATE_INTERVAL_MILLIS = 1L
