package com.odesa.musicMatters.ui.playlists

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.odesa.musicMatters.core.common.connection.MusicServiceConnection
import com.odesa.musicMatters.core.data.preferences.SortPlaylistsBy
import com.odesa.musicMatters.core.data.preferences.impl.SettingsDefaults
import com.odesa.musicMatters.core.data.repository.PlaylistRepository
import com.odesa.musicMatters.core.data.repository.SongsAdditionalMetadataRepository
import com.odesa.musicMatters.core.data.settings.SettingsRepository
import com.odesa.musicMatters.core.datatesting.playlists.testPlaylistInfos
import com.odesa.musicMatters.core.designsystem.theme.ThemeMode
import com.odesa.musicMatters.core.i8n.English
import com.odesa.musicMatters.core.i8n.Language
import com.odesa.musicMatters.core.model.PlaylistInfo
import com.odesa.musicMatters.core.model.Song
import com.odesa.musicMatters.ui.BaseViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PlaylistsViewModel(
    private val musicServiceConnection: MusicServiceConnection,
    private val settingsRepository: SettingsRepository,
    private val playlistRepository: PlaylistRepository,
    songsAdditionalMetadataRepository: SongsAdditionalMetadataRepository,
) : BaseViewModel(
    musicServiceConnection = musicServiceConnection,
    settingsRepository = settingsRepository,
    playlistRepository = playlistRepository,
    songsAdditionalMetadataRepository = songsAdditionalMetadataRepository
) {

    private val _uiState = MutableStateFlow(
        PlaylistsScreenUiState(
            playlistInfos = playlistRepository.playlists.value,
            sortPlaylistsBy = settingsRepository.sortPlaylistsBy.value,
            sortPlaylistsInReverse = settingsRepository.sortPlaylistsInReverse.value,
            songs = emptyList(),
            isLoadingSongs = musicServiceConnection.isInitializing.value,
            language = settingsRepository.language.value,
            themeMode = settingsRepository.themeMode.value
        )
    )
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch { observeMusicServiceConnectionInitializedStatus() }
        viewModelScope.launch { observeSongs() }
        viewModelScope.launch { observePlaylists() }
        viewModelScope.launch { observeLanguageChange() }
        viewModelScope.launch { observeThemeModeChange() }
        viewModelScope.launch { observeSortPlaylistsBy() }
        viewModelScope.launch { observeSortPlaylistsInReverse() }
    }

    private suspend fun observeMusicServiceConnectionInitializedStatus() {
        musicServiceConnection.isInitializing.collect {
            _uiState.value = _uiState.value.copy(
                isLoadingSongs = it
            )
        }
    }

    private suspend fun observeSongs() {
        musicServiceConnection.cachedSongs.collect {
            _uiState.value = _uiState.value.copy(
                songs = it
            )
        }
    }

    private suspend fun observePlaylists() {
        playlistRepository.playlists.collect {
            _uiState.value = _uiState.value.copy(
                playlistInfos = it.sortPlaylists(
                    sortPlaylistsBy = settingsRepository.sortPlaylistsBy.value,
                    reverse = settingsRepository.sortPlaylistsInReverse.value
                )
            )
        }
    }

    private suspend fun observeLanguageChange() {
        settingsRepository.language.collect {
            _uiState.value = _uiState.value.copy (
                language = it
            )
        }
    }

    private suspend fun observeThemeModeChange() {
        settingsRepository.themeMode.collect {
            _uiState.value = _uiState.value.copy(
                themeMode = it
            )
        }
    }

    private suspend fun observeSortPlaylistsBy() {
        viewModelScope.launch {
            settingsRepository.sortPlaylistsBy.collect {
                _uiState.value = _uiState.value.copy(
                    sortPlaylistsBy = it,
                    playlistInfos = playlistRepository.playlists.value.sortPlaylists(
                        sortPlaylistsBy = it,
                        reverse = settingsRepository.sortPlaylistsInReverse.value
                    )
                )
            }
        }
    }

    private suspend fun observeSortPlaylistsInReverse() {
        viewModelScope.launch {
            settingsRepository.sortPlaylistsInReverse.collect {
                _uiState.value = _uiState.value.copy(
                    sortPlaylistsInReverse = it,
                    playlistInfos = playlistRepository.playlists.value.sortPlaylists(
                        sortPlaylistsBy = settingsRepository.sortPlaylistsBy.value,
                        reverse = it
                    )
                )
            }
        }
    }

    fun setSortPlaylistsBy( sortPlaylistsBy: SortPlaylistsBy ) {
        viewModelScope.launch { settingsRepository.setSortPlaylistsBy( sortPlaylistsBy ) }
    }

    fun setSortPlaylistsInReverse( reverse: Boolean ) {
        viewModelScope.launch { settingsRepository.setSortPlaylistsInReverse( reverse ) }
    }
}

data class PlaylistsScreenUiState(
    val songs: List<Song>,
    val playlistInfos: List<PlaylistInfo>,
    val sortPlaylistsBy: SortPlaylistsBy,
    val sortPlaylistsInReverse: Boolean,
    val isLoadingSongs: Boolean,
    val language: Language,
    val themeMode: ThemeMode,
)

internal val testPlaylistsScreenUiState = PlaylistsScreenUiState(
    songs = emptyList(),
    playlistInfos = testPlaylistInfos,
    sortPlaylistsBy = SettingsDefaults.sortPlaylistsBy,
    sortPlaylistsInReverse = SettingsDefaults.SORT_PLAYLISTS_IN_REVERSE,
    isLoadingSongs = false,
    language = English,
    themeMode = SettingsDefaults.themeMode
)

private fun List<PlaylistInfo>.sortPlaylists(sortPlaylistsBy: SortPlaylistsBy, reverse: Boolean ) =
    when ( sortPlaylistsBy ) {
        SortPlaylistsBy.TITLE -> if ( reverse ) sortedByDescending { it.title } else sortedBy { it.title }
        SortPlaylistsBy.TRACKS_COUNT -> if ( reverse ) sortedByDescending { it.songIds.size } else sortedBy { it.songIds.size }
        SortPlaylistsBy.CUSTOM -> shuffled()
    }

@Suppress( "UNCHECKED_CAST" )
class PlaylistsViewModelFactory(
    private val musicServiceConnection: MusicServiceConnection,
    private val settingsRepository: SettingsRepository,
    private val playlistRepository: PlaylistRepository,
    private val songsAdditionalMetadataRepository: SongsAdditionalMetadataRepository,
) : ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel> create(modelClass: Class<T>) =
        ( PlaylistsViewModel(
            musicServiceConnection = musicServiceConnection,
            settingsRepository = settingsRepository,
            playlistRepository = playlistRepository,
            songsAdditionalMetadataRepository = songsAdditionalMetadataRepository
        ) as T )
}