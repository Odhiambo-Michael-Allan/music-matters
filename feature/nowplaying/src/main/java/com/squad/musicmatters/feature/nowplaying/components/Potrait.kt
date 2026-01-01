package com.squad.musicmatters.feature.nowplaying.components

import android.content.Context
import android.content.Intent
import androidx.activity.result.contract.ActivityResultContract
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DragHandle
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.outlined.ThumbUpAlt
import androidx.compose.material.icons.rounded.ThumbUpAlt
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import com.squad.musicmatters.core.datastore.DefaultPreferences
import com.squad.musicmatters.core.designsystem.component.DevicePreviews
import com.squad.musicmatters.core.designsystem.theme.MusicMattersTheme
import com.squad.musicmatters.core.media.connection.PlaybackPosition
import com.squad.musicmatters.core.media.connection.PlayerState
import com.squad.musicmatters.core.media.connection.SleepTimer
import com.squad.musicmatters.core.model.LoopMode
import com.squad.musicmatters.core.model.Song
import com.squad.musicmatters.core.model.SongAdditionalMetadata
import com.squad.musicmatters.core.model.ThemeMode
import com.squad.musicmatters.core.ui.FadeTransition
import com.squad.musicmatters.feature.nowplaying.NowPlayingScreenUiState
import java.util.Timer
import kotlin.time.DurationUnit
import kotlin.time.toDuration

@OptIn( ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
internal fun PortraitLayout(
    modifier: Modifier = Modifier,
    uiState: NowPlayingScreenUiState.Success,
    currentlyPlayingSong: Song,
    playbackPosition: PlaybackPosition,
    durationFormatter: ( Long ) -> String,
    onFavorite: ( Song, Boolean ) -> Unit,
    onArtworkSwipedLeft: () -> Unit,
    onArtworkSwipedRight: () -> Unit,
    onArtworkSwipedDown: () -> Unit,
    onArtworkClicked: ( Song ) -> Unit,
    onArtistClicked: ( String ) -> Unit,
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
    onCreateEqualizerActivityContract: () -> Unit,
    onShowSleepTimerBottomSheet: () -> Unit,
) {
    Column (
        verticalArrangement = Arrangement.SpaceBetween,
        modifier = modifier
            .fillMaxSize()
            .padding( 20.dp ),
    ) {
        Row(
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            BottomSheetDefaults.DragHandle()
        }
        NowPlayingArtwork(
            modifier = Modifier
                .fillMaxWidth(),
            artworkUri = currentlyPlayingSong.artworkUri?.toUri(),
            onSwipeLeft = onArtworkSwipedLeft,
            onSwipeRight = onArtworkSwipedRight,
            onSwipeDown = onArtworkSwipedDown,
            onArtworkClicked = { onArtworkClicked( currentlyPlayingSong ) }
        )
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
                    modifier = Modifier
                        .padding( 0.dp, 16.dp )

                ) {
                    Text(
                        text = target.title,
                        style = MaterialTheme.typography.titleLarge
                            .copy( fontWeight = FontWeight.Bold ),
                        maxLines = 1,
                        modifier = Modifier.basicMarquee( iterations = Int.MAX_VALUE )
                    )
                    ArtistsRow(
                        artists = target.artists,
                        onArtistClicked = onArtistClicked
                    )
                    if ( uiState.userData.showNowPlayingAudioInformation ) {
                        uiState.songAdditionalMetadata?.let {
                            Text(
                                text = it.toSamplingInfoString( uiState.language ),
                                style = MaterialTheme.typography.labelSmall
                                    .copy( color = LocalContentColor.current.copy( alpha = 0.7f ) ),
                                maxLines = 3,
                            )
                        }
                    }
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
                                Icons.Rounded.ThumbUpAlt
                            } else {
                                Icons.Outlined.ThumbUpAlt
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
                        imageVector = Icons.Filled.MoreVert,
                        contentDescription = null
                    )
                }
            }
        }
        MusicMattersSeekBar(
            playbackPosition = playbackPosition,
            durationFormatter = durationFormatter,
            onSeekStart = onSeekStart,
            onSeekEnd = onSeekEnd
        )
        Spacer( modifier = Modifier.height( 28.dp ) )
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
        NowPlayingBodyBottomBar(
            language = uiState.language,
            currentLoopMode = uiState.userData.loopMode,
            shuffle = uiState.userData.shuffle,
            currentSpeed = uiState.userData.playbackSpeed,
            currentPitch = uiState.userData.playbackPitch,
            onToggleLoopMode = onToggleLoopMode,
            onToggleShuffleMode = onToggleShuffleMode,
            onSpeedChange = onPlayingSpeedChange,
            onPitchChange = onPlayingPitchChange,
            onCreateEqualizerActivityContract = onCreateEqualizerActivityContract
        )
    }
}

@PreviewScreenSizes
@Composable
private fun NowPlayingScreenContentPreview() {
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
                    miniPlayerShowTrackControls = false,
                    controlsLayoutDefault = false,
                    showNowPlayingSeekControls = true,
                    loopMode = LoopMode.Queue,
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
            ),
            onArtworkSwipedLeft = { TODO() },
            onArtworkSwipedRight = { TODO() },
            onArtworkSwipedDown = { TODO() },
            onShowOptionsMenu = { TODO() },
            onShowSleepTimerBottomSheet = { TODO() },
        )
    }
}