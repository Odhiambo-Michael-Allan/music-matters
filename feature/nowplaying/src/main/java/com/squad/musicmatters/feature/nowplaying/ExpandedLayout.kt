package com.squad.musicmatters.feature.nowplaying

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Devices.FOLDABLE
import androidx.compose.ui.tooling.preview.Devices.TABLET
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
import com.squad.musicmatters.core.model.Song
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

@Composable
internal fun ExpandedLayout(
    modifier: Modifier = Modifier,
    model: PortraitExpandedLayoutModel,
) {

    Card(
        modifier = modifier.fillMaxSize()
    ) {
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            modifier = modifier
                .fillMaxSize()
        ) {
            Column (
                verticalArrangement = Arrangement.SpaceEvenly,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth( 0.5f )
                    .fillMaxHeight()
            ) {
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
                Column(
                    verticalArrangement = Arrangement.SpaceEvenly,
                    modifier = Modifier
                        .fillMaxHeight()
                        .width( 400.dp )
                ) {
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
                    NowPlayingPlayerControls(
                        isPlaying = model.uiState.playerState.isPlaying,
                        shuffle = model.uiState.userData.shuffle,
                        loopMode = model.uiState.userData.loopMode,
                        onPreviousButtonClick = model.onPreviousButtonClick,
                        onPausePlayButtonClick = model.onPausePlayButtonClick,
                        onNextButtonClick = model.onPlayNext,
                        onToggleLoopMode = model.onToggleLoopMode,
                        onToggleShuffleMode  = { shuffle -> model.onToggleShuffleMode( shuffle ) },
                    )
                    NowPlayingScreenBottomBar(
                        showLyrics = model.uiState.userData.showLyrics,
                        showLyricsOnSeparateScreen = model.uiState.userData.showLyricsOnSeparateScreen,
                        onShowLyrics = model.onShowLyrics,
                        onNavigateToQueueScreen = model.onNavigateToQueueScreen,
                        onNavigateToLyricsScreen = model.onNavigateToLyricsScreen,
                    )
                }
            }
            AnimatedVisibility(
                visible = model.uiState.userData.showLyrics
            ) {
                LyricsLayout(
                    lyricsUiState = model.lyricsUiState,
                    onGetPlaybackPosition = model.onGetPlaybackPosition,
                    onSeekEnd = model.onSeekEnd,
                    modifier = Modifier.padding( 48.dp )
                )
            }
        }
    }
}

internal data class PortraitExpandedLayoutModel(
    val uiState: NowPlayingScreenUiState.Success,
    val lyricsUiState: LyricsUiState,
    val currentlyPlayingSong: Song,
    val onGetPlaybackPosition: () -> PlaybackPosition,
    val onFavorite: ( Song, Boolean ) -> Unit,
    val onArtworkSwipedLeft: () -> Unit,
    val onArtworkSwipedRight: () -> Unit,
    val onArtworkSwipedDown: () -> Unit,
    val onArtworkClicked: ( Song ) -> Unit,
    val onArtistClicked: ( Long ) -> Unit,
    val onShowOptionsMenu: () -> Unit,
    val onSeekStart: () -> Unit,
    val onSeekEnd: ( Long ) -> Unit,
    val onPausePlayButtonClick: () -> Unit,
    val onPreviousButtonClick: () -> Unit,
    val onPlayNext: () -> Unit,
    val onNavigateToQueueScreen: () -> Unit,
    val onNavigateToLyricsScreen: () -> Unit,
    val onShowLyrics: ( Boolean ) -> Unit,
    val onToggleLoopMode: ( LoopMode ) -> Unit,
    val onToggleShuffleMode: ( Boolean ) -> Unit,
)

@Preview( name = "Unfolded Foldable", device = FOLDABLE, showSystemUi = true )
@Preview( name = "Tablet - Landscape", device = TABLET, showSystemUi = true )
//@Preview( name = "Desktop", device = "spec:width=1920dp,height=1080dp,dpi=160", showSystemUi = true )
@Composable
private fun ExpandedLayoutPreview(
    @PreviewParameter(MusicMattersPreviewParametersProvider::class )
    previewData: PreviewData
) {
    MusicMattersTheme(
        themeMode = ThemeMode.LIGHT,
        primaryColorName = "Blue",
        fontName = DefaultPreferences.FONT_NAME,
        fontScale = DefaultPreferences.FONT_SCALE,
        useMaterialYou = true
    ) {
        ExpandedLayout(
            model = PortraitExpandedLayoutModel(
                uiState = NowPlayingScreenUiState.Success(
                    userData = emptyUserData.copy(
                        loopMode = LoopMode.Queue,
                        shuffle = true,
                        showLyrics = false,
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
                onToggleShuffleMode = {},
                onToggleLoopMode = {},
                onNavigateToLyricsScreen = {},
            )
        )
    }
}