package com.squad.musicmatters.ui.search
//
//import android.net.Uri
//import androidx.annotation.DrawableRes
//import androidx.compose.foundation.layout.Box
//import androidx.compose.foundation.layout.Column
//import androidx.compose.foundation.layout.Spacer
//import androidx.compose.foundation.layout.fillMaxSize
//import androidx.compose.foundation.layout.height
//import androidx.compose.foundation.layout.padding
//import androidx.compose.foundation.rememberScrollState
//import androidx.compose.foundation.verticalScroll
//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.filled.PriorityHigh
//import androidx.compose.material3.CircularProgressIndicator
//import androidx.compose.material3.Icon
//import androidx.compose.material3.LocalTextStyle
//import androidx.compose.material3.MaterialTheme
//import androidx.compose.material3.Text
//import androidx.compose.runtime.Composable
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.platform.LocalContext
//import androidx.compose.ui.text.font.FontWeight
//import androidx.compose.ui.tooling.preview.Preview
//import androidx.compose.ui.unit.dp
//import androidx.core.net.toUri
//import com.squad.musicmatters.R
//import com.squad.musicmatters.core.data.preferences.impl.SettingsDefaults
//import com.squad.musicmatters.core.data.utils.subListNonStrict
//import com.squad.musicmatters.core.testing.albums.testAlbums
//import com.squad.musicmatters.core.testing.artists.testArtists
//import com.squad.musicmatters.core.testing.genres.testGenres
//import com.squad.musicmatters.core.testing.playlists.testPlaylistInfos
//import com.squad.musicmatters.core.testing.songs.testSongs
//import com.squad.musicmatters.core.designsystem.theme.MusicMattersTheme
//import com.squad.musicmatters.core.i8n.English
//import com.squad.musicmatters.core.i8n.Language
//import com.squad.musicmatters.core.model.Album
//import com.squad.musicmatters.core.model.Artist
//import com.squad.musicmatters.core.model.Genre
//import com.squad.musicmatters.core.model.PlaylistInfo
//import com.squad.musicmatters.core.model.SearchFilter
//import com.squad.musicmatters.core.model.Song
//import com.squad.musicmatters.ui.components.GenericCard
//import com.squad.musicmatters.ui.components.IconTextBody
//
//@Composable
//fun SearchResultsList(
//    isSearching: Boolean,
//    currentSearchFilter: SearchFilter?,
//    searchResults: SearchResults,
//    language: Language,
//    @DrawableRes fallbackResourceId: Int,
//    currentlyPlayingSongId: String,
//    onSongClick: (Song) -> Unit,
//    onArtistClick: (Artist) -> Unit,
//    onAlbumClick: (Album) -> Unit,
//    onGenreClick: (Genre) -> Unit,
//    onGetPlaylistArtworkUri: (PlaylistInfo) -> Uri?,
//    onPlaylistClick: (PlaylistInfo) -> Unit,
//) {
//    val context = LocalContext.current
//
//    searchResults.run {
//        val hasSongs = isFilterSelected( SearchFilter.SONG, currentSearchFilter ) && searchResults.matchingSongs.isNotEmpty()
//        val hasArtists = isFilterSelected( SearchFilter.ARTIST, currentSearchFilter ) && searchResults.matchingArtists.isNotEmpty()
//        val hasAlbums = isFilterSelected( SearchFilter.ALBUM, currentSearchFilter ) && searchResults.matchingAlbums.isNotEmpty()
//        val hasGenres = isFilterSelected( SearchFilter.GENRE, currentSearchFilter ) && searchResults.matchingGenres.isNotEmpty()
//        val hasPlaylists = isFilterSelected( SearchFilter.PLAYLIST, currentSearchFilter ) && searchResults.matchingPlaylistInfos.isNotEmpty()
//        val hasNoResults = !hasSongs && !hasArtists && !hasAlbums && !hasPlaylists && !hasGenres
//
//        Box( modifier = Modifier.fillMaxSize() ) {
//            when {
//                isSearching -> {
//                    CircularProgressIndicator(
//                        modifier = Modifier.align( Alignment.Center ),
//                        color = MaterialTheme.colorScheme.primary
//                    )
//                }
//                hasNoResults -> {
//                    Box(
//                        modifier = Modifier.align( Alignment.Center )
//                    ) {
//                        IconTextBody(
//                            icon = { modifier ->
//                                Icon(
//                                    modifier = modifier,
//                                    imageVector = Icons.Filled.PriorityHigh,
//                                    contentDescription = null
//                                )
//                            }
//                        ) {
//                            Text(text = language.noResultsFound)
//                        }
//                    }
//                }
//                else -> {
//                    Column (
//                        modifier = Modifier.verticalScroll( rememberScrollState() )
//                    ) {
//                        if ( hasSongs ) {
//                            SideHeading( searchFilter = SearchFilter.SONG, language = language )
//                            matchingSongs.forEach {
//                                GenericCard(
//                                    imageRequest = createImageRequest(
//                                        context = context,
//                                        artworkUri = it.artworkUri?.toUri(),
//                                        fallbackResourceId = fallbackResourceId,
//                                    ),
//                                    title = {
//                                        Text(
//                                            text = it.title,
//                                            style = MaterialTheme.typography.bodyMedium.copy(
//                                                color = when {
//                                                    currentlyPlayingSongId == it.id -> MaterialTheme.colorScheme.primary
//                                                    else -> LocalTextStyle.current.color
//                                                }
//                                            )
//                                        )
//                                    },
//                                    subtitle = {
//                                        Text( text = it.artists.joinToString() )
//                                    },
//                                    onClick = { onSongClick( it ) }
//                                )
//                            }
//                        }
//                        if ( hasArtists ) {
//                            SideHeading( searchFilter = SearchFilter.ARTIST, language = language )
//                            matchingArtists.forEach {
//                                GenericCard(
//                                    imageRequest = createImageRequest(
//                                        context = context,
//                                        artworkUri = it.artworkUri?.toUri(),
//                                        fallbackResourceId = fallbackResourceId
//                                    ),
//                                    title = {
//                                        Text( text = it.name )
//                                    },
//                                    onClick = { onArtistClick( it ) }
//                                )
//                            }
//                        }
//                        if ( hasAlbums ) {
//                            SideHeading( searchFilter = SearchFilter.ALBUM, language = language )
//                            matchingAlbums.forEach {
//                                GenericCard(
//                                    imageRequest = createImageRequest(
//                                        context = context,
//                                        artworkUri = it.artworkUri?.toUri(),
//                                        fallbackResourceId = fallbackResourceId
//                                    ),
//                                    title = {
//                                        Text( text = it.title )
//                                    },
//                                    subtitle = {
//                                        Text( text = it.artists.joinToString() )
//                                    },
//                                    onClick = { onAlbumClick( it ) }
//                                )
//                            }
//                        }
//                        if ( hasGenres ) {
//                            SideHeading( searchFilter = SearchFilter.GENRE, language = language )
//                            matchingGenres.forEach {
//                                GenericCard(
//                                    imageRequest = null,
//                                    title = { Text( text = it.name ) },
//                                    subtitle = { Text( text = it.numberOfTracks.toString() ) }
//                                ) {
//                                    onGenreClick( it )
//                                }
//                            }
//                        }
//                        if ( hasPlaylists ) {
//                            SideHeading( searchFilter = SearchFilter.PLAYLIST, language = language )
//                            matchingPlaylistInfos.forEach {
//                                GenericCard(
//                                    imageRequest = createImageRequest(
//                                        context = context,
//                                        artworkUri = onGetPlaylistArtworkUri( it ),
//                                        fallbackResourceId = fallbackResourceId,
//                                    ),
//                                    title = {
//                                        Text( text = it.title )
//                                    }
//                                ) {
//                                    onPlaylistClick( it )
//                                }
//                            }
//                        }
//                        Spacer( modifier = Modifier.height( 12.dp ) )
//                    }
//                }
//            }
//        }
//    }
//}
//
//@Composable
//private fun SideHeading(
//    searchFilter: SearchFilter,
//    language: Language,
//) {
//    Text(
//        modifier = Modifier.padding( 12.dp, 12.dp, 12.dp, 4.dp ),
//        text = searchFilter.label( language ),
//        style = MaterialTheme.typography.bodyMedium.copy( fontWeight = FontWeight.Bold )
//    )
//}
//
//private fun isFilterSelected( searchFilter: SearchFilter, currentFilter: SearchFilter? ) = currentFilter == null || currentFilter == searchFilter
//
//internal fun SearchFilter.label( language: Language ) = when ( this ) {
//    SearchFilter.SONG -> language.songs
//    SearchFilter.ALBUM -> language.albums
//    SearchFilter.ARTIST -> language.artists
//    SearchFilter.GENRE -> language.genres
//    SearchFilter.PLAYLIST -> language.playlists
//}
//
//@Preview( showSystemUi = true )
//@Composable
//fun SearchResultsListPreview() {
//    MusicMattersTheme(
//        themeMode = SettingsDefaults.themeMode,
//        primaryColorName = SettingsDefaults.PRIMARY_COLOR_NAME,
//        fontName = SettingsDefaults.font,
//        fontScale = SettingsDefaults.FONT_SCALE,
//        useMaterialYou = true
//    ) {
//        SearchResultsList(
//            isSearching = false,
//            currentSearchFilter = null,
//            searchResults = SearchResults(
//                matchingSongs = testSongs.subListNonStrict( 2 ),
//                matchingAlbums = testAlbums.subListNonStrict( 2 ),
//                matchingGenres = testGenres.subListNonStrict( 2 ),
//                matchingArtists = testArtists.subListNonStrict( 2 ),
//                matchingPlaylistInfos = testPlaylistInfos.subListNonStrict( 2 )
//            ),
//            language = English,
//            fallbackResourceId = R.drawable.placeholder_light,
//            currentlyPlayingSongId = testSongs.first().id,
//            onArtistClick = {},
//            onAlbumClick = {},
//            onPlaylistClick = {},
//            onGenreClick = {},
//            onSongClick = {},
//            onGetPlaylistArtworkUri = { null }
//        )
//    }
//}