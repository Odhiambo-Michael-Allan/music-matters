package com.squad.musicmatters.ui.tree
//
//import android.net.Uri
//import androidx.compose.foundation.layout.Column
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.getValue
//import androidx.compose.ui.platform.LocalContext
//import androidx.compose.ui.tooling.preview.Preview
//import androidx.lifecycle.compose.collectAsStateWithLifecycle
//import com.squad.musicmatters.R
//import com.squad.musicmatters.core.designsystem.theme.MusicMattersTheme
//import com.squad.musicmatters.core.designsystem.theme.isLight
//import com.squad.musicmatters.core.model.PlaylistInfo
//import com.squad.musicmatters.core.model.Song
//import com.squad.musicmatters.ui.components.LoaderScaffold
//import com.squad.musicmatters.ui.components.TopAppBar
//import com.squad.musicmatters.ui.components.TreeSongList
//
//@Composable
//fun TreeScreen(
//    viewModel: TreeScreenViewModel,
//    onViewAlbum: ( String ) -> Unit,
//    onViewArtist: ( String ) -> Unit,
//    onShareSong: ( Uri, String ) -> Unit,
//    onNavigateToSearch: () -> Unit,
//    onSettingsClicked: () -> Unit,
//    onDeleteSong: ( Song ) -> Unit,
//) {
//
//    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
//
//    TreeScreenContent(
//        uiState = uiState,
//        togglePath = viewModel::togglePath,
//        onPlaySong = viewModel::playSelectedSong,
//        onAddSongsToPlaylist = viewModel::addSongsToPlaylist,
//        onSearchSongsMatchingQuery = viewModel::searchSongsMatching,
//        onCreatePlaylist = viewModel::createPlaylist,
//        onSettingsClicked = onSettingsClicked,
//        onPlayNext = viewModel::playSongNext,
//        onViewArtist = onViewArtist,
//        onViewAlbum = onViewAlbum,
//        onFavorite = viewModel::addToFavorites,
//        onAddToQueue = viewModel::addSongToQueue,
//        onShareSong = { onShareSong( it, uiState.language.shareFailedX( "" ) ) },
//        onNavigateToSearch = onNavigateToSearch,
//        onGetPlaylists = { uiState.playlistInfos },
//        onGetSongsInPlaylist = viewModel::getSongsInPlaylist,
//        onShufflePlay = viewModel::shuffleAndPlay,
//        onSortSongsByChange = viewModel::setSortSongsBy,
//        onSortSongsInReverseChange = viewModel::setSortSongsInReverse,
//        onSortPathsByChange = viewModel::setSortPathsBy,
//        onSortPathsInReverseChange = viewModel::setSortPathsInReverse,
//        onDeleteSong = onDeleteSong
//    )
//}
//
//@Composable
//fun TreeScreenContent(
//    uiState: TreeScreenUiState,
//    togglePath: ( String ) -> Unit,
//    onPlaySong: (Song) -> Unit,
//    onAddSongsToPlaylist: (PlaylistInfo, List<Song> ) -> Unit,
//    onSearchSongsMatchingQuery: ( String ) -> List<Song>,
//    onCreatePlaylist: ( String, List<Song> ) -> Unit,
//    onAddToQueue: ( Song ) -> Unit,
//    onFavorite: ( String ) -> Unit,
//    onViewArtist: ( String ) -> Unit,
//    onPlayNext: ( Song ) -> Unit,
//    onViewAlbum: ( String ) -> Unit,
//    onShareSong: ( Uri ) -> Unit,
//    onNavigateToSearch: () -> Unit,
//    onSettingsClicked: () -> Unit,
//    onGetPlaylists: () -> List<PlaylistInfo>,
//    onGetSongsInPlaylist: (PlaylistInfo ) -> List<Song>,
//    onShufflePlay: ( List<Song> ) -> Unit,
//    onSortSongsByChange: ( SortSongsBy ) -> Unit,
//    onSortSongsInReverseChange: ( Boolean ) -> Unit,
//    onSortPathsByChange: ( SortPathsBy ) -> Unit,
//    onSortPathsInReverseChange: ( Boolean ) -> Unit,
//    onDeleteSong: ( Song ) -> Unit,
//) {
//
//    val fallbackResourceId =
//        if ( uiState.themeMode.isLight( LocalContext.current ) )
//            R.drawable.placeholder_light else R.drawable.placeholder_dark
//
//    Column {
//        TopAppBar(
//            onNavigationIconClicked = onNavigateToSearch,
//            title = uiState.language.tree,
//            settings = uiState.language.settings,
//            onSettingsClicked = onSettingsClicked,
//        )
//        LoaderScaffold(
//            isLoading = uiState.isConstructingTree,
//            loading = uiState.language.loading
//        ) {
//            TreeSongList(
//                uiState = uiState,
//                togglePath = togglePath,
//                onPlaySong = onPlaySong,
//                fallbackResourceId = fallbackResourceId,
//                onAddSongsToPlaylist = onAddSongsToPlaylist,
//                onSearchSongsMatchingQuery = onSearchSongsMatchingQuery,
//                onCreatePlaylist = onCreatePlaylist,
//                onAddToQueue = onAddToQueue,
//                onFavorite = onFavorite,
//                onViewArtist = onViewArtist,
//                onPlayNext = onPlayNext,
//                onViewAlbum = onViewAlbum,
//                onShareSong = onShareSong,
//                onGetPlaylists = onGetPlaylists,
//                onGetSongsInPlaylist = onGetSongsInPlaylist,
//                onShufflePlay = onShufflePlay,
//                onSortSongsByChange = onSortSongsByChange,
//                onSortSongsInReverseChange = onSortSongsInReverseChange,
//                onSortPathsByChange = onSortPathsByChange,
//                onSortPathsInReverseChange = onSortPathsInReverseChange,
//                onDeleteSong = onDeleteSong,
//            )
//        }
//    }
//}
//
//@Preview( showSystemUi = true )
//@Composable
//fun TreeScreenContentPreview() {
//    MusicMattersTheme(
//        themeMode = SettingsDefaults.themeMode,
//        primaryColorName = SettingsDefaults.PRIMARY_COLOR_NAME,
//        fontName = SettingsDefaults.font,
//        fontScale = SettingsDefaults.FONT_SCALE,
//        useMaterialYou = true
//    ) {
//        TreeScreenContent(
//            uiState = testTreeScreenUiState,
//            togglePath = {},
//            onPlaySong = {},
//            onSettingsClicked = {},
//            onAddSongsToPlaylist = { _, _ -> },
//            onSearchSongsMatchingQuery = { emptyList() },
//            onCreatePlaylist = { _, _ -> },
//            onAddToQueue = {},
//            onFavorite = {},
//            onPlayNext = {},
//            onShareSong = {},
//            onViewAlbum = {},
//            onViewArtist = {},
//            onNavigateToSearch = {},
//            onGetPlaylists = { emptyList() },
//            onShufflePlay = {},
//            onGetSongsInPlaylist = { emptyList() },
//            onSortSongsByChange = {},
//            onSortSongsInReverseChange = {},
//            onSortPathsByChange = {},
//            onSortPathsInReverseChange = {},
//            onDeleteSong = {}
//        )
//    }
//}