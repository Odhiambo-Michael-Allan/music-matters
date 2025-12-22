package com.squad.musicmatters.ui.genres
//
//import androidx.lifecycle.ViewModel
//import androidx.lifecycle.ViewModelProvider
//import androidx.lifecycle.viewModelScope
//import com.squad.musicmatters.core.media.connection.MusicServiceConnection
//import com.squad.musicmatters.core.data.preferences.SortGenresBy
//import com.squad.musicmatters.core.data.preferences.impl.SettingsDefaults
//import com.squad.musicmatters.core.data.settings.SettingsRepository
//import com.squad.musicmatters.core.testing.genres.testGenres
//import com.squad.musicmatters.core.i8n.English
//import com.squad.musicmatters.core.i8n.Language
//import com.squad.musicmatters.core.model.Genre
//import kotlinx.coroutines.flow.MutableStateFlow
//import kotlinx.coroutines.flow.asStateFlow
//import kotlinx.coroutines.launch
//
//class GenresScreenViewModel(
//    private val musicServiceConnection: MusicServiceConnection,
//    private val settingsRepository: SettingsRepository,
//) : ViewModel() {
//
//    private val _uiState = MutableStateFlow(
//        GenreScreenUiState(
//            genres = emptyList(),
//            sortGenresBy = settingsRepository.sortGenresBy.value,
//            sortGenresInReverse = settingsRepository.sortSongsInReverse.value,
//            language = settingsRepository.language.value,
//            isLoading = true
//        )
//    )
//    val uiState = _uiState.asStateFlow()
//
//    init {
//        viewModelScope.launch { observeIsLoadingGenres() }
//        viewModelScope.launch { observeGenres() }
//        viewModelScope.launch { observeLanguageChange() }
//        viewModelScope.launch { observeSortGenresBy() }
//        viewModelScope.launch { observeSortGenresInReverse() }
//    }
//
//    private suspend fun observeIsLoadingGenres() {
//        musicServiceConnection.isLoadingGenres.collect {
//            _uiState.value = _uiState.value.copy(
//                isLoading = it
//            )
//        }
//    }
//
//    private suspend fun observeGenres() {
//        musicServiceConnection.cachedGenres.collect {
//            _uiState.value = _uiState.value.copy(
//                genres = it.sortGenres(
//                    sortGenresBy = settingsRepository.sortGenresBy.value,
//                    reverse = settingsRepository.sortGenresInReverse.value
//                )
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
//    private suspend fun observeSortGenresBy() {
//        settingsRepository.sortGenresBy.collect {
//            _uiState.value = _uiState.value.copy(
//                sortGenresBy = it,
//                genres = musicServiceConnection.cachedGenres.value.sortGenres(
//                    sortGenresBy = it,
//                    reverse = settingsRepository.sortGenresInReverse.value
//                )
//            )
//        }
//    }
//
//    private suspend fun observeSortGenresInReverse() {
//        settingsRepository.sortGenresInReverse.collect {
//            _uiState.value = _uiState.value.copy(
//                sortGenresInReverse = it,
//                genres = musicServiceConnection.cachedGenres.value.sortGenres(
//                    sortGenresBy = settingsRepository.sortGenresBy.value,
//                    reverse = it
//                )
//            )
//        }
//    }
//
//    fun setSortGenresBy( by: SortGenresBy ) {
//        viewModelScope.launch { settingsRepository.setSortGenresBy( by ) }
//    }
//
//    fun setSortGenresInReverse( reverse: Boolean ) {
//        viewModelScope.launch { settingsRepository.setSortGenresInReverse( reverse ) }
//    }
//}
//
//data class GenreScreenUiState(
//    val genres: List<Genre>,
//    val sortGenresBy: SortGenresBy,
//    val sortGenresInReverse: Boolean,
//    val language: Language,
//    val isLoading: Boolean,
//)
//
//private fun List<Genre>.sortGenres( sortGenresBy: SortGenresBy, reverse: Boolean ) =
//    when ( sortGenresBy ) {
//        SortGenresBy.NAME -> if ( reverse ) sortedByDescending { it.name } else sortedBy { it.name }
//        SortGenresBy.TRACKS_COUNT -> if ( reverse ) sortedByDescending { it.numberOfTracks } else sortedBy { it.numberOfTracks }
//        SortGenresBy.CUSTOM -> shuffled()
//    }
//
//@Suppress( "UNCHECKED_CAST" )
//class GenresScreenViewModelFactory(
//    private val musicServiceConnection: MusicServiceConnection,
//    private val settingsRepository: SettingsRepository,
//) : ViewModelProvider.NewInstanceFactory() {
//    override fun <T: ViewModel> create( modelClass: Class<T> ) =
//        ( GenresScreenViewModel(
//            musicServiceConnection = musicServiceConnection,
//            settingsRepository = settingsRepository,
//        ) as T )
//}
//
//internal val testGenreScreenUiState = GenreScreenUiState(
//    genres = testGenres,
//    sortGenresBy = SettingsDefaults.sortGenresBy,
//    sortGenresInReverse = SettingsDefaults.SORT_GENRES_IN_REVERSE,
//    language = English,
//    isLoading = false
//)