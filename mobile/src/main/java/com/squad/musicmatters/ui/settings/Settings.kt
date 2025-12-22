package com.squad.musicmatters.ui.settings
//
//import androidx.compose.foundation.layout.Column
//import androidx.compose.foundation.layout.fillMaxSize
//import androidx.compose.foundation.layout.fillMaxWidth
//import androidx.compose.foundation.lazy.LazyColumn
//import androidx.compose.material3.HorizontalDivider
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.getValue
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.tooling.preview.Preview
//import androidx.compose.ui.unit.dp
//import androidx.lifecycle.compose.collectAsStateWithLifecycle
//import com.squad.musicmatters.core.datastore.DefaultPreferences
//import com.squad.musicmatters.core.designsystem.theme.MusicMattersTheme
//import com.squad.musicmatters.core.designsystem.theme.MusicMattersFont
//import com.squad.musicmatters.core.designsystem.theme.SupportedFonts
//import com.squad.musicmatters.core.i8n.English
//import com.squad.musicmatters.core.i8n.Language
//import com.squad.musicmatters.core.model.BottomBarLabelVisibility
//import com.squad.musicmatters.core.model.LyricsLayout
//import com.squad.musicmatters.core.model.ThemeMode
//import com.squad.musicmatters.ui.components.MinimalAppBar
//import com.squad.musicmatters.ui.settings.Interface.BottomBarLabelVisibility
//import com.squad.musicmatters.ui.settings.about.About
//import com.squad.musicmatters.ui.settings.appearance.Font
//import com.squad.musicmatters.ui.settings.appearance.FontScale
//import com.squad.musicmatters.ui.settings.appearance.Language
//import com.squad.musicmatters.ui.settings.appearance.MaterialYou
//import com.squad.musicmatters.ui.settings.appearance.PrimaryColor
//import com.squad.musicmatters.ui.settings.appearance.Theme
//import com.squad.musicmatters.ui.settings.components.SettingsSideHeading
//import com.squad.musicmatters.ui.settings.help.Help
//import com.squad.musicmatters.ui.settings.miniPlayer.ShowSeekControls
//import com.squad.musicmatters.ui.settings.miniPlayer.ShowTrackControls
//import com.squad.musicmatters.ui.settings.miniPlayer.TextMarquee
//import com.squad.musicmatters.ui.settings.nowPlaying.ControlsLayout
//import com.squad.musicmatters.ui.settings.nowPlaying.LyricsLayout
//import com.squad.musicmatters.ui.settings.nowPlaying.ShowAudioInformation
//import com.squad.musicmatters.ui.settings.player.FadePlayback
//import com.squad.musicmatters.ui.settings.player.FadePlaybackDuration
//import com.squad.musicmatters.ui.settings.player.FastForwardDuration
//import com.squad.musicmatters.ui.settings.player.FastRewindDuration
//import com.squad.musicmatters.ui.settings.player.IgnoreAudioFocusLoss
//import com.squad.musicmatters.ui.settings.player.PauseOnHeadphonesDisconnect
//import com.squad.musicmatters.ui.settings.player.PlayOnHeadphonesConnect
//import com.squad.musicmatters.ui.settings.player.RequireAudioFocus
//
//@Composable
//fun SettingsScreen(
//    viewModel: SettingsViewModel,
//    onBackPressed: () -> Unit,
//    goToRedditCommunity: () -> Unit,
//    goToDiscordServer: () -> Unit,
//    goToTelegramChannel: () -> Unit,
//    goToGithubProfile: () -> Unit,
//    goToAppGithubRepository: () -> Unit,
//) {
//    val settingsScreenUiState by viewModel.uiState.collectAsStateWithLifecycle()
//
//    SettingsScreenContent(
//        uiState = settingsScreenUiState,
//        onBackPressed = onBackPressed,
//        onLanguageChange = {
//                newLanguage -> viewModel.setLanguage( newLanguage.locale )
//        },
//        onFontChange = {
//                newFont -> viewModel.setFont( newFont.name )
//        },
//        onFontScaleChange = viewModel::setFontScale,
//        onThemeChange = viewModel::setThemeMode,
//        onUseMaterialYouChange = viewModel::setUseMaterialYou,
//        onPrimaryColorChange = viewModel::setPrimaryColorName,
//        onHomePageBottomBarLabelVisibilityChange = viewModel::setHomePageBottomBarLabelVisibility,
//        onFadePlaybackChange = viewModel::setFadePlayback,
//        onFadePlaybackDurationChange = viewModel::setFadePlaybackDuration,
//        onRequireAudioFocusChange = viewModel::setRequireAudioFocus,
//        onIgnoreAudioFocusLossChange = viewModel::setIgnoreAudioFocusLoss,
//        onPlayOnHeadphonesConnectChange = viewModel::setPlayOnHeadphonesConnect,
//        onPauseOnHeadphonesDisconnectChange = viewModel::setPauseOnHeadphonesDisconnect,
//        onFastRewindDurationChange = { fastRewindDuration ->
//            viewModel.setFastRewindDuration( fastRewindDuration.toInt() )
//        },
//        onFastForwardDurationChange = { fastForwardDuration ->
//            viewModel.setFastForwardDuration( fastForwardDuration.toInt() )
//        },
//        onMiniPlayerShowTrackControlsChange = viewModel::setMiniPlayerShowTrackControls,
//        onMiniPlayerShowSeekControlsChange = viewModel::setMiniPlayerShowSeekControls,
//        onMiniPlayerTextMarqueeChange = viewModel::setMiniPlayerTextMarquee,
//        onNowPlayingControlsLayoutChange = viewModel::setControlsLayoutIsDefault,
//        onNowPlayingLyricsLayoutChange = viewModel::setNowPlayingLyricsLayout,
//        onShowNowPlayingAudioInformationChange = viewModel::setShowNowPlayingAudioInformation,
//        onShowNowPlayingSeekControlsChange = viewModel::setShowNowPlayingSeekControls,
//        goToRedditCommunity = goToRedditCommunity,
//        goToTelegramChannel = goToTelegramChannel,
//        goToDiscordServer = goToDiscordServer,
//        goToGithubProfile = goToGithubProfile,
//        goToAppGithubRepository = goToAppGithubRepository,
//    )
//}
//
//@Composable
//fun SettingsScreenContent(
//    uiState: SettingsScreenUiState,
//    onBackPressed: () -> Unit,
//    onLanguageChange: (Language) -> Unit,
//    onFontChange: (MusicMattersFont) -> Unit,
//    onFontScaleChange: ( String ) -> Unit,
//    onThemeChange: (ThemeMode) -> Unit,
//    onUseMaterialYouChange: ( Boolean ) -> Unit,
//    onPrimaryColorChange: ( String ) -> Unit,
//    onHomePageBottomBarLabelVisibilityChange: ( BottomBarLabelVisibility ) -> Unit,
//    onFadePlaybackChange: ( Boolean ) -> Unit,
//    onFadePlaybackDurationChange: ( Float ) -> Unit,
//    onRequireAudioFocusChange: ( Boolean ) -> Unit,
//    onIgnoreAudioFocusLossChange: ( Boolean ) -> Unit,
//    onPlayOnHeadphonesConnectChange: ( Boolean ) -> Unit,
//    onPauseOnHeadphonesDisconnectChange: ( Boolean ) -> Unit,
//    onFastRewindDurationChange: ( Float ) -> Unit,
//    onFastForwardDurationChange: ( Float ) -> Unit,
//    onMiniPlayerShowTrackControlsChange: ( Boolean ) -> Unit,
//    onMiniPlayerShowSeekControlsChange: ( Boolean ) -> Unit,
//    onMiniPlayerTextMarqueeChange: ( Boolean ) -> Unit,
//    onNowPlayingControlsLayoutChange: ( Boolean ) -> Unit,
//    onNowPlayingLyricsLayoutChange: ( LyricsLayout ) -> Unit,
//    onShowNowPlayingAudioInformationChange: ( Boolean ) -> Unit,
//    onShowNowPlayingSeekControlsChange: ( Boolean ) -> Unit,
//    goToRedditCommunity: () -> Unit,
//    goToDiscordServer: () -> Unit,
//    goToTelegramChannel: () -> Unit,
//    goToGithubProfile: () -> Unit,
//    goToAppGithubRepository: () -> Unit,
//    ) {
//
//    Column(
//        modifier = Modifier.fillMaxSize()
//    ) {
//        MinimalAppBar(
//            modifier = Modifier.fillMaxWidth(),
//            onNavigationIconClicked = onBackPressed,
//            title = uiState.language.settings
//        )
//        LazyColumn(
//            modifier = Modifier.weight( 1f )
//        ) {
//            item {
//                SettingsSideHeading( text = uiState.language.appearance )
//                Language(
//                    language = uiState.language,
//                    onLanguageChange = onLanguageChange
//                )
//                Font(
//                    font = uiState.font,
//                    language = uiState.language,
//                    onFontChange = onFontChange
//                )
//                FontScale(
//                    language = uiState.language,
//                    fontScale = uiState.fontScale,
//                    onFontScaleChange = onFontScaleChange
//                )
//                Theme(
//                    language = uiState.language,
//                    themeMode = uiState.themeMode,
//                    onThemeChange = onThemeChange
//                )
//                MaterialYou(
//                    language = uiState.language,
//                    useMaterialYou = uiState.useMaterialYou,
//                    onUseMaterialYouChange = onUseMaterialYouChange
//                )
//                PrimaryColor(
//                    primaryColor = uiState.primaryColorName,
//                    language = uiState.language,
//                    onPrimaryColorChange = onPrimaryColorChange,
//                    useMaterialYou = uiState.useMaterialYou
//                )
//                HorizontalDivider( thickness = 0.5.dp )
//                SettingsSideHeading( text = uiState.language.Interface )
//                BottomBarLabelVisibility(
//                    value = uiState.homePageBottomBarLabelVisibility,
//                    language = uiState.language,
//                    onValueChange = onHomePageBottomBarLabelVisibilityChange
//                )
//                HorizontalDivider( thickness = 0.5.dp )
//                SettingsSideHeading( text = uiState.language.player )
//                FadePlayback(
//                    language = uiState.language,
//                    fadePlayback = uiState.fadePlayback,
//                    onFadePlaybackChange = onFadePlaybackChange
//                )
//                FadePlaybackDuration(
//                    language = uiState.language,
//                    value = uiState.fadePlaybackDuration,
//                    onFadePlaybackDurationChange = onFadePlaybackDurationChange
//                )
//                RequireAudioFocus(
//                    language = uiState.language,
//                    requireAudioFocus = uiState.requireAudioFocus,
//                    onRequireAudioFocusChange = onRequireAudioFocusChange
//                )
//                IgnoreAudioFocusLoss(
//                    language = uiState.language,
//                    ignoreAudioFocusLoss = uiState.ignoreAudioFocusLoss,
//                    onIgnoreAudioFocusLossChange = onIgnoreAudioFocusLossChange
//                )
//                PlayOnHeadphonesConnect(
//                    language = uiState.language,
//                    playOnHeadphonesConnect = uiState.playOnHeadphonesConnect,
//                    onPlayOnHeadphonesConnectChange = onPlayOnHeadphonesConnectChange
//                )
//                PauseOnHeadphonesDisconnect(
//                    language = uiState.language,
//                    pauseOnHeadphonesDisconnect = uiState.pauseOnHeadphonesDisconnect,
//                    onPauseOnHeadphonesDisconnectChange = onPauseOnHeadphonesDisconnectChange
//                )
//                FastRewindDuration(
//                    language = uiState.language,
//                    value = uiState.fastRewindDuration.toFloat(),
//                    onFastRewindDurationChange = onFastRewindDurationChange
//                )
//                FastForwardDuration(
//                    language = uiState.language,
//                    value = uiState.fastForwardDuration.toFloat(),
//                    onFastForwardDurationChange = onFastForwardDurationChange
//                )
//                HorizontalDivider( thickness = 0.5.dp )
//                SettingsSideHeading( text = uiState.language.miniPlayer )
//                ShowTrackControls(
//                    value = uiState.miniPlayerShowTrackControls,
//                    language = uiState.language,
//                    onValueChange = onMiniPlayerShowTrackControlsChange
//                )
//                ShowSeekControls(
//                    language = uiState.language,
//                    value = uiState.miniPlayerShowSeekControls,
//                    onValueChange = onMiniPlayerShowSeekControlsChange
//                )
//                TextMarquee(
//                    language = uiState.language,
//                    value = uiState.miniPlayerTextMarquee,
//                    onValueChange = onMiniPlayerTextMarqueeChange
//                )
//                HorizontalDivider( thickness = 0.5.dp )
//                SettingsSideHeading( text = uiState.language.nowPlaying )
//                ControlsLayout(
//                    controlsLayoutIsDefault = uiState.controlsLayoutIsDefault,
//                    language = uiState.language,
//                    onNowPlayingControlsLayoutChange = onNowPlayingControlsLayoutChange
//                )
//                LyricsLayout(
//                    nowPlayingLyricsLayout = uiState.nowPlayingLyricsLayout,
//                    language = uiState.language,
//                    onNowPlayingLyricsLayoutChange = onNowPlayingLyricsLayoutChange
//                )
//                ShowAudioInformation(
//                    language = uiState.language,
//                    value = uiState.showNowPlayingAudioInformation,
//                    onValueChange = onShowNowPlayingAudioInformationChange
//                )
//                ShowSeekControls(
//                    language = uiState.language,
//                    value = uiState.showNowPlayingSeekControls,
//                    onValueChange = onShowNowPlayingSeekControlsChange
//                )
//                HorizontalDivider( thickness = 0.5.dp )
//                SettingsSideHeading( text = uiState.language.help )
//                Help(
//                    goToReddit = goToRedditCommunity,
//                    goToDiscord = goToDiscordServer,
//                    goToTelegram = goToTelegramChannel
//                )
//                HorizontalDivider( thickness = 0.5.dp )
//                SettingsSideHeading( text = uiState.language.about )
//                About(
//                    language = uiState.language,
//                    goToGithubProfile = goToGithubProfile,
//                    goToAppGithubRepository = goToAppGithubRepository
//                )
//            }
//        }
//    }
//}
//
//@Preview( showSystemUi = true )
//@Composable
//fun SettingsScreenContentPreview() {
//    val uiState = SettingsScreenUiState(
//        language = English,
//        font = SupportedFonts.ProductSans,
//        fontScale = DefaultPreferences.FONT_SCALE,
//        themeMode = ThemeMode.LIGHT,
//        useMaterialYou = true,
//        primaryColorName = "Blue",
//        homePageBottomBarLabelVisibility = DefaultPreferences.BOTTOM_BAR_LABEL_VISIBILITY,
//        fadePlayback = true,
//        fadePlaybackDuration = DefaultPreferences.FADE_PLAYBACK_DURATION,
//        requireAudioFocus = true,
//        ignoreAudioFocusLoss = false,
//        playOnHeadphonesConnect = false,
//        pauseOnHeadphonesDisconnect = true,
//        fastRewindDuration = DefaultPreferences.FAST_REWIND_DURATION,
//        fastForwardDuration = DefaultPreferences.FAST_FORWARD_DURATION,
//        miniPlayerShowTrackControls = true,
//        miniPlayerShowSeekControls = false,
//        miniPlayerTextMarquee = true,
//        controlsLayoutIsDefault = true,
//        nowPlayingLyricsLayout = DefaultPreferences.LYRICS_LAYOUT,
//        showNowPlayingAudioInformation = true,
//        showNowPlayingSeekControls = false,
//    )
//    MusicMattersTheme(
//        themeMode = ThemeMode.LIGHT,
//        primaryColorName = DefaultPreferences.PRIMARY_COLOR_NAME,
//        fontName = DefaultPreferences.FONT_NAME,
//        fontScale = DefaultPreferences.FONT_SCALE,
//        useMaterialYou = true
//    ) {
//        SettingsScreenContent(
//            uiState = uiState,
//            onBackPressed = {},
//            onLanguageChange = {},
//            onFontChange = {},
//            onFontScaleChange = {},
//            onThemeChange = {},
//            onUseMaterialYouChange = {},
//            onPrimaryColorChange = {},
//            onHomePageBottomBarLabelVisibilityChange = {},
//            onFadePlaybackChange = {},
//            onFadePlaybackDurationChange = {},
//            onRequireAudioFocusChange = {},
//            onIgnoreAudioFocusLossChange = {},
//            onPlayOnHeadphonesConnectChange = {},
//            onPauseOnHeadphonesDisconnectChange = {},
//            onFastRewindDurationChange = {},
//            onFastForwardDurationChange = {},
//            onMiniPlayerShowTrackControlsChange = {},
//            onMiniPlayerShowSeekControlsChange = {},
//            onMiniPlayerTextMarqueeChange = {},
//            onNowPlayingControlsLayoutChange = {},
//            onNowPlayingLyricsLayoutChange = {},
//            onShowNowPlayingAudioInformationChange = {},
//            onShowNowPlayingSeekControlsChange = {},
//            goToAppGithubRepository = {},
//            goToRedditCommunity = {},
//            goToTelegramChannel = {},
//            goToDiscordServer = {},
//            goToGithubProfile = {}
//        )
//    }
//}