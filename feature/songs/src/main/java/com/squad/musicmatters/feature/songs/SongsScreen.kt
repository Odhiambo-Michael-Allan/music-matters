package com.squad.musicmatters.feature.songs

import android.net.Uri
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.squad.musicmatters.core.datastore.DefaultPreferences
import com.squad.musicmatters.core.designsystem.theme.MusicMattersTheme
import com.squad.musicmatters.core.designsystem.theme.SupportedFonts
import com.squad.musicmatters.core.model.Playlist
import com.squad.musicmatters.core.model.Song
import com.squad.musicmatters.core.model.SortSongsBy
import com.squad.musicmatters.core.ui.PreviewParameterData
import com.squad.musicmatters.core.ui.SongList

@Composable
internal fun SongsScreen(
    viewModel: SongsScreenViewModel = hiltViewModel(),
    onViewAlbum: ( Long ) -> Unit,
    onViewArtist: ( Long ) -> Unit,
    onShareSong: ( Uri ) -> Unit,
    onDeleteSong: ( Song ) -> Unit,
    onShowSnackBar: ( String ) -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    SongsScreenContent(
        uiState = uiState,
        onSortReverseChange = viewModel::setSortSongsInReverse,
        onSortTypeChange = viewModel::setSortSongsBy,
        onShufflePlay = viewModel::shuffleAndPlay,
        playSong = viewModel::playSongs,
        onFavorite = viewModel::addToFavorites,
        onViewAlbum = onViewAlbum,
        onViewArtist = onViewArtist,
        onPlayNext = viewModel::playSongNext,
        onSongIsPresentInQueue = viewModel::songIsPresentInQueue,
        onAddToQueue = viewModel::addSongToQueue,
        onRemoveFromQueue = viewModel::removeSongFromQueue,
        onAddSongsToPlaylist = { playlist, songs ->
            viewModel.addSongsToPlaylist( playlist, songs )
        },
        onCreatePlaylist = { title, songs ->
            viewModel.createPlaylist( title, songs )
        },
        onShareSong = onShareSong,
        onDeleteSong = onDeleteSong,
        onShowSnackBar = onShowSnackBar,
    )
}

@Composable
private fun SongsScreenContent(
    uiState: SongsScreenUiState,
    onSortReverseChange: ( Boolean ) -> Unit,
    onSortTypeChange: ( SortSongsBy ) -> Unit,
    onShufflePlay: ( List<Song> ) -> Unit,
    playSong: ( Song, List<Song> ) -> Unit,
    onFavorite: ( Song, Boolean ) -> Unit,
    onViewAlbum: ( Long ) -> Unit,
    onViewArtist: ( Long ) -> Unit,
    onShareSong: ( Uri ) -> Unit,
    onPlayNext: ( Song ) -> Unit,
    onSongIsPresentInQueue: ( Song ) -> Boolean,
    onAddToQueue: ( Song ) -> Unit,
    onRemoveFromQueue: ( Song ) -> Unit,
    onAddSongsToPlaylist: ( Playlist, List<Song> ) -> Unit,
    onCreatePlaylist: ( String, List<Song> ) -> Unit,
    onDeleteSong: ( Song ) -> Unit,
    onShowSnackBar: ( String ) -> Unit,
) {

    when ( uiState ) {
        SongsScreenUiState.Loading -> {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxSize()
            ) {
                CircularProgressIndicator()
            }
        }
        is SongsScreenUiState.Success -> {
            SongList(
                sortSongsInReverse = uiState.sortSongsInReverse,
                onSortSongsInReverseChange = onSortReverseChange,
                sortSongsBy = uiState.sortSongsBy,
                onSortTypeChange = onSortTypeChange,
                songs = uiState.songs,
                onGetPlaylists = { uiState.playlists },
                onShufflePlay = { onShufflePlay( uiState.songs ) },
                currentlyPlayingSongId = uiState.currentlyPlayingSongId,
                playSong = playSong,
                onGetSongsAdditionalMetadata = { uiState.songsAdditionalMetadata },
                isFavorite = { uiState.favoriteSongIds.contains( it ) },
                onFavorite = onFavorite,
                onViewAlbum = onViewAlbum,
                onViewArtist = onViewArtist,
                onShareSong = onShareSong,
                onPlaySongNext = onPlayNext,
                onAddSongToQueue = onAddToQueue,
                onAddSongsToPlaylist = onAddSongsToPlaylist,
                onCreatePlaylist = onCreatePlaylist,
                onDeleteSong = onDeleteSong,
                onShowSnackBar = onShowSnackBar,
                onSongIsPresentInQueue = onSongIsPresentInQueue,
                onRemoveSongFromQueue = onRemoveFromQueue,
            )
        }
    }
}


@PreviewScreenSizes
@Composable
private fun SongsScreenContentPreview() {
    MusicMattersTheme(
        fontName = SupportedFonts.ProductSans.name,
        themeMode = DefaultPreferences.THEME_MODE,
        primaryColorName = DefaultPreferences.PRIMARY_COLOR_NAME,
        fontScale = 1.0f,
        useMaterialYou = true
    ) {
        SongsScreenContent(
                SongsScreenUiState.Success(
                songs = PreviewParameterData.songs,
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
            onSongIsPresentInQueue = { true },
            onRemoveFromQueue = {}
        )
    }
}

