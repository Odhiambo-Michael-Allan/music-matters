package com.squad.musicmatters.feature.foryou

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import com.squad.musicmatters.core.datastore.DefaultPreferences
import com.squad.musicmatters.core.designsystem.component.MusicMattersIcons
import com.squad.musicmatters.core.designsystem.theme.MusicMattersTheme
import com.squad.musicmatters.core.designsystem.theme.SupportedFonts
import com.squad.musicmatters.core.model.Song
import com.squad.musicmatters.core.ui.DynamicAsyncImage
import com.squad.musicmatters.core.ui.GenericTile
import com.squad.musicmatters.core.ui.MusicMattersPreviewParametersProvider
import com.squad.musicmatters.core.ui.PreviewData
import com.squad.musicmatters.core.ui.Tile
import com.squad.musicmatters.core.i8n.R as i8nR

@OptIn( ExperimentalMaterial3ExpressiveApi::class )
@Composable
private fun ForYouScreenContent(
    uiState: ForYouScreenUiState,
    onPlaySong: ( Song, List<Song> ) -> Unit,
) {

    when ( uiState ) {
        ForYouScreenUiState.Loading -> {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxSize()
            ) {
                LoadingIndicator()
            }
        }
        is ForYouScreenUiState.Success -> {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {
                ForYouSongRow(
                    heading = stringResource( id = i8nR.string.core_i8n_recently_added_songs ),
                    songs = uiState.recentlyAddedSongs,
                    onPlaySong = onPlaySong,
                )
                SideHeading {
                    Text(
                        text = stringResource( id = i8nR.string.core_i8n_suggested_albums )
                    )
                }
                LazyRow (
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues( 8.dp, 0.dp )
                ) {
                    items(
                        uiState.suggestedAlbums,
                        key = { it.id }
                    ) {
                        Tile(
                            modifier = Modifier.width( 200.dp ),
                            imageUri = it.artworkUri?.toUri(),
                            onPlay = {},
                            onClick = {},
                            content = {
                                Text(
                                    text = it.title,
                                    fontWeight = FontWeight.SemiBold,
                                    textAlign = TextAlign.Center,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = it.artist
                                        ?: stringResource( id = i8nR.string.core_i8n_untitled ),
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        fontWeight = FontWeight.SemiBold,
                                        color = MaterialTheme.colorScheme.onSurface
                                            .copy( alpha = 0.5f )
                                    ),
                                    textAlign = TextAlign.Center,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        )
                    }
                }
            }
        }
    }

}

@Composable
fun ForYouSongRow(
    modifier: Modifier = Modifier,
    heading: String,
    songs: List<Song>,
    onPlaySong: ( Song, List<Song> ) -> Unit,
) {
    Column(
        modifier = modifier
    ) {
        SideHeading {
            Text( text = heading )
        }
        LazyRow(
            modifier = Modifier.fillMaxWidth()
        ) {
            items(
                songs,
                key = { it.id }
            ) {
                ForYouSongCard(
                    modifier = Modifier
                        .width(300.dp)
                        .height(96.dp),
                    song = it,
                    onClick = { onPlaySong( it, songs ) }
                )
            }
        }
    }
}

@Composable
private fun SideHeading( text: @Composable () -> Unit ) {
    Box {
        ProvideTextStyle(
            value = MaterialTheme.typography.headlineSmall.copy(
                fontWeight = FontWeight.Bold
            )
        ) {
            text()
        }
    }
}

@Composable
fun ForYouSongCard(
    modifier: Modifier = Modifier,
    song: Song,
    onClick: () -> Unit
) {

    val backgroundColor = MaterialTheme.colorScheme.surface

    ElevatedCard(
        modifier = modifier,
        onClick = onClick
    ) {
        Box {
            DynamicAsyncImage(
                modifier = Modifier.matchParentSize(),
                imageUri = song.artworkUri?.toUri(),
                contentDescription = null,
            )
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(
                                backgroundColor.copy(alpha = 0.2f),
                                backgroundColor.copy(alpha = 0.7f),
                                backgroundColor.copy(alpha = 0.8f),
                            )
                        )
                    )
            )
            Row(
                modifier = Modifier.padding( 8.dp )
            ) {
                Box {
                    DynamicAsyncImage(
                        modifier = Modifier
                            .aspectRatio(1f)
                            .fillMaxHeight()
                            .clip(RoundedCornerShape(4.dp)),
                        imageUri = song.artworkUri?.toUri(),
                        contentDescription = null,
                    )
                    Box(
                        modifier = Modifier.matchParentSize(),
                        contentAlignment = Alignment.Center,
                    ) {
                        Box(
                            modifier = Modifier
                                .background(
                                    backgroundColor.copy(alpha = 0.25f),
                                    CircleShape
                                )
                                .padding(1.dp)
                        ) {
                            Icon(
                                modifier = Modifier.size( 20.dp ),
                                imageVector = MusicMattersIcons.Play,
                                contentDescription = null
                            )
                        }
                    }
                }
                Column(
                    modifier = Modifier
                        .padding(8.dp, 0.dp)
                        .fillMaxSize(),
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = song.title,
                        style = MaterialTheme.typography.titleMedium,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = song.artist,
                        style = MaterialTheme.typography.bodyMedium,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }
        }
    }
}

@PreviewScreenSizes
@Composable
private fun ForYouScreenContentPreview(
    @PreviewParameter( MusicMattersPreviewParametersProvider::class )
    previewData: PreviewData
) {
    MusicMattersTheme(
        fontName = SupportedFonts.ProductSans.name,
        themeMode = DefaultPreferences.THEME_MODE,
        primaryColorName = DefaultPreferences.PRIMARY_COLOR_NAME,
        fontScale = 1.0f,
        useMaterialYou = true
    ) {
        ForYouScreenContent(
            uiState = ForYouScreenUiState.Success(
                recentlyAddedSongs = previewData.songs,
                suggestedAlbums = previewData.albums,
                mostPlayedSongs = previewData.songs,
                suggestedArtists = previewData.artists,
                recentlyPlayedSongs = previewData.songs
            ),
            onPlaySong = { _, _ -> }
        )
    }
}