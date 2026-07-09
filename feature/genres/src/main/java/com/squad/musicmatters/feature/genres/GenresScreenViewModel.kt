package com.squad.musicmatters.feature.genres

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.squad.musicmatters.core.data.repository.SongsMetadataRepository
import com.squad.musicmatters.core.datastore.PreferencesDataSource
import com.squad.musicmatters.core.model.Genre
import com.squad.musicmatters.core.model.SortGenresBy
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GenresScreenViewModel @Inject constructor(
    private val songsMetadataRepository: SongsMetadataRepository,
    private val preferencesDataSource: PreferencesDataSource,
) : ViewModel() {

    val uiState: StateFlow<GenresScreenUiState> =
        combine(
            preferencesDataSource.userData,
            preferencesDataSource.userData.flatMapLatest {
                songsMetadataRepository.fetchGenres(
                    sortGenresBy = it.sortGenresBy,
                    reverse = it.sortGenresReverse
                )
            }
        ) { userData, genres ->
            GenresScreenUiState.Success(
                genres = genres,
                sortGenresBy = userData.sortGenresBy,
                sortGenresInReverse = userData.sortGenresReverse
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed( 5_000 ),
            initialValue = GenresScreenUiState.Loading
        )

    fun onSortTypeChange( by: SortGenresBy ) {
        viewModelScope.launch { preferencesDataSource.setSortGenresBy( by ) }
    }

    fun onSortInReverseChange( reverse: Boolean ) {
        viewModelScope.launch { preferencesDataSource.setSortGenresInReverse( reverse ) }
    }

}

sealed interface GenresScreenUiState {
    data object Loading : GenresScreenUiState
    data class Success(
        val genres: List<Genre>,
        val sortGenresBy: SortGenresBy,
        val sortGenresInReverse: Boolean,
    ): GenresScreenUiState
}