package com.squad.musicmatters.ui.components

import androidx.annotation.DrawableRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import coil.request.ImageRequest
import com.squad.musicmatters.core.i8n.Language
import com.squad.musicmatters.core.model.Album
import com.squad.musicmatters.core.model.Playlist
import com.squad.musicmatters.core.model.Song

@Composable
fun AlbumTile(
    modifier: Modifier,
    album: Album,
    language: Language,
    @DrawableRes fallbackResourceId: Int,
    onPlayAlbum: () -> Unit,
    onAddToQueue: () -> Unit,
    onPlayNext: () -> Unit,
    onShufflePlay: () -> Unit,
    onViewArtist: ( String ) -> Unit,
    onClick: () -> Unit,
    onGetSongsInAlbum: ( Album ) -> List<Song>,
    onGetPlaylists: () -> List<Playlist>,
    onGetSongsInPlaylist: (Playlist ) -> List<Song>,
    onAddSongsToPlaylist: (Playlist, List<Song> ) -> Unit,
    onSearchSongsMatchingQuery: ( String ) -> List<Song>,
    onCreatePlaylist: ( String, List<Song> ) -> Unit,
) {

    GenericTile(
        modifier = modifier,
        imageRequest = ImageRequest.Builder( LocalContext.current ).apply {
            data( album.artworkUri )
            placeholder( fallbackResourceId )
            fallback( fallbackResourceId )
            error( fallbackResourceId )
            crossfade( true )
        }.build(),
        title = album.title,
        description = album.artists.joinToString(),
        headerDescription = album.artists.joinToString(),
        language = language,
        fallbackResourceId = fallbackResourceId,
        onPlay = onPlayAlbum,
        onClick = onClick,
        onShufflePlay = onShufflePlay,
        onAddToQueue = onAddToQueue,
        onPlayNext = onPlayNext,
        onGetSongs = { onGetSongsInAlbum( album ) },
        onGetPlaylists = onGetPlaylists,
        onGetSongsInPlaylist = onGetSongsInPlaylist,
        onAddSongsToPlaylist = onAddSongsToPlaylist,
        onCreatePlaylist = onCreatePlaylist,
        onSearchSongsMatchingQuery = onSearchSongsMatchingQuery,
        additionalBottomSheetMenuItems = { onDismissRequest ->
            album.artists.forEach { artist ->
                BottomSheetMenuItem(
                    leadingIcon = Icons.Filled.Person,
                    label = "${language.viewArtist}: $artist"
                ) {
                    onDismissRequest()
                    onViewArtist( artist )
                }
            }
        }
    )
}

//@Preview( showSystemUi = true )
//@Composable
//fun AlbumTilePreview() {
//    AlbumTile(
//        modifier = Modifier.fillMaxWidth(),
//        album = testAlbums.first(),
//        language = English,
//        fallbackResourceId = R.drawable.placeholder_light,
//        onPlayAlbum = { /*TODO*/ },
//        onAddToQueue = { /*TODO*/ },
//        onPlayNext = { /*TODO*/ },
//        onShufflePlay = { /*TODO*/ },
//        onViewArtist = {},
//        onAddSongsToPlaylist = { _, _ -> },
//        onGetSongsInAlbum = { emptyList() },
//        onClick = {},
//        onCreatePlaylist = { _, _ -> },
//        onGetPlaylists = { emptyList() },
//        onGetSongsInPlaylist = { emptyList() },
//        onSearchSongsMatchingQuery = { emptyList() }
//    )
//}