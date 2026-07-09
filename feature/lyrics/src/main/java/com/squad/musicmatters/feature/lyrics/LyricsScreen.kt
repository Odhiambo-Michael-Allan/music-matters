package com.squad.musicmatters.feature.lyrics

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.squad.musicmatters.core.datastore.DefaultPreferences
import com.squad.musicmatters.core.designsystem.component.DevicePreviews
import com.squad.musicmatters.core.designsystem.theme.MusicMattersTheme
import com.squad.musicmatters.core.designsystem.theme.SupportedFonts
import com.squad.musicmatters.core.model.Lyric
import com.squad.musicmatters.core.ui.LibraryDestinationContainer
import com.squad.musicmatters.core.ui.LyricsLayout
import com.squad.musicmatters.core.ui.MinimalAppBar
import java.time.Duration

@Composable
internal fun LyricsScreen(
    viewModel: LyricsScreenViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit,
) {

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val playbackPosition by viewModel.playbackPosition.collectAsStateWithLifecycle()

    LyricsScreenContent(
        uiState = uiState,
        onGetPlayedDuration = {
            Duration.ofMillis(
                playbackPosition.played
            )
        },
        onSeekEnd = viewModel::onSeekTo,
        onNavigateBack = onNavigateBack,
    )
}

@Composable
private fun LyricsScreenContent(
    uiState: LyricsScreenUiState,
    onGetPlayedDuration: () -> Duration,
    onSeekEnd: ( Long ) -> Unit,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
) {

    LibraryDestinationContainer(
        isLoading = uiState is LyricsScreenUiState.Loading,
        onNavigateBack = onNavigateBack,
        options = {},
    ) {
        when ( uiState ) {
            LyricsScreenUiState.Loading -> {}
            is LyricsScreenUiState.Success -> {
                LyricsLayout(
                    lyrics = uiState.lyrics,
                    currentDurationInPlayback = onGetPlayedDuration(),
                    blurColor = MaterialTheme.colorScheme.background,
                    onSeekTo = { onSeekEnd( it.toMillis() ) },
                    modifier = modifier
                        .fillMaxSize()
                        .padding( 16.dp )
                )
            }
        }
    }

}

@DevicePreviews
@Composable
private fun LyricsScreenContentPreview() {
    MusicMattersTheme(
        themeMode = DefaultPreferences.THEME_MODE,
        primaryColorName = DefaultPreferences.PRIMARY_COLOR_NAME,
        useMaterialYou = true,
        fontScale = DefaultPreferences.FONT_SCALE,
        fontName = SupportedFonts.ProductSans.name
    ) {
        LyricsScreenContent(
            uiState = LyricsScreenUiState.Success(
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
            ),
            onGetPlayedDuration = { Duration.ofMinutes( 3L ) },
            onSeekEnd = {},
            onNavigateBack = {},
        )
    }
}