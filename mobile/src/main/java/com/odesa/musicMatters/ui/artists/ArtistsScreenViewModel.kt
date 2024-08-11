package com.odesa.musicMatters.ui.artists

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.odesa.musicMatters.core.common.connection.MusicServiceConnection
import com.odesa.musicMatters.core.data.preferences.SortArtistsBy
import com.odesa.musicMatters.core.data.preferences.impl.SettingsDefaults
import com.odesa.musicMatters.core.data.repository.PlaylistRepository
import com.odesa.musicMatters.core.data.repository.SongsAdditionalMetadataRepository
import com.odesa.musicMatters.core.data.settings.SettingsRepository
import com.odesa.musicMatters.core.datatesting.artists.testArtists
import com.odesa.musicMatters.core.designsystem.theme.ThemeMode
import com.odesa.musicMatters.core.i8n.English
import com.odesa.musicMatters.core.i8n.Language
import com.odesa.musicMatters.core.model.Artist
import com.odesa.musicMatters.core.model.PlaylistInfo
import com.odesa.musicMatters.ui.BaseViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ArtistsScreenViewModel(
    private val musicServiceConnection: MusicServiceConnection,
    private val settingsRepository: SettingsRepository,
    playlistRepository: PlaylistRepository,
    songsAdditionalMetadataRepository: SongsAdditionalMetadataRepository,
) : BaseViewModel(
    musicServiceConnection = musicServiceConnection,
    settingsRepository = settingsRepository,
    playlistRepository = playlistRepository,
    songsAdditionalMetadataRepository = songsAdditionalMetadataRepository
) {

    private val _uiState = MutableStateFlow(
        ArtistsScreenUiState(
            artists = emptyList(),
            isLoadingArtists = true,
            language = settingsRepository.language.value,
            themeMode = settingsRepository.themeMode.value,
            sortArtistsBy = settingsRepository.sortArtistsBy.value,
            sortArtistsInReverse = settingsRepository.sortArtistsInReverse.value,
            playlistInfos = emptyList()
        )
    )
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch { observeMusicServiceConnectionInitializedStatus() }
        viewModelScope.launch { observeArtists() }
        viewModelScope.launch { observeLanguageChange() }
        viewModelScope.launch { observeThemeModeChange() }
        viewModelScope.launch { observeSortArtistsBy() }
        viewModelScope.launch { observeSortArtistsInReverse() }
        addOnPlaylistsChangeListener {
            _uiState.value = _uiState.value.copy( playlistInfos = it )
        }
    }

    private suspend fun observeMusicServiceConnectionInitializedStatus() {
        musicServiceConnection.isInitializing.collect {
            _uiState.value = _uiState.value.copy(
                isLoadingArtists = it
            )
        }
    }

    private suspend fun observeArtists() {
        musicServiceConnection.cachedArtists.collect {
            _uiState.value = _uiState.value.copy(
                artists = sortArtists(
                    it,
                    settingsRepository.sortArtistsBy.value,
                    settingsRepository.sortArtistsInReverse.value
                )
            )
        }
    }

    private suspend fun observeLanguageChange() {
        settingsRepository.language.collect {
            _uiState.value = _uiState.value.copy(
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

    private suspend fun observeSortArtistsBy() {
        settingsRepository.sortArtistsBy.collect {
            _uiState.value = _uiState.value.copy(
                sortArtistsBy = it,
                artists = sortArtists(
                    artists = musicServiceConnection.cachedArtists.value,
                    sortArtistsBy = it,
                    reverse = settingsRepository.sortArtistsInReverse.value
                )
            )
        }
    }

    private suspend fun observeSortArtistsInReverse() {
        settingsRepository.sortArtistsInReverse.collect {
            _uiState.value = _uiState.value.copy(
                sortArtistsInReverse = it,
                artists = sortArtists(
                    artists = musicServiceConnection.cachedArtists.value,
                    sortArtistsBy = settingsRepository.sortArtistsBy.value,
                    reverse = it
                )
            )
        }
    }

    fun setSortArtistsBy( sortType: SortArtistsBy) {
        viewModelScope.launch {
            settingsRepository.setSortArtistsBy( sortType )
        }
    }

    fun setSortArtistsInReverse( sortReverse: Boolean ) {
        viewModelScope.launch {
            settingsRepository.setSortArtistsInReverseTo( sortReverse )
        }
    }

    private fun sortArtists( artists: List<Artist>, sortArtistsBy: SortArtistsBy, reverse: Boolean ): List<Artist> {
        return when ( sortArtistsBy ) {
            SortArtistsBy.ARTIST_NAME -> if ( reverse ) artists.sortedByDescending { it.name } else artists.sortedBy { it.name }
            SortArtistsBy.ALBUMS_COUNT -> if ( reverse ) artists.sortedByDescending { it.albumCount } else artists.sortedBy { it.albumCount }
            SortArtistsBy.TRACKS_COUNT -> if ( reverse ) artists.sortedByDescending { it.trackCount } else artists.sortedBy { it.trackCount }
            else -> artists.shuffled()
        }
    }
}

data class ArtistsScreenUiState(
    val artists: List<Artist>,
    val isLoadingArtists: Boolean,
    val language: Language,
    val themeMode: ThemeMode,
    val sortArtistsBy: SortArtistsBy,
    val sortArtistsInReverse: Boolean,
    val playlistInfos: List<PlaylistInfo>
)

internal val testArtistsScreenUiState = ArtistsScreenUiState(
    artists = testArtists,
    isLoadingArtists = false,
    language = English,
    themeMode = SettingsDefaults.themeMode,
    sortArtistsBy = SettingsDefaults.sortArtistsBy,
    sortArtistsInReverse = false,
    playlistInfos = emptyList()
)


@Suppress( "UNCHECKED_CAST" )
class ArtistsViewModelFactory(
    private val musicServiceConnection: MusicServiceConnection,
    private val settingsRepository: SettingsRepository,
    private val playlistRepository: PlaylistRepository,
    private val songsAdditionalMetadataRepository: SongsAdditionalMetadataRepository,
) : ViewModelProvider.NewInstanceFactory() {
    override fun <T: ViewModel> create( modelClass: Class<T> ) =
        ( ArtistsScreenViewModel(
            musicServiceConnection = musicServiceConnection,
            settingsRepository = settingsRepository,
            playlistRepository = playlistRepository,
            songsAdditionalMetadataRepository = songsAdditionalMetadataRepository
        ) as T )
}