package com.squad.musicmatters.feature.artists.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.squad.musicmatters.core.ui.FadeTransition
import com.squad.musicmatters.core.ui.SlideTransition
import com.squad.musicmatters.feature.artists.ArtistsScreen
import kotlinx.serialization.Serializable

@Serializable data object ArtistsRoute


fun NavController.navigateToArtists( navOptions: NavOptions ) {
    navigate(
        route = ArtistsRoute,
        navOptions = navOptions,
    )
}

fun NavGraphBuilder.artistsScreen(
    onNavigateBack: () -> Unit,
    onViewArtist: ( Long ) -> Unit,
    onShowSnackBar: ( String ) -> Unit,
    onNavigateToSettings: () -> Unit,
) {
    composable<ArtistsRoute>(
        enterTransition = { SlideTransition.slideUp.enterTransition() },
        exitTransition = { FadeTransition.exitTransition() }
    ) {
        ArtistsScreen(
            onNavigateBack = onNavigateBack,
            onViewArtist = onViewArtist,
            onShowSnackBar = onShowSnackBar,
            onNavigateToSettings = onNavigateToSettings,
        )
    }
}
