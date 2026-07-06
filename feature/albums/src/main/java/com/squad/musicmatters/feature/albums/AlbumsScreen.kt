package com.squad.musicmatters.feature.albums

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.squad.musicmatters.core.datastore.DefaultPreferences
import com.squad.musicmatters.core.designsystem.theme.MusicMattersTheme
import com.squad.musicmatters.core.designsystem.theme.SupportedFonts
import com.squad.musicmatters.core.model.Album
import com.squad.musicmatters.core.model.Playlist
import com.squad.musicmatters.core.model.Song
import com.squad.musicmatters.core.model.SortAlbumsBy
import com.squad.musicmatters.core.ui.LibraryDestinationContainer
import com.squad.musicmatters.core.ui.MusicMattersPreviewParametersProvider
import com.squad.musicmatters.core.ui.PreviewData
import com.squad.musicmatters.feature.albums.components.AlbumGrid

@Composable
internal fun AlbumsScreen(
    viewModel: AlbumsScreenViewModel = hiltViewModel(),
    onViewAlbum: ( Album ) -> Unit,
    onViewArtist: ( String ) -> Unit,
    onShowSnackBar: ( String ) -> Unit,
    onNavigateBack: () -> Unit,
) {

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LibraryDestinationContainer(
        isLoading = uiState is AlbumsScreenUiState.Loading,
        onNavigateBack = onNavigateBack,
    ) {
        AlbumsScreenContent(
            uiState = uiState,
            onViewAlbum = onViewAlbum,
            onViewAlbumArtist = onViewArtist,
            onShowSnackBar = onShowSnackBar,
            onSortTypeChange = viewModel::onSortTypeChange,
            onSortInReverseChange = viewModel::onSortInReverseChange,
            onPlaySongsInAlbum = viewModel::playSongs,
            onAddSongsInAlbumToQueue = viewModel::addSongsToQueue,
            onPlaySongsInAlbumNext = viewModel::playSongsNext,
            onShuffleAndPlaySongsInAlbum = viewModel::shuffleAndPlay,
            onAddSongsToPlaylist = viewModel::addSongsToPlaylist,
            onCreatePlaylist = viewModel::createPlaylist,
            onShowAddToQueueOption = viewModel::noSongInTheAlbumPresentInTheQueue,
            onRemoveSongsInAlbumFromQueue = viewModel::removeSongsFromQueue,

            )
    }

}

@OptIn( ExperimentalMaterial3Api::class )
@Composable
private fun AlbumsScreenContent(
    uiState: AlbumsScreenUiState,
    onSortTypeChange: ( SortAlbumsBy ) -> Unit,
    onSortInReverseChange: ( Boolean ) -> Unit,
    onViewAlbum: ( Album ) -> Unit,
    onPlaySongsInAlbum: ( Song, List<Song> ) -> Unit,
    onAddSongsInAlbumToQueue: ( List<Song> ) -> Unit,
    onPlaySongsInAlbumNext: ( List<Song> ) -> Unit,
    onShuffleAndPlaySongsInAlbum: ( List<Song> ) -> Unit,
    onViewAlbumArtist: ( String ) -> Unit,
    onAddSongsToPlaylist: (Playlist, List<Song> ) -> Unit,
    onCreatePlaylist: ( String, List<Song> ) -> Unit,
    onShowSnackBar: ( String ) -> Unit,
    onShowAddToQueueOption: ( List<Song> ) -> Boolean,
    onRemoveSongsInAlbumFromQueue: ( List<Song> ) -> Unit,
) {

    when ( uiState ) {
        AlbumsScreenUiState.Loading -> {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxSize()
            ) {
                CircularProgressIndicator()
            }
        }

        is AlbumsScreenUiState.Success -> {
            AlbumGrid(
                albums = uiState.albums,
                sortBy = uiState.sortAlbumsBy,
                sortInReverse = uiState.sortAlbumsInReverse,
                onSortTypeChange = onSortTypeChange,
                onSortInReverseChange = onSortInReverseChange,
                onViewAlbum = onViewAlbum,
                onPlaySongsInAlbum = {
                    val songsInAlbum = uiState.songs.filter { song -> song.albumId == it.id }
                    onPlaySongsInAlbum( songsInAlbum.first(), uiState.songs )
                },
                onAddSongsInAlbumToQueue = {
                    onAddSongsInAlbumToQueue(
                        uiState.songs.filter { song -> song.albumId == it.id }
                    )
                },
                onPlaySongsInAlbumNext = {
                    onPlaySongsInAlbumNext(
                        uiState.songs.filter { song -> song.albumId == it.id }
                    )
                },
                onShuffleAndPlaySongsInAlbum = {
                    onShuffleAndPlaySongsInAlbum(
                        uiState.songs.filter { song -> song.albumId == it.id }
                    )
                },
                onViewAlbumArtist = onViewAlbumArtist,
                onGetPlaylists = { uiState.playlists },
                onAddSongsToPlaylist = onAddSongsToPlaylist,
                onCreatePlaylist = onCreatePlaylist,
                onGetSongsInAlbum = { album -> uiState.songs.filter { it.albumId == album.id } },
                onShowSnackBar = onShowSnackBar,
                onShowAddToQueueOption = { album ->
                    onShowAddToQueueOption( uiState.songs.filter { it.albumId == album.id } )
                },
                onRemoveSongsInAlbumFromQueue = { album ->
                    onRemoveSongsInAlbumFromQueue( uiState.songs.filter { it.albumId == album.id } )
                },
            )
        }
    }

}

@PreviewScreenSizes
@Composable
private fun AlbumsScreenContentPreview(
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
        AlbumsScreenContent(
            uiState = AlbumsScreenUiState.Success(
                albums = previewData.albums,
                sortAlbumsBy = SortAlbumsBy.ALBUM_NAME,
                sortAlbumsInReverse = false,
                songs = emptyList(),
                playlists = emptyList(),
            ),
            onViewAlbum = {},
            onShowSnackBar = {},
            onViewAlbumArtist = {},
            onPlaySongsInAlbum = { _, _ -> },
            onCreatePlaylist = { _, _ -> },
            onPlaySongsInAlbumNext = {},
            onRemoveSongsInAlbumFromQueue = {},
            onShowAddToQueueOption = { false },
            onAddSongsInAlbumToQueue = {},
            onSortInReverseChange = {},
            onShuffleAndPlaySongsInAlbum = {},
            onSortTypeChange = {},
            onAddSongsToPlaylist = { _, _ -> }
        )
    }
}