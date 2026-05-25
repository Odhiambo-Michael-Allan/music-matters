package com.squad.musicmatters.feature.nowplaying.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.outlined.ThumbUpAlt
import androidx.compose.material.icons.rounded.ThumbUpAlt
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.squad.musicmatters.core.model.Song
import com.squad.musicmatters.core.ui.FadeTransition

@Composable
internal fun TitleAndArtistSection(
    currentlyPlayingSong: Song,
    currentlyPlayingSongIsFavorite: Boolean,
    onArtistClicked: ( String ) -> Unit,
    onFavorite: ( Song, Boolean ) -> Unit,
    onShowOptionsMenu: () -> Unit,
) {
    Row {
        AnimatedContent(
            modifier = Modifier.weight( 1f ),
            label = "now-playing-body-content",
            targetState = currentlyPlayingSong,
            transitionSpec = {
                FadeTransition.enterTransition()
                    .togetherWith( FadeTransition.exitTransition() )
            }
        ) { target ->
            Column (
                modifier = Modifier
                    .padding( 0.dp, 16.dp )

            ) {
                NowPlayingBottomBarContentText(
                    text = target.title,
                    style = MaterialTheme.typography.titleLarge
                        .copy(fontWeight = FontWeight.Bold),
                    textMarquee = true,
                )

                ArtistsRow(
                    artists = target.artists,
                    onArtistClicked = onArtistClicked
                )
            }
        }
        Row (
            modifier = Modifier.padding( 0.dp, 16.dp )
        ) {
            IconButton(
                modifier = Modifier.offset( 4.dp ),
                onClick = {
                    onFavorite(
                        currentlyPlayingSong,
                        currentlyPlayingSongIsFavorite.not()
                    )
                }
            ) {
                AnimatedContent(
                    targetState = currentlyPlayingSongIsFavorite,
                    label = "now-playing-screen-is-favorite-icon"
                ) {
                    Icon(
                        imageVector = if ( it ) {
                            Icons.Rounded.ThumbUpAlt
                        } else {
                            Icons.Outlined.ThumbUpAlt
                        },
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )

                }
            }
            IconButton(
                onClick = onShowOptionsMenu
            ) {
                Icon(
                    imageVector = Icons.Filled.MoreVert,
                    contentDescription = null
                )
            }
        }
    }
}