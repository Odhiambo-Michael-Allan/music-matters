package com.squad.musicmatters.feature.foryou

import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.LargeFloatingActionButton
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.squad.musicmatters.core.designsystem.R
import com.squad.musicmatters.core.data.utils.subListNonStrict
import com.squad.musicmatters.core.datastore.DefaultPreferences
import com.squad.musicmatters.core.designsystem.component.MusicMattersIcons
import com.squad.musicmatters.core.designsystem.theme.MusicMattersTheme
import com.squad.musicmatters.core.designsystem.theme.SupportedFonts
import com.squad.musicmatters.core.model.Song
import com.squad.musicmatters.core.ui.DynamicAsyncImage
import com.squad.musicmatters.core.ui.MusicMattersPreviewParametersProvider
import com.squad.musicmatters.core.ui.PreviewData
import com.squad.musicmatters.core.ui.Tile
import com.squad.musicmatters.core.i8n.R as i8nR

@Composable
internal fun ForYouScreen(
    viewModel: ForYouScreenViewModel = hiltViewModel(),
    onViewAlbum: ( Long ) -> Unit,
    onViewArtist: ( Long ) -> Unit,
) {
    
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    ForYouScreenContent(
        uiState = uiState,
        onViewAlbum = onViewAlbum,
        onViewArtist = onViewArtist,
        onPlaySong = viewModel::playSongs,
        onShuffleAndPlay = viewModel::shuffleAndPlay,
    )
    
}

@OptIn( ExperimentalMaterial3ExpressiveApi::class )
@Composable
private fun ForYouScreenContent(
    uiState: ForYouScreenUiState,
    onPlaySong: ( Song, List<Song> ) -> Unit,
    onShuffleAndPlay: ( List<Song> ) -> Unit,
    onViewAlbum: ( Long ) -> Unit,
    onViewArtist: ( Long ) -> Unit,
) {

    val context = LocalContext.current

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
            val showEmptyLayout = uiState.recentlyAddedSongs.isEmpty() &&
                    uiState.suggestedAlbums.isEmpty() &&
                    uiState.mostPlayedSongs.isEmpty() &&
                    uiState.suggestedArtists.isEmpty() &&
                    uiState.recentlyPlayedSongs.isEmpty()

            Box(
                modifier = Modifier.fillMaxSize()
            ) {
                if ( showEmptyLayout ) {
                    Text(
                        text = stringResource( id = i8nR.string.core_i8n_damn_this_is_so_empty ),
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.align( Alignment.Center )
                    )
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues( bottom = 100.dp ),
                    ) {
                        item {
                            Spacer( Modifier.height( 8.dp ) )
                            if ( uiState.recentlyAddedSongs.isNotEmpty() ) {
                                ForYouSongRow(
                                    heading = stringResource( id = i8nR.string.core_i8n_recently_added_songs ),
                                    songs = uiState.recentlyAddedSongs.subListNonStrict( 10 ),
                                    onPlaySong = onPlaySong,
                                )
                            }
                            if ( uiState.suggestedAlbums.isNotEmpty() ) {
                                Spacer( Modifier.height( 8.dp ) )
                                SideHeading {
                                    Text(
                                        text = stringResource( id = i8nR.string.core_i8n_suggested_albums )
                                    )
                                }
                                Spacer( Modifier.height( 8.dp ) )
                                ForYouTileRow(
                                    items = uiState.suggestedAlbums,
                                    onGetItemKey = { it.id.toString() },
                                    onGetTitle = { it.title },
                                    onGetDescription = {
                                        it.artist ?: context.getString( i8nR.string.core_i8n_untitled )
                                    },
                                    onGetArtworkUri = { it.artworkUri?.toUri() },
                                    onViewItem = { onViewAlbum( it.id ) },
                                    onPlay = {
                                        val songsInAlbum = uiState.recentlyAddedSongs.filter { song ->
                                            song.albumId == it.id
                                        }
                                        onPlaySong( songsInAlbum.first(), songsInAlbum )
                                    },
                                )
                            }
                            AnimatedVisibility(
                                visible = uiState.mostPlayedSongs.isNotEmpty()
                            ) {
                                Spacer( Modifier.height( 8.dp ) )
                                ForYouSongRow(
                                    heading = stringResource( id = i8nR.string.core_i8n_most_played_songs ),
                                    songs = uiState.mostPlayedSongs,
                                    onPlaySong = onPlaySong,
                                )
                            }

                            if ( uiState.suggestedArtists.isNotEmpty() ) {
                                Spacer( Modifier.height( 8.dp ) )
                                SideHeading {
                                    Text(
                                        text = stringResource( id = i8nR.string.core_i8n_suggested_artists )
                                    )
                                }
                                Spacer( Modifier.height( 8.dp ) )
                                ForYouTileRow(
                                    items = uiState.suggestedArtists,
                                    onGetItemKey = { it.id.toString() },
                                    onGetTitle = { it.name },
                                    onGetDescription = {
                                        context.getString(
                                            if ( it.trackCount > 1 ) {
                                                i8nR.string.core_i8n_n_songs
                                            } else {
                                                i8nR.string.core_i8n_one_song
                                            },
                                            it.trackCount
                                        )
                                    },
                                    onGetArtworkUri = { it.artworkUri?.toUri() },
                                    onViewItem = { onViewArtist( it.id ) },
                                    onPlay = {
                                        val songsByArtist = uiState.recentlyAddedSongs.filter { song ->
                                            song.artistId == it.id
                                        }
                                        onPlaySong( songsByArtist.first(), songsByArtist )
                                    },
                                )
                            }
                            AnimatedVisibility(
                                visible = uiState.recentlyPlayedSongs.isNotEmpty()
                            ) {
                                ForYouSongRow(
                                    heading = stringResource( id = i8nR.string.core_i8n_recently_played_songs ),
                                    songs = uiState.recentlyPlayedSongs,
                                    onPlaySong = onPlaySong,
                                )
                            }
                        }
                    }
                    val animatedBottomPadding by animateDpAsState(
                        targetValue = if ( uiState.currentlyPlayingSongId.isNotBlank() ) {
                            60.dp
                        } else {
                            0.dp
                        },
                        label = "BottomPaddingAnimation"
                    )
                    Box(
                        Modifier
                            .align( Alignment.BottomEnd )
                            .padding( bottom = animatedBottomPadding )
                    ) {
                        LargeFloatingActionButton(
                            modifier = Modifier.padding( 16.dp ),
                            containerColor = MaterialTheme.colorScheme.secondaryContainer,
                            onClick = { onShuffleAndPlay( uiState.recentlyAddedSongs ) }
                        ) {
                            Icon(
                                painter = painterResource( id = R.drawable.ic_shuffle ),
                                contentDescription = null,
                                modifier = Modifier.size(
                                    MusicMattersIcons.Shuffle.defaultHeight.minus( 5.dp )
                                )
                            )
                        }

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
        Spacer( Modifier.height( 16.dp ) )
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues( 12.dp, 0.dp ),
            horizontalArrangement = Arrangement.spacedBy( 12.dp )
        ) {
            items(
                songs,
                key = { it.id }
            ) {
                ForYouSongCard(
                    modifier = Modifier
                        .width(300.dp)
                        .height(96.dp)
                        .animateItem(),
                    song = it,
                    onClick = { onPlaySong( it, songs ) }
                )
            }
        }
    }
}

@Composable
private fun <T> ForYouTileRow(
    modifier: Modifier = Modifier,
    items: List<T>,
    onGetArtworkUri: ( T ) -> Uri?,
    onGetTitle: ( T ) -> String,
    onGetDescription: ( T ) -> String,
    onViewItem: ( T ) -> Unit,
    onGetItemKey: ( T ) -> String,
    onPlay: ( T ) -> Unit,
) {
    LazyRow (
        modifier = modifier.fillMaxWidth(),
        contentPadding = PaddingValues( 12.dp, 0.dp ),
        horizontalArrangement = Arrangement.spacedBy( 12.dp )
    ) {
        items(
            items = items,
            key = onGetItemKey
        ) {
            Tile(
                modifier = Modifier
                    .width(150.dp)
                    .animateItem(),
                imageUri = onGetArtworkUri( it ),
                onPlay = { onPlay( it ) },
                onClick = { onViewItem( it ) },
                content = {
                    Text(
                        text = onGetTitle( it ),
                        fontWeight = FontWeight.SemiBold,
                        textAlign = TextAlign.Center,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = onGetDescription( it ),
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

@Composable
private fun SideHeading( text: @Composable () -> Unit ) {
    Box (
        modifier = Modifier.padding( 12.dp, 0.dp )
    ) {
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
                modifier = Modifier.fillMaxSize(),
                imageUri = song.artworkUri?.toUri(),
                contentScale = ContentScale.FillWidth,
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
                            .clip(RoundedCornerShape(8.dp)),
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
                recentlyAddedSongs = emptyList(),
//                    previewData.songs,
                suggestedAlbums = emptyList(),
//                    previewData.albums,
                mostPlayedSongs = emptyList(),
//                    previewData.songs,
                suggestedArtists = emptyList(),
//                    previewData.artists,
                recentlyPlayedSongs = emptyList(),
//                    previewData.songs,
                currentlyPlayingSongId = "",
            ),
            onPlaySong = { _, _ -> },
            onShuffleAndPlay = {},
            onViewAlbum = {},
            onViewArtist = {},
        )
    }
}