package com.squad.musicmatters.ui.artist
//
//import android.net.Uri
//import androidx.annotation.DrawableRes
//import androidx.compose.foundation.layout.Column
//import androidx.compose.foundation.layout.Spacer
//import androidx.compose.foundation.layout.fillMaxSize
//import androidx.compose.foundation.layout.height
//import androidx.compose.foundation.layout.padding
//import androidx.compose.material3.HorizontalDivider
//import androidx.compose.material3.MaterialTheme
//import androidx.compose.material3.Text
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.getValue
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.platform.LocalContext
//import androidx.compose.ui.text.font.FontWeight
//import androidx.compose.ui.tooling.preview.Preview
//import androidx.compose.ui.unit.dp
//import androidx.lifecycle.compose.collectAsStateWithLifecycle
//import coil.request.ImageRequest
//import com.squad.musicmatters.R
//import com.squad.musicmatters.core.data.preferences.SortSongsBy
//import com.squad.musicmatters.core.designsystem.theme.isLight
//import com.squad.musicmatters.core.i8n.Language
//import com.squad.musicmatters.core.model.Album
//import com.squad.musicmatters.core.model.Artist
//import com.squad.musicmatters.core.model.PlaylistInfo
//import com.squad.musicmatters.core.model.Song
//import com.squad.musicmatters.ui.components.AlbumRow
//import com.squad.musicmatters.ui.components.Banner
//import com.squad.musicmatters.ui.components.LoaderScaffold
//import com.squad.musicmatters.ui.components.MinimalAppBar
//import com.squad.musicmatters.ui.components.SongList
//
//
//@Composable
//fun ArtistScreen(
//    artistName: String,
//    viewModel: ArtistScreenViewModel,
//    onViewAlbum: ( String ) -> Unit,
//    onViewArtist: (String ) -> Unit,
//    onShareSong: ( Uri, String ) -> Unit,
//    onDeleteSong: ( Song ) -> Unit,
//    onNavigateBack: () -> Unit,
//) {
//
//    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
//
//    ArtistScreenContent(
//        artistName = artistName,
//        uiState = uiState,
//        onSortTypeChange = viewModel::setSortSongsBy,
//        onSortReverseChange = viewModel::setSortSongsInReverse,
//        onPlaySongsInAlbum = viewModel::playSongsInAlbum,
//        onShufflePlay = viewModel::shufflePlaySongsByArtist,
//        onFavorite = viewModel::addToFavorites,
//        playSong = {
//            viewModel.playSongs(
//                selectedSong = it,
//                songsInPlaylist = uiState.songsByArtist
//            )
//        },
//        onViewAlbum = { onViewAlbum( it ) },
//        onViewArtist = onViewArtist,
//        onNavigateBack = onNavigateBack,
//        onPlayNext = viewModel::playSongNext,
//        onAddToQueue = viewModel::addSongToQueue,
//        onSearchSongsMatchingQuery = viewModel::searchSongsMatching,
//        onCreatePlaylist = viewModel::createPlaylist,
//        onShareSong = { onShareSong( it, uiState.language.shareFailedX( "" ) ) },
//        onAddSongsToPlaylist = viewModel::addSongsToPlaylist,
//        onGetSongsInAlbum = viewModel::getSongsInAlbum,
//        onGetSongsInPlaylist = viewModel::getSongsInPlaylist,
//        onShufflePlaySongsInAlbum = viewModel::shufflePlaySongsInAlbum,
//        onPlaySongsInAlbumNext = viewModel::playSongsInAlbumNext,
//        onAddSongsInAlbumToQueue = viewModel::addSongsInAlbumToQueue,
//        onAddSongsByArtistToQueue = viewModel::addSongsByArtistToQueue,
//        onPlaySongsByArtistNext = viewModel::playSongsByArtistNext,
//        onShuffleAndPlaySongsByArtist = viewModel::shufflePlaySongsByArtist,
//        onGetPlaylists = { uiState.playlistInfos },
//        onDeleteSong = onDeleteSong,
//    )
//}
//
//@Composable
//fun ArtistScreenContent(
//    artistName: String,
//    uiState: ArtistScreenUiState,
//    onSortReverseChange: ( Boolean ) -> Unit,
//    onSortTypeChange: (SortSongsBy) -> Unit,
//    onPlaySongsInAlbum: (Album) -> Unit,
//    onShufflePlay: (Artist) -> Unit,
//    playSong: (Song) -> Unit,
//    onFavorite: ( String ) -> Unit,
//    onViewAlbum: ( String ) -> Unit,
//    onViewArtist: ( String ) -> Unit,
//    onShareSong: ( Uri ) -> Unit,
//    onPlayNext: ( Song ) -> Unit,
//    onAddToQueue: ( Song ) -> Unit,
//    onSearchSongsMatchingQuery: ( String ) -> List<Song>,
//    onCreatePlaylist: ( String, List<Song> ) -> Unit,
//    onAddSongsToPlaylist: (PlaylistInfo, List<Song> ) -> Unit,
//    onGetSongsInAlbum: ( Album ) -> List<Song>,
//    onGetSongsInPlaylist: (PlaylistInfo ) -> List<Song>,
//    onNavigateBack: () -> Unit,
//    onShufflePlaySongsInAlbum: ( Album ) -> Unit,
//    onPlaySongsInAlbumNext: ( Album ) -> Unit,
//    onAddSongsInAlbumToQueue: ( Album ) -> Unit,
//    onAddSongsByArtistToQueue: ( Artist ) -> Unit,
//    onPlaySongsByArtistNext: ( Artist ) -> Unit,
//    onShuffleAndPlaySongsByArtist: ( Artist ) -> Unit,
//    onGetPlaylists: () -> List<PlaylistInfo>,
//    onDeleteSong: ( Song ) -> Unit,
//    ) {
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
//            title = artistName
//        )
//        LoaderScaffold(
//            isLoading = uiState.isLoadingSongsByArtist,
//            loading = uiState.language.loading
//        ) {
//            SongList(
//                sortReverse = uiState.sortSongsByArtistInReverse,
//                sortSongsBy = uiState.sortSongsByArtistBy,
//                language = uiState.language,
//                songs = uiState.songsByArtist,
//                fallbackResourceId = fallbackResourceId,
//                onShufflePlay = { uiState.artist?.let { onShufflePlay( it ) } },
//                onSortTypeChange = onSortTypeChange,
//                onSortReverseChange = onSortReverseChange,
//                currentlyPlayingSongId = uiState.currentlyPlayingSongId,
//                playlistInfos = onGetPlaylists(),
//                playSong = playSong,
//                isFavorite = { uiState.favoriteSongIds.contains(it) },
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
//                leadingContent = {
//                    item {
//                        ArtistArtwork(
//                            artist = uiState.artist!!,
//                            language = uiState.language,
//                            fallbackResourceId = fallbackResourceId,
//                            onShufflePlay = onShuffleAndPlaySongsByArtist,
//                            onPlayNext = onPlaySongsByArtistNext,
//                            onAddToQueue = onAddSongsByArtistToQueue,
//                            onGetSongsByArtist = { uiState.songsByArtist },
//                            onGetPlaylists = onGetPlaylists,
//                            onGetSongsInPlaylist = onGetSongsInPlaylist,
//                            onAddSongsToPlaylist = onAddSongsToPlaylist,
//                            onSearchSongsMatchingQuery = onSearchSongsMatchingQuery,
//                            onCreatePlaylist = onCreatePlaylist
//                        )
//                    }
//                    if ( uiState.albumsByArtist.isNotEmpty() ) {
//                        item {
//                            Spacer( modifier = Modifier.height( 4.dp ) )
//                            Text(
//                                modifier = Modifier.padding( 12.dp ),
//                                text = uiState.language.albums,
//                                style = MaterialTheme.typography.titleMedium.copy(
//                                    fontWeight = FontWeight.Bold
//                                )
//                            )
//                            AlbumRow(
//                                albums = uiState.albumsByArtist,
//                                language = uiState.language,
//                                fallbackResourceId = fallbackResourceId,
//                                onPlaySongsInAlbum = { onPlaySongsInAlbum( it ) },
//                                onAddToQueue = onAddSongsInAlbumToQueue,
//                                onPlayNext = onPlaySongsInAlbumNext,
//                                onShufflePlay = onShufflePlaySongsInAlbum,
//                                onViewAlbum = onViewAlbum,
//                                onViewArtist = onViewArtist,
//                                onAddSongsToPlaylist = onAddSongsToPlaylist,
//                                onCreatePlaylist = onCreatePlaylist,
//                                onGetPlaylists = onGetPlaylists,
//                                onGetSongsInAlbum = onGetSongsInAlbum,
//                                onGetSongsInPlaylist = onGetSongsInPlaylist,
//                                onSearchSongsMatchingQuery = onSearchSongsMatchingQuery,
//                            )
//                            HorizontalDivider()
//                        }
//                    }
//                }
//            )
//        }
//    }
//}
//
//@Composable
//private fun ArtistArtwork(
//    artist: Artist,
//    language: Language,
//    @DrawableRes fallbackResourceId: Int,
//    onShufflePlay: ( Artist ) -> Unit,
//    onPlayNext: ( Artist ) -> Unit,
//    onAddToQueue: ( Artist ) -> Unit,
//    onGetSongsByArtist: ( Artist ) -> List<Song>,
//    onGetPlaylists: () -> List<PlaylistInfo>,
//    onGetSongsInPlaylist: (PlaylistInfo ) -> List<Song>,
//    onAddSongsToPlaylist: (PlaylistInfo, List<Song> ) -> Unit,
//    onSearchSongsMatchingQuery: (String ) -> List<Song>,
//    onCreatePlaylist: (String, List<Song> ) -> Unit,
//) {
//    Banner(
//        imageRequest = ImageRequest.Builder( LocalContext.current ).apply {
//            data( artist.artworkUri )
//            placeholder( fallbackResourceId )
//            fallback( fallbackResourceId )
//            error( fallbackResourceId )
//            crossfade( true )
//        }.build(),
//        bottomSheetHeaderTitle = language.artist,
//        bottomSheetHeaderDescription = artist.name,
//        language = language,
//        fallbackResourceId = fallbackResourceId,
//        onShufflePlay = { onShufflePlay( artist ) },
//        onPlayNext = { onPlayNext( artist ) },
//        onAddToQueue = { onAddToQueue( artist ) },
//        onAddSongsToPlaylist = onAddSongsToPlaylist,
//        onGetSongs = { onGetSongsByArtist( artist ) },
//        onSearchSongsMatchingQuery = onSearchSongsMatchingQuery,
//        onGetPlaylists = onGetPlaylists,
//        onCreatePlaylist = onCreatePlaylist,
//        onGetSongsInPlaylist = onGetSongsInPlaylist,
//    ) {
//        Column {
//            Text( text = artist.name )
//        }
//    }
//}
//
//@Preview( showSystemUi = true )
//@Composable
//fun ArtistScreenContentPreview() {
//    ArtistScreenContent(
//        artistName = "Travis Scott",
//        uiState = testArtistScreenUiState,
//        onSortReverseChange = {},
//        onSortTypeChange = {},
//        onShufflePlay = {},
//        playSong = {},
//        onFavorite = {},
//        onPlaySongsInAlbum = {},
//        onViewAlbum = {},
//        onViewArtist = {},
//        onShareSong = {},
//        onPlayNext = {},
//        onNavigateBack = {},
//        onAddToQueue = {},
//        onSearchSongsMatchingQuery = { emptyList() },
//        onCreatePlaylist = { _, _ -> },
//        onAddSongsToPlaylist = { _, _ -> },
//        onGetSongsInAlbum = { emptyList() },
//        onGetSongsInPlaylist = { emptyList() },
//        onShufflePlaySongsInAlbum = {},
//        onPlaySongsInAlbumNext = {},
//        onAddSongsInAlbumToQueue = {},
//        onAddSongsByArtistToQueue = {},
//        onPlaySongsByArtistNext = {},
//        onShuffleAndPlaySongsByArtist = {},
//        onGetPlaylists = { emptyList() },
//        onDeleteSong = {}
//    )
//}