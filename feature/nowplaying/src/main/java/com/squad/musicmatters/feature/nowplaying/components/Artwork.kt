package com.squad.musicmatters.feature.nowplaying.components

import android.net.Uri
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import com.squad.musicmatters.core.model.Song
import com.squad.musicmatters.core.ui.DynamicAsyncImage

@Composable
internal fun NowPlayingSongArtwork(
    modifier: Modifier = Modifier,
    song: Song,
    normalArtworkSize: Dp = 350.dp,
    expandedArtworkSize: Dp = 400.dp,
    isPlaying: () -> Boolean,
    onSwipeLeft: () -> Unit,
    onSwipeRight: () -> Unit,
    onSwipeDown: () -> Unit,
    onArtworkClicked: () -> Unit
) {

    val imageSize by animateDpAsState(
        targetValue = if ( isPlaying() ) expandedArtworkSize else normalArtworkSize,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioLowBouncy,
            stiffness = Spring.StiffnessLow
        )
    )
    AnimatedContent(
        targetState = song
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = modifier.size( expandedArtworkSize )
        ) {
            ElevatedCard(
                elevation = CardDefaults.elevatedCardElevation(
                    defaultElevation = if ( isPlaying() ) 10.dp else 5.dp
                ),
            ) {
                DynamicAsyncImage(
                    imageUri = it.artworkUri?.toUri(),
                    contentDescription = "now-playing-artwork",
                    modifier = Modifier
                        .sizeIn( maxWidth = imageSize, maxHeight = imageSize )
                        .aspectRatio( 1f )
                        .clip( MaterialTheme.shapes.medium )
                        .swipeable(
                            minimumDragAmount = 100f,
                            onSwipeLeft = onSwipeLeft,
                            onSwipeRight = onSwipeRight,
                            onSwipeDown = onSwipeDown,
                        )
                        .pointerInput(Unit) {
                            detectTapGestures { _ -> onArtworkClicked() }
                        }
                )
            }
        }
    }
}