package com.squad.musicmatters.feature.settings.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.squad.musicmatters.core.ui.SlideTransition
import com.squad.musicmatters.feature.settings.SettingsScreen
import kotlinx.serialization.Serializable

fun NavController.navigateToSettings( navOptions: NavOptions ) =
    navigate( route = SettingsRoute, navOptions )
@Serializable data object SettingsRoute

fun NavGraphBuilder.settingsScreen(
    onNavigateBack: () -> Unit,
) {
    composable<SettingsRoute>(
        enterTransition = { SlideTransition.slideUp.enterTransition() }
    ) {
        SettingsScreen(
            onNavigateBack = onNavigateBack,
        )
    }
}