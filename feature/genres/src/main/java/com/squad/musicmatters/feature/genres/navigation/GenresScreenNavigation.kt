package com.squad.musicmatters.feature.genres.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.squad.musicmatters.core.model.Genre
import com.squad.musicmatters.core.ui.FadeTransition
import com.squad.musicmatters.core.ui.SlideTransition
import com.squad.musicmatters.feature.genres.GenresScreen
import kotlinx.serialization.Serializable

@Serializable data object GenresRoute

fun NavController.navigateToGenres( navOptions: NavOptions ) {
    navigate(
        route = GenresRoute,
        navOptions = navOptions
    )
}

fun NavGraphBuilder.genresScreen(
    onNavigateBack: () -> Unit,
    onViewGenre: ( Genre ) -> Unit,
    onNavigateToSettings: () -> Unit,
) {
    composable<GenresRoute>(
        enterTransition = { SlideTransition.slideUp.enterTransition() },
        exitTransition = { FadeTransition.exitTransition() }
    ) {
        GenresScreen(
            onNavigateBack = onNavigateBack,
            onViewGenre = onViewGenre,
            onNavigateToSettings = onNavigateToSettings,
        )
    }
}