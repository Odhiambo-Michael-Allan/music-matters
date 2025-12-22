package com.squad.musicmatters.ui.queue
//
//import androidx.lifecycle.ViewModel
//import androidx.lifecycle.ViewModelProvider
//import androidx.lifecycle.viewModelScope
//import com.squad.musicmatters.core.media.connection.MusicServiceConnection
//import com.squad.musicmatters.core.media.media.extensions.artistTagSeparators
//import com.squad.musicmatters.core.media.media.extensions.toSong
//import com.squad.musicmatters.core.data.preferences.impl.SettingsDefaults
//import com.squad.musicmatters.core.data.repository.PlaylistRepository
//import com.squad.musicmatters.core.data.repository.SongsAdditionalMetadataRepository
//import com.squad.musicmatters.core.data.settings.SettingsRepository
//import com.squad.musicmatters.core.testing.songs.testSongs
//import com.squad.musicmatters.core.i8n.Language
//import com.squad.musicmatters.core.model.PlaylistInfo
//import com.squad.musicmatters.core.model.Song
//import com.squad.musicmatters.core.model.SongAdditionalMetadataInfo
//import com.squad.musicmatters.core.model.ThemeMode
//import com.squad.musicmatters.ui.BaseViewModel
//import kotlinx.coroutines.flow.MutableStateFlow
//import kotlinx.coroutines.flow.asStateFlow
//import kotlinx.coroutines.launch
//
//class QueueScreenViewModel(
//    private val musicServiceConnection: MusicServiceConnection,
//    private val settingsRepository: SettingsRepository,
//    private val playlistRepository: PlaylistRepository,
//    songsAdditionalMetadataRepository: SongsAdditionalMetadataRepository,
//) : BaseViewModel(
//    musicServiceConnection = musicServiceConnection,
//    settingsRepository = settingsRepository,
//    playlistRepository = playlistRepository,
//    songsAdditionalMetadataRepository = songsAdditionalMetadataRepository,
//) {
//
//    private val _uiState = MutableStateFlow(
//        QueueScreenUiState(
//            songsInQueue = emptyList(),
//            language = settingsRepository.language.value,
//            currentlyPlayingSongId = musicServiceConnection.nowPlayingMediaItem.value.mediaId,
//            currentlyPlayingSongIndex = musicServiceConnection.currentlyPlayingMediaItemIndex.value,
//            themeMode = settingsRepository.themeMode.value,
//            favoriteSongIds = emptyList(),
//            isLoading = true,
//            playlistInfos = emptyList(),
//            songsAdditionalMetadataList = emptyList(),
//        )
//    )
//    val uiState = _uiState.asStateFlow()
//
//    init {
//        viewModelScope.launch { observeMediaItemsInQueue() }
//        viewModelScope.launch { observeCurrentlyPlayingSong() }
//        viewModelScope.launch { observeCurrentlyPlayingSongIndex() }
//        viewModelScope.launch { observeThemeMode() }
//        viewModelScope.launch { observeFavoriteSongIds() }
//        addOnPlaylistsChangeListener { _uiState.value = _uiState.value.copy( playlistInfos = it ) }
//        addOnSongsMetadataListChangeListener {
//            _uiState.value = _uiState.value.copy( songsAdditionalMetadataList = it )
//        }
//    }
//
//    private suspend fun observeMediaItemsInQueue() {
//        musicServiceConnection.mediaItemsInQueue.collect { mediaItems ->
//            _uiState.value = _uiState.value.copy(
//                songsInQueue = mediaItems.map { it.toSong( artistTagSeparators ) },
//                isLoading = false
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
//    private suspend fun observeCurrentlyPlayingSongIndex() {
//        musicServiceConnection.currentlyPlayingMediaItemIndex.collect {
//            _uiState.value = _uiState.value.copy(
//                currentlyPlayingSongIndex = it
//            )
//        }
//    }
//
//    private suspend fun observeThemeMode() {
//        settingsRepository.themeMode.collect {
//            _uiState.value = _uiState.value.copy(
//                themeMode = it
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
//    fun moveSong( from: Int, to: Int ) {
//        musicServiceConnection.moveMediaItem( from, to )
//    }
//
//    fun clearQueue() {
//        musicServiceConnection.clearQueue()
//    }
//
//}
//
//data class QueueScreenUiState(
//    val songsInQueue: List<Song>,
//    val currentlyPlayingSongId: String,
//    val currentlyPlayingSongIndex: Int,
//    val language: Language,
//    val themeMode: ThemeMode,
//    val favoriteSongIds: List<String>,
//    val isLoading: Boolean,
//    val playlistInfos: List<PlaylistInfo>,
//    val songsAdditionalMetadataList: List<SongAdditionalMetadataInfo>
//)
//
//internal val testQueueScreenUiState = QueueScreenUiState(
//    songsInQueue = testSongs,
//    language = SettingsDefaults.language,
//    currentlyPlayingSongId = testSongs.first().id,
//    currentlyPlayingSongIndex = 0,
//    themeMode = SettingsDefaults.themeMode,
//    favoriteSongIds = emptyList(),
//    isLoading = false,
//    playlistInfos = emptyList(),
//    songsAdditionalMetadataList = emptyList()
//)
//
//@Suppress( "UNCHECKED_CAST" )
//class QueueScreenViewModelFactory(
//    private val settingsRepository: SettingsRepository,
//    private val playlistRepository: PlaylistRepository,
//    private val musicServiceConnection: MusicServiceConnection,
//    private val songsAdditionalMetadataRepository: SongsAdditionalMetadataRepository,
//) : ViewModelProvider.NewInstanceFactory() {
//    override fun <T : ViewModel> create( modelClass: Class<T> ) =
//        ( QueueScreenViewModel(
//            musicServiceConnection = musicServiceConnection,
//            settingsRepository = settingsRepository,
//            playlistRepository = playlistRepository,
//            songsAdditionalMetadataRepository = songsAdditionalMetadataRepository,
//        ) as T )
//}