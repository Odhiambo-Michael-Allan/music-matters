package com.squad.musicmatters.ui.album
//
//import androidx.lifecycle.ViewModel
//import androidx.lifecycle.ViewModelProvider
//import androidx.lifecycle.viewModelScope
//import com.squad.musicmatters.core.media.connection.MusicServiceConnection
//import com.squad.musicmatters.core.data.repository.PlaylistRepository
//import com.squad.musicmatters.core.data.repository.SongsAdditionalMetadataRepository
//import com.squad.musicmatters.core.data.utils.sortSongs
//import com.squad.musicmatters.core.i8n.Language
//import com.squad.musicmatters.core.model.Album
//import com.squad.musicmatters.core.model.PlaylistInfo
//import com.squad.musicmatters.core.model.Song
//import com.squad.musicmatters.core.model.SongAdditionalMetadataInfo
//import com.squad.musicmatters.core.model.ThemeMode
//import kotlinx.coroutines.flow.MutableStateFlow
//import kotlinx.coroutines.flow.asStateFlow
//import kotlinx.coroutines.launch
//
//class AlbumScreenViewModel(
//    private val albumName: String,
//    private val musicServiceConnection: MusicServiceConnection,
//    private val settingsRepository: SettingsRepository,
//    private val playlistRepository: PlaylistRepository,
//    songsAdditionalMetadataRepository: SongsAdditionalMetadataRepository,
//) : BaseViewModel(
//    musicServiceConnection = musicServiceConnection,
//    settingsRepository = settingsRepository,
//    playlistRepository = playlistRepository,
//    songsAdditionalMetadataRepository = songsAdditionalMetadataRepository
//) {
//
//    private val _uiState = MutableStateFlow(
//        AlbumScreenUiState(
//            album = null,
//            themeMode = settingsRepository.themeMode.value,
//            isLoadingSongsInAlbum = true,
//            language = settingsRepository.language.value,
//            songsInAlbum = emptyList(),
//            sortSongsBy = settingsRepository.sortSongsBy.value,
//            sortSongsInReverse = settingsRepository.sortSongsInReverse.value,
//            currentlyPlayingSongId = musicServiceConnection.nowPlayingMediaItem.value.mediaId,
//            favoriteSongIds = emptyList(),
//            playlistInfos = emptyList(),
//            songsAdditionalMetadataList = emptyList()
//        )
//    )
//    val uiState = _uiState.asStateFlow()
//
//    init {
//        viewModelScope.launch { observeMusicServiceConnectionInitializedStatus() }
//        viewModelScope.launch { observeLanguageChange() }
//        viewModelScope.launch { observeThemeModeChange() }
//        viewModelScope.launch { observeCurrentlyPlayingSong() }
//        viewModelScope.launch { observeFavoriteSongIds() }
//        addOnPlaylistsChangeListener {
//            _uiState.value = _uiState.value.copy(
//                playlistInfos = it
//            )
//        }
//        addOnSortSongsByChangeListener { sortSongsBy, sortSongsInReverse ->
//            _uiState.value = _uiState.value.copy(
//                sortSongsBy = sortSongsBy,
//                sortSongsInReverse = sortSongsInReverse,
//            )
//            loadSongsInAlbum( albumName )
//        }
//        addOnSongsMetadataListChangeListener {
//            _uiState.value = _uiState.value.copy(
//                songsAdditionalMetadataList = it
//            )
//        }
//    }
//
//    private suspend fun observeMusicServiceConnectionInitializedStatus() {
//        musicServiceConnection.isInitializing.collect { isInitializing ->
//            _uiState.value = _uiState.value.copy(
//                isLoadingSongsInAlbum = isInitializing
//            )
//            if ( !isInitializing ) loadSongsInAlbum( albumName )
//        }
//    }
//
//    private suspend fun observeLanguageChange() {
//        settingsRepository.language.collect {
//            _uiState.value = _uiState.value.copy(
//                language = it
//            )
//        }
//    }
//
//    private suspend fun observeThemeModeChange() {
//        settingsRepository.themeMode.collect {
//            _uiState.value = _uiState.value.copy(
//                themeMode = it
//            )
//        }
//    }
//
//    private suspend fun observeCurrentlyPlayingSong() {
//        musicServiceConnection.nowPlayingMediaItem.collect {
//            _uiState.value = _uiState.value.copy(
//                currentlyPlayingSongId = it.mediaId
//            )
//        }
//    }
//
//    private suspend fun observeFavoriteSongIds() {
////        playlistRepository.favoritesPlaylistInfo.collect {
////            _uiState.value = _uiState.value.copy(
////                favoriteSongIds = it.songIds
////            )
////        }
//    }
//
//    private fun loadSongsInAlbum( albumName: String ) {
//        val songs = musicServiceConnection.cachedSongs.value
//        val albums = musicServiceConnection.cachedAlbums.value
//        _uiState.value = _uiState.value.copy(
//            album = albums.find { it.title == albumName },
//            songsInAlbum = songs.filter { it.albumTitle == albumName }.sortSongs(
//                sortSongsBy = settingsRepository.sortSongsBy.value,
//                reverse = settingsRepository.sortSongsInReverse.value
//            ),
//        )
//    }
//}
//
//@Suppress( "UNCHECKED_CAST" )
//class AlbumScreenViewModelFactory(
//    private val albumName: String,
//    private val musicServiceConnection: MusicServiceConnection,
//    private val settingsRepository: SettingsRepository,
//    private val playlistRepository: PlaylistRepository,
//    private val songsAdditionalMetadataRepository: SongsAdditionalMetadataRepository,
//) : ViewModelProvider.NewInstanceFactory() {
//    override fun <T : ViewModel> create( modelClass: Class<T> ) =
//        ( AlbumScreenViewModel(
//            albumName = albumName,
//            musicServiceConnection = musicServiceConnection,
//            settingsRepository = settingsRepository,
//            playlistRepository = playlistRepository,
//            songsAdditionalMetadataRepository = songsAdditionalMetadataRepository
//        ) as T )
//}
//
//data class AlbumScreenUiState(
//    val album: Album?,
//    val themeMode: ThemeMode,
//    val isLoadingSongsInAlbum: Boolean,
//    val language: Language,
//    val songsInAlbum: List<Song>,
//    val sortSongsBy: SortSongsBy,
//    val sortSongsInReverse: Boolean,
//    val currentlyPlayingSongId: String,
//    val favoriteSongIds: List<String>,
//    val playlistInfos: List<PlaylistInfo>,
//    val songsAdditionalMetadataList: List<SongAdditionalMetadataInfo>
//)
//
//internal val testAlbumScreenUiState = AlbumScreenUiState(
//    album = testAlbums.first(),
//    themeMode = SettingsDefaults.themeMode,
//    isLoadingSongsInAlbum = false,
//    language = SettingsDefaults.language,
//    songsInAlbum = testSongs,
//    sortSongsBy = SettingsDefaults.sortSongsBy,
//    sortSongsInReverse = false,
//    currentlyPlayingSongId = "",
//    favoriteSongIds = emptyList(),
//    playlistInfos = emptyList(),
//    songsAdditionalMetadataList = emptyList()
//)