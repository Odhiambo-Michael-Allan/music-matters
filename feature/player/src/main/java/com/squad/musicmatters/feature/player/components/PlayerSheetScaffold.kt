package com.squad.musicmatters.feature.player.components

import android.util.Log
import androidx.activity.compose.PredictiveBackHandler
import androidx.compose.animation.core.exponentialDecay
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.AnchoredDraggableState
import androidx.compose.foundation.gestures.DraggableAnchors
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.anchoredDraggable
import androidx.compose.foundation.gestures.animateTo
import androidx.compose.foundation.gestures.snapTo
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveableStateHolder
import androidx.compose.runtime.snapshotFlow
import androidx.compose.runtime.State
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.layout.layout
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.Velocity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.lerp
import androidx.compose.ui.util.lerp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.squad.musicmatters.core.media.connection.PlaybackPosition
import com.squad.musicmatters.core.model.LoopMode
import com.squad.musicmatters.core.model.Playlist
import com.squad.musicmatters.core.model.Song
import com.squad.musicmatters.feature.player.LyricsUiState
import com.squad.musicmatters.feature.player.NowPlayingScreenCallbacks
import com.squad.musicmatters.feature.player.NowPlayingScreenContent
import com.squad.musicmatters.feature.player.NowPlayingScreenModel
import com.squad.musicmatters.feature.player.NowPlayingScreenUiState
import com.squad.musicmatters.feature.player.NowPlayingScreenViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.coroutines.cancellation.CancellationException
import kotlin.math.roundToInt
import kotlin.time.Duration

enum class PlayerSheetValue { Collapsed, Expanded }

/**
 * Fling velocity distance shared by [AnchoredDraggableState.velocityThreshold] and
 * [resolvePlayerSheetSettleTarget]'s "decisive fling" check, so both agree on what counts as a
 * deliberate swipe regardless of screen density.
 */
private val PlayerSheetDecisiveFlingDistance = 180.dp

data class PlayerSheetLayout(
    val collapsedTargetY: Float,
    val containerHeight: Dp,
    val collapsedHorizontalPadding: Dp = 12.dp,
    val expandTrigger: Long = 0L,
)

/**
 * An [AnchoredDraggableState]-driven bottom sheet that morphs between a mini bar and the immersive
 * full player. Springs everywhere, velocity-aware settling, nested-scroll handoff with the full
 * player content, and predictive-back collapse.
 */
@Composable
fun PlayerSheetScaffold(
    modifier: Modifier = Modifier,
    viewModel: NowPlayingScreenViewModel = hiltViewModel(),
    layout: PlayerSheetLayout,
    onViewAlbum: ( Long ) -> Unit,
    onViewArtist: ( Long ) -> Unit,
    onNavigateToQueueScreen: () -> Unit,
    onNavigateToLyricsScreen: () -> Unit,
    onLaunchEqualizerActivity: () -> Unit,
    onShowSnackBar: ( String ) -> Unit,
) {

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val lyricsUiState by viewModel.lyricsUiState.collectAsStateWithLifecycle()
    val playbackPosition by viewModel.playbackPosition.collectAsStateWithLifecycle()

    PlayerSheetScaffoldContent(
        modifier = modifier,
        uiState = uiState,
        lyricsUiState = lyricsUiState,
        onGetPlaybackPosition = { playbackPosition },
        onFavorite = viewModel::addToFavorites,
        onPlayPauseButtonClick = viewModel::playPause,
        onPreviousButtonClick = viewModel::playPreviousSong,
        onPlayNext = viewModel::playNextSong,
        onSeekStart = viewModel::onSeekStarted,
        onSeekEnd = viewModel::onSeekEnd,
        onNavigateToQueueScreen = onNavigateToQueueScreen,
        onNavigateToLyricsScreen = onNavigateToLyricsScreen,
        onCreateEqualizerActivityContract = onLaunchEqualizerActivity,
        onCreatePlaylist = viewModel::createPlaylist,
        onAddSongsToPlaylist = viewModel::addSongsToPlaylist,
        onViewAlbum = onViewAlbum,
        onViewArtist = onViewArtist,
        onSwipeArtworkLeft = viewModel::playNextSong,
        onSwipeArtworkRight = viewModel::playPreviousSong,
        onStartSleepTimer = viewModel::startSleepTimer,
        onStopSleepTimer = viewModel::stopSleepTimer,
        onShowLyrics = viewModel::onShowLyrics,
        onToggleLoopMode = viewModel::onToggleLoopMode,
        onToggleShuffleMode = viewModel::onToggleShuffleMode,
        onRemoveSongFromQueue = viewModel::removeSongFromQueue,
        onShowSnackBar = onShowSnackBar,
        layout = layout,
    )


}

@Composable
private fun PlayerSheetScaffoldContent(
    modifier: Modifier = Modifier,
    uiState: NowPlayingScreenUiState,
    lyricsUiState: LyricsUiState,
    layout: PlayerSheetLayout,
    onGetPlaybackPosition: () -> PlaybackPosition,
    onFavorite: ( Song, Boolean ) -> Unit,
    onPlayPauseButtonClick: () -> Unit,
    onPreviousButtonClick: () -> Unit,
    onPlayNext: () -> Unit,
    onSeekStart: () -> Unit,
    onSeekEnd: ( Long ) -> Unit,
    onSwipeArtworkLeft: () -> Unit,
    onSwipeArtworkRight: () -> Unit,
    onNavigateToQueueScreen: () -> Unit,
    onNavigateToLyricsScreen: () -> Unit,
    onCreateEqualizerActivityContract: () -> Unit,
    onCreatePlaylist: ( String, List<Song> ) -> Unit,
    onAddSongsToPlaylist: (Playlist, List<Song> ) -> Unit,
    onViewAlbum: ( Long ) -> Unit,
    onViewArtist: ( Long ) -> Unit,
    onShowSnackBar: ( String ) -> Unit,
    onStartSleepTimer: ( Duration ) -> Unit,
    onStopSleepTimer: () -> Unit,
    onShowLyrics: ( Boolean ) -> Unit,
    onToggleShuffleMode: ( Boolean ) -> Unit,
    onToggleLoopMode: ( LoopMode ) -> Unit,
    onRemoveSongFromQueue: (Song ) -> Unit,
) {

    val density = LocalDensity.current
    val scope = rememberCoroutineScope()
    val playerStateHolder = rememberSaveableStateHolder()
    val haptics = LocalHapticFeedback.current

    when ( uiState ) {
        NowPlayingScreenUiState.Loading -> {}
        is NowPlayingScreenUiState.Success -> {
            SideEffect { Log.d( "Recomp", "PlayerSheetScaffoldContent" ) }

            val colorScheme = rememberPlayerColorScheme(
                uiState.currentlyPlayingSong.artworkUri
            )

            // Thresholds/specs factory is deprecated in favor of AnchoredDraggableDefaults.flingBehavior;
            // Keep it here so mini-player settle velocity/feel stays identical.
            @Suppress( "DEPRECATION" )
            val sheetState = remember( density ) {
                AnchoredDraggableState(
                    initialValue = PlayerSheetValue.Collapsed,
                    positionalThreshold = { distance: Float -> distance * 0.5f },
                    velocityThreshold = {
                        with( density ) {
                            PlayerSheetDecisiveFlingDistance.toPx()
                        }
                    },
                    // Soft expressive settle - gesture gates below keep mini controls tappable
                    // while this runs ( don't stiffen the spring to "fix" dead taps ).
                    snapAnimationSpec = ExpressiveMotion.SpatialLargeSpring,
                    decayAnimationSpec = exponentialDecay(),
                )
            }
            ConfigurePlayerSheetAnchors( sheetState, layout.collapsedTargetY )
            val geometry = rememberPlayerSheetGeometry( sheetState, layout )
            PlayerSheetSettledEffects(
                sheetState,
                haptics,
                uiState.currentlyPlayingSong
            )
            PlayerSheetExternalExpansion( sheetState, layout.expandTrigger )
            PlayerSheetPredictiveBack(
                enabled = sheetState.settledValue == PlayerSheetValue.Expanded,
                sheetState = sheetState,
            )
            val sheetNestedScrollConnection = rememberPlayerSheetNestedScrollConnection(
                sheetState,
                density
            )

            val collapsedTargetY = layout.collapsedTargetY
            val playerSheetCallbacks = remember(
                sheetState,
                scope,
                collapsedTargetY
            ) {
                PlayerSheetCallbacks(
                    onExpand = {
                        requestSheetExpansion( sheetState, scope )
                    },
                    onCollapseWithAnimation = {
                        requestSheetCollapse(
                            sheetState,
                            collapsedTargetY,
                            scope
                        )
                    },
                    onCollapseWithoutAnimation = {
                        requestSheetCollapseWithoutAnimation(
                            sheetState,
                            collapsedTargetY,
                            scope
                        )
                    }
                )
            }
            val resources = remember (
                playerStateHolder,
                scope,
                haptics,
                sheetNestedScrollConnection
            ) {
                PlayerSheetResources(
                    playerStateHolder,
                    scope,
                    haptics,
                    sheetNestedScrollConnection
                )
            }

            PlayerSheetSurface(
                modifier = modifier,
                geometry = geometry,
                colorScheme = colorScheme,
                resources = PlayerSheetResources(
                    stateHolder = playerStateHolder,
                    scope = scope,
                    haptics = haptics,
                    nestedScrollConnection = sheetNestedScrollConnection,
                ),
                playerSheetCallbacks = playerSheetCallbacks,
                sheetState = sheetState,
                containerHeight = layout.containerHeight,
                nowPlayingScreenModel = NowPlayingScreenModel(
                    uiState = uiState,
                    lyricsUiState = lyricsUiState,
                    colorScheme = colorScheme,
                ),
                nowPlayingScreenCallbacks = NowPlayingScreenCallbacks(
                    onGetPlaybackPosition = onGetPlaybackPosition,
                    onFavorite = onFavorite,
                    onPlayPauseButtonClick = onPlayPauseButtonClick,
                    onPreviousButtonClick = onPreviousButtonClick,
                    onPlayNext = onPlayNext,
                    onSeekStart = onSeekStart,
                    onSeekEnd = onSeekEnd,
                    onArtworkClicked = { song ->
                        playerSheetCallbacks.onCollapseWithoutAnimation()
                        onViewAlbum( song.albumId )
                    },
                    onArtistClicked = {
                        playerSheetCallbacks.onCollapseWithoutAnimation()
                        onViewArtist( it )
                    },
                    onNavigateToQueueScreen = {
                        playerSheetCallbacks.onCollapseWithoutAnimation()
                        onNavigateToQueueScreen()
                    },
                    onNavigateToLyricsScreen = {
                        playerSheetCallbacks.onCollapseWithoutAnimation()
                        onNavigateToLyricsScreen()
                    },
                    onCreateEqualizerActivityContract = onCreateEqualizerActivityContract,
                    onCreatePlaylist = onCreatePlaylist,
                    onAddSongsToPlaylist = onAddSongsToPlaylist,
                    onViewAlbum = {
                        playerSheetCallbacks.onCollapseWithoutAnimation()
                        onViewAlbum( it )
                    },
                    onViewArtist = {
                        playerSheetCallbacks.onCollapseWithoutAnimation()
                        onViewArtist( it )
                    },
                    onSwipeArtworkLeft = onSwipeArtworkLeft,
                    onSwipeArtworkRight = onSwipeArtworkRight,
                    onStartSleepTimer = onStartSleepTimer,
                    onStopSleepTimer = onStopSleepTimer,
                    onShowLyrics = onShowLyrics,
                    onToggleLoopMode = onToggleLoopMode,
                    onToggleShuffleMode = onToggleShuffleMode,
                    onRemoveSongFromQueue = onRemoveSongFromQueue,
                    onShowSnackBar = onShowSnackBar,
                )
            )
        }
    }
}

@Composable
private fun ConfigurePlayerSheetAnchors(
    sheetState: AnchoredDraggableState<PlayerSheetValue>,
    collapsedTargetY: Float,
) {
    LaunchedEffect( collapsedTargetY ) {
        sheetState.updateAnchors(
            DraggableAnchors {
                PlayerSheetValue.Collapsed at collapsedTargetY
                PlayerSheetValue.Expanded at 0f
            }
        )
    }
}


@Composable
private fun rememberPlayerSheetGeometry(
    sheetState: AnchoredDraggableState<PlayerSheetValue>,
    layout: PlayerSheetLayout,
): State<PlayerSheetGeometry> {
    val density = LocalDensity.current
    val currentLayout by rememberUpdatedState( layout )
    val fullEntranceOffsetPx = remember( density ) { with( density ) { 24.dp.toPx() } }

    return remember( sheetState, fullEntranceOffsetPx ) {
        derivedStateOf {
            val collapsedTargetY = currentLayout.collapsedTargetY
            val raw = sheetState.offset
            val sheetOffset =
                if ( raw.isNaN() ) collapsedTargetY else raw.coerceIn( 0f, collapsedTargetY )

            val values = calculatePlayerSheetGeometry(
                PlayerSheetGeometryInput(
                    sheetOffset = sheetOffset,
                    collapsedTargetY = collapsedTargetY,
                    containerHeight = currentLayout.containerHeight,
                    collapsedHorizontalPadding = currentLayout.collapsedHorizontalPadding,
                    miniPlayerHeight = miniPlayerHeight,
                    collapsedTopCornerRadius = 12.dp,
                    collapsedBottomCornerRadius = 12.dp,
                    fullEntranceOffsetPx = fullEntranceOffsetPx,
                )
            )
            PlayerSheetGeometry(
                sheetOffset = sheetOffset,
                expansionFraction = values.expansionFraction,
                miniPlayerHeight = values.miniPlayerHeight,
                sheetHeight = values.sheetHeight,
                topCornerRadius = values.topCornerRadius,
                bottomCornerRadius = values.bottomCornerRadius,
                horizontalPadding = values.horizontalPadding,
                sheetElevation = values.sheetElevation,
                miniAlpha = values.miniAlpha,
                fullAlpha = values.fullAlpha,
                fullTranslationY = values.fullTranslationY,
            )
        }
    }
}

internal fun calculatePlayerSheetGeometry(
    input: PlayerSheetGeometryInput,
): PlayerSheetGeometryValues {
    val expansionFraction = if ( input.collapsedTargetY <= 0f ) {
        0f
    } else {
        ( 1f - input.sheetOffset / input.collapsedTargetY ).coerceIn( 0f, 1f )
    }
    val fullAlpha = ( ( expansionFraction - 0.25f ).coerceIn( 0f, 0.75f ) / 0.75f )
    return PlayerSheetGeometryValues(
        expansionFraction = expansionFraction,
        miniPlayerHeight = input.miniPlayerHeight,
        sheetHeight = lerp( input.miniPlayerHeight, input.containerHeight, expansionFraction ),
        topCornerRadius = lerp( input.collapsedTopCornerRadius, 0.dp, expansionFraction ),
        bottomCornerRadius = lerp( input.collapsedBottomCornerRadius, 0.dp, expansionFraction ),
        horizontalPadding = lerp( input.collapsedHorizontalPadding, 0.dp, expansionFraction ),
        sheetElevation = lerp( 3.dp, 16.dp, expansionFraction ),
        miniAlpha = ( 1f - expansionFraction * 2f ).coerceIn( 0f, 1f ),
        fullAlpha = fullAlpha,
        fullTranslationY = lerp( input.fullEntranceOffsetPx, 0f, fullAlpha )
    )
}

internal data class PlayerSheetGeometryInput(
    val sheetOffset: Float,
    val collapsedTargetY: Float,
    val containerHeight: Dp,
    val collapsedHorizontalPadding: Dp,
    val fullEntranceOffsetPx: Float,
    val miniPlayerHeight: Dp = 64.dp,
    val collapsedTopCornerRadius: Dp = 24.dp,
    val collapsedBottomCornerRadius: Dp = 24.dp,
)

internal data class PlayerSheetGeometryValues(
    val expansionFraction: Float,
    val miniPlayerHeight: Dp,
    val sheetHeight: Dp,
    val topCornerRadius: Dp,
    val bottomCornerRadius: Dp,
    val horizontalPadding: Dp,
    val sheetElevation: Dp,
    val miniAlpha: Float,
    val fullAlpha: Float,
    val fullTranslationY: Float
)

private data class PlayerSheetGeometry(
    val sheetOffset: Float,
    val expansionFraction: Float,
    val miniPlayerHeight: Dp,
    val sheetHeight: Dp,
    val topCornerRadius: Dp,
    val bottomCornerRadius: Dp,
    val horizontalPadding: Dp,
    val sheetElevation: Dp,
    val miniAlpha: Float,
    val fullAlpha: Float,
    val fullTranslationY: Float,
)

@Composable
private fun PlayerSheetSettledEffects(
    sheetState: AnchoredDraggableState<PlayerSheetValue>,
    haptics: androidx.compose.ui.hapticfeedback.HapticFeedback,
    song: Song,
) {
    LaunchedEffect( sheetState, song.id ) {
        var previous = sheetState.settledValue
        snapshotFlow { sheetState.settledValue }.collect { value ->
            if ( value == previous ) return@collect
            haptics.performHapticFeedback( HapticFeedbackType.TextHandleMove )
            previous = value
        }
    }
}

@Composable
private fun PlayerSheetExternalExpansion(
    sheetState: AnchoredDraggableState<PlayerSheetValue>,
    expandTrigger: Long,
) {
    LaunchedEffect( expandTrigger ) {
        if ( expandTrigger > 0L ) {
            sheetState.animateTo( PlayerSheetValue.Expanded )
        }
    }
}

@Composable
private fun PlayerSheetPredictiveBack(
    enabled: Boolean,
    sheetState: AnchoredDraggableState<PlayerSheetValue>,
) {
    PredictiveBackHandler( enabled = enabled ) { progress ->
        progress.collect()
        sheetState.animateTo( PlayerSheetValue.Collapsed )
    }
}

@Composable
private fun rememberPlayerSheetNestedScrollConnection(
    sheetState: AnchoredDraggableState<PlayerSheetValue>,
    density: Density,
): NestedScrollConnection = remember( sheetState, density ) {
    object : NestedScrollConnection {
        private var contentOwnsGesture = false

        override fun onPreScroll(
            available: Offset,
            source: NestedScrollSource,
        ): Offset =
            consumePlayerSheetPreScroll(sheetState, available)

        override fun onPostScroll(
            consumed: Offset,
            available: Offset,
            source: NestedScrollSource,
        ): Offset {
            if (source == NestedScrollSource.UserInput) {
                contentOwnsGesture =
                    PlayerSheetNestedScrollLogic.contentOwnsGestureAfterScroll(
                        contentOwnsGesture,
                        consumed.y,
                    )
            }
            return consumePlayerSheetPostScroll(
                sheetState = sheetState,
                available = available,
                source = source,
                contentOwnsGesture = contentOwnsGesture,
            )
        }

        override suspend fun onPreFling(available: Velocity): Velocity =
            settlePlayerSheetPreFling(
                sheetState,
                available,
                density
            )

        override suspend fun onPostFling(
            consumed: Velocity,
            available: Velocity,
        ): Velocity {
            try {
                if (
                    !PlayerSheetNestedScrollLogic.shouldSettleSheetOnPostFling(
                        contentOwnsGesture = contentOwnsGesture,
                        sheetOffset = sheetState.requireOffset(),
                        animationRunning = sheetState.isAnimationRunning,
                    )
                ) {
                    return Velocity.Zero
                }
                return settlePlayerSheetPostFling(
                    sheetState,
                    available,
                    density
                )
            } finally {
                contentOwnsGesture = false
            }
        }
    }
}

/**
 * Nested-scroll handoff between full-player content and the mini/full sheet.
 *
 * A swipe that is still scrolling queue/notes must stop at the content top.
 * Collapsing the sheet requires a new downward swipe from that rest position.
 */
object PlayerSheetNestedScrollLogic {
    const val EXPANDED_OFFSET_EPS = 0.5f

    fun isFullyExpanded( sheetOffset: Float ): Boolean = sheetOffset <= EXPANDED_OFFSET_EPS

    fun contentOwnsGestureAfterScroll(
        alreadyOwned: Boolean,
        childConsumedY: Float,
    ): Boolean = alreadyOwned || childConsumedY != 0f

    fun shouldMoveSheetOnPostScroll(
        contentOwnsGesture: Boolean,
        sheetOffset: Float,
        availableY: Float,
        sourceIsUserInput: Boolean,
    ): Boolean {
        if ( !sourceIsUserInput || availableY == 0f ) return false
        // Leftover downward overscroll from a content scroll must not start collapse.
        if ( contentOwnsGesture && isFullyExpanded( sheetOffset ) && availableY > 0f ) return false
        return true
    }

    fun shouldSettleSheetOnPostFling(
        contentOwnsGesture: Boolean,
        sheetOffset: Float,
        animationRunning: Boolean,
    ): Boolean {
        if ( animationRunning ) return false
        if ( contentOwnsGesture && isFullyExpanded( sheetOffset ) ) return false
        return true
    }
}

private fun consumePlayerSheetPreScroll(
    sheetState: AnchoredDraggableState<PlayerSheetValue>,
    available: Offset,
): Offset {
    val delta = available.y
    return if ( delta < 0 && sheetState.requireOffset() > 0f ) {
        Offset( 0f, sheetState.dispatchRawDelta( delta ) )
    } else {
        Offset.Zero
    }
}

private fun consumePlayerSheetPostScroll(
    sheetState: AnchoredDraggableState<PlayerSheetValue>,
    available: Offset,
    source: NestedScrollSource,
    contentOwnsGesture: Boolean,
): Offset {
    if (
        !PlayerSheetNestedScrollLogic.shouldMoveSheetOnPostScroll(
            contentOwnsGesture = contentOwnsGesture,
            sheetOffset = sheetState.requireOffset(),
            availableY = available.y,
            sourceIsUserInput = source == NestedScrollSource.UserInput,
        )
    ) {
        return Offset.Zero
    }
    return Offset( 0f, sheetState.dispatchRawDelta( available.y ) )
}

private suspend fun settlePlayerSheetPostFling(
    sheetState: AnchoredDraggableState<PlayerSheetValue>,
    available: Velocity,
    density: Density,
): Velocity {
    // Always snap to an anchor after nested-scroll user input — including
    // zero-velocity releases from slow taps/drags. Skipping settle left the sheet
    // mid-anchor with currentValue still Expanded while UI already looked collapsed.
    if ( sheetState.isAnimationRunning ) return Velocity.Zero
    sheetState.animateTo(
        resolvePlayerSheetSettleTarget( sheetState, available.y, density )
    )
    return available
}

private suspend fun settlePlayerSheetPreFling(
    sheetState: AnchoredDraggableState<PlayerSheetValue>,
    available: Velocity,
    density: Density,
): Velocity {
    if (available.y >= 0 || sheetState.requireOffset() <= 0f) return Velocity.Zero
    // animateTo (not settle-with-velocity): fling kick + underdamped spring overshoots
    // past the mini anchor, then the visual clamp makes the rebound look like a
    // disconnected bounce against travel direction.
    sheetState.animateTo(
        resolvePlayerSheetSettleTarget( sheetState, available.y, density )
    )
    return available
}

private fun resolvePlayerSheetSettleTarget(
    sheetState: AnchoredDraggableState<PlayerSheetValue>,
    velocityY: Float,
    density: Density,
): PlayerSheetValue {
    // Decisive fling — same density-scaled distance as AnchoredDraggableState's velocityThreshold,
    // so a "decisive" swipe means the same thing in both places on every screen density.
    val decisiveFling = with( density ) { PlayerSheetDecisiveFlingDistance.toPx() }
    when {
        velocityY > decisiveFling -> return PlayerSheetValue.Collapsed
        velocityY < -decisiveFling -> return PlayerSheetValue.Expanded
    }
    val offset = sheetState.requireOffset()
    val collapsed = sheetState.anchors.positionOf( PlayerSheetValue.Collapsed )
    val expanded = sheetState.anchors.positionOf( PlayerSheetValue.Expanded )
    val midpoint = ( collapsed + expanded ) * 0.5f
    return if ( offset >= midpoint ) PlayerSheetValue.Collapsed else PlayerSheetValue.Expanded
}

@Composable
@Suppress( "DEPRECATION" )
private fun PlayerSheetSurface(
    geometry: State<PlayerSheetGeometry>,
    colorScheme: ColorScheme,
    resources: PlayerSheetResources,
    nowPlayingScreenCallbacks: NowPlayingScreenCallbacks,
    playerSheetCallbacks: PlayerSheetCallbacks,
    sheetState: AnchoredDraggableState<PlayerSheetValue>,
    containerHeight: Dp,
    nowPlayingScreenModel: NowPlayingScreenModel,
    modifier: Modifier = Modifier,
) {
    val containerColor = miniPlayerSheetColor( colorScheme )
    val interactionSource = remember { MutableInteractionSource() }

    // Only invalidates when the value flips, not on every frame.
    val miniOnTop by remember( geometry ) {
        derivedStateOf { geometry.value.expansionFraction < 0.5f }
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            // Layout phase: height, horizontal padding and vertical offset.
            .layout { measurable, constraints ->
                val g = geometry.value
                val pad = g.horizontalPadding.roundToPx()
                val width = ( constraints.maxWidth - pad * 2 ).coerceAtLeast( 0 )
                val height = g.sheetHeight.roundToPx()
                val placeable = measurable.measure( Constraints.fixed( width, height ) )
                layout( constraints.maxWidth, height ) {
                    placeable.place( pad, g.sheetOffset.roundToInt() )
                }
            }
            // Draw phase: shadow, corner radii, clipping.
            .graphicsLayer {
                val g = geometry.value
                shadowElevation = g.sheetElevation.toPx()
                shape = RoundedCornerShape(
                    topStart = g.topCornerRadius,
                    topEnd = g.topCornerRadius,
                    bottomStart = g.bottomCornerRadius,
                    bottomEnd = g.bottomCornerRadius,
                )
                clip = true
            }
            .drawBehind { drawRect( containerColor ) }
            .anchoredDraggable(
                state = sheetState,
                orientation = Orientation.Vertical,
                startDragImmediately = false,
            )
            .clickable(
                enabled = miniOnTop,
                interactionSource = interactionSource,
                indication = null,
            ) {
                resources.haptics.performHapticFeedback( HapticFeedbackType.TextHandleMove )
                playerSheetCallbacks.onExpand()
            },
    ) {
        PlayerSheetLayers(
            geometry = geometry,
            resources = resources,
            playerSheetCallbacks = playerSheetCallbacks,
            nowPlayingScreenCallbacks = nowPlayingScreenCallbacks,
            containerHeight = containerHeight,
            model = nowPlayingScreenModel,
        )
    }
}



private data class PlayerSheetResources(
    val stateHolder: androidx.compose.runtime.saveable.SaveableStateHolder,
    val scope: CoroutineScope,
    val haptics: androidx.compose.ui.hapticfeedback.HapticFeedback,
    val nestedScrollConnection: NestedScrollConnection,
)
private data class PlayerSheetCallbacks(
    val onExpand: () -> Unit,
    val onCollapseWithAnimation: () -> Unit,
    val onCollapseWithoutAnimation: () -> Unit,
)


@Composable
private fun PlayerSheetLayers(
    geometry: State<PlayerSheetGeometry>,
    resources: PlayerSheetResources,
    nowPlayingScreenCallbacks: NowPlayingScreenCallbacks,
    playerSheetCallbacks: PlayerSheetCallbacks,
    containerHeight: Dp,
    model: NowPlayingScreenModel,
) {
    // Each of these flips at most a few times per gesture.
    val miniVisible by remember( geometry ) {
        derivedStateOf { geometry.value.expansionFraction < 0.999f }
    }
    val fullVisible by remember( geometry ) {
        derivedStateOf { geometry.value.expansionFraction > 0.01f }
    }
    val fullOnTop by remember( geometry ) {
        derivedStateOf { geometry.value.expansionFraction >= 0.5f }
    }

    Box( modifier = Modifier.fillMaxSize() ) {
        if ( miniVisible ) {
            MiniPlayerLayer(
                geometry = geometry,
                fullOnTop = fullOnTop,
                resources = resources,
                model = model,
                nowPlayingScreenCallbacks = nowPlayingScreenCallbacks,
                playerSheetCallbacks = playerSheetCallbacks,
            )
        }
        if ( fullVisible ) {
            FullPlayerLayer(
                geometry = geometry,
                fullOnTop = fullOnTop,
                resources = resources,
                containerHeight = containerHeight,
                model = model,
                callbacks = nowPlayingScreenCallbacks,
            )
        }
    }
}

@Composable
private fun MiniPlayerLayer(
    geometry: State<PlayerSheetGeometry>,
    fullOnTop: Boolean,
    resources: PlayerSheetResources,
    model: NowPlayingScreenModel,
    nowPlayingScreenCallbacks: NowPlayingScreenCallbacks,
    playerSheetCallbacks: PlayerSheetCallbacks,
) {
    resources.stateHolder.SaveableStateProvider( "mini-player" ) {
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .height( miniPlayerHeight )
                .graphicsLayer { alpha = geometry.value.miniAlpha }   // draw phase
                .zIndex( if ( fullOnTop ) 0f else 1f )
        ) {
            MiniPlayerContent(
                model = model,
                callbacks = nowPlayingScreenCallbacks,
                onNowPlayingBottomBarClick = playerSheetCallbacks.onExpand,
            )
        }
    }
}

@Composable
private fun FullPlayerLayer(
    geometry: State<PlayerSheetGeometry>,
    fullOnTop: Boolean,
    resources: PlayerSheetResources,
    containerHeight: Dp,
    model: NowPlayingScreenModel,
    callbacks: NowPlayingScreenCallbacks,
) {
    resources.stateHolder.SaveableStateProvider( "full-player" ) {
        Box(
            modifier = Modifier
                // Fixed-height, top-aligned content that ignores the shrinking parent,
                // so the full player isn't re-measured as the sheet height changes.
                .wrapContentHeight( align = Alignment.Top, unbounded = true )
                .height( containerHeight )
                .graphicsLayer {                                       // draw phase
                    val g = geometry.value
                    alpha = g.fullAlpha
                    translationY = g.fullTranslationY
                }
                .zIndex( if ( fullOnTop ) 1f else 0f )
        ) {
            NowPlayingScreenContent(
                model = model,
                callbacks = callbacks,
                nestedScrollConnection = resources.nestedScrollConnection,
            )
        }
    }
}

private fun requestSheetExpansion(
    sheetState: AnchoredDraggableState<PlayerSheetValue>,
    scope: CoroutineScope,
) {
    if ( sheetState.offset > 0.5f ) {
        scope.launch { sheetState.animateTo( PlayerSheetValue.Expanded ) }
    }
}

private fun requestSheetCollapse(
    sheetState: AnchoredDraggableState<PlayerSheetValue>,
    collapsedTargetY: Float,
    scope: CoroutineScope,
) {
    if ( sheetState.offset >= collapsedTargetY - 0.5f ) return
    scope.launch { sheetState.animateTo( PlayerSheetValue.Collapsed ) }
}

private fun requestSheetCollapseWithoutAnimation(
    sheetState: AnchoredDraggableState<PlayerSheetValue>,
    collapsedTargetY: Float,
    scope: CoroutineScope,
) {
    if ( sheetState.offset >= collapsedTargetY - 0.5f ) return
    scope.launch { sheetState.snapTo( PlayerSheetValue.Collapsed ) }
}

private val navigationBarHeight = 80.dp
private val navigationBottomInset = 0.dp
private val miniPlayerHeight = 72.dp
private val miniPlayerNavigationGap = 8.dp
private val miniPlayerTopCornerRadius = 26.dp
private val miniPlayerBottomCornerRadius = 14.dp