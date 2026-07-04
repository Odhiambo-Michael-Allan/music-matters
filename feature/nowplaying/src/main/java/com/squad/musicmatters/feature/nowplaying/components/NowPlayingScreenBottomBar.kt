package com.squad.musicmatters.feature.nowplaying.components

import android.content.Context
import android.content.Intent
import androidx.activity.result.contract.ActivityResultContract
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.squad.musicMatters.core.designsystem.R
import com.squad.musicmatters.core.datastore.DefaultPreferences
import com.squad.musicmatters.core.designsystem.component.DevicePreviews
import com.squad.musicmatters.core.designsystem.component.MusicMattersIcons
import com.squad.musicmatters.core.designsystem.theme.MusicMattersTheme
import com.squad.musicmatters.core.model.ThemeMode
import com.squad.musicmatters.core.ui.dialog.ScaffoldDialog


@OptIn( ExperimentalMaterial3Api::class )
@Composable
internal fun NowPlayingScreenBottomBar(
    modifier: Modifier = Modifier,
    showLyrics: Boolean,
    showLyricsOnSeparateScreen: Boolean,
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
                    tint = if ( it ) MaterialTheme.colorScheme.primary else LocalContentColor.current,
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
            onShowLyrics = {},
            onNavigateToQueueScreen = {},
            onNavigateToLyricsScreen = {},
        )
    }
}