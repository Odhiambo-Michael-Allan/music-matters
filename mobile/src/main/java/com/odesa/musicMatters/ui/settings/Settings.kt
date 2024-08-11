package com.odesa.musicMatters.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.East
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.odesa.musicMatters.core.data.preferences.HomePageBottomBarLabelVisibility
import com.odesa.musicMatters.core.data.preferences.NowPlayingLyricsLayout
import com.odesa.musicMatters.core.data.preferences.impl.SettingsDefaults
import com.odesa.musicMatters.core.designsystem.theme.MusicMattersTheme
import com.odesa.musicMatters.core.designsystem.theme.MusicallyFont
import com.odesa.musicMatters.core.designsystem.theme.SupportedFonts
import com.odesa.musicMatters.core.designsystem.theme.ThemeMode
import com.odesa.musicMatters.core.i8n.English
import com.odesa.musicMatters.core.i8n.Language
import com.odesa.musicMatters.ui.components.MinimalAppBar
import com.odesa.musicMatters.ui.settings.Interface.BottomBarLabelVisibility
import com.odesa.musicMatters.ui.settings.appearance.Font
import com.odesa.musicMatters.ui.settings.appearance.FontScale
import com.odesa.musicMatters.ui.settings.appearance.Language
import com.odesa.musicMatters.ui.settings.appearance.MaterialYou
import com.odesa.musicMatters.ui.settings.appearance.PrimaryColor
import com.odesa.musicMatters.ui.settings.appearance.Theme
import com.odesa.musicMatters.ui.settings.components.SettingsSideHeading
import com.odesa.musicMatters.ui.settings.miniPlayer.ShowSeekControls
import com.odesa.musicMatters.ui.settings.miniPlayer.ShowTrackControls
import com.odesa.musicMatters.ui.settings.miniPlayer.TextMarquee
import com.odesa.musicMatters.ui.settings.nowPlaying.ControlsLayout
import com.odesa.musicMatters.ui.settings.nowPlaying.LyricsLayout
import com.odesa.musicMatters.ui.settings.nowPlaying.ShowAudioInformation
import com.odesa.musicMatters.ui.settings.player.FadePlayback
import com.odesa.musicMatters.ui.settings.player.FadePlaybackDuration
import com.odesa.musicMatters.ui.settings.player.FastForwardDuration
import com.odesa.musicMatters.ui.settings.player.FastRewindDuration
import com.odesa.musicMatters.ui.settings.player.IgnoreAudioFocusLoss
import com.odesa.musicMatters.ui.settings.player.PauseOnHeadphonesDisconnect
import com.odesa.musicMatters.ui.settings.player.PlayOnHeadphonesConnect
import com.odesa.musicMatters.ui.settings.player.RequireAudioFocus

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel,
    onBackPressed: () -> Unit
) {
    val settingsScreenUiState by viewModel.uiState.collectAsStateWithLifecycle()

    SettingsScreenContent(
        uiState = settingsScreenUiState,
        onBackPressed = onBackPressed,
        onLanguageChange = {
                newLanguage -> viewModel.setLanguage( newLanguage.locale )
        },
        onFontChange = {
                newFont -> viewModel.setFont( newFont.name )
        },
        onFontScaleChange = viewModel::setFontScale,
        onThemeChange = viewModel::setThemeMode,
        onUseMaterialYouChange = viewModel::setUseMaterialYou,
        onPrimaryColorChange = viewModel::setPrimaryColorName,
        onHomePageBottomBarLabelVisibilityChange = viewModel::setHomePageBottomBarLabelVisibility,
        onFadePlaybackChange = viewModel::setFadePlayback,
        onFadePlaybackDurationChange = viewModel::setFadePlaybackDuration,
        onRequireAudioFocusChange = viewModel::setRequireAudioFocus,
        onIgnoreAudioFocusLossChange = viewModel::setIgnoreAudioFocusLoss,
        onPlayOnHeadphonesConnectChange = viewModel::setPlayOnHeadphonesConnect,
        onPauseOnHeadphonesDisconnectChange = viewModel::setPauseOnHeadphonesDisconnect,
        onFastRewindDurationChange = { fastRewindDuration ->
            viewModel.setFastRewindDuration( fastRewindDuration.toInt() )
        },
        onFastForwardDurationChange = { fastForwardDuration ->
            viewModel.setFastForwardDuration( fastForwardDuration.toInt() )
        },
        onMiniPlayerShowTrackControlsChange = viewModel::setMiniPlayerShowTrackControls,
        onMiniPlayerShowSeekControlsChange = viewModel::setMiniPlayerShowSeekControls,
        onMiniPlayerTextMarqueeChange = viewModel::setMiniPlayerTextMarquee,
        onNowPlayingControlsLayoutChange = viewModel::setControlsLayoutIsDefault,
        onNowPlayingLyricsLayoutChange = viewModel::setNowPlayingLyricsLayout,
        onShowNowPlayingAudioInformationChange = viewModel::setShowNowPlayingAudioInformation,
        onShowNowPlayingSeekControlsChange = viewModel::setShowNowPlayingSeekControls
    )
}

@Composable
fun SettingsScreenContent(
    uiState: SettingsScreenUiState,
    onBackPressed: () -> Unit,
    onLanguageChange: (Language) -> Unit,
    onFontChange: (MusicallyFont) -> Unit,
    onFontScaleChange: ( String ) -> Unit,
    onThemeChange: (ThemeMode) -> Unit,
    onUseMaterialYouChange: ( Boolean ) -> Unit,
    onPrimaryColorChange: ( String ) -> Unit,
    onHomePageBottomBarLabelVisibilityChange: (HomePageBottomBarLabelVisibility) -> Unit,
    onFadePlaybackChange: ( Boolean ) -> Unit,
    onFadePlaybackDurationChange: ( Float ) -> Unit,
    onRequireAudioFocusChange: ( Boolean ) -> Unit,
    onIgnoreAudioFocusLossChange: ( Boolean ) -> Unit,
    onPlayOnHeadphonesConnectChange: ( Boolean ) -> Unit,
    onPauseOnHeadphonesDisconnectChange: ( Boolean ) -> Unit,
    onFastRewindDurationChange: ( Float ) -> Unit,
    onFastForwardDurationChange: ( Float ) -> Unit,
    onMiniPlayerShowTrackControlsChange: ( Boolean ) -> Unit,
    onMiniPlayerShowSeekControlsChange: ( Boolean ) -> Unit,
    onMiniPlayerTextMarqueeChange: ( Boolean ) -> Unit,
    onNowPlayingControlsLayoutChange: ( Boolean ) -> Unit,
    onNowPlayingLyricsLayoutChange: (NowPlayingLyricsLayout) -> Unit,
    onShowNowPlayingAudioInformationChange: ( Boolean ) -> Unit,
    onShowNowPlayingSeekControlsChange: ( Boolean ) -> Unit,

    ) {

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        MinimalAppBar(
            modifier = Modifier.fillMaxWidth(),
            onNavigationIconClicked = onBackPressed,
            title = uiState.language.settings
        )
        LazyColumn(
            modifier = Modifier.weight( 1f )
        ) {
            item {
                val contentColor = MaterialTheme.colorScheme.onPrimary

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.primary)
                        .clickable { }
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp, 8.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Favorite,
                            contentDescription = null,
                            tint = contentColor,
                            modifier = Modifier.size( 12.dp )
                        )
                        Spacer( modifier = Modifier.width( 4.dp ) )
                        Text(
                            text = uiState.language.considerContributing,
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = contentColor
                            )
                        )
                    }
                    Box(
                        modifier = Modifier
                            .align(Alignment.CenterEnd)
                            .padding(8.dp, 0.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.East,
                            contentDescription = null,
                            tint = contentColor,
                            modifier = Modifier.size( 20.dp )
                        )
                    }
                }
                SettingsSideHeading( text = uiState.language.appearance )
                Language(
                    language = uiState.language,
                    onLanguageChange = onLanguageChange
                )
                Font(
                    font = uiState.font,
                    language = uiState.language,
                    onFontChange = onFontChange
                )
                FontScale(
                    language = uiState.language,
                    fontScale = uiState.fontScale,
                    onFontScaleChange = onFontScaleChange
                )
                Theme(
                    language = uiState.language,
                    themeMode = uiState.themeMode,
                    onThemeChange = onThemeChange
                )
                MaterialYou(
                    language = uiState.language,
                    useMaterialYou = uiState.useMaterialYou,
                    onUseMaterialYouChange = onUseMaterialYouChange
                )
                PrimaryColor(
                    primaryColor = uiState.primaryColorName,
                    language = uiState.language,
                    onPrimaryColorChange = onPrimaryColorChange,
                    useMaterialYou = uiState.useMaterialYou
                )
                Divider( thickness = 0.5.dp )
                SettingsSideHeading( text = uiState.language.Interface )
                BottomBarLabelVisibility(
                    value = uiState.homePageBottomBarLabelVisibility,
                    language = uiState.language,
                    onValueChange = onHomePageBottomBarLabelVisibilityChange
                )
                Divider( thickness = 0.5.dp )
                SettingsSideHeading( text = uiState.language.player )
                FadePlayback(
                    language = uiState.language,
                    fadePlayback = uiState.fadePlayback,
                    onFadePlaybackChange = onFadePlaybackChange
                )
                FadePlaybackDuration(
                    language = uiState.language,
                    value = uiState.fadePlaybackDuration,
                    onFadePlaybackDurationChange = onFadePlaybackDurationChange
                )
                RequireAudioFocus(
                    language = uiState.language,
                    requireAudioFocus = uiState.requireAudioFocus,
                    onRequireAudioFocusChange = onRequireAudioFocusChange
                )
                IgnoreAudioFocusLoss(
                    language = uiState.language,
                    ignoreAudioFocusLoss = uiState.ignoreAudioFocusLoss,
                    onIgnoreAudioFocusLossChange = onIgnoreAudioFocusLossChange
                )
                PlayOnHeadphonesConnect(
                    language = uiState.language,
                    playOnHeadphonesConnect = uiState.playOnHeadphonesConnect,
                    onPlayOnHeadphonesConnectChange = onPlayOnHeadphonesConnectChange
                )
                PauseOnHeadphonesDisconnect(
                    language = uiState.language,
                    pauseOnHeadphonesDisconnect = uiState.pauseOnHeadphonesDisconnect,
                    onPauseOnHeadphonesDisconnectChange = onPauseOnHeadphonesDisconnectChange
                )
                FastRewindDuration(
                    language = uiState.language,
                    value = uiState.fastRewindDuration.toFloat(),
                    onFastRewindDurationChange = onFastRewindDurationChange
                )
                FastForwardDuration(
                    language = uiState.language,
                    value = uiState.fastForwardDuration.toFloat(),
                    onFastForwardDurationChange = onFastForwardDurationChange
                )
                Divider( thickness = 0.5.dp )
                SettingsSideHeading( text = uiState.language.miniPlayer )
                ShowTrackControls(
                    value = uiState.miniPlayerShowTrackControls,
                    language = uiState.language,
                    onValueChange = onMiniPlayerShowTrackControlsChange
                )
                ShowSeekControls(
                    language = uiState.language,
                    value = uiState.miniPlayerShowSeekControls,
                    onValueChange = onMiniPlayerShowSeekControlsChange
                )
                TextMarquee(
                    language = uiState.language,
                    value = uiState.miniPlayerTextMarquee,
                    onValueChange = onMiniPlayerTextMarqueeChange
                )
                Divider( thickness = 0.5.dp )
                SettingsSideHeading( text = uiState.language.nowPlaying )
                ControlsLayout(
                    controlsLayoutIsDefault = uiState.controlsLayoutIsDefault,
                    language = uiState.language,
                    onNowPlayingControlsLayoutChange = onNowPlayingControlsLayoutChange
                )
                LyricsLayout(
                    nowPlayingLyricsLayout = uiState.nowPlayingLyricsLayout,
                    language = uiState.language,
                    onNowPlayingLyricsLayoutChange = onNowPlayingLyricsLayoutChange
                )
                ShowAudioInformation(
                    language = uiState.language,
                    value = uiState.showNowPlayingAudioInformation,
                    onValueChange = onShowNowPlayingAudioInformationChange
                )
                ShowSeekControls(
                    language = uiState.language,
                    value = uiState.showNowPlayingSeekControls,
                    onValueChange = onShowNowPlayingSeekControlsChange
                )
                Divider( thickness = 0.5.dp )
                SettingsSideHeading( text = uiState.language.groove )
            }
        }
    }
}

@Preview( showSystemUi = true )
@Composable
fun SettingsScreenContentPreview() {
    val uiState = SettingsScreenUiState(
        language = English,
        font = SupportedFonts.ProductSans,
        fontScale = SettingsDefaults.FONT_SCALE,
        themeMode = ThemeMode.LIGHT,
        useMaterialYou = SettingsDefaults.USE_MATERIAL_YOU,
        primaryColorName = "Blue",
        homePageBottomBarLabelVisibility = SettingsDefaults.homePageBottomBarLabelVisibility,
        fadePlayback = SettingsDefaults.FADE_PLAYBACK,
        fadePlaybackDuration = SettingsDefaults.FADE_PLAYBACK_DURATION,
        requireAudioFocus = SettingsDefaults.REQUIRE_AUDIO_FOCUS,
        ignoreAudioFocusLoss = SettingsDefaults.IGNORE_AUDIO_FOCUS_LOSS,
        playOnHeadphonesConnect = SettingsDefaults.PLAY_ON_HEADPHONES_CONNECT,
        pauseOnHeadphonesDisconnect = SettingsDefaults.PAUSE_ON_HEADPHONES_DISCONNECT,
        fastRewindDuration = SettingsDefaults.FAST_REWIND_DURATION,
        fastForwardDuration = SettingsDefaults.FAST_FORWARD_DURATION,
        miniPlayerShowTrackControls = SettingsDefaults.MINI_PLAYER_SHOW_TRACK_CONTROLS,
        miniPlayerShowSeekControls = SettingsDefaults.MINI_PLAYERS_SHOW_SEEK_CONTROLS,
        miniPlayerTextMarquee = SettingsDefaults.MINI_PLAYER_TEXT_MARQUEE,
        controlsLayoutIsDefault = SettingsDefaults.CONTROLS_LAYOUT_IS_DEFAULT,
        nowPlayingLyricsLayout = SettingsDefaults.nowPlayingLyricsLayout,
        showNowPlayingAudioInformation = SettingsDefaults.SHOW_NOW_PLAYING_AUDIO_INFORMATION,
        showNowPlayingSeekControls = SettingsDefaults.SHOW_NOW_PLAYING_SEEK_CONTROLS,
    )
    MusicMattersTheme(
        themeMode = ThemeMode.LIGHT,
        primaryColorName = SettingsDefaults.PRIMARY_COLOR_NAME,
        fontName = SettingsDefaults.font.name,
        fontScale = SettingsDefaults.FONT_SCALE,
        useMaterialYou = SettingsDefaults.USE_MATERIAL_YOU
    ) {
        SettingsScreenContent(
            uiState = uiState,
            onBackPressed = {},
            onLanguageChange = {},
            onFontChange = {},
            onFontScaleChange = {},
            onThemeChange = {},
            onUseMaterialYouChange = {},
            onPrimaryColorChange = {},
            onHomePageBottomBarLabelVisibilityChange = {},
            onFadePlaybackChange = {},
            onFadePlaybackDurationChange = {},
            onRequireAudioFocusChange = {},
            onIgnoreAudioFocusLossChange = {},
            onPlayOnHeadphonesConnectChange = {},
            onPauseOnHeadphonesDisconnectChange = {},
            onFastRewindDurationChange = {},
            onFastForwardDurationChange = {},
            onMiniPlayerShowTrackControlsChange = {},
            onMiniPlayerShowSeekControlsChange = {},
            onMiniPlayerTextMarqueeChange = {},
            onNowPlayingControlsLayoutChange = {},
            onNowPlayingLyricsLayoutChange = {},
            onShowNowPlayingAudioInformationChange = {},
            onShowNowPlayingSeekControlsChange = {}
        )
    }
}