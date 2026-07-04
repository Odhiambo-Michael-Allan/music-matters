package com.squad.musicmatters.feature.queue.navigation

import android.net.Uri
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.squad.musicmatters.core.model.Song
import com.squad.musicmatters.core.ui.SlideTransition
import com.squad.musicmatters.feature.queue.QueueScreen
import kotlinx.serialization.Serializable

@Serializable data object QueueRoute

fun NavController.navigateToQueueScreen( navOptions: NavOptions ) =
    navigate( route = QueueRoute, navOptions )

fun NavGraphBuilder.queueScreen(
    onNavigateBack: () -> Unit,
    onViewAlbum: ( String ) -> Unit,
    onViewArtist: ( String ) -> Unit,
    onShareSong: ( Uri ) -> Unit,
    onDeleteSong: ( Song ) -> Unit,
    onShowSnackBar: ( String ) -> Unit,
) {
    composable<QueueRoute>(
        enterTransition = { SlideTransition.slideUp.enterTransition() }
    ) {
        QueueScreen(
            onNavigateBack = onNavigateBack,
            onViewAlbum = onViewAlbum,
            onViewArtist = onViewArtist,
            onShareSong = onShareSong,
            onDeleteSong = onDeleteSong,
            onShowSnackBar = onShowSnackBar,
        )
    }
}