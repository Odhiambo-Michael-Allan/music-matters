package com.squad.musicmatters.feature.nowplaying.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.width
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.boundsInParent
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.squad.musicmatters.core.ui.FadeTransition
import kotlin.math.absoluteValue

@OptIn( ExperimentalLayoutApi::class )
@Composable
internal fun ArtistsRow(
    artists: Set<String>,
    onArtistClicked: ( String ) -> Unit,
) {

    NowPlayingBottomBarContentText(
        text = artists.joinToString( ", " ),
        style = LocalTextStyle.current.copy(
            color = MaterialTheme.colorScheme.onSurface.copy( alpha = 0.7f ),
            fontWeight = FontWeight.SemiBold
        ),
        textMarquee = true,
        onClick = { onArtistClicked( artists.first() ) },
    )

}