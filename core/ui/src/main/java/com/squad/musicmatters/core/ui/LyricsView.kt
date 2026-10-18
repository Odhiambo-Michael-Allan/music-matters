
package com.squad.musicmatters.core.ui

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.collectIsDraggedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import androidx.compose.ui.unit.dp
import com.squad.musicmatters.core.datastore.DefaultPreferences
import com.squad.musicmatters.core.designsystem.theme.MusicMattersTheme
import com.squad.musicmatters.core.model.Lyric
import com.squad.musicmatters.core.model.ThemeMode
import com.squad.musicmatters.core.i8n.R as i8nR
import kotlinx.coroutines.delay
import java.time.Duration
import kotlin.math.abs
import kotlin.time.Duration.Companion.milliseconds

private const val LyricsManualScrollTimeout = 3_000L
private const val LyricsPendingSeekTimeout = 5_000L

private const val LyricsSeekToleranceMillis = 1_500L

@Composable
fun LyricsViewContent(
    modifier: Modifier = Modifier,
    lyrics: List<Lyric>,
    currentDurationInPlayback: Duration,
    blurColor: Color = CardDefaults.cardColors().containerColor,
    onSeekTo: ( Duration ) -> Unit,
) {
    val listState = rememberLazyListState()

    var clickedIndex by remember {
        mutableStateOf<Int?>( null )
    }

    var isAutoScrollEnabled by remember {
        mutableStateOf( true )
    }

    /*
     * The clicked lyric takes priority while a seek is pending.
     *
     * Otherwise, select the most recent lyric whose timestamp
     * is less than or equal to the playback position.
     *
     * This assumes lyrics are sorted by ascending timestamp.
     */
    val activeIndex = remember(
        lyrics,
        clickedIndex,
        currentDurationInPlayback,
    ) {
        clickedIndex ?: lyrics.indexOfLast { lyric ->
            lyric.timeStamp <= currentDurationInPlayback
        }
    }

    /*
     * Detect when playback reaches the pending seek target.
     *
     * Checking the absolute difference also works for seeking
     * backwards, unlike checking only whether playback has
     * passed the target timestamp.
     */
    LaunchedEffect(
        clickedIndex,
        currentDurationInPlayback,
        lyrics,
    ) {
        val index = clickedIndex ?: return@LaunchedEffect
        val lyric = lyrics.getOrNull( index ) ?: return@LaunchedEffect

        val differenceMillis = abs(
            currentDurationInPlayback.toMillis() -
                    lyric.timeStamp.toMillis()
        )

        if ( differenceMillis <= LyricsSeekToleranceMillis ) {
            clickedIndex = null
        }
    }

    /*
     * Prevent a failed or unresponsive seek from leaving
     * the clicked lyric highlighted indefinitely.
     *
     * Cancelling/restarting this effect when clickedIndex
     * changes ensures the latest selection gets its own timeout.
     */
    LaunchedEffect( clickedIndex, lyrics ) {
        val index = clickedIndex ?: return@LaunchedEffect

        delay( LyricsPendingSeekTimeout.milliseconds )

        if ( clickedIndex == index ) {
            clickedIndex = null
        }
    }

    /*
     * Detect direct user dragging. Temporarily suspend automatic
     * synchronization so playback does not fight manual scrolling.
     */
    val isDragged by listState.interactionSource.collectIsDraggedAsState()

    LaunchedEffect( isDragged ) {
        if ( isDragged ) {
            isAutoScrollEnabled = false
        } else {
            delay( LyricsManualScrollTimeout.milliseconds )
            isAutoScrollEnabled = true
        }
    }

    LyricsScrollEffect(
        listState = listState,
        activeIndex = activeIndex,
        syncEnabled = isAutoScrollEnabled,
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(blurColor),
        contentAlignment = Alignment.Center,
    ) {
        if ( lyrics.isEmpty() ) {
            Text(
                text = stringResource( id = i8nR.string.core_i8n_no_lyrics_found ),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(
                    alpha = 0.6f,
                ),
            )
        } else {
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer {
                        /*
                         * Offscreen compositing ensures the
                         * destination-in gradients fade the
                         * rendered list rather than individual
                         * drawing operations.
                         */
                        compositingStrategy =
                            CompositingStrategy.Offscreen
                    }
                    .drawWithContent {
                        drawContent()

                        val fadeHeight = 48.dp.toPx()
                            .coerceAtMost( size.height / 2f )

                        // Fade the top edge.
                        drawRect(
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    Color.Black,
                                ),
                                startY = 0f,
                                endY = fadeHeight,
                            ),
                            blendMode = BlendMode.DstIn,
                        )

                        // Fade the bottom edge.
                        drawRect(
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    Color.Black,
                                    Color.Transparent,
                                ),
                                startY = size.height - fadeHeight,
                                endY = size.height,
                            ),
                            blendMode = BlendMode.DstIn,
                        )
                    },
                contentPadding = PaddingValues(
                    horizontal = 24.dp,
                    vertical = 32.dp,
                ),
                verticalArrangement = Arrangement.spacedBy( 16.dp ),
            ) {
                itemsIndexed(
                    items = lyrics,
                    key = { index, _ ->
                        /*
                         * Use a stable lyric ID here instead if
                         * your Lyric model provides one.
                         *
                         * Index keys assume the list retains
                         * its ordering while playback continues.
                         */
                        index
                    },
                ) { index, lyric ->
                    LyricItem(
                        lyric = lyric,
                        isActive = index == activeIndex,
                        onClick = {
                            clickedIndex = index
                            onSeekTo( lyric.timeStamp )
                        },
                    )
                }
            }
        }
    }
}

@Composable
private fun LyricsScrollEffect(
    listState: LazyListState,
    activeIndex: Int,
    syncEnabled: Boolean = true,
) {
    LaunchedEffect(
        activeIndex,
        syncEnabled,
        listState,
    ) {
        if ( !syncEnabled || activeIndex < 0 ) {
            return@LaunchedEffect
        }

        if ( activeIndex >= listState.layoutInfo.totalItemsCount ) {
            return@LaunchedEffect
        }

        /*
         * Use the actual viewport dimensions rather than
         * assuming a fixed lyric item height.
         */
        val layoutInfo = listState.layoutInfo

        val viewportHeight =
            layoutInfo.viewportEndOffset -
                    layoutInfo.viewportStartOffset

        if ( viewportHeight <= 0 ) {
            /*
             * The list has not been measured yet.
             * Scroll to a nearby item to bring the target
             * into the layout, then let the effect run again
             * when the active index or sync state changes.
             */
            listState.scrollToItem(
                index = ( activeIndex - 2 ).coerceAtLeast( 0 ),
            )
            return@LaunchedEffect
        }

        /*
         * Place the active lyric approximately 30% down
         * the viewport, leaving room for upcoming lines.
         *
         * A negative scroll offset moves the target item
         * down from the start of the viewport.
         */
        val targetOffset = -( viewportHeight * 0.30f ).toInt()

        listState.animateScrollToItem(
            index = activeIndex,
            scrollOffset = targetOffset,
        )
    }
}

@Composable
private fun LyricItem(
    lyric: Lyric,
    isActive: Boolean,
    onClick: () -> Unit,
) {
    val textColor by animateColorAsState(
        targetValue = if ( isActive ) {
            MaterialTheme.colorScheme.primary
        } else {
            MaterialTheme.colorScheme.onSurface
        },
        animationSpec = tween( durationMillis = 300 ),
        label = "textColor"
    )

    val textScale by animateFloatAsState(
        targetValue = if ( isActive ) 1.03f else 1.0f,
        animationSpec = tween( durationMillis = 300 ),
        label = "textScale"
    )
    val textOpacity by animateFloatAsState(
        targetValue = if ( isActive ) 1.0f else 0.35f,
        animationSpec = tween( durationMillis = 300 ),
        label = "textOpacity"
    )

    val textStyle = MaterialTheme.typography.displaySmall.copy( fontWeight = FontWeight.Bold )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clickable( onClick = onClick )
            .padding( vertical = 4.dp )
            .graphicsLayer {
                scaleX = textScale
                scaleY = textScale
                alpha = textOpacity
                transformOrigin = TransformOrigin( 0f, 0.5f )
            },
    ) {
        Text(
            text = lyric.content,
            style = textStyle,
            color = textColor,
        )
    }
}

@PreviewScreenSizes
@Composable
private fun LyricsViewContentPreview() {
    MusicMattersTheme(
        themeMode = ThemeMode.LIGHT,
        primaryColorName = "Blue",
        fontName = DefaultPreferences.FONT_NAME,
        fontScale = DefaultPreferences.FONT_SCALE,
        useMaterialYou = true,
    ) {
        LyricsViewContent(
            lyrics = listOf(
                Lyric(
                    timeStamp = Duration.ofMinutes(1),
                    content = "Sometime say the magic you dey feel inside is like gold",
                ),
                Lyric(
                    timeStamp = Duration.ofMinutes(2),
                    content = "Something like do re mi fa so lat ti do do (Yeah)",
                ),
                Lyric(
                    timeStamp = Duration.ofMinutes(3),
                    content = "Make I sing for you la la do do",
                ),
                Lyric(
                    timeStamp = Duration.ofMinutes(4),
                    content = "Make I sing your song",
                ),
                Lyric(
                    timeStamp = Duration.ofMinutes(5),
                    content = "Make I sing make you wine am do do o",
                ),
            ),
            currentDurationInPlayback = Duration.ofMinutes(3),
            onSeekTo = {},
        )
    }
}
