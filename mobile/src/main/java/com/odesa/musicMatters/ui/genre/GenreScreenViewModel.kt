package com.odesa.musicMatters.ui.genre

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.odesa.musicMatters.core.common.connection.MusicServiceConnection
import com.odesa.musicMatters.core.data.preferences.SortSongsBy
import com.odesa.musicMatters.core.data.preferences.impl.SettingsDefaults
import com.odesa.musicMatters.core.data.repository.PlaylistRepository
import com.odesa.musicMatters.core.data.repository.SongsAdditionalMetadataRepository
import com.odesa.musicMatters.core.data.settings.SettingsRepository
import com.odesa.musicMatters.core.data.utils.sortSongs
import com.odesa.musicMatters.core.datatesting.songs.testSongs
import com.odesa.musicMatters.core.designsystem.theme.ThemeMode
import com.odesa.musicMatters.core.i8n.Language
import com.odesa.musicMatters.core.model.PlaylistInfo
import com.odesa.musicMatters.core.model.Song
import com.odesa.musicMatters.core.model.SongAdditionalMetadataInfo
import com.odesa.musicMatters.ui.BaseViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class GenreScreenViewModel(
    private val genreName: String,
    private val musicServiceConnection: MusicServiceConnection,
    private val settingsRepository: SettingsRepository,
    private val playlistRepository: PlaylistRepository,
    private val songsAdditionalMetadataRepository: SongsAdditionalMetadataRepository
) : BaseViewModel(
    musicServiceConnection = musicServiceConnection,
    settingsRepository = settingsRepository,
    playlistRepository = playlistRepository,
    songsAdditionalMetadataRepository = songsAdditionalMetadataRepository
) {

    private val _uiState = MutableStateFlow(
        GenreScreenUiState(
            language = settingsRepository.language.value,
            themeMode = settingsRepository.themeMode.value,
            songsInGenre = emptyList(),
            sortSongsBy = settingsRepository.sortSongsBy.value,
            sortSongsInReverse = settingsRepository.sortSongsInReverse.value,
            currentlyPlayingSongId = musicServiceConnection.nowPlayingMediaItem.value.mediaId,
            favoriteSongIds = emptyList(),
            isLoading = musicServiceConnection.isInitializing.value,
            playlistInfos = emptyList(),
            songsAdditionalMetadataList = emptyList()
        )
    )
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch { observeMusicServiceConnectionInitializedStatus() }
        viewModelScope.launch { observeLanguageChange() }
        viewModelScope.launch { observeThemeModeChange() }
        viewModelScope.launch { observeCurrentlyPlayingSong() }
        viewModelScope.launch { observeFavoriteSongIds() }
        addOnPlaylistsChangeListener {
            _uiState.value = _uiState.value.copy( playlistInfos = it )
        }
        addOnSortSongsByChangeListener { sortSongsBy, sortSongsInReverse ->
            _uiState.value = _uiState.value.copy(
                sortSongsBy = sortSongsBy,
                sortSongsInReverse = sortSongsInReverse,
                songsInGenre = loadSongsInGenre( genreName )
            )
        }
        addOnSongsMetadataListChangeListener {
            _uiState.value = _uiState.value.copy(
                songsAdditionalMetadataList = it
            )
        }
    }

    private suspend fun observeMusicServiceConnectionInitializedStatus() {
        musicServiceConnection.isInitializing.collect { isInitializing ->
            _uiState.value = _uiState.value.copy(
                isLoading = isInitializing
            )
            if ( !isInitializing ) {
                _uiState.value = _uiState.value.copy(
                    songsInGenre = loadSongsInGenre( genreName )
                )
            }
        }
    }

    private fun loadSongsInGenre( genreName: String ) =
        musicServiceConnection
            .cachedGenres
            .value
            .find { it.name == genreName }
            ?.songsInGenre
            ?.sortSongs(
                sortSongsBy = settingsRepository.sortSongsBy.value,
                reverse = settingsRepository.sortSongsInReverse.value
            ) ?: emptyList()


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

    private suspend fun observeCurrentlyPlayingSong() {
        musicServiceConnection.nowPlayingMediaItem.collect {
            _uiState.value = _uiState.value.copy(
                currentlyPlayingSongId = it.mediaId
            )
        }
    }

    private suspend fun observeFavoriteSongIds() {
        playlistRepository.favoritesPlaylistInfo.collect {
            _uiState.value = _uiState.value.copy(
                favoriteSongIds = it.songIds
            )
        }
    }

}

@Suppress( "UNCHECKED_CAST" )
class GenreScreenViewModelFactory(
    private val genreName: String,
    private val musicServiceConnection: MusicServiceConnection,
    private val settingsRepository: SettingsRepository,
    private val playlistRepository: PlaylistRepository,
    private val songsAdditionalMetadataRepository: SongsAdditionalMetadataRepository,
) : ViewModelProvider.NewInstanceFactory() {
    override fun <T: ViewModel> create( modelClass: Class<T> ) =
        ( GenreScreenViewModel(
            genreName = genreName,
            musicServiceConnection = musicServiceConnection,
            settingsRepository = settingsRepository,
            playlistRepository = playlistRepository,
            songsAdditionalMetadataRepository = songsAdditionalMetadataRepository,
        ) as T )
}

data class GenreScreenUiState(
    val language: Language,
    val themeMode: ThemeMode,
    val songsInGenre: List<Song>,
    val sortSongsBy: SortSongsBy,
    val sortSongsInReverse: Boolean,
    val currentlyPlayingSongId: String,
    val favoriteSongIds: List<String>,
    val isLoading: Boolean,
    val playlistInfos: List<PlaylistInfo>,
    val songsAdditionalMetadataList: List<SongAdditionalMetadataInfo>
)

internal val testGenreScreenUiState = GenreScreenUiState(
    language = SettingsDefaults.language,
    themeMode = SettingsDefaults.themeMode,
    sortSongsBy = SettingsDefaults.sortSongsBy,
    sortSongsInReverse = SettingsDefaults.SORT_SONGS_IN_REVERSE,
    songsInGenre = testSongs,
    currentlyPlayingSongId = testSongs.first().id,
    favoriteSongIds = emptyList(),
    isLoading = false,
    playlistInfos = emptyList(),
    songsAdditionalMetadataList = emptyList()
)