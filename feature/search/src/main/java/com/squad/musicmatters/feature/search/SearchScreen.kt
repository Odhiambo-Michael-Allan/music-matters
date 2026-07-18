package com.squad.musicmatters.feature.search

import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.LineHeightStyle.Alignment
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.squad.musicmatters.core.designsystem.component.MusicMattersIcons
import com.squad.musicmatters.core.designsystem.theme.MusicMattersTheme
import com.squad.musicmatters.core.designsystem.theme.PrimaryThemeColors
import com.squad.musicmatters.core.designsystem.theme.SupportedFonts
import com.squad.musicmatters.core.model.Playlist
import com.squad.musicmatters.core.model.SearchFilter
import com.squad.musicmatters.core.model.Song
import com.squad.musicmatters.core.model.SongMetadata
import com.squad.musicmatters.core.model.ThemeMode
import com.squad.musicmatters.core.ui.GenericCard
import com.squad.musicmatters.core.ui.MusicMattersPreviewParametersProvider
import com.squad.musicmatters.core.ui.PreviewData
import com.squad.musicmatters.core.ui.SongCard
import com.squad.musicmatters.core.i8n.R as i8nR

@Composable
internal fun SearchScreen(
    viewModel: SearchScreenViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit,
    onViewAlbum: ( Long ) -> Unit,
    onViewArtist: ( Long ) -> Unit,
    onViewGenre: ( Long ) -> Unit,
    onViewPlaylist: ( String ) -> Unit,
    onShareSong: ( Uri ) -> Unit,
    onDeleteSong: ( Song ) -> Unit,
    onShowSnackBar: ( String ) -> Unit,
) {

    val currentSearchQuery by viewModel.currentSearchQuery.collectAsStateWithLifecycle()
    val currentSearchFilter by viewModel.currentSearchFilter.collectAsStateWithLifecycle()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    SearchScreenContent(
        currentSearchQuery = currentSearchQuery,
        currentlySelectedFilter = currentSearchFilter,
        uiState = uiState,
        onNavigateBack = onNavigateBack,
        onViewAlbum = onViewAlbum,
        onViewArtist = onViewArtist,
        onViewGenre = onViewGenre,
        onViewPlaylist = onViewPlaylist,
        onShareSong = onShareSong,
        onDeleteSong = onDeleteSong,
        onShowSnackBar = onShowSnackBar,
        onQueryChange = viewModel::onSearch,
        onSearchFilterChange = viewModel::onSearchFilterSelected,
        onClearSearchBar = viewModel::onClearSearch,
        onFavorite = viewModel::addToFavorites,
        onPlaySong = viewModel::playSong,
        onPlaySongNext = viewModel::playSongNext,
        onAddSongToQueue = viewModel::addSongToQueue,
        onCreatePlaylist = viewModel::createPlaylist,
        onAddSongsToPlaylist = viewModel::addSongsToPlaylist,
        onRemoveSongFromQueue = viewModel::removeSongFromQueue,
        onSongIsPresentInQueue = viewModel::songIsPresentInQueue,
    )

}

@OptIn( ExperimentalMaterial3ExpressiveApi::class )
@Composable
private fun SearchScreenContent(
    currentSearchQuery: String,
    currentlySelectedFilter: SearchFilter,
    uiState: SearchScreenUiState,
    onNavigateBack: () -> Unit,
    onQueryChange: ( String ) -> Unit,
    onClearSearchBar: () -> Unit,
    onSearchFilterChange: ( SearchFilter ) -> Unit,
    onPlaySong: ( Song ) -> Unit,
    onFavorite: ( Song, Boolean ) -> Unit,
    onViewAlbum: ( Long ) -> Unit,
    onViewArtist: ( Long ) -> Unit,
    onViewGenre: ( Long ) -> Unit,
    onViewPlaylist: ( String ) -> Unit,
    onSongIsPresentInQueue: ( Song ) -> Boolean,
    onAddSongToQueue: (Song ) -> Unit,
    onRemoveSongFromQueue: (Song ) -> Unit,
    onShareSong: ( Uri ) -> Unit,
    onPlaySongNext: (Song ) -> Unit,
    onAddSongsToPlaylist: ( Playlist, List<Song> ) -> Unit,
    onCreatePlaylist: ( String, List<Song> ) -> Unit,
    onDeleteSong: ( Song ) -> Unit,
    onShowSnackBar: ( String ) -> Unit,
) {

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        SearchBar(
            query = currentSearchQuery,
            currentSearchFilter = currentlySelectedFilter,
            onQueryChange = onQueryChange,
            onClear = onClearSearchBar,
            onSearchFilterChange = onSearchFilterChange,
            onNavigateBack = onNavigateBack
        )

        when ( uiState ) {
            SearchScreenUiState.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = androidx.compose.ui.Alignment.Center
                ) {
                    LoadingIndicator()
                }
            }
            is SearchScreenUiState.Success -> {
                if ( uiState.songs.isEmpty() && uiState.albums.isEmpty() &&
                    uiState.artists.isEmpty() && uiState.genres.isEmpty() &&
                    uiState.playlists.isEmpty() ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = androidx.compose.ui.Alignment.Center
                    ) {
                        Text(
                            text = stringResource( id = i8nR.string.core_i8n_no_results ),
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                else {
                    LazyColumn {
                        item {
                            AnimatedVisibility(
                                visible = uiState.songs.isNotEmpty()
                            ) {
                                Column(
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        modifier = Modifier.padding( 12.dp, 4.dp ),
                                        text = stringResource( id = i8nR.string.core_i8n_songs ),
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary,
                                    )
                                    uiState.songs.forEach { song ->
                                        SongCard(
                                            modifier = Modifier.animateItem(),
                                            song = song,
                                            isCurrentlyPlaying =
                                                uiState.currentlyPlayingSongId == song.id,
                                            isFavorite = false,
                                            onGetPlaylists = { uiState.playlists },
                                            onGetSongMetadata = { null },
                                            onClick = { onPlaySong( song ) },
                                            onFavorite = onFavorite,
                                            onPlayNext = onPlaySongNext,
                                            onAddToQueue = onAddSongToQueue,
                                            onViewArtist = onViewArtist,
                                            onViewAlbum = onViewAlbum,
                                            onShareSong = onShareSong,
                                            onAddSongsToPlaylist = onAddSongsToPlaylist,
                                            onCreatePlaylist = onCreatePlaylist,
                                            onDeleteSong = onDeleteSong,
                                            onShowSnackBar = onShowSnackBar,
                                            onRemoveFromQueue = onRemoveSongFromQueue,
                                            onSongIsPresentInQueue = onSongIsPresentInQueue,
                                        )
                                    }
                                }
                            }
                            AnimatedVisibility(
                                visible = uiState.albums.isNotEmpty()
                            ) {
                                Column(
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        modifier = Modifier.padding( 12.dp, 4.dp ),
                                        text = stringResource( id = i8nR.string.core_i8n_albums ),
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary,
                                    )
                                    uiState.albums.forEach { album ->
                                        GenericCard(
                                            imageUri = album.artworkUri?.toUri(),
                                            title = {
                                                Text(
                                                    text = album.title,
                                                    fontWeight = FontWeight.Bold
                                                )
                                            },
                                            subtitle = {
                                                Text(
                                                    text = album.artist ?: "",
                                                    style = MaterialTheme.typography.titleMedium.copy(
                                                        color = MaterialTheme
                                                            .colorScheme
                                                            .onSurface
                                                            .copy( alpha = 0.5f )
                                                    ),
                                                    textAlign = TextAlign.Center,
                                                )
                                            },
                                            onClick = { onViewAlbum( album.id ) }
                                        )
                                    }
                                }
                            }
                            AnimatedVisibility(
                                visible = uiState.artists.isNotEmpty()
                            ) {
                                Column(
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        modifier = Modifier.padding( 12.dp, 4.dp ),
                                        text = stringResource( id = i8nR.string.core_i8n_artists ),
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary,
                                    )
                                    uiState.artists.forEach { artist ->
                                        GenericCard(
                                            imageUri = artist.artworkUri?.toUri(),
                                            title = {
                                                Text(
                                                    text = artist.name,
                                                    fontWeight = FontWeight.Bold
                                                )
                                            },
                                            subtitle = {
                                                Text(
                                                    text = stringResource(
                                                        id = if ( artist.trackCount > 1 ) {
                                                            i8nR.string.core_i8n_n_songs
                                                        } else {
                                                            i8nR.string.core_i8n_one_song
                                                        },
                                                        artist.trackCount
                                                    ),
                                                    style = MaterialTheme.typography.titleMedium.copy(
                                                        color = MaterialTheme
                                                            .colorScheme
                                                            .onSurface
                                                            .copy( alpha = 0.5f )
                                                    ),
                                                    textAlign = TextAlign.Center,
                                                )
                                            },
                                            onClick = { onViewArtist( artist.id ) }
                                        )
                                    }
                                }
                            }
                            AnimatedVisibility(
                                visible = uiState.genres.isNotEmpty()
                            ) {
                                Column(
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        modifier = Modifier.padding( 12.dp, 4.dp ),
                                        text = stringResource( id = i8nR.string.core_i8n_genres ),
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary,
                                    )
                                    uiState.genres.forEach { genre ->
                                        GenericCard(
                                            imageUri = null,
                                            title = {
                                                Text(
                                                    text = genre.name,
                                                    fontWeight = FontWeight.Bold
                                                )
                                            },
                                            subtitle = {
                                                Text(
                                                    text = stringResource(
                                                        id = if ( genre.numberOfTracks > 1 ) {
                                                            i8nR.string.core_i8n_n_songs
                                                        } else {
                                                            i8nR.string.core_i8n_one_song
                                                        },
                                                        genre.numberOfTracks
                                                    ),
                                                    style = MaterialTheme.typography.titleMedium.copy(
                                                        color = MaterialTheme
                                                            .colorScheme
                                                            .onSurface
                                                            .copy( alpha = 0.5f )
                                                    ),
                                                    textAlign = TextAlign.Center,
                                                )
                                            },
                                            onClick = { onViewGenre( genre.id ) }
                                        )
                                    }
                                }
                            }
                            AnimatedVisibility(
                                visible = uiState.playlists.isNotEmpty()
                            ) {
                                Column(
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        modifier = Modifier.padding( 12.dp, 4.dp ),
                                        text = stringResource( id = i8nR.string.core_i8n_playlists ),
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary,
                                    )
                                    uiState.playlists.forEach { playlist ->
                                        GenericCard(
                                            imageUri = playlist.artworkUri?.toUri(),
                                            title = {
                                                Text(
                                                    text = playlist.title,
                                                    fontWeight = FontWeight.Bold
                                                )
                                            },
                                            subtitle = {
                                                Text(
                                                    text = stringResource(
                                                        id = if ( playlist.songIds.size > 1 ) {
                                                            i8nR.string.core_i8n_n_songs
                                                        } else {
                                                            i8nR.string.core_i8n_one_song
                                                        },
                                                        playlist.songIds.size
                                                    ),
                                                    style = MaterialTheme.typography.titleMedium.copy(
                                                        color = MaterialTheme
                                                            .colorScheme
                                                            .onSurface
                                                            .copy( alpha = 0.5f )
                                                    ),
                                                    textAlign = TextAlign.Center,
                                                )
                                            },
                                            onClick = { onViewPlaylist( playlist.id ) }
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

}

@Composable
private fun SearchBar(
    query: String,
    currentSearchFilter: SearchFilter,
    onQueryChange: ( String ) -> Unit,
    onClear: () -> Unit,
    onSearchFilterChange: ( SearchFilter ) -> Unit,
    onNavigateBack: () -> Unit,
) {
    Column(
        modifier = Modifier
            .windowInsetsPadding(TopAppBarDefaults.windowInsets)
            .clipToBounds()
    ) {
        TextField(
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions( imeAction = ImeAction.Search ),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent
            ),
            singleLine = true,
            value = query,
            onValueChange = { onQueryChange( it ) },
            placeholder = {
                Text(
                    text = stringResource( id = i8nR.string.core_i8n_search ),
                    fontWeight = FontWeight.Bold
                )
            },
            leadingIcon = {
                IconButton(
                    onClick = onNavigateBack
                ) {
                    Icon(
                        imageVector = MusicMattersIcons.NavigateBack,
                        contentDescription = null,
                    )
                }
            },
            trailingIcon = {
                if ( query.isNotEmpty() ) {
                    IconButton(
                        onClick = onClear
                    ) {
                        Icon(
                            imageVector = MusicMattersIcons.Remove,
                            contentDescription = null
                        )
                    }
                }
            }
        )
        Spacer( modifier = Modifier.height( 4.dp ) )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.Center,
        ) {
            Spacer( modifier = Modifier.width( 4.dp ) )
            SearchFilter.entries.forEach { filter ->
                FilterChip(
                    modifier = Modifier.padding( 4.dp, 0.dp ),
                    selected = currentSearchFilter == filter,
                    onClick = { onSearchFilterChange( filter ) },
                    label = {
                        Text(
                            text = filter.getName(),
                            fontWeight = FontWeight.Bold
                        )
                    }
                )
            }
            Spacer( modifier = Modifier.width( 4.dp ) )
        }
        Spacer( modifier = Modifier.height( 4.dp ) )
    }
}

@Composable
private fun SearchFilter.getName() =
    when ( this ) {
        SearchFilter.ALL -> stringResource( id = i8nR.string.core_i8n_all )
        SearchFilter.SONGS -> stringResource( id = i8nR.string.core_i8n_songs )
        SearchFilter.ALBUMS -> stringResource( id = i8nR.string.core_i8n_albums )
        SearchFilter.ARTISTS -> stringResource( id = i8nR.string.core_i8n_artists )
        SearchFilter.GENRES -> stringResource( id = i8nR.string.core_i8n_genres )
        SearchFilter.PLAYLISTS -> stringResource( id = i8nR.string.core_i8n_playlists )
    }

@PreviewScreenSizes
@Composable
private fun SearchScreenContentPreview(
    @PreviewParameter( MusicMattersPreviewParametersProvider::class )
    previewData: PreviewData
) {
    MusicMattersTheme(
        fontName = SupportedFonts.ProductSans.name,
        useMaterialYou = true,
        fontScale = 1.25f,
        themeMode = ThemeMode.LIGHT,
        primaryColorName = PrimaryThemeColors.Blue.name
    ) {
        SearchScreenContent(
            currentSearchQuery = "Trav",
            currentlySelectedFilter = SearchFilter.SONGS,
            onClearSearchBar = {},
            onNavigateBack = {},
            onQueryChange = {},
            onSearchFilterChange = {},
            uiState = SearchScreenUiState.Success(
                songs = previewData.songs,
                albums = previewData.albums,
                artists = previewData.artists,
                genres = previewData.genres,
                playlists = previewData.playlists,
                currentlyPlayingSongId = previewData.songs[1].id,
            ),
            onSongIsPresentInQueue = { false },
            onFavorite = { _, _ -> },
            onViewAlbum = {},
            onViewArtist = {},
            onPlaySong = {},
            onDeleteSong = {},
            onShareSong = {},
            onCreatePlaylist = { _, _ -> },
            onAddSongsToPlaylist = { _, _ -> },
            onShowSnackBar = {},
            onPlaySongNext = {},
            onAddSongToQueue = {},
            onRemoveSongFromQueue = {},
            onViewGenre = {},
            onViewPlaylist = {},
        )
    }
}