package com.squad.musicmatters.feature.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.squad.musicmatters.core.datastore.UserPreferencesRepository
import com.squad.musicmatters.core.designsystem.theme.MusicMattersFont
import com.squad.musicmatters.core.model.BottomBarLabelVisibility
import com.squad.musicmatters.core.model.ThemeMode
import com.squad.musicmatters.core.model.UserData
import com.squad.musicmatters.feature.settings.appearance.FONT_SCALE_VALUES
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
internal class SettingsScreenViewModel @Inject constructor(
    private val userUserPreferencesRepository: UserPreferencesRepository
) : ViewModel() {

    val uiState: StateFlow<SettingsScreenUiState> =
        userUserPreferencesRepository.userData.map {
            SettingsScreenUiState.Success( userData = it )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed( 5_000 ),
            initialValue = SettingsScreenUiState.Loading
        )

    fun setFont( font: MusicMattersFont ) {
        viewModelScope.launch { userUserPreferencesRepository.setFontName( font.name ) }
    }

    fun setFontScale( fontScale: String ) {
        fontScale.toFloatOrNull()?.let {
            if ( FONT_SCALE_VALUES.contains( it ) ) {
                viewModelScope.launch { userUserPreferencesRepository.setFontScale( it ) }
            }
        }
    }

    fun setThemeMode( themeMode: ThemeMode ) {
        viewModelScope.launch { userUserPreferencesRepository.setThemeMode( themeMode ) }
    }

    fun setUseMaterialYou( use: Boolean ) {
        viewModelScope.launch { userUserPreferencesRepository.setUseMaterialYou( use ) }
    }

    fun setPrimaryColorName( primaryColorName: String ) {
        viewModelScope.launch { userUserPreferencesRepository.setPrimaryColorName( primaryColorName ) }
    }

    fun setBottomBarLabelVisibility( bottomBarLabelVisibility: BottomBarLabelVisibility ) {
        viewModelScope.launch {
            userUserPreferencesRepository.setBottomBarLabelVisibility( bottomBarLabelVisibility )
        }
    }

    fun setPlayOnHeadphonesConnect( playOnHeadphoneConnect: Boolean ) {
        viewModelScope.launch {
            userUserPreferencesRepository.setPlayOnHeadphonesConnect( playOnHeadphoneConnect )
        }
    }

    fun setPauseOnHeadphonesDisconnect( pauseOnHeadphonesDisconnect: Boolean ) {
        viewModelScope.launch {
            userUserPreferencesRepository.setPauseOnHeadphonesDisconnect( pauseOnHeadphonesDisconnect )
        }
    }

    fun setMiniPlayerTextMarquee( marquee: Boolean ) {
        viewModelScope.launch { userUserPreferencesRepository.setMiniPlayerTextMarquee( marquee ) }
    }

    fun setShowLyricsOnSeparateScreen( showLyricsOnSeparatePage: Boolean ) {
        viewModelScope.launch {
            userUserPreferencesRepository.setShowLyricsOnSeparateScreen( showLyricsOnSeparatePage )
            if ( showLyricsOnSeparatePage ) userUserPreferencesRepository.setShowLyrics( false )
        }
    }

}

internal sealed interface SettingsScreenUiState {
    data object Loading: SettingsScreenUiState
    data class Success( val userData: UserData ): SettingsScreenUiState
}