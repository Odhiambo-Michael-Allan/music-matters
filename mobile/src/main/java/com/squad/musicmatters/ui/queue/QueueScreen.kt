package com.squad.musicmatters.ui.queue
//
//import android.net.Uri
//import androidx.compose.foundation.layout.Column
//import androidx.compose.foundation.layout.fillMaxSize
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.getValue
//import androidx.compose.runtime.mutableStateOf
//import androidx.compose.runtime.remember
//import androidx.compose.runtime.setValue
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.platform.LocalContext
//import androidx.compose.ui.tooling.preview.Preview
//import androidx.lifecycle.compose.collectAsStateWithLifecycle
//import com.squad.musicmatters.R
//import com.squad.musicmatters.core.designsystem.theme.isLight
//import com.squad.musicmatters.core.model.PlaylistInfo
//import com.squad.musicmatters.core.model.Song
//import com.squad.musicmatters.ui.components.LoaderScaffold
//import com.squad.musicmatters.ui.components.NewPlaylistDialog
//import com.squad.musicmatters.ui.components.QueueScreenTopAppBar
//
//@Composable
//fun QueueScreen(
//    viewModel: QueueScreenViewModel,
//    onViewAlbum: ( String ) -> Unit,
//    onViewArtist: ( String ) -> Unit,
//    onShareSong: ( Uri, String ) -> Unit,
//    onDeleteSong: ( Song ) -> Unit,
//    onNavigateBack: () -> Unit
//) {
//
//    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
//
//    QueueScreenContent(
//        uiState = uiState,
//        onNavigateUp = onNavigateBack,
//        onCreatePlaylist = { title, songs -> viewModel.createPlaylist( title, songs ) },
//        onClearQueue = viewModel::clearQueue,
//        onFavorite = viewModel::addToFavorites,
//        playSong = {
//            viewModel.playSongs(
//                selectedSong = it,
//                songsInPlaylist = uiState.songsInQueue
//            )
//        },
//        onMoveSong = viewModel::moveSong,
//        onPlayNext = viewModel::playSongNext,
//        onViewAlbum = onViewAlbum,
//        onViewArtist = onViewArtist,
//        onAddToQueue = viewModel::addSongToQueue,
//        onAddSongsToPlaylist = { playlist, songs ->
//            viewModel.addSongsToPlaylist( playlist, songs )
//        },
//        onSearchSongsMatchingQuery = viewModel::searchSongsMatching,
//        onShareSong = { onShareSong( it, uiState.language.shareFailedX( "" ) ) },
//        onGetPlaylists = { uiState.playlistInfos },
//        onDeleteSong = onDeleteSong,
//    )
//}
//
//@Composable
//fun QueueScreenContent(
//    uiState: QueueScreenUiState,
//    onNavigateUp: () -> Unit,
//    onCreatePlaylist: ( String, List<Song> ) -> Unit,
//    onClearQueue: () -> Unit,
//    onFavorite: ( String ) -> Unit,
//    playSong: ( Song ) -> Unit,
//    onMoveSong: ( Int, Int ) -> Unit,
//    onPlayNext: ( Song ) -> Unit,
//    onViewAlbum: ( String ) -> Unit,
//    onViewArtist: ( String ) -> Unit,
//    onShareSong: ( Uri ) -> Unit,
//    onAddToQueue: ( Song ) -> Unit,
//    onAddSongsToPlaylist: (PlaylistInfo, List<Song> ) -> Unit,
//    onSearchSongsMatchingQuery: ( String ) -> List<Song>,
//    onGetPlaylists: () -> List<PlaylistInfo>,
//    onDeleteSong: ( Song ) -> Unit,
//) {
//
//    val fallbackResourceId =
//        if ( uiState.themeMode.isLight( LocalContext.current ) )
//            R.drawable.placeholder_light else R.drawable.placeholder_dark
//
//    var showSaveDialog by remember { mutableStateOf(false) }
//
//    Column(
//        modifier = Modifier.fillMaxSize()
//    ) {
//        QueueScreenTopAppBar(
//            onBackArrowClick = onNavigateUp,
//            onSaveClick = {
//                showSaveDialog = !showSaveDialog
//            },
//            onClearClick = {
//                onClearQueue()
//                onNavigateUp()
//            }
//        )
//        LoaderScaffold(
//            isLoading = uiState.isLoading,
//            loading = uiState.language.loading
//        ) {
//            QueueList(
//                uiState = uiState,
//                fallbackResourceId = fallbackResourceId,
//                isFavorite = { uiState.favoriteSongIds.contains(it) },
//                onFavorite = onFavorite,
//                playSong = playSong,
//                onMove = onMoveSong,
//                onPlayNext = onPlayNext,
//                onAddToQueue = onAddToQueue,
//                onShareSong = onShareSong,
//                onViewAlbum = onViewAlbum,
//                onViewArtist = onViewArtist,
//                onAddSongsToPlaylist = onAddSongsToPlaylist,
//                onSearchSongsMatchingQuery = onSearchSongsMatchingQuery,
//                onCreatePlaylist = onCreatePlaylist,
//                onGetPlaylists = { onGetPlaylists() },
//                onDeleteSong = onDeleteSong
//            )
//        }
//
//        if ( showSaveDialog ) {
//            NewPlaylistDialog(
//                language = uiState.language,
//                fallbackResourceId = fallbackResourceId,
//                initialSongsToAdd = uiState.songsInQueue,
//                onSearchSongsMatchingQuery = onSearchSongsMatchingQuery,
//                onConfirmation = { title, songs ->
//                    onCreatePlaylist( title, songs )
//                    showSaveDialog = false
//                },
//                onDismissRequest = { showSaveDialog = false }
//            )
//        }
//    }
//}
//
//@Preview( showSystemUi = true )
//@Composable
//fun QueueScreenContentPreview() {
//    QueueScreenContent(
//        uiState = testQueueScreenUiState,
//        onNavigateUp = {},
//        onCreatePlaylist = { _, _ -> },
//        onClearQueue = {},
//        onFavorite = {},
//        playSong = {},
//        onMoveSong = { _, _ ->  },
//        onPlayNext = {},
//        onAddToQueue = {},
//        onViewAlbum = {},
//        onViewArtist = {},
//        onShareSong = {},
//        onAddSongsToPlaylist = { _, _, -> },
//        onSearchSongsMatchingQuery = { emptyList() },
//        onGetPlaylists = { emptyList() },
//        onDeleteSong = {}
//    )
//}
//
