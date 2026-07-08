package com.squad.musicmatters.feature.settings

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.annotation.ColorInt
import androidx.browser.customtabs.CustomTabColorSchemeParams
import androidx.browser.customtabs.CustomTabsIntent
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.squad.musicmatters.core.datastore.DefaultPreferences
import com.squad.musicmatters.core.designsystem.theme.MusicMattersFont
import com.squad.musicmatters.core.designsystem.theme.MusicMattersTheme
import com.squad.musicmatters.core.designsystem.theme.MusicMattersTypography
import com.squad.musicmatters.core.designsystem.theme.SupportedFonts
import com.squad.musicmatters.core.model.BottomBarLabelVisibility
import com.squad.musicmatters.core.model.LoopMode
import com.squad.musicmatters.core.model.SortAlbumsBy
import com.squad.musicmatters.core.model.SortArtistsBy
import com.squad.musicmatters.core.model.SortGenresBy
import com.squad.musicmatters.core.model.SortPathsBy
import com.squad.musicmatters.core.model.SortPlaylistsBy
import com.squad.musicmatters.core.model.SortSongsBy
import com.squad.musicmatters.core.model.ThemeMode
import com.squad.musicmatters.core.model.UserData
import com.squad.musicmatters.core.ui.LibraryDestinationContainer
import com.squad.musicmatters.feature.settings.Interface.BottomBarLabelVisibility
import com.squad.musicmatters.feature.settings.about.About
import com.squad.musicmatters.feature.settings.appearance.Font
import com.squad.musicmatters.feature.settings.appearance.FontScale
import com.squad.musicmatters.feature.settings.appearance.Language
import com.squad.musicmatters.feature.settings.appearance.PrimaryColor
import com.squad.musicmatters.feature.settings.appearance.Theme
import com.squad.musicmatters.feature.settings.appearance.UseMaterialYou
import com.squad.musicmatters.feature.settings.community.Community
import com.squad.musicmatters.feature.settings.components.SettingsSideHeading
import com.squad.musicmatters.feature.settings.miniPlayer.MiniPlayerTextMarquee
import com.squad.musicmatters.feature.settings.nowPlaying.LyricsLayout
import com.squad.musicmatters.feature.settings.player.PauseOnHeadphonesDisconnect
import com.squad.musicmatters.feature.settings.player.PlayOnHeadphonesConnect
import com.squad.musicmatters.core.i8n.R as i8nR

@Composable
internal fun SettingsScreen(
    viewModel: SettingsScreenViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit,
) {

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val backgroundColor = MaterialTheme.colorScheme.background
    val context = LocalContext.current

    SettingsScreenContent(
        uiState = uiState,
        onNavigateBack = onNavigateBack,
        onFontChange = viewModel::setFont,
        onFontScaleChange = viewModel::setFontScale,
        onThemeModeChange = viewModel::setThemeMode,
        onUseMaterialYouChange = viewModel::setUseMaterialYou,
        onPrimaryColorNameChange = viewModel::setPrimaryColorName,
        onBottomBarLabelVisibilityChange = viewModel::setBottomBarLabelVisibility,
        onPlayOnHeadphonesConnectChange = viewModel::setPlayOnHeadphonesConnect,
        onPauseOnHeadphonesDisconnectChange = viewModel::setPauseOnHeadphonesDisconnect,
        onMiniPlayerTextMarqueeChange = viewModel::setMiniPlayerTextMarquee,
        onShowLyricsOnSeparateScreenChange = viewModel::setShowLyricsOnSeparateScreen,
        onGoToReddit = {
            context.launchCustomChromeTab(
                context.getString( i8nR.string.core_i8n_reddit_community_url ).toUri(),
                backgroundColor.toArgb()
            )
        },
        onGoToAppGithubRepository = {
            context.launchCustomChromeTab(
                context.getString( i8nR.string.core_i8n_app_github_repo_url ).toUri(),
                backgroundColor.toArgb()
            )
        },
        onGoToTelegram = {
            context.launchCustomChromeTab(
                context.getString( i8nR.string.core_i8n_telegram_channel_link ).toUri(),
                backgroundColor.toArgb()
            )
        },
        onGoToGithubProfile = {
            context.launchCustomChromeTab(
                context.getString( i8nR.string.core_i8n_github_profile_url ).toUri(),
                backgroundColor.toArgb()
            )
        },
        onGoToDiscord = {
            context.launchCustomChromeTab(
                context.getString( i8nR.string.core_i8n_discord_server_url ).toUri(),
                backgroundColor.toArgb(),
            )
        }
    )
}

@Composable
private fun SettingsScreenContent(
    uiState: SettingsScreenUiState,
    onNavigateBack: () -> Unit,
    onFontChange: ( MusicMattersFont ) -> Unit,
    onFontScaleChange: ( String ) -> Unit,
    onThemeModeChange: ( ThemeMode ) -> Unit,
    onUseMaterialYouChange: ( Boolean ) -> Unit,
    onPrimaryColorNameChange: ( String ) -> Unit,
    onBottomBarLabelVisibilityChange: ( BottomBarLabelVisibility ) -> Unit,
    onPlayOnHeadphonesConnectChange: ( Boolean ) -> Unit,
    onPauseOnHeadphonesDisconnectChange: ( Boolean ) -> Unit,
    onMiniPlayerTextMarqueeChange: ( Boolean ) -> Unit,
    onShowLyricsOnSeparateScreenChange: ( Boolean ) -> Unit,
    onGoToReddit: () -> Unit,
    onGoToDiscord: () -> Unit,
    onGoToTelegram: () -> Unit,
    onGoToGithubProfile: () -> Unit,
    onGoToAppGithubRepository: () -> Unit,
) {

    LibraryDestinationContainer(
        isLoading = uiState is SettingsScreenUiState.Loading,
        titleResId = i8nR.string.core_i8n_settings,
        onNavigateBack = onNavigateBack,
    ) {
        when ( uiState ) {
            SettingsScreenUiState.Loading -> {}
            is SettingsScreenUiState.Success -> {
                LazyColumn {
                    item {
                        SettingsSideHeading( text = stringResource( id = i8nR.string.core_i8n_appearance ) )
                        Language()
                        Font(
                            font = MusicMattersTypography.resolveFont( uiState.userData.fontName ),
                            onFontChange = onFontChange
                        )
                        FontScale(
                            fontScale = uiState.userData.fontScale,
                            onFontScaleChange = onFontScaleChange,
                        )
                        Theme(
                            themeMode = uiState.userData.themeMode,
                            onThemeChange = onThemeModeChange,
                        )
                        UseMaterialYou(
                            useMaterialYou = uiState.userData.useMaterialYou,
                            onUseMaterialYouChange = onUseMaterialYouChange,
                        )
                        PrimaryColor(
                            primaryColor = uiState.userData.primaryColorName,
                            useMaterialYou = uiState.userData.useMaterialYou,
                            onPrimaryColorChange = onPrimaryColorNameChange,
                        )
                        HorizontalDivider(
                            thickness = 0.5.dp,
                            modifier = Modifier.padding( 16.dp, 0.dp )
                        )
                        SettingsSideHeading(
                            text = stringResource( id = i8nR.string.core_i8n_interface )
                        )
                        BottomBarLabelVisibility(
                            value = uiState.userData.bottomBarLabelVisibility,
                            onValueChange = onBottomBarLabelVisibilityChange,
                        )
                        HorizontalDivider(
                            thickness = 0.5.dp,
                            modifier = Modifier.padding( 16.dp, 0.dp )
                        )
                        SettingsSideHeading(
                            text = stringResource( id = i8nR.string.core_i8n_player )
                        )
                        PlayOnHeadphonesConnect(
                            playOnHeadphonesConnect = uiState.userData.playOnHeadphonesConnect,
                            onPlayOnHeadphonesConnectChange = onPlayOnHeadphonesConnectChange,
                        )
                        PauseOnHeadphonesDisconnect(
                            pauseOnHeadphonesDisconnect = uiState.userData.pauseOnHeadphonesDisconnect,
                            onPauseOnHeadphonesDisconnectChange = onPauseOnHeadphonesDisconnectChange,
                        )
                        HorizontalDivider(
                            thickness = 0.5.dp,
                            modifier = Modifier.padding( 16.dp, 0.dp )
                        )
                        SettingsSideHeading(
                            text = stringResource( id = i8nR.string.core_i8n_mini_player )
                        )
                        MiniPlayerTextMarquee(
                            value = uiState.userData.miniPlayerTextMarquee,
                            onValueChange = onMiniPlayerTextMarqueeChange,
                        )
                        HorizontalDivider(
                            thickness = 0.5.dp,
                            modifier = Modifier.padding( 16.dp, 0.dp )
                        )
                        SettingsSideHeading(
                            text = stringResource( id = i8nR.string.core_i8n_now_playing )
                        )
                        LyricsLayout(
                            showLyricsOnSeparateScreen = uiState.userData.showLyricsOnSeparateScreen,
                            onShowLyricsOnSeparateScreenChange = onShowLyricsOnSeparateScreenChange,
                        )
                        HorizontalDivider(
                            thickness = 0.5.dp,
                            modifier = Modifier.padding( 16.dp, 0.dp )
                        )
                        SettingsSideHeading(
                            text = stringResource( id = i8nR.string.core_i8n_community )
                        )
                        Community(
                            onGoToReddit = onGoToReddit,
                            onGoToDiscord = onGoToDiscord,
                            onGoToTelegram = onGoToTelegram,
                        )
                        HorizontalDivider(
                            thickness = 0.5.dp,
                            modifier = Modifier.padding( 16.dp, 0.dp )
                        )
                        SettingsSideHeading(
                            text = stringResource( id = i8nR.string.core_i8n_about )
                        )
                        About(
                            onGoToGithubProfile = onGoToGithubProfile,
                            onGoToAppGithubRepository = onGoToAppGithubRepository,
                        )
                    }
                }
            }
        }
    }
}

@Preview( showBackground = true )
@Composable
private fun SettingsScreenContentPreview() {
    MusicMattersTheme(
        themeMode = DefaultPreferences.THEME_MODE,
        primaryColorName = DefaultPreferences.PRIMARY_COLOR_NAME,
        fontScale = 1.0f,
        fontName = SupportedFonts.ProductSans.name,
        useMaterialYou = true
    ) {
        SettingsScreenContent(
            uiState = SettingsScreenUiState.Success(
                userData = UserData(
                    fontName = SupportedFonts.ProductSans.name,
                    fontScale = 1f,
                    themeMode = ThemeMode.FOLLOW_SYSTEM,
                    useMaterialYou = true,
                    primaryColorName = "Blue",
                    bottomBarLabelVisibility = BottomBarLabelVisibility.ALWAYS_VISIBLE,
                    fadePlayback = true,
                    fadePlaybackDuration = 1f,
                    requireAudioFocus = true,
                    ignoreAudioFocusLoss = false,
                    playOnHeadphonesConnect = true,
                    pauseOnHeadphonesDisconnect = false,
                    miniPlayerTextMarquee = true,
                    loopMode = LoopMode.None,
                    shuffle = false,
                    showLyrics = false,
                    currentlyDisabledTreePaths = emptySet(),
                    sortSongsBy = SortSongsBy.TITLE,
                    sortSongsReverse = false,
                    sortArtistsBy = SortArtistsBy.ARTIST_NAME,
                    sortArtistsReverse = false,
                    sortGenresBy = SortGenresBy.NAME,
                    sortGenresReverse = false,
                    sortPlaylistsBy = SortPlaylistsBy.TITLE,
                    sortPlaylistsReverse = false,
                    sortAlbumsBy = SortAlbumsBy.ALBUM_NAME,
                    sortAlbumsReverse = false,
                    sortPathsBy = SortPathsBy.NAME,
                    sortPathsReverse = false,
                    currentlyPlayingSongId = "",
                    showLyricsOnSeparateScreen = false,
                )
            ),
            onNavigateBack = {},
            onFontChange = {},
            onFontScaleChange = {},
            onThemeModeChange = {},
            onUseMaterialYouChange = {},
            onPrimaryColorNameChange = {},
            onBottomBarLabelVisibilityChange = {},
            onPlayOnHeadphonesConnectChange = {},
            onPauseOnHeadphonesDisconnectChange = {},
            onMiniPlayerTextMarqueeChange = {},
            onShowLyricsOnSeparateScreenChange = {},
            onGoToReddit = {},
            onGoToDiscord = {},
            onGoToTelegram = {},
            onGoToGithubProfile = {},
            onGoToAppGithubRepository = {},
        )
    }
}



private fun Context.launchCustomChromeTab( uri: Uri, @ColorInt toolbarColor: Int ) {
    val customTabColor = CustomTabColorSchemeParams.Builder()
        .setToolbarColor( toolbarColor ).build()
    val customTabsIntent = CustomTabsIntent.Builder()
        .setDefaultColorSchemeParams( customTabColor )
        .build()

    try {
        customTabsIntent.launchUrl( this, uri )
    } catch ( exception: Exception ) {
        Log.e( "CUSTOM CHROME TAB", exception.toString() )
    }
}