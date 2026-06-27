package com.squad.musicmatters.feature.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.squad.musicmatters.core.datastore.PreferencesDataSource
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
    private val userPreferencesDataSource: PreferencesDataSource
) : ViewModel() {

    val uiState: StateFlow<SettingsScreenUiState> =
        userPreferencesDataSource.userData.map {
            SettingsScreenUiState.Success( userData = it )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed( 5_000 ),
            initialValue = SettingsScreenUiState.Loading
        )

    fun setFont( font: MusicMattersFont ) {
        viewModelScope.launch { userPreferencesDataSource.setFontName( font.name ) }
    }

    fun setFontScale( fontScale: String ) {
        fontScale.toFloatOrNull()?.let {
            if ( FONT_SCALE_VALUES.contains( it ) ) {
                viewModelScope.launch { userPreferencesDataSource.setFontScale( it ) }
            }
        }
    }

    fun setThemeMode( themeMode: ThemeMode ) {
        viewModelScope.launch { userPreferencesDataSource.setThemeMode( themeMode ) }
    }

    fun setUseMaterialYou( use: Boolean ) {
        viewModelScope.launch { userPreferencesDataSource.setUseMaterialYou( use ) }
    }

    fun setPrimaryColorName( primaryColorName: String ) {
        viewModelScope.launch { userPreferencesDataSource.setPrimaryColorName( primaryColorName ) }
    }

    fun setBottomBarLabelVisibility( bottomBarLabelVisibility: BottomBarLabelVisibility ) {
        viewModelScope.launch {
            userPreferencesDataSource.setBottomBarLabelVisibility( bottomBarLabelVisibility )
        }
    }

    fun setFadePlayback( fadePlayback: Boolean ) {
        viewModelScope.launch { userPreferencesDataSource.setFadePlayback( fadePlayback ) }
    }

    fun setFadePlaybackDuration( fadePlaybackDuration: Float ) {
        viewModelScope.launch {
            userPreferencesDataSource.setFadePlaybackDuration( fadePlaybackDuration )
        }
    }

    fun setRequireAudioFocus( requireAudioFocus: Boolean ) {
        viewModelScope.launch {
            userPreferencesDataSource.setRequireAudioFocus( requireAudioFocus )
        }
    }

    fun setIgnoreAudioFocusLoss( ignoreAudioFocusLoss: Boolean ) {
        viewModelScope.launch {
            userPreferencesDataSource.setIgnoreAudioFocusLoss( ignoreAudioFocusLoss )
        }
    }

    fun setPlayOnHeadphonesConnect( playOnHeadphoneConnect: Boolean ) {
        viewModelScope.launch {
            userPreferencesDataSource.setPlayOnHeadphonesConnect( playOnHeadphoneConnect )
        }
    }

    fun setPauseOnHeadphonesDisconnect( pauseOnHeadphonesDisconnect: Boolean ) {
        viewModelScope.launch {
            userPreferencesDataSource.setPauseOnHeadphonesDisconnect( pauseOnHeadphonesDisconnect )
        }
    }

    fun setMiniPlayerTextMarquee( marquee: Boolean ) {
        viewModelScope.launch { userPreferencesDataSource.setMiniPlayerTextMarquee( marquee ) }
    }

    fun setShowLyricsOnSeparateScreen( showLyricsOnSeparatePage: Boolean ) {
        viewModelScope.launch {
            userPreferencesDataSource.setShowLyricsOnSeparateScreen( showLyricsOnSeparatePage )
        }
    }

}

internal sealed interface SettingsScreenUiState {
    data object Loading: SettingsScreenUiState
    data class Success( val userData: UserData ): SettingsScreenUiState
}