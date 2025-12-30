package com.squad.musicmatters.feature.nowplaying

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.absoluteOffset
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.FastForward
import androidx.compose.material.icons.rounded.FastRewind
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.SkipNext
import androidx.compose.material.icons.rounded.SkipPrevious
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.boundsInParent
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.squad.musicmatters.core.datastore.DefaultPreferences
import com.squad.musicmatters.core.designsystem.theme.MusicMattersTheme
import com.squad.musicmatters.core.i8n.English
import com.squad.musicmatters.core.media.connection.PlaybackPosition
import com.squad.musicmatters.core.media.connection.PlayerState
import com.squad.musicmatters.core.model.BottomBarLabelVisibility
import com.squad.musicmatters.core.model.LoopMode
import com.squad.musicmatters.core.model.LyricsLayout
import com.squad.musicmatters.core.model.Song
import com.squad.musicmatters.core.model.SortAlbumsBy
import com.squad.musicmatters.core.model.SortArtistsBy
import com.squad.musicmatters.core.model.SortGenresBy
import com.squad.musicmatters.core.model.SortPathsBy
import com.squad.musicmatters.core.model.SortPlaylistsBy
import com.squad.musicmatters.core.model.SortSongsBy
import com.squad.musicmatters.core.model.ThemeMode
import com.squad.musicmatters.core.model.UserData
import com.squad.musicmatters.core.ui.DynamicAsyncImage
import com.squad.musicmatters.core.ui.FadeTransition
import com.squad.musicmatters.core.ui.TransitionDurations
import kotlin.math.absoluteValue

// Stateful
@Composable
fun NowPlayingBottomBar(
    viewModel: NowPlayingScreenViewModel = hiltViewModel(),
    onShowNowPlayingBottomSheet: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val playbackPosition by viewModel.playbackPosition.collectAsStateWithLifecycle()

    NowPlayingBottomBarContent(
        uiState = uiState,
        playbackPosition = playbackPosition,
        onNowPlayingBottomBarSwipeUp = onShowNowPlayingBottomSheet,
        onNowPlayingBottomBarClick = onShowNowPlayingBottomSheet,
        nextSong = viewModel::playNextSong,
        previousSong = viewModel::playPreviousSong,
        seekBack = viewModel::fastRewind,
        seekForward = viewModel::fastForward,
        playPause = viewModel::playPause
    )
}

// Stateless
@Composable
private fun NowPlayingBottomBarContent(
    uiState: NowPlayingScreenUiState,
    playbackPosition: PlaybackPosition,
    onNowPlayingBottomBarSwipeUp: () -> Unit,
    onNowPlayingBottomBarClick: () -> Unit,
    nextSong: () -> Boolean,
    previousSong: () -> Boolean,
    seekBack: () -> Unit,
    seekForward: () -> Unit,
    playPause: () -> Unit,
) {

    when ( uiState ) {
        NowPlayingScreenUiState.Loading -> {}
        is NowPlayingScreenUiState.Success -> {

            val currentlyPlayingSong = uiState.queue.firstOrNull {
                it.id == uiState.playerState.currentlyPlayingSongId
            }

            AnimatedVisibility(
                visible = currentlyPlayingSong != null,
            ) {

                currentlyPlayingSong?.let { playingSong ->
                    Column (
                        modifier = Modifier
                            .background(
                                MaterialTheme.colorScheme.surfaceColorAtElevation( 1.dp )
                            )
                    ) {

                        ElevatedCard (
                            modifier = Modifier
                                .fillMaxWidth()
                                .wrapContentHeight()
                                .swipeable(
                                    onSwipeUp = onNowPlayingBottomBarSwipeUp,
                                ),
                            shape = RectangleShape,
                            onClick = onNowPlayingBottomBarClick
                        ) {
                            Row (
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding( 0.dp, 8.dp )
                            ) {
                                Spacer( modifier = Modifier.width( 12.dp ) )
                                AnimatedContent(
                                    label = "now-playing-card-image",
                                    targetState = playingSong,
                                    transitionSpec = {
                                        val from = fadeIn(
                                            animationSpec = TransitionDurations.Normal.asTween(
                                                delayMillis = 150
                                            )
                                        )
                                        val to = fadeOut(
                                            animationSpec = TransitionDurations.Fast.asTween()
                                        )
                                        from togetherWith to
                                    },
                                ) { song ->
                                    DynamicAsyncImage(
                                        imageUri = song?.artworkUri?.toUri(),
                                        contentDescription = null,
                                        modifier = Modifier
                                            .size(45.dp)
                                            .clip(RoundedCornerShape(10.dp))
                                    )
                                }
                                Spacer( modifier = Modifier.width( 15.dp ) )
                                AnimatedContent(
                                    modifier = Modifier.weight( 1f ),
                                    label = "now-playing-card-content",
                                    targetState = playingSong,
                                    transitionSpec = {
                                        val from = fadeIn(
                                            animationSpec = TransitionDurations.Normal.asTween(
                                                delayMillis = 150
                                            )
                                        ) + scaleIn(
                                            initialScale = 0.99f,
                                            animationSpec = TransitionDurations.Normal.asTween(
                                                delayMillis = 150
                                            )
                                        )
                                        val to = fadeOut(
                                            animationSpec = TransitionDurations.Fast.asTween()
                                        )
                                        from togetherWith to
                                    }
                                ) {
                                    NowPlayingBottomBarContent(
                                        song = it,
                                        nextSong = nextSong,
                                        previousSong = previousSong,
                                        textMarquee = uiState.userData.miniPlayerTextMarquee
                                    )
                                }
                                Spacer( modifier = Modifier.width( 15.dp ) )
                                AnimatedVisibility(
                                    uiState.userData.miniPlayerShowTrackControls
                                ) {
                                    IconButton(
                                        onClick = { previousSong() }
                                    ) {
                                        Icon(
                                            imageVector = Icons.Rounded.SkipPrevious,
                                            contentDescription = null
                                        )
                                    }
                                }
                                AnimatedVisibility(
                                    uiState.userData.miniPlayerShowSeekControls
                                ) {
                                    IconButton(
                                        onClick = seekBack
                                    ) {
                                        Icon(
                                            imageVector = Icons.Rounded.FastRewind,
                                            contentDescription = null
                                        )
                                    }
                                }
                                IconButton( onClick = playPause ) {
                                    AnimatedContent(
                                        targetState = uiState.playerState.isPlaying,
                                        label = "now-playing-bottom-bar-play-arrow"
                                    ) {
                                        Icon(
                                            imageVector = if ( it ) {
                                                Icons.Rounded.Pause
                                            } else {
                                                Icons.Rounded.PlayArrow
                                            },
                                            contentDescription = null
                                        )
                                    }
                                }
                                AnimatedVisibility(
                                    uiState.userData.miniPlayerShowSeekControls
                                ) {
                                    IconButton(
                                        onClick = seekForward
                                    ) {
                                        Icon(
                                            imageVector = Icons.Rounded.FastForward,
                                            contentDescription = null
                                        )
                                    }
                                }
                                AnimatedVisibility(
                                    uiState.userData.miniPlayerShowTrackControls
                                ) {
                                    IconButton(
                                        onClick = { nextSong() }
                                    ) {
                                        Icon(
                                            imageVector = Icons.Rounded.SkipNext,
                                            contentDescription = null
                                        )
                                    }
                                }
                                Spacer( modifier = Modifier.width( 8.dp ) )
                            }
                        }
                        // ------------------------- Progress Bar ------------------------------
                        Box(
                            modifier = Modifier
                                .height( 2.dp )
                                .fillMaxWidth()
                        ) {
                            Box(
                                modifier = Modifier
                                    .background(
                                        MaterialTheme.colorScheme.primary.copy( 0.3f )
                                    )
                                    .fillMaxWidth()
                                    .fillMaxHeight()
                            )
                            Box(
                                modifier = Modifier
                                    .align( Alignment.CenterStart )
                                    .background( MaterialTheme.colorScheme.primary.copy( alpha = 0.4f ) )
                                    .fillMaxWidth(  playbackPosition.bufferedRatio )
                                    .fillMaxHeight()
                            )
                            Box(
                                modifier = Modifier
                                    .align( Alignment.CenterStart )
                                    .background( MaterialTheme.colorScheme.primary )
                                    .fillMaxWidth(  playbackPosition.playedRatio )
                                    .fillMaxHeight()
                            )
                        }
                    }
                }
            }
        }
    }
}

@SuppressLint( "UnusedBoxWithConstraintsScope" )
@Composable
private fun NowPlayingBottomBarContent(
    song: Song,
    nextSong: () -> Boolean,
    previousSong: () -> Boolean,
    textMarquee: Boolean,
) {
    BoxWithConstraints {
        val cardWidthInPixels = constraints.maxWidth
        var offsetX by remember { mutableFloatStateOf( 0f ) }
        val cardOffsetX = animateIntAsState(
            targetValue = offsetX.toInt(),
            label = "now-playing-card-offset-x"
        )
        val cardOpacity = animateFloatAsState(
            targetValue = if ( offsetX != 0f ) 0.7f else 1f,
            label = "now-playing-card-opacity"
        )

        Box(
            modifier = Modifier
                .alpha( cardOpacity.value )
                .absoluteOffset {
                    IntOffset( cardOffsetX.value.div( 2 ), 0 )
                }
                .pointerInput( Unit ) {
                    detectHorizontalDragGestures(
                        onDragEnd = {
                            val threshHold = cardWidthInPixels / 4
                            offsetX = when {
                                -offsetX > threshHold -> {
                                    val changed = nextSong()
                                    if (changed) -cardWidthInPixels.toFloat() else 0f
                                }

                                offsetX > threshHold -> {
                                    val changed = previousSong()
                                    if (changed) cardWidthInPixels.toFloat() else 0f
                                }

                                else -> 0f
                            }
                        },
                        onDragCancel = {
                            offsetX = 0f
                        },
                        onHorizontalDrag = { _, dragAmount, ->
                            offsetX += dragAmount
                        },
                    )
                }
        ) {
            Column (
                modifier = Modifier.fillMaxWidth()
            ) {
                NowPlayingBottomBarContentText(
                    text = song.title,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.SemiBold
                    ),
                    textMarquee = textMarquee

                )
                NowPlayingBottomBarContentText(
                    song.artists.joinToString(),
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface.copy( alpha = 0.5f )
                    ),
                    textMarquee = textMarquee
                )
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun NowPlayingBottomBarContentText(
    text: String,
    style: TextStyle,
    textMarquee: Boolean
) {
    var showOverlay by remember { mutableStateOf( false ) }

    Box {
        Text(
            text = text,
            style = style,
            maxLines = 1,
            overflow = when {
                textMarquee -> TextOverflow.Clip
                else -> TextOverflow.Ellipsis
            },
            modifier = Modifier
                .onGloballyPositioned {
                    val offsetX = it.boundsInParent().centerLeft.x
                    showOverlay = offsetX.absoluteValue != 0f
                }
        )
        AnimatedVisibility(
            visible = showOverlay,
            modifier = Modifier.matchParentSize(),
            enter = FadeTransition.enterTransition(),
            exit = FadeTransition.exitTransition()
        ) {
            val backgroundColor = MaterialTheme.colorScheme.surfaceColorAtElevation( 1.dp )
            Row {
                Box(
                    modifier = Modifier
                        .width(12.dp)
                        .fillMaxHeight()
                        .background(
                            brush = Brush.horizontalGradient(
                                colors = listOf(backgroundColor, Color.Transparent)
                            )
                        )
                )
                Spacer( modifier = Modifier.weight( 1f ) )
                Box (
                    modifier = Modifier
                        .width( 12.dp )
                        .fillMaxHeight()
                        .background(
                            brush = Brush.horizontalGradient(
                                colors = listOf(Color.Transparent, backgroundColor)
                            )
                        )
                )
            }
        }
    }
}

internal fun Modifier.swipeable(
    minimumDragAmount: Float = 50f,
    onSwipeLeft: ( () -> Unit )? = null,
    onSwipeRight: ( () -> Unit )? = null,
    onSwipeUp: ( () -> Unit )? = null,
    onSwipeDown: ( () -> Unit )? = null,
) = pointerInput( Unit ) {
    var offset = Offset.Zero
    detectDragGestures(
        onDrag = { pointer, dragAmount ->
            pointer.consume()
            offset += dragAmount
        },
        onDragEnd = {
            val xAbsolute = offset.x.absoluteValue
            val yAbsolute = offset.y.absoluteValue
            when {
                xAbsolute > minimumDragAmount && xAbsolute > yAbsolute -> when {
                    offset.x > 0 -> onSwipeRight?.invoke()
                    else -> onSwipeLeft?.invoke()
                }
                yAbsolute > minimumDragAmount -> when {
                    offset.y > 0 -> onSwipeDown?.invoke()
                    else -> onSwipeUp?.invoke()
                }
            }
            offset = Offset.Zero
        },
        onDragCancel = {
            offset = Offset.Zero
        }
    )
}

@Preview( showBackground = true )
@Composable
private fun NowPlayingBottomBarPreview() {
    MusicMattersTheme(
        themeMode = ThemeMode.LIGHT,
        primaryColorName = DefaultPreferences.PRIMARY_COLOR_NAME,
        fontName = DefaultPreferences.FONT_NAME,
        fontScale = 1f,
        useMaterialYou = true,
    ) {
        NowPlayingBottomBarContent(
            uiState = NowPlayingScreenUiState.Success(
                userData = emptyUserData.copy( miniPlayerShowTrackControls = false ),
                queue = listOf(
                    Song(
                        id = "song-id-1",
                        mediaUri = "Uri.EMPTY",
                        title = "Started From the Bottom",
                        displayTitle = "",
                        duration = 0L,
                        artists = setOf( "Drake" ),
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
                    isPlaying = true,
                    isBuffering = false,
                ),
                playlists = emptyList(),
                songsAdditionalMetadataList = emptyList(),
            ),
            playbackPosition = PlaybackPosition(
                played = 3L,
                total = 5L,
                buffered = 4L,
            ),
            onNowPlayingBottomBarSwipeUp = {},
            onNowPlayingBottomBarClick = {},
            nextSong = { true },
            previousSong = { true },
            seekBack = {},
            seekForward = {},
            playPause = {},
        )
    }
}

val emptyUserData = UserData(
    language = English,
    fontName = "Product Sans",
    fontScale = 1f,
    themeMode = ThemeMode.FOLLOW_SYSTEM,
    useMaterialYou = true,
    primaryColorName = "Blue",
    bottomBarLabelVisibility = BottomBarLabelVisibility.ALWAYS_VISIBLE,
    fadePlayback = true,
    fadePlaybackDuration = 1f,
    requireAudioFocus = true,
    ignoreAudioFocusLoss = false,
    playOnHeadphonesConnect = true,
    pauseOnHeadphonesDisconnect = false,
    fastRewindDuration = 30,
    fastForwardDuration = 30,
    miniPlayerShowTrackControls = true,
    miniPlayerShowSeekControls = false,
    miniPlayerTextMarquee = true,
    lyricsLayout = LyricsLayout.REPLACE_ARTWORK,
    showNowPlayingAudioInformation = true,
    showNowPlayingSeekControls = false,
    playbackSpeed = 1f,
    playbackPitch = 1f,
    loopMode = LoopMode.None,
    shuffle = false,
    showLyrics = false,
    controlsLayoutDefault = true,
    currentlyDisabledTreePaths = emptySet(),
    sortSongsBy = SortSongsBy.TITLE,
    sortSongsReverse = false,
    sortArtistsBy = SortArtistsBy.ARTIST_NAME,
    sortArtistsReverse = false,
    sortGenresBy = SortGenresBy.NAME,
    sortGenresReverse = false,
    sortPlaylistsBy = SortPlaylistsBy.TITLE,
    sortPlaylistsReverse = false,
    sortAlbumsBy = SortAlbumsBy.ALBUM_NAME,
    sortAlbumsReverse = false,
    sortPathsBy = SortPathsBy.NAME,
    sortPathsReverse = false,
    currentlyPlayingSongId = "",
)

