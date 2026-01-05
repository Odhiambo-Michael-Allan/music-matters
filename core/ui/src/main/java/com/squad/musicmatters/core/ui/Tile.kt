package com.squad.musicmatters.core.ui

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.PlaylistPlay
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.squad.musicMatters.core.i8n.R
import com.squad.musicmatters.core.designsystem.component.MusicMattersIcons
import com.squad.musicmatters.core.model.Playlist
import com.squad.musicmatters.core.model.Song
import com.squad.musicmatters.core.ui.dialog.AddSongsToPlaylistBottomSheet
import com.squad.musicmatters.core.ui.dialog.NewPlaylistDialog

@OptIn( ExperimentalMaterial3Api::class )
@Composable
fun GenericTile(
    modifier: Modifier,
    imageUri: Uri?,
    title: String,
    description: String? = null,
    headerDescription: String,
    playlists: List<Playlist>,
    onPlay: () -> Unit,
    onClick: () -> Unit,
    onShufflePlay: () -> Unit,
    onAddToQueue: () -> Unit,
    onPlayNext: () -> Unit,
    onGetSongs: () -> List<Song>,
    onAddSongsToPlaylist: ( Playlist, List<Song> ) -> Unit,
    onCreatePlaylist: ( String, List<Song> ) -> Unit,
    onShowSnackBar: ( String ) -> Unit,
    additionalBottomSheetMenuItems: ( @Composable ( () -> Unit ) -> Unit )? = null,
) {

    Tile(
        modifier = modifier,
        imageUri = imageUri,
        options = { expanded, onDismissRequest ->
            if ( expanded ) {
                ModalBottomSheet(
                    onDismissRequest = onDismissRequest
                ) {
                    GenericOptionsBottomSheet(
                        headerImageUri = imageUri,
                        headerTitle = title,
                        headerDescription = headerDescription,
                        playlists = playlists,
                        onDismissRequest = onDismissRequest,
                        onAddToQueue = onAddToQueue,
                        onPlayNext = onPlayNext,
                        onAddSongsToPlaylist = onAddSongsToPlaylist,
                        onCreatePlaylist = onCreatePlaylist,
                        onGetSongs = onGetSongs,
                        onShowSnackBar = onShowSnackBar,
                        trailingBottomSheetMenuItems = additionalBottomSheetMenuItems,
                        leadingBottomSheetMenuItem = {
                            BottomSheetMenuItem(
                                leadingIcon = Icons.AutoMirrored.Filled.PlaylistPlay,
                                label = stringResource( id = R.string.core_i8n_shuffle_play )
                            ) {
                                onDismissRequest()
                                onShufflePlay()
                            }
                        }
                    )
                }
            }
        },
        content = {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            description?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodySmall,
                    textAlign = TextAlign.Center,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        },
        onPlay = onPlay,
        onClick = onClick
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GenericOptionsBottomSheet(
    headerImageUri: Uri? = null,
    headerTitle: String,
    titleIsHighlighted: Boolean = false,
    headerDescription: String,
    playlists: List<Playlist>,
    onDismissRequest: () -> Unit,
    onPlayNext: () -> Unit,
    onAddToQueue: () -> Unit,
    onCreatePlaylist: ( String, List<Song> ) -> Unit,
    onAddSongsToPlaylist: ( Playlist, List<Song> ) -> Unit,
    onGetSongs: () -> List<Song>,
    onShowSnackBar: ( String ) -> Unit,
    leadingBottomSheetMenuItem: ( @Composable ( () -> Unit ) -> Unit ),
    trailingBottomSheetMenuItems: ( @Composable ( () -> Unit ) -> Unit )? = null,
) {

    val context = LocalContext.current
    var showAddSongToPlaylistBottomSheet by remember { mutableStateOf( false ) }
    var showCreateNewPlaylistDialog by remember { mutableStateOf( false ) }

    BottomSheetMenuContent(
        bottomSheetHeader = {
            BottomSheetMenuHeader(
                headerImageUri = headerImageUri,
                title = headerTitle,
                titleIsHighlighted = titleIsHighlighted,
                description = headerDescription,
            )
        }
    ) {
        leadingBottomSheetMenuItem( onDismissRequest )
        BottomSheetMenuItem(
            leadingIcon = MusicMattersIcons.Queue,
            label = stringResource( id = R.string.core_i8n_play_next )
        ) {
            val feedback = context.getString( R.string.core_i8n_song_will_play_next )
            onDismissRequest()
            onPlayNext()
            onShowSnackBar( feedback )
        }
        BottomSheetMenuItem(
            leadingIcon = MusicMattersIcons.PlaylistAdd,
            label = stringResource( id = R.string.core_i8n_add_to_queue )
        ) {
            val feedback = context.getString( R.string.core_i8n_song_added_to_queue )
            onDismissRequest()
            onAddToQueue()
            onShowSnackBar( feedback )
        }
        BottomSheetMenuItem(
            leadingIcon = MusicMattersIcons.PlaylistAdd,
            label = stringResource( id = R.string.core_i8n_add_to_playlist )
        ) {
            showAddSongToPlaylistBottomSheet = true
        }
        trailingBottomSheetMenuItems?.let {
            it( onDismissRequest )
        }
        Spacer( modifier = Modifier.size( 32.dp ) )
    }
    if ( showAddSongToPlaylistBottomSheet ) {
        ModalBottomSheet(
            sheetState = rememberModalBottomSheetState( skipPartiallyExpanded = true ),
            onDismissRequest = { showAddSongToPlaylistBottomSheet = false }
        ) {
            AddSongsToPlaylistBottomSheet(
                songsToAdd = onGetSongs(),
                playlists = playlists,
                onAddSongsToPlaylist = { playlist, songs ->
                    onAddSongsToPlaylist( playlist, songs )
                },
                onCreateNewPlaylist = { showCreateNewPlaylistDialog = true },
                onDismissRequest = { message ->
                    showAddSongToPlaylistBottomSheet = false
                    onShowSnackBar( message )
                }
            )
        }
    }
    if ( showCreateNewPlaylistDialog ) {
        NewPlaylistDialog(
            songsToAdd = onGetSongs(),
            onConfirmation = { playlistName, selectedSongs ->
                showCreateNewPlaylistDialog = false
                onCreatePlaylist( playlistName, selectedSongs )
            },
            onDismissRequest = { showCreateNewPlaylistDialog = false }
        )
    }
}

@Composable
fun Tile(
    modifier: Modifier,
    imageUri: Uri?,
    options: @Composable ( Boolean, () -> Unit ) -> Unit,
    content: @Composable ColumnScope.() -> Unit,
    onPlay: () -> Unit,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .wrapContentHeight()
            .then( modifier ),
        colors = CardDefaults.cardColors( containerColor = Color.Transparent ),
        onClick = onClick
    ) {
        Box(
            modifier = Modifier.padding( 12.dp )
        ) {
            Column (
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box {
                    DynamicAsyncImage(
                        imageUri = imageUri,
                        contentDescription = imageUri?.toString(),
                        modifier = Modifier
                            .aspectRatio( 1f )
                            .fillMaxWidth()
                            .clip( RoundedCornerShape( 10.dp ) )
                    )
                    Box(
                        modifier = Modifier
                            .align( Alignment.TopEnd )
                            .padding( top = 4.dp )
                    ) {
                        var showOptionsMenu by remember { mutableStateOf( false ) }

                        IconButton(
                            onClick = { showOptionsMenu = !showOptionsMenu }
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.MoreVert,
                                contentDescription = null
                            )
                            options( showOptionsMenu ) {
                                showOptionsMenu = false
                            }
                        }
                    }
                    Box(
                        modifier = Modifier
                            .align( Alignment.BottomStart )
                            .padding( 8.dp )
                    ) {
                        IconButton(
                            modifier = Modifier
                                .background(
                                    MaterialTheme.colorScheme.surface,
                                    RoundedCornerShape( 12.dp )
                                )
                                .then(Modifier.size( 36.dp ) ),
                            onClick = onPlay
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.PlayArrow,
                                contentDescription = null
                            )
                        }
                    }
                }
                Spacer( modifier = Modifier.height( 8.dp ) )
                content()
            }
        }
    }
}
