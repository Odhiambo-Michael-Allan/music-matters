package com.squad.musicmatters.core.ui

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.squad.musicmatters.core.model.Lyric
import kotlin.collections.indexOfLast
import java.time.Duration
import com.squad.musicmatters.core.i8n.R as i8nR

@Composable
fun LyricsLayout(
    modifier: Modifier = Modifier,
    lyrics: List<Lyric>,
    currentDurationInPlayback: Duration,
    blurColor: Color = CardDefaults.cardColors().containerColor,
    onSeekTo: ( Duration ) -> Unit,
) {
    val scrollState = rememberLazyListState()

    // 1. Calculate the active lyric index based on time
    // We use derivedStateOf so this only triggers a recomposition when the index actually changes
    val currentLyricIndex by remember( lyrics, currentDurationInPlayback ) {
        derivedStateOf {
            lyrics.indexOfLast { it.timeStamp <= currentDurationInPlayback }
                .coerceAtLeast( 0 )
        }
    }

    // 2. Scroll whenever the index changes
    LaunchedEffect( currentLyricIndex ) {
        if ( lyrics.isNotEmpty() ) {
            scrollState.animateScrollToItem( currentLyricIndex )
        }
    }

    Box(
        modifier = modifier
    ) {

        if ( lyrics.isEmpty() ) {
            Text(
                text = stringResource( i8nR.string.core_i8n_no_lyrics_found ),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.displaySmall.copy(
                    fontWeight = FontWeight.ExtraBold
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .align( Alignment.Center )
            )
        } else {
            LazyColumn(
                state = scrollState,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                itemsIndexed( lyrics ) { index, lyric ->
                    val isActive = index == currentLyricIndex

                    val color by animateColorAsState(
                        targetValue = if ( isActive ) MaterialTheme.colorScheme.primary else Color.Unspecified,
                        animationSpec = tween( durationMillis = 300 ),
                        label = "LyricColor"
                    )

                    val blurAlpha by animateFloatAsState(
                        targetValue = if ( isActive ) 1f else 0.5f,
                        animationSpec = tween( durationMillis = 300 ),
                        label = "LyricAlpha"
                    )

                    Text(
                        text = lyric.content,
                        style = MaterialTheme.typography.displaySmall.copy(
                            fontWeight = FontWeight.ExtraBold
                        ),
                        color = color,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding( vertical = 12.dp, horizontal = 0.dp )
                            .pointerInput( Unit ) {
                                detectTapGestures { onSeekTo( lyric.timeStamp ) }
                            }.graphicsLayer { alpha = blurAlpha }
                    )
                }
            }
        }
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height( 12.dp )
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                blurColor,
                                Color.Transparent
                            ),
                            tileMode = TileMode.Mirror
                        )
                    )
            )
            Spacer( modifier = Modifier.weight( 1f ) )
            Box (
                modifier = Modifier
                    .fillMaxWidth()
                    .height( 150.dp )
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                blurColor
                            )
                        )
                    )
            )
        }
    }
}