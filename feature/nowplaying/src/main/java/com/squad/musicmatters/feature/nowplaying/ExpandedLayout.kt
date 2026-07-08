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
import androidx.compose.ui.unit.dp
import com.squad.musicmatters.core.datastore.DefaultPreferences
import com.squad.musicmatters.core.designsystem.theme.MusicMattersTheme
import com.squad.musicmatters.core.media.connection.PlaybackPosition
import com.squad.musicmatters.core.media.connection.PlayerState
import com.squad.musicmatters.core.media.connection.SleepTimer
import com.squad.musicmatters.core.model.LoopMode
import com.squad.musicmatters.core.model.Lyric
import com.squad.musicmatters.core.model.Song
import com.squad.musicmatters.core.model.SongAdditionalMetadata
import com.squad.musicmatters.core.model.ThemeMode
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
    uiState: NowPlayingScreenUiState.Success,
    lyricsUiState: LyricsUiState,
    currentlyPlayingSong: Song,
    onGetPlaybackPosition: () -> PlaybackPosition,
    onFavorite: ( Song, Boolean ) -> Unit,
    onArtworkSwipedLeft: () -> Unit,
    onArtworkSwipedRight: () -> Unit,
    onArtworkSwipedDown: () -> Unit,
    onArtworkClicked: ( Song ) -> Unit,
    onArtistClicked: ( Long ) -> Unit,
    onShowOptionsMenu: () -> Unit,
    onSeekStart: () -> Unit,
    onSeekEnd: ( Long ) -> Unit,
    onPausePlayButtonClick: () -> Unit,
    onPreviousButtonClick: () -> Unit,
    onPlayNext: () -> Unit,
    onNavigateToQueueScreen: () -> Unit,
    onNavigateToLyricsScreen: () -> Unit,
    onShowLyrics: ( Boolean ) -> Unit,
    onToggleLoopMode: ( LoopMode ) -> Unit,
    onToggleShuffleMode: ( Boolean ) -> Unit,
) {

    Card(
        modifier = modifier.fillMaxSize()
    ) {
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            modifier = modifier
                .fillMaxSize()
                .padding( start = 48.dp, 0.dp )
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
                    song = currentlyPlayingSong,
                    isPlaying = { uiState.playerState.isPlaying
                            || uiState.playerState.isBuffering
                    },
                    onSwipeLeft = onArtworkSwipedLeft,
                    onSwipeRight = onArtworkSwipedRight,
                    onSwipeDown = onArtworkSwipedDown,
                    onArtworkClicked = { onArtworkClicked( currentlyPlayingSong ) }
                )
                Column(
                    verticalArrangement = Arrangement.SpaceEvenly,
                    modifier = Modifier
                        .fillMaxHeight()
                        .width( 400.dp )
                ) {
                    TitleAndArtistSection(
                        currentlyPlayingSong = currentlyPlayingSong,
                        currentlyPlayingSongIsFavorite = uiState.currentlyPlayingSongIsFavorite,
                        onArtistClicked = onArtistClicked,
                        onFavorite = onFavorite,
                        onShowOptionsMenu = onShowOptionsMenu,
                    )
                    NowPlayingSeekBar(
                        onGetPlaybackPosition = onGetPlaybackPosition,
                        onSeekStart = onSeekStart,
                        onSeekEnd = onSeekEnd
                    )
                    NowPlayingPlayerControls(
                        isPlaying = uiState.playerState.isPlaying,
                        shuffle = uiState.userData.shuffle,
                        loopMode = uiState.userData.loopMode,
                        onPreviousButtonClick = onPreviousButtonClick,
                        onPausePlayButtonClick = onPausePlayButtonClick,
                        onNextButtonClick = onPlayNext,
                        onToggleLoopMode = onToggleLoopMode,
                        onToggleShuffleMode  = { shuffle -> onToggleShuffleMode( shuffle ) },
                    )
                    NowPlayingScreenBottomBar(
                        showLyrics = uiState.userData.showLyrics,
                        showLyricsOnSeparateScreen = uiState.userData.showLyricsOnSeparateScreen,
                        onShowLyrics = onShowLyrics,
                        onNavigateToQueueScreen = onNavigateToQueueScreen,
                        onNavigateToLyricsScreen = onNavigateToLyricsScreen,
                    )
                }
            }
            AnimatedVisibility(
                visible = uiState.userData.showLyrics
            ) {
                LyricsLayout(
                    lyricsUiState = lyricsUiState,
                    onGetPlaybackPosition = onGetPlaybackPosition,
                    onSeekEnd = onSeekEnd,
                    modifier = Modifier.padding( 48.dp )
                )
            }
        }
    }

}

@Preview( name = "Unfolded Foldable", device = FOLDABLE, showSystemUi = true )
@Preview( name = "Tablet - Landscape", device = TABLET, showSystemUi = true )
//@Preview( name = "Desktop", device = "spec:width=1920dp,height=1080dp,dpi=160", showSystemUi = true )
@Composable
private fun ExpandedLayoutPreview() {
    MusicMattersTheme(
        themeMode = ThemeMode.LIGHT,
        primaryColorName = "Blue",
        fontName = DefaultPreferences.FONT_NAME,
        fontScale = DefaultPreferences.FONT_SCALE,
        useMaterialYou = true
    ) {
        ExpandedLayout(
            uiState = NowPlayingScreenUiState.Success(
                userData = emptyUserData.copy(
                    loopMode = LoopMode.Queue,
                    shuffle = true,
                    showLyrics = true,
                ),
                currentlyPlayingSong = Song(
                    id = "song-id-1",
                    mediaUri = "Uri.EMPTY",
                    title = "Started From the Bottom",
                    duration = 0L,
                    artist = "Michael Jackson",
                    size = 0L,
                    albumId = 0L,
                    dateModified = 0L,
                    path = "",
                    trackNumber = null,
                    year = null,
                    albumTitle = null,
                    composer = null,
                    artworkUri = null,
                    artistId = 0,
                ),
                currentlyPlayingSongIsFavorite = true,
                playerState = PlayerState(
                    currentlyPlayingSongId = "song-id-1",
                    isPlaying = true,
                    isBuffering = false,
                ),
                playlists = emptyList(),
                songAdditionalMetadata = SongAdditionalMetadata(
                    songId = "",
                    codec = "mp3",
                    bitrate = 0,
                    samplingRate = 0f,
                    bitsPerSample = 0,
                    genre = "Hip-Hop"
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
            currentlyPlayingSong = Song(
                id = "song-id-1",
                mediaUri = "Uri.EMPTY",
                title = "Started From the Bottom Now we Here",
                albumId = 0L,
                duration = 0L,
                artist = "Michael Jackson",
                size = 0L,
                dateModified = 0L,
                path = "",
                trackNumber = null,
                year = null,
                albumTitle = null,
                composer = null,
                artworkUri = null,
                artistId = 0,
            ),
            onArtworkSwipedLeft = { TODO() },
            onArtworkSwipedRight = { TODO() },
            onArtworkSwipedDown = { TODO() },
            onShowOptionsMenu = { TODO() },
            onShowLyrics = {},
            onToggleShuffleMode = {},
            onToggleLoopMode = {},
            onNavigateToLyricsScreen = {},
        )
    }
}