package com.squad.musicmatters.feature.artist.navigation

import android.net.Uri
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.squad.musicmatters.core.model.Song
import com.squad.musicmatters.core.ui.SlideTransition
import com.squad.musicmatters.feature.artist.ArtistScreen
import kotlinx.serialization.Serializable

@Serializable data class ArtistRoute(
    // The id of the artist to be displayed at this destination
    val artistId: Long
)

fun NavController.navigateToArtist(
    artistId: Long,
    navOptions: NavOptions
) {
    navigate(
        route = ArtistRoute( artistId = artistId ),
        navOptions = navOptions,
    )
}

fun NavGraphBuilder.artistScreen(
    onNavigateBack: () -> Unit,
    onShowSnackBar: ( String ) -> Unit,
    onDeleteSong: ( Song ) -> Unit,
    onViewAlbum: ( Long ) -> Unit,
    onViewArtist: ( Long ) -> Unit,
    onShareSong: ( Uri ) -> Unit,
) {
    composable<ArtistRoute>(
        enterTransition = { SlideTransition.slideUp.enterTransition() },
        exitTransition = { SlideTransition.slideDown.exitTransition() },
    ) {
        ArtistScreen(
            onNavigateBack = onNavigateBack,
            onShowSnackBar = onShowSnackBar,
            onDeleteSong = onDeleteSong,
            onViewAlbum = onViewAlbum,
            onViewArtist = onViewArtist,
            onShareSong = onShareSong,
        )
    }
}
