package com.squad.musicmatters.ui.albums
//
//import androidx.compose.foundation.layout.Column
//import androidx.compose.foundation.layout.fillMaxSize
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.getValue
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.platform.LocalContext
//import androidx.compose.ui.tooling.preview.Preview
//import androidx.lifecycle.compose.collectAsStateWithLifecycle
//import com.squad.musicmatters.R
//import com.squad.musicmatters.core.data.preferences.SortAlbumsBy
//import com.squad.musicmatters.core.data.preferences.impl.SettingsDefaults
//import com.squad.musicmatters.core.designsystem.theme.MusicMattersTheme
//import com.squad.musicmatters.core.designsystem.theme.isLight
//import com.squad.musicmatters.core.model.Album
//import com.squad.musicmatters.core.model.PlaylistInfo
//import com.squad.musicmatters.core.model.Song
//import com.squad.musicmatters.ui.components.AlbumGrid
//import com.squad.musicmatters.ui.components.LoaderScaffold
//import com.squad.musicmatters.ui.components.TopAppBar
//
//@Composable
//fun AlbumsScreen(
//    viewModel: AlbumsScreenViewModel,
//    onAlbumClick: ( String ) -> Unit,
//    onNavigateToSearch: () -> Unit,
//    onSettingsClicked: () -> Unit,
//    onViewArtist: (String ) -> Unit,
//) {
//
//    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
//
//    AlbumsScreenContent(
//        uiState = uiState,
//        onAlbumClick = onAlbumClick,
//        onSettingsClicked = onSettingsClicked,
//        onPlayAlbum = viewModel::playSongsInAlbum,
//        onNavigateToSearch = onNavigateToSearch,
//        onViewArtist = onViewArtist,
//        onPlayNext = viewModel::playSongsInAlbumNext,
//        onShufflePlay = viewModel::shufflePlaySongsInAlbum,
//        onAddToQueue = viewModel::addSongsInAlbumToQueue,
//        onSearchSongsMatchingQuery = viewModel::searchSongsMatching,
//        onCreatePlaylist = viewModel::createPlaylist,
//        onAddSongsToPlaylist = viewModel::addSongsToPlaylist,
//        onGetPlaylists = { uiState.playlistInfos },
//        onGetSongsInAlbum = viewModel::getSongsInAlbum,
//        onGetSongsInPlaylist = viewModel::getSongsInPlaylist,
//        onSortTypeChange = viewModel::setSortAlbumsBy,
//        onSortReverseChange = viewModel::setSortAlbumsInReverse
//    )
//}
//
//@Composable
//fun AlbumsScreenContent(
//    uiState: AlbumsScreenUiState,
//    onAlbumClick: ( String ) -> Unit,
//    onSettingsClicked: () -> Unit,
//    onPlayAlbum: ( Album ) -> Unit,
//    onNavigateToSearch: () -> Unit,
//    onViewArtist: ( String ) -> Unit,
//    onPlayNext: (Album) -> Unit,
//    onShufflePlay: ( Album ) -> Unit,
//    onAddToQueue: ( Album ) -> Unit,
//    onAddSongsToPlaylist: (PlaylistInfo, List<Song> ) -> Unit,
//    onCreatePlaylist: ( String, List<Song> ) -> Unit,
//    onGetPlaylists: () -> List<PlaylistInfo>,
//    onGetSongsInAlbum: ( Album ) -> List<Song>,
//    onGetSongsInPlaylist: (PlaylistInfo ) -> List<Song>,
//    onSearchSongsMatchingQuery: ( String ) -> List<Song>,
//    onSortTypeChange: ( SortAlbumsBy ) -> Unit,
//    onSortReverseChange: ( Boolean ) -> Unit,
//) {
//
//    val fallbackResourceId =
//        if ( uiState.themeMode.isLight( LocalContext.current ) )
//            R.drawable.placeholder_light else R.drawable.placeholder_dark
//
//    Column (
//        modifier = Modifier.fillMaxSize()
//    ) {
//        TopAppBar(
//            onNavigationIconClicked = onNavigateToSearch,
//            title = uiState.language.albums,
//            settings = uiState.language.settings,
//            onSettingsClicked = onSettingsClicked
//        )
//        LoaderScaffold(
//            isLoading = uiState.isLoadingAlbums,
//            loading = uiState.language.loading
//        ) {
//            AlbumGrid(
//                albums = uiState.albums,
//                language = uiState.language,
//                sortType = uiState.sortAlbumsBy,
//                sortReverse = uiState.sortAlbumsInReverse,
//                fallbackResourceId = fallbackResourceId,
//                onSortReverseChange = onSortReverseChange,
//                onSortTypeChange = onSortTypeChange,
//                onAlbumClick = onAlbumClick,
//                onPlayAlbum = onPlayAlbum,
//                onViewArtist = onViewArtist,
//                onPlayNext = onPlayNext,
//                onShufflePlay = onShufflePlay,
//                onAddToQueue = onAddToQueue,
//                onAddSongsToPlaylist = onAddSongsToPlaylist,
//                onCreatePlaylist = onCreatePlaylist,
//                onGetPlaylists = onGetPlaylists,
//                onGetSongsInAlbum = onGetSongsInAlbum,
//                onGetSongsInPlaylist = onGetSongsInPlaylist,
//                onSearchSongsMatchingQuery = onSearchSongsMatchingQuery,
//            )
//        }
//    }
//}
//
//@Preview( showSystemUi = true )
//@Composable
//fun AlbumsScreenContentPreview() {
//    MusicMattersTheme(
//        themeMode = SettingsDefaults.themeMode,
//        primaryColorName = SettingsDefaults.PRIMARY_COLOR_NAME,
//        fontName = SettingsDefaults.font,
//        fontScale = SettingsDefaults.FONT_SCALE,
//        useMaterialYou = true
//    ) {
//        AlbumsScreenContent(
//            uiState = testAlbumsScreenUiState,
//            onAlbumClick = {},
//            onSettingsClicked = {},
//            onPlayAlbum = {},
//            onNavigateToSearch = {},
//            onViewArtist = {},
//            onPlayNext = {},
//            onShufflePlay = {},
//            onAddToQueue = {},
//            onSearchSongsMatchingQuery = { emptyList() },
//            onCreatePlaylist = { _, _ -> },
//            onAddSongsToPlaylist = { _, _ -> },
//            onGetPlaylists = { emptyList() },
//            onGetSongsInAlbum = { emptyList() },
//            onGetSongsInPlaylist = { emptyList() },
//            onSortTypeChange = {},
//            onSortReverseChange = {}
//        )
//    }
//}