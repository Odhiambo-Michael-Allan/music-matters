package com.squad.musicmatters.ui.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Album
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.squad.musicmatters.R
import com.squad.musicmatters.core.i8n.English
import com.squad.musicmatters.core.i8n.Language
import com.squad.musicmatters.core.model.Album
import com.squad.musicmatters.core.model.PlaylistInfo
import com.squad.musicmatters.core.model.Song
import com.squad.musicmatters.core.model.SortAlbumsBy

@Composable
fun AlbumGrid(
    albums: List<Album>,
    language: Language,
    sortType: SortAlbumsBy,
    sortReverse: Boolean,
    @DrawableRes fallbackResourceId: Int,
    onSortReverseChange: ( Boolean ) -> Unit,
    onSortTypeChange: ( SortAlbumsBy ) -> Unit,
    onAlbumClick: ( String ) -> Unit,
    onPlayAlbum: ( Album ) -> Unit,
    onAddToQueue: ( Album ) -> Unit,
    onPlayNext: ( Album ) -> Unit,
    onShufflePlay: ( Album ) -> Unit,
    onViewArtist: ( String ) -> Unit,
    onAddSongsToPlaylist: (PlaylistInfo, List<Song> ) -> Unit,
    onCreatePlaylist: ( String, List<Song> ) -> Unit,
    onGetPlaylists: () -> List<PlaylistInfo>,
    onGetSongsInAlbum: ( Album ) -> List<Song>,
    onGetSongsInPlaylist: (PlaylistInfo ) -> List<Song>,
    onSearchSongsMatchingQuery: ( String ) -> List<Song>,
) {
    MediaSortBarScaffold(
        mediaSortBar = {
            MediaSortBar(
                sortReverse = sortReverse,
                onSortReverseChange = onSortReverseChange,
                sortType = sortType,
                sortTypes = SortAlbumsBy.entries.associateBy( { it }, { it.sortAlbumByLabel( language ) } ),
                onSortTypeChange = onSortTypeChange,
                label = {
                    Text(
                        text = language.xAlbums( albums.size.toString() )
                    )
                }
            )
        }
    ) {
        when {
            albums.isEmpty() -> IconTextBody(
                icon = { modifier ->
                    Icon(
                        modifier = modifier,
                        imageVector = Icons.Filled.Album,
                        contentDescription = null
                    )
                }
            ) {
                Text( text = language.damnThisIsSoEmpty )
            }
            else -> {
                LazyVerticalGrid(
                    columns = GridCells.Adaptive( minSize = 128.dp ),
                    contentPadding = PaddingValues( 8.dp )
                ) {
                    items( albums ) {  album ->
                        AlbumTile(
                            modifier = Modifier.fillMaxWidth(),
                            album = album,
                            language = language,
                            fallbackResourceId = fallbackResourceId,
                            onPlayAlbum = { onPlayAlbum( album ) },
                            onAddToQueue = { onAddToQueue( album ) },
                            onPlayNext = { onPlayNext( album ) },
                            onShufflePlay = { onShufflePlay( album ) },
                            onViewArtist = onViewArtist,
                            onClick = { onAlbumClick( album.title ) },
                            onAddSongsToPlaylist = onAddSongsToPlaylist,
                            onCreatePlaylist = onCreatePlaylist,
                            onGetPlaylists = onGetPlaylists,
                            onGetSongsInAlbum = onGetSongsInAlbum,
                            onGetSongsInPlaylist = onGetSongsInPlaylist,
                            onSearchSongsMatchingQuery = onSearchSongsMatchingQuery,
                        )
                    }
                }
            }
        }
    }
}

fun SortAlbumsBy.sortAlbumByLabel( language: Language ) = when ( this ) {
    SortAlbumsBy.ALBUM_NAME -> language.album
    SortAlbumsBy.ARTIST_NAME -> language.artist
    SortAlbumsBy.CUSTOM -> language.custom
    SortAlbumsBy.TRACK_COUNT -> language.trackCount
}

//@Preview( showSystemUi = true )
//@Composable
//fun AlbumGridPreview() {
//    AlbumGrid(
//        albums = MusicMattersPreview,
//        language = English,
//        sortType = SortAlbumsBy.ALBUM_NAME,
//        sortReverse = false,
//        fallbackResourceId = R.drawable.placeholder_light,
//        onSortReverseChange = {},
//        onSortTypeChange = {},
//        onAlbumClick = {},
//        onPlayAlbum = {},
//        onViewArtist = {},
//        onPlayNext = {},
//        onAddToQueue = {},
//        onShufflePlay = {},
//        onAddSongsToPlaylist = { _, _ -> },
//        onGetSongsInAlbum = { emptyList() },
//        onGetPlaylists = { emptyList() },
//        onCreatePlaylist = { _, _ ->  },
//        onGetSongsInPlaylist = { emptyList() },
//        onSearchSongsMatchingQuery = { emptyList() }
//    )
//}