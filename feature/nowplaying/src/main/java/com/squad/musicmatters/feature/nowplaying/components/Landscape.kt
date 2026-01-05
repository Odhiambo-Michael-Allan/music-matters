package com.squad.musicmatters.feature.nowplaying.components

import android.content.Context
import android.content.Intent
import androidx.activity.result.contract.ActivityResultContract
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.width
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import com.squad.musicmatters.core.datastore.DefaultPreferences
import com.squad.musicmatters.core.designsystem.component.MusicMattersIcons
import com.squad.musicmatters.core.designsystem.theme.MusicMattersTheme
import com.squad.musicmatters.core.media.connection.PlaybackPosition
import com.squad.musicmatters.core.media.connection.PlayerState
import com.squad.musicmatters.core.media.connection.SleepTimer
import com.squad.musicmatters.core.model.LoopMode
import com.squad.musicmatters.core.model.Lyric
import com.squad.musicmatters.core.model.Song
import com.squad.musicmatters.core.model.SongAdditionalMetadata
import com.squad.musicmatters.core.model.ThemeMode
import com.squad.musicmatters.core.ui.FadeTransition
import com.squad.musicmatters.core.ui.LyricsLayout
import com.squad.musicmatters.feature.nowplaying.LyricsUiState
import com.squad.musicmatters.feature.nowplaying.NowPlayingScreenUiState
import java.time.Duration
import java.util.Timer
import kotlin.time.DurationUnit
import kotlin.time.toDuration

@OptIn(  ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class )
@Composable
internal fun LandscapeLayout(
    modifier: Modifier = Modifier,
    uiState: NowPlayingScreenUiState.Success,
    lyricsUiState: LyricsUiState,
    currentlyPlayingSong: Song,
    playbackPosition: PlaybackPosition,
    durationFormatter: ( Long ) -> String,
    onFavorite: ( Song, Boolean ) -> Unit,
    onArtworkSwipedLeft: () -> Unit,
    onArtworkSwipedRight: () -> Unit,
    onArtworkClicked: ( Song ) -> Unit,
    onArtistClicked: ( String ) -> Unit,
    onArtworkSwipedDown: () -> Unit,
    onShowOptionsMenu: () -> Unit,
    onSeekStart: () -> Unit,
    onSeekEnd: ( Long ) -> Unit,
    onPausePlayButtonClick: () -> Unit,
    onPreviousButtonClick: () -> Unit,
    onPlayNext: () -> Unit,
    onNavigateToQueue: () -> Unit,
    onToggleLoopMode: ( LoopMode ) -> Unit,
    onToggleShuffleMode: ( Boolean ) -> Unit,
    onPlayingSpeedChange: ( Float ) -> Unit,
    onPlayingPitchChange: ( Float ) -> Unit,
    onShowLyrics: (Boolean ) -> Unit,
    onCreateEqualizerActivityContract: () -> Unit,
    onShowSleepTimerBottomSheet: () -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Row(
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding( top = 8.dp )
        ) {
            BottomSheetDefaults.DragHandle()
        }
        Row (
            modifier = modifier
                .fillMaxSize()
                .padding( start = 20.dp, end = 20.dp, bottom = 20.dp, top = 0.dp ),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Box(
                modifier = Modifier
                    .sizeIn( maxWidth = 400.dp, maxHeight = 400.dp )
                    .aspectRatio( 1f )
                    .clip( MaterialTheme.shapes.medium )
            ) {
                AnimatedContent(
                    targetState = uiState.userData.showLyrics
                ) { showLyrics ->
                    if ( showLyrics ) {
                        when ( lyricsUiState ) {
                            LyricsUiState.Loading -> {}
                            is LyricsUiState.Success -> {
                                LyricsLayout(
                                    modifier = Modifier.fillMaxSize(),
                                    lyrics = lyricsUiState.lyrics,
                                    currentDurationInPlayback = Duration.ofMillis( playbackPosition.played ),
                                    onSeekTo = { onSeekEnd( it.toMillis() ) }
                                )
                            }
                        }
                    } else {
                        NowPlayingArtwork(
                            modifier = Modifier
                                .fillMaxWidth(),
                            artworkUri = currentlyPlayingSong.artworkUri?.toUri(),
                            onSwipeLeft = onArtworkSwipedLeft,
                            onSwipeRight = onArtworkSwipedRight,
                            onSwipeDown = onArtworkSwipedDown,
                            onArtworkClicked = { onArtworkClicked( currentlyPlayingSong ) }
                        )
                    }
                }
            }
            Spacer( modifier = Modifier.width( 16.dp ) )
            Column {

                Row {
                    AnimatedContent(
                        modifier = Modifier.weight( 1f ),
                        label = "now-playing-body-content",
                        targetState = currentlyPlayingSong,
                        transitionSpec = {
                            FadeTransition.enterTransition()
                                .togetherWith( FadeTransition.exitTransition() )
                        }
                    ) { target ->
                        Column (
                            modifier = Modifier.padding( 0.dp, 16.dp )
                        ) {
                            Text(
                                text = target.title,
                                style = MaterialTheme.typography.titleLarge
                                    .copy( fontWeight = FontWeight.Bold ),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            ArtistsRow(
                                artists = target.artists,
                                onArtistClicked = onArtistClicked
                            )
                        }
                    }
                    Row (
                        modifier = Modifier.padding( 0.dp, 16.dp )
                    ) {
                        IconButton(
                            modifier = Modifier.offset( 4.dp ),
                            onClick = {
                                onFavorite(
                                    currentlyPlayingSong,
                                    !uiState.currentlyPlayingSongIsFavorite
                                )
                            }
                        ) {
                            AnimatedContent(
                                targetState = uiState.currentlyPlayingSongIsFavorite,
                                label = "now-playing-screen-is-favorite-icon"
                            ) {
                                Icon(
                                    imageVector = if ( it ) {
                                        MusicMattersIcons.Favorite
                                    } else {
                                        MusicMattersIcons.FavoriteBorder
                                    },
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                        IconButton(
                            onClick = onShowOptionsMenu
                        ) {
                            Icon(
                                imageVector = MusicMattersIcons.MoreVertical,
                                contentDescription = null
                            )
                        }
                    }
                }
                Spacer( modifier = Modifier.height( 24.dp ) )
                MusicMattersSeekBar(
                    playbackPosition = playbackPosition,
                    durationFormatter = durationFormatter,
                    onSeekStart = onSeekStart,
                    onSeekEnd = onSeekEnd
                )
                Spacer( modifier = Modifier.height( 24.dp ) )
                when {
                    uiState.userData.controlsLayoutDefault ->
                        NowPlayingDefaultControlsLayout(
                            isPlaying = uiState.playerState.isPlaying,
                            sleepTimer = uiState.sleepTimer,
                            onPausePlayButtonClick = onPausePlayButtonClick,
                            onPreviousButtonClick = onPreviousButtonClick,
                            onNextButtonClick = onPlayNext,
                            onNavigateToQueue = onNavigateToQueue,
                            onShowSleepTimerBottomSheet = onShowSleepTimerBottomSheet,
                        )
                    else ->
                        NowPlayingTraditionalControlsLayout(
                            isPlaying = uiState.playerState.isPlaying,
                            sleepTimer = uiState.sleepTimer,
                            onPreviousButtonClick = onPreviousButtonClick,
                            onPausePlayButtonClick = onPausePlayButtonClick,
                            onNextButtonClick = onPlayNext,
                            onNavigateToQueue = onNavigateToQueue,
                            onShowSleepTimerBottomSheet = onShowSleepTimerBottomSheet,
                        )
                }
                Spacer( modifier = Modifier.height( 16.dp ) )
                NowPlayingScreenBottomBar(
                    currentLoopMode = uiState.userData.loopMode,
                    shuffle = uiState.userData.shuffle,
                    showLyrics = uiState.userData.showLyrics,
                    currentSpeed = uiState.userData.playbackSpeed,
                    currentPitch = uiState.userData.playbackPitch,
                    onToggleLoopMode = onToggleLoopMode,
                    onToggleShuffleMode = onToggleShuffleMode,
                    onSpeedChange = onPlayingSpeedChange,
                    onPitchChange = onPlayingPitchChange,
                    onShowLyrics = onShowLyrics,
                    onCreateEqualizerActivityContract = onCreateEqualizerActivityContract
                )
                Spacer( modifier = Modifier.size( 32.dp ) )
            }
        }
    }
}

@Preview( name = "landscape", device = "spec:width=640dp,height=360dp,dpi=480", showBackground = true )
@Composable
private fun LandscapePreview() {
    MusicMattersTheme(
        themeMode = ThemeMode.LIGHT,
        primaryColorName = "Blue",
        fontName = DefaultPreferences.FONT_NAME,
        fontScale = DefaultPreferences.FONT_SCALE,
        useMaterialYou = true
    ) {
        LandscapeLayout(
            uiState = NowPlayingScreenUiState.Success(
                userData = emptyUserData.copy(
                    miniPlayerShowTrackControls = false,
                    controlsLayoutDefault = false,
                    loopMode = LoopMode.Queue,
                    shuffle = true,
                ),
                queue = listOf(
                    Song(
                        id = "song-id-1",
                        mediaUri = "Uri.EMPTY",
                        title = "Started From the Bottom",
                        displayTitle = "",
                        duration = 0L,
                        artists = setOf(
                            "Drake",
                            "Disclosure",
                            "London",
                            "Grammar",
                            "The Weekend",
                            "Young thug"
                        ),
                        size = 0L,
                        dateModified = 0L,
                        path = "",
                        trackNumber = null,
                        year = null,
                        albumTitle = null,
                        composer = null,
                        artworkUri = null,
                    )
                ),
                currentlyPlayingSongIsFavorite = true,
                playerState = PlayerState(
                    currentlyPlayingSongId = "song-id-1",
                    isPlaying = false,
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
                    endsAt = System.currentTimeMillis().toDuration(DurationUnit.MILLISECONDS),
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
            playbackPosition = PlaybackPosition(2L, 3L, 5L),
            durationFormatter = { "05:33" },
            onArtistClicked = {},
            onFavorite = { _, _ -> },
            onPausePlayButtonClick = {},
            onPreviousButtonClick = {},
            onPlayNext = {},
            onSeekEnd = {},
            onArtworkClicked = {},
            onPlayingSpeedChange = {},
            onPlayingPitchChange = {},
            onNavigateToQueue = {},
            onToggleLoopMode = {},
            onToggleShuffleMode = {},
            onSeekStart = {},
            onCreateEqualizerActivityContract = {
                object : ActivityResultContract<Unit, Unit>() {
                    override fun createIntent(context: Context, input: Unit) = Intent()
                    override fun parseResult(resultCode: Int, intent: Intent?) {}

                }
            },
            currentlyPlayingSong = Song(
                id = "song-id-1",
                mediaUri = "Uri.EMPTY",
                title = "Started From the Bottom Now we Here",
                displayTitle = "",
                duration = 0L,
                artists = setOf(
                    "Drake",
                    "Disclosure",
                    "London",
                    "Grammar",
                    "The Weekend",
                    "Young thug"
                ),
                size = 0L,
                dateModified = 0L,
                path = "",
                trackNumber = null,
                year = null,
                albumTitle = null,
                composer = null,
                artworkUri = null,
            ),
            onArtworkSwipedLeft = { TODO() },
            onArtworkSwipedRight = { TODO() },
            onArtworkSwipedDown = { TODO() },
            onShowOptionsMenu = { TODO() },
            onShowSleepTimerBottomSheet = { TODO() },
            onShowLyrics = {},
        )
    }
}
