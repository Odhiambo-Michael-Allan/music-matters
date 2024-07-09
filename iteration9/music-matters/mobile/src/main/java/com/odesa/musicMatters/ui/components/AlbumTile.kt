package com.odesa.musicMatters.ui.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.PlaylistAdd
import androidx.compose.material.icons.automirrored.filled.PlaylistPlay
import androidx.compose.material.icons.filled.Person
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import coil.request.ImageRequest
import com.odesa.musicMatters.R
import com.odesa.musicMatters.core.datatesting.albums.testAlbums
import com.odesa.musicMatters.core.i8n.English
import com.odesa.musicMatters.core.i8n.Language
import com.odesa.musicMatters.core.model.Album
import com.odesa.musicMatters.core.model.PlaylistInfo
import com.odesa.musicMatters.core.model.Song

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
    onGetPlaylists: () -> List<PlaylistInfo>,
    onGetSongsInPlaylist: (PlaylistInfo ) -> List<Song>,
    onAddSongsToPlaylist: (PlaylistInfo, List<Song> ) -> Unit,
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

@Composable
fun AlbumOptionsBottomSheetMenu(
    album: Album,
    language: Language,
    @DrawableRes fallbackResourceId: Int,
    onShufflePlay: () -> Unit,
    onPlayNext: () -> Unit,
    onAddToQueue: () -> Unit,
    onViewArtist: ( String ) -> Unit,
    onAddToPlaylist: () -> Unit,
    onDismissRequest: () -> Unit
) {
    BottomSheetMenuContent(
        bottomSheetHeader = {
            BottomSheetMenuHeader(
                headerImage = ImageRequest.Builder( LocalContext.current ).apply {
                    data( album.artworkUri )
                    placeholder( fallbackResourceId )
                    fallback( fallbackResourceId )
                    error( fallbackResourceId )
                    crossfade( true )
                }.build(),
                title = album.title,
                description = album.artists.joinToString()
            )
        }
    ) {
        BottomSheetMenuItem(
            leadingIcon = Icons.AutoMirrored.Filled.PlaylistPlay,
            label = language.shufflePlay
        ) {
            onDismissRequest()
            onShufflePlay()
        }
        BottomSheetMenuItem(
            leadingIcon = Icons.AutoMirrored.Filled.PlaylistPlay,
            label = language.playNext
        ) {
            onDismissRequest()
            onPlayNext()
        }
        BottomSheetMenuItem(
            leadingIcon = Icons.AutoMirrored.Filled.PlaylistPlay,
            label = language.addToQueue
        ) {
            onDismissRequest()
            onAddToQueue()
        }
        BottomSheetMenuItem(
            leadingIcon = Icons.AutoMirrored.Filled.PlaylistAdd,
            label = language.addToPlaylist
        ) {
            onDismissRequest()
            onAddToPlaylist()
        }
        album.artists.forEach {
            BottomSheetMenuItem(
                leadingIcon = Icons.Filled.Person,
                label = "${language.viewArtist}: $it"
            ) {
                onDismissRequest()
                onViewArtist( it )
            }
        }
    }
}


@Preview( showSystemUi = true )
@Composable
fun AlbumTilePreview() {
    AlbumTile(
        modifier = Modifier.fillMaxWidth(),
        album = testAlbums.first(),
        language = English,
        fallbackResourceId = R.drawable.placeholder_light,
        onPlayAlbum = { /*TODO*/ },
        onAddToQueue = { /*TODO*/ },
        onPlayNext = { /*TODO*/ },
        onShufflePlay = { /*TODO*/ },
        onViewArtist = {},
        onAddSongsToPlaylist = { _, _ -> },
        onGetSongsInAlbum = { emptyList() },
        onClick = {},
        onCreatePlaylist = { _, _ -> },
        onGetPlaylists = { emptyList() },
        onGetSongsInPlaylist = { emptyList() },
        onSearchSongsMatchingQuery = { emptyList() }
    )
}