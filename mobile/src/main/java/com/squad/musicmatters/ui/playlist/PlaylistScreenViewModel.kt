package com.squad.musicmatters.ui.playlist
//
//import androidx.lifecycle.ViewModel
//import androidx.lifecycle.ViewModelProvider
//import androidx.lifecycle.viewModelScope
//import com.squad.musicmatters.core.media.connection.MusicServiceConnection
//import com.squad.musicmatters.core.datapreferences.SortSongsBy
//import com.squad.musicmatters.core.data.preferences.impl.SettingsDefaults
//import com.squad.musicmatters.core.data.repository.PlaylistRepository
//import com.squad.musicmatters.core.data.repository.SongsAdditionalMetadataRepository
//import com.squad.musicmatters.core.data.settings.SettingsRepository
//import com.squad.musicmatters.core.testing.playlists.testPlaylistInfos
//import com.squad.musicmatters.core.testing.songs.testSongs
//import com.squad.musicmatters.core.i8n.English
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
//class PlaylistScreenViewModel(
//    private val playlistId: String,
//    private val musicServiceConnection: MusicServiceConnection,
//    private val playlistRepository: PlaylistRepository,
//    private val settingsRepository: SettingsRepository,
//    songsAdditionalMetadataRepository: SongsAdditionalMetadataRepository,
//) : BaseViewModel(
//    musicServiceConnection = musicServiceConnection,
//    settingsRepository = settingsRepository,
//    playlistRepository = playlistRepository,
//    songsAdditionalMetadataRepository = songsAdditionalMetadataRepository
//) {
//
//    private var currentPlaylistId: String? = null
//
//    private val _uiState = MutableStateFlow(
//        PlaylistScreenUiState(
//            playlistInfo = EMPTY_PLAYLISTInfo,
//            songsInPlaylist = emptyList(),
//            sortSongsBy = settingsRepository.sortSongsBy.value,
//            sortSongsInReverse = settingsRepository.sortSongsInReverse.value,
//            isLoadingSongsInPlaylist = musicServiceConnection.isInitializing.value,
//            language = settingsRepository.language.value,
//            themeMode = settingsRepository.themeMode.value,
//            currentlyPlayingSongId = musicServiceConnection.nowPlayingMediaItem.value.mediaId,
//            favoriteSongIds = emptyList(),
////                playlistRepository.favoritesPlaylistInfo.value.songIds,
//            playlistInfos = emptyList(),
//            songsAdditionalMetadataList = emptyList()
//        )
//    )
//    val uiState = _uiState.asStateFlow()
//
//    init {
//        viewModelScope.launch { observeMusicServiceConnectionInitializedStatus() }
//        viewModelScope.launch { observeCurrentlyPlayingSong() }
//        viewModelScope.launch { observeFavoriteSongIds() }
//        addOnPlaylistsChangeListener {
//            _uiState.value = _uiState.value.copy( playlistInfos = it )
//            // The songs in the current playlist may have changed so fetch them again..
//            fetchSongsInPlaylistWithId( playlistId )
//        }
//        addOnSortSongsByChangeListener { sortSongsBy, reverse ->
//            _uiState.value = _uiState.value.copy(
//                sortSongsBy = sortSongsBy,
//                sortSongsInReverse = reverse,
//            )
//            fetchSongsInPlaylistWithId( playlistId )
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
//                isLoadingSongsInPlaylist = isInitializing
//            )
//            if ( !isInitializing ) fetchSongsInPlaylistWithId( playlistId )
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
//    private fun fetchSongsInPlaylistWithId( playlistId: String ) {
////        currentPlaylistId = playlistId
////        playlistRepository.playlists.value.find { it.id == playlistId }?.let { playlist ->
////            _uiState.value = _uiState.value.copy(
////                playlistInfo = playlist,
////                songsInPlaylist = musicServiceConnection.cachedSongs.value
////                    .filter { playlist.songIds.contains( it.id ) }
////                    .sortSongs(
////                        sortSongsBy = settingsRepository.sortSongsBy.value,
////                        reverse = settingsRepository.sortSongsInReverse.value
////                    )
////            )
////        }
//    }
//
//}
//
//data class PlaylistScreenUiState(
//    val playlistInfo: PlaylistInfo,
//    val songsInPlaylist: List<Song>,
//    val sortSongsBy: SortSongsBy,
//    val sortSongsInReverse: Boolean,
//    val isLoadingSongsInPlaylist: Boolean,
//    val language: Language,
//    val themeMode: ThemeMode,
//    val currentlyPlayingSongId: String,
//    val favoriteSongIds: List<String>,
//    val playlistInfos: List<PlaylistInfo>,
//    val songsAdditionalMetadataList: List<SongAdditionalMetadataInfo>
//)
//
//private val EMPTY_PLAYLISTInfo = PlaylistInfo(
//    id = "",
//    title = "",
//    songIds = emptyList()
//)
//
//internal val testPlaylistScreenUiState = PlaylistScreenUiState(
//    playlistInfo = testPlaylistInfos.first(),
//    songsInPlaylist = testSongs,
//    sortSongsBy = SettingsDefaults.sortSongsBy,
//    sortSongsInReverse = false,
//    isLoadingSongsInPlaylist = false,
//    language = English,
//    currentlyPlayingSongId = testSongs.first().id,
//    favoriteSongIds = emptyList(),
//    themeMode = SettingsDefaults.themeMode,
//    playlistInfos = emptyList(),
//    songsAdditionalMetadataList = emptyList()
//)
//
//@Suppress( "UNCHECKED_CAST" )
//class PlaylistScreenViewModelFactory(
//    private val playlistId: String,
//    private val musicServiceConnection: MusicServiceConnection,
//    private val settingsRepository: SettingsRepository,
//    private val playlistsRepository: PlaylistRepository,
//    private val songsAdditionalMetadataRepository: SongsAdditionalMetadataRepository,
//) : ViewModelProvider.NewInstanceFactory() {
//    override fun <T : ViewModel> create( modelClass: Class<T> ) =
//        ( PlaylistScreenViewModel(
//            playlistId = playlistId,
//            musicServiceConnection = musicServiceConnection,
//            settingsRepository = settingsRepository,
//            playlistRepository = playlistsRepository,
//            songsAdditionalMetadataRepository = songsAdditionalMetadataRepository
//        ) as T )
//}