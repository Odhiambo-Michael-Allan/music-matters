package com.squad.musicmatters.ui.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import com.squad.musicmatters.core.i8n.Language
import com.squad.musicmatters.core.model.Artist
import com.squad.musicmatters.core.model.Playlist
import com.squad.musicmatters.core.model.Song
import com.squad.musicmatters.core.model.SortArtistsBy

@Composable
fun ArtistsGrid(
    artists: List<Artist>,
    language: Language,
    sortBy: SortArtistsBy,
    sortReverse: Boolean,
    @DrawableRes fallbackResourceId: Int,
    onSortReverseChange: ( Boolean ) -> Unit,
    onSortTypeChange: ( SortArtistsBy ) -> Unit,
    onArtistClick: ( String ) -> Unit,
    onPlaySongsByArtist: ( Artist ) -> Unit,
    onShufflePlaySongsByArtist: (Artist ) -> Unit,
    onAddSongsByArtistToQueue: (Artist ) -> Unit,
    onPlaySongsByArtistNext: (Artist ) -> Unit,
    onAddSongsByArtistToPlaylist: (Playlist, List<Song> ) -> Unit,
    onCreatePlaylist: ( String, List<Song> ) -> Unit,
    onGetPlaylists: () -> List<Playlist>,
    onGetSongsInPlaylist: (Playlist ) -> List<Song>,
    onSearchSongsMatchingQuery: ( String ) -> List<Song>,
    onGetSongsByArtist: ( Artist ) -> List<Song>,
) {
    MediaSortBarScaffold(
        mediaSortBar = {
            MediaSortBar(
                sortReverse = sortReverse,
                onSortReverseChange = onSortReverseChange,
                sortType = sortBy,
                sortTypes = SortArtistsBy.entries.associateBy({ it }, { it.sortArtistsByLabel(language) }),
                onSortTypeChange = onSortTypeChange,
                label = {
                    Text( text = language.xArtists( artists.size.toString() ) )
                }
            )
        }
    ) {
        when {
            artists.isEmpty() -> IconTextBody(
                icon = { modifier ->
                    Icon(
                        modifier = modifier,
                        imageVector = Icons.Filled.Person,
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
                    items( artists ) { artist ->
                        ArtistTile(
                            artist = artist,
                            language = language,
                            fallbackResourceId = fallbackResourceId,
                            onPlaySongsByArtist = { onPlaySongsByArtist( artist ) },
                            onShufflePlay = { onShufflePlaySongsByArtist( artist ) },
                            onPlayNext = { onPlaySongsByArtistNext( artist ) },
                            onAddToQueue = { onAddSongsByArtistToQueue( artist ) },
                            onViewArtist = { onArtistClick( artist.name ) },
                            onAddSongsByArtistToPlaylist = { playlist, songs ->
                                onAddSongsByArtistToPlaylist( playlist, songs )
                            },
                            onCreatePlaylist = onCreatePlaylist,
                            onGetPlaylists = onGetPlaylists,
                            onGetSongsInPlaylist = onGetSongsInPlaylist,
                            onSearchSongsMatchingQuery = onSearchSongsMatchingQuery,
                            onGetSongsByArtist = onGetSongsByArtist,
                        )
                    }
                }
            }
        }
    }
}

fun SortArtistsBy.sortArtistsByLabel( language: Language ) = when ( this ) {
    SortArtistsBy.ARTIST_NAME -> language.name
    SortArtistsBy.CUSTOM -> language.custom
    SortArtistsBy.TRACK_COUNT -> language.trackCount
    SortArtistsBy.ALBUM_COUNT -> language.albumCount
}

//@Preview( showSystemUi = true )
//@Composable
//fun ArtistsGridPreview() {
//    ArtistsGrid(
//        artists = testArtists,
//        language = English,
//        sortBy = SortArtistsBy.ARTIST_NAME,
//        sortReverse = false,
//        fallbackResourceId = R.drawable.placeholder_light,
//        onSortReverseChange = {},
//        onSortTypeChange = {},
//        onArtistClick = {},
//        onPlaySongsByArtist = {},
//        onAddSongsByArtistToQueue = {},
//        onShufflePlaySongsByArtist = {},
//        onPlaySongsByArtistNext = {},
//        onAddSongsByArtistToPlaylist = { _, _, -> },
//        onCreatePlaylist = { _, _ -> },
//        onGetPlaylists = { emptyList() },
//        onGetSongsInPlaylist = { emptyList() },
//        onSearchSongsMatchingQuery = { emptyList() },
//        onGetSongsByArtist = { emptyList() }
//    )
//}