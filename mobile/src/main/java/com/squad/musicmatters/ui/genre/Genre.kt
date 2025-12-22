package com.squad.musicmatters.ui.genre
//
//import android.net.Uri
//import androidx.compose.foundation.layout.Column
//import androidx.compose.foundation.layout.fillMaxSize
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.getValue
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.platform.LocalContext
//import androidx.compose.ui.tooling.preview.Preview
//import androidx.lifecycle.compose.collectAsStateWithLifecycle
//import com.squad.musicmatters.R
//import com.squad.musicmatters.core.designsystem.theme.MusicMattersTheme
//import com.squad.musicmatters.core.designsystem.theme.isLight
//import com.squad.musicmatters.core.model.PlaylistInfo
//import com.squad.musicmatters.core.model.Song
//import com.squad.musicmatters.ui.components.LoaderScaffold
//import com.squad.musicmatters.ui.components.MinimalAppBar
//import com.squad.musicmatters.ui.components.SongList
//
//@Composable
//fun GenreScreen(
//    genreName: String,
//    viewModel: GenreScreenViewModel,
//    onViewAlbum: ( String ) -> Unit,
//    onViewArtist: ( String ) -> Unit,
//    onShareSong: ( Uri, String ) -> Unit,
//    onDeleteSong: (Song ) -> Unit,
//    onNavigateBack: () -> Unit,
//) {
//
//    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
//
//    GenreScreenContent(
//        uiState = uiState,
//        genreName = genreName,
//        onSortReverseChange = viewModel::setSortSongsInReverse,
//        onSortTypeChange = viewModel::setSortSongsBy,
//        onShufflePlay = {
//            viewModel.shuffleAndPlay( songs = uiState.songsInGenre )
//        },
//        playSong = {
//            viewModel.playSongs(
//                selectedSong = it,
//                songsInPlaylist = uiState.songsInGenre
//            )
//        },
//        onFavorite = viewModel::addToFavorites,
//        onViewAlbum = onViewAlbum,
//        onViewArtist = onViewArtist,
//        onNavigateBack = onNavigateBack,
//        onPlayNext = viewModel::playSongNext,
//        onAddToQueue = viewModel::addSongToQueue,
//        onAddSongsToPlaylist = viewModel::addSongsToPlaylist,
//        onSearchSongsMatchingQuery = viewModel::searchSongsMatching,
//        onCreatePlaylist = viewModel::createPlaylist,
//        onShareSong = { onShareSong( it, uiState.language.shareFailedX( "" ) ) },
//        onGetPlaylists = { uiState.playlistInfos },
//        onGetSongsInPlaylist = viewModel::getSongsInPlaylist,
//        onDeleteSong = onDeleteSong
//    )
//}
//
//@Composable
//fun GenreScreenContent(
//    uiState: GenreScreenUiState,
//    genreName: String,
//    onSortReverseChange: ( Boolean ) -> Unit,
//    onSortTypeChange: (SortSongsBy) -> Unit,
//    onShufflePlay: () -> Unit,
//    playSong: (Song) -> Unit,
//    onFavorite: ( String ) -> Unit,
//    onViewAlbum: ( String ) -> Unit,
//    onViewArtist: ( String ) -> Unit,
//    onShareSong: ( Uri ) -> Unit,
//    onPlayNext: ( Song ) -> Unit,
//    onAddToQueue: ( Song ) -> Unit,
//    onAddSongsToPlaylist: (PlaylistInfo, List<Song> ) -> Unit,
//    onSearchSongsMatchingQuery: ( String ) -> List<Song>,
//    onCreatePlaylist: ( String, List<Song> ) -> Unit,
//    onNavigateBack: () -> Unit,
//    onGetPlaylists: () -> List<PlaylistInfo>,
//    onGetSongsInPlaylist: (PlaylistInfo ) -> List<Song>,
//    onDeleteSong: ( Song ) -> Unit,
//) {
//
//    val fallbackResourceId =
//        if ( uiState.themeMode.isLight( LocalContext.current ) )
//            R.drawable.placeholder_light else R.drawable.placeholder_dark
//
//    Column (
//        modifier = Modifier.fillMaxSize()
//    ) {
//        MinimalAppBar(
//            onNavigationIconClicked = onNavigateBack,
//            title = "${uiState.language.genre} - $genreName"
//        )
//        LoaderScaffold(
//            isLoading = uiState.isLoading,
//            loading = uiState.language.loading
//        ) {
//            SongList(
//                sortReverse = uiState.sortSongsInReverse,
//                onSortReverseChange = onSortReverseChange,
//                sortSongsBy = uiState.sortSongsBy,
//                onSortTypeChange = onSortTypeChange,
//                language = uiState.language,
//                songs = uiState.songsInGenre,
//                playlistInfos = onGetPlaylists(),
//                onShufflePlay = onShufflePlay,
//                fallbackResourceId = fallbackResourceId,
//                currentlyPlayingSongId = uiState.currentlyPlayingSongId,
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
//                onDeleteSong = onDeleteSong
//            )
//        }
//    }
//}
//
//@Preview( showSystemUi = true )
//@Composable
//fun GenreScreenContentPreview() {
//    MusicMattersTheme(
//        themeMode = SettingsDefaults.themeMode,
//        primaryColorName = SettingsDefaults.PRIMARY_COLOR_NAME,
//        fontName = SettingsDefaults.font,
//        fontScale = SettingsDefaults.FONT_SCALE,
//        useMaterialYou = true
//    ) {
//        GenreScreenContent(
//            uiState = testGenreScreenUiState,
//            genreName = "Hip Hop",
//            onSortReverseChange = {},
//            onSortTypeChange = {},
//            onShufflePlay = {},
//            playSong = {},
//            onFavorite = {},
//            onNavigateBack = {},
//            onViewAlbum = {},
//            onViewArtist = {},
//            onShareSong = {},
//            onPlayNext = {},
//            onAddToQueue = {},
//            onAddSongsToPlaylist = { _, _ -> },
//            onSearchSongsMatchingQuery = { emptyList() },
//            onCreatePlaylist = { _, _ -> },
//            onGetPlaylists = { emptyList() },
//            onGetSongsInPlaylist = { emptyList() },
//            onDeleteSong = {}
//        )
//    }
//}
//
