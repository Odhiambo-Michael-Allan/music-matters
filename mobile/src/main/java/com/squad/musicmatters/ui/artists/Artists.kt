package com.squad.musicmatters.ui.artists
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
//import com.squad.musicmatters.core.data.preferences.SortArtistsBy
//import com.squad.musicmatters.core.designsystem.theme.isLight
//import com.squad.musicmatters.core.model.Artist
//import com.squad.musicmatters.core.model.PlaylistInfo
//import com.squad.musicmatters.core.model.Song
//import com.squad.musicmatters.ui.components.ArtistsGrid
//import com.squad.musicmatters.ui.components.LoaderScaffold
//import com.squad.musicmatters.ui.components.TopAppBar
//
//@Composable
//fun ArtistsScreen(
//    viewModel: ArtistsScreenViewModel,
//    onArtistClick: ( String ) -> Unit,
//    onNavigateToSearch: () -> Unit,
//    onSettingsClicked: () -> Unit
//) {
//
//    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
//
//    ArtistsScreenContent(
//        uiState = uiState,
//        onArtistClick = onArtistClick,
//        onPlaySongsByArtist = viewModel::playSongsByArtist,
//        onSettingsClicked = onSettingsClicked,
//        onNavigateToSearch = onNavigateToSearch,
//        onPlaySongsByArtistNext = viewModel::playSongsByArtistNext,
//        onShufflePlaySongsByArtist = viewModel::shufflePlaySongsByArtist,
//        onAddSongsByArtistToQueue = viewModel::addSongsByArtistToQueue,
//        onAddSongsToPlaylist = viewModel::addSongsToPlaylist,
//        onGetSongsByArtist = viewModel::getSongsByArtist,
//        onGetPlaylists = { uiState.playlistInfos },
//        onCreatePlaylist = viewModel::createPlaylist,
//        onSortTypeChange = viewModel::setSortArtistsBy,
//        onSortReverseChange = viewModel::setSortArtistsInReverse,
//        onGetSongsInPlaylist = viewModel::getSongsInPlaylist,
//        onSearchSongsMatchingQuery = viewModel::searchSongsMatching
//    )
//}
//
//@Composable
//fun ArtistsScreenContent(
//    uiState: ArtistsScreenUiState,
//    onArtistClick: ( String ) -> Unit,
//    onPlaySongsByArtist: (Artist) -> Unit,
//    onSettingsClicked: () -> Unit,
//    onNavigateToSearch: () -> Unit,
//    onSortReverseChange: ( Boolean ) -> Unit,
//    onSortTypeChange: (SortArtistsBy) -> Unit,
//    onAddSongsByArtistToQueue: ( Artist ) -> Unit,
//    onShufflePlaySongsByArtist: ( Artist ) -> Unit,
//    onPlaySongsByArtistNext: ( Artist ) -> Unit,
//    onAddSongsToPlaylist: (PlaylistInfo, List<Song> ) -> Unit,
//    onGetSongsByArtist: ( Artist ) -> List<Song>,
//    onGetPlaylists: () -> List<PlaylistInfo>,
//    onCreatePlaylist: ( String, List<Song> ) -> Unit,
//    onSearchSongsMatchingQuery: ( String ) -> List<Song>,
//    onGetSongsInPlaylist: (PlaylistInfo ) -> List<Song>,
//    ) {
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
//            title = uiState.language.artists,
//            settings = uiState.language.settings,
//            onSettingsClicked = onSettingsClicked
//        )
//        LoaderScaffold(
//            isLoading = uiState.isLoadingArtists,
//            loading = uiState.language.loading
//        ) {
//            ArtistsGrid(
//                artists = uiState.artists,
//                language = uiState.language,
//                sortBy = uiState.sortArtistsBy,
//                sortReverse = uiState.sortArtistsInReverse,
//                fallbackResourceId = fallbackResourceId,
//                onSortReverseChange = onSortReverseChange,
//                onSortTypeChange = onSortTypeChange,
//                onArtistClick = onArtistClick,
//                onPlaySongsByArtist = onPlaySongsByArtist,
//                onAddSongsByArtistToQueue = onAddSongsByArtistToQueue,
//                onShufflePlaySongsByArtist = onShufflePlaySongsByArtist,
//                onPlaySongsByArtistNext = onPlaySongsByArtistNext,
//                onAddSongsByArtistToPlaylist = onAddSongsToPlaylist,
//                onGetSongsByArtist = onGetSongsByArtist,
//                onGetPlaylists = onGetPlaylists,
//                onCreatePlaylist = onCreatePlaylist,
//                onSearchSongsMatchingQuery = onSearchSongsMatchingQuery,
//                onGetSongsInPlaylist = onGetSongsInPlaylist,
//            )
//        }
//    }
//}
//
//@Preview( showSystemUi = true )
//@Composable
//fun ArtistsScreenContentPreview() {
//    ArtistsScreenContent(
//        uiState = testArtistsScreenUiState,
//        onArtistClick = {},
//        onPlaySongsByArtist = {},
//        onSettingsClicked = {},
//        onNavigateToSearch = {},
//        onAddSongsByArtistToQueue = {},
//        onShufflePlaySongsByArtist = {},
//        onPlaySongsByArtistNext = {},
//        onAddSongsToPlaylist = { _, _ -> },
//        onCreatePlaylist = { _, _ -> },
//        onGetPlaylists = { emptyList() },
//        onGetSongsInPlaylist = { emptyList() },
//        onSearchSongsMatchingQuery = { emptyList() },
//        onGetSongsByArtist = { emptyList() },
//        onSortTypeChange = {},
//        onSortReverseChange = {}
//    )
//}