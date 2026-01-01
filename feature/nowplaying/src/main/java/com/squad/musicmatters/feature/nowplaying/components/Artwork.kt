package com.squad.musicmatters.feature.nowplaying.components

import android.net.Uri
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import com.squad.musicmatters.core.ui.DynamicAsyncImage
import com.squad.musicmatters.core.ui.FadeTransition

@Composable
internal fun NowPlayingArtwork(
    modifier: Modifier = Modifier,
    artworkUri: Uri?,
    onSwipeLeft: () -> Unit,
    onSwipeRight: () -> Unit,
    onSwipeDown: () -> Unit,
    onArtworkClicked: () -> Unit
) {
    Box(
        modifier = modifier
    ) {
        AnimatedContent(
            modifier = Modifier.align( Alignment.Center ),
            label = "now-playing-artwork",
            targetState = artworkUri,
            transitionSpec = {
                FadeTransition.enterTransition()
                    .togetherWith( FadeTransition.exitTransition() )
            }
        ) {
            DynamicAsyncImage(
                imageUri = it,
                contentDescription = "now-playing-artwork",
                modifier = Modifier
                    .sizeIn( maxWidth = 500.dp, maxHeight = 500.dp )
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