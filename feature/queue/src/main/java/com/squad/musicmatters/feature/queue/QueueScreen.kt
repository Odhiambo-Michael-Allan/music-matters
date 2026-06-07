package com.squad.musicmatters.feature.queue

import android.net.Uri
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.squad.musicMatters.core.i8n.R
import com.squad.musicmatters.core.datastore.DefaultPreferences
import com.squad.musicmatters.core.designsystem.component.DevicePreviews
import com.squad.musicmatters.core.designsystem.component.MusicMattersIcons
import com.squad.musicmatters.core.designsystem.theme.MusicMattersTheme
import com.squad.musicmatters.core.model.LoopMode
import com.squad.musicmatters.core.model.Playlist
import com.squad.musicmatters.core.model.Song
import com.squad.musicmatters.core.ui.MusicMattersPreviewParametersProvider
import com.squad.musicmatters.core.ui.PreviewData
import com.squad.musicmatters.core.ui.TopAppBarMinimalTitle
import com.squad.musicmatters.core.ui.dialog.NewPlaylistDialog
import com.squad.musicmatters.feature.queue.components.QueueList

@Composable
internal fun QueueScreen(
    viewModel: QueueScreenViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit,
) {

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    QueueScreenContent(
        uiState = uiState,
        onNavigateUp = onNavigateBack,
//        onCreatePlaylist = { title, songs -> viewModel.createPlaylist( title, songs ) },
//        onFavorite = viewModel::addToFavorites,
        playSong = viewModel::playSongs,
//        onPlayNext = viewModel::playSongNext,
//        onViewAlbum = onViewAlbum,
//        onViewArtist = onViewArtist,
//        onAddToQueue = viewModel::addSongToQueue,
//        onAddSongsToPlaylist = { playlist, songs ->
//            viewModel.addSongsToPlaylist( playlist, songs )
//        },
//        onShareSong = {
//            onShareSong( it, uiState.language.shareFailedX( "" ) )
//        },
//        onDeleteSong = onDeleteSong,
        onMoveSong = viewModel::moveSong,
        onShuffle = viewModel::shuffle,
//        onShowSnackBar = onShowSnackBar,
    )
}

@Composable
private fun QueueScreenContent(
    uiState: QueueScreenUiState,
    onNavigateUp: () -> Unit,
    playSong: ( Song, List<Song> ) -> Unit,
    onMoveSong: ( Int, Int ) -> Unit,
    onShuffle: () -> Unit,
) {

    var showSaveDialog by remember { mutableStateOf( false ) }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        QueueScreenTopAppBar(
            onBackArrowClick = onNavigateUp,
            onShuffle = onShuffle,
        )
        when ( uiState ) {
            QueueScreenUiState.Loading -> {}
            is QueueScreenUiState.Success -> {
                QueueList(
                    songsInQueue = uiState.songsInQueue,
                    currentlyPlayingSongId = uiState.currentlyPlayingSongId,
                    playSong = playSong,
                    onMoveSong = onMoveSong,
                )
            }
        }
    }
}

@OptIn( ExperimentalMaterial3Api::class )
@Composable
private fun QueueScreenTopAppBar(
    modifier: Modifier = Modifier,
    onBackArrowClick: () -> Unit,
    onShuffle: () -> Unit,
) {
    CenterAlignedTopAppBar(
        modifier = modifier,
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = Color.Transparent
        ),
        navigationIcon = {
            IconButton( onClick = onBackArrowClick ) {
                Icon(
                    imageVector = MusicMattersIcons.NavigateBack,
                    contentDescription = null
                )
            }
        },
        title = {
            TopAppBarMinimalTitle {
                Text( text = stringResource( id = R.string.core_i8n_queue ) )
            }
        },
        actions = {
            IconButton(
                onClick = onShuffle
            ) {
                Icon(
                    imageVector = MusicMattersIcons.Shuffle,
                    contentDescription = null,
                )
            }
        }
    )
}



@DevicePreviews
@Composable
private fun QueueScreenContentPreview(
    @PreviewParameter( MusicMattersPreviewParametersProvider::class )
    previewData: PreviewData
) {
    MusicMattersTheme(
        themeMode = DefaultPreferences.THEME_MODE,
        primaryColorName = DefaultPreferences.PRIMARY_COLOR_NAME,
        fontScale = 1.0f,
        useMaterialYou = true
    ) {
        QueueScreenContent(
            uiState = QueueScreenUiState.Success(
                songsInQueue = previewData.songs,
                currentlyPlayingSongId = previewData.songs.first().id,
            ),
            onNavigateUp = {},
            playSong = { _, _ -> },
            onMoveSong = { _, _ -> },
            onShuffle = {},
        )
    }
}

