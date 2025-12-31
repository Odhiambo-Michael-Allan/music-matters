package com.squad.musicmatters.ui.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import coil.request.ImageRequest
import com.squad.musicmatters.R
import com.squad.musicmatters.core.testing.playlists.testPlaylists
import com.squad.musicmatters.core.i8n.English
import com.squad.musicmatters.core.i8n.Language
import com.squad.musicmatters.core.model.Playlist
import com.squad.musicmatters.core.model.Song

@Composable
fun PlaylistTile(
    modifier: Modifier,
    playList: Playlist,
    language: Language,
    @DrawableRes fallbackResourceId: Int,
    playlistIsDeletable: Boolean,
    onPlaySongsInPlaylist: () -> Unit,
    onPlayNext: () -> Unit,
    onShufflePlay: () -> Unit,
    onPlaylistClick: () -> Unit,
    onAddToQueue: () -> Unit,
    onGetSongsInPlaylist: (Playlist ) -> List<Song>,
    onGetPlaylists: () -> List<Playlist>,
    onAddSongsInPlaylistToPlaylist: (Playlist, List<Song> ) -> Unit,
    onCreatePlaylist: ( String, List<Song> ) -> Unit,
    onSearchSongsMatchingQuery: ( String ) -> List<Song>,
    onDeletePlaylist: (Playlist ) -> Unit,
    onRenamePlaylist: (Playlist, String ) -> Unit,
) {

    var showRenamePlaylistDialog by remember { mutableStateOf( false ) }
    GenericTile(
        modifier = modifier,
        imageRequest = ImageRequest.Builder( LocalContext.current ).apply {
            data( onGetSongsInPlaylist( playList ).firstOrNull { it.artworkUri != null }?.artworkUri )
            placeholder( fallbackResourceId )
            error( fallbackResourceId )
            crossfade( true )
        }.build(),
        title = playList.title,
        headerDescription = language.playlist,
        language = language,
        fallbackResourceId = fallbackResourceId,
        onPlay = onPlaySongsInPlaylist,
        onClick = onPlaylistClick,
        onShufflePlay = onShufflePlay,
        onAddToQueue = onAddToQueue,
        onPlayNext = onPlayNext,
        onGetSongs = { onGetSongsInPlaylist( playList ) },
        onGetPlaylists = onGetPlaylists,
        onGetSongsInPlaylist = onGetSongsInPlaylist,
        onAddSongsToPlaylist = onAddSongsInPlaylistToPlaylist,
        onCreatePlaylist = onCreatePlaylist,
        onSearchSongsMatchingQuery = onSearchSongsMatchingQuery,
        additionalBottomSheetMenuItems = { onDismissRequest ->
            if ( playlistIsDeletable ) {
                BottomSheetMenuItem(
                    leadingIcon = Icons.Default.Edit,
                    label = language.rename
                ) {
                    showRenamePlaylistDialog = true
                    onDismissRequest()
                }
            }
            if ( playlistIsDeletable ) {
                BottomSheetMenuItem(
                    leadingIcon = Icons.Default.Delete,
                    leadingIconTint = Color.Red,
                    label = language.delete,
                ) {
                    onDeletePlaylist( playList )
                    onDismissRequest()
                }
            }
        }
    )
    if ( showRenamePlaylistDialog ) {
        RenamePlaylistDialog(
            playlistTitle = playList.title,
            language = language,
            onRename = { onRenamePlaylist( playList, it )}
        ) {
            showRenamePlaylistDialog = false
        }
    }
}

@Preview( showBackground = true )
@Composable
fun PlaylistTilePreview() {
    PlaylistTile(
        modifier = Modifier.fillMaxWidth(),
        playList = testPlaylists.first(),
        language = English,
        fallbackResourceId = R.drawable.placeholder_light,
        playlistIsDeletable = true,
        onPlaySongsInPlaylist = { /*TODO*/ },
        onPlayNext = { /*TODO*/ },
        onShufflePlay = { /*TODO*/ },
        onAddSongsInPlaylistToPlaylist = { _, _ -> },
        onCreatePlaylist = { _, _ -> },
        onPlaylistClick = {},
        onGetPlaylists = { emptyList() },
        onSearchSongsMatchingQuery = { emptyList() },
        onAddToQueue = {},
        onGetSongsInPlaylist = { emptyList() },
        onDeletePlaylist = {},
        onRenamePlaylist = { _, _ -> }
    )
}
