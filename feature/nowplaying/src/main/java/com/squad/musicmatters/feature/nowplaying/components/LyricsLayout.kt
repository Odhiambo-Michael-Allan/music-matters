package com.squad.musicmatters.feature.nowplaying.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.unit.dp
import com.squad.musicmatters.core.datastore.DefaultPreferences
import com.squad.musicmatters.core.designsystem.component.DevicePreviews
import com.squad.musicmatters.core.designsystem.theme.MusicMattersTheme
import com.squad.musicmatters.core.designsystem.theme.SupportedFonts
import com.squad.musicmatters.core.media.connection.PlaybackPosition
import com.squad.musicmatters.core.model.Lyric
import com.squad.musicmatters.core.ui.LyricsLayout
import com.squad.musicmatters.feature.nowplaying.LyricsUiState
import java.time.Duration

// Stateful
@Composable
internal fun LyricsLayout(
    modifier: Modifier = Modifier,
    lyricsUiState: LyricsUiState,
    onGetPlaybackPosition: () -> PlaybackPosition,
    onSeekEnd: ( Long ) -> Unit,
) {
    when ( lyricsUiState ) {
        LyricsUiState.Loading -> {}
        is LyricsUiState.Success -> {
            Box {
                LyricsLayout(
                    modifier = modifier.fillMaxSize(),
                    lyrics = lyricsUiState.lyrics,
                    currentDurationInPlayback = Duration.ofMillis(
                        onGetPlaybackPosition().played
                    ),
                    onSeekTo = { onSeekEnd( it.toMillis() ) }
                )

            }
        }
    }
}




@DevicePreviews
@Composable
private fun LyricsLayoutPreview() {
    MusicMattersTheme(
        themeMode = DefaultPreferences.THEME_MODE,
        primaryColorName = DefaultPreferences.PRIMARY_COLOR_NAME,
        useMaterialYou = true,
        fontScale = DefaultPreferences.FONT_SCALE,
        fontName = SupportedFonts.ProductSans.name,
    ) {
        LyricsLayout(
            modifier = Modifier.padding( 24.dp ),
            lyrics = listOf(
                Lyric(
                    timeStamp = Duration.ofMinutes( 1 ),
                    content = "Sometime say the magic you dey feel inside is like gold"
                ),
                Lyric(
                    timeStamp = Duration.ofMinutes( 2 ),
                    content = "Something like do re mi fa so lat ti do do (Yeah)"
                ),
                Lyric(
                    timeStamp = Duration.ofMinutes( 3 ),
                    content = "Make I sing for you la la do do"
                ),
                Lyric(
                    timeStamp = Duration.ofMinutes( 4 ),
                    content = "Make I sing your song"
                ),
                Lyric(
                    timeStamp = Duration.ofMinutes( 5 ),
                    content = "Make I sing make you wine am do do o"
                )
            ),
            currentDurationInPlayback = Duration.ofMinutes( 2L ),
            onSeekTo = {}
        )
    }
}

@DevicePreviews
@Composable
private fun LyricsLayoutEmptyPreview() {
    MusicMattersTheme(
        themeMode = DefaultPreferences.THEME_MODE,
        primaryColorName = DefaultPreferences.PRIMARY_COLOR_NAME,
        useMaterialYou = true,
        fontScale = DefaultPreferences.FONT_SCALE
    ) {
        LyricsLayout(
            modifier = Modifier.padding( 24.dp ),
            lyrics = emptyList(),
            currentDurationInPlayback = Duration.ofMinutes( 2L ),
            onSeekTo = {}
        )
    }
}