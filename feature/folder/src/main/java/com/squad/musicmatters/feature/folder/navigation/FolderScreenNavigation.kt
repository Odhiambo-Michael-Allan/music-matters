package com.squad.musicmatters.feature.folder.navigation

import android.net.Uri
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.squad.musicmatters.core.model.Song
import com.squad.musicmatters.core.ui.SlideTransition
import com.squad.musicmatters.feature.folder.FolderScreen
import kotlinx.serialization.Serializable

@Serializable data class FolderRoute(
    // The path of the folder to be displayed at this destination.
    val path: String,
)

fun NavController.navigateToFolder(
    path: String,
    navOptions: NavOptions
) {
    navigate(
        route = FolderRoute( path = path ),
        navOptions = navOptions,
    )
}

fun NavGraphBuilder.folderScreen(
    onNavigateBack: () -> Unit,
    onShowSnackBar: ( String ) -> Unit,
    onDeleteSong: ( Song ) -> Unit,
    onViewAlbum: ( Long ) -> Unit,
    onViewArtist: ( Long ) -> Unit,
    onShareSong: ( Uri ) -> Unit,
) {
    composable<FolderRoute>(
        enterTransition = { SlideTransition.slideUp.enterTransition() },
        exitTransition = { SlideTransition.slideDown.exitTransition() }
    ) {
        FolderScreen(
            onNavigateBack = onNavigateBack,
            onShowSnackBar = onShowSnackBar,
            onDeleteSong = onDeleteSong,
            onViewAlbum = onViewAlbum,
            onViewArtist = onViewArtist,
            onShareSong = onShareSong,
        )
    }
}