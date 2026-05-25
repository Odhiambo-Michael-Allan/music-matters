package com.squad.musicmatters.feature.nowplaying.components

import android.annotation.SuppressLint
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.squad.musicMatters.core.i8n.R
import com.squad.musicmatters.core.media.connection.PlaybackPosition
import java.util.Locale
import kotlin.text.toLong
import kotlin.time.Duration
import kotlin.time.DurationUnit
import kotlin.time.toDuration

@Composable
internal fun NowPlayingSeekBar(
    onGetPlaybackPosition: () -> PlaybackPosition,
    onSeekStart: () -> Unit,
    onSeekEnd: ( Long ) -> Unit,
) {
    var seekRatio by remember { mutableStateOf<Float?>( null ) }
    var isDragging by remember { mutableStateOf( false ) }

    Column (
        modifier = Modifier.fillMaxWidth()
    ) {
//        Spacer( modifier = Modifier.height( 12.dp ) )
        Column (
            modifier = Modifier.fillMaxWidth()
        ) {
            NowPlayingSeekBar(
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

@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
private fun NowPlayingSeekBar(
    modifier: Modifier = Modifier,
    onGetPlaybackPosition: () -> PlaybackPosition,
    isDragging: () -> Boolean,
    onSetDragging: ( Boolean ) -> Unit,
    onSeekStart: () -> Unit,
    onSeek: ( Float ) -> Unit,
    onSeekEnd: ( Float ) -> Unit,
    onSeekCancel: () -> Unit,
) {

    var dragRatio by remember { mutableFloatStateOf( 0f ) }

    val sliderHeight = 24.dp
    val thumbSize = 12.dp
    val thumbSizeHalf = thumbSize.div( 2 )
    val trackHeight by animateDpAsState(
        targetValue = if ( isDragging() ) 12.dp else 8.dp,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioLowBouncy,
            stiffness = Spring.StiffnessLow
        )
    )



    BoxWithConstraints(
        modifier = modifier
            .fillMaxWidth()
            .height(sliderHeight),
        contentAlignment = Alignment.Center,
    ) {
        val sliderWidth = maxWidth

        Box(
            modifier = Modifier
                .height(sliderHeight)
                .fillMaxWidth()
                .pointerInput(Unit) {
                    detectTapGestures(
                        onTap = { offset ->
                            onSeekStart()
                            val tapRatio = (offset.x / sliderWidth.toPx())
                                .coerceIn(0f..1f)
                            onSeekEnd(tapRatio)
                        }
                    )
                }
                .pointerInput(Unit) {
                    var offsetX = 0f
                    detectHorizontalDragGestures(
                        onDragStart = { offset ->
                            offsetX = offset.x
                            onSetDragging(true)
                            onSeekStart()
                        },
                        onDragEnd = {
                            onSeekEnd(dragRatio)
                            offsetX = 0f
                            onSetDragging(false)
                            dragRatio = 0f
                        },
                        onDragCancel = {
                            onSeekCancel()
                            offsetX = 0f
                            onSetDragging(false)
                            dragRatio = 0f
                        },
                        onHorizontalDrag = { pointer, dragAmount ->
                            pointer.consume()
                            offsetX += dragAmount
                            dragRatio = (offsetX / sliderWidth.toPx())
                                .coerceIn(0f..1f)
                            onSeek(dragRatio)
                        }
                    )
                }
        )
        Box(
            modifier = Modifier
                .height(trackHeight)
                .fillMaxWidth()
                .background(
                    MaterialTheme.colorScheme.surfaceVariant,
                    RoundedCornerShape(thumbSizeHalf)
                )
        ) {
            Box(
                modifier = Modifier
                    .height(trackHeight)
                    .fillMaxWidth(
                        if (isDragging()) {
                            dragRatio
                        } else {
                            onGetPlaybackPosition().playedRatio
                        }
                    )
                    .background(
                        MaterialTheme.colorScheme.primary,
                        RoundedCornerShape(thumbSizeHalf)
                    )
            )
            Box(
                modifier = Modifier
                    .height(trackHeight)
                    .fillMaxWidth(onGetPlaybackPosition().bufferedRatio)
                    .background(
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
                        RoundedCornerShape(thumbSizeHalf)
                    )
            )
        }
    }

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