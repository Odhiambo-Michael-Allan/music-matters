package com.squad.musicmatters.feature.songs.navigation

import android.net.Uri
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.squad.musicmatters.core.model.Song
import com.squad.musicmatters.core.ui.SlideTransition
import com.squad.musicmatters.feature.songs.SongsScreen
import kotlinx.serialization.Serializable

@Serializable data object SongsRoute

fun NavController.navigateToSongs( navOptions: NavOptions ) =
    navigate( route = SongsRoute, navOptions )

fun NavGraphBuilder.songsScreen(
    onViewAlbum: ( String ) -> Unit,
    onViewArtist: ( String ) -> Unit,
    onShareSong: ( Uri, String ) -> Unit,
    onDeleteSong: ( Song ) -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToSearch: () -> Unit,
) {
    composable<SongsRoute>(
        enterTransition = { SlideTransition.slideUp.enterTransition() },
//        exitTransition = { FadeTransition.exitTransition() }
    ) {
        SongsScreen(
            onViewAlbum = onViewAlbum,
            onViewArtist = onViewArtist,
            onShareSong = onShareSong,
            onDeleteSong = onDeleteSong,
            onNavigateToSettings = onNavigateToSettings,
            onNavigateToSearch = onNavigateToSearch,
        )
    }
}