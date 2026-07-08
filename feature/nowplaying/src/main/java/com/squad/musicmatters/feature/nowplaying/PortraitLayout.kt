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
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Devices.PHONE
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

@OptIn( ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
internal fun PortraitLayout(
    modifier: Modifier = Modifier,
    uiState: NowPlayingScreenUiState.Success,
    lyricsUiState: LyricsUiState,
    currentlyPlayingSong: Song,
    backgroundColor: Color = Color.Unspecified,
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
        colors = CardDefaults.cardColors(
            containerColor = backgroundColor
        ),
        modifier = Modifier.fillMaxSize()
    ) {
        Column (
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = modifier
                .fillMaxSize()
                .padding(28.dp),
        ) {
            Box(
                modifier = Modifier
                    .sizeIn(maxWidth = 400.dp, maxHeight = 400.dp)
                    .aspectRatio(1f)
                    .clip(MaterialTheme.shapes.medium)
            ) {
                AnimatedContent(
                    targetState = uiState.userData.showLyrics
                ) { showLyrics ->
                    if ( showLyrics ) {
                        LyricsLayout(
                            lyricsUiState = lyricsUiState,
                            onGetPlaybackPosition = onGetPlaybackPosition,
                            onSeekEnd = onSeekEnd,
                        )
                    } else {
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
                    }
                }
            }
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
            Spacer( modifier = Modifier.height( 28.dp ) )
            NowPlayingPlayerControls(
                isPlaying = uiState.playerState.isPlaying,
                shuffle = uiState.userData.shuffle,
                loopMode = uiState.userData.loopMode,
                onPreviousButtonClick = onPreviousButtonClick,
                onPausePlayButtonClick = onPausePlayButtonClick,
                onNextButtonClick = onPlayNext,
                onToggleLoopMode = onToggleLoopMode,
                onToggleShuffleMode = { shuffle -> onToggleShuffleMode( shuffle ) },
            )
            Spacer( modifier = Modifier.height( 16.dp ) )
            NowPlayingScreenBottomBar(
                showLyrics = uiState.userData.showLyrics,
                showLyricsOnSeparateScreen = uiState.userData.showLyricsOnSeparateScreen,
                onShowLyrics = onShowLyrics,
                onNavigateToQueueScreen = onNavigateToQueueScreen,
                onNavigateToLyricsScreen = onNavigateToLyricsScreen,
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
private fun PortraitPreview() {
    MusicMattersTheme(
        themeMode = ThemeMode.LIGHT,
        primaryColorName = "Blue",
        fontName = DefaultPreferences.FONT_NAME,
        fontScale = DefaultPreferences.FONT_SCALE,
        useMaterialYou = true
    ) {
        PortraitLayout(
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
            onToggleLoopMode = {},
            onToggleShuffleMode = {},
            onNavigateToLyricsScreen = {},
        )
    }
}