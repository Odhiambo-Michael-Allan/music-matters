package com.squad.musicmatters.feature.songs

import android.content.res.Configuration
import android.net.Uri
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.odesa.musicMatters.core.data.preferences.SortSongsBy
import com.odesa.musicMatters.core.data.preferences.impl.SettingsDefaults
import com.odesa.musicMatters.core.designsystem.component.DevicePreviews
import com.odesa.musicMatters.core.designsystem.theme.MusicMattersTheme
import com.odesa.musicMatters.core.designsystem.theme.ThemeMode
import com.odesa.musicMatters.core.designsystem.theme.isLight
import com.odesa.musicMatters.core.i8n.English
import com.odesa.musicMatters.core.model.PlaylistInfo
import com.odesa.musicMatters.core.model.Song
import com.odesa.musicMatters.core.model.SongAdditionalMetadataInfo
import com.squad.musicmatters.core.ui.LoaderScaffold
import com.squad.musicmatters.core.ui.MusicMattersPreviewParametersProvider
import com.squad.musicmatters.core.ui.PreviewData
import com.squad.musicmatters.core.ui.SongList
import com.squad.musicmatters.core.ui.TopAppBar

@Composable
fun SongsScreen(
    viewModel: SongsScreenViewModel,
    onViewAlbum: (String ) -> Unit,
    onViewArtist: ( String ) -> Unit,
    onShareSong: ( Uri, String ) -> Unit,
    onDeleteSong: ( Song ) -> Unit,
    onSettingsClicked: () -> Unit,
    onNavigateToSearch: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    SongsScreenContent(
        uiState = uiState,
        onSortReverseChange = viewModel::setSortSongsInReverse,
        onSortTypeChange = viewModel::setSortSongsBy,
        onSettingsClicked = onSettingsClicked,
        onShufflePlay = { viewModel.shuffleAndPlay( songs = uiState.songs ) },
        playSong = {
            viewModel.playSongs(
                selectedSong = it,
                songsInPlaylist = uiState.songs
            )
        },
        onFavorite = viewModel::addToFavorites,
        onViewAlbum = onViewAlbum,
        onViewArtist = onViewArtist,
        onPlayNext = viewModel::playSongNext,
        onAddToQueue = viewModel::addSongToQueue,
        onGetSongsInPlaylist = viewModel::getSongsInPlaylist,
        onAddSongsToPlaylist = { playlist, songs ->
            viewModel.addSongsToPlaylist( playlist, songs )
        },
        onSearchSongsMatchingQuery = viewModel::searchSongsMatching,
        onCreatePlaylist = { title, songs -> viewModel.createPlaylist( title, songs ) },
        onShareSong = { onShareSong( it, uiState.language.shareFailedX( "" ) ) },
        onNavigateToSearch = onNavigateToSearch,
        onGetPlaylists = { uiState.playlistInfos },
        onGetAdditionalMetadataForSongWithId = { songId ->
            uiState.songsAdditionalMetadataList.find { it.id == songId }
        },
        onDeleteSong = onDeleteSong
    )
}

@Composable
fun SongsScreenContent(
    uiState: SongsScreenUiState,
    onSortReverseChange: ( Boolean ) -> Unit,
    onSortTypeChange: (SortSongsBy) -> Unit,
    onSettingsClicked: () -> Unit,
    onShufflePlay: () -> Unit,
    playSong: (Song) -> Unit,
    onFavorite: ( String ) -> Unit,
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
        LoaderScaffold(
            isLoading = uiState.isLoading,
            loading = uiState.language.loading
        ) {
            SongList(
                sortReverse = uiState.sortSongsInReverse,
                onSortReverseChange = onSortReverseChange,
                sortSongsBy = uiState.sortSongsBy,
                onSortTypeChange = onSortTypeChange,
                language = uiState.language,
                songs = uiState.songs,
                playlistInfos = onGetPlaylists(),
                onShufflePlay = onShufflePlay,
                fallbackResourceId = fallbackResourceId,
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


@DevicePreviews
@Composable
private fun SongsScreenContentPreview(
    @PreviewParameter( MusicMattersPreviewParametersProvider::class )
    previewData: PreviewData
) {
    MusicMattersTheme(
        themeMode = SettingsDefaults.themeMode,
        primaryColorName = SettingsDefaults.PRIMARY_COLOR_NAME,
        fontName = SettingsDefaults.font.name,
        fontScale = 1.0f,
        useMaterialYou = true
    ) {
        SongsScreenContent(
            uiState = SongsScreenUiState(
                language = English,
                songs = previewData.songs,
                themeMode = ThemeMode.LIGHT,
                currentlyPlayingSongId = previewData.songs.first().id,
                favoriteSongIds = previewData.songs.map { it.id },
                isLoading = false,
                sortSongsBy = SortSongsBy.TITLE,
                sortSongsInReverse = false,
                playlistInfos = emptyList(),
                songsAdditionalMetadataList = emptyList()
            ),
            onSortReverseChange = {},
            onSortTypeChange = {},
            onSettingsClicked = {},
            onShufflePlay = {},
            playSong = {},
            onFavorite = {},
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

