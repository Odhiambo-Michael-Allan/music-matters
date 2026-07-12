package com.squad.musicmatters.feature.folder

import android.net.Uri
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import androidx.core.net.toUri
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.squad.musicmatters.core.datastore.DefaultPreferences
import com.squad.musicmatters.core.designsystem.component.MusicMattersIcons
import com.squad.musicmatters.core.designsystem.theme.MusicMattersTheme
import com.squad.musicmatters.core.designsystem.theme.SupportedFonts
import com.squad.musicmatters.core.model.Playlist
import com.squad.musicmatters.core.model.Song
import com.squad.musicmatters.core.model.SortSongsBy
import com.squad.musicmatters.core.ui.BottomSheetMenuItem
import com.squad.musicmatters.core.ui.GenericOptionsBottomSheet
import com.squad.musicmatters.core.ui.LibraryDestinationContainer
import com.squad.musicmatters.core.ui.MusicMattersPreviewParametersProvider
import com.squad.musicmatters.core.ui.PreviewData
import com.squad.musicmatters.core.ui.SongsList
import kotlin.io.path.Path
import kotlin.io.path.name
import com.squad.musicmatters.core.i8n.R as i8nR

@Composable
internal fun FolderScreen(
    viewModel: FolderScreenViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit,
    onViewAlbum: ( Long ) -> Unit,
    onViewArtist: ( Long ) -> Unit,
    onShareSong: ( Uri ) -> Unit,
    onDeleteSong: ( Song ) -> Unit,
    onShowSnackBar: ( String ) -> Unit,
) {

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    FolderScreenContent(
        uiState = uiState,
        onNavigateBack = onNavigateBack,
        onViewArtist = onViewArtist,
        onViewAlbum = onViewAlbum,
        onShareSong = onShareSong,
        onShowSnackBar = onShowSnackBar,
        onDeleteSong = onDeleteSong,
        onShuffleAndPlay = viewModel::shuffleAndPlay,
        onSortTypeChange = viewModel::setSortSongsBy,
        onSortSongsInReverseChange = viewModel::setSortSongsInReverse,
        onPlaySong = viewModel::playSongs,
        onAddToFavorites = viewModel::addToFavorites,
        onSongIsPresentInQueue = viewModel::songIsPresentInQueue,
        onAddSongToQueue = viewModel::addSongToQueue,
        onRemoveSongFromQueue = viewModel::removeSongFromQueue,
        onPlaySongNext = viewModel::playSongNext,
        onAddSongsToPlaylist = viewModel::addSongsToPlaylist,
        onCreatePlaylist = viewModel::createPlaylist,
        onPlaySongsByArtistNext = viewModel::playSongsNext,
        onAddSongsByArtistToQueue = viewModel::addSongsToQueue,
        onRemoveSongsByArtistFromQueue = viewModel::removeSongsFromQueue,
        onShowAddToQueueOption = viewModel::noSongInTheListIsPresentInTheQueue,
    )

}

@OptIn( ExperimentalMaterial3Api::class )
@Composable
private fun FolderScreenContent(
    uiState: FolderScreenUiState,
    onNavigateBack: () -> Unit,
    onShuffleAndPlay: ( List<Song> ) -> Unit,
    onSortTypeChange: ( SortSongsBy ) -> Unit,
    onSortSongsInReverseChange: ( Boolean ) -> Unit,
    onPlaySong: (Song, List<Song> ) -> Unit,
    onAddToFavorites: ( Song, Boolean ) -> Unit,
    onSongIsPresentInQueue: ( Song ) -> Boolean,
    onAddSongToQueue: ( Song ) -> Unit,
    onRemoveSongFromQueue: ( Song ) -> Unit,
    onViewAlbum: ( Long ) -> Unit,
    onViewArtist: ( Long ) -> Unit,
    onShareSong: ( Uri ) -> Unit,
    onPlaySongNext: ( Song ) -> Unit,
    onAddSongsToPlaylist: ( Playlist, List<Song> ) -> Unit,
    onCreatePlaylist: ( String, List<Song> ) -> Unit,
    onDeleteSong: ( Song ) -> Unit,
    onShowSnackBar: ( String ) -> Unit,
    onShowAddToQueueOption: ( List<Song> ) -> Boolean,
    onPlaySongsByArtistNext: ( List<Song> ) -> Unit,
    onAddSongsByArtistToQueue: ( List<Song> ) -> Unit,
    onRemoveSongsByArtistFromQueue: ( List<Song> ) -> Unit,
) {

    var showBottomSheetMenu by remember { mutableStateOf( false ) }

    LibraryDestinationContainer(
        title = ( uiState as? FolderScreenUiState.Success )?.let { Path( it.name ).name },
        isLoading = uiState is FolderScreenUiState.Loading,
        onNavigateBack = onNavigateBack,
        options = {
            IconButton(
                onClick = { showBottomSheetMenu = !showBottomSheetMenu }
            ) {
                Icon(
                    imageVector = MusicMattersIcons.MoreVertical,
                    contentDescription = null,
                )
            }
        }
    ) {
        when ( uiState ) {
            FolderScreenUiState.Loading -> {}
            is FolderScreenUiState.Success -> {
                val subTitle = stringResource(
                    id = if ( uiState.songsInFolder.size > 1 ) {
                        i8nR.string.core_i8n_n_songs
                    } else {
                        i8nR.string.core_i8n_one_song
                    },
                    uiState.songsInFolder.size
                )
                SongsList(
                    sortSongsInReverse = uiState.sortSongsInReverse,
                    sortSongsBy = uiState.sortSongsBy,
                    songs = uiState.songsInFolder,
                    currentlyPlayingSongId = uiState.currentlyPlayingSongId,
                    onGetPlaylists = { uiState.playlists },
                    onGetSongsAdditionalMetadata = { uiState.songsMetadata },
                    onShufflePlay = { onShuffleAndPlay( uiState.songsInFolder ) },
                    onSortTypeChange = onSortTypeChange,
                    onSortSongsInReverseChange = onSortSongsInReverseChange,
                    onPlaySong = onPlaySong,
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
                )

                if ( showBottomSheetMenu ) {
                    ModalBottomSheet(
                        onDismissRequest = { showBottomSheetMenu = false }
                    ) {
                        GenericOptionsBottomSheet(
                            headerImageUri = uiState.songsInFolder.firstOrNull {
                                it.artworkUri != null
                            }?.artworkUri?.toUri(),
                            headerTitle = uiState.name,
                            headerDescription = subTitle,
                            onGetPlaylists = { uiState.playlists },
                            onDismissRequest = { showBottomSheetMenu = false },
                            onPlayNext = { onPlaySongsByArtistNext( uiState.songsInFolder ) },
                            onAddToQueue = { onAddSongsByArtistToQueue( uiState.songsInFolder ) },
                            onRemoveFromQueue = {
                                onRemoveSongsByArtistFromQueue( uiState.songsInFolder )
                            },
                            onCreatePlaylist = onCreatePlaylist,
                            onAddSongsToPlaylist = onAddSongsToPlaylist,
                            onGetSongs = { uiState.songsInFolder},
                            onShowAddToQueueOption = {
                                onShowAddToQueueOption( uiState.songsInFolder )
                            },
                            onShowSnackBar = onShowSnackBar,
                            leadingBottomSheetMenuItem = { onDismissRequest ->
                                BottomSheetMenuItem(
                                    leadingIcon = MusicMattersIcons.Shuffle,
                                    label = stringResource( id = i8nR.string.core_i8n_shuffle_play )
                                ) {
                                    onDismissRequest()
                                    onShuffleAndPlay( uiState.songsInFolder )
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

@PreviewScreenSizes
@Composable
private fun FolderScreenContentPreview(
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
        FolderScreenContent(
            uiState = FolderScreenUiState.Success(
                name = "Views",
                songsInFolder = previewData.songs,
                sortSongsBy = SortSongsBy.TITLE,
                sortSongsInReverse = false,
                currentlyPlayingSongId = previewData.songs[1].id,
                favoriteSongIds = setOf( previewData.songs[2].id ),
                playlists = emptyList(),
                songsMetadata = emptyList()
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
            onPlaySong = { _, _ -> },
            onShowAddToQueueOption = { true },
            onPlaySongsByArtistNext = {},
            onAddSongsByArtistToQueue = {},
            onRemoveSongsByArtistFromQueue = {},
        )
    }
}