package com.squad.musicmatters.feature.foryou.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.squad.musicmatters.core.ui.FadeTransition
import com.squad.musicmatters.core.ui.SlideTransition
import com.squad.musicmatters.feature.foryou.ForYouScreen
import kotlinx.serialization.Serializable

@Serializable data object ForYouRoute

fun NavController.navigateToForYouScreen( navOptions: NavOptions ) =
    navigate( route = ForYouRoute, navOptions )

fun NavGraphBuilder.forYouScreen(
    onViewAlbum: ( Long ) -> Unit,
    onViewArtist: ( Long ) -> Unit,
) {
    composable<ForYouRoute>(
        enterTransition = { SlideTransition.slideUp.enterTransition() },
        exitTransition = { FadeTransition.exitTransition() }
    ) {
        ForYouScreen(
            onViewAlbum = onViewAlbum,
            onViewArtist = onViewArtist,
        )
    }
}