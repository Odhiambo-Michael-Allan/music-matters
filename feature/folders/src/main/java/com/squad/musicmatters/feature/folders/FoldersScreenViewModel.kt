package com.squad.musicmatters.feature.folders

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.squad.musicmatters.core.data.repository.SongsRepository
import com.squad.musicmatters.core.datastore.UserDataRepository
import com.squad.musicmatters.core.model.Folder
import com.squad.musicmatters.core.model.Song
import com.squad.musicmatters.core.model.SortPathsBy
import com.squad.musicmatters.core.model.directoryName
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.nio.file.Path
import javax.inject.Inject
import kotlin.io.path.Path
import kotlin.io.path.name
import kotlin.io.path.pathString

@HiltViewModel
class FoldersScreenViewModel @Inject constructor(
    songsRepository: SongsRepository,
    private val userDataRepository: UserDataRepository,
) : ViewModel() {

    val uiState: StateFlow<FoldersScreenUiState> = combine(
        songsRepository.fetchSongs(),
        userDataRepository.userData
    ) { songs, userData ->
        FoldersScreenUiState.Success(
            folders = songs.fetchSortedFolders(
                sortBy = userData.sortPathsBy,
                reverse = userData.sortPathsReverse
            ),
            sortPathsBy = userData.sortPathsBy,
            sortPathsInReverse = userData.sortPathsReverse,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed( 5_000 ),
        initialValue = FoldersScreenUiState.Loading
    )

    fun setSortPaths( by: SortPathsBy ) {
        viewModelScope.launch {  userDataRepository.setSortPathsBy( by ) }
    }

    fun setSortPathsIn( reverse: Boolean ) {
        viewModelScope.launch { userDataRepository.setSortPathsInReverse( reverse ) }
    }

}

sealed interface FoldersScreenUiState {
    data object Loading : FoldersScreenUiState
    data class Success(
        val folders: List<Folder>,
        val sortPathsBy: SortPathsBy,
        val sortPathsInReverse: Boolean,
    ): FoldersScreenUiState
}

private fun List<Song>.fetchSortedFolders(
    sortBy: SortPathsBy,
    reverse: Boolean,
): List<Folder> {
    val tree = mutableMapOf<String, MutableList<Song>>()
    forEach { song ->
        val directoryName = Path( song.path ).directoryName()
        if ( !tree.containsKey( directoryName ) ) {
            tree[ directoryName ] = mutableListOf()
        }
        tree[ directoryName ]!!.add( song )
    }
    return tree.map { ( path, songs ) ->
        Folder(
            name = Path( path ).name,
            path = path,
            artworkUri = songs.firstOrNull { !it.artworkUri.isNullOrBlank() }?.artworkUri,
            trackCount = songs.size
        )
    }.sortFolders(
        sortBy = sortBy,
        reverse = reverse
    )
}

private fun List<Folder>.sortFolders( sortBy: SortPathsBy, reverse: Boolean ): List<Folder> {
    val sortedList = when ( sortBy ) {
        SortPathsBy.NAME -> sortedBy { it.name }
        SortPathsBy.TRACK_COUNT -> sortedBy { it.trackCount }
        SortPathsBy.CUSTOM -> shuffled()
    }
    return if ( reverse ) sortedList.reversed() else sortedList
}





