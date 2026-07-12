package com.squad.musicmatters.feature.folders.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.squad.musicmatters.core.ui.FadeTransition
import com.squad.musicmatters.core.ui.SlideTransition
import com.squad.musicmatters.feature.folders.FoldersScreen
import kotlinx.serialization.Serializable

@Serializable data object FoldersRoute

fun NavController.navigateToFolders( navOptions: NavOptions ) {
    navigate(
        route = FoldersRoute,
        navOptions = navOptions,
    )
}

fun NavGraphBuilder.foldersScreen(
    onViewFolder: ( String ) -> Unit,
    onNavigateBack: () -> Unit,
    onNavigateToSettings: () -> Unit,
) {
    composable<FoldersRoute>(
        enterTransition = { SlideTransition.slideUp.enterTransition() },
        exitTransition = { FadeTransition.exitTransition() }
    ) {
        FoldersScreen(
            onViewFolder = onViewFolder,
            onNavigateBack = onNavigateBack,
            onNavigateToSettings = onNavigateToSettings,
        )
    }
}