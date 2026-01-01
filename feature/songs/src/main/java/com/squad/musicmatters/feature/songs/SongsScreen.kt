package com.squad.musicmatters.feature.songs

import android.net.Uri
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.squad.musicmatters.core.datastore.DefaultPreferences
import com.squad.musicmatters.core.designsystem.component.DevicePreviews
import com.squad.musicmatters.core.designsystem.theme.MusicMattersTheme
import com.squad.musicmatters.core.designsystem.theme.SupportedFonts
import com.squad.musicmatters.core.i8n.English
import com.squad.musicmatters.core.model.Playlist
import com.squad.musicmatters.core.model.Song
import com.squad.musicmatters.core.model.SortSongsBy
import com.squad.musicmatters.core.model.ThemeMode
import com.squad.musicmatters.core.ui.PreviewParameterData
import com.squad.musicmatters.core.ui.SongList
import com.squad.musicmatters.core.ui.TopAppBar

@Composable
internal fun SongsScreen(
    viewModel: SongsScreenViewModel = hiltViewModel(),
    onViewAlbum: (String ) -> Unit,
    onViewArtist: ( String ) -> Unit,
    onShareSong: ( Uri, String ) -> Unit,
    onDeleteSong: ( Song ) -> Unit,
    onShowSnackBar: ( String ) -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    SongsScreenContent(
        uiState = uiState,
        onSortReverseChange = viewModel::setSortSongsInReverse,
        onSortTypeChange = viewModel::setSortSongsBy,
        onShufflePlay = {
//            viewModel.shuffleAndPlay( songs = uiState.songs )
        },
        playSong = viewModel::playSongs,
        onFavorite = viewModel::addToFavorites,
        onViewAlbum = onViewAlbum,
        onViewArtist = onViewArtist,
        onPlayNext = viewModel::playSongNext,
        onAddToQueue = viewModel::addSongToQueue,
        onAddSongsToPlaylist = { playlist, songs ->
            viewModel.addSongsToPlaylist( playlist, songs )
        },
        onCreatePlaylist = { title, songs ->
            viewModel.createPlaylist( title, songs )
        },
        onShareSong = {
//            onShareSong( it, uiState.language.shareFailedX( "" ) )
        },
        onDeleteSong = onDeleteSong,
        onShowSnackBar = onShowSnackBar,
    )
}

@Composable
private fun SongsScreenContent(
    uiState: SongsScreenUiState,
    onSortReverseChange: ( Boolean ) -> Unit,
    onSortTypeChange: ( SortSongsBy ) -> Unit,
    onShufflePlay: () -> Unit,
    playSong: ( Song, List<Song> ) -> Unit,
    onFavorite: ( Song, Boolean ) -> Unit,
    onViewAlbum: ( String ) -> Unit,
    onViewArtist: ( String ) -> Unit,
    onShareSong: ( Uri ) -> Unit,
    onPlayNext: ( Song ) -> Unit,
    onAddToQueue: ( Song ) -> Unit,
    onAddSongsToPlaylist: ( Playlist, List<Song> ) -> Unit,
    onCreatePlaylist: ( String, List<Song> ) -> Unit,
    onDeleteSong: ( Song ) -> Unit,
    onShowSnackBar: ( String ) -> Unit,
) {

    when ( uiState ) {
        SongsScreenUiState.Loading -> {}
        is SongsScreenUiState.Success -> {
            SongList(
                sortReverse = uiState.sortSongsInReverse,
                onSortReverseChange = onSortReverseChange,
                sortSongsBy = uiState.sortSongsBy,
                onSortTypeChange = onSortTypeChange,
                language = uiState.language,
                songs = uiState.songs,
                playlists = uiState.playlists,
                onShufflePlay = onShufflePlay,
                currentlyPlayingSongId = uiState.currentlyPlayingSongId,
                playSong = playSong,
                songsAdditionalMetadata = uiState.songsAdditionalMetadata,
                isFavorite = { uiState.favoriteSongIds.contains( it ) },
                onFavorite = onFavorite,
                onViewAlbum = onViewAlbum,
                onViewArtist = onViewArtist,
                onShareSong = onShareSong,
                onPlayNext = onPlayNext,
                onAddToQueue = onAddToQueue,
                onAddSongsToPlaylist = onAddSongsToPlaylist,
                onCreatePlaylist = onCreatePlaylist,
                onDeleteSong = onDeleteSong,
                onShowSnackBar = onShowSnackBar,
            )
        }
    }
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
            onShufflePlay = {},
            playSong = { _, _ -> },
            onFavorite = { _, _ -> },
            onViewAlbum = {},
            onViewArtist = {},
            onShareSong = {},
            onPlayNext = {},
            onAddToQueue = {},
            onAddSongsToPlaylist = { _, _ -> },
            onCreatePlaylist = { _, _ -> },
            onDeleteSong = {},
            onShowSnackBar = {},
        )
    }
}

