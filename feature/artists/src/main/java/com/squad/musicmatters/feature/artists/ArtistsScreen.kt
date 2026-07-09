package com.squad.musicmatters.feature.artists

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.squad.musicmatters.core.datastore.DefaultPreferences
import com.squad.musicmatters.core.designsystem.component.MusicMattersIcons
import com.squad.musicmatters.core.designsystem.theme.MusicMattersTheme
import com.squad.musicmatters.core.designsystem.theme.SupportedFonts
import com.squad.musicmatters.core.model.Artist
import com.squad.musicmatters.core.model.Playlist
import com.squad.musicmatters.core.model.Song
import com.squad.musicmatters.core.model.SortArtistsBy
import com.squad.musicmatters.core.ui.GenericTile
import com.squad.musicmatters.core.ui.IconTextBody
import com.squad.musicmatters.core.ui.LibraryDestinationContainer
import com.squad.musicmatters.core.ui.MediaSortBar
import com.squad.musicmatters.core.ui.MediaSortBarScaffold
import com.squad.musicmatters.core.ui.MusicMattersPreviewParametersProvider
import com.squad.musicmatters.core.ui.PreviewData
import com.squad.musicmatters.core.i8n.R as i8nR

@Composable
internal fun ArtistsScreen(
    viewModel: ArtistsScreenViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit,
    onViewArtist: ( Long ) -> Unit,
    onShowSnackBar: ( String ) -> Unit,
    onNavigateToSettings: () -> Unit,
) {

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    ArtistsScreenContent(
        uiState = uiState,
        onViewArtist = onViewArtist,
        onShowSnackBar = onShowSnackBar,
        onNavigateBack = onNavigateBack,
        onNavigateToSettings = onNavigateToSettings,
        onSortTypeChange = viewModel::onSortTypeChange,
        onSortInReverseChange = viewModel::onSortInReverseChange,
        onCreatePlaylist = viewModel::createPlaylist,
        onShowAddToQueueOption = viewModel::noSongInTheListIsPresentInTheQueue,
        onPlaySongs = viewModel::playSongs,
        onAddSongsToPlaylist = viewModel::addSongsToPlaylist,
        onShuffleAndPlay = viewModel::shuffleAndPlay,
        onPlaySongsNext = viewModel::playSongsNext,
        onAddSongsToQueue = viewModel::addSongsToQueue,
        onRemoveSongsFromQueue = viewModel::removeSongsFromQueue,
    )

}

@Composable
private fun ArtistsScreenContent(
    uiState: ArtistsScreenUiState,
    onSortTypeChange: ( SortArtistsBy ) -> Unit,
    onSortInReverseChange: ( Boolean ) -> Unit,
    onViewArtist: ( Long ) -> Unit,
    onPlaySongs: ( Song, List<Song> ) -> Unit,
    onAddSongsToQueue: ( List<Song> ) -> Unit,
    onRemoveSongsFromQueue: ( List<Song> ) -> Unit,
    onPlaySongsNext: ( List<Song> ) -> Unit,
    onShuffleAndPlay: ( List<Song> ) -> Unit,
    onAddSongsToPlaylist: ( Playlist, List<Song> ) -> Unit,
    onCreatePlaylist: ( String, List<Song> ) -> Unit,
    onShowSnackBar: ( String ) -> Unit,
    onShowAddToQueueOption: ( List<Song> ) -> Boolean,
    onNavigateBack: () -> Unit,
    onNavigateToSettings: () -> Unit,
) {

    LibraryDestinationContainer(
        title = stringResource( id = i8nR.string.core_i8n_artists ),
        isLoading = uiState is ArtistsScreenUiState.Loading,
        onNavigateBack = onNavigateBack,
        onNavigateToSettings = onNavigateToSettings,
    ) {
        when ( uiState ) {
            ArtistsScreenUiState.Loading -> {}
            is ArtistsScreenUiState.Success -> {
                ArtistsGrid(
                    artists = uiState.artists,
                    sortArtistsBy = uiState.sortArtistsBy,
                    sortArtistsInReverse = uiState.sortArtistsInReverse,
                    onViewArtist = onViewArtist,
                    onSortTypeChange = onSortTypeChange,
                    onSortInReverseChange = onSortInReverseChange,
                    onGetSongsByArtist = { artist -> uiState.songs.filter { it.artistId == artist.id } },
                    onPlaySongsByArtist = { artist ->
                        val songsByArtists = uiState.songs.filter { it.artistId == artist.id }
                        onPlaySongs( songsByArtists.first(), songsByArtists )
                    },
                    onAddSongsByArtistToQueue = { artist ->
                        onAddSongsToQueue(
                            uiState.songs.filter { it.artistId == artist.id }
                        )
                    },
                    onRemoveSongsByArtistFromQueue = { artist ->
                        onRemoveSongsFromQueue( uiState.songs.filter { it.artistId == artist.id } )
                    },
                    onPlaySongsByArtistNext = { artist ->
                        onPlaySongsNext( uiState.songs.filter { it.artistId == artist.id } )
                    },
                    onShuffleAndPlaySongsByArtist = { artist ->
                        onShuffleAndPlay( uiState.songs.filter { it.artistId == artist.id } )
                    },
                    onGetPlaylists = { uiState.playlists },
                    onAddSongsToPlaylist = onAddSongsToPlaylist,
                    onCreatePlaylist = onCreatePlaylist,
                    onShowSnackBar = onShowSnackBar,
                    onShowAddToQueueOption = { artist ->
                        onShowAddToQueueOption( uiState.songs.filter { it.artistId == artist.id } )
                    }
                )
            }
        }
    }

}


@Composable
internal fun ArtistsGrid(
    artists: List<Artist>,
    sortArtistsBy: SortArtistsBy,
    sortArtistsInReverse: Boolean,
    onSortTypeChange: ( SortArtistsBy ) -> Unit,
    onSortInReverseChange: ( Boolean ) -> Unit,
    onViewArtist: ( Long ) -> Unit,
    onPlaySongsByArtist: ( Artist ) -> Unit,
    onAddSongsByArtistToQueue: ( Artist ) -> Unit,
    onRemoveSongsByArtistFromQueue: ( Artist ) -> Unit,
    onPlaySongsByArtistNext: ( Artist ) -> Unit,
    onShuffleAndPlaySongsByArtist: ( Artist ) -> Unit,
    onGetPlaylists: () -> List<Playlist>,
    onAddSongsToPlaylist: ( Playlist, List<Song> ) -> Unit,
    onCreatePlaylist: ( String, List<Song> ) -> Unit,
    onGetSongsByArtist: ( Artist ) -> List<Song>,
    onShowSnackBar: ( String ) -> Unit,
    onShowAddToQueueOption: ( Artist ) -> Boolean,
) {
    MediaSortBarScaffold (
        mediaSortBar = {
            MediaSortBar(
                sortBy = sortArtistsBy,
                sortInReverse = sortArtistsInReverse,
                sortTypes = SortArtistsBy.entries.associateBy(
                    { it },
                    { it.label() }
                ),
                onSortTypeChange = onSortTypeChange,
                onSortReverseChange = onSortInReverseChange,
                label = {
                    Text(
                        text = stringResource(
                            id = if ( artists.size > 1 ) {
                                i8nR.string.core_i8n_n_artists
                            } else {
                                i8nR.string.core_i8n_artist
                            },
                            artists.size
                        ),
                        fontWeight = FontWeight.SemiBold
                    )
                }
            )
        }
    ) {
        when {
            artists.isEmpty() -> IconTextBody(
                icon = { modifier ->
                    Icon(
                        modifier = modifier,
                        imageVector = MusicMattersIcons.Artist,
                        contentDescription = null,
                    )
                }
            ) {
                Text( text = stringResource( id = i8nR.string.core_i8n_damn_this_is_so_empty ) )
            }
            else -> {
                LazyVerticalGrid(
                    columns = GridCells.Adaptive( minSize = 150.dp ),
                    contentPadding = PaddingValues(
                        start = 8.dp,
                        end = 8.dp,
                        top = 8.dp,
                        bottom = 70.dp
                    )
                ) {
                    items(
                        artists,
                        key = { it.id }
                    ) {
                        ArtistTile(
                            artist = it,
                            onViewArtist = onViewArtist,
                            onPlaySongsByArtist = { onPlaySongsByArtist( it ) },
                            onAddSongsByArtistToQueue = { onAddSongsByArtistToQueue( it ) },
                            onPlaySongsByArtistNext = { onPlaySongsByArtistNext( it ) },
                            onShuffleAndPlaySongsByArtist = { onShuffleAndPlaySongsByArtist( it ) },
                            onGetPlaylists = onGetPlaylists,
                            onAddSongsToPlaylist = onAddSongsToPlaylist,
                            onShowSnackBar = onShowSnackBar,
                            onCreatePlaylist = onCreatePlaylist,
                            onShowAddToQueueOption = { onShowAddToQueueOption( it ) },
                            onRemoveSongsByArtistFromQueue = {
                                onRemoveSongsByArtistFromQueue( it )
                            },
                            onGetSongsByArtist = { onGetSongsByArtist( it ) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(4.dp)
                                .animateItem()
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ArtistTile(
    modifier: Modifier = Modifier,
    artist: Artist,
    onViewArtist: ( Long ) -> Unit,
    onPlaySongsByArtist: () -> Unit,
    onAddSongsByArtistToQueue: () -> Unit,
    onRemoveSongsByArtistFromQueue: () -> Unit,
    onPlaySongsByArtistNext: () -> Unit,
    onShuffleAndPlaySongsByArtist: () -> Unit,
    onGetPlaylists: () -> List<Playlist>,
    onAddSongsToPlaylist: ( Playlist, List<Song> ) -> Unit,
    onCreatePlaylist: ( String, List<Song> ) -> Unit,
    onGetSongsByArtist: () -> List<Song>,
    onShowSnackBar: ( String ) -> Unit,
    onShowAddToQueueOption: () -> Boolean,
) {
    val subTitle = stringResource(
        id = if ( artist.trackCount > 1 ) {
            i8nR.string.core_i8n_n_songs
        } else {
            i8nR.string.core_i8n_one_song
        },
        artist.trackCount
    )
    GenericTile(
        modifier = modifier,
        imageUri = artist.artworkUri?.toUri(),
        title = artist.name,
        subTitle = subTitle,
        headerDescription = subTitle,
        onGetPlaylists = onGetPlaylists,
        onPlay = onPlaySongsByArtist,
        onClick = { onViewArtist( artist.id ) },
        onShufflePlay = onShuffleAndPlaySongsByArtist,
        onAddToQueue = onAddSongsByArtistToQueue,
        onPlayNext = onPlaySongsByArtistNext,
        onGetSongs = onGetSongsByArtist,
        onCreatePlaylist = onCreatePlaylist,
        onAddSongsToPlaylist = onAddSongsToPlaylist,
        onShowSnackBar = onShowSnackBar,
        onShowAddToQueueOption = onShowAddToQueueOption,
        onRemoveFromQueue = onRemoveSongsByArtistFromQueue,
    )
}

private fun SortArtistsBy.label(): Int =
    when ( this ) {
        SortArtistsBy.ARTIST_NAME -> i8nR.string.core_i8n_title
        SortArtistsBy.CUSTOM -> i8nR.string.core_i8n_custom
        SortArtistsBy.TRACK_COUNT -> i8nR.string.core_i8n_track_count
    }

@PreviewScreenSizes
@Composable
private fun ArtistsScreenPreview(
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
        ArtistsScreenContent(
            uiState = ArtistsScreenUiState.Success(
                artists = previewData.artists,
                sortArtistsBy = SortArtistsBy.ARTIST_NAME,
                sortArtistsInReverse = false,
                playlists = emptyList(),
                songs = emptyList()
            ),
            onViewArtist = {},
            onCreatePlaylist = { _, _ -> },
            onSortTypeChange = {},
            onSortInReverseChange = {},
            onShowSnackBar = {},
            onShowAddToQueueOption = { false },
            onAddSongsToPlaylist = { _, _ -> },
            onRemoveSongsFromQueue = {},
            onAddSongsToQueue = {},
            onPlaySongs = { _, _ -> },
            onShuffleAndPlay = {},
            onPlaySongsNext = {},
            onNavigateBack = {},
            onNavigateToSettings = {},
        )
    }
}