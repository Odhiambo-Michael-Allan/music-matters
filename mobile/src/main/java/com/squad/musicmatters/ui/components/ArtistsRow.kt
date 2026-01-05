package com.squad.musicmatters.ui.components;
//
//import androidx.annotation.DrawableRes
//import androidx.compose.foundation.layout.Box
//import androidx.compose.foundation.layout.BoxWithConstraints
//import androidx.compose.foundation.layout.PaddingValues
//import androidx.compose.foundation.layout.width
//import androidx.compose.foundation.lazy.LazyRow
//import androidx.compose.foundation.lazy.items
//import androidx.compose.runtime.Composable;
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.unit.dp
//import androidx.compose.ui.unit.min
//import com.squad.musicmatters.core.i8n.Language
//import com.squad.musicmatters.core.model.Artist
//import com.squad.musicmatters.core.model.Playlist
//import com.squad.musicmatters.core.model.Song
//
//@Composable
//fun ArtistsRow(
//    modifier: Modifier = Modifier,
//    artists: List<Artist>,
//    language: Language,
//    @DrawableRes fallbackResourceId: Int,
//    onAddToQueue: ( Artist ) -> Unit,
//    onPlayNext: ( Artist ) -> Unit,
//    onShufflePlay: ( Artist ) -> Unit,
//    onViewArtist: ( Artist ) -> Unit,
//    onGetSongsByArtist: ( Artist ) -> List<Song>,
//    onGetPlaylists: () -> List<Playlist>,
//    onGetSongsInPlaylist: (Playlist) -> List<Song>,
//    onAddSongsToPlaylist: (Playlist, List<Song> ) -> Unit,
//    onSearchSongsMatchingQuery: ( String ) -> List<Song>,
//    onCreatePlaylist: ( String, List<Song> ) -> Unit,
//    onPlaySongsByArtist: ( Artist ) -> Unit,
//) {
//    BoxWithConstraints {
//        val maxSize = min( maxHeight, maxWidth ).div( 2f )
//        val width = min( maxSize, 200.dp )
//
//        LazyRow (
//            modifier = modifier,
//            contentPadding = PaddingValues( 8.dp, 0.dp )
//        ) {
//            items( artists ) {
//                Box(
//                    modifier = Modifier.width( width.minus( 15.dp ) )
//                ) {
//                    ArtistTile(
//                        artist = it,
//                        language = language,
//                        fallbackResourceId = fallbackResourceId,
//                        onPlaySongsByArtist = { onPlaySongsByArtist( it ) },
//                        onShufflePlay = { onShufflePlay( it ) },
//                        onPlayNext = { onPlayNext( it ) },
//                        onAddToQueue = { onAddToQueue( it ) },
//                        onViewArtist = { onViewArtist( it ) },
//                        onGetSongsByArtist = onGetSongsByArtist,
//                        onGetPlaylists = onGetPlaylists,
//                        onGetSongsInPlaylist = onGetSongsInPlaylist,
//                        onAddSongsByArtistToPlaylist = onAddSongsToPlaylist,
//                        onSearchSongsMatchingQuery = onSearchSongsMatchingQuery,
//                        onCreatePlaylist = onCreatePlaylist,
//                    )
//                }
//            }
//        }
//    }
//}
//
