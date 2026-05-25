package com.squad.musicmatters.feature.queue

import android.net.Uri
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.squad.musicMatters.core.i8n.R
import com.squad.musicmatters.core.datastore.DefaultPreferences
import com.squad.musicmatters.core.designsystem.component.DevicePreviews
import com.squad.musicmatters.core.designsystem.component.MusicMattersIcons
import com.squad.musicmatters.core.designsystem.theme.MusicMattersTheme
import com.squad.musicmatters.core.model.LoopMode
import com.squad.musicmatters.core.model.Playlist
import com.squad.musicmatters.core.model.Song
import com.squad.musicmatters.core.ui.MusicMattersPreviewParametersProvider
import com.squad.musicmatters.core.ui.PreviewData
import com.squad.musicmatters.core.ui.TopAppBarMinimalTitle
import com.squad.musicmatters.core.ui.dialog.NewPlaylistDialog
import com.squad.musicmatters.feature.queue.components.QueueList

@Composable
internal fun QueueScreen(
    viewModel: QueueScreenViewModel = hiltViewModel(),
    onViewAlbum: ( String ) -> Unit,
    onViewArtist: ( String ) -> Unit,
    onShareSong: ( Uri, String ) -> Unit,
    onDeleteSong: ( Song ) -> Unit,
    onNavigateBack: () -> Unit,
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
        onShareSong = {
//            onShareSong( it, uiState.language.shareFailedX( "" ) )
        },
        onDeleteSong = onDeleteSong,
        onSaveQueue = viewModel::saveQueue,
        onShowSnackBar = onShowSnackBar,
        onToggleLoopMode = viewModel::toggleLoopMode,
        onToggleShuffleMode = viewModel::setShuffleMode
    )
}

@Composable
private fun QueueScreenContent(
    uiState: QueueScreenUiState,
    onNavigateUp: () -> Unit,
    onCreatePlaylist: ( String, List<Song> ) -> Unit,
    onFavorite: ( Song, Boolean ) -> Unit,
    playSong: ( Song, List<Song> ) -> Unit,
    onPlayNext: ( Song ) -> Unit,
    onViewAlbum: ( String ) -> Unit,
    onViewArtist: ( String ) -> Unit,
    onShareSong: ( Uri ) -> Unit,
    onAddToQueue: ( Song ) -> Unit,
    onAddSongsToPlaylist: ( Playlist, List<Song> ) -> Unit,
    onDeleteSong: ( Song ) -> Unit,
    onSaveQueue: ( List<Song> ) -> Unit,
    onShowSnackBar: ( String ) -> Unit,
    onToggleLoopMode: ( LoopMode ) -> Unit,
    onToggleShuffleMode: ( Boolean ) -> Unit,
) {

    var showSaveDialog by remember { mutableStateOf( false ) }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        QueueScreenTopAppBar(
            loopMode = ( uiState as? QueueScreenUiState.Success )?.loopMode,
            shuffle = ( uiState as? QueueScreenUiState.Success )?.shuffle ?: false,
            onBackArrowClick = onNavigateUp,
            onToggleLoopMode = onToggleLoopMode,
            onToggleShuffleMode = onToggleShuffleMode,
        )
        when ( uiState ) {
            QueueScreenUiState.Loading -> {}
            is QueueScreenUiState.Success -> {
                QueueList(
                    songsInQueue = uiState.songsInQueue,
                    currentlyPlayingSongId = uiState.currentlyPlayingSongId,
                    songsAdditionalMetadata = uiState.songsAdditionalMetadata,
                    favoriteSongIds = uiState.favoriteSongIds,
                    playlists = uiState.playlists,
                    onFavorite = onFavorite,
                    playSong = playSong,
                    onPlayNext = onPlayNext,
                    onAddToQueue = onAddToQueue,
                    onShareSong = onShareSong,
                    onViewAlbum = onViewAlbum,
                    onViewArtist = onViewArtist,
                    onAddSongsToPlaylist = onAddSongsToPlaylist,
                    onCreatePlaylist = onCreatePlaylist,
                    onDeleteSong = onDeleteSong,
                    onSaveQueue = onSaveQueue,
                    onShowSnackBar = onShowSnackBar,
                )

                if ( showSaveDialog ) {
                    NewPlaylistDialog(
                        songsToAdd = uiState.songsInQueue,
                        onConfirmation = { title, songs ->
                            onCreatePlaylist( title, songs )
                            showSaveDialog = false
                        },
                        onDismissRequest = { showSaveDialog = false }
                    )
                }
            }
        }
    }
}

@OptIn( ExperimentalMaterial3Api::class )
@Composable
private fun QueueScreenTopAppBar(
    modifier: Modifier = Modifier,
    loopMode: LoopMode?,
    shuffle: Boolean,
    onBackArrowClick: () -> Unit,
    onToggleLoopMode: ( LoopMode ) -> Unit,
    onToggleShuffleMode: ( Boolean ) -> Unit,
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
            AnimatedContent(
                targetState = shuffle,
                label = "ShuffleAnimation"
            ) { isShuffleEnabled ->
                IconButton(
                    onClick = { onToggleShuffleMode( !isShuffleEnabled ) }
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            painter = painterResource(id = com.squad.musicMatters.core.designsystem.R.drawable.ic_shuffle),
                            contentDescription = null,
                            tint = if ( isShuffleEnabled ) MaterialTheme.colorScheme.primary else LocalContentColor.current,
                            modifier = Modifier.size(
                                MusicMattersIcons.Shuffle.defaultWidth,
                                MusicMattersIcons.Shuffle.defaultHeight,
                            )
                        )

                        if ( isShuffleEnabled ) {
                            OnIndicator()
                        }
                    }
                }
            }
            AnimatedContent(
                targetState = loopMode,
            ) {
                IconButton(
                    onClick = { loopMode?.let { onToggleLoopMode( loopMode ) } }
                ) {
                    Column (
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            painter = painterResource(
                                id = when ( it ) {
                                    LoopMode.Song -> com.squad.musicMatters.core.designsystem.R.drawable.ic_repeat_current
                                    else -> com.squad.musicMatters.core.designsystem.R.drawable.ic_repeat
                                }
                            ),
                            contentDescription = null,
                            tint = when ( loopMode ) {
                                null, LoopMode.None -> LocalContentColor.current
                                else -> MaterialTheme.colorScheme.primary
                            },
                            modifier = Modifier.size(
                                MusicMattersIcons.Loop.defaultWidth,
                                MusicMattersIcons.Loop.defaultHeight
                            )
                        )
                        if ( it != LoopMode.None ) {
                            OnIndicator()
                        }
                    }
                }
            }
        }
    )
}

@Composable
private fun OnIndicator() {
    Spacer( modifier = Modifier.height( 1.dp ) )
    Box(
        modifier = Modifier
            .size( 4.dp ) // Exact size of the dot
            .background(
                color = MaterialTheme.colorScheme.primary,
                shape = CircleShape
            )
    )
}

@Preview( showBackground = true )
@Composable
private fun QueueScreenTopAppBarPreview() {
    QueueScreenTopAppBar(
        loopMode = LoopMode.Queue,
        shuffle = true,
        onBackArrowClick = {},
        onToggleLoopMode = {},
        onToggleShuffleMode = {},
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
//        fontName = SupportedFonts.ProductSans.name,
        fontScale = 1.0f,
        useMaterialYou = true
    ) {
        QueueScreenContent(
            uiState = QueueScreenUiState.Success(
                songsInQueue = previewData.songs,
                currentlyPlayingSongId = previewData.songs.first().id,
                favoriteSongIds = setOf( previewData.songs.first().id ),
                playlists = previewData.playlists,
                songsAdditionalMetadata = emptyList(),
                shuffle = true,
                loopMode = LoopMode.Song,
            ),
            onNavigateUp = {},
            onCreatePlaylist = { _, _ -> },
            onFavorite = { _, _ -> },
            playSong = { _, _ -> },
            onPlayNext = {},
            onAddToQueue = {},
            onViewAlbum = {},
            onViewArtist = {},
            onShareSong = {},
            onAddSongsToPlaylist = { _, _, -> },
            onDeleteSong = {},
            onSaveQueue = {},
            onShowSnackBar = {},
            onToggleLoopMode = {},
            onToggleShuffleMode = {},
        )
    }
}

