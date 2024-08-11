package com.odesa.musicMatters.ui.components

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
import androidx.compose.foundation.basicMarquee
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
import androidx.compose.material.icons.filled.FastForward
import androidx.compose.material.icons.filled.FastRewind
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.boundsInParent
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.odesa.musicMatters.R
import com.odesa.musicMatters.core.designsystem.theme.isLight
import com.odesa.musicMatters.core.model.Song
import com.odesa.musicMatters.ui.navigation.FadeTransition
import com.odesa.musicMatters.ui.navigation.TransitionDurations
import com.odesa.musicMatters.ui.nowPlaying.NowPlayingScreenUiState
import com.odesa.musicMatters.ui.nowPlaying.NowPlayingViewModel
import com.odesa.musicMatters.ui.nowPlaying.testNowPlayingScreenUiState
import com.odesa.musicMatters.utils.runFunctionIfTrueElseReturnThisObject
import kotlin.math.absoluteValue


// Stateful
@Composable
fun NowPlayingBottomBar(
    viewModel: NowPlayingViewModel,
    onShowNowPlayingBottomSheet: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsState()

    NowPlayingBottomBarContent(
        nowPlayingScreenUiState = uiState,
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
fun NowPlayingBottomBarContent(
    nowPlayingScreenUiState: NowPlayingScreenUiState,
    onNowPlayingBottomBarSwipeUp: () -> Unit,
    onNowPlayingBottomBarClick: () -> Unit,
    nextSong: () -> Boolean,
    previousSong: () -> Boolean,
    seekBack: () -> Unit,
    seekForward: () -> Unit,
    playPause: () -> Unit,
) {

    val fallbackResourceId = if (
        nowPlayingScreenUiState.themeMode.isLight( LocalContext.current )
        ) R.drawable.placeholder_light else R.drawable.placeholder_dark

    AnimatedVisibility(
        visible = nowPlayingScreenUiState.currentlyPlayingSong != null,
    ) {

        nowPlayingScreenUiState.currentlyPlayingSong?.let { playingSong ->
            Column (
                modifier = Modifier
                    .background( MaterialTheme.colorScheme.surfaceColorAtElevation( 1.dp ) )
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
                            AsyncImage(
                                model = ImageRequest.Builder( LocalContext.current ).apply {
                                    data( song.artworkUri )
                                    placeholder( fallbackResourceId )
                                    fallback( fallbackResourceId )
                                    error( fallbackResourceId )
                                    crossfade( true )
                                }.build(),
                                modifier = Modifier
                                    .size(45.dp)
                                    .clip(RoundedCornerShape(10.dp)),
                                contentDescription = null
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
                                textMarquee = nowPlayingScreenUiState.textMarquee
                            )
                        }
                        Spacer( modifier = Modifier.width( 15.dp ) )
                        AnimatedVisibility ( nowPlayingScreenUiState.showTrackControls ) {
                            IconButton(
                                onClick = { previousSong() }
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.SkipPrevious,
                                    contentDescription = null
                                )
                            }
                        }
                        AnimatedVisibility ( nowPlayingScreenUiState.showSeekControls ) {
                            IconButton(
                                onClick = seekBack
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.FastRewind,
                                    contentDescription = null
                                )
                            }
                        }
                        IconButton( onClick = playPause ) {
                            AnimatedContent(
                                targetState = nowPlayingScreenUiState.isPlaying,
                                label = "now-playing-bottom-bar-play-arrow"
                            ) {
                                Icon(
                                    imageVector = if ( it ) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                                    contentDescription = null
                                )
                            }
                        }
                        AnimatedVisibility ( nowPlayingScreenUiState.showSeekControls ) {
                            IconButton(
                                onClick = seekForward
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.FastForward,
                                    contentDescription = null
                                )
                            }
                        }
                        AnimatedVisibility ( nowPlayingScreenUiState.showTrackControls ) {
                            IconButton(
                                onClick = { nextSong() }
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.SkipNext,
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
                        .height(1.dp)
                        .fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier
                            .background(MaterialTheme.colorScheme.primary.copy(0.3f))
                            .fillMaxWidth()
                            .fillMaxHeight()
                    )
                    Box(
                        modifier = Modifier
                            .align(Alignment.CenterStart)
                            .background(MaterialTheme.colorScheme.primary)
                            .fillMaxWidth(nowPlayingScreenUiState.playbackPosition.ratio)
                            .fillMaxHeight()
                    )
                }
            }
        }
    }
}

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
                .alpha(cardOpacity.value)
                .absoluteOffset {
                    IntOffset(cardOffsetX.value.div(2), 0)
                }
                .pointerInput(Unit) {
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
                    style = MaterialTheme.typography.bodyMedium,
                    textMarquee = textMarquee

                )
                NowPlayingBottomBarContentText(
                    song.artists.joinToString(),
                    style = MaterialTheme.typography.bodySmall,
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
                .runFunctionIfTrueElseReturnThisObject<Modifier>(textMarquee) {
                    basicMarquee(iterations = Int.MAX_VALUE)
                }
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
                        .width(12.dp)
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

data class PlaybackPosition(
    val played: Long,
    val total: Long,
) {
    val ratio: Float
        get() = ( played.toFloat() / total ).takeIf { it.isFinite() } ?: 0f

    companion object {
        val zero = PlaybackPosition( 0L, 0L )
    }
}

@Preview( showBackground = true )
@Composable
fun NowPlayingBottomBarPreview() {
    NowPlayingBottomBarContent(
        nowPlayingScreenUiState = testNowPlayingScreenUiState,
        onNowPlayingBottomBarSwipeUp = {},
        onNowPlayingBottomBarClick = {},
        nextSong = { true },
        previousSong = { true },
        seekBack = {},
        seekForward = {},
        playPause = {},
    )
}

