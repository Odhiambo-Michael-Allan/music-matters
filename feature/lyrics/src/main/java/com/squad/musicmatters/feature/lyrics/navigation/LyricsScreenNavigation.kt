package com.squad.musicmatters.feature.lyrics.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.squad.musicmatters.feature.lyrics.LyricsScreen
import kotlinx.serialization.Serializable


@Serializable data object LyricsRoute

fun NavController.navigateToLyricsScreen( navOptions: NavOptions ) =
    navigate( route = LyricsRoute, navOptions )

fun NavGraphBuilder.lyricsScreen(
    onNavigateBack: () -> Unit,
) {
    composable<LyricsRoute>() {
        LyricsScreen(
            onNavigateBack = onNavigateBack
        )
    }
}