package com.squad.musicmatters.feature.queue

import android.net.Uri
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.rounded.ClearAll
import androidx.compose.material.icons.rounded.Save
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.squad.musicmatters.core.datastore.DefaultPreferences
import com.squad.musicmatters.core.designsystem.component.DevicePreviews
import com.squad.musicmatters.core.designsystem.theme.MusicMattersTheme
import com.squad.musicmatters.core.designsystem.theme.SupportedFonts
import com.squad.musicmatters.core.model.PlaylistInfo
import com.squad.musicmatters.core.model.Song
import com.squad.musicmatters.core.ui.LoaderScaffold
import com.squad.musicmatters.core.ui.MusicMattersPreviewParametersProvider
import com.squad.musicmatters.core.ui.PreviewData
import com.squad.musicmatters.core.ui.TopAppBarMinimalTitle
import com.squad.musicmatters.core.ui.dialog.NewPlaylistDialog
import com.squad.musicmatters.feature.queue.components.QueueList

@Composable
internal fun QueueScreen(
    viewModel: QueueScreenViewModel = hiltViewModel(),
    onViewAlbum: ( String ) -> Unit,
    onViewArtist: ( String ) -> Unit,
    onShareSong: ( Uri, String ) -> Unit,
    onDeleteSong: ( Song ) -> Unit,
    onNavigateBack: () -> Unit
) {

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    QueueScreenContent(
        uiState = uiState,
        onNavigateUp = onNavigateBack,
        onCreatePlaylist = { title, songs -> viewModel.createPlaylist( title, songs ) },
        onClearQueue = viewModel::clearQueue,
        onFavorite = viewModel::addToFavorites,
        playSong = viewModel::playSongs,
        onMoveSong = viewModel::moveSong,
        onPlayNext = viewModel::playSongNext,
        onViewAlbum = onViewAlbum,
        onViewArtist = onViewArtist,
        onAddToQueue = viewModel::addSongToQueue,
        onAddSongsToPlaylist = { playlist, songs ->
            viewModel.addSongsToPlaylist( playlist, songs )
        },
        onSearchSongsMatchingQuery = {
            emptyList<Song>()
//            viewModel::searchSongsMatching
        },
        onShareSong = {
//            onShareSong( it, uiState.language.shareFailedX( "" ) )
        },
        onGetPlaylists = {
            emptyList<PlaylistInfo>()
//            uiState.playlistInfos
        },
        onDeleteSong = onDeleteSong,
        onSaveQueue = viewModel::saveQueue,
    )
}

@Composable
private fun QueueScreenContent(
    uiState: QueueScreenUiState,
    onNavigateUp: () -> Unit,
    onCreatePlaylist: ( String, List<Song> ) -> Unit,
    onClearQueue: () -> Unit,
    onFavorite: ( String, Boolean ) -> Unit,
    playSong: ( Song, List<Song> ) -> Unit,
    onMoveSong: ( Int, Int ) -> Unit,
    onPlayNext: ( Song ) -> Unit,
    onViewAlbum: ( String ) -> Unit,
    onViewArtist: ( String ) -> Unit,
    onShareSong: ( Uri ) -> Unit,
    onAddToQueue: ( Song ) -> Unit,
    onAddSongsToPlaylist: (PlaylistInfo, List<Song> ) -> Unit,
    onSearchSongsMatchingQuery: ( String ) -> List<Song>,
    onGetPlaylists: () -> List<PlaylistInfo>,
    onDeleteSong: ( Song ) -> Unit,
    onSaveQueue: ( List<Song> ) -> Unit,
) {

    var showSaveDialog by remember { mutableStateOf( false ) }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        QueueScreenTopAppBar(
            onBackArrowClick = onNavigateUp,
            onSaveClick = {
                showSaveDialog = !showSaveDialog
            },
            onClearClick = {
                onClearQueue()
                onNavigateUp()
            }
        )
        LoaderScaffold(
            isLoading = uiState is QueueScreenUiState.Loading
        ) {
            when ( uiState ) {
                QueueScreenUiState.Loading -> {}
                is QueueScreenUiState.Success -> {
                    QueueList(
                        songsInQueue = uiState.songsInQueue,
                        currentlyPlayingSongId = uiState.currentlyPlayingSongId,
                        songsAdditionalMetadata = uiState.songsAdditionalMetadata,
                        language = uiState.language,
                        favoriteSongIds = uiState.favoriteSongIds,
                        onFavorite = onFavorite,
                        playSong = playSong,
                        onMove = onMoveSong,
                        onPlayNext = onPlayNext,
                        onAddToQueue = onAddToQueue,
                        onShareSong = onShareSong,
                        onViewAlbum = onViewAlbum,
                        onViewArtist = onViewArtist,
                        onAddSongsToPlaylist = onAddSongsToPlaylist,
                        onSearchSongsMatchingQuery = onSearchSongsMatchingQuery,
                        onCreatePlaylist = onCreatePlaylist,
                        onGetPlaylists = { onGetPlaylists() },
                        onDeleteSong = onDeleteSong,
                        onSaveQueue = onSaveQueue,
                    )

                    if ( showSaveDialog ) {
                        NewPlaylistDialog(
                            language = uiState.language,
                            initialSongsToAdd = uiState.songsInQueue,
                            onSearchSongsMatchingQuery = onSearchSongsMatchingQuery,
                            onConfirmation = { title, songs ->
                                onCreatePlaylist( title, songs )
                                showSaveDialog = false
                            },
                            onDismissRequest = { showSaveDialog = false }
                        )
                    }
                }
            }
        }
    }
}

@OptIn( ExperimentalMaterial3Api::class )
@Composable
private fun QueueScreenTopAppBar(
    modifier: Modifier = Modifier,
    onBackArrowClick: () -> Unit,
    onSaveClick: () -> Unit,
    onClearClick: () -> Unit
) {
    CenterAlignedTopAppBar(
        modifier = modifier,
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = Color.Transparent
        ),
        navigationIcon = {
            IconButton( onClick = onBackArrowClick ) {
                Icon(
                    imageVector = Icons.Filled.ArrowBack,
                    contentDescription = null
                )
            }
        },
        title = {
            Crossfade(
                targetState = stringResource( id = R.string.feature_queue_queue ),
                label = "top-app-bar-title"
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    TopAppBarMinimalTitle {
                        Text( text = it )
                    }
                }
            }
        },
        actions = {
            IconButton(
                onClick = onSaveClick
            ) {
                Icon(
                    imageVector = Icons.Rounded.Save,
                    contentDescription = null
                )
            }
            IconButton(
                onClick = onClearClick
            ) {
                Icon(
                    imageVector = Icons.Rounded.ClearAll,
                    contentDescription = null
                )
            }
        }
    )
}

@Preview( showBackground = true )
@Composable
private fun QueueScreenTopAppBarPreview() {
    QueueScreenTopAppBar(
        onBackArrowClick = {},
        onSaveClick = {},
        onClearClick = {},
    )
}

@DevicePreviews
@Composable
private fun QueueScreenContentPreview(
    @PreviewParameter( MusicMattersPreviewParametersProvider::class )
    previewData: PreviewData
) {
    MusicMattersTheme(
        themeMode = DefaultPreferences.THEME_MODE,
        primaryColorName = DefaultPreferences.PRIMARY_COLOR_NAME,
        fontName = SupportedFonts.ProductSans.name,
        fontScale = 1.0f,
        useMaterialYou = true
    ) {
        QueueScreenContent(
            uiState = QueueScreenUiState.Success(
                songsInQueue = previewData.songs,
                currentlyPlayingSongId = previewData.songs.first().id,
                favoriteSongIds = setOf( previewData.songs.first().id ),
                playlists = previewData.playlists,
                songsAdditionalMetadata = emptyList()
            ),
            onNavigateUp = {},
            onCreatePlaylist = { _, _ -> },
            onClearQueue = {},
            onFavorite = { _, _ -> },
            playSong = { _, _ -> },
            onMoveSong = { _, _ ->  },
            onPlayNext = {},
            onAddToQueue = {},
            onViewAlbum = {},
            onViewArtist = {},
            onShareSong = {},
            onAddSongsToPlaylist = { _, _, -> },
            onSearchSongsMatchingQuery = { emptyList() },
            onGetPlaylists = { emptyList() },
            onDeleteSong = {},
            onSaveQueue = {}
        )
    }
}

