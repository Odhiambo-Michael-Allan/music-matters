package com.squad.musicmatters.feature.nowplaying.components

import android.annotation.SuppressLint
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.squad.musicmatters.core.media.connection.PlaybackPosition

@Composable
internal fun MusicMattersSeekBar(
    modifier: Modifier = Modifier,
    playbackPosition: PlaybackPosition,
    durationFormatter: (Long ) -> String,
    onSeekStart: () -> Unit,
    onSeekEnd: ( Long ) -> Unit
) {

    Column( modifier = modifier ) {
        var seekRatio by remember { mutableStateOf<Float?>( null ) }

        MusicMattersSeekBar(
            playbackPosition = playbackPosition,
            onSeekStart = {
                seekRatio = 0f
                onSeekStart()
            },
            onSeek = { seekRatio = it },
            onSeekEnd = {
                onSeekEnd( ( it * playbackPosition.total ).toLong() )
                seekRatio = null
            },
            onSeekCancel = { seekRatio = null }
        )
        Spacer( modifier = Modifier.height( 2.dp ) )
        Row (
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            NowPlayingPlaybackPositionText(
                durationFormatter = durationFormatter,
                duration = seekRatio?.let { it * playbackPosition.total }?.toLong()
                    ?: playbackPosition.played,
                alignment = Alignment.CenterStart
            )

            NowPlayingPlaybackPositionText(
                durationFormatter = durationFormatter,
                duration = playbackPosition.total,
                alignment = Alignment.CenterEnd
            )
        }
    }
}

@SuppressLint( "UnusedBoxWithConstraintsScope" )
@Composable
private fun MusicMattersSeekBar(
    playbackPosition: PlaybackPosition,
    onSeekStart: () -> Unit,
    onSeek: ( Float ) -> Unit,
    onSeekEnd: (Float ) -> Unit,
    onSeekCancel: () -> Unit
) {
    val sliderHeight = 12.dp
    val thumbSize = 12.dp
    val thumbSizeHalf = thumbSize.div( 2 )
    val trackHeight = 4.dp

    var dragging by remember { mutableStateOf( false ) }
    var dragRatio by remember { mutableFloatStateOf( 0f ) }

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxWidth()
            .height(sliderHeight),
        contentAlignment = Alignment.Center
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
                            val tapRatio = (offset.x / sliderWidth.toPx()).coerceIn(0f..1f)
                            onSeekEnd(tapRatio)
                        }
                    )
                }
                .pointerInput(Unit) {
                    var offsetX = 0f
                    detectHorizontalDragGestures(
                        onDragStart = { offset ->
                            offsetX = offset.x
                            dragging = true
                            onSeekStart()
                        },
                        onDragEnd = {
                            onSeekEnd(dragRatio)
                            offsetX = 0f
                            dragging = false
                            dragRatio = 0f
                        },
                        onDragCancel = {
                            onSeekCancel()
                            offsetX = 0f
                            dragging = false
                            dragRatio = 0f
                        },
                        onHorizontalDrag = { pointer, dragAmount ->
                            pointer.consume()
                            offsetX += dragAmount
                            dragRatio = (offsetX / sliderWidth.toPx()).coerceIn(0f..1f)
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
                    .fillMaxWidth(playbackPosition.bufferedRatio)
                    .background(
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                        RoundedCornerShape(thumbSizeHalf)
                    )
            )
            Box(
                modifier = Modifier
                    .height(trackHeight)
                    .fillMaxWidth(if (dragging) dragRatio else playbackPosition.playedRatio)
                    .background(
                        MaterialTheme.colorScheme.primary,
                        RoundedCornerShape(thumbSizeHalf)
                    )
            )
        }
        Box( modifier = Modifier.fillMaxWidth() ) {
            Box(
                modifier = Modifier
                    .size(thumbSize)
                    .offset(
                        sliderWidth
                            .minus(thumbSizeHalf.times(2))
                            .times(if (dragging) dragRatio else playbackPosition.playedRatio),
                        0.dp
                    )
                    .background(MaterialTheme.colorScheme.primary, CircleShape)
            )
        }
    }
}

@Composable
private fun NowPlayingPlaybackPositionText(
    duration: Long,
    durationFormatter: ( Long ) -> String,
    alignment: Alignment
) {
    val textStyle = MaterialTheme.typography.labelMedium
    val durationFormatted = durationFormatter( duration )

    Box( contentAlignment = alignment ) {
        Text(
            text = "0".repeat( durationFormatted.length ),
            style = textStyle.copy(
                color = Color.Transparent,
                fontWeight = FontWeight.SemiBold
            )
        )
        Text(
            text = durationFormatted,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.SemiBold
        )
    }
}