package com.squad.musicmatters.feature.player.components

import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontWeight

@OptIn( ExperimentalLayoutApi::class )
@Composable
internal fun ArtistsRow(
    artistId: Long,
    artistName: String,
    colorScheme: ColorScheme,
    onArtistClicked: ( Long ) -> Unit,
) {

    NowPlayingBottomBarContentText(
        text = artistName,
        style = LocalTextStyle.current.copy(
            color = MaterialTheme.colorScheme.onSurface.copy( alpha = 0.7f ),
            fontWeight = FontWeight.SemiBold
        ),
        textMarquee = true,
        colorScheme = colorScheme,
        onClick = { onArtistClicked( artistId ) },
    )

}