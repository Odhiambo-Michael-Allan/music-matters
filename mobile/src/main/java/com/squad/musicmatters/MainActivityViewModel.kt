package com.squad.musicmatters

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.squad.musicmatters.core.datastore.UserPreferencesRepository
import com.squad.musicmatters.core.media.connection.MusicMattersPlayer
import com.squad.musicmatters.core.model.Song
import com.squad.musicmatters.core.model.UserData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor(
    userPreferencesRepository: UserPreferencesRepository,
    private val player: MusicMattersPlayer,
) : ViewModel() {

    val uiState: StateFlow<MainActivityUiState> = userPreferencesRepository.userData.map {
        MainActivityUiState.Success(
            userData = it
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed( 5_000 ),
        initialValue = MainActivityUiState.Loading
    )

    fun deleteSong( song: Song ) = player.remove( song )

}

sealed interface MainActivityUiState {
    data object Loading : MainActivityUiState
    data class Success(
        val userData: UserData
    ): MainActivityUiState
}