package com.odesa.musicMatters.ui.search

import android.content.Context
import android.net.Uri
import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.request.ImageRequest
import com.odesa.musicMatters.R
import com.odesa.musicMatters.core.data.preferences.impl.SettingsDefaults
import com.odesa.musicMatters.core.data.utils.subListNonStrict
import com.odesa.musicMatters.core.datatesting.albums.testAlbums
import com.odesa.musicMatters.core.datatesting.artists.testArtists
import com.odesa.musicMatters.core.datatesting.genres.testGenres
import com.odesa.musicMatters.core.datatesting.playlists.testPlaylistInfos
import com.odesa.musicMatters.core.datatesting.search.testSearchHistoryItems
import com.odesa.musicMatters.core.datatesting.songs.testSongs
import com.odesa.musicMatters.core.designsystem.theme.MusicMattersTheme
import com.odesa.musicMatters.core.designsystem.theme.isLight
import com.odesa.musicMatters.core.i8n.English
import com.odesa.musicMatters.core.model.Album
import com.odesa.musicMatters.core.model.Artist
import com.odesa.musicMatters.core.model.Genre
import com.odesa.musicMatters.core.model.PlaylistInfo
import com.odesa.musicMatters.core.model.SearchFilter
import com.odesa.musicMatters.core.model.SearchHistoryItem
import com.odesa.musicMatters.core.model.Song

@Composable
fun SearchScreen(
    viewModel: SearchScreenViewModel,
    initialSearchFilter: SearchFilter?,
    onArtistClick: (Artist) -> Unit,
    onAlbumClick: (Album) -> Unit,
    onGenreClick: (Genre) -> Unit,
    onPlaylistClick: (PlaylistInfo) -> Unit,
    onNavigateBack: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    SearchScreenContent(
        uiState = uiState,
        initialSearchFilter = initialSearchFilter,
        onGetSong = viewModel::getSongWithId,
        onGetAlbum = viewModel::getAlbumWithName,
        onGetArtist = viewModel::getArtistWithName,
        onGetGenre = viewModel::getGenreWithName,
        onGetPlaylistInfo = viewModel::getPlaylistWithId,
        onGetPlaylistArtworkUri = viewModel::getPlaylistArtworkUri,
        onSearch = viewModel::search,
        onSongClick = {
            viewModel.addSongToSearchHistory( it )
            viewModel.playSong( it )
        },
        onAlbumClick = {
            viewModel.addAlbumToSearchHistory( it )
            onAlbumClick( it )
        },
        onArtistClick = {
            viewModel.addArtistToSearchHistory( it )
            onArtistClick( it )
        },
        onGenreClick = {
            viewModel.addGenreToSearchHistory( it )
            onGenreClick( it )
        },
        onPlaylistClick = {
            viewModel.addPlaylistToSearchHistory( it )
            onPlaylistClick( it )
        },
        onClearSearchHistory = viewModel::clearSearchHistory,
        onDeleteSearchHistoryItem = viewModel::deleteSearchHistoryItem,
        onNavigateBack = onNavigateBack
    )

}

@Composable
fun SearchScreenContent(
    uiState: SearchScreenUiState,
    initialSearchFilter: SearchFilter?,
    onGetSong: ( String ) -> Song?,
    onGetAlbum: ( String ) -> Album?,
    onGetArtist: ( String ) -> Artist?,
    onGetGenre: ( String ) -> Genre?,
    onGetPlaylistInfo: (String ) -> PlaylistInfo?,
    onGetPlaylistArtworkUri: (PlaylistInfo ) -> Uri?,
    onSearch: ( String, SearchFilter? ) -> Unit,
    onSongClick: ( Song ) -> Unit,
    onArtistClick: ( Artist ) -> Unit,
    onAlbumClick: ( Album ) -> Unit,
    onGenreClick: ( Genre ) -> Unit,
    onPlaylistClick: (PlaylistInfo ) -> Unit,
    onClearSearchHistory: () -> Unit,
    onDeleteSearchHistoryItem: (SearchHistoryItem) -> Unit,
    onNavigateBack: () -> Unit,
) {

    var currentSearchQuery by remember { mutableStateOf( "" ) }
    var currentSearchFilter by rememberSaveable { mutableStateOf( initialSearchFilter ) }

    val fallbackResourceId =
        if ( uiState.themeMode.isLight( LocalContext.current ) )
            R.drawable.placeholder_light else R.drawable.placeholder_dark
    
    Column (
        modifier = Modifier.fillMaxSize()
    ) {
        SearchBar(
            searchQuery = currentSearchQuery,
            language = uiState.language,
            currentSearchFilter = currentSearchFilter,
            onSearchQueryChange = {
                currentSearchQuery = it
                onSearch( currentSearchQuery, currentSearchFilter )
            },
            onFilterChange = {
                currentSearchFilter = it
                onSearch( currentSearchQuery, currentSearchFilter )
            },
            onClearSearch = { currentSearchQuery = "" },
            onNavigateBack = onNavigateBack
        )
        if ( currentSearchQuery.isNotEmpty() ) {
            SearchResultsList(
                isSearching = uiState.isSearching,
                currentSearchFilter = currentSearchFilter,
                searchResults = uiState.currentSearchResults,
                language = uiState.language,
                fallbackResourceId = fallbackResourceId,
                currentlyPlayingSongId = uiState.currentlyPlayingSongId,
                onSongClick = onSongClick,
                onArtistClick = onArtistClick,
                onAlbumClick = onAlbumClick,
                onGenreClick = onGenreClick,
                onPlaylistClick = onPlaylistClick,
                onGetPlaylistArtworkUri = onGetPlaylistArtworkUri,
            )
        }
        else {
            SearchHistoryList(
                searchHistoryItems = uiState.searchHistoryItems,
                isLoadingSearchHistory = uiState.isLoadingSearchHistory,
                language = uiState.language,
                fallbackResourceId = fallbackResourceId,
                currentlyPlayingSongId = uiState.currentlyPlayingSongId,
                onGetSong = onGetSong,
                onGetAlbum = onGetAlbum,
                onGetArtist = onGetArtist,
                onGetGenre = onGetGenre,
                onGetPlaylistInfo = onGetPlaylistInfo,
                onGetPlaylistArtworkUri = onGetPlaylistArtworkUri,
                onSongClick = onSongClick,
                onAlbumClick = onAlbumClick,
                onArtistClick = onArtistClick,
                onGenreClick = onGenreClick,
                onPlaylistClick = onPlaylistClick,
                onClearSearchHistory = onClearSearchHistory,
                onDeleteSearchHistoryItem = onDeleteSearchHistoryItem
            )
        }
    }
}

@Preview( showSystemUi = true )
@PreviewScreenSizes
@Composable
fun SearchScreenContentPreview() {
    MusicMattersTheme(
        themeMode = SettingsDefaults.themeMode,
        primaryColorName = SettingsDefaults.PRIMARY_COLOR_NAME,
        fontName = SettingsDefaults.font.name,
        fontScale = SettingsDefaults.FONT_SCALE,
        useMaterialYou = true
    ) {
        SearchScreenContent(
            uiState = SearchScreenUiState(
                isLoadingSearchHistory = false,
                language = English,
                isSearching = false,
                currentSearchResults = SearchResults(
                    matchingSongs = testSongs.subListNonStrict( 2 ),
                    matchingAlbums = testAlbums.subListNonStrict( 2 ),
                    matchingGenres = testGenres.subListNonStrict( 2 ),
                    matchingArtists = testArtists.subListNonStrict( 2 ),
                    matchingPlaylistInfos = testPlaylistInfos.subListNonStrict( 2 )
                ),
                themeMode = SettingsDefaults.themeMode,
                searchHistoryItems = testSearchHistoryItems,
                currentlyPlayingSongId = testSongs.first().id
            ),
            initialSearchFilter = null,
            onSearch = { _, _ -> },
            onGetSong = { testSongs.first() },
            onGetAlbum = { testAlbums.first() },
            onGetArtist = { testArtists.first() },
            onGetGenre = { testGenres.first() },
            onGetPlaylistInfo = { testPlaylistInfos.first() },
            onGetPlaylistArtworkUri = { null },
            onGenreClick = {},
            onArtistClick = {},
            onPlaylistClick = {},
            onAlbumClick = {},
            onSongClick = {},
            onNavigateBack = {},
            onClearSearchHistory = {},
            onDeleteSearchHistoryItem = {}
        )
    }
}

internal fun createImageRequest( context: Context, artworkUri: Uri?, @DrawableRes fallbackResourceId: Int ) =
    ImageRequest.Builder( context ).apply{
        data( artworkUri )
        placeholder( fallbackResourceId )
        fallback( fallbackResourceId )
        error( fallbackResourceId )
        crossfade( true )
        build()
    }.build()


