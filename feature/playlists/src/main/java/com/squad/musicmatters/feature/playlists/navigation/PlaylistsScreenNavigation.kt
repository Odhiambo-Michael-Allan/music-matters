package com.squad.musicmatters.feature.playlists.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.squad.musicmatters.core.model.Playlist
import com.squad.musicmatters.core.ui.FadeTransition
import com.squad.musicmatters.core.ui.SlideTransition
import com.squad.musicmatters.feature.playlists.PlaylistsScreen
import kotlinx.serialization.Serializable

fun NavController.navigateToPlaylists( navOptions: NavOptions ) {
    navigate(
        route = PlaylistsRoute,
        navOptions = navOptions
    )
}

fun NavGraphBuilder.playlistsScreen(
    onNavigateBack: () -> Unit,
    onViewPlaylist: ( Playlist) -> Unit,
    onShowSnackBar: ( String ) -> Unit,
    onNavigateToSettings: () -> Unit,
) {
    composable<PlaylistsRoute>(
        enterTransition = { SlideTransition.slideUp.enterTransition() },
        exitTransition = { FadeTransition.exitTransition() }
    ) {
        PlaylistsScreen(
            onNavigateBack = onNavigateBack,
            onNavigateToSettings = onNavigateToSettings,
            onViewPlaylist = onViewPlaylist,
            onShowSnackBar = onShowSnackBar,
        )
    }
}

@Serializable data object PlaylistsRoute