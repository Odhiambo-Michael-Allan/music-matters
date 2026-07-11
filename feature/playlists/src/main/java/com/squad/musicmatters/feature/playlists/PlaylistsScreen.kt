package com.squad.musicmatters.feature.playlists

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
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.squad.musicmatters.core.data.repository.impl.FAVORITES_PLAYLIST_ID
import com.squad.musicmatters.core.datastore.DefaultPreferences
import com.squad.musicmatters.core.designsystem.component.MusicMattersIcons
import com.squad.musicmatters.core.designsystem.theme.MusicMattersTheme
import com.squad.musicmatters.core.designsystem.theme.SupportedFonts
import com.squad.musicmatters.core.model.Playlist
import com.squad.musicmatters.core.model.Song
import com.squad.musicmatters.core.model.SortPlaylistsBy
import com.squad.musicmatters.core.ui.BottomSheetMenuItem
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
internal fun PlaylistsScreen(
    viewModel: PlaylistsScreenViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit,
    onViewPlaylist: ( Playlist ) -> Unit,
    onShowSnackBar: ( String ) -> Unit,
    onNavigateToSettings: () -> Unit,
) {

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    PlaylistsScreenContent(
        uiState = uiState,
        onViewPlaylist = onViewPlaylist,
        onNavigateBack = onNavigateBack,
        onNavigateToSettings = onNavigateToSettings,
        onShowSnackBar = onShowSnackBar,
        onCreatePlaylist = viewModel::createPlaylist,
        onDeletePlaylist = viewModel::deletePlaylist,
        onAddSongsToPlaylist = viewModel::addSongsToPlaylist,
        onPlaySongs = viewModel::playSongs,
        onPlaySongsNext = viewModel::playSongsNext,
        onAddSongsToQueue = viewModel::addSongsToQueue,
        onSortTypeChange = viewModel::onSortTypeChange,
        onSortInReverseChange = viewModel::onSortInReverseChange,
        onShuffleAndPlaySongs = viewModel::shuffleAndPlay,
        onRemoveSongsFromQueue = viewModel::removeSongsFromQueue,
        onShowAddToQueueOption = viewModel::noSongInTheListIsPresentInTheQueue,
    )

}

@Composable
private fun PlaylistsScreenContent(
    uiState: PlaylistsScreenUiState,
    onSortTypeChange: ( SortPlaylistsBy ) -> Unit,
    onSortInReverseChange: ( Boolean ) -> Unit,
    onViewPlaylist: ( Playlist ) -> Unit,
    onPlaySongs: ( Song, List<Song> ) -> Unit,
    onAddSongsToQueue: (List<Song> ) -> Unit,
    onPlaySongsNext: (List<Song> ) -> Unit,
    onShuffleAndPlaySongs: (List<Song> ) -> Unit,
    onAddSongsToPlaylist: ( Playlist, List<Song> ) -> Unit,
    onCreatePlaylist: ( String, List<Song> ) -> Unit,
    onShowSnackBar: ( String ) -> Unit,
    onShowAddToQueueOption: ( List<Song> ) -> Boolean,
    onRemoveSongsFromQueue: (List<Song> ) -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateBack: () -> Unit,
    onDeletePlaylist: ( Playlist ) -> Unit,
) {

    val context = LocalContext.current

    LibraryDestinationContainer(
        title = stringResource( id = i8nR.string.core_i8n_playlists ),
        isLoading = uiState is PlaylistsScreenUiState.Loading,
        onNavigateBack = onNavigateBack,
        onNavigateToSettings = onNavigateToSettings
    ) {
        when ( uiState ) {
            PlaylistsScreenUiState.Loading -> {}
            is PlaylistsScreenUiState.Success -> {
                val onGetSubTitle: ( Playlist ) -> String = {
                    context.getString(
                        if ( it.songIds.size > 1 ) {
                            i8nR.string.core_i8n_n_songs
                        } else {
                            i8nR.string.core_i8n_one_song
                        },
                        it.songIds.size
                    )
                }
                GenericGrid(
                    items = uiState.playlists,
                    multipleItemsSortBarLabel = i8nR.string.core_i8n_n_playlists,
                    singleItemSortBarLabel = i8nR.string.core_i8n_one_playlist,
                    icon = MusicMattersIcons.Playlist,
                    sortBy = uiState.sortPlaylistsBy,
                    sortTypes = SortPlaylistsBy.entries.associateBy(
                        { it },
                        { it.label() }
                    ),
                    sortInReverse = uiState.sortPlaylistsInReverse,
                    onSortTypeChange = onSortTypeChange,
                    onSortInReverseChange = onSortInReverseChange,
                    onGetItemKeyFor = { it.id },
                    onGetArtworkUriFor = { it.artworkUri?.toUri() },
                    onGetTitleFor = { it.title },
                    onGetSubTitleFor = onGetSubTitle,
                    onGetHeaderDescriptionFor = onGetSubTitle,
                    onGetSongsForItem = {
                        uiState.songs.filter { song -> song.id in it.songIds }
                    },
                    onPlaySongsForItem = {
                        val songsInPlaylist = uiState.songs.filter { song -> song.id in it.songIds }
                        onPlaySongs( songsInPlaylist.first(), songsInPlaylist )
                    },
                    onAddSongsForItemToQueue = { playlist ->
                        onAddSongsToQueue(
                            uiState.songs.filter { it.id in playlist.songIds }
                        )
                    },
                    onRemoveSongsForItemFromQueue = { playlist ->
                        onRemoveSongsFromQueue(
                            uiState.songs.filter { it.id in playlist.songIds }
                        )
                    },
                    onPlaySongsForItemNext = { playlist ->
                        onPlaySongsNext(
                            uiState.songs.filter { it.id in playlist.songIds }
                        )
                    },
                    onShuffleAndPlaySongsForItem = { playlist ->
                        onShuffleAndPlaySongs(
                            uiState.songs.filter { it.id in playlist.songIds }
                        )
                    },
                    onGetPlaylists = { uiState.playlists },
                    onAddSongsToPlaylist = onAddSongsToPlaylist,
                    onCreatePlaylist = onCreatePlaylist,
                    onShowSnackBar = onShowSnackBar,
                    onShowAddToQueueOptionFor = { playlist ->
                        onShowAddToQueueOption( uiState.songs.filter { it.id in playlist.songIds } )
                    },
                    onViewItem = onViewPlaylist,
                    additionalBottomSheetMenuItems = { playlist, onDismissRequest ->
                        if ( playlist.id != FAVORITES_PLAYLIST_ID ) {
                            BottomSheetMenuItem(
                                leadingIcon = MusicMattersIcons.Delete,
                                label = stringResource( id = i8nR.string.core_i8n_delete )
                            ) {
                                onDismissRequest()
                                onDeletePlaylist( playlist )
                            }
                        }
                    }
                )
            }
        }
    }
}


private fun SortPlaylistsBy.label() =
    when ( this ) {
        SortPlaylistsBy.TITLE -> i8nR.string.core_i8n_title
        SortPlaylistsBy.TRACK_COUNT -> i8nR.string.core_i8n_track_count
        SortPlaylistsBy.CUSTOM -> i8nR.string.core_i8n_custom
    }

@PreviewScreenSizes
@Composable
private fun PlaylistsScreenContentPreview(
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
        PlaylistsScreenContent(
            uiState = PlaylistsScreenUiState.Success(
                playlists = previewData.playlists,
                songs = emptyList(),
                sortPlaylistsBy = SortPlaylistsBy.TITLE,
                sortPlaylistsInReverse = false,
            ),
            onDeletePlaylist = {},
            onCreatePlaylist = { _, _ -> },
            onViewPlaylist = {},
            onAddSongsToPlaylist = { _, _ -> },
            onShowSnackBar = {},
            onShowAddToQueueOption = { false },
            onSortTypeChange = {},
            onSortInReverseChange = {},
            onNavigateBack = {},
            onPlaySongs = { _, _ -> },
            onNavigateToSettings = {},
            onPlaySongsNext = {},
            onAddSongsToQueue = {},
            onRemoveSongsFromQueue = {},
            onShuffleAndPlaySongs = {},
        )
    }
}