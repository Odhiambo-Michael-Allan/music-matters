package com.squad.musicmatters.feature.search.navigation

import android.net.Uri
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.squad.musicmatters.core.model.Album
import com.squad.musicmatters.core.model.Genre
import com.squad.musicmatters.core.model.Song
import com.squad.musicmatters.core.ui.FadeTransition
import com.squad.musicmatters.core.ui.SlideTransition
import com.squad.musicmatters.feature.search.SearchScreen
import kotlinx.serialization.Serializable

@Serializable data object SearchRoute

fun NavController.navigateToSearchScreen( navOptions: NavOptions ) {
    navigate(
        route = SearchRoute,
        navOptions = navOptions,
    )
}

fun NavGraphBuilder.searchScreen(
    onNavigateBack: () -> Unit,
    onViewAlbum: ( Long ) -> Unit,
    onViewArtist: ( Long ) -> Unit,
    onViewPlaylist: ( String ) -> Unit,
    onShowSnackBar: ( String ) -> Unit,
    onViewGenre: ( Genre) -> Unit,
    onDeleteSong: ( Song ) -> Unit,
    onShareSong: ( Uri ) -> Unit,
) {
    composable<SearchRoute>(
        enterTransition = { SlideTransition.slideUp.enterTransition() },
        exitTransition = { FadeTransition.exitTransition() }
    ) {
        SearchScreen(
            onNavigateBack = onNavigateBack,
            onViewAlbum = onViewAlbum,
            onViewArtist = onViewArtist,
            onShowSnackBar = onShowSnackBar,
            onDeleteSong = onDeleteSong,
            onShareSong = onShareSong,
            onViewGenre = onViewGenre,
            onViewPlaylist = onViewPlaylist,
        )
    }
}