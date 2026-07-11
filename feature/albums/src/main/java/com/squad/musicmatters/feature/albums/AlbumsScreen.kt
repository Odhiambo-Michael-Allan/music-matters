package com.squad.musicmatters.feature.albums

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.toString
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import androidx.core.net.toUri
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.squad.musicmatters.core.datastore.DefaultPreferences
import com.squad.musicmatters.core.designsystem.component.MusicMattersIcons
import com.squad.musicmatters.core.designsystem.theme.MusicMattersTheme
import com.squad.musicmatters.core.designsystem.theme.SupportedFonts
import com.squad.musicmatters.core.model.Album
import com.squad.musicmatters.core.model.Playlist
import com.squad.musicmatters.core.model.Song
import com.squad.musicmatters.core.model.SortAlbumsBy
import com.squad.musicmatters.core.ui.BottomSheetMenuItem
import com.squad.musicmatters.core.ui.GenericGrid
import com.squad.musicmatters.core.ui.LibraryDestinationContainer
import com.squad.musicmatters.core.ui.MusicMattersPreviewParametersProvider
import com.squad.musicmatters.core.ui.PreviewData
import com.squad.musicmatters.core.i8n.R as i8nR

@Composable
internal fun AlbumsScreen(
    viewModel: AlbumsScreenViewModel = hiltViewModel(),
    onViewAlbum: ( Album ) -> Unit,
    onViewArtist: ( String ) -> Unit,
    onShowSnackBar: ( String ) -> Unit,
    onNavigateBack: () -> Unit,
    onNavigateToSettings: () -> Unit,
) {

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    AlbumsScreenContent(
        uiState = uiState,
        onViewAlbum = onViewAlbum,
        onViewAlbumArtist = onViewArtist,
        onShowSnackBar = onShowSnackBar,
        onNavigateBack = onNavigateBack,
        onNavigateToSettings = onNavigateToSettings,
        onSortTypeChange = viewModel::onSortTypeChange,
        onSortInReverseChange = viewModel::onSortInReverseChange,
        onPlaySongsInAlbum = viewModel::playSongs,
        onAddSongsInAlbumToQueue = viewModel::addSongsToQueue,
        onPlaySongsInAlbumNext = viewModel::playSongsNext,
        onShuffleAndPlaySongsInAlbum = viewModel::shuffleAndPlay,
        onAddSongsToPlaylist = viewModel::addSongsToPlaylist,
        onCreatePlaylist = viewModel::createPlaylist,
        onShowAddToQueueOption = viewModel::noSongInTheListIsPresentInTheQueue,
        onRemoveSongsInAlbumFromQueue = viewModel::removeSongsFromQueue
    )

}

@OptIn( ExperimentalMaterial3Api::class )
@Composable
private fun AlbumsScreenContent(
    uiState: AlbumsScreenUiState,
    onSortTypeChange: ( SortAlbumsBy ) -> Unit,
    onSortInReverseChange: ( Boolean ) -> Unit,
    onViewAlbum: ( Album ) -> Unit,
    onPlaySongsInAlbum: ( Song, List<Song> ) -> Unit,
    onAddSongsInAlbumToQueue: ( List<Song> ) -> Unit,
    onPlaySongsInAlbumNext: ( List<Song> ) -> Unit,
    onShuffleAndPlaySongsInAlbum: ( List<Song> ) -> Unit,
    onViewAlbumArtist: ( String ) -> Unit,
    onAddSongsToPlaylist: (Playlist, List<Song> ) -> Unit,
    onCreatePlaylist: ( String, List<Song> ) -> Unit,
    onShowSnackBar: ( String ) -> Unit,
    onShowAddToQueueOption: ( List<Song> ) -> Boolean,
    onRemoveSongsInAlbumFromQueue: ( List<Song> ) -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateBack: () -> Unit,
) {

    LibraryDestinationContainer(
        title = stringResource( id = i8nR.string.core_i8n_albums ),
        isLoading = uiState is AlbumsScreenUiState.Loading,
        onNavigateBack = onNavigateBack,
        onNavigateToSettings = onNavigateToSettings,
    ) {
        when ( uiState ) {
            AlbumsScreenUiState.Loading -> {}
            is AlbumsScreenUiState.Success -> {
                GenericGrid(
                    items = uiState.albums,
                    multipleItemsSortBarLabel = i8nR.string.core_i8n_n_albums,
                    singleItemSortBarLabel = i8nR.string.core_i8n_album,
                    icon = MusicMattersIcons.Album,
                    sortBy = uiState.sortAlbumsBy,
                    sortTypes = SortAlbumsBy.entries.associateBy(
                        { it },
                        { it.label() }
                    ),
                    sortInReverse = uiState.sortAlbumsInReverse,
                    onSortTypeChange = onSortTypeChange,
                    onSortInReverseChange = onSortInReverseChange,
                    onViewItem = onViewAlbum,
                    onGetTitleFor = { it.title },
                    onGetSubTitleFor = { album ->
                        album.artist.takeIf { !it.isNullOrBlank() }
                    },
                    onGetItemKeyFor = { it.id.toString() },
                    onGetImageUriFor = { it.artworkUri?.toUri() },
                    onGetHeaderDescriptionFor = { it.artist ?: "" },
                    onPlaySongsForItem = {
                        val songsInAlbum = uiState.songs.filter { song -> song.albumId == it.id }
                        onPlaySongsInAlbum( songsInAlbum.first(), songsInAlbum )
                    },
                    onAddSongsForItemToQueue = {
                        onAddSongsInAlbumToQueue(
                            uiState.songs.filter { song -> song.albumId == it.id }
                        )
                    },
                    onPlaySongsForItemNext = {
                        onPlaySongsInAlbumNext(
                            uiState.songs.filter { song -> song.albumId == it.id }
                        )
                    },
                    onShuffleAndPlaySongsForItem = {
                        onShuffleAndPlaySongsInAlbum(
                            uiState.songs.filter { song -> song.albumId == it.id }
                        )
                    },
                    onGetPlaylists = { uiState.playlists },
                    onAddSongsToPlaylist = onAddSongsToPlaylist,
                    onCreatePlaylist = onCreatePlaylist,
                    onGetSongsForItem = { album -> uiState.songs.filter { it.albumId == album.id } },
                    onShowSnackBar = onShowSnackBar,
                    onShowAddToQueueOptionFor = { album ->
                        onShowAddToQueueOption( uiState.songs.filter { it.albumId == album.id } )
                    },
                    onRemoveSongsForItemFromQueue = { album ->
                        onRemoveSongsInAlbumFromQueue( uiState.songs.filter { it.albumId == album.id } )
                    },
                    additionalBottomSheetMenuItems = { album, onDismissRequest ->
                        album.artist?.let { albumArtist ->
                            BottomSheetMenuItem(
                                leadingIcon = MusicMattersIcons.Artist,
                                label = stringResource(
                                    id = i8nR.string.core_i8n_go_to_artist,
                                    albumArtist
                                )
                            ) {
                                onDismissRequest()
                                onViewAlbumArtist( albumArtist )
                            }
                        }
                    }
                )
            }
        }
    }

}

private fun SortAlbumsBy.label(): Int =
    when ( this ) {
        SortAlbumsBy.ALBUM_NAME -> i8nR.string.core_i8n_title
        SortAlbumsBy.CUSTOM -> i8nR.string.core_i8n_custom
        SortAlbumsBy.ARTIST_NAME -> i8nR.string.core_i8n_artist_name
        SortAlbumsBy.TRACK_COUNT -> i8nR.string.core_i8n_track_count
    }

@PreviewScreenSizes
@Composable
private fun AlbumsScreenContentPreview(
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
        AlbumsScreenContent(
            uiState = AlbumsScreenUiState.Success(
                albums = previewData.albums,
                sortAlbumsBy = SortAlbumsBy.ALBUM_NAME,
                sortAlbumsInReverse = false,
                songs = emptyList(),
                playlists = emptyList(),
            ),
            onViewAlbum = {},
            onShowSnackBar = {},
            onViewAlbumArtist = {},
            onPlaySongsInAlbum = { _, _ -> },
            onCreatePlaylist = { _, _ -> },
            onPlaySongsInAlbumNext = {},
            onRemoveSongsInAlbumFromQueue = {},
            onShowAddToQueueOption = { false },
            onAddSongsInAlbumToQueue = {},
            onSortInReverseChange = {},
            onShuffleAndPlaySongsInAlbum = {},
            onSortTypeChange = {},
            onAddSongsToPlaylist = { _, _ -> },
            onNavigateBack = {},
            onNavigateToSettings = {},
        )
    }
}