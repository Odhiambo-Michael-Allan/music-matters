package com.squad.musicmatters.feature.folders

import androidx.lifecycle.ViewModel
import com.squad.musicmatters.core.data.repository.SongsRepository
import com.squad.musicmatters.core.datastore.UserPreferencesRepository
import com.squad.musicmatters.core.model.Song
import com.squad.musicmatters.core.model.SortPathsBy
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import java.nio.file.Path
import javax.inject.Inject
import kotlin.io.path.Path
import kotlin.io.path.pathString

@HiltViewModel
class FoldersScreenViewModel @Inject constructor(
    songsRepository: SongsRepository,
    userPreferencesRepository: UserPreferencesRepository,
) : ViewModel() {

    val uiState: StateFlow<FoldersScreenUiState> = combine(
        songsRepository.fetchSongs(),
        userPreferencesRepository.userData
    ) { songs, userData ->

    }

}

internal sealed interface FoldersScreenUiState {
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
        SortPathsBy.NAME -> sortedBy { it.path }
        SortPathsBy.TRACK_COUNT -> sortedBy { it.trackCount }
        SortPathsBy.CUSTOM -> shuffled()
    }
    return if ( reverse ) sortedList.reversed() else sortedList
}

private fun Path.directoryName(): String {
    val indexOfSeparator = pathString.lastIndexOf( "/" )
    return pathString.substring( 0, indexOfSeparator )
}

internal data class Folder(
    val path: String,
    val artworkUri: String? = null,
    val trackCount: Int,
)

