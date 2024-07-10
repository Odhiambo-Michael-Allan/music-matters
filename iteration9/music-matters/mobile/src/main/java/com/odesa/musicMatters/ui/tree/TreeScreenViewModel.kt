package com.odesa.musicMatters.ui.tree

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.odesa.musicMatters.core.common.connection.MusicServiceConnection
import com.odesa.musicMatters.core.data.preferences.SortPathsBy
import com.odesa.musicMatters.core.data.preferences.SortSongsBy
import com.odesa.musicMatters.core.data.preferences.impl.SettingsDefaults
import com.odesa.musicMatters.core.data.repository.PlaylistRepository
import com.odesa.musicMatters.core.data.repository.SongsAdditionalMetadataRepository
import com.odesa.musicMatters.core.data.settings.SettingsRepository
import com.odesa.musicMatters.core.data.utils.sortSongs
import com.odesa.musicMatters.core.datatesting.songs.testSongs
import com.odesa.musicMatters.core.datatesting.tree.testTreeMap
import com.odesa.musicMatters.core.designsystem.theme.ThemeMode
import com.odesa.musicMatters.core.i8n.English
import com.odesa.musicMatters.core.i8n.Language
import com.odesa.musicMatters.core.model.PlaylistInfo
import com.odesa.musicMatters.core.model.Song
import com.odesa.musicMatters.core.model.SongAdditionalMetadataInfo
import com.odesa.musicMatters.ui.BaseViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.nio.file.Path
import kotlin.io.path.Path
import kotlin.io.path.pathString

class TreeScreenViewModel(
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
        TreeScreenUiState(
            tree = emptyMap(),
            sortPathsBy = settingsRepository.sortPathsBy.value,
            sortPathsInReverse = settingsRepository.sortPathsInReverse.value,
            sortSongsBy = settingsRepository.sortSongsBy.value,
            sortSongsInReverse = settingsRepository.sortSongsInReverse.value,
            songsCount = 0,
            isConstructingTree = musicServiceConnection.isInitializing.value,
            currentlyPlayingSongId = musicServiceConnection.nowPlayingMediaItem.value.mediaId,
            language = settingsRepository.language.value,
            themeMode = settingsRepository.themeMode.value,
            favoriteSongIds = playlistRepository.favoritesPlaylistInfo.value.songIds,
            disabledTreePaths = emptyList(),
            playlistInfos = emptyList(),
            songsAdditionalMetadataList = emptyList()
        )
    )
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch { observeMusicServiceConnectionInitializedStatus() }
        viewModelScope.launch { observeSongs() }
        viewModelScope.launch { observeCurrentlyPlayingSongId() }
        viewModelScope.launch { observeLanguageChange() }
        viewModelScope.launch { observeThemeModeChange() }
        viewModelScope.launch { observeFavoriteSongsPlaylist() }
        viewModelScope.launch { observeDisabledDirectoryNames() }
        viewModelScope.launch { observeSortPathsBy() }
        viewModelScope.launch { observeSortPathsInReverse() }
        addOnPlaylistsChangeListener {
            _uiState.value = _uiState.value.copy( playlistInfos = it )
        }
        addOnSortSongsByChangeListener { sortSongsBy, sortSongsInReverse ->
            _uiState.value = _uiState.value.copy(
                sortSongsBy = sortSongsBy,
                sortSongsInReverse = sortSongsInReverse,
                tree = constructTreeUsing( musicServiceConnection.cachedSongs.value )
            )
        }
        addOnSongsMetadataListChangeListener {
            _uiState.value = _uiState.value.copy(
                songsAdditionalMetadataList = it
            )
        }
    }

    private suspend fun observeMusicServiceConnectionInitializedStatus() {
        musicServiceConnection.isInitializing.collect {
            _uiState.value = _uiState.value.copy(
                isConstructingTree = it
            )
        }
    }

    private suspend fun observeSongs() {
        musicServiceConnection.cachedSongs.collect { songs ->
            val tree = constructTreeUsing( songs )
            _uiState.value = _uiState.value.copy(
                tree = tree,
                songsCount = tree.map { it.value.size }.sum()
            )
        }
    }

    private fun constructTreeUsing( songs: List<Song> ): Map<String, List<Song>> {
        val unsortedTree = constructUnsortedTreeUsing( songs )
        return constructSortedTreeUsing( unsortedTree )
    }

    private fun constructUnsortedTreeUsing( songs: List<Song> ): Map<String, List<Song>> {
        val tree = mutableMapOf<String, MutableList<Song>>()
        songs.forEach {
            val directoryName = Path( it.path ).directoryName()
            if ( !tree.containsKey( directoryName ) )
                tree[ directoryName ] = mutableListOf()
            tree[ directoryName ]!!.add( it )
        }
        return tree
    }

    private fun constructSortedTreeUsing( unsortedTree: Map<String, List<Song>> ): Map<String, List<Song>> {
        val treeWithSortedKeys = sortKeysIn( unsortedTree )
        return sortTreeValuesIn( treeWithSortedKeys )
    }

    private fun sortKeysIn( unsortedTree: Map<String, List<Song>> ) = unsortedTree
        .sortPaths(
            sortPathsBy = settingsRepository.sortPathsBy.value,
            reverse = settingsRepository.sortPathsInReverse.value
        )

    private fun sortTreeValuesIn( tree: Map<String, List<Song>> ): Map<String, List<Song>> {
        val sortedTree = mutableMapOf<String, List<Song>>()
        tree.keys.forEach { key ->
            sortedTree[ key ] = tree[ key ]!!.sortSongs(
                sortSongsBy = settingsRepository.sortSongsBy.value,
                reverse = settingsRepository.sortSongsInReverse.value
            )
        }
        return sortedTree
    }

    private suspend fun observeCurrentlyPlayingSongId() {
        musicServiceConnection.nowPlayingMediaItem.collect {
            _uiState.value = _uiState.value.copy(
                currentlyPlayingSongId = it.mediaId
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

    private suspend fun observeFavoriteSongsPlaylist() {
        playlistRepository.favoritesPlaylistInfo.collect {
            _uiState.value = _uiState.value.copy(
                favoriteSongIds = it.songIds
            )
        }
    }

    private suspend fun observeDisabledDirectoryNames() {
        settingsRepository.currentlyDisabledTreePaths.collect {
            _uiState.value = _uiState.value.copy(
                disabledTreePaths = it
            )
        }
    }

    private suspend fun observeSortPathsInReverse() {
        settingsRepository.sortPathsInReverse.collect {
            _uiState.value = _uiState.value.copy(
                sortPathsInReverse = it,
                tree = constructTreeUsing( musicServiceConnection.cachedSongs.value )
            )
        }
    }

    private suspend fun observeSortPathsBy() {
        settingsRepository.sortPathsBy.collect {
            _uiState.value = _uiState.value.copy(
                sortPathsBy = it,
                tree = constructTreeUsing( musicServiceConnection.cachedSongs.value )
            )
        }
    }

    fun togglePath( path: String ) {
        viewModelScope.launch {
            val currentlyDisabledPaths = uiState.value.disabledTreePaths.toMutableList()
            if ( currentlyDisabledPaths.contains( path ) )
                currentlyDisabledPaths.remove( path )
            else
                currentlyDisabledPaths.add( path )
            settingsRepository.setCurrentlyDisabledTreePaths( currentlyDisabledPaths )
            _uiState.value = _uiState.value.copy(
                disabledTreePaths = currentlyDisabledPaths
            )
        }
    }

    fun playSelectedSong(
        selectedSong: Song
    ) {
        val songs = mutableListOf<Song>()
        uiState.value.tree.values.forEach { list ->
            list.forEach { song -> songs.add( song ) }
        }
        playSongs(
            selectedSong = selectedSong,
            songsInPlaylist = songs
        )
    }

    fun setSortPathsBy( by: SortPathsBy ) {
        viewModelScope.launch { settingsRepository.setSortPathsBy( by ) }
    }

    fun setSortPathsInReverse( reverse: Boolean ) {
        viewModelScope.launch { settingsRepository.setSortPathsInReverse( reverse ) }
    }

}

data class TreeScreenUiState(
    val tree: Map<String, List<Song>>,
    val sortPathsBy: SortPathsBy,
    val sortPathsInReverse: Boolean,
    val sortSongsBy: SortSongsBy,
    val sortSongsInReverse: Boolean,
    val songsCount: Int,
    val isConstructingTree: Boolean,
    val currentlyPlayingSongId: String,
    val language: Language,
    val themeMode: ThemeMode,
    val favoriteSongIds: List<String>,
    val disabledTreePaths: List<String>,
    val playlistInfos: List<PlaylistInfo>,
    val songsAdditionalMetadataList: List<SongAdditionalMetadataInfo>
)

val testTreeScreenUiState = TreeScreenUiState(
    tree = testTreeMap,
    sortPathsBy = SettingsDefaults.sortPathsBy,
    sortPathsInReverse = true,
    sortSongsBy = SettingsDefaults.sortSongsBy,
    sortSongsInReverse = false,
    songsCount = 150,
    isConstructingTree = false,
    currentlyPlayingSongId = testSongs.first().id,
    language = English,
    favoriteSongIds = emptyList(),
    themeMode = SettingsDefaults.themeMode,
    disabledTreePaths = emptyList(),
    playlistInfos = emptyList(),
    songsAdditionalMetadataList = emptyList()
)

fun Path.directoryName(): String {
    val indexOfSeparator = this.pathString.lastIndexOf( "/" )
    return this.pathString.substring( 0, indexOfSeparator )
}

internal fun SortPathsBy.sortPathsByLabel( language: Language ) = when ( this ) {
    SortPathsBy.CUSTOM -> language.custom
    SortPathsBy.NAME -> language.name
    SortPathsBy.TRACK_COUNT -> language.trackCount
}

private fun Map<String, List<Song>>.sortPaths( sortPathsBy: SortPathsBy, reverse: Boolean ): Map<String, List<Song>> {
    val newMap = mutableMapOf<String, List<Song>>()
    when ( sortPathsBy ) {
        SortPathsBy.NAME -> {
            if ( reverse ) newMap.putAll( toSortedMap().toList().reversed() )
            else newMap.putAll( toSortedMap().toList() )
        }
        SortPathsBy.TRACK_COUNT -> {
            if ( reverse ) newMap.putAll( toList().sortedByDescending { it.second.size } )
            else newMap.putAll( toList().sortedBy { it.second.size } )
        }
        SortPathsBy.CUSTOM -> {
            newMap.putAll( toList().shuffled() )
        }
    }
    return newMap
}

@Suppress( "UNCHECKED_CAST" )
class TreeViewModelFactory(
    private val musicServiceConnection: MusicServiceConnection,
    private val settingsRepository: SettingsRepository,
    private val playlistRepository: PlaylistRepository,
    private val songsAdditionalMetadataRepository: SongsAdditionalMetadataRepository,
) : ViewModelProvider.NewInstanceFactory() {
    override fun <T: ViewModel> create( modelClass: Class<T> ) =
        ( TreeScreenViewModel(
            musicServiceConnection = musicServiceConnection,
            settingsRepository = settingsRepository,
            playlistRepository = playlistRepository,
            songsAdditionalMetadataRepository = songsAdditionalMetadataRepository
        ) as T )
}