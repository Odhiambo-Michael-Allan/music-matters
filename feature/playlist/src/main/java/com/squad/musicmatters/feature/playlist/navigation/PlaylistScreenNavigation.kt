package com.squad.musicmatters.feature.playlist.navigation

import android.net.Uri
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.squad.musicmatters.core.model.Song
import com.squad.musicmatters.core.ui.SlideTransition
import com.squad.musicmatters.feature.playlist.PlaylistScreen
import kotlinx.serialization.Serializable

fun NavGraphBuilder.playlistScreen(
    onNavigateBack: () -> Unit,
    onShowSnackBar: ( String ) -> Unit,
    onDeleteSong: ( Song ) -> Unit,
    onViewAlbum: ( Long ) -> Unit,
    onViewArtist: ( Long ) -> Unit,
    onShareSong: ( Uri ) -> Unit,
) {
    composable<PlaylistRoute>(
        enterTransition = { SlideTransition.slideUp.enterTransition() },
        exitTransition = { SlideTransition.slideDown.exitTransition() },
    ) {
        PlaylistScreen(
            onNavigateBack = onNavigateBack,
            onShowSnackBar = onShowSnackBar,
            onDeleteSong = onDeleteSong,
            onViewAlbum = onViewAlbum,
            onViewArtist = onViewArtist,
            onShareSong = onShareSong,
        )
    }
}

fun NavController.navigateToPlaylist(
    playlistId: String,
    navOptions: NavOptions
) {
    navigate(
        route = PlaylistRoute( playlistId = playlistId ),
        navOptions = navOptions,
    )
}

@Serializable data class PlaylistRoute(
    // The id of the playlist to be displayed at this destination
    val playlistId: String
)