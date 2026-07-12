package com.squad.musicmatters.navigation

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.navOptions
import com.squad.musicmatters.core.i8n.R
import com.squad.musicmatters.core.model.Song
import com.squad.musicmatters.feature.album.navigation.albumScreen
import com.squad.musicmatters.feature.album.navigation.navigateToAlbum
import com.squad.musicmatters.feature.albums.navigation.albumsScreen
import com.squad.musicmatters.feature.artist.navigation.artistScreen
import com.squad.musicmatters.feature.artist.navigation.navigateToArtist
import com.squad.musicmatters.feature.artists.navigation.artistsScreen
import com.squad.musicmatters.feature.folder.navigation.folderScreen
import com.squad.musicmatters.feature.folder.navigation.navigateToFolder
import com.squad.musicmatters.feature.folders.navigation.foldersScreen
import com.squad.musicmatters.feature.genre.navigation.genreScreen
import com.squad.musicmatters.feature.genre.navigation.navigateToGenre
import com.squad.musicmatters.feature.genres.navigation.genresScreen
import com.squad.musicmatters.feature.lyrics.navigation.lyricsScreen
import com.squad.musicmatters.feature.playlist.navigation.navigateToPlaylist
import com.squad.musicmatters.feature.playlist.navigation.playlistScreen
import com.squad.musicmatters.feature.playlists.navigation.playlistsScreen
import com.squad.musicmatters.feature.queue.navigation.queueScreen
import com.squad.musicmatters.feature.settings.navigation.settingsScreen
import com.squad.musicmatters.feature.songs.navigation.SongsRoute
import com.squad.musicmatters.feature.songs.navigation.songsScreen
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import com.squad.musicmatters.core.i8n.R as i8nR

@Composable
internal fun MusicMattersNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    snackBarHostState: SnackbarHostState,
    onDeleteSong: ( Song ) -> Unit,
    onNavigateToSettings: () -> Unit,
) {

    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    NavHost(
        modifier = modifier.fillMaxSize(),
        navController = navController,
        startDestination = SongsRoute,
    ) {

        songsScreen(
            onShareSong = {
                shareSong(
                    context = context,
                    uri = it,
                    localizedErrorMessage = context
                        .getString( i8nR.string.core_i8n_sharing_song_failed )
                )
            },
            onDeleteSong = onDeleteSong,
            onViewArtist = {},
            onViewAlbum = {},
            onShowSnackBar = {
                snackBarHostState.showSnackBar(
                    coroutineScope,
                    it
                )
            }
        )
        queueScreen(
            onNavigateBack = { navController.navigateUp() },
            onShareSong = {
                shareSong(
                    context = context,
                    uri = it,
                    localizedErrorMessage = context
                        .getString( i8nR.string.core_i8n_sharing_song_failed )
                )
            },
            onDeleteSong = onDeleteSong,
            onViewArtist = {},
            onViewAlbum = {},
            onShowSnackBar = {
                snackBarHostState.showSnackBar(
                    coroutineScope,
                    it
                )
            }
        )
        lyricsScreen(
            onNavigateBack = { navController.navigateUp() }
        )
        settingsScreen(
            onNavigateBack = { navController.navigateUp() }
        )
        albumsScreen(
            onViewAlbum = {
                navController.navigateToAlbum(
                    albumId = it.id,
                    navOptions = nonTopLevelDestinationNavOptions()
                )
            },
            onViewArtist = {},
            onNavigateBack = { navController.navigateUp() },
            onNavigateToSettings = onNavigateToSettings,
            onShowSnackBar = {
                snackBarHostState.showSnackBar(
                    coroutineScope,
                    it
                )
            }
        )
        albumScreen(
            onViewAlbum = {},
            onViewArtist = {},
            onNavigateBack = { navController.navigateUp() },
            onDeleteSong = onDeleteSong,
            onShareSong = {},
            onShowSnackBar = {
                snackBarHostState.showSnackBar(
                    coroutineScope,
                    it
                )
            }
        )
        artistsScreen(
            onNavigateBack = { navController.navigateUp() },
            onNavigateToSettings = onNavigateToSettings,
            onViewArtist = {
                navController.navigateToArtist(
                    artistId = it,
                    navOptions = nonTopLevelDestinationNavOptions()
                )
            },
            onShowSnackBar = {
                snackBarHostState.showSnackBar(
                    coroutineScope,
                    it
                )
            }
        )
        artistScreen(
            onViewAlbum = {},
            onViewArtist = {},
            onNavigateBack = { navController.navigateUp() },
            onDeleteSong = onDeleteSong,
            onShareSong = {},
            onShowSnackBar = {
                snackBarHostState.showSnackBar(
                    coroutineScope,
                    it
                )
            }
        )
        genresScreen(
            onViewGenre = {
                navController.navigateToGenre(
                    genreName = it,
                    navOptions = nonTopLevelDestinationNavOptions()
                )
            },
            onNavigateBack = { navController.navigateUp() },
            onNavigateToSettings = onNavigateToSettings,
        )
        genreScreen(
            onViewAlbum = {},
            onViewArtist = {},
            onNavigateBack = { navController.navigateUp() },
            onDeleteSong = onDeleteSong,
            onShareSong = {},
            onShowSnackBar = {
                snackBarHostState.showSnackBar(
                    coroutineScope,
                    it
                )
            }
        )
        playlistsScreen(
            onViewPlaylist = {
                navController.navigateToPlaylist(
                    playlistId = it.id,
                    navOptions = nonTopLevelDestinationNavOptions()
                )
            },
            onNavigateToSettings = onNavigateToSettings,
            onNavigateBack = { navController.navigateUp() },
            onShowSnackBar = {
                snackBarHostState.showSnackBar(
                    coroutineScope,
                    it
                )
            }
        )
        playlistScreen(
            onViewAlbum = {},
            onViewArtist = {},
            onNavigateBack = { navController.navigateUp() },
            onDeleteSong = onDeleteSong,
            onShareSong = {},
            onShowSnackBar = {
                snackBarHostState.showSnackBar(
                    coroutineScope,
                    it
                )
            }
        )
        foldersScreen(
            onViewFolder = {
                navController.navigateToFolder(
                    path = it,
                    navOptions = nonTopLevelDestinationNavOptions()
                )
            },
            onNavigateBack = { navController.navigateUp() },
            onNavigateToSettings = onNavigateToSettings,
        )
        folderScreen(
            onViewAlbum = {},
            onViewArtist = {},
            onNavigateBack = { navController.navigateUp() },
            onDeleteSong = onDeleteSong,
            onShareSong = {},
            onShowSnackBar = {
                snackBarHostState.showSnackBar(
                    coroutineScope,
                    it
                )
            }
        )

    }
}

private fun SnackbarHostState.showSnackBar(
    coroutineScope: CoroutineScope,
    message: String,
    duration: SnackbarDuration = SnackbarDuration.Short,
) {
    // Instantly dismiss the active snackbar if one exists
    currentSnackbarData?.dismiss()

    // Launch the new one immediately
    coroutineScope.launch {
        showSnackbar(
            message = message,
            duration = duration
        )
    }
}

private fun shareSong( context: Context, uri: Uri, localizedErrorMessage: String ) {
    try {
        val intent = createShareSongIntent( context, uri )
        context.startActivity( intent )
    } catch ( exception: Exception ) {
        displayToastWithMessage(
            context,
            localizedErrorMessage
        )
    }
}

internal fun createShareSongIntent(context: Context, uri: Uri) = Intent( Intent.ACTION_SEND ).apply {
    addFlags( Intent.FLAG_GRANT_READ_URI_PERMISSION )
    putExtra( Intent.EXTRA_STREAM, uri )
    type = context.contentResolver.getType( uri )
}

internal fun displayToastWithMessage(context: Context, message: String ) = Toast.makeText(
    context,
    message,
    Toast.LENGTH_SHORT
).show()

private fun nonTopLevelDestinationNavOptions() = navOptions {
    launchSingleTop = true
    restoreState = true
}