package com.squad.musicmatters.feature.genres

import androidx.lifecycle.ViewModel
import com.squad.musicmatters.core.data.repository.SongsMetadataRepository
import com.squad.musicmatters.core.datastore.PreferencesDataSource
import com.squad.musicmatters.core.model.Genre
import com.squad.musicmatters.core.model.SortGenresBy
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import javax.inject.Inject

@HiltViewModel
class GenresScreenViewModel @Inject constructor(
    private val songsMetadataRepository: SongsMetadataRepository,
    private val preferencesDataSource: PreferencesDataSource,
) : ViewModel() {

//    val uiState: StateFlow<GenresScreenUiState> =
//        combine(
//            preferencesDataSource.userData,
//            preferencesDataSource.userData.flatMapLatest {
//                songsMetadataRepository.fetchGenres(
//                    sortedBy = it.sortGenresBy,
//                    reverse = it.sortGenresReverse
//                )
//            }
//        ) { userData, genres ->}

}

sealed interface GenresScreenUiState {
    data object Loading : GenresScreenUiState
    data class Success(
        val genres: List<Genre>,
        val sortGenresBy: SortGenresBy,
        val sortGenresInReverse: Boolean,
    )
}