package com.squad.musicmatters.feature.nowplaying.components

import androidx.activity.compose.PredictiveBackHandler
import androidx.compose.animation.core.exponentialDecay
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.AnchoredDraggableState
import androidx.compose.foundation.gestures.DraggableAnchors
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.anchoredDraggable
import androidx.compose.foundation.gestures.animateTo
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveableStateHolder
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
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
import com.squad.musicmatters.feature.nowplaying.LyricsUiState
import com.squad.musicmatters.feature.nowplaying.NowPlayingScreenCallbacks
import com.squad.musicmatters.feature.nowplaying.NowPlayingScreenContent
import com.squad.musicmatters.feature.nowplaying.NowPlayingScreenModel
import com.squad.musicmatters.feature.nowplaying.NowPlayingScreenUiState
import com.squad.musicmatters.feature.nowplaying.NowPlayingScreenViewModel
import kotlinx.coroutines.NonCancellable
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
    onViewAlbum: ( String ) -> Unit,
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
    onViewAlbum: ( String ) -> Unit,
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
            val isExpanded = sheetState.currentValue == PlayerSheetValue.Expanded
            PlayerSheetSettledEffects(
                sheetState,
                haptics,
                uiState.currentlyPlayingSong
            )
            PlayerSheetExternalExpansion( sheetState, layout.expandTrigger )
            PlayerSheetPredictiveBack(
                enabled = isExpanded,
                sheetState = sheetState,
                collapsedTargetY = layout.collapsedTargetY,
            )
            val sheetNestedScrollConnection = rememberPlayerSheetNestedScrollConnection( sheetState, density )
            val playerSheetCallbacks = PlayerSheetCallbacks(
                onExpand = { requestSheetExpansion( sheetState, geometry.sheetOffset, scope ) },
                onCollapse = {
                    requestSheetCollapse( sheetState, geometry.sheetOffset, layout.collapsedTargetY, scope )
                },
            )
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
                        playerSheetCallbacks.onCollapse()
                        song.albumTitle?.let { onViewAlbum( it ) }
                    },
                    onArtistClicked = {
                        playerSheetCallbacks.onCollapse()
                        onViewArtist( it )
                    },
                    onNavigateToQueueScreen = {
                        playerSheetCallbacks.onCollapse()
                        onNavigateToQueueScreen()
                    },
                    onNavigateToLyricsScreen = {
                        playerSheetCallbacks.onCollapse()
                        onNavigateToLyricsScreen()
                    },
                    onCreateEqualizerActivityContract = onCreateEqualizerActivityContract,
                    onCreatePlaylist = onCreatePlaylist,
                    onAddSongsToPlaylist = onAddSongsToPlaylist,
                    onViewAlbum = onViewAlbum,
                    onViewArtist = onViewArtist,
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
): PlayerSheetGeometry {

    val density = LocalDensity.current
    val sheetOffset by remember( sheetState, layout.collapsedTargetY ) {
        derivedStateOf {
            val raw = sheetState.offset
            if ( raw.isNaN() ) layout.collapsedTargetY else raw.coerceIn( 0f, layout.collapsedTargetY )
        }
    }
    val fullEntranceOffsetPx = remember( density ) {
        with( density ) {
            24.dp.toPx()
        }
    }
    val values = calculatePlayerSheetGeometry(
        PlayerSheetGeometryInput(
            sheetOffset = sheetOffset,
            collapsedTargetY = layout.collapsedTargetY,
            containerHeight = layout.containerHeight,
            collapsedHorizontalPadding = layout.collapsedHorizontalPadding,
            miniPlayerHeight = 72.dp,
            collapsedTopCornerRadius = 12.dp,
            collapsedBottomCornerRadius = 12.dp,
            fullEntranceOffsetPx = fullEntranceOffsetPx,
        )
    )
    return PlayerSheetGeometry(
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
    collapsedTargetY: Float,
) {
    PredictiveBackHandler( enabled = enabled ) { progress ->
        try {
            progress.collect { backEvent ->
                val target = collapsedTargetY * 0.2f * backEvent.progress
                sheetState.dispatchRawDelta( target - sheetState.requireOffset() )
            }
            sheetState.animateTo( PlayerSheetValue.Collapsed )
        } catch ( exception: CancellationException ) {
            withContext( NonCancellable ) {
                sheetState.animateTo( PlayerSheetValue.Expanded )
            }
            throw exception
        }
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
@Suppress("DEPRECATION") // startDragImmediately=false keeps mini controls tappable during settle
private fun PlayerSheetSurface(
    geometry: PlayerSheetGeometry,
    colorScheme: ColorScheme,
    resources: PlayerSheetResources,
    nowPlayingScreenCallbacks: NowPlayingScreenCallbacks,
    playerSheetCallbacks: PlayerSheetCallbacks,
    sheetState: AnchoredDraggableState<PlayerSheetValue>,
    containerHeight: Dp,
    nowPlayingScreenModel: NowPlayingScreenModel,
    modifier: Modifier = Modifier,
) {
    val shape =
        RoundedCornerShape(
            topStart = geometry.topCornerRadius,
            topEnd = geometry.topCornerRadius,
            bottomStart = geometry.bottomCornerRadius,
            bottomEnd = geometry.bottomCornerRadius,
        )
    Box(
        modifier =
            modifier
                .fillMaxWidth()
                .offset { IntOffset( 0, geometry.sheetOffset.roundToInt() ) }
                .graphicsLayer { clip = false }
                .height( geometry.sheetHeight ),
    ) {
        Box(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding( horizontal = geometry.horizontalPadding )
                    .height( geometry.sheetHeight )
                    .shadow( elevation = geometry.sheetElevation, shape = shape, clip = false )
                    .background( color = miniPlayerSheetColor( colorScheme ), shape = shape )
                    .clip( shape )
                    .anchoredDraggable(
                        state = sheetState,
                        orientation = Orientation.Vertical,
                        // Never steal taps while settling: default true-during-animation
                        // ate mini controls for ~1–2s. Touch-slop still allows swipe-up
                        // expand from the collapsed mini player.
                        startDragImmediately = false,
                    )
                    .clickable(
                        enabled = geometry.expansionFraction < 0.5f,
                        interactionSource = remember { MutableInteractionSource() },
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
}



private data class PlayerSheetResources(
    val stateHolder: androidx.compose.runtime.saveable.SaveableStateHolder,
    val scope: kotlinx.coroutines.CoroutineScope,
    val haptics: androidx.compose.ui.hapticfeedback.HapticFeedback,
    val nestedScrollConnection: NestedScrollConnection,
)
private data class PlayerSheetCallbacks(
    val onExpand: () -> Unit,
    val onCollapse: () -> Unit,
)


@Composable
private fun PlayerSheetLayers(
    geometry: PlayerSheetGeometry,
    resources: PlayerSheetResources,
    nowPlayingScreenCallbacks: NowPlayingScreenCallbacks,
    playerSheetCallbacks: PlayerSheetCallbacks,
    containerHeight: Dp,
    model: NowPlayingScreenModel,
) {
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        MiniPlayerLayer(
            visible = geometry.expansionFraction < 0.999f,
            geometry = geometry,
            resources = resources,
            nowPlayingScreenCallbacks = nowPlayingScreenCallbacks,
            playerSheetCallbacks = playerSheetCallbacks,
            model = model,
        )
        FullPlayerLayer(
            visible = geometry.expansionFraction > 0.001f,
            geometry = geometry,
            resources = resources,
            containerHeight = containerHeight,
            model = model,
            callbacks = nowPlayingScreenCallbacks,
        )
    }
}

@Composable
private fun MiniPlayerLayer(
    visible: Boolean,
    geometry: PlayerSheetGeometry,
    resources: PlayerSheetResources,
    model: NowPlayingScreenModel,
    nowPlayingScreenCallbacks: NowPlayingScreenCallbacks,
    playerSheetCallbacks: PlayerSheetCallbacks,
) {
    if ( !visible ) return
    resources.stateHolder.SaveableStateProvider( "mini-player" ) {
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .height( geometry.miniPlayerHeight )
                .fillMaxSize()
                .graphicsLayer { alpha = geometry.miniAlpha }
                .zIndex(
                    if ( geometry.expansionFraction < 0.5f ) 1f else 0f
                )
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
    visible: Boolean,
    geometry: PlayerSheetGeometry,
    resources: PlayerSheetResources,
    containerHeight: Dp,
    model: NowPlayingScreenModel,
    callbacks: NowPlayingScreenCallbacks,
) {
    if ( !visible ) return

    resources.stateHolder.SaveableStateProvider( "full-player" ) {
        Box(
            modifier = Modifier
                .height( containerHeight )
                .graphicsLayer {
                    alpha = geometry.fullAlpha
                    translationY = geometry.fullTranslationY
                }
                .zIndex(
                    if ( geometry.expansionFraction >= 0.5f ) 1f else 0f
                )
                .offset {
                    if ( geometry.expansionFraction <= 0.01f ) {
                        IntOffset( 0, 10000 )
                    } else {
                        IntOffset.Zero
                    }
                }
        ) {
            NowPlayingScreenContent(
                model = model,
                callbacks = callbacks,
                nestedScrollConnection = resources.nestedScrollConnection
            )
        }
    }
}

private fun requestSheetExpansion(
    sheetState: AnchoredDraggableState<PlayerSheetValue>,
    sheetOffset: Float,
    scope: kotlinx.coroutines.CoroutineScope,
) {
    if ( sheetOffset > 0.5f ) {
        scope.launch { sheetState.animateTo( PlayerSheetValue.Expanded ) }
    }

}

private fun requestSheetCollapse(
    sheetState: AnchoredDraggableState<PlayerSheetValue>,
    sheetOffset: Float,
    collapsedTargetY: Float,
    scope: kotlinx.coroutines.CoroutineScope,
) {
    if ( sheetOffset >= collapsedTargetY - 0.5f ) return
    scope.launch { sheetState.animateTo( PlayerSheetValue.Collapsed ) }
}

//@PreviewScreenSizes
//@Composable
//private fun PlayerSheetScaffoldPreview(
//    @PreviewParameter( MusicMattersPreviewParametersProvider::class )
//    previewData: PreviewData
//) {
//    MusicMattersTheme(
//        themeMode = ThemeMode.LIGHT,
//        primaryColorName = "Blue",
//        fontName = DefaultPreferences.FONT_NAME,
//        fontScale = DefaultPreferences.FONT_SCALE,
//        useMaterialYou = true
//    ) {
//        BoxWithConstraints(
//            modifier = Modifier.fillMaxSize()
//        ) {
//
//            val density = LocalDensity.current
//            val screenHeightDp = maxHeight
//            val systemNavBarHeight = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
//
//            val containerHeight = screenHeightDp + systemNavBarHeight + 50.dp
//            val collapsedTargetY =
//                with(density) {
//                    (
//                            screenHeightDp -
//                                    miniPlayerHeight -
//                                    ( navigationBarHeight + navigationBottomInset ) -
////                                    playerSystemNavigationInset -
//                                    miniPlayerNavigationGap
//                            ).toPx()
//                }
//
//            PlayerSheetScaffold(
//                model = NowPlayingScreenModel(
//                    uiState = NowPlayingScreenUiState.Success(
//                        userData = emptyUserData.copy(
//                            loopMode = LoopMode.Queue,
//                        ),
//                        currentlyPlayingSong = previewData.songs.first(),
//                        currentlyPlayingSongIsFavorite = true,
//                        playerState = PlayerState(
//                            currentlyPlayingSongId = "song-id-1",
//                            isPlaying = true,
//                            isBuffering = false,
//                        ),
//                        playlists = emptyList(),
//                        songMetadata = SongMetadata(
//                            songId = "",
//                            codec = "mp3",
//                            bitrate = 0,
//                            samplingRate = 0f,
//                            bitsPerSample = 0,
//                            genreName = "Hip-Hop"
//                        ),
//                        sleepTimer = SleepTimer(
//                            duration = 300000L.toDuration( DurationUnit.MILLISECONDS ),
//                            endsAt = System.currentTimeMillis().toDuration( DurationUnit.MILLISECONDS ),
//                            timer = Timer()
//                        )
//                    ),
//                    lyricsUiState = LyricsUiState.Success(
//                        lyrics = listOf(
//                            Lyric(
//                                timeStamp = java.time.Duration.ofMinutes( 1 ),
//                                content = "Sometime say the magic you dey feel inside is like gold"
//                            ),
//                            Lyric(
//                                timeStamp = java.time.Duration.ofMinutes( 2 ),
//                                content = "Something like do re mi fa so lat ti do do (Yeah)"
//                            ),
//                            Lyric(
//                                timeStamp = java.time.Duration.ofMinutes( 3 ),
//                                content = "Make I sing for you la la do do"
//                            ),
//                            Lyric(
//                                timeStamp = java.time.Duration.ofMinutes( 4 ),
//                                content = "Make I sing your song"
//                            ),
//                            Lyric(
//                                timeStamp = java.time.Duration.ofMinutes( 5 ),
//                                content = "Make I sing make you wine am do do o"
//                            )
//                        ),
//                    ),
//                    onGetPlaybackPosition = { PlaybackPosition( 2L, 3L, 5L ) },
//                    onArtistClicked = {},
//                    onFavorite = { _, _ -> },
//                    onPlayPauseButtonClick = {},
//                    onPreviousButtonClick = {},
//                    onPlayNext = {},
//                    onSeekEnd = {},
//                    onArtworkClicked = {},
//                    onSwipeArtworkLeft = {},
//                    onSwipeArtworkRight = {},
//                    onNavigateToQueueScreen = {},
//                    onSeekStart = {},
//                    onCreateEqualizerActivityContract = {
//                        object : ActivityResultContract<Unit, Unit>() {
//                            override fun createIntent(context: Context, input: Unit) = Intent()
//                            override fun parseResult( resultCode: Int, intent: Intent? ) {}
//
//                        }
//                    },
//                    onCreatePlaylist = { _, _ -> },
//                    onAddSongsToPlaylist = { _, _ -> },
//                    onViewAlbum = {},
//                    onViewArtist = {},
//                    onHideNowPlayingBottomSheet = {},
//                    onShowSnackBar = {},
//                    onStopSleepTimer = {},
//                    onStartSleepTimer = {},
//                    onShowLyrics = {},
//                    onToggleLoopMode = {},
//                    onToggleShuffleMode = {},
//                    onRemoveSongFromQueue = {},
//                    onNavigateToLyricsScreen = {},
//                ),
//                layout = PlayerSheetLayout(
//                    collapsedTargetY = collapsedTargetY,
//                    containerHeight = containerHeight,
//                    collapsedHorizontalPadding = 12.dp,
//                ),
//            )
//        }
//    }
//}

private val navigationBarHeight = 80.dp
private val navigationBottomInset = 0.dp
private val miniPlayerHeight = 72.dp
private val miniPlayerNavigationGap = 8.dp
private val miniPlayerTopCornerRadius = 26.dp
private val miniPlayerBottomCornerRadius = 14.dp