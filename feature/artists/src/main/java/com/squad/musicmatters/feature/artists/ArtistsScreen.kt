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
import androidx.compose.ui.platform.LocalContext
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
import com.squad.musicmatters.core.ui.GenericGrid
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

    val context = LocalContext.current

    LibraryDestinationContainer(
        title = stringResource( id = i8nR.string.core_i8n_artists ),
        isLoading = uiState is ArtistsScreenUiState.Loading,
        onNavigateBack = onNavigateBack,
        onNavigateToSettings = onNavigateToSettings,
    ) {
        when ( uiState ) {
            ArtistsScreenUiState.Loading -> {}
            is ArtistsScreenUiState.Success -> {
                val onGetSubTitle: ( Artist ) -> String = {
                    context.getString(
                        if ( it.trackCount > 1 ) {
                            i8nR.string.core_i8n_n_songs
                        } else {
                            i8nR.string.core_i8n_one_song
                        },
                        it.trackCount
                    )
                }
                GenericGrid(
                    items = uiState.artists,
                    multipleItemsSortBarLabel = i8nR.string.core_i8n_n_artists,
                    singleItemSortBarLabel = i8nR.string.core_i8n_one_artist,
                    icon = MusicMattersIcons.Artist,
                    sortBy = uiState.sortArtistsBy,
                    sortInReverse = uiState.sortArtistsInReverse,
                    sortTypes = SortArtistsBy.entries.associateBy(
                        { it },
                        { it.label() }
                    ),
                    onSortTypeChange = onSortTypeChange,
                    onSortInReverseChange = onSortInReverseChange,
                    onGetItemKeyFor = { it.id.toString() },
                    onGetTitleFor = { it.name },
                    onGetSubTitleFor = onGetSubTitle,
                    onGetArtworkUriFor = { it.artworkUri?.toUri() },
                    onGetHeaderDescriptionFor = onGetSubTitle,
                    onViewItem = { onViewArtist( it.id ) },
                    onPlaySongsForItem = { artist ->
                        val songsByArtists = uiState.songs.filter { it.artistId == artist.id }
                        onPlaySongs( songsByArtists.first(), songsByArtists )
                    },
                    onAddSongsForItemToQueue = { artist ->
                        onAddSongsToQueue(
                            uiState.songs.filter { it.artistId == artist.id }
                        )
                    },
                    onRemoveSongsForItemFromQueue = { artist ->
                        onRemoveSongsFromQueue( uiState.songs.filter { it.artistId == artist.id } )
                    },
                    onPlaySongsForItemNext = { artist ->
                        onPlaySongsNext( uiState.songs.filter { it.artistId == artist.id } )
                    },
                    onShuffleAndPlaySongsForItem = { artist ->
                        onShuffleAndPlay( uiState.songs.filter { it.artistId == artist.id } )
                    },
                    onGetPlaylists = { uiState.playlists },
                    onAddSongsToPlaylist = onAddSongsToPlaylist,
                    onCreatePlaylist = onCreatePlaylist,
                    onShowSnackBar = onShowSnackBar,
                    onShowAddToQueueOptionFor = { artist ->
                        onShowAddToQueueOption( uiState.songs.filter { it.artistId == artist.id } )
                    },
                    onGetSongsForItem = { artist ->
                        uiState.songs.filter { it.artistId == artist.id }
                    },
                )
            }
        }
    }
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