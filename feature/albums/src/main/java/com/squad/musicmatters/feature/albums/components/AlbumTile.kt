package com.squad.musicmatters.feature.albums.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.core.net.toUri
import com.squad.musicmatters.core.designsystem.component.MusicMattersIcons
import com.squad.musicmatters.core.model.Album
import com.squad.musicmatters.core.model.Playlist
import com.squad.musicmatters.core.model.Song
import com.squad.musicmatters.core.ui.BottomSheetMenuItem
import com.squad.musicmatters.core.ui.GenericTile
import com.squad.musicmatters.core.i8n.R as i8nR

@Composable
internal fun AlbumTile(
    modifier: Modifier = Modifier,
    album: Album,
    onViewAlbum: () -> Unit,
    onPlaySongsInAlbum: () -> Unit,
    onAddSongsInAlbumToQueue: () -> Unit,
    onPlaySongsInAlbumNext: () -> Unit,
    onShuffleAndPlaySongsInAlbum: () -> Unit,
    onViewAlbumArtist: ( String ) -> Unit,
    onGetPlaylists: () -> List<Playlist>,
    onAddSongsToPlaylist: (Playlist, List<Song> ) -> Unit,
    onCreatePlaylist: ( String, List<Song> ) -> Unit,
    onGetSongsInAlbum: () -> List<Song>,
    onShowSnackBar: ( String ) -> Unit,
    onShowAddToQueueOption: () -> Boolean,
    onRemoveSongsInAlbumFromQueue: () -> Unit,
) {

    GenericTile(
        modifier = modifier,
        imageUri = album.artworkUri?.toUri(),
        title = album.title,
        description = album.artist,
        headerDescription = album.artist ?: "",
        onGetPlaylists = onGetPlaylists,
        onPlay = onPlaySongsInAlbum,
        onClick = onViewAlbum,
        onShufflePlay = onShuffleAndPlaySongsInAlbum,
        onAddToQueue = onAddSongsInAlbumToQueue,
        onPlayNext = onPlaySongsInAlbumNext,
        onGetSongs = onGetSongsInAlbum,
        onCreatePlaylist = onCreatePlaylist,
        onAddSongsToPlaylist = onAddSongsToPlaylist,
        onShowSnackBar = onShowSnackBar,
        onShowAddToQueueOption = onShowAddToQueueOption,
        onRemoveFromQueue = onRemoveSongsInAlbumFromQueue,
        additionalBottomSheetMenuItems = { onDismissRequest ->
            BottomSheetMenuItem(
                leadingIcon = MusicMattersIcons.Artist,
                label = stringResource(
                    id = i8nR.string.core_i8n_view_artist,
                    album.artist ?: ""
                )
            ) {
                onDismissRequest()
                album.artist.takeIf { !it.isNullOrBlank() }?.let { onViewAlbumArtist( it ) }
            }
        }
    )

}