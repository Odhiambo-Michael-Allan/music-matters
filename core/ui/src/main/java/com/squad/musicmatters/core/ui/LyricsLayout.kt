package com.squad.musicmatters.core.ui

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.squad.musicmatters.core.datastore.DefaultPreferences
import com.squad.musicmatters.core.designsystem.component.DevicePreviews
import com.squad.musicmatters.core.designsystem.theme.MusicMattersTheme
import com.squad.musicmatters.core.model.Lyric
import kotlinx.coroutines.launch
import java.time.Duration

@Composable
fun LyricsLayout(
    modifier: Modifier = Modifier,
    lyrics: List<Lyric>,
    currentDurationInPlayback: Duration,
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
        LazyColumn(
            state = scrollState,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            itemsIndexed( lyrics ) { index, lyric ->
                val isActive = index == currentLyricIndex

                val scale by animateFloatAsState(
                    targetValue = if ( isActive ) 1.15f else 1f,
                    animationSpec = spring( dampingRatio = Spring.DampingRatioLowBouncy ),
                    label = "LyricScale"
                )

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
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.headlineMedium,
                    color = color,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding( vertical = 12.dp, horizontal = 24.dp )
                        .pointerInput( Unit ) {
                            detectTapGestures { onSeekTo( lyric.timeStamp ) }
                        }
                        .graphicsLayer {
                            scaleX = scale
                            scaleY = scale
                            alpha = blurAlpha
                        }
                )
            }
        }
        Box(
            modifier = Modifier
                .align( Alignment.BottomStart )
                .fillMaxWidth()
                .height( 16.dp )
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf( Color.Transparent, MaterialTheme.colorScheme.background )
                    )
                )
        )
    }
}

@Composable
private fun BottomGradientEdge(
    color: Color,
    isBottom: Boolean = true
) {

}

@DevicePreviews
@Composable
private fun LyricsLayoutPreview() {
    MusicMattersTheme(
        themeMode = DefaultPreferences.THEME_MODE,
        fontName = DefaultPreferences.FONT_NAME,
        primaryColorName = DefaultPreferences.PRIMARY_COLOR_NAME,
        useMaterialYou = true,
        fontScale = DefaultPreferences.FONT_SCALE
    ) {
        LyricsLayout(
            modifier = Modifier.padding( 24.dp ),
            lyrics = listOf(
                Lyric(
                    timeStamp = Duration.ofMinutes( 1 ),
                    content = "Sometime say the magic you dey feel inside is like gold"
                ),
                Lyric(
                    timeStamp = Duration.ofMinutes( 2 ),
                    content = "Something like do re mi fa so lat ti do do (Yeah)"
                ),
                Lyric(
                    timeStamp = Duration.ofMinutes( 3 ),
                    content = "Make I sing for you la la do do"
                ),
                Lyric(
                    timeStamp = Duration.ofMinutes( 4 ),
                    content = "Make I sing your song"
                ),
                Lyric(
                    timeStamp = Duration.ofMinutes( 5 ),
                    content = "Make I sing make you wine am do do o"
                )
            ),
            currentDurationInPlayback = Duration.ofMinutes( 2L ),
            onSeekTo = {}
        )
    }
}