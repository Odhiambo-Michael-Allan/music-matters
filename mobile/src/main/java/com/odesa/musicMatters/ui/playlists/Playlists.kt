package com.odesa.musicMatters.ui.playlists

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.odesa.musicMatters.R
import com.odesa.musicMatters.core.data.preferences.SortPlaylistsBy
import com.odesa.musicMatters.core.designsystem.theme.isLight
import com.odesa.musicMatters.core.model.PlaylistInfo
import com.odesa.musicMatters.core.model.Song
import com.odesa.musicMatters.ui.components.LoaderScaffold
import com.odesa.musicMatters.ui.components.NewPlaylistDialog
import com.odesa.musicMatters.ui.components.PlaylistGrid
import com.odesa.musicMatters.ui.components.TopAppBar

@Composable
fun PlaylistsScreen(
    viewModel: PlaylistsViewModel,
    onPlaylistClick: ( String, String ) -> Unit,
    onNavigateToSearch: () -> Unit,
    onSettingsClicked: () -> Unit
) {

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    PlaylistsScreenContent(
        uiState = uiState,
        onPlaylistClick = onPlaylistClick,
        onNavigateToSearch = onNavigateToSearch,
        onSettingsClicked = onSettingsClicked,
        onPlaySongsInPlaylist = viewModel::playSongsInPlaylist,
        onGetSongsInPlaylist = viewModel::getSongsInPlaylist,
        onAddSongsInPlaylistToPlaylist = viewModel::addSongsToPlaylist,
        onAddSongsInPlaylistToQueue = {  playlist ->
            viewModel.apply {
                getSongsInPlaylist( playlist ).forEach { addSongToQueue( it ) }
            }
        },
        onCreatePlaylist = viewModel::createPlaylist,
        onPlaySongsInPlaylistNext = { playlist ->
            viewModel.apply {
                getSongsInPlaylist( playlist ).forEach { playSongNext( it ) }
            }
        },
        onShufflePlaySongsInPlaylist = {
            viewModel.apply {
                shuffleAndPlay(
                    songs = getSongsInPlaylist( it )
                )
            }
        },
        onSearchSongsMatchingQuery = viewModel::searchSongsMatching,
        playlistIsDeletable = viewModel::isPlaylistDeletable,
        onDeletePlaylist = viewModel::deletePlaylist,
        onRenamePlaylist = viewModel::renamePlaylist,
        onSortTypeChange = viewModel::setSortPlaylistsBy,
        onSortReverseChange = viewModel::setSortPlaylistsInReverse
    )
}

@Composable
fun PlaylistsScreenContent(
    uiState: PlaylistsScreenUiState,
    onPlaylistClick: ( String, String ) -> Unit,
    onNavigateToSearch: () -> Unit,
    onSettingsClicked: () -> Unit,
    onPlaySongsInPlaylist: (PlaylistInfo ) -> Unit,
    onGetSongsInPlaylist: (PlaylistInfo ) -> List<Song>,
    onAddSongsInPlaylistToPlaylist: (PlaylistInfo, List<Song> ) -> Unit,
    onAddSongsInPlaylistToQueue: (PlaylistInfo ) -> Unit,
    onCreatePlaylist: ( String, List<Song> ) -> Unit,
    onPlaySongsInPlaylistNext: (PlaylistInfo ) -> Unit,
    onShufflePlaySongsInPlaylist: (PlaylistInfo ) -> Unit,
    onSearchSongsMatchingQuery: ( String ) -> List<Song>,
    playlistIsDeletable: (PlaylistInfo ) -> Boolean,
    onDeletePlaylist: (PlaylistInfo ) -> Unit,
    onRenamePlaylist: (PlaylistInfo, String ) -> Unit,
    onSortTypeChange: ( SortPlaylistsBy ) -> Unit,
    onSortReverseChange: ( Boolean ) -> Unit,
) {

    val fallbackResourceId =
        if ( uiState.themeMode.isLight( LocalContext.current ) )
            R.drawable.placeholder_light else R.drawable.placeholder_dark
    var showCreatePlaylistDialog by remember {
        mutableStateOf( false )
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Column {
            TopAppBar(
                onNavigationIconClicked = onNavigateToSearch,
                title = uiState.language.playlists,
                settings = uiState.language.settings,
                onSettingsClicked = onSettingsClicked,
            )
            LoaderScaffold(
                isLoading = uiState.isLoadingSongs,
                loading = uiState.language.loading
            ) {
                PlaylistGrid(
                    playlistInfos = uiState.playlistInfos,
                    isLoadingSongsInPlaylist = uiState.isLoadingSongs,
                    language = uiState.language,
                    sortType = uiState.sortPlaylistsBy,
                    sortReverse = uiState.sortPlaylistsInReverse,
                    fallbackResourceId = fallbackResourceId,
                    onSortReverseChange = onSortReverseChange,
                    onSortTypeChange = onSortTypeChange,
                    onPlaylistClick = onPlaylistClick,
                    onPlaySongsInPlaylist = onPlaySongsInPlaylist,
                    onGetSongsInPlaylist = onGetSongsInPlaylist,
                    onGetPlaylists = { uiState.playlistInfos },
                    onAddSongsInPlaylistToPlaylist = onAddSongsInPlaylistToPlaylist,
                    onAddSongsInPlaylistToQueue = onAddSongsInPlaylistToQueue,
                    onCreatePlaylist = onCreatePlaylist,
                    onPlaySongsInPlaylistNext = onPlaySongsInPlaylistNext,
                    onShufflePlaySongsInPlaylist = onShufflePlaySongsInPlaylist,
                    onSearchSongsMatchingQuery = onSearchSongsMatchingQuery,
                    onDeletePlaylist = onDeletePlaylist,
                    playlistIsDeletable = playlistIsDeletable,
                    onRenamePlaylist = onRenamePlaylist,
                )
            }
        }


        Column (
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp),
            horizontalAlignment = Alignment.End
        ) {
            FloatingActionButton(
                modifier = Modifier.padding( 8.dp ),
                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                onClick = { showCreatePlaylistDialog = true }
            ) {
                Icon(
                    imageVector = Icons.Rounded.Add,
                    contentDescription = null
                )
            }
        }

        if ( showCreatePlaylistDialog ) {
            NewPlaylistDialog(
                language = uiState.language,
                fallbackResourceId = fallbackResourceId,
                onSearchSongsMatchingQuery = onSearchSongsMatchingQuery,
                onConfirmation = { playlistTitle, songs ->
                    onCreatePlaylist( playlistTitle, songs )
                    showCreatePlaylistDialog = false
                }
            ) {
                showCreatePlaylistDialog = false
            }
        }
    }
}

@PreviewScreenSizes
@Composable
fun PlaylistsScreenContentPreview() {
    PlaylistsScreenContent(
        uiState = testPlaylistsScreenUiState,
        onSettingsClicked = {},
        onPlaySongsInPlaylist = {},
        onPlaylistClick = { _, _ -> },
        onNavigateToSearch = {},
        onGetSongsInPlaylist = { emptyList() },
        onAddSongsInPlaylistToPlaylist = { _, _ -> },
        onPlaySongsInPlaylistNext = {},
        onSearchSongsMatchingQuery = { emptyList() },
        onAddSongsInPlaylistToQueue = {},
        onCreatePlaylist = { _, _ -> },
        onShufflePlaySongsInPlaylist = {},
        onDeletePlaylist = {},
        playlistIsDeletable = { true },
        onRenamePlaylist = { _, _ -> },
        onSortTypeChange = {},
        onSortReverseChange = {}
    )
}