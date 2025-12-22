package com.squad.musicmatters.ui.playlist
//
//import android.net.Uri
//import androidx.annotation.DrawableRes
//import androidx.compose.foundation.layout.Box
//import androidx.compose.foundation.layout.Column
//import androidx.compose.foundation.layout.fillMaxSize
//import androidx.compose.foundation.layout.fillMaxWidth
//import androidx.compose.foundation.layout.wrapContentHeight
//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.automirrored.filled.PlaylistPlay
//import androidx.compose.material.icons.filled.Delete
//import androidx.compose.material.icons.filled.Edit
//import androidx.compose.material.icons.filled.MoreVert
//import androidx.compose.material3.CircularProgressIndicator
//import androidx.compose.material3.ExperimentalMaterial3Api
//import androidx.compose.material3.Icon
//import androidx.compose.material3.IconButton
//import androidx.compose.material3.ModalBottomSheet
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.getValue
//import androidx.compose.runtime.mutableStateOf
//import androidx.compose.runtime.remember
//import androidx.compose.runtime.setValue
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.platform.LocalContext
//import androidx.compose.ui.tooling.preview.Preview
//import androidx.lifecycle.compose.collectAsStateWithLifecycle
//import coil.request.ImageRequest
//import com.squad.musicmatters.R
//import com.squad.musicmatters.core.data.preferences.SortSongsBy
//import com.squad.musicmatters.core.data.preferences.impl.SettingsDefaults
//import com.squad.musicmatters.core.testing.playlists.testPlaylistInfos
//import com.squad.musicmatters.core.designsystem.theme.MusicMattersTheme
//import com.squad.musicmatters.core.designsystem.theme.isLight
//import com.squad.musicmatters.core.i8n.English
//import com.squad.musicmatters.core.i8n.Language
//import com.squad.musicmatters.core.model.PlaylistInfo
//import com.squad.musicmatters.core.model.Song
//import com.squad.musicmatters.ui.components.BottomSheetMenuItem
//import com.squad.musicmatters.ui.components.GenericOptionsBottomSheet
//import com.squad.musicmatters.ui.components.LoaderScaffold
//import com.squad.musicmatters.ui.components.MinimalAppBar
//import com.squad.musicmatters.ui.components.RenamePlaylistDialog
//import com.squad.musicmatters.ui.components.SongList
//
//@Composable
//fun PlaylistScreen(
//    playlistTitle: String,
//    viewModel: PlaylistScreenViewModel,
//    onViewAlbum: ( String ) -> Unit,
//    onViewArtist: ( String ) -> Unit,
//    onShareSong: ( Uri, String ) -> Unit,
//    onDeleteSong: ( Song ) -> Unit,
//    onNavigateBack: () -> Unit,
//) {
//    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
//
//    PlaylistScreenContent(
//        playlistTitle = uiState.playlistInfo.title.ifEmpty { playlistTitle },
//        uiState = uiState,
//        onSortReverseChange = viewModel::setSortSongsInReverse,
//        onSortTypeChange = viewModel::setSortSongsBy,
//        onShufflePlay = { viewModel.shuffleAndPlay( songs = uiState.songsInPlaylist ) },
//        playSong = {
//            viewModel.playSongs(
//                selectedSong = it,
//                songsInPlaylist = uiState.songsInPlaylist
//            )
//        },
//        onFavorite = viewModel::addToFavorites,
//        onViewAlbum = onViewAlbum,
//        onViewArtist = onViewArtist,
//        onNavigateBack = onNavigateBack,
//        onPlayNext = viewModel::playSongNext,
//        onAddToQueue = viewModel::addSongToQueue,
//        onAddSongsToPlaylist = { playlist, songs ->
//            viewModel.addSongsToPlaylist( playlist, songs )
//        },
//        onSearchSongsMatchingQuery = viewModel::searchSongsMatching,
//        onCreatePlaylist = { title, songs ->
//            viewModel.createPlaylist( title, songs )
//        },
//        onShareSong = { onShareSong( it, uiState.language.shareFailedX( "" ) ) },
//        onGetPlaylists = { uiState.playlistInfos },
//        onGetSongsInPlaylist = viewModel::getSongsInPlaylist,
//        onPlaySongsInPlaylistNext = { viewModel.playSongsNext( songs = uiState.songsInPlaylist ) },
//        onAddSongsInPlaylistToQueue = { viewModel.addSongsToQueue( songs = uiState.songsInPlaylist ) },
//        onRenamePlaylist = { playlist, newTitle -> viewModel.renamePlaylist( playlist, newTitle ) },
//        playlistIsDeletable = viewModel::isPlaylistDeletable,
//        onDeletePlaylist = {
//            onNavigateBack()
//            viewModel.deletePlaylist( it )
//        },
//        onDeleteSong = onDeleteSong
//    )
//}
//
//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun PlaylistScreenContent(
//    playlistTitle: String,
//    uiState: PlaylistScreenUiState,
//    playlistIsDeletable: (PlaylistInfo ) -> Boolean,
//    onSortReverseChange: ( Boolean ) -> Unit,
//    onSortTypeChange: ( SortSongsBy ) -> Unit,
//    onShufflePlay: () -> Unit,
//    playSong: ( Song ) -> Unit,
//    onFavorite: ( String ) -> Unit,
//    onViewAlbum: ( String ) -> Unit,
//    onViewArtist: ( String ) -> Unit,
//    onShareSong: ( Uri ) -> Unit,
//    onPlayNext: ( Song ) -> Unit,
//    onAddToQueue: ( Song ) -> Unit,
//    onAddSongsToPlaylist: ( PlaylistInfo, List<Song> ) -> Unit,
//    onSearchSongsMatchingQuery: ( String ) -> List<Song>,
//    onCreatePlaylist: ( String, List<Song> ) -> Unit,
//    onNavigateBack: () -> Unit,
//    onGetPlaylists: () -> List<PlaylistInfo>,
//    onGetSongsInPlaylist: ( PlaylistInfo ) -> List<Song>,
//    onAddSongsInPlaylistToQueue: () -> Unit,
//    onPlaySongsInPlaylistNext: () -> Unit,
//    onRenamePlaylist: ( playlistInfo: PlaylistInfo, String ) -> Unit,
//    onDeletePlaylist: ( PlaylistInfo ) -> Unit,
//    onDeleteSong: ( Song ) -> Unit,
//) {
//
//    val fallbackResourceId =
//        if ( uiState.themeMode.isLight( LocalContext.current ) )
//            R.drawable.placeholder_light else R.drawable.placeholder_dark
//
//    var showOptionsMenu by remember { mutableStateOf( false ) }
//
//    Column (
//        modifier = Modifier.fillMaxSize()
//    ) {
//        MinimalAppBar(
//            onNavigationIconClicked = onNavigateBack,
//            title = playlistTitle,
//            options = {
//                IconButton(
//                    onClick = { showOptionsMenu = !showOptionsMenu }
//                ) {
//                    Icon(
//                        imageVector = Icons.Default.MoreVert,
//                        contentDescription = null
//                    )
//                    if ( showOptionsMenu ) {
//                        ModalBottomSheet(
//                            onDismissRequest = { showOptionsMenu = false }
//                        ) {
//                            PlaylistBottomSheetMenu(
//                                isLoading = uiState.isLoadingSongsInPlaylist,
//                                playlistInfo = uiState.playlistInfo,
//                                fallbackResourceId = fallbackResourceId,
//                                language = uiState.language,
//                                playlistIsDeletable = playlistIsDeletable( uiState.playlistInfo ),
//                                onGetSongsInPlaylist = onGetSongsInPlaylist,
//                                onShufflePlay = onShufflePlay,
//                                onPlayNext = onPlaySongsInPlaylistNext,
//                                onAddToQueue = onAddSongsInPlaylistToQueue,
//                                onDismissRequest = { showOptionsMenu = false },
//                                onSearchSongsMatchingQuery = onSearchSongsMatchingQuery,
//                                onGetPlaylists = onGetPlaylists,
//                                onGetSongs = { onGetSongsInPlaylist( uiState.playlistInfo ) },
//                                onAddSongsToPlaylist = onAddSongsToPlaylist,
//                                onCreatePlaylist = onCreatePlaylist,
//                                onRenamePlaylist = onRenamePlaylist,
//                                onDeletePlaylist = onDeletePlaylist
//                            )
//                        }
//                    }
//                }
//            }
//        )
//        LoaderScaffold(
//            isLoading = uiState.isLoadingSongsInPlaylist,
//            loading = uiState.language.loading
//        ) {
//            SongList(
//                sortReverse = uiState.sortSongsInReverse,
//                onSortReverseChange = onSortReverseChange,
//                sortSongsBy = uiState.sortSongsBy,
//                onSortTypeChange = onSortTypeChange,
//                language = uiState.language,
//                songs = uiState.songsInPlaylist,
//                onShufflePlay = onShufflePlay,
//                fallbackResourceId = fallbackResourceId,
//                currentlyPlayingSongId = uiState.currentlyPlayingSongId,
//                playlistInfos = onGetPlaylists(),
//                playSong = playSong,
//                isFavorite = { uiState.favoriteSongIds.contains( it ) },
//                onFavorite = onFavorite,
//                onViewAlbum = onViewAlbum,
//                onViewArtist = onViewArtist,
//                onShareSong = onShareSong,
//                onPlayNext = onPlayNext,
//                onAddToQueue = onAddToQueue,
//                onGetSongsInPlaylist = onGetSongsInPlaylist,
//                onAddSongsToPlaylist = onAddSongsToPlaylist,
//                onSearchSongsMatchingQuery = onSearchSongsMatchingQuery,
//                onCreatePlaylist = onCreatePlaylist,
//                onGetAdditionalMetadataForSongWithId = { songId ->
//                    uiState.songsAdditionalMetadataList.find { it.songId == songId }
//                },
//                onDeleteSong = onDeleteSong,
//            )
//        }
//    }
//}
//
//@Composable
//fun PlaylistBottomSheetMenu(
//    isLoading: Boolean,
//    playlistInfo: PlaylistInfo,
//    @DrawableRes fallbackResourceId: Int,
//    language: Language,
//    playlistIsDeletable: Boolean,
//    onGetSongsInPlaylist: (PlaylistInfo ) -> List<Song>,
//    onShufflePlay: () -> Unit,
//    onPlayNext: () -> Unit,
//    onAddToQueue: () -> Unit,
//    onAddSongsToPlaylist: (PlaylistInfo, List<Song> ) -> Unit,
//    onGetPlaylists: () -> List<PlaylistInfo>,
//    onCreatePlaylist: (String, List<Song>) -> Unit,
//    onGetSongs: () -> List<Song>,
//    onSearchSongsMatchingQuery: ( String ) -> List<Song>,
//    onDismissRequest: () -> Unit,
//    onRenamePlaylist: (PlaylistInfo, String ) -> Unit,
//    onDeletePlaylist: (PlaylistInfo ) -> Unit
//) {
//
//    var showRenamePlaylistDialog by remember { mutableStateOf( false ) }
//
//    Box(
//        modifier = Modifier
//            .fillMaxWidth()
//            .wrapContentHeight(),
//        contentAlignment = Alignment.Center
//    ) {
//        if ( isLoading ) {
//            CircularProgressIndicator(
//                modifier = Modifier.align( Alignment.TopCenter )
//            )
//        } else {
//            GenericOptionsBottomSheet(
//                headerImage = ImageRequest.Builder( LocalContext.current ).apply {
//                    data( onGetSongsInPlaylist( playlistInfo ).firstOrNull { it.artworkUri != null }?.artworkUri )
//                    placeholder( fallbackResourceId )
//                    error( fallbackResourceId )
//                    crossfade( true )
//                }.build(),
//                headerTitle = playlistInfo.title,
//                headerDescription = language.playlist,
//                language = language,
//                fallbackResourceId = fallbackResourceId,
//                onDismissRequest = onDismissRequest,
////                onShufflePlay = onShufflePlay,
//                onPlayNext = onPlayNext,
//                onAddToQueue = onAddToQueue,
//                onAddSongsToPlaylist = onAddSongsToPlaylist,
//                onGetPlaylists = onGetPlaylists,
//                onCreatePlaylist = onCreatePlaylist,
//                onGetSongs = onGetSongs,
//                onGetSongsInPlaylist = onGetSongsInPlaylist,
//                onSearchSongsMatchingQuery = onSearchSongsMatchingQuery,
//                leadingBottomSheetMenuItem = {
//                    BottomSheetMenuItem(
//                        leadingIcon = Icons.AutoMirrored.Filled.PlaylistPlay,
//                        label = language.shufflePlay
//                    ) {
//                        onDismissRequest()
//                        onShufflePlay()
//                    }
//                },
//                trailingBottomSheetMenuItems = {
//                    if ( playlistIsDeletable ) {
//                        BottomSheetMenuItem(
//                            leadingIcon = Icons.Default.Edit,
//                            label = language.rename,
//                            onClick = {
//                                showRenamePlaylistDialog = true
//                            }
//                        )
//                    }
//                    if ( playlistIsDeletable ) {
//                        BottomSheetMenuItem(
//                            leadingIcon = Icons.Default.Delete,
//                            leadingIconTint = Color.Red,
//                            label = language.delete,
//                            onClick = {
//                                onDismissRequest()
//                                onDeletePlaylist( playlistInfo )
//                            }
//                        )
//                    }
//                }
//            )
//        }
//        if ( showRenamePlaylistDialog ) {
//            RenamePlaylistDialog(
//                playlistTitle = playlistInfo.title,
//                language = language,
//                onRename = { onRenamePlaylist( playlistInfo, it ) }
//            ) {
//                showRenamePlaylistDialog = false
//                onDismissRequest()
//            }
//        }
//    }
//}
//
//@Preview( showSystemUi = true )
//@Composable
//fun PlaylistBottomSheetMenuPreview() {
//    MusicMattersTheme(
//        themeMode = SettingsDefaults.themeMode,
//        primaryColorName = SettingsDefaults.PRIMARY_COLOR_NAME,
//        fontName = SettingsDefaults.font,
//        fontScale = SettingsDefaults.FONT_SCALE,
//        useMaterialYou = SettingsDefaults.USE_MATERIAL_YOU
//    ) {
//        PlaylistBottomSheetMenu(
//            isLoading = false,
//            playlistInfo = testPlaylistInfos.first(),
//            fallbackResourceId = R.drawable.placeholder_light,
//            language = English,
//            playlistIsDeletable = true,
//            onGetSongsInPlaylist = { emptyList() },
//            onPlayNext = {},
//            onShufflePlay = {},
//            onAddToQueue = {},
//            onGetPlaylists = { emptyList() },
//            onCreatePlaylist = { _, _ -> },
//            onGetSongs = { emptyList() },
//            onAddSongsToPlaylist = { _, _ -> },
//            onSearchSongsMatchingQuery = { emptyList() },
//            onRenamePlaylist = { _, _ -> },
//            onDismissRequest = {},
//            onDeletePlaylist = {}
//        )
//    }
//}
//
//@Preview( showSystemUi = true )
//@Composable
//fun PlaylistScreenContentPreview() {
//    PlaylistScreenContent(
//        playlistTitle = "Favorites",
//        uiState = testPlaylistScreenUiState,
//        playlistIsDeletable = { true },
//        onFavorite = {},
//        onShufflePlay = {},
//        onSortTypeChange = {},
//        onSortReverseChange = {},
//        playSong = {},
//        onViewAlbum = {},
//        onViewArtist = {},
//        onNavigateBack = {},
//        onShareSong = {},
//        onPlayNext = {},
//        onAddToQueue = {},
//        onAddSongsToPlaylist = { _, _ -> },
//        onSearchSongsMatchingQuery = { emptyList() },
//        onCreatePlaylist = { _, _ -> },
//        onGetPlaylists = { emptyList() },
//        onGetSongsInPlaylist = { emptyList() },
//        onAddSongsInPlaylistToQueue = {},
//        onPlaySongsInPlaylistNext = {},
//        onRenamePlaylist = { _, _ -> },
//        onDeletePlaylist = {},
//        onDeleteSong = {}
//    )
//}