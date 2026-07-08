package com.squad.musicmatters.feature.album

import android.net.Uri
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.squad.musicmatters.core.datastore.DefaultPreferences
import com.squad.musicmatters.core.designsystem.theme.MusicMattersTheme
import com.squad.musicmatters.core.designsystem.theme.SupportedFonts
import com.squad.musicmatters.core.model.Playlist
import com.squad.musicmatters.core.model.Song
import com.squad.musicmatters.core.model.SortSongsBy
import com.squad.musicmatters.core.ui.DynamicAsyncImage
import com.squad.musicmatters.core.ui.LibraryDestinationContainer
import com.squad.musicmatters.core.ui.MusicMattersPreviewParametersProvider
import com.squad.musicmatters.core.ui.PreviewData
import com.squad.musicmatters.core.ui.SongList

@Composable
internal fun AlbumScreen(
    viewModel: AlbumScreenViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit,
    onViewAlbum: ( Long ) -> Unit,
    onViewArtist: ( String ) -> Unit,
    onShareSong: ( Uri ) -> Unit,
    onDeleteSong: ( Song ) -> Unit,
    onShowSnackBar: ( String ) -> Unit,
) {

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    AlbumScreenContent(
        uiState = uiState,
        onNavigateBack = onNavigateBack,
        onViewAlbum = onViewAlbum,
        onViewArtist = onViewArtist,
        onShareSong = onShareSong,
        onDeleteSong = onDeleteSong,
        onShowSnackBar = onShowSnackBar,
        onShuffleAndPlay = viewModel::shuffleAndPlay,
        onSortTypeChange = viewModel::setSortSongsBy,
        onSortSongsInReverseChange = viewModel::setSortSongsInReverse,
        playSong = viewModel::playSongs,
        onAddToFavorites = viewModel::addToFavorites,
        onSongIsPresentInQueue = viewModel::songIsPresentInQueue,
        onAddSongToQueue = viewModel::addSongToQueue,
        onRemoveSongFromQueue = viewModel::removeSongFromQueue,
        onPlaySongNext = viewModel::playSongNext,
        onAddSongsToPlaylist = viewModel::addSongsToPlaylist,
        onCreatePlaylist = viewModel::createPlaylist,
    )
}

@Composable
private fun AlbumScreenContent(
    uiState: AlbumScreenUiState,
    onNavigateBack: () -> Unit,
    onShuffleAndPlay: ( List<Song> ) -> Unit,
    onSortTypeChange: ( SortSongsBy ) -> Unit,
    onSortSongsInReverseChange: ( Boolean ) -> Unit,
    playSong: ( Song, List<Song> ) -> Unit,
    onAddToFavorites: ( Song, Boolean ) -> Unit,
    onViewAlbum: ( Long ) -> Unit,
    onViewArtist: ( String ) -> Unit,
    onSongIsPresentInQueue: ( Song ) -> Boolean,
    onAddSongToQueue: ( Song ) -> Unit,
    onRemoveSongFromQueue: ( Song ) -> Unit,
    onShareSong: ( Uri ) -> Unit,
    onPlaySongNext: ( Song ) -> Unit,
    onAddSongsToPlaylist: ( Playlist, List<Song> ) -> Unit,
    onCreatePlaylist: ( String, List<Song> ) -> Unit,
    onDeleteSong: ( Song ) -> Unit,
    onShowSnackBar: ( String ) -> Unit,
) {

    LibraryDestinationContainer(
        isLoading = uiState is AlbumScreenUiState.Loading,
        onNavigateBack = onNavigateBack,
    ) {
        when ( uiState ) {
            AlbumScreenUiState.Loading -> {}
            is AlbumScreenUiState.Success -> {
                SongList(
                    sortSongsInReverse = uiState.sortSongsInReverse,
                    sortSongsBy = uiState.sortSongsBy,
                    songs = uiState.songsInAlbum,
                    currentlyPlayingSongId = uiState.currentlyPlayingSongId,
                    onGetPlaylists = { uiState.playlists },
                    onGetSongsAdditionalMetadata = { uiState.songsAdditionalMetadata },
                    onShufflePlay = { onShuffleAndPlay( uiState.songsInAlbum ) },
                    onSortTypeChange = onSortTypeChange,
                    onSortSongsInReverseChange = onSortSongsInReverseChange,
                    playSong = playSong,
                    isFavorite = { uiState.favoriteSongIds.contains( it ) },
                    onFavorite = onAddToFavorites,
                    onViewAlbum = onViewAlbum,
                    onViewArtist = onViewArtist,
                    onSongIsPresentInQueue = onSongIsPresentInQueue,
                    onAddSongToQueue = onAddSongToQueue,
                    onRemoveSongFromQueue = onRemoveSongFromQueue,
                    onShareSong = onShareSong,
                    onPlaySongNext = onPlaySongNext,
                    onAddSongsToPlaylist = onAddSongsToPlaylist,
                    onCreatePlaylist = onCreatePlaylist,
                    onDeleteSong = onDeleteSong,
                    onShowSnackBar = onShowSnackBar,
                    leadingContent = {
                        item {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Spacer( modifier = Modifier.height( 32.dp ) )
                                ElevatedCard(
                                    elevation = CardDefaults.elevatedCardElevation(
                                        defaultElevation = 8.dp,
                                    ),
                                    shape = MaterialTheme.shapes.medium,
                                ) {
                                    DynamicAsyncImage(
                                        imageUri = uiState.album.artworkUri?.toUri(),
                                        contentDescription = null,
                                        modifier = Modifier
                                            .size( 250.dp )
                                            .clip( MaterialTheme.shapes.medium )
                                    )
                                }
                                Spacer( modifier = Modifier.height( 32.dp ) )
                                Text(
                                    text = uiState.album.title,
                                    style = MaterialTheme.typography.titleLarge
                                )
                                uiState.album.artist?.takeIf { it.isNotBlank() }?.let {
                                    Spacer( modifier = Modifier.height( 8.dp ) )
                                    Text(
                                        text = it,
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            color = MaterialTheme
                                                .colorScheme
                                                .onSurface
                                                .copy( alpha = 0.5f )
                                        ),
                                    )
                                }
                                Spacer( modifier = Modifier.height( 32.dp ) )
                            }
                        }
                    }
                )
            }
        }
    }

}

@PreviewScreenSizes
@Composable
private fun AlbumScreenContentPreview(
    @PreviewParameter(MusicMattersPreviewParametersProvider::class )
    previewData: PreviewData
) {
    MusicMattersTheme(
        fontName = SupportedFonts.ProductSans.name,
        themeMode = DefaultPreferences.THEME_MODE,
        primaryColorName = DefaultPreferences.PRIMARY_COLOR_NAME,
        fontScale = 1.0f,
        useMaterialYou = true
    ) {
        AlbumScreenContent(
            uiState = AlbumScreenUiState.Success(
                album = previewData.albums.first(),
                songsInAlbum = previewData.songs,
                sortSongsBy = DefaultPreferences.SORT_SONGS_BY,
                sortSongsInReverse = false,
                currentlyPlayingSongId = previewData.songs.first().id,
                playlists = emptyList(),
                songsAdditionalMetadata = emptyList(),
                favoriteSongIds = emptySet(),
            ),
            onNavigateBack = {},
            onShowSnackBar = {},
            onAddSongsToPlaylist = { _, _ -> },
            onSortTypeChange = {},
            onViewAlbum = {},
            onCreatePlaylist = { _, _ -> },
            onViewArtist = {},
            onShuffleAndPlay = {},
            onShareSong = {},
            onDeleteSong = {},
            onSongIsPresentInQueue = { false },
            onPlaySongNext = {},
            onAddToFavorites = { _, _ -> },
            onAddSongToQueue = {},
            onRemoveSongFromQueue = {},
            onSortSongsInReverseChange = {},
            playSong = { _, _ -> }
        )
    }
}