package com.squad.musicmatters.feature.genre.navigation

import android.net.Uri
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.squad.musicmatters.core.model.Song
import com.squad.musicmatters.core.ui.SlideTransition
import com.squad.musicmatters.feature.genre.GenreScreen
import kotlinx.serialization.Serializable

@Serializable data class GenreRoute(
    // The name of the genre to be displayed at this destination
    val genreName: String,
)

fun NavController.navigateToGenre(
    genreName: String,
    navOptions: NavOptions
) {
    navigate(
        route = GenreRoute( genreName = genreName ),
        navOptions = navOptions,
    )
}

fun NavGraphBuilder.genreScreen(
    onNavigateBack: () -> Unit,
    onShowSnackBar: ( String ) -> Unit,
    onDeleteSong: ( Song ) -> Unit,
    onViewAlbum: ( Long ) -> Unit,
    onViewArtist: ( Long ) -> Unit,
    onShareSong: ( Uri ) -> Unit,
) {
    composable<GenreRoute>(
        enterTransition = { SlideTransition.slideUp.enterTransition() },
        exitTransition = { SlideTransition.slideDown.exitTransition() },
    ) {
        GenreScreen(
            onNavigateBack = onNavigateBack,
            onShowSnackBar = onShowSnackBar,
            onDeleteSong = onDeleteSong,
            onViewAlbum = onViewAlbum,
            onViewArtist = onViewArtist,
            onShareSong = onShareSong,
        )
    }
}