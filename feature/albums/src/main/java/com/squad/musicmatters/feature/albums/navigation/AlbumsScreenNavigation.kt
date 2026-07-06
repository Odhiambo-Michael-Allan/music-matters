package com.squad.musicmatters.feature.albums.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.squad.musicmatters.core.model.Album
import com.squad.musicmatters.core.ui.FadeTransition
import com.squad.musicmatters.core.ui.SlideTransition
import com.squad.musicmatters.feature.albums.AlbumsScreen
import kotlinx.serialization.Serializable

@Serializable data object AlbumsRoute

fun NavController.navigateToAlbums( navOptions: NavOptions ) {
    navigate(
        route = AlbumsRoute,
        navOptions = navOptions,
    )
}

fun NavGraphBuilder.albumsScreen(
    onViewAlbum: ( Album ) -> Unit,
    onViewArtist: ( String ) -> Unit,
    onShowSnackBar: ( String ) -> Unit,
) {
    composable<AlbumsRoute>(
        enterTransition = { SlideTransition.slideUp.enterTransition() },
        exitTransition = { FadeTransition.exitTransition() }
    ) {
        AlbumsScreen(
            onViewAlbum = onViewAlbum,
            onViewArtist = onViewArtist,
            onShowSnackBar = onShowSnackBar
        )
    }
}