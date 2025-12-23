package com.squad.musicmatters.feature.songs

import android.net.Uri
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.squad.musicmatters.core.datastore.DefaultPreferences
import com.squad.musicmatters.core.designsystem.component.DevicePreviews
import com.squad.musicmatters.core.designsystem.theme.MusicMattersTheme
import com.squad.musicmatters.core.designsystem.theme.SupportedFonts
import com.squad.musicmatters.core.designsystem.theme.isLight
import com.squad.musicmatters.core.i8n.English
import com.squad.musicmatters.core.model.PlaylistInfo
import com.squad.musicmatters.core.model.Song
import com.squad.musicmatters.core.model.SongAdditionalMetadataInfo
import com.squad.musicmatters.core.model.SortSongsBy
import com.squad.musicmatters.core.model.ThemeMode
import com.squad.musicmatters.core.ui.AnimatedLoadingWheel
import com.squad.musicmatters.core.ui.LoaderScaffold
import com.squad.musicmatters.core.ui.LoadingWheel
import com.squad.musicmatters.core.ui.PreviewParameterData
import com.squad.musicmatters.core.ui.SongList
import com.squad.musicmatters.core.ui.TopAppBar

@Composable
fun SongsScreen(
    viewModel: SongsScreenViewModel = hiltViewModel(),
    onViewAlbum: (String ) -> Unit,
    onViewArtist: ( String ) -> Unit,
    onShareSong: ( Uri, String ) -> Unit,
    onDeleteSong: ( Song ) -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToSearch: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    SongsScreenContent(
        uiState = uiState,
        onSortReverseChange = viewModel::setSortSongsInReverse,
        onSortTypeChange = viewModel::setSortSongsBy,
        onSettingsClicked = onNavigateToSettings,
        onShufflePlay = {
//            viewModel.shuffleAndPlay( songs = uiState.songs )
        },
        playSong = viewModel::playSongs,
        onFavorite = viewModel::addToFavorites,
        onViewAlbum = onViewAlbum,
        onViewArtist = onViewArtist,
        onPlayNext = viewModel::playSongNext,
        onAddToQueue = viewModel::addSongToQueue,
        onGetSongsInPlaylist = {
            emptyList<Song>()
//            viewModel::getSongsInPlaylist
        },
        onAddSongsToPlaylist = { playlist, songs ->
            viewModel.addSongsToPlaylist( playlist, songs )
        },
        onSearchSongsMatchingQuery = {
            emptyList<Song>()
//            viewModel::searchSongsMatching
        },
        onCreatePlaylist = { title, songs ->
            viewModel.createPlaylist( title, songs )
        },
        onShareSong = {
//            onShareSong( it, uiState.language.shareFailedX( "" ) )
        },
        onNavigateToSearch = onNavigateToSearch,
        onGetPlaylists = {
            emptyList<PlaylistInfo>()
//            uiState.playlistInfos
        },
        onGetAdditionalMetadataForSongWithId = { songId ->
            null
//            uiState.songsAdditionalMetadataList.find { it.songId == songId }
        },
        onDeleteSong = onDeleteSong
    )
}

@Composable
fun SongsScreenContent(
    uiState: SongsScreenUiState,
    onSortReverseChange: ( Boolean ) -> Unit,
    onSortTypeChange: ( SortSongsBy ) -> Unit,
    onSettingsClicked: () -> Unit,
    onShufflePlay: () -> Unit,
    playSong: ( Song, List<Song> ) -> Unit,
    onFavorite: ( String, Boolean ) -> Unit,
    onViewAlbum: ( String ) -> Unit,
    onViewArtist: ( String ) -> Unit,
    onShareSong: ( Uri ) -> Unit,
    onPlayNext: ( Song ) -> Unit,
    onAddToQueue: ( Song ) -> Unit,
    onGetSongsInPlaylist: (PlaylistInfo) -> List<Song>,
    onAddSongsToPlaylist: (PlaylistInfo, List<Song> ) -> Unit,
    onSearchSongsMatchingQuery: ( String ) -> List<Song>,
    onCreatePlaylist: ( String, List<Song> ) -> Unit,
    onGetPlaylists: () -> List<PlaylistInfo>,
    onNavigateToSearch: () -> Unit,
    onGetAdditionalMetadataForSongWithId: ( String ) -> SongAdditionalMetadataInfo?,
    onDeleteSong: ( Song ) -> Unit,
) {

    when ( uiState ) {
        SongsScreenUiState.Loading -> {}
        is SongsScreenUiState.Success -> {
            val fallbackResourceId =
                if ( uiState.themeMode.isLight( LocalContext.current ) )
                    com.squad.musicmatters.core.ui.R.drawable.core_ui_placeholder_light else com.squad.musicmatters.core.ui.R.drawable.core_ui_placeholder_dark

            Column (
                modifier = Modifier.fillMaxSize()
            ) {
                TopAppBar(
                    onNavigationIconClicked = onNavigateToSearch,
                    title = uiState.language.songs,
                    settings = uiState.language.settings,
                    onSettingsClicked = onSettingsClicked
                )
                SongList(
                    sortReverse = uiState.sortSongsInReverse,
                    onSortReverseChange = onSortReverseChange,
                    sortSongsBy = uiState.sortSongsBy,
                    onSortTypeChange = onSortTypeChange,
                    language = uiState.language,
                    songs = uiState.songs,
                    playlistInfos = onGetPlaylists(),
                    onShufflePlay = onShufflePlay,
                    currentlyPlayingSongId = uiState.currentlyPlayingSongId,
                    playSong = playSong,
                    isFavorite = { uiState.favoriteSongIds.contains( it ) },
                    onFavorite = onFavorite,
                    onViewAlbum = onViewAlbum,
                    onViewArtist = onViewArtist,
                    onShareSong = onShareSong,
                    onPlayNext = onPlayNext,
                    onAddToQueue = onAddToQueue,
                    onGetSongsInPlaylist = onGetSongsInPlaylist,
                    onAddSongsToPlaylist = onAddSongsToPlaylist,
                    onSearchSongsMatchingQuery = onSearchSongsMatchingQuery,
                    onCreatePlaylist = onCreatePlaylist,
                    onGetAdditionalMetadataForSongWithId = onGetAdditionalMetadataForSongWithId,
                    onDeleteSong = onDeleteSong
                )
            }
        }
    }

    AnimatedLoadingWheel( isVisible = uiState is SongsScreenUiState.Loading )
}


@DevicePreviews
@Composable
private fun SongsScreenContentPreview() {
    MusicMattersTheme(
        themeMode = DefaultPreferences.THEME_MODE,
        primaryColorName = DefaultPreferences.PRIMARY_COLOR_NAME,
        fontName = SupportedFonts.ProductSans.name,
        fontScale = 1.0f,
        useMaterialYou = true
    ) {
        SongsScreenContent(
            uiState = SongsScreenUiState.Success(
                language = English,
                songs = PreviewParameterData.songs,
                themeMode = ThemeMode.LIGHT,
                currentlyPlayingSongId = PreviewParameterData.songs.first().id,
                favoriteSongIds = PreviewParameterData.songs.map { it.id }.toSet(),
                sortSongsBy = SortSongsBy.TITLE,
                sortSongsInReverse = false,
                playlists = emptyList(),
                songsAdditionalMetadata = emptyList()
            ),
            onSortReverseChange = {},
            onSortTypeChange = {},
            onSettingsClicked = {},
            onShufflePlay = {},
            playSong = { _, _ -> },
            onFavorite = { _, _ -> },
            onViewAlbum = {},
            onViewArtist = {},
            onShareSong = {},
            onPlayNext = {},
            onAddToQueue = {},
            onGetSongsInPlaylist = { emptyList() },
            onAddSongsToPlaylist = { _, _ -> },
            onSearchSongsMatchingQuery = { emptyList() },
            onCreatePlaylist = { _, _ -> },
            onNavigateToSearch = {},
            onGetPlaylists = { emptyList() },
            onGetAdditionalMetadataForSongWithId = { null },
            onDeleteSong = {}
        )
    }
}

