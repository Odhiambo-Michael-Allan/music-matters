package com.odesa.musicMatters.ui.search

import android.net.Uri
import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import androidx.compose.ui.unit.dp
import com.odesa.musicMatters.R
import com.odesa.musicMatters.core.data.preferences.impl.SettingsDefaults
import com.odesa.musicMatters.core.datatesting.albums.testAlbums
import com.odesa.musicMatters.core.datatesting.artists.testArtists
import com.odesa.musicMatters.core.datatesting.genres.testGenres
import com.odesa.musicMatters.core.datatesting.playlists.testPlaylistInfos
import com.odesa.musicMatters.core.datatesting.search.testSearchHistoryItems
import com.odesa.musicMatters.core.datatesting.songs.testSongs
import com.odesa.musicMatters.core.designsystem.theme.MusicMattersTheme
import com.odesa.musicMatters.core.i8n.English
import com.odesa.musicMatters.core.i8n.Language
import com.odesa.musicMatters.core.model.Album
import com.odesa.musicMatters.core.model.Artist
import com.odesa.musicMatters.core.model.Genre
import com.odesa.musicMatters.core.model.PlaylistInfo
import com.odesa.musicMatters.core.model.SearchFilter
import com.odesa.musicMatters.core.model.SearchHistoryItem
import com.odesa.musicMatters.core.model.Song
import com.odesa.musicMatters.ui.components.GenericCard

@Composable
fun SearchHistoryList(
    searchHistoryItems: List<SearchHistoryItem>,
    isLoadingSearchHistory: Boolean,
    language: Language,
    @DrawableRes fallbackResourceId: Int,
    currentlyPlayingSongId: String,
    onGetSong: ( String ) -> Song?,
    onGetAlbum: ( String ) -> Album?,
    onGetArtist: ( String ) -> Artist?,
    onGetGenre: ( String ) -> Genre?,
    onGetPlaylistInfo: (String ) -> PlaylistInfo?,
    onGetPlaylistArtworkUri: (PlaylistInfo) -> Uri?,
    onSongClick: (Song) -> Unit,
    onAlbumClick: (Album) -> Unit,
    onArtistClick: (Artist) -> Unit,
    onGenreClick: (Genre) -> Unit,
    onPlaylistClick: (PlaylistInfo) -> Unit,
    onClearSearchHistory: () -> Unit,
    onDeleteSearchHistoryItem: ( SearchHistoryItem ) -> Unit,
) {

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        when {
            searchHistoryItems.isEmpty() -> {
                Text(
                    modifier = Modifier.align( Alignment.Center ),
                    text = "No recent searches"
                )
            }
            isLoadingSearchHistory -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                ) {
                    Column (
                        modifier = Modifier.align( Alignment.Center ),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CircularProgressIndicator(
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            modifier = Modifier.padding( 8.dp ),
                            text = "Loading Search History"
                        )
                    }
                }
            }
            else -> {
                Column (
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                ) {
                    searchHistoryItems.forEach {
                        when ( it.category ) {
                            SearchFilter.SONG -> {
                                onGetSong( it.id )?.let { song ->
                                    SearchHistoryListItem(
                                        artworkUri = song.artworkUri,
                                        fallbackResourceId = fallbackResourceId,
                                        title = song.title,
                                        subtitle = "Song - ${song.artists.joinToString()}",
                                        isHighlighted = song.id == currentlyPlayingSongId,
                                        onClick = { onSongClick( song ) },
                                        onDeleteSearchHistoryItem = { onDeleteSearchHistoryItem( it ) }
                                    )
                                }

                            }
                            SearchFilter.ALBUM -> {
                                onGetAlbum( it.id )?.let { album ->
                                    SearchHistoryListItem(
                                        artworkUri = album.artworkUri,
                                        fallbackResourceId = fallbackResourceId,
                                        title = album.title,
                                        subtitle = "Album - ${album.artists.joinToString()}",
                                        onClick = { onAlbumClick( album ) },
                                        onDeleteSearchHistoryItem = { onDeleteSearchHistoryItem( it ) }
                                    )
                                }

                            }
                            SearchFilter.ARTIST -> {
                                onGetArtist( it.id )?.let { artist ->
                                    SearchHistoryListItem(
                                        artworkUri = artist.artworkUri,
                                        fallbackResourceId = fallbackResourceId,
                                        title = artist.name,
                                        subtitle = language.artist,
                                        onClick = { onArtistClick( artist ) },
                                        onDeleteSearchHistoryItem = { onDeleteSearchHistoryItem( it ) }
                                    )
                                }

                            }
                            SearchFilter.GENRE -> {
                                onGetGenre( it.id )?.let { genre ->
                                    Row (
                                        modifier = Modifier.fillMaxWidth(),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        GenericCard(
                                            modifier = Modifier.weight( 1f ),
                                            imageRequest = null,
                                            title = { Text( text = genre.name ) },
                                            subtitle = { Text( text = genre.numberOfTracks.toString() ) },
                                            onClick = { onGenreClick( genre ) }
                                        )
                                        IconButton(
                                            onClick = { onDeleteSearchHistoryItem( it ) }
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Close,
                                                contentDescription = null
                                            )
                                        }
                                    }
                                }

                            }
                            SearchFilter.PLAYLIST -> {
                                onGetPlaylistInfo( it.id )?.let { playlist ->
                                    SearchHistoryListItem(
                                        artworkUri = onGetPlaylistArtworkUri( playlist ),
                                        fallbackResourceId = fallbackResourceId,
                                        title = playlist.title,
                                        subtitle = language.playlist,
                                        onClick = { onPlaylistClick( playlist ) },
                                        onDeleteSearchHistoryItem = { onDeleteSearchHistoryItem( it ) }
                                    )
                                }
                            }
                        }
                    }
                    Row (
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        OutlinedButton(
                            onClick = onClearSearchHistory
                        ) {
                            Text(
                                text = "Clear Search History"
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SearchHistoryListItem(
    artworkUri: Uri?,
    @DrawableRes fallbackResourceId: Int,
    title: String,
    subtitle: String,
    isHighlighted: Boolean = false,
    onClick: () -> Unit,
    onDeleteSearchHistoryItem: () -> Unit,
) {
    val context = LocalContext.current

    Row (
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        GenericCard(
            modifier = Modifier.weight( 1f ),
            imageRequest = createImageRequest(
                context = context,
                artworkUri = artworkUri,
                fallbackResourceId = fallbackResourceId
            ),
            title = {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = when {
                            isHighlighted -> MaterialTheme.colorScheme.primary
                            else -> LocalTextStyle.current.color
                        }
                    )
                )
            },
            subtitle = {
                Text(
                    text = subtitle
                )
            },
            onClick = onClick
        )
        IconButton(
            onClick = onDeleteSearchHistoryItem
        ) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = null
            )
        }
    }
}

@PreviewScreenSizes
@Composable
fun SearchHistoryListPreview() {
    MusicMattersTheme(
        themeMode = SettingsDefaults.themeMode,
        primaryColorName = SettingsDefaults.PRIMARY_COLOR_NAME,
        fontName = SettingsDefaults.font.name,
        fontScale = SettingsDefaults.FONT_SCALE,
        useMaterialYou = true
    ) {
        SearchHistoryList(
            searchHistoryItems = testSearchHistoryItems,
            isLoadingSearchHistory = false,
            language = English,
            fallbackResourceId = R.drawable.placeholder_light,
            currentlyPlayingSongId = testSongs.first().id,
            onGetSong = { testSongs.first() },
            onGetAlbum = { testAlbums.first() },
            onGetArtist = { testArtists.first() },
            onGetGenre = { testGenres.first() },
            onGetPlaylistInfo = { testPlaylistInfos.first() },
            onGetPlaylistArtworkUri = { null },
            onArtistClick = {},
            onAlbumClick = {},
            onGenreClick = {},
            onSongClick = {},
            onPlaylistClick = {},
            onClearSearchHistory = {},
            onDeleteSearchHistoryItem = {}
        )
    }
}