package com.squad.musicmatters.feature.nowplaying

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.tooling.preview.Devices.PHONE
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import com.squad.musicmatters.core.datastore.DefaultPreferences
import com.squad.musicmatters.core.designsystem.theme.MusicMattersTheme
import com.squad.musicmatters.core.media.connection.PlaybackPosition
import com.squad.musicmatters.core.media.connection.PlayerState
import com.squad.musicmatters.core.media.connection.SleepTimer
import com.squad.musicmatters.core.model.LoopMode
import com.squad.musicmatters.core.model.Lyric
import com.squad.musicmatters.core.model.SongMetadata
import com.squad.musicmatters.core.model.ThemeMode
import com.squad.musicmatters.core.ui.MusicMattersPreviewParametersProvider
import com.squad.musicmatters.core.ui.PreviewData
import com.squad.musicmatters.feature.nowplaying.components.LyricsLayout
import com.squad.musicmatters.feature.nowplaying.components.NowPlayingSongArtwork
import com.squad.musicmatters.feature.nowplaying.components.NowPlayingScreenBottomBar
import com.squad.musicmatters.feature.nowplaying.components.NowPlayingSeekBar
import com.squad.musicmatters.feature.nowplaying.components.NowPlayingPlayerControls
import com.squad.musicmatters.feature.nowplaying.components.TitleAndArtistSection
import com.squad.musicmatters.feature.nowplaying.components.emptyUserData
import java.time.Duration
import java.util.Timer
import kotlin.time.DurationUnit
import kotlin.time.toDuration

@OptIn( ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
internal fun PortraitLayout(
    modifier: Modifier = Modifier,
    model: PortraitExpandedLayoutModel,
    nestedScrollConnection: NestedScrollConnection,
) {
    Card(
        modifier = Modifier.fillMaxSize()
    ) {
        Column (
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = modifier
                .fillMaxSize()
                .padding( 28.dp )
                .nestedScroll( nestedScrollConnection ),
        ) {
            Box(
                modifier = Modifier
                    .sizeIn( maxWidth = 400.dp, maxHeight = 400.dp )
                    .aspectRatio( 1f )
                    .clip( MaterialTheme.shapes.medium )
            ) {
                AnimatedContent(
                    targetState = model.uiState.userData.showLyrics
                ) { showLyrics ->
                    if ( showLyrics ) {
                        LyricsLayout(
                            lyricsUiState = model.lyricsUiState,
                            onGetPlaybackPosition = model.onGetPlaybackPosition,
                            onSeekEnd = model.onSeekEnd,
                        )
                    } else {
                        NowPlayingSongArtwork(
                            modifier = Modifier
                                .fillMaxWidth(),
                            song = model.currentlyPlayingSong,
                            isPlaying = { model.uiState.playerState.isPlaying
                                    || model.uiState.playerState.isBuffering
                            },
                            onSwipeLeft = model.onArtworkSwipedLeft,
                            onSwipeRight = model.onArtworkSwipedRight,
                            onSwipeDown = model.onArtworkSwipedDown,
                            onArtworkClicked = {
                                model.onArtworkClicked( model.currentlyPlayingSong )
                            }
                        )
                    }
                }
            }
            TitleAndArtistSection(
                currentlyPlayingSong = model.currentlyPlayingSong,
                currentlyPlayingSongIsFavorite = model.uiState.currentlyPlayingSongIsFavorite,
                onArtistClicked = model.onArtistClicked,
                onFavorite = model.onFavorite,
                onShowOptionsMenu = model.onShowOptionsMenu,
            )
            NowPlayingSeekBar(
                onGetPlaybackPosition = model.onGetPlaybackPosition,
                onSeekStart = model.onSeekStart,
                onSeekEnd = model.onSeekEnd
            )
            Spacer( modifier = Modifier.height( 28.dp ) )
            NowPlayingPlayerControls(
                isPlaying = model.uiState.playerState.isPlaying,
                shuffle = model.uiState.userData.shuffle,
                loopMode = model.uiState.userData.loopMode,
                onPreviousButtonClick = model.onPreviousButtonClick,
                onPausePlayButtonClick = model.onPausePlayButtonClick,
                onNextButtonClick = model.onPlayNext,
                onToggleLoopMode = model.onToggleLoopMode,
                onToggleShuffleMode = { shuffle -> model.onToggleShuffleMode( shuffle ) },
            )
            Spacer( modifier = Modifier.height( 16.dp ) )
            NowPlayingScreenBottomBar(
                showLyrics = model.uiState.userData.showLyrics,
                showLyricsOnSeparateScreen = model.uiState.userData.showLyricsOnSeparateScreen,
                onShowLyrics = model.onShowLyrics,
                onNavigateToQueueScreen = model.onNavigateToQueueScreen,
                onNavigateToLyricsScreen = model.onNavigateToLyricsScreen,
            )
        }
    }
}

@Preview(
    name = "Tablet",
    device = "spec:width=1280dp,height=800dp,dpi=240,orientation=portrait",
    showSystemUi = true,
)
@Preview( name = "Phone", device = PHONE, showSystemUi = true )
@Composable
private fun PortraitPreview(
    @PreviewParameter( MusicMattersPreviewParametersProvider::class )
    previewData: PreviewData
) {
    MusicMattersTheme(
        themeMode = ThemeMode.LIGHT,
        primaryColorName = "Blue",
        fontName = DefaultPreferences.FONT_NAME,
        fontScale = DefaultPreferences.FONT_SCALE,
        useMaterialYou = true
    ) {
        PortraitLayout(
            model = PortraitExpandedLayoutModel(
                uiState = NowPlayingScreenUiState.Success(
                    userData = emptyUserData.copy(
                        loopMode = LoopMode.Queue,
                        shuffle = true,
                        showLyrics = true,
                    ),
                    currentlyPlayingSong = previewData.songs.first(),
                    currentlyPlayingSongIsFavorite = true,
                    playerState = PlayerState(
                        currentlyPlayingSongId = "song-id-1",
                        isPlaying = true,
                        isBuffering = false,
                    ),
                    playlists = emptyList(),
                    songMetadata = SongMetadata(
                        songId = "",
                        codec = "mp3",
                        bitrate = 0,
                        samplingRate = 0f,
                        bitsPerSample = 0,
                        genreName = "Hip-Hop"
                    ),
                    sleepTimer = SleepTimer(
                        duration = 300000L.toDuration(DurationUnit.MILLISECONDS),
                        endsAt = System.currentTimeMillis().toDuration(
                            DurationUnit.MILLISECONDS
                        ),
                        timer = Timer()
                    )
                ),
                lyricsUiState = LyricsUiState.Success(
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
                onGetPlaybackPosition = { PlaybackPosition( 2L, 3L, 5L ) },
                onArtistClicked = {},
                onFavorite = { _, _ -> },
                onPausePlayButtonClick = {},
                onPreviousButtonClick = {},
                onPlayNext = {},
                onSeekEnd = {},
                onArtworkClicked = {},
                onNavigateToQueueScreen = {},
                onSeekStart = {},
                currentlyPlayingSong = previewData.songs.first(),
                onArtworkSwipedLeft = { TODO() },
                onArtworkSwipedRight = { TODO() },
                onArtworkSwipedDown = { TODO() },
                onShowOptionsMenu = { TODO() },
                onShowLyrics = {},
                onToggleLoopMode = {},
                onToggleShuffleMode = {},
                onNavigateToLyricsScreen = {},
            ),
            nestedScrollConnection = object : NestedScrollConnection {}
        )
    }
}