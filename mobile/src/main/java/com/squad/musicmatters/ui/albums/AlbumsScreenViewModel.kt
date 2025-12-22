package com.squad.musicmatters.ui.albums
//
//import androidx.lifecycle.ViewModel
//import androidx.lifecycle.ViewModelProvider
//import androidx.lifecycle.viewModelScope
//import com.squad.musicmatters.core.media.connection.MusicServiceConnection
//import com.squad.musicmatters.core.data.preferences.SortAlbumsBy
//import com.squad.musicmatters.core.data.preferences.impl.SettingsDefaults
//import com.squad.musicmatters.core.data.repository.PlaylistRepository
//import com.squad.musicmatters.core.data.repository.SongsAdditionalMetadataRepository
//import com.squad.musicmatters.core.data.settings.SettingsRepository
//import com.squad.musicmatters.core.i8n.English
//import com.squad.musicmatters.core.i8n.Language
//import com.squad.musicmatters.core.model.Album
//import com.squad.musicmatters.core.model.PlaylistInfo
//import com.squad.musicmatters.core.model.ThemeMode
//import com.squad.musicmatters.ui.BaseViewModel
//import kotlinx.coroutines.flow.MutableStateFlow
//import kotlinx.coroutines.flow.asStateFlow
//import kotlinx.coroutines.launch
//
//class AlbumsScreenViewModel(
//    private val musicServiceConnection: MusicServiceConnection,
//    private val settingsRepository: SettingsRepository,
//    playlistRepository: PlaylistRepository,
//    songsAdditionalMetadataRepository: SongsAdditionalMetadataRepository
//) : BaseViewModel(
//    musicServiceConnection = musicServiceConnection,
//    settingsRepository = settingsRepository,
//    playlistRepository = playlistRepository,
//    songsAdditionalMetadataRepository = songsAdditionalMetadataRepository,
//) {
//
//    private val _uiState = MutableStateFlow(
//        AlbumsScreenUiState(
//            albums = emptyList(),
//            sortAlbumsBy = settingsRepository.sortAlbumsBy.value,
//            sortAlbumsInReverse = settingsRepository.sortAlbumsInReverse.value,
//            isLoadingAlbums = true,
//            language = settingsRepository.language.value,
//            themeMode = settingsRepository.themeMode.value,
//            playlistInfos = emptyList()
//        )
//    )
//    val uiState = _uiState.asStateFlow()
//
//    init {
//        viewModelScope.launch { observeMusicServiceConnectionInitializedStatus() }
//        viewModelScope.launch { observeAlbums() }
//        viewModelScope.launch { observeLanguageChange() }
//        viewModelScope.launch { observeThemeModeChange() }
//        viewModelScope.launch { observeSortAlbumsBy() }
//        viewModelScope.launch { observeSortAlbumsInReverse() }
//        addOnPlaylistsChangeListener {
//            _uiState.value = _uiState.value.copy(
//                playlistInfos = it
//            )
//        }
//    }
//
//    private suspend fun observeMusicServiceConnectionInitializedStatus() {
//        musicServiceConnection.isInitializing.collect {
//            _uiState.value = _uiState.value.copy(
//                isLoadingAlbums = it
//            )
//        }
//    }
//
//    private suspend fun observeAlbums() {
//        musicServiceConnection.cachedAlbums.collect {
//            _uiState.value = _uiState.value.copy(
//                albums = sortAlbumsIn( it )
//            )
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
//    private suspend fun observeSortAlbumsBy() {
//        settingsRepository.sortAlbumsBy.collect {
//            _uiState.value = _uiState.value.copy(
//                sortAlbumsBy = it,
//                albums = sortAlbumsIn( musicServiceConnection.cachedAlbums.value )
//            )
//        }
//    }
//
//    private suspend fun observeSortAlbumsInReverse() {
//        settingsRepository.sortAlbumsInReverse.collect {
//            _uiState.value = _uiState.value.copy(
//                sortAlbumsInReverse = it,
//                albums = sortAlbumsIn( musicServiceConnection.cachedAlbums.value )
//            )
//        }
//    }
//
//    private fun sortAlbumsIn( albumList: List<Album> ) =
//        albumList.sortAlbums(
//            sortAlbumsBy = settingsRepository.sortAlbumsBy.value,
//            reverse = settingsRepository.sortAlbumsInReverse.value
//        )
//
//    fun setSortAlbumsBy( sortAlbumsBy: SortAlbumsBy ) {
//        viewModelScope.launch { settingsRepository.setSortAlbumsBy( sortAlbumsBy ) }
//    }
//
//    fun setSortAlbumsInReverse( reverse: Boolean ) {
//        viewModelScope.launch { settingsRepository.setSortAlbumsInReverse( reverse ) }
//    }
//}
//
//private fun List<Album>.sortAlbums( sortAlbumsBy: SortAlbumsBy, reverse: Boolean ): List<Album> {
//    return when ( sortAlbumsBy ) {
//        SortAlbumsBy.ALBUM_NAME -> if ( reverse ) sortedByDescending { it.title } else sortedBy { it.title }
//        SortAlbumsBy.ARTIST_NAME -> if ( reverse ) sortedByDescending { it.artists.joinToString() } else sortedBy { it.artists.joinToString() }
//        SortAlbumsBy.TRACKS_COUNT -> if ( reverse ) sortedByDescending { it.trackCount } else sortedBy { it.trackCount }
//        SortAlbumsBy.CUSTOM -> shuffled()
//    }
//}
//
//data class AlbumsScreenUiState(
//    val albums: List<Album>,
//    val sortAlbumsBy: SortAlbumsBy,
//    val sortAlbumsInReverse: Boolean,
//    val isLoadingAlbums: Boolean,
//    val language: Language,
//    val themeMode: ThemeMode,
//    val playlistInfos: List<PlaylistInfo>
//)
//
//internal val testAlbumsScreenUiState =
//    AlbumsScreenUiState(
//        albums = List( 10 ) {
//            Album(
//                title = "Album-$it",
//                artists = setOf( "Travis Scott", "Doja Cat" ),
//                trackCount = 0,
//                artworkUri = null
//            )
//        },
//        sortAlbumsBy = SettingsDefaults.sortAlbumsBy,
//        sortAlbumsInReverse = SettingsDefaults.SORT_ALBUMS_IN_REVERSE,
//        isLoadingAlbums = false,
//        language = English,
//        themeMode = SettingsDefaults.themeMode,
//        playlistInfos = emptyList()
//    )
//
//@Suppress( "UNCHECKED_CAST" )
//class AlbumsViewModelFactory(
//    private val musicServiceConnection: MusicServiceConnection,
//    private val settingsRepository: SettingsRepository,
//    private val playlistRepository: PlaylistRepository,
//    private val songsAdditionalMetadataRepository: SongsAdditionalMetadataRepository
//) : ViewModelProvider.NewInstanceFactory() {
//    override fun <T: ViewModel> create( modelClass: Class<T> ) =
//        ( AlbumsScreenViewModel(
//            musicServiceConnection = musicServiceConnection,
//            settingsRepository = settingsRepository,
//            playlistRepository = playlistRepository,
//            songsAdditionalMetadataRepository = songsAdditionalMetadataRepository
//        ) as T )
//}