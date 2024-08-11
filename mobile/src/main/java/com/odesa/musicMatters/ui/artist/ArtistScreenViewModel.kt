package com.odesa.musicMatters.ui.artist

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
import com.odesa.musicMatters.core.datatesting.albums.testAlbums
import com.odesa.musicMatters.core.datatesting.artists.testArtists
import com.odesa.musicMatters.core.datatesting.songs.testSongs
import com.odesa.musicMatters.core.designsystem.theme.ThemeMode
import com.odesa.musicMatters.core.i8n.English
import com.odesa.musicMatters.core.i8n.Language
import com.odesa.musicMatters.core.model.Album
import com.odesa.musicMatters.core.model.Artist
import com.odesa.musicMatters.core.model.PlaylistInfo
import com.odesa.musicMatters.core.model.Song
import com.odesa.musicMatters.core.model.SongAdditionalMetadataInfo
import com.odesa.musicMatters.ui.BaseViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ArtistScreenViewModel(
    private val artistName: String,
    private val musicServiceConnection: MusicServiceConnection,
    private val settingsRepository: SettingsRepository,
    private val playlistRepository: PlaylistRepository,
    songsAdditionalMetadataRepository: SongsAdditionalMetadataRepository
) : BaseViewModel(
    musicServiceConnection = musicServiceConnection,
    settingsRepository = settingsRepository,
    playlistRepository = playlistRepository,
    songsAdditionalMetadataRepository = songsAdditionalMetadataRepository
) {

    private val _uiState = MutableStateFlow(
        ArtistScreenUiState(
            artist = null,
            songsByArtist = emptyList(),
            isLoadingSongsByArtist = true,
            albumsByArtist = emptyList(),
            language = settingsRepository.language.value,
            themeMode = settingsRepository.themeMode.value,
            currentlyPlayingSongId = musicServiceConnection.nowPlayingMediaItem.value.mediaId,
            favoriteSongIds = playlistRepository.favoritesPlaylistInfo.value.songIds,
            sortSongsByArtistBy = settingsRepository.sortSongsBy.value,
            sortSongsByArtistInReverse = settingsRepository.sortSongsInReverse.value,
            playlistInfos = emptyList(),
            songsAdditionalMetadataList = emptyList()
        )
    )
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch { observeMusicServiceConnectionInitializedStatus() }
        viewModelScope.launch { observeLanguageChange() }
        viewModelScope.launch { observeThemeModeChange() }
        viewModelScope.launch { observeCurrentlyPlayingSongId() }
        viewModelScope.launch { observeFavoritesPlaylist() }
        addOnPlaylistsChangeListener {
            _uiState.value = _uiState.value.copy(
                playlistInfos = it
            )
        }
        addOnSortSongsByChangeListener { sortSongsBy, sortSongsInReverse ->
            _uiState.value = _uiState.value.copy(
                sortSongsByArtistBy = sortSongsBy,
                sortSongsByArtistInReverse = sortSongsInReverse
            )
            loadSongsBy( artistName )
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
                isLoadingSongsByArtist = isInitializing
            )
            if ( !isInitializing ) loadSongsBy( artistName )
        }
    }

    private fun loadSongsBy( artistName: String ) {
        val artist = musicServiceConnection.cachedArtists.value.find { it.name == artistName }
        val songsByArtist = musicServiceConnection.cachedSongs.value.filter { it.artists.contains( artistName ) }
        val albumsByArtist = musicServiceConnection.cachedAlbums.value.filter { it.artists.contains( artistName ) }
        _uiState.value = _uiState.value.copy(
            artist = artist,
            songsByArtist = songsByArtist.sortSongs( 
                settingsRepository.sortSongsBy.value,
                settingsRepository.sortSongsInReverse.value
            ),
            albumsByArtist = albumsByArtist,
        )
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

    private suspend fun observeCurrentlyPlayingSongId() {
        musicServiceConnection.nowPlayingMediaItem.collect {
            _uiState.value = _uiState.value.copy(
                currentlyPlayingSongId = it.mediaId
            )
        }
    }

    private suspend fun observeFavoritesPlaylist() {
        playlistRepository.favoritesPlaylistInfo.collect {
            _uiState.value = _uiState.value.copy(
                favoriteSongIds = it.songIds
            )
        }
    }

}

@Suppress( "UNCHECKED_CAST" )
class ArtistScreenViewModelFactory(
    private val artistName: String,
    private val musicServiceConnection: MusicServiceConnection,
    private val settingsRepository: SettingsRepository,
    private val playlistRepository: PlaylistRepository,
    private val songsAdditionalMetadataRepository: SongsAdditionalMetadataRepository
) : ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel> create( modelClass: Class<T> ) =
        ( ArtistScreenViewModel(
            artistName = artistName,
            musicServiceConnection = musicServiceConnection,
            settingsRepository = settingsRepository,
            playlistRepository = playlistRepository,
            songsAdditionalMetadataRepository = songsAdditionalMetadataRepository
        ) as T )
}

data class ArtistScreenUiState(
    val artist: Artist?,
    val songsByArtist: List<Song>,
    val isLoadingSongsByArtist: Boolean,
    val albumsByArtist: List<Album>,
    val language: Language,
    val themeMode: ThemeMode,
    val currentlyPlayingSongId: String,
    val favoriteSongIds: List<String>,
    val sortSongsByArtistBy: SortSongsBy,
    val sortSongsByArtistInReverse: Boolean,
    val playlistInfos: List<PlaylistInfo>,
    val songsAdditionalMetadataList: List<SongAdditionalMetadataInfo>
)

internal val testArtistScreenUiState = ArtistScreenUiState(
    artist = testArtists.first(),
    songsByArtist = testSongs,
    isLoadingSongsByArtist = false,
    albumsByArtist = testAlbums,
    language = English,
    themeMode = SettingsDefaults.themeMode,
    currentlyPlayingSongId = testSongs.first().id,
    favoriteSongIds = emptyList(),
    sortSongsByArtistBy = SettingsDefaults.sortSongsBy,
    sortSongsByArtistInReverse = false,
    playlistInfos = emptyList(),
    songsAdditionalMetadataList = emptyList()
)
