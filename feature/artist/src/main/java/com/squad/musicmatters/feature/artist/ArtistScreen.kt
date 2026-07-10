package com.squad.musicmatters.feature.artist

import android.net.Uri
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.squad.musicmatters.core.datastore.DefaultPreferences
import com.squad.musicmatters.core.designsystem.component.MusicMattersIcons
import com.squad.musicmatters.core.designsystem.theme.MusicMattersTheme
import com.squad.musicmatters.core.designsystem.theme.SupportedFonts
import com.squad.musicmatters.core.i8n.R as i8nR
import com.squad.musicmatters.core.model.Playlist
import com.squad.musicmatters.core.model.Song
import com.squad.musicmatters.core.model.SortSongsBy
import com.squad.musicmatters.core.ui.BottomSheetMenuItem
import com.squad.musicmatters.core.ui.DynamicAsyncImage
import com.squad.musicmatters.core.ui.GenericOptionsBottomSheet
import com.squad.musicmatters.core.ui.LibraryDestinationContainer
import com.squad.musicmatters.core.ui.MusicMattersPreviewParametersProvider
import com.squad.musicmatters.core.ui.PreviewData
import com.squad.musicmatters.core.ui.SongsList

@Composable
internal fun ArtistScreen(
    viewModel: ArtistScreenViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit,
    onViewAlbum: ( Long ) -> Unit,
    onViewArtist: ( Long ) -> Unit,
    onShareSong: ( Uri ) -> Unit,
    onDeleteSong: ( Song ) -> Unit,
    onShowSnackBar: ( String ) -> Unit,
) {

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    ArtistScreenContent(
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ArtistScreenContent(
    uiState: ArtistScreenUiState,
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
        isLoading = uiState is ArtistScreenUiState.Loading,
        onNavigateBack = onNavigateBack,
        options = {
            IconButton(
                onClick = {
                    showBottomSheetMenu = !showBottomSheetMenu
                }
            ) {
                Icon(
                    imageVector = MusicMattersIcons.MoreVertical,
                    contentDescription = null,
                )
            }
        }
    ) {
        when ( uiState ) {
            ArtistScreenUiState.Loading -> {}
            is ArtistScreenUiState.Success -> {
                val subTitle = stringResource(
                    id = if ( uiState.artist.trackCount > 1 ) {
                        i8nR.string.core_i8n_n_songs
                    } else {
                        i8nR.string.core_i8n_one_song
                    },
                    uiState.artist.trackCount
                )
                SongsList(
                    sortSongsInReverse = uiState.sortSongsInReverse,
                    sortSongsBy = uiState.sortSongsBy,
                    songs = uiState.songsByArtist,
                    currentlyPlayingSongId = uiState.currentlyPlayingSongId,
                    onGetPlaylists = { uiState.playlists },
                    onGetSongsAdditionalMetadata = { uiState.songsMetadata },
                    onShufflePlay = { onShuffleAndPlay( uiState.songsByArtist ) },
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
                                        imageUri = uiState.artist.artworkUri?.toUri(),
                                        contentDescription = null,
                                        modifier = Modifier
                                            .size( 250.dp )
                                            .clip( MaterialTheme.shapes.medium )
                                    )
                                }
                                Spacer( modifier = Modifier.height( 32.dp ) )
                                Text(
                                    text = uiState.artist.name,
                                    style = MaterialTheme.typography.titleLarge,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.padding( 8.dp, 0.dp ),
                                )
                                Spacer( modifier = Modifier.height( 8.dp ) )
                                Text(
                                    text = subTitle,
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        color = MaterialTheme
                                            .colorScheme
                                            .onSurface
                                            .copy( alpha = 0.5f )
                                    ),
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier
                                        .padding( 8.dp, 0.dp ),
                                )
                                Spacer( modifier = Modifier.height( 32.dp ) )
                            }
                        }
                    }
                )

                if ( showBottomSheetMenu ) {
                    ModalBottomSheet(
                        onDismissRequest = { showBottomSheetMenu = false }
                    ) {
                        GenericOptionsBottomSheet(
                            headerImageUri = uiState.artist.artworkUri?.toUri(),
                            headerTitle = uiState.artist.name,
                            headerDescription = subTitle,
                            onGetPlaylists = { uiState.playlists },
                            onDismissRequest = { showBottomSheetMenu = false },
                            onPlayNext = { onPlaySongsByArtistNext( uiState.songsByArtist ) },
                            onAddToQueue = { onAddSongsByArtistToQueue( uiState.songsByArtist ) },
                            onRemoveFromQueue = {
                                onRemoveSongsByArtistFromQueue( uiState.songsByArtist )
                            },
                            onCreatePlaylist = onCreatePlaylist,
                            onAddSongsToPlaylist = onAddSongsToPlaylist,
                            onGetSongs = { uiState.songsByArtist },
                            onShowAddToQueueOption = {
                                onShowAddToQueueOption( uiState.songsByArtist )
                            },
                            onShowSnackBar = onShowSnackBar,
                            leadingBottomSheetMenuItem = { onDismissRequest ->
                                BottomSheetMenuItem(
                                    leadingIcon = MusicMattersIcons.Shuffle,
                                    label = stringResource( id = i8nR.string.core_i8n_shuffle_play )
                                ) {
                                    onDismissRequest()
                                    onShuffleAndPlay( uiState.songsByArtist )
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
private fun ArtistScreenContentPreview(
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
        ArtistScreenContent(
            uiState = ArtistScreenUiState.Success(
                artist = previewData.artists.first(),
                songsByArtist = previewData.songs,
                sortSongsBy = DefaultPreferences.SORT_SONGS_BY,
                sortSongsInReverse = false,
                currentlyPlayingSongId = previewData.songs.first().id,
                playlists = emptyList(),
                songsMetadata = emptyList(),
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
            onPlaySong = { _, _ -> },
            onShowAddToQueueOption = { true },
            onPlaySongsByArtistNext = {},
            onAddSongsByArtistToQueue = {},
            onRemoveSongsByArtistFromQueue = {},
        )
    }

}