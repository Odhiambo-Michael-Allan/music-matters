package com.squad.musicmatters.feature.player.components

import android.annotation.SuppressLint
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.squad.musicmatters.core.i8n.R
import com.squad.musicmatters.core.media.connection.PlaybackPosition
import java.util.Locale
import kotlin.time.Duration
import kotlin.time.DurationUnit
import kotlin.time.toDuration

@Composable
internal fun NowPlayingSeekBar(
    colorScheme: ColorScheme,
    onGetPlaybackPosition: () -> PlaybackPosition,
    onSeekStart: () -> Unit,
    onSeekEnd: ( Long ) -> Unit,
) {
    var seekRatio by remember { mutableStateOf<Float?>( null ) }
    var isDragging by remember { mutableStateOf( false ) }

    Column (
        modifier = Modifier.fillMaxWidth()
    ) {
        Column (
            modifier = Modifier.fillMaxWidth()
        ) {
            NowPlayingSeekBar(
                colorScheme = colorScheme,
                onGetPlaybackPosition = onGetPlaybackPosition,
                isDragging = { isDragging },
                onSetDragging = { isDragging = it },
                onSeekStart = {
                    seekRatio = 0f
                    onSeekStart()
                },
                onSeek = { seekRatio = it },
                onSeekEnd = {
                    onSeekEnd( ( it * onGetPlaybackPosition().total ).toLong() )
                    seekRatio = null
                },
                onSeekCancel = { seekRatio = null }
            )
            PlayDurationLabel(
                totalDuration = { onGetPlaybackPosition().total },
                playedDuration = {
                    seekRatio?.let { it * onGetPlaybackPosition().total }?.toLong()
                        ?: onGetPlaybackPosition().played
                },
                sleepTimerDurationLeft = {
                    onGetPlaybackPosition().sleepTimerDurationLeft
                },
                isDragging = { isDragging }
            )
        }
        Spacer( modifier = Modifier.height( 12.dp ) )
    }
}

private val SeekBarHeight = 24.dp
private val SeekBarCornerRadius = 6.dp

@Composable
private fun NowPlayingSeekBar(
    modifier: Modifier = Modifier,
    colorScheme: ColorScheme,
    onGetPlaybackPosition: () -> PlaybackPosition,
    isDragging: () -> Boolean,
    onSetDragging: ( Boolean ) -> Unit,
    onSeekStart: () -> Unit,
    onSeek: ( Float ) -> Unit,
    onSeekEnd: ( Float ) -> Unit,
    onSeekCancel: () -> Unit,
) {
    val currentOnSetDragging by rememberUpdatedState( onSetDragging )
    val currentOnSeekStart by rememberUpdatedState( onSeekStart )
    val currentOnSeek by rememberUpdatedState( onSeek )
    val currentOnSeekEnd by rememberUpdatedState( onSeekEnd )
    val currentOnSeekCancel by rememberUpdatedState( onSeekCancel )

    var dragRatio by remember { mutableFloatStateOf( 0f ) }

    // Keep this as State<Dp> (no `by`) so it is only read in the draw phase.
    val trackHeight = animateDpAsState(
        targetValue = if ( isDragging() ) 12.dp else 8.dp,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioLowBouncy,
            stiffness = Spring.StiffnessLow,
        ),
        label = "seekBarTrackHeight",
    )

    val trackColor = colorScheme.surfaceVariant
    val playedColor = colorScheme.primary
    val bufferedColor = playedColor.copy( alpha = 0.2f )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height( SeekBarHeight )
            .drawBehind {
                val trackHeightPx = trackHeight.value.toPx()
                val top = ( size.height - trackHeightPx ) / 2f
                val radius = CornerRadius( SeekBarCornerRadius.toPx() )

                fun bar( color: Color, fraction: Float ) {
                    val width = size.width * fraction.coerceIn( 0f, 1f )
                    if ( width > 0f ) {
                        drawRoundRect(
                            color = color,
                            topLeft = Offset( 0f, top ),
                            size = Size( width, trackHeightPx ),
                            cornerRadius = radius,
                        )
                    }
                }

                val position = onGetPlaybackPosition()
                val playedRatio = if ( isDragging() ) dragRatio else position.playedRatio

                bar( trackColor, 1f )
                bar( playedColor, playedRatio )
                bar( bufferedColor, position.bufferedRatio )
            }
            .pointerInput( Unit ) {
                detectTapGestures { offset ->
                    val width = size.width.toFloat().coerceAtLeast( 1f )
                    currentOnSeekStart()
                    currentOnSeekEnd( ( offset.x / width ).coerceIn( 0f, 1f ) )
                }
            }
            .pointerInput( Unit ) {
                var offsetX = 0f
                detectHorizontalDragGestures(
                    onDragStart = { offset ->
                        val width = size.width.toFloat().coerceAtLeast( 1f )
                        offsetX = offset.x
                        dragRatio = ( offsetX / width ).coerceIn( 0f, 1f )
                        currentOnSetDragging( true )
                        currentOnSeekStart()
                    },
                    onDragEnd = {
                        currentOnSeekEnd( dragRatio )
                        currentOnSetDragging( false )
                    },
                    onDragCancel = {
                        currentOnSeekCancel()
                        currentOnSetDragging( false )
                    },
                    onHorizontalDrag = { change, dragAmount ->
                        change.consume()
                        val width = size.width.toFloat().coerceAtLeast( 1f )
                        offsetX += dragAmount
                        dragRatio = ( offsetX / width ).coerceIn( 0f, 1f )
                        currentOnSeek( dragRatio )
                    },
                )
            }
    )
}
@Composable
private fun PlayDurationLabel(
    totalDuration: () -> Long,
    playedDuration: () -> Long,
    sleepTimerDurationLeft: () -> Duration?,
    isDragging: () -> Boolean,
) {
    val textSize by animateDpAsState(
        targetValue = if ( isDragging() ) 16.dp else 13.dp,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioLowBouncy,
            stiffness = Spring.StiffnessLow
        )
    )

    Box(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = formatPlayDuration(
                duration = playedDuration().toDuration( DurationUnit.MILLISECONDS )
            ),
            style = LocalTextStyle.current.copy(
                fontSize = textSize.value.sp,
                fontWeight = FontWeight.SemiBold
            )
        )
        sleepTimerDurationLeft()?.let {
            Text(
                text = stringResource(
                    id = R.string.core_i8n_sleep_timer_duration_left,
                    formatPlayDuration( duration = it )
                ),
                style = LocalTextStyle.current.copy(
                    fontSize = 10.sp,
                    fontWeight = FontWeight.SemiBold
                ),
                modifier = Modifier.align( Alignment.Center ),
            )
        }
        Text(
            text = "-${formatPlayDuration(
                duration = totalDuration().toDuration( DurationUnit.MILLISECONDS )
                    .minus( playedDuration().toDuration( DurationUnit.MILLISECONDS ) )
            )}",
            style = LocalTextStyle.current.copy(
                fontSize = textSize.value.sp,
                fontWeight = FontWeight.SemiBold
            ),
            modifier = Modifier.align( Alignment.CenterEnd ),
        )
    }
}



@Composable
fun formatPlayDuration( duration: Duration ): String =
    duration.toComponents { hours, minutes, seconds, _ ->
        when {
            hours > 0 -> String.format( Locale.getDefault(), "%d:%02d:%02d", hours, minutes, seconds )
            else -> String.format( Locale.getDefault(), "%d:%02d", minutes, seconds )
        }
    }