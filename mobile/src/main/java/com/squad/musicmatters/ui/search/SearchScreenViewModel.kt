package com.squad.musicmatters.ui.search
//
//import androidx.core.net.toUri
//import androidx.lifecycle.ViewModel
//import androidx.lifecycle.ViewModelProvider
//import androidx.lifecycle.viewModelScope
//import com.squad.musicmatters.core.media.connection.MusicServiceConnection
//import com.squad.musicmatters.core.data.repository.PlaylistRepository
//import com.squad.musicmatters.core.data.repository.SongsAdditionalMetadataRepository
//import com.squad.musicmatters.core.data.search.SearchHistoryRepository
//import com.squad.musicmatters.core.data.settings.SettingsRepository
//import com.squad.musicmatters.core.i8n.Language
//import com.squad.musicmatters.core.model.Album
//import com.squad.musicmatters.core.model.Artist
//import com.squad.musicmatters.core.model.Genre
//import com.squad.musicmatters.core.model.PlaylistInfo
//import com.squad.musicmatters.core.model.SearchFilter
//import com.squad.musicmatters.core.model.SearchHistoryItem
//import com.squad.musicmatters.core.model.Song
//import com.squad.musicmatters.core.model.ThemeMode
//import com.squad.musicmatters.ui.BaseViewModel
//import com.squad.musicmatters.utils.FuzzySearchOption
//import com.squad.musicmatters.utils.FuzzySearcher
//import kotlinx.coroutines.Dispatchers
//import kotlinx.coroutines.flow.MutableStateFlow
//import kotlinx.coroutines.flow.asStateFlow
//import kotlinx.coroutines.launch
//import kotlinx.coroutines.withContext
//
//class SearchScreenViewModel (
//    val musicServiceConnection: MusicServiceConnection,
//    val playlistRepository: PlaylistRepository,
//    val settingsRepository: SettingsRepository,
//    val searchHistoryRepository: SearchHistoryRepository,
//    songsAdditionalMetadataRepository: SongsAdditionalMetadataRepository,
//) : BaseViewModel(
//    musicServiceConnection = musicServiceConnection,
//    playlistRepository = playlistRepository,
//    settingsRepository = settingsRepository,
//    songsAdditionalMetadataRepository = songsAdditionalMetadataRepository
//) {
//
//    private val albumsFuzzySearcher = FuzzySearcher<String>(
//        options = listOf(
//            FuzzySearchOption( { v -> compareString( getAlbumWithName( v )?.title ?: "" ) }, 3 ),
//            FuzzySearchOption( { v -> compareCollection( getAlbumWithName( v )?.artists ?: listOf() ) } )
//        )
//    )
//    private val artistFuzzySearcher = FuzzySearcher<String>(
//        options = listOf(
//            FuzzySearchOption( { v -> compareString( getArtistWithName( v )?.name ?: "" ) } )
//        )
//    )
//    private val genreFuzzySearcher = FuzzySearcher<String>(
//        options = listOf(
//            FuzzySearchOption( { v -> compareString( getGenreWithName( v )?.name ?: "" ) } )
//        )
//    )
//    private val playlistFuzzySearcher = FuzzySearcher<String>(
//        options = listOf(
//            FuzzySearchOption( { v -> compareString( getPlaylistWithId( v )?.title ?: "" ) } )
//        )
//    )
//
//    private val _uiState = MutableStateFlow(
//        SearchScreenUiState(
//            isLoadingSearchHistory = musicServiceConnection.isInitializing.value,
//            isSearching = false,
//            searchHistoryItems = searchHistoryRepository.searchHistory.value,
//            language = settingsRepository.language.value,
//            currentSearchResults = emptySearchResults,
//            themeMode = settingsRepository.themeMode.value,
//            currentlyPlayingSongId = musicServiceConnection.nowPlayingMediaItem.value.mediaId,
//        )
//    )
//    val uiState = _uiState.asStateFlow()
//
//    init {
//        viewModelScope.launch { observeMusicServiceConnectionIsInitializingStatus() }
//        viewModelScope.launch { observeSearchHistory() }
//        viewModelScope.launch { observeLanguageChange() }
//        viewModelScope.launch { observeThemeModeChange() }
//        viewModelScope.launch { observeCurrentlyPlayingSongId() }
//    }
//
//    private suspend fun observeMusicServiceConnectionIsInitializingStatus() {
//        musicServiceConnection.isInitializing.collect {
//            _uiState.value = _uiState.value.copy(
//                isLoadingSearchHistory = it
//            )
//        }
//    }
//
//    private suspend fun observeSearchHistory() {
//        searchHistoryRepository.searchHistory.collect {
//            _uiState.value = _uiState.value.copy(
//                searchHistoryItems = it
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
//    private suspend fun observeCurrentlyPlayingSongId() {
//        musicServiceConnection.nowPlayingMediaItem.collect {
//            _uiState.value = _uiState.value.copy(
//                currentlyPlayingSongId = it.mediaId
//            )
//        }
//    }
//
//    fun saveSearchHistoryItem( searchHistoryItem: SearchHistoryItem ) {
//        viewModelScope.launch { searchHistoryRepository.saveSearchHistoryItem( searchHistoryItem ) }
//    }
//
//    fun deleteSearchHistoryItem( searchHistoryItem: SearchHistoryItem ) {
//        viewModelScope.launch { searchHistoryRepository.deleteSearchHistoryItem( searchHistoryItem ) }
//    }
//
//    fun getAlbumWithName( name: String ) = musicServiceConnection.cachedAlbums.value.find { it.title == name }
//    fun getArtistWithName( name: String ) = musicServiceConnection.cachedArtists.value.find { it.name == name }
//    fun getGenreWithName( name: String ) = musicServiceConnection.cachedGenres.value.find { it.name == name }
//    fun getPlaylistWithId( id: String ) = PlaylistInfo( id = "", title = "", songIds = emptyList() )
////        playlistRepository.playlists.value.find { it.id == id }
//
//    fun search( searchQuery: String, searchFilter: SearchFilter? ) {
//        viewModelScope.launch {
//            withContext( Dispatchers.Default ) {
//                _uiState.value = _uiState.value.copy(
//                    isSearching = true
//                )
//                val songs = mutableListOf<Song>()
//                val albums = mutableListOf<Album>()
//                val artists = mutableListOf<Artist>()
//                val genres = mutableListOf<Genre>()
//                val playlistInfos = mutableListOf<PlaylistInfo>()
//                if ( searchQuery.isNotEmpty() ) {
//                    searchFilter?.let {
//                        when ( it ) {
//                            SearchFilter.SONG -> {
//                                songs.addAll(
//                                    searchSongsMatching( searchQuery )
//                                )
//                            }
//                            SearchFilter.ALBUM -> {
//                                albums.addAll(
//                                    albumsFuzzySearcher.search(
//                                        terms = searchQuery,
//                                        entities = musicServiceConnection.cachedAlbums.value.map { album -> album.title }
//                                    ).mapNotNull { fuzzySearchResult -> getAlbumWithName( fuzzySearchResult.entity ) }
//                                )
//                            }
//                            SearchFilter.ARTIST -> {
//                                artists.addAll(
//                                    artistFuzzySearcher.search(
//                                        terms = searchQuery,
//                                        entities = musicServiceConnection.cachedArtists.value.map { artist -> artist.name }
//                                    ).mapNotNull { fuzzySearchResult -> getArtistWithName( fuzzySearchResult.entity ) }
//                                )
//                            }
//                            SearchFilter.GENRE -> {
//                                genres.addAll(
//                                    genreFuzzySearcher.search(
//                                        terms = searchQuery,
//                                        entities = musicServiceConnection.cachedGenres.value.map { genre -> genre.name }
//                                    ).mapNotNull { fuzzySearchResult -> getGenreWithName( fuzzySearchResult.entity ) }
//                                )
//                            }
//                            SearchFilter.PLAYLIST -> {
//                                playlistInfos.addAll(
//                                    playlistFuzzySearcher.search(
//                                        terms = searchQuery,
//                                        entities = emptyList()
////                                            playlistRepository.playlists.value.map { playlist -> playlist.id }
//                                    ).mapNotNull { fuzzySearchResult -> getPlaylistWithId( fuzzySearchResult.entity ) }
//                                )
//                            }
//                        }
//                    } ?: run {
//                        songs.addAll(
//                            searchSongsMatching( searchQuery )
//                        )
//                        albums.addAll(
//                            albumsFuzzySearcher.search(
//                                terms = searchQuery,
//                                entities = musicServiceConnection.cachedAlbums.value.map { it.title }
//                            ).mapNotNull { getAlbumWithName( it.entity ) }
//                        )
//                        artists.addAll(
//                            artistFuzzySearcher.search(
//                                terms = searchQuery,
//                                entities = musicServiceConnection.cachedArtists.value.map { it.name }
//                            ).mapNotNull { getArtistWithName( it.entity ) }
//                        )
//                        genres.addAll(
//                            genreFuzzySearcher.search(
//                                terms = searchQuery,
//                                entities = musicServiceConnection.cachedGenres.value.map { it.name }
//                            ).mapNotNull { getGenreWithName( it.entity ) }
//                        )
//                        playlistInfos.addAll(
//                            playlistFuzzySearcher.search(
//                                terms = searchQuery,
//                                entities = emptyList()
////                                    playlistRepository.playlists.value.map { it.id }
//                            ).mapNotNull { getPlaylistWithId( it.entity ) }
//                        )
//                    }
//                }
//                val searchResults = SearchResults(
//                    matchingSongs = songs,
//                    matchingAlbums = albums,
//                    matchingArtists = artists,
//                    matchingGenres = genres,
//                    matchingPlaylistInfos = playlistInfos
//                )
//                _uiState.value = _uiState.value.copy(
//                    currentSearchResults = searchResults,
//                    isSearching = false
//                )
//            }
//        }
//    }
//
//    fun getSongWithId( id: String ) = musicServiceConnection.cachedSongs.value.find { it.id == id }
//
//    fun getPlaylistArtworkUri(playlistInfo: PlaylistInfo ) = musicServiceConnection.cachedSongs.value
//        .filter { playlistInfo.songIds.contains( it.id ) }.firstOrNull { it.artworkUri != null }
//        ?.artworkUri
//        ?.toUri()
//
//    fun addSongToSearchHistory( song: Song ) {
//        viewModelScope.launch {
//            searchHistoryRepository.saveSearchHistoryItem(
//                SearchHistoryItem(
//                    id = song.id,
//                    category = SearchFilter.SONG
//                )
//            )
//        }
//    }
//
//    fun addAlbumToSearchHistory( album: Album ) {
//        viewModelScope.launch {
//            searchHistoryRepository.saveSearchHistoryItem(
//                SearchHistoryItem(
//                    id = album.title,
//                    category = SearchFilter.ALBUM
//                )
//            )
//        }
//    }
//
//    fun addArtistToSearchHistory( artist: Artist ) {
//         viewModelScope.launch {
//             searchHistoryRepository.saveSearchHistoryItem(
//                 SearchHistoryItem(
//                     id = artist.name,
//                     category = SearchFilter.ARTIST
//                 )
//             )
//         }
//     }
//
//    fun addGenreToSearchHistory( genre: Genre ) {
//        viewModelScope.launch {
//            searchHistoryRepository.saveSearchHistoryItem(
//                SearchHistoryItem(
//                    id = genre.name,
//                    category = SearchFilter.GENRE
//                )
//            )
//        }
//    }
//
//    fun addPlaylistToSearchHistory(playlistInfo: PlaylistInfo ) {
//        viewModelScope.launch {
//            searchHistoryRepository.saveSearchHistoryItem(
//                SearchHistoryItem(
//                    id = playlistInfo.id,
//                    category = SearchFilter.PLAYLIST
//                )
//            )
//        }
//    }
//
//    fun clearSearchHistory() {
//        viewModelScope.launch {
//            uiState.value.searchHistoryItems.forEach {
//                searchHistoryRepository.deleteSearchHistoryItem( it )
//            }
//        }
//    }
//
//}
//
//@Suppress( "UNCHECKED_CAST" )
//class SearchScreenViewModelFactory(
//    private val musicServiceConnection: MusicServiceConnection,
//    private val settingsRepository: SettingsRepository,
//    private val playlistRepository: PlaylistRepository,
//    private val searchHistoryRepository: SearchHistoryRepository,
//    private val songsAdditionalMetadataRepository: SongsAdditionalMetadataRepository,
//) : ViewModelProvider.NewInstanceFactory() {
//    override fun <T : ViewModel> create( modelClass: Class<T> ) =
//        ( SearchScreenViewModel(
//            musicServiceConnection = musicServiceConnection,
//            settingsRepository = settingsRepository,
//            playlistRepository = playlistRepository,
//            searchHistoryRepository = searchHistoryRepository,
//            songsAdditionalMetadataRepository = songsAdditionalMetadataRepository
//        ) as T )
//}
//
//data class SearchScreenUiState(
//    val isLoadingSearchHistory: Boolean,
//    val isSearching: Boolean,
//    val searchHistoryItems: List<SearchHistoryItem>,
//    val language: Language,
//    val currentSearchResults: SearchResults,
//    val themeMode: ThemeMode,
//    val currentlyPlayingSongId: String,
//)
//
//data class SearchResults(
//    val matchingSongs: List<Song>,
//    val matchingArtists: List<Artist>,
//    val matchingAlbums: List<Album>,
//    val matchingGenres: List<Genre>,
//    val matchingPlaylistInfos: List<PlaylistInfo>,
//)
//
//val emptySearchResults = SearchResults(
//    matchingSongs = emptyList(),
//    matchingAlbums = emptyList(),
//    matchingArtists = emptyList(),
//    matchingGenres = emptyList(),
//    matchingPlaylistInfos = emptyList()
//)