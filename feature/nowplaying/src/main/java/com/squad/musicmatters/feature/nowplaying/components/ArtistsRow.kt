package com.squad.musicmatters.feature.nowplaying.components

import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow

@OptIn( ExperimentalLayoutApi::class )
@Composable
internal fun ArtistsRow(
    artists: Set<String>,
    onArtistClicked: ( String ) -> Unit,
) {
    FlowRow(
        maxLines = 1,
        modifier = Modifier.basicMarquee( Int.MAX_VALUE ),
    ) {
        artists.forEachIndexed { index, it ->
            Text(
                text = it,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface.copy( alpha = 0.7f ),
                modifier = Modifier.pointerInput( Unit ) {
                    detectTapGestures { _ ->
                        onArtistClicked( it )
                    }
                }
            )
            if ( index != artists.size - 1 ) Text( text = ", " )
        }
    }
}