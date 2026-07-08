package com.squad.musicmatters.feature.album.navigation

import android.net.Uri
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.squad.musicmatters.core.model.Song
import com.squad.musicmatters.core.ui.SlideTransition
import com.squad.musicmatters.feature.album.AlbumScreen
import kotlinx.serialization.Serializable

@Serializable data class AlbumRoute(
    // The id of the album to be displayed at this destination.
    val albumId: Long
)

fun NavController.navigateToAlbum(
    albumId: Long,
    navOptions: NavOptions,
) {
    navigate(
        route = AlbumRoute( albumId = albumId ),
        navOptions = navOptions,
    )
}

fun NavGraphBuilder.albumScreen(
    onNavigateBack: () -> Unit,
    onShowSnackBar: ( String ) -> Unit,
    onDeleteSong: ( Song ) -> Unit,
    onViewAlbum: ( Long ) -> Unit,
    onViewArtist: ( Long ) -> Unit,
    onShareSong: ( Uri ) -> Unit,
) {
    composable<AlbumRoute>(
        enterTransition = { SlideTransition.slideUp.enterTransition() },
        exitTransition = { SlideTransition.slideDown.exitTransition() },
    ) {
        AlbumScreen(
            onNavigateBack = onNavigateBack,
            onShowSnackBar = onShowSnackBar,
            onDeleteSong = onDeleteSong,
            onViewAlbum = onViewAlbum,
            onViewArtist = onViewArtist,
            onShareSong = onShareSong,
        )
    }
}