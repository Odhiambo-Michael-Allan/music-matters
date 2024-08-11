package com.odesa.musicMatters.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.odesa.musicMatters.core.data.preferences.HomePageBottomBarLabelVisibility
import com.odesa.musicMatters.core.data.preferences.NowPlayingLyricsLayout
import com.odesa.musicMatters.core.data.settings.SettingsRepository
import com.odesa.musicMatters.core.data.settings.impl.scalingPresets
import com.odesa.musicMatters.core.designsystem.theme.MusicallyFont
import com.odesa.musicMatters.core.designsystem.theme.ThemeMode
import com.odesa.musicMatters.core.i8n.Language
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        SettingsScreenUiState(
            language = settingsRepository.language.value,
            font = settingsRepository.font.value,
            fontScale = settingsRepository.fontScale.value,
            themeMode = settingsRepository.themeMode.value,
            useMaterialYou = settingsRepository.useMaterialYou.value,
            primaryColorName = settingsRepository.primaryColorName.value,
            homePageBottomBarLabelVisibility = settingsRepository
                .homePageBottomBarLabelVisibility.value,
            fadePlayback = settingsRepository.fadePlayback.value,
            fadePlaybackDuration = settingsRepository.fadePlaybackDuration.value,
            requireAudioFocus = settingsRepository.requireAudioFocus.value,
            ignoreAudioFocusLoss = settingsRepository.ignoreAudioFocusLoss.value,
            playOnHeadphonesConnect = settingsRepository.playOnHeadphonesConnect.value,
            pauseOnHeadphonesDisconnect = settingsRepository.pauseOnHeadphonesDisconnect.value,
            fastRewindDuration = settingsRepository.fastRewindDuration.value,
            fastForwardDuration = settingsRepository.fastForwardDuration.value,
            miniPlayerShowTrackControls = settingsRepository.miniPlayerShowTrackControls.value,
            miniPlayerShowSeekControls = settingsRepository.miniPlayerShowSeekControls.value,
            miniPlayerTextMarquee = settingsRepository.miniPlayerTextMarquee.value,
            controlsLayoutIsDefault = settingsRepository.controlsLayoutIsDefault.value,
            nowPlayingLyricsLayout = settingsRepository.nowPlayingLyricsLayout.value,
            showNowPlayingAudioInformation = settingsRepository.showNowPlayingAudioInformation.value,
            showNowPlayingSeekControls = settingsRepository.showNowPlayingSeekControls.value,

        )
    )
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch { observeLanguageSetting() }
        viewModelScope.launch { observeFontSetting() }
        viewModelScope.launch { observeFontScaleSetting() }
        viewModelScope.launch { observeThemeModeSetting() }
        viewModelScope.launch { observeUseMaterialYouSetting() }
        viewModelScope.launch { observePrimaryColorNameSetting() }
        viewModelScope.launch { observeHomePageBottomBarLabelVisibilitySetting() }
        viewModelScope.launch { observeFadePlaybackSetting() }
        viewModelScope.launch { observeFadePlaybackDurationSetting() }
        viewModelScope.launch { observeRequireAudioFocusSetting() }
        viewModelScope.launch { observeIgnoreAudioFocusLossSetting() }
        viewModelScope.launch { observePlayOnHeadphonesConnectSetting() }
        viewModelScope.launch { observePauseOnHeadphonesDisconnectSetting() }
        viewModelScope.launch { observeFastRewindDurationSetting() }
        viewModelScope.launch { observeFastForwardDurationSetting() }
        viewModelScope.launch { observeMiniPlayerShowTrackControlsSetting() }
        viewModelScope.launch { observeMiniPlayerShowSeekControlsSetting() }
        viewModelScope.launch { observeMiniPlayerTextMarqueeSetting() }
        viewModelScope.launch { observeNowPlayingControlsLayoutSetting() }
        viewModelScope.launch { observeNowPlayingLyricsLayoutSetting() }
        viewModelScope.launch { observeShowNowPlayingAudioInformationSetting() }
        viewModelScope.launch { observeShowNowPlayingSeekControlsSetting() }
    }

    private suspend fun observeLanguageSetting() {
        settingsRepository.language.collect {
            _uiState.value = _uiState.value.copy(
                language = it
            )
        }
    }

    private suspend fun observeFontSetting() {
        settingsRepository.font.collect {
            _uiState.value = _uiState.value.copy(
                font = it
            )
        }
    }

    private suspend fun observeFontScaleSetting() {
        settingsRepository.fontScale.collect {
            _uiState.value = _uiState.value.copy(
                fontScale = it
            )
        }
    }

    private suspend fun observeThemeModeSetting() {
        settingsRepository.themeMode.collect {
            _uiState.value = _uiState.value.copy(
                themeMode = it
            )
        }
    }

    private suspend fun observeUseMaterialYouSetting() {
        settingsRepository.useMaterialYou.collect {
            _uiState.value = _uiState.value.copy(
                useMaterialYou = it
            )
        }
    }

    private suspend fun observePrimaryColorNameSetting() {
        settingsRepository.primaryColorName.collect {
            _uiState.value = _uiState.value.copy(
                primaryColorName = it
            )
        }
    }

    private suspend fun observeHomePageBottomBarLabelVisibilitySetting() {
        settingsRepository.homePageBottomBarLabelVisibility.collect {
            _uiState.value = _uiState.value.copy(
                homePageBottomBarLabelVisibility = it
            )
        }
    }

    private suspend fun observeFadePlaybackSetting() {
        settingsRepository.fadePlayback.collect {
            _uiState.value = _uiState.value.copy(
                fadePlayback = it
            )
        }
    }

    private suspend fun observeFadePlaybackDurationSetting() {
        settingsRepository.fadePlaybackDuration.collect {
            _uiState.value = _uiState.value.copy(
                fadePlaybackDuration = it
            )
        }
    }

    private suspend fun observeRequireAudioFocusSetting() {
        settingsRepository.requireAudioFocus.collect {
            _uiState.value = _uiState.value.copy(
                requireAudioFocus = it
            )
        }
    }

    private suspend fun observeIgnoreAudioFocusLossSetting() {
        settingsRepository.ignoreAudioFocusLoss.collect {
            _uiState.value = _uiState.value.copy(
                ignoreAudioFocusLoss = it
            )
        }
    }

    private suspend fun observePlayOnHeadphonesConnectSetting() {
        settingsRepository.playOnHeadphonesConnect.collect {
            _uiState.value = _uiState.value.copy(
                playOnHeadphonesConnect = it
            )
        }
    }

    private suspend fun observePauseOnHeadphonesDisconnectSetting() {
        settingsRepository.pauseOnHeadphonesDisconnect.collect {
            _uiState.value = _uiState.value.copy(
                pauseOnHeadphonesDisconnect = it
            )
        }
    }

    private suspend fun observeFastRewindDurationSetting() {
        settingsRepository.fastRewindDuration.collect {
            _uiState.value = _uiState.value.copy(
                fastRewindDuration = it
            )
        }
    }

    private suspend fun observeFastForwardDurationSetting() {
        settingsRepository.fastForwardDuration.collect {
            _uiState.value = _uiState.value.copy(
                fastForwardDuration = it
            )
        }
    }

    private suspend fun observeMiniPlayerShowTrackControlsSetting() {
        settingsRepository.miniPlayerShowTrackControls.collect {
            _uiState.value = _uiState.value.copy(
                miniPlayerShowTrackControls = it
            )
        }
    }

    private suspend fun observeMiniPlayerShowSeekControlsSetting() {
        settingsRepository.miniPlayerShowSeekControls.collect {
            _uiState.value = _uiState.value.copy(
                miniPlayerShowSeekControls = it
            )
        }
    }

    private suspend fun observeMiniPlayerTextMarqueeSetting() {
        settingsRepository.miniPlayerTextMarquee.collect {
            _uiState.value = _uiState.value.copy(
                miniPlayerTextMarquee = it
            )
        }
    }

    private suspend fun observeNowPlayingControlsLayoutSetting() {
        settingsRepository.controlsLayoutIsDefault.collect {
            _uiState.value = _uiState.value.copy(
                controlsLayoutIsDefault = it
            )
        }
    }

    private suspend fun observeNowPlayingLyricsLayoutSetting() {
        settingsRepository.nowPlayingLyricsLayout.collect {
            _uiState.value = _uiState.value.copy(
                nowPlayingLyricsLayout = it
            )
        }
    }

    private suspend fun observeShowNowPlayingAudioInformationSetting() {
        settingsRepository.showNowPlayingAudioInformation.collect {
            _uiState.value = _uiState.value.copy(
                showNowPlayingAudioInformation = it
            )
        }
    }

    private suspend fun observeShowNowPlayingSeekControlsSetting() {
        settingsRepository.showNowPlayingSeekControls.collect {
            _uiState.value = _uiState.value.copy(
                showNowPlayingSeekControls = it
            )
        }
    }

    fun setLanguage( localeCode: String ) {
        viewModelScope.launch { settingsRepository.setLanguage( localeCode ) }
    }

    fun setFont( fontName: String ) {
        viewModelScope.launch { settingsRepository.setFont( fontName ) }
    }

    fun setFontScale( fontScale: String ) {
        viewModelScope.launch {
            val float = fontScale.toFloatOrNull()
            float?.let {
                if ( scalingPresets.contains( it ) ) settingsRepository.setFontScale( it )
            }
        }
    }

    fun setThemeMode( themeMode: ThemeMode) {
        viewModelScope.launch { settingsRepository.setThemeMode( themeMode ) }
    }

    fun setUseMaterialYou( useMaterialYou: Boolean ) {
        viewModelScope.launch { settingsRepository.setUseMaterialYou( useMaterialYou ) }
    }

    fun setPrimaryColorName( primaryColorName: String ) {
        viewModelScope.launch { settingsRepository.setPrimaryColorName( primaryColorName ) }
    }

    fun setHomePageBottomBarLabelVisibility( value: HomePageBottomBarLabelVisibility) {
        viewModelScope.launch { settingsRepository.setHomePageBottomBarLabelVisibility( value ) }
    }

    fun setFadePlayback( fadePlayback: Boolean ) {
        viewModelScope.launch { settingsRepository.setFadePlayback( fadePlayback ) }
    }

    fun setFadePlaybackDuration( fadePlaybackDuration: Float ) {
        viewModelScope.launch { settingsRepository.setFadePlaybackDuration( fadePlaybackDuration ) }
    }

    fun setRequireAudioFocus( requireAudioFocus: Boolean ) {
        viewModelScope.launch { settingsRepository.setRequireAudioFocus( requireAudioFocus ) }
    }

    fun setIgnoreAudioFocusLoss( ignoreAudioFocusLoss: Boolean ) {
        viewModelScope.launch { settingsRepository.setIgnoreAudioFocusLoss( ignoreAudioFocusLoss ) }
    }

    fun setPlayOnHeadphonesConnect( playOnHeadphonesConnect: Boolean ) {
        viewModelScope.launch {
            settingsRepository.setPlayOnHeadphonesConnect( playOnHeadphonesConnect )
        }
    }

    fun setPauseOnHeadphonesDisconnect( pauseOnHeadphonesDisconnect: Boolean ) {
        viewModelScope.launch {
            settingsRepository.setPauseOnHeadphonesDisconnect( pauseOnHeadphonesDisconnect )
        }
    }

    fun setFastRewindDuration( fastRewindDuration: Int ) {
        viewModelScope.launch { settingsRepository.setFastRewindDuration( fastRewindDuration ) }
    }

    fun setFastForwardDuration( fastForwardDuration: Int ) {
        viewModelScope.launch { settingsRepository.setFastForwardDuration( fastForwardDuration ) }
    }

    fun setMiniPlayerShowTrackControls( showTrackControls: Boolean ) {
        viewModelScope.launch {
            settingsRepository.setMiniPlayerShowTrackControls( showTrackControls )
        }
    }

    fun setMiniPlayerShowSeekControls( showSeekControls: Boolean ) {
        viewModelScope.launch {
            settingsRepository.setMiniPlayerShowSeekControls( showSeekControls )
        }
    }

    fun setMiniPlayerTextMarquee( textMarquee: Boolean ) {
        viewModelScope.launch { settingsRepository.setMiniPlayerTextMarquee( textMarquee ) }
    }

    fun setControlsLayoutIsDefault( controlsLayoutIsDefault: Boolean ) {
        viewModelScope.launch {
            settingsRepository.setControlsLayoutIsDefault( controlsLayoutIsDefault )
        }
    }

    fun setNowPlayingLyricsLayout( nowPlayingLyricsLayout: NowPlayingLyricsLayout) {
        viewModelScope.launch {
            settingsRepository.setNowPlayingLyricsLayout( nowPlayingLyricsLayout )
        }
    }

    fun setShowNowPlayingAudioInformation( showNowPlayingAudioInformation: Boolean ) {
        viewModelScope.launch {
            settingsRepository.setShowNowPlayingAudioInformation( showNowPlayingAudioInformation )
        }
    }

    fun setShowNowPlayingSeekControls( showNowPlayingSeekControls: Boolean ) {
        viewModelScope.launch {
            settingsRepository.setShowNowPlayingSeekControls( showNowPlayingSeekControls )
        }
    }

}

@Suppress( "UNCHECKED_CAST" )
class SettingsViewModelFactory(
    private val settingsRepository: SettingsRepository
) : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel> create( modelClass: Class<T> ) =
        ( SettingsViewModel( settingsRepository ) as T )
}

data class SettingsScreenUiState(
    val language: Language,
    val font: MusicallyFont,
    val fontScale: Float,
    val themeMode: ThemeMode,
    val useMaterialYou: Boolean,
    val primaryColorName: String,
    val homePageBottomBarLabelVisibility: HomePageBottomBarLabelVisibility,
    val fadePlayback: Boolean,
    val fadePlaybackDuration: Float,
    val requireAudioFocus: Boolean,
    val ignoreAudioFocusLoss: Boolean,
    val playOnHeadphonesConnect: Boolean,
    val pauseOnHeadphonesDisconnect: Boolean,
    val fastRewindDuration: Int,
    val fastForwardDuration: Int,
    val miniPlayerShowTrackControls: Boolean,
    val miniPlayerShowSeekControls: Boolean,
    val miniPlayerTextMarquee: Boolean,
    val controlsLayoutIsDefault: Boolean,
    val nowPlayingLyricsLayout: NowPlayingLyricsLayout,
    val showNowPlayingAudioInformation: Boolean,
    val showNowPlayingSeekControls: Boolean,
)