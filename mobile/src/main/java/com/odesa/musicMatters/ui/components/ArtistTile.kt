package com.odesa.musicMatters.ui.components

import androidx.annotation.DrawableRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.PlaylistAdd
import androidx.compose.material.icons.automirrored.filled.PlaylistPlay
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import coil.request.ImageRequest
import com.odesa.musicMatters.R
import com.odesa.musicMatters.core.datatesting.artists.testArtists
import com.odesa.musicMatters.core.i8n.English
import com.odesa.musicMatters.core.i8n.Language
import com.odesa.musicMatters.core.model.Artist
import com.odesa.musicMatters.core.model.PlaylistInfo
import com.odesa.musicMatters.core.model.Song


@Composable
fun ArtistTile(
    artist: Artist,
    language: Language,
    @DrawableRes fallbackResourceId: Int,
    onPlaySongsByArtist: () -> Unit,
    onShufflePlay: () -> Unit,
    onPlayNext: () -> Unit,
    onAddToQueue: () -> Unit,
    onViewArtist: () -> Unit,
    onGetSongsByArtist: ( Artist ) -> List<Song>,
    onGetPlaylists: () -> List<PlaylistInfo>,
    onGetSongsInPlaylist: (PlaylistInfo ) -> List<Song>,
    onAddSongsByArtistToPlaylist: (PlaylistInfo, List<Song> ) -> Unit,
    onSearchSongsMatchingQuery: ( String ) -> List<Song>,
    onCreatePlaylist: ( String, List<Song> ) -> Unit,
) {
    GenericTile(
        modifier = Modifier,
        imageRequest = ImageRequest.Builder( LocalContext.current ).apply {
            data( artist.artworkUri )
            placeholder( fallbackResourceId )
            fallback( fallbackResourceId )
            error( fallbackResourceId )
            crossfade( true )
        }.build(),
        title = language.artist,
        description = artist.name,
        headerDescription = artist.name,
        language = language,
        fallbackResourceId = fallbackResourceId,
        onPlay = onPlaySongsByArtist,
        onClick = onViewArtist,
        onShufflePlay = onShufflePlay,
        onAddToQueue = onAddToQueue,
        onPlayNext = onPlayNext,
        onGetSongs = { onGetSongsByArtist( artist ) },
        onGetPlaylists = onGetPlaylists,
        onGetSongsInPlaylist = onGetSongsInPlaylist,
        onAddSongsToPlaylist = onAddSongsByArtistToPlaylist,
        onCreatePlaylist = onCreatePlaylist,
        onSearchSongsMatchingQuery = onSearchSongsMatchingQuery
    )
}

@Preview( showBackground = true )
@Composable
fun ArtistTilePreview() {
    ArtistTile(
        artist = testArtists.first(),
        language = English,
        fallbackResourceId = R.drawable.placeholder_light,
        onPlaySongsByArtist = { /*TODO*/ },
        onShufflePlay = { /*TODO*/ },
        onPlayNext = { /*TODO*/ },
        onAddToQueue = { /*TODO*/ },
        onViewArtist = {},
        onAddSongsByArtistToPlaylist = { _, _ -> },
        onCreatePlaylist = { _, _ -> },
        onGetPlaylists = { emptyList() },
        onGetSongsInPlaylist = { emptyList() },
        onSearchSongsMatchingQuery = { emptyList() },
        onGetSongsByArtist = { emptyList() }
    )
}