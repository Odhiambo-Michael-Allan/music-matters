package com.squad.musicmatters.feature.queue

import android.net.Uri
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.squad.musicmatters.core.i8n.R
import com.squad.musicmatters.core.datastore.DefaultPreferences
import com.squad.musicmatters.core.designsystem.component.DevicePreviews
import com.squad.musicmatters.core.designsystem.component.MusicMattersIcons
import com.squad.musicmatters.core.designsystem.component.ShuffleButton
import com.squad.musicmatters.core.designsystem.theme.MusicMattersTheme
import com.squad.musicmatters.core.model.Playlist
import com.squad.musicmatters.core.model.Song
import com.squad.musicmatters.core.ui.MusicMattersPreviewParametersProvider
import com.squad.musicmatters.core.ui.PreviewData
import com.squad.musicmatters.core.ui.TopAppBarMinimalTitle
import com.squad.musicmatters.feature.queue.components.QueueList

@Composable
internal fun QueueScreen(
    viewModel: QueueScreenViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit,
    onShareSong: ( Uri ) -> Unit,
    onViewAlbum: (String ) -> Unit,
    onViewArtist: ( String ) -> Unit,
    onDeleteSong: ( Song ) -> Unit,
    onShowSnackBar: ( String ) -> Unit,
) {

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    QueueScreenContent(
        uiState = uiState,
        onNavigateUp = onNavigateBack,
        onCreatePlaylist = { title, songs -> viewModel.createPlaylist( title, songs ) },
        onFavorite = viewModel::addToFavorites,
        playSong = viewModel::playSongs,
        onPlayNext = viewModel::playSongNext,
        onViewAlbum = onViewAlbum,
        onViewArtist = onViewArtist,
        onAddToQueue = viewModel::addSongToQueue,
        onAddSongsToPlaylist = { playlist, songs ->
            viewModel.addSongsToPlaylist( playlist, songs )
        },
        onShareSong = onShareSong,
        onDeleteSong = onDeleteSong,
        onMoveSong = viewModel::moveSong,
        onShuffle = viewModel::onToggleShuffleMode,
        onClearQueue = viewModel::clearQueue,
        onShowSnackBar = onShowSnackBar,
        onSongIsPresentInQueue = viewModel::songIsPresentInQueue,
        onRemoveFromQueue = viewModel::removeSongFromQueue,
    )
}

@Composable
private fun QueueScreenContent(
    uiState: QueueScreenUiState,
    onNavigateUp: () -> Unit,
    playSong: ( Song, List<Song> ) -> Unit,
    onMoveSong: ( Int, Int ) -> Unit,
    onShuffle: ( Boolean ) -> Unit,
    onClearQueue: () -> Unit,
    onViewAlbum: ( String ) -> Unit,
    onViewArtist: ( String ) -> Unit,
    onAddToQueue: ( Song ) -> Unit,
    onShareSong: ( Uri ) -> Unit,
    onPlayNext: ( Song ) -> Unit,
    onAddSongsToPlaylist: ( Playlist, List<Song> ) -> Unit,
    onCreatePlaylist: ( String, List<Song> ) -> Unit,
    onDeleteSong: ( Song ) -> Unit,
    onShowSnackBar: ( String ) -> Unit,
    onFavorite: ( Song, Boolean ) -> Unit,
    onSongIsPresentInQueue: ( Song ) -> Boolean,
    onRemoveFromQueue: ( Song ) -> Unit,
) {

    var showSaveDialog by remember { mutableStateOf( false ) }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        QueueScreenTopAppBar(
            uiState = uiState,
            onBackArrowClick = onNavigateUp,
            onShuffle = onShuffle,
            onClearQueue = onClearQueue,
        )
        when ( uiState ) {
            QueueScreenUiState.Loading -> {}
            is QueueScreenUiState.Success -> {
                QueueList(
                    songsInQueue = uiState.songsInQueue,
                    currentlyPlayingSongId = uiState.currentlyPlayingSongId,
                    songsAdditionalMetadata = uiState.songsAdditionalMetadata,
                    onGetPlaylists = { uiState.playlists },
                    playSong = playSong,
                    onMoveSong = onMoveSong,
                    onAddToQueue = onAddToQueue,
                    onFavorite = onFavorite,
                    onViewArtist = onViewArtist,
                    onViewAlbum = onViewAlbum,
                    onDeleteSong = onDeleteSong,
                    onShareSong = onShareSong,
                    onCreatePlaylist = onCreatePlaylist,
                    onPlayNext = onPlayNext,
                    onShowSnackBar = onShowSnackBar,
                    onAddSongsToPlaylist = onAddSongsToPlaylist,
                    isFavorite = { uiState.favoriteSongIds.contains( it.id ) },
                    onSongIsPresentInQueue = onSongIsPresentInQueue,
                    onRemoveFromQueue = onRemoveFromQueue,
                )
            }
        }
    }
}

@OptIn( ExperimentalMaterial3Api::class )
@Composable
private fun QueueScreenTopAppBar(
    modifier: Modifier = Modifier,
    uiState: QueueScreenUiState,
    onBackArrowClick: () -> Unit,
    onShuffle: ( Boolean ) -> Unit,
    onClearQueue: () -> Unit,
) {
    CenterAlignedTopAppBar(
        modifier = modifier,
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = Color.Transparent
        ),
        navigationIcon = {
            IconButton( onClick = onBackArrowClick ) {
                Icon(
                    imageVector = MusicMattersIcons.NavigateBack,
                    contentDescription = null
                )
            }
        },
        title = {
            TopAppBarMinimalTitle {
                Text( text = stringResource( id = R.string.core_i8n_queue ) )
            }
        },
        actions = {
            when ( uiState ) {
                is QueueScreenUiState.Success -> {
                    IconButton(
                        onClick = onClearQueue
                    ) {
                        Icon(
                            imageVector = MusicMattersIcons.Clear,
                            contentDescription = null,
                        )
                    }
                    ShuffleButton(
                        shuffleEnabled = uiState.shuffle,
                        onToggleShuffleMode = { onShuffle( !uiState.shuffle ) },
                        modifier = Modifier.size(
                            MusicMattersIcons.Shuffle.defaultHeight.minus( 5.dp )
                        )
                    )
                }
                else -> {}
            }
        }
    )
}



@DevicePreviews
@Composable
private fun QueueScreenContentPreview(
    @PreviewParameter( MusicMattersPreviewParametersProvider::class )
    previewData: PreviewData
) {
    MusicMattersTheme(
        themeMode = DefaultPreferences.THEME_MODE,
        primaryColorName = DefaultPreferences.PRIMARY_COLOR_NAME,
        fontScale = 1.0f,
        useMaterialYou = true
    ) {
        QueueScreenContent(
            uiState = QueueScreenUiState.Success(
                songsInQueue = previewData.songs,
                currentlyPlayingSongId = previewData.songs.first().id,
                shuffle = true,
                favoriteSongIds = emptySet(),
                songsAdditionalMetadata = emptyList(),
                playlists = emptyList(),
            ),
            onNavigateUp = {},
            playSong = { _, _ -> },
            onMoveSong = { _, _ -> },
            onShuffle = {},
            onClearQueue = {},
            onFavorite = { _, _ -> },
            onViewAlbum = {},
            onViewArtist = {},
            onShareSong = {},
            onPlayNext = {},
            onAddToQueue = {},
            onAddSongsToPlaylist = { _, _ -> },
            onCreatePlaylist = { _, _ -> },
            onDeleteSong = {},
            onShowSnackBar = {},
            onRemoveFromQueue = {},
            onSongIsPresentInQueue = { true }
        )
    }
}

