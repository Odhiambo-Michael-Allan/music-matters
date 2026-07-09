package com.squad.musicmatters.navigation

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
import com.squad.musicmatters.feature.genre.navigation.genreScreen
import com.squad.musicmatters.feature.genre.navigation.navigateToGenre
import com.squad.musicmatters.feature.genres.navigation.genresScreen
import com.squad.musicmatters.feature.lyrics.navigation.lyricsScreen
import com.squad.musicmatters.feature.queue.navigation.queueScreen
import com.squad.musicmatters.feature.settings.navigation.navigateToSettings
import com.squad.musicmatters.feature.settings.navigation.settingsScreen
import com.squad.musicmatters.feature.songs.navigation.SongsRoute
import com.squad.musicmatters.feature.songs.navigation.songsScreen
import com.squad.musicmatters.ui.utils.shareSong
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

private fun nonTopLevelDestinationNavOptions() = navOptions {
    launchSingleTop = true
    restoreState = true
}