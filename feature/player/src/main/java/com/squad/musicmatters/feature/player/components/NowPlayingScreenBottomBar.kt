package com.squad.musicmatters.feature.player.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.squad.musicmatters.core.designsystem.R
import com.squad.musicmatters.core.datastore.DefaultPreferences
import com.squad.musicmatters.core.designsystem.component.DevicePreviews
import com.squad.musicmatters.core.designsystem.component.MusicMattersIcons
import com.squad.musicmatters.core.designsystem.theme.MusicMattersTheme
import com.squad.musicmatters.core.model.ThemeMode


@OptIn( ExperimentalMaterial3Api::class )
@Composable
internal fun NowPlayingScreenBottomBar(
    modifier: Modifier = Modifier,
    showLyrics: Boolean,
    showLyricsOnSeparateScreen: Boolean,
    colorScheme: ColorScheme,
    onShowLyrics: ( Boolean ) -> Unit,
    onNavigateToQueueScreen: () -> Unit,
    onNavigateToLyricsScreen: () -> Unit,
) {

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(
                start = 4.dp,
                end = 4.dp,
                bottom = 4.dp
            ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        AnimatedContent(
            targetState = showLyrics,
            label = "LyricsLayoutAnimation"
        ) {
            IconButton(
                onClick = {
                    if ( showLyricsOnSeparateScreen ) onNavigateToLyricsScreen()
                    else onShowLyrics( !showLyrics )
                }
            ) {
                Icon(
                    painter = painterResource(
                        id = if ( it ) R.drawable.ic_lyrics else R.drawable.ic_lyrics_outline,
                    ),
                    tint = if ( it ) colorScheme.primary else LocalContentColor.current,
                    contentDescription = null,
                )
            }
        }
        IconButton(
            onClick = onNavigateToQueueScreen,
        ) {
            Icon(
                imageVector = MusicMattersIcons.Queue,
                contentDescription = null,
            )
        }
    }
}

@DevicePreviews
@Composable
private fun NowPlayingScreenBottomBarPreview() {
    MusicMattersTheme(
        themeMode = ThemeMode.LIGHT,
        useMaterialYou = true,
        fontName = DefaultPreferences.FONT_NAME,
        fontScale = DefaultPreferences.FONT_SCALE,
        primaryColorName = DefaultPreferences.PRIMARY_COLOR_NAME
    ) {
        NowPlayingScreenBottomBar(
            showLyrics = true,
            showLyricsOnSeparateScreen = false,
            colorScheme = MaterialTheme.colorScheme,
            onShowLyrics = {},
            onNavigateToQueueScreen = {},
            onNavigateToLyricsScreen = {},
        )
    }
}