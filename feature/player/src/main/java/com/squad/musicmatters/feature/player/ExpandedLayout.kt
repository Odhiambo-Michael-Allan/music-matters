package com.squad.musicmatters.feature.player

import android.content.Context
import android.content.Intent
import androidx.activity.result.contract.ActivityResultContract
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.nestedScroll
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
import com.squad.musicmatters.core.model.SongMetadata
import com.squad.musicmatters.core.model.ThemeMode
import com.squad.musicmatters.core.ui.MusicMattersPreviewParametersProvider
import com.squad.musicmatters.core.ui.PreviewData
import com.squad.musicmatters.feature.player.components.LyricsLayout
import com.squad.musicmatters.feature.player.components.NowPlayingSongArtwork
import com.squad.musicmatters.feature.player.components.NowPlayingScreenBottomBar
import com.squad.musicmatters.feature.player.components.NowPlayingSeekBar
import com.squad.musicmatters.feature.player.components.NowPlayingPlayerControls
import com.squad.musicmatters.feature.player.components.TitleAndArtistSection
import com.squad.musicmatters.feature.player.components.emptyUserData
import java.time.Duration
import java.util.Timer
import kotlin.time.DurationUnit
import kotlin.time.toDuration

@Composable
internal fun ExpandedLayout(
    modifier: Modifier = Modifier,
    model: NowPlayingScreenModel,
    callbacks: NowPlayingScreenCallbacks,
    nestedScrollConnection: NestedScrollConnection,
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
                .nestedScroll( nestedScrollConnection )
        ) {
            NowPlayingSongArtwork(
                modifier = Modifier
                    .fillMaxWidth(),
                song = model.uiState.currentlyPlayingSong,
                isPlaying = { model.uiState.playerState.isPlaying
                        || model.uiState.playerState.isBuffering
                },
                onSwipeLeft = callbacks.onSwipeArtworkLeft,
                onSwipeRight = callbacks.onSwipeArtworkRight,
                onArtworkClicked = {
                    callbacks.onArtworkClicked( model.uiState.currentlyPlayingSong )
                }
            )
            Column(
                verticalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier
                    .fillMaxHeight()
                    .width( 400.dp )
            ) {
                TitleAndArtistSection(
                    currentlyPlayingSong = model.uiState.currentlyPlayingSong,
                    currentlyPlayingSongIsFavorite = model.uiState.currentlyPlayingSongIsFavorite,
                    colorScheme = model.colorScheme,
                    onArtistClicked = callbacks.onArtistClicked,
                    onFavorite = callbacks.onFavorite,
                    onShowOptionsMenu = callbacks.onShowOptionsMenu,
                )
                NowPlayingSeekBar(
                    colorScheme = model.colorScheme,
                    onGetPlaybackPosition = callbacks.onGetPlaybackPosition,
                    onSeekStart = callbacks.onSeekStart,
                    onSeekEnd = callbacks.onSeekEnd
                )
                NowPlayingPlayerControls(
                    isPlaying = model.uiState.playerState.isPlaying,
                    shuffle = model.uiState.userData.shuffle,
                    loopMode = model.uiState.userData.loopMode,
                    colorScheme = model.colorScheme,
                    onPreviousButtonClick = callbacks.onPreviousButtonClick,
                    onPlayPauseButtonClick = callbacks.onPlayPauseButtonClick,
                    onPlayNext = callbacks.onPlayNext,
                    onToggleLoopMode = callbacks.onToggleLoopMode,
                    onToggleShuffleMode  = { shuffle -> callbacks.onToggleShuffleMode( shuffle ) },
                )
                NowPlayingScreenBottomBar(
                    showLyrics = model.uiState.userData.showLyrics,
                    showLyricsOnSeparateScreen = model.uiState.userData.showLyricsOnSeparateScreen,
                    onShowLyrics = callbacks.onShowLyrics,
                    colorScheme = model.colorScheme,
                    onNavigateToQueueScreen = callbacks.onNavigateToQueueScreen,
                    onNavigateToLyricsScreen = callbacks.onNavigateToLyricsScreen,
                )
            }
        }
        AnimatedVisibility(
            visible = model.uiState.userData.showLyrics
        ) {
            LyricsLayout(
                lyricsUiState = model.lyricsUiState,
                colorScheme = model.colorScheme,
                onGetPlaybackPosition = callbacks.onGetPlaybackPosition,
                onSeekEnd = callbacks.onSeekEnd,
                modifier = Modifier.padding( 48.dp )
            )
        }
    }
}

@Preview( name = "Unfolded Foldable", device = FOLDABLE, showSystemUi = true )
@Preview( name = "Tablet - Landscape", device = TABLET, showSystemUi = true )
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
            model = NowPlayingScreenModel(
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
                colorScheme = MaterialTheme.colorScheme,
            ),
            callbacks = NowPlayingScreenCallbacks(
                onGetPlaybackPosition = { PlaybackPosition( 2L, 3L, 5L ) },
                onArtistClicked = {},
                onFavorite = { _, _ -> },
                onPlayPauseButtonClick = {},
                onPreviousButtonClick = {},
                onPlayNext = {},
                onSeekEnd = {},
                onArtworkClicked = {},
                onSwipeArtworkLeft = {},
                onSwipeArtworkRight = {},
                onNavigateToQueueScreen = {},
                onSeekStart = {},
                onCreateEqualizerActivityContract = {
                    object : ActivityResultContract<Unit, Unit>() {
                        override fun createIntent(context: Context, input: Unit) = Intent()
                        override fun parseResult( resultCode: Int, intent: Intent? ) {}

                    }
                },
                onCreatePlaylist = { _, _ -> },
                onAddSongsToPlaylist = { _, _ -> },
                onViewAlbum = {},
                onViewArtist = {},
                onShowSnackBar = {},
                onStopSleepTimer = {},
                onStartSleepTimer = {},
                onShowLyrics = {},
                onToggleLoopMode = {},
                onToggleShuffleMode = {},
                onRemoveSongFromQueue = {},
                onNavigateToLyricsScreen = {},
            ),
            nestedScrollConnection = object : NestedScrollConnection {}
        )
    }
}