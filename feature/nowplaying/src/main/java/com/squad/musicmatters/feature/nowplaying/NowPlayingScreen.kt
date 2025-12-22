package com.squad.musicmatters.feature.nowplaying

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import androidx.activity.result.contract.ActivityResultContract
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Album
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.rounded.FastForward
import androidx.compose.material.icons.rounded.FastRewind
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.SkipNext
import androidx.compose.material.icons.rounded.SkipPrevious
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.window.core.layout.WindowWidthSizeClass
import com.squad.musicmatters.core.datastore.DefaultPreferences
import com.squad.musicmatters.core.designsystem.component.DevicePreviews
import com.squad.musicmatters.core.media.media.extensions.formatMilliseconds
import com.squad.musicmatters.core.designsystem.theme.MusicMattersTheme
import com.squad.musicmatters.core.media.connection.PlaybackPosition
import com.squad.musicmatters.core.media.connection.PlayerState
import com.squad.musicmatters.core.model.LoopMode
import com.squad.musicmatters.core.model.PlaylistInfo
import com.squad.musicmatters.core.model.Song
import com.squad.musicmatters.core.model.SongAdditionalMetadataInfo
import com.squad.musicmatters.core.model.ThemeMode
import com.squad.musicmatters.core.ui.BottomSheetMenuItem
import com.squad.musicmatters.core.ui.DynamicAsyncImage
import com.squad.musicmatters.core.ui.FadeTransition
import com.squad.musicmatters.core.ui.GenericOptionsBottomSheet
import com.squad.musicmatters.core.ui.dialog.SongDetailsDialog


// Stateful
@Composable
fun NowPlayingBottomSheet(
    viewModel: NowPlayingViewModel = hiltViewModel(),
    onViewAlbum: ( String ) -> Unit,
    onViewArtist: ( String ) -> Unit,
    onNavigateToQueueScreen: () -> Unit,
    onLaunchEqualizerActivity: () -> Unit,
    onHideBottomSheet: () -> Unit
) {

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val playbackPosition by viewModel.playbackPosition.collectAsStateWithLifecycle()

    NowPlayingScreenContent(
        uiState = uiState,
        playbackPosition = playbackPosition,
        onFavorite = viewModel::addToFavorites,
        onPausePlayButtonClick = viewModel::playPause,
        onPreviousButtonClick = viewModel::playPreviousSong,
        onPlayNext = viewModel::playNextSong,
        onFastRewindButtonClick = viewModel::fastRewind,
        onFastForwardButtonClick = viewModel::fastForward,
        onSeekStart = viewModel::onSeekStarted,
        onSeekEnd = viewModel::onSeekEnd,
        onArtworkClicked = { song ->
            onHideBottomSheet()
            song.albumTitle?.let { onViewAlbum( it ) }
        },
        onArtistClicked = {
            onHideBottomSheet()
            onViewArtist( it )
        },
        onToggleLoopMode = viewModel::setLoopMode,
        onToggleShuffleMode = viewModel::setShuffleMode,
        onPlayingSpeedChange = viewModel::onPlayingSpeedChange,
        onPlayingPitchChange = viewModel::onPlayingPitchChange,
        onQueueClicked = {
            onHideBottomSheet()
            onNavigateToQueueScreen()
        },
        onCreateEqualizerActivityContract = onLaunchEqualizerActivity,
        onGetSongsInPlaylist = {
            emptyList()
//            viewModel::getSongsInPlaylist
        },
        onSearchSongsMatchingQuery = {
            emptyList()
//            viewModel::searchSongsMatching
        },
        onGetPlaylists = { emptyList() },
        durationFormatter = { it.formatMilliseconds() },
        onGetSongAdditionalMetadata = { null },
        onCreatePlaylist = viewModel::createPlaylist,
        onAddSongsToPlaylist = viewModel::addSongsToPlaylist,
        onViewAlbum = onViewAlbum,
        onViewArtist = onViewArtist,
        onHideNowPlayingBottomSheet = onHideBottomSheet,
        onSwipeArtworkLeft = viewModel::playNextSong,
        onSwipeArtworkRight = viewModel::playPreviousSong
    )
}


@OptIn( ExperimentalMaterial3Api::class )
@Composable
fun NowPlayingScreenContent(
    uiState: NowPlayingScreenUiState,
    playbackPosition: PlaybackPosition,
    durationFormatter: ( Long ) -> String,
    onArtistClicked: ( String ) -> Unit,
    onFavorite: ( String ) -> Unit,
    onPausePlayButtonClick: () -> Unit,
    onPreviousButtonClick: () -> Unit,
    onPlayNext: () -> Unit,
    onFastRewindButtonClick: () -> Unit,
    onFastForwardButtonClick: () -> Unit,
    onSeekStart: () -> Unit,
    onSeekEnd: ( Long ) -> Unit,
    onArtworkClicked: ( Song ) -> Unit,
    onSwipeArtworkLeft: () -> Unit,
    onSwipeArtworkRight: () -> Unit,
    onQueueClicked: () -> Unit,
    onToggleLoopMode: ( LoopMode ) -> Unit,
    onToggleShuffleMode: ( Boolean ) -> Unit,
    onPlayingSpeedChange: ( Float ) -> Unit,
    onPlayingPitchChange: ( Float ) -> Unit,
    onCreateEqualizerActivityContract: () -> Unit,
    onGetSongsInPlaylist: (PlaylistInfo ) -> List<Song>,
    onSearchSongsMatchingQuery: ( String ) -> List<Song>,
    onCreatePlaylist: ( String, List<Song> ) -> Unit,
    onAddSongsToPlaylist: (PlaylistInfo, List<Song> ) -> Unit,
    onGetPlaylists: () -> List<PlaylistInfo>,
    onViewAlbum: ( String ) -> Unit,
    onViewArtist: ( String ) -> Unit,
    onHideNowPlayingBottomSheet: () -> Unit,
    onGetSongAdditionalMetadata: () -> SongAdditionalMetadataInfo?
) {
    val currentWindowSizeClass = currentWindowAdaptiveInfo().windowSizeClass
    val currentConfiguration = LocalConfiguration.current
    var showOptionsMenu by remember { mutableStateOf( false ) }
    var showSongDetailsDialog by remember { mutableStateOf( false ) }

    when ( uiState ) {
        NowPlayingScreenUiState.Loading -> {}
        is NowPlayingScreenUiState.Success -> {
            uiState.queue.firstOrNull { it.id == uiState.playerState.currentlyPlayingSongId }?.let { song ->
                when ( currentWindowSizeClass.windowWidthSizeClass ) {
                    WindowWidthSizeClass.COMPACT, WindowWidthSizeClass.MEDIUM -> {
                        if ( currentConfiguration.orientation == Configuration.ORIENTATION_PORTRAIT ) {
                            PortraitLayout(
                                uiState = uiState,
                                currentlyPlayingSong = song,
                                playbackPosition = playbackPosition,
                                durationFormatter = durationFormatter,
                                onFavorite = onFavorite,
                                onSwipeArtworkLeft = onSwipeArtworkLeft,
                                onSwipeArtworkRight = onSwipeArtworkRight,
                                onArtworkClicked = onArtworkClicked,
                                onArtistClicked = onArtistClicked,
                                onShowOptionsMenu = { showOptionsMenu = true },
                                onSeekStart = onSeekStart,
                                onSeekEnd = onSeekEnd,
                                onPausePlayButtonClick = onPausePlayButtonClick,
                                onPreviousButtonClick = onPreviousButtonClick,
                                onPlayNext = onPlayNext,
                                onFastRewindButtonClick = onFastRewindButtonClick,
                                onFastForwardButtonClick = onFastForwardButtonClick,
                                onQueueClicked = onQueueClicked,
                                onToggleLoopMode = onToggleLoopMode,
                                onToggleShuffleMode = onToggleShuffleMode,
                                onPlayingSpeedChange = onPlayingSpeedChange,
                                onPlayingPitchChange = onPlayingPitchChange,
                                onGetSongAdditionalMetadata = onGetSongAdditionalMetadata,
                                onCreateEqualizerActivityContract = onCreateEqualizerActivityContract
                            )
                        } else {
                            LandscapeLayout(
                                uiState = uiState,
                                currentlyPlayingSong = song,
                                playbackPosition = playbackPosition,
                                durationFormatter = durationFormatter,
                                onFavorite = onFavorite,
                                onSwipeArtworkLeft = onSwipeArtworkLeft,
                                onSwipeArtworkRight = onSwipeArtworkRight,
                                onArtworkClicked = onArtworkClicked,
                                onArtistClicked = onArtistClicked,
                                onShowOptionsMenu = { showOptionsMenu = true },
                                onSeekStart = onSeekStart,
                                onSeekEnd = onSeekEnd,
                                onPausePlayButtonClick = onPausePlayButtonClick,
                                onPreviousButtonClick = onPreviousButtonClick,
                                onPlayNext = onPlayNext,
                                onFastRewindButtonClick = onFastRewindButtonClick,
                                onFastForwardButtonClick = onFastForwardButtonClick,
                                onQueueClicked = onQueueClicked,
                                onToggleLoopMode = onToggleLoopMode,
                                onToggleShuffleMode = onToggleShuffleMode,
                                onPlayingSpeedChange = onPlayingSpeedChange,
                                onPlayingPitchChange = onPlayingPitchChange,
                                onGetSongAdditionalMetadata = onGetSongAdditionalMetadata,
                                onCreateEqualizerActivityContract = onCreateEqualizerActivityContract
                            )
                        }
                    }
                    WindowWidthSizeClass.EXPANDED -> {
                        LandscapeLayout(
                            uiState = uiState,
                            currentlyPlayingSong = song,
                            playbackPosition = playbackPosition,
                            durationFormatter = durationFormatter,
                            onFavorite = onFavorite,
                            onSwipeArtworkLeft = onSwipeArtworkLeft,
                            onSwipeArtworkRight = onSwipeArtworkRight,
                            onArtworkClicked = onArtworkClicked,
                            onArtistClicked = onArtistClicked,
                            onShowOptionsMenu = { showOptionsMenu = true },
                            onSeekStart = onSeekStart,
                            onSeekEnd = onSeekEnd,
                            onPausePlayButtonClick = onPausePlayButtonClick,
                            onPreviousButtonClick = onPreviousButtonClick,
                            onPlayNext = onPlayNext,
                            onFastRewindButtonClick = onFastRewindButtonClick,
                            onFastForwardButtonClick = onFastForwardButtonClick,
                            onQueueClicked = onQueueClicked,
                            onToggleLoopMode = onToggleLoopMode,
                            onToggleShuffleMode = onToggleShuffleMode,
                            onPlayingSpeedChange = onPlayingSpeedChange,
                            onPlayingPitchChange = onPlayingPitchChange,
                            onGetSongAdditionalMetadata = onGetSongAdditionalMetadata,
                            onCreateEqualizerActivityContract = onCreateEqualizerActivityContract
                        )
                    }
                }

                if ( showOptionsMenu ) {
                    ModalBottomSheet(
                        onDismissRequest = {
                            showOptionsMenu = false
                        }
                    ) {
                        GenericOptionsBottomSheet(
                            headerImageUri = song.artworkUri?.toUri(),
                            headerTitle = song.title,
                            titleIsHighlighted = true,
                            headerDescription = song.artists.joinToString(),
                            language = uiState.language,
                            onDismissRequest = { showOptionsMenu = false },
                            onPlayNext = {}, // No need to do anything as duplicates are not allowed in queue
                            onAddToQueue = {}, // No need to do anything as duplicates are not allowed in queue
                            onGetPlaylists = onGetPlaylists,
                            onGetSongsInPlaylist = onGetSongsInPlaylist,
                            onSearchSongsMatchingQuery = onSearchSongsMatchingQuery,
                            onCreatePlaylist = onCreatePlaylist,
                            onAddSongsToPlaylist = onAddSongsToPlaylist,
                            onGetSongs = { listOf( song ) },
                            leadingBottomSheetMenuItem = { onDismissRequest ->
                                BottomSheetMenuItem(
                                    leadingIcon = if ( uiState.currentlyPlayingSongIsFavorite ) {
                                        Icons.Filled.Favorite
                                    } else {
                                        Icons.Filled.FavoriteBorder
                                    },
                                    label = uiState.language.favorite,
                                    leadingIconTint = MaterialTheme.colorScheme.primary
                                ) {
                                    onDismissRequest()
                                    onFavorite( song.id )
                                }
                            },
                            trailingBottomSheetMenuItems = { onDismissRequest ->
                                song.albumTitle?.let { albumTitle ->
                                    BottomSheetMenuItem(
                                        leadingIcon = Icons.Default.Album,
                                        label = "${uiState.language.viewAlbum}: $albumTitle"
                                    ) {
                                        onDismissRequest()
                                        onHideNowPlayingBottomSheet()
                                        onViewAlbum( albumTitle )
                                    }
                                }
                                song.artists.forEach { artistName ->
                                    BottomSheetMenuItem(
                                        leadingIcon = Icons.Default.Person,
                                        label = "${uiState.language.viewArtist}: $artistName"
                                    ) {
                                        onDismissRequest()
                                        onHideNowPlayingBottomSheet()
                                        onViewArtist( artistName )
                                    }
                                }
                                BottomSheetMenuItem(
                                    leadingIcon = Icons.Default.Info,
                                    label = uiState.language.details
                                ) {
                                    onDismissRequest()
                                    showSongDetailsDialog = true
                                }
                            }
                        )
                    }
                }

                if ( showSongDetailsDialog ) {
                    SongDetailsDialog(
                        song = song,
                        language = uiState.language,
                        durationFormatter = { it.formatMilliseconds() },
                        isLoadingSongAdditionalMetadata = false,
                        onGetSongAdditionalMetadata = onGetSongAdditionalMetadata
                    ) {
                        showSongDetailsDialog = false
                    }
                }
            }
        }
    }

}

@OptIn( ExperimentalLayoutApi::class )
@Composable
fun PortraitLayout(
    uiState: NowPlayingScreenUiState.Success,
    currentlyPlayingSong: Song,
    playbackPosition: PlaybackPosition,
    durationFormatter: ( Long ) -> String,
    onFavorite: ( String ) -> Unit,
    onSwipeArtworkLeft: () -> Unit,
    onSwipeArtworkRight: () -> Unit,
    onArtworkClicked: ( Song ) -> Unit,
    onArtistClicked: ( String ) -> Unit,
    onShowOptionsMenu: () -> Unit,
    onSeekStart: () -> Unit,
    onSeekEnd: ( Long ) -> Unit,
    onPausePlayButtonClick: () -> Unit,
    onPreviousButtonClick: () -> Unit,
    onPlayNext: () -> Unit,
    onFastRewindButtonClick: () -> Unit,
    onFastForwardButtonClick: () -> Unit,
    onQueueClicked: () -> Unit,
    onToggleLoopMode: ( LoopMode ) -> Unit,
    onToggleShuffleMode: ( Boolean ) -> Unit,
    onPlayingSpeedChange: ( Float ) -> Unit,
    onPlayingPitchChange: ( Float ) -> Unit,
    onGetSongAdditionalMetadata: () -> SongAdditionalMetadataInfo?,
    onCreateEqualizerActivityContract: () -> Unit,
) {
    Column (
        modifier = Modifier
            .padding(start = 8.dp, end = 8.dp, top = 0.dp, bottom = 0.dp)
            .verticalScroll(rememberScrollState())
    ) {
        NowPlayingArtwork(
            modifier = Modifier
                .padding(16.dp, 0.dp)
                .fillMaxWidth(),
            artworkUri = currentlyPlayingSong.artworkUri?.toUri(),
            onSwipeLeft = onSwipeArtworkLeft,
            onSwipeRight = onSwipeArtworkRight,
            onArtworkClicked = { onArtworkClicked( currentlyPlayingSong ) }
        )
        Row {
            AnimatedContent(
                modifier = Modifier.weight( 1f ),
                label = "now-playing-body-content",
                targetState = currentlyPlayingSong,
                transitionSpec = {
                    FadeTransition.enterTransition()
                        .togetherWith( FadeTransition.exitTransition() )
                }
            ) { target ->
                Column (
                    modifier = Modifier.padding( 16.dp, 16.dp )
                ) {
                    Text(
                        text = target.title,
                        style = MaterialTheme.typography.titleLarge
                            .copy( fontWeight = FontWeight.Bold ),
                        maxLines = 1,
                        modifier = Modifier.basicMarquee( iterations = Int.MAX_VALUE )
                    )
                    FlowRow {
                        target.artists.forEachIndexed { index, it ->
                            Text(
                                text = it,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.pointerInput( Unit ) {
                                    detectTapGestures { _ ->
                                        onArtistClicked( it )
                                    }
                                }
                            )
                            if ( index != target.artists.size - 1 ) Text( text = ", " )
                        }
                    }
                    if ( uiState.userData.showNowPlayingAudioInformation ) {
                        onGetSongAdditionalMetadata()?.let {
                            Text(
                                text = it.toSamplingInfoString( uiState.language ),
                                style = MaterialTheme.typography.labelSmall
                                    .copy( color = LocalContentColor.current.copy( alpha = 0.7f ) ),
                            )
                        } ?: run {
                            Row (
                                modifier = Modifier.padding( top = 8.dp, bottom = 4.dp ),
                                horizontalArrangement = Arrangement.spacedBy( 4.dp ),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                CircularProgressIndicator(
                                    strokeWidth = 2.dp,
                                    modifier = Modifier.size( 12.dp )
                                )
                                Text(
                                    text = uiState.language.loading,
                                    style = MaterialTheme.typography.labelMedium
                                )
                            }
                        }
                    }
                }
            }
            Row (
                modifier = Modifier.padding( 0.dp, 16.dp )
            ) {
                IconButton(
                    modifier = Modifier.offset( 4.dp ),
                    onClick = { onFavorite( currentlyPlayingSong.id ) }
                ) {
                    AnimatedContent(
                        targetState = uiState.currentlyPlayingSongIsFavorite,
                        label = "now-playing-screen-is-favorite-icon"
                    ) {
                        Icon(
                            imageVector = if ( it ) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )

                    }
                }
                IconButton(
                    onClick = onShowOptionsMenu
                ) {
                    Icon(
                        imageVector = Icons.Filled.MoreVert,
                        contentDescription = null
                    )
                }
            }
        }
        Spacer( modifier = Modifier.height( 24.dp ) )
        NowPlayingSeekBar(
            playbackPosition = playbackPosition,
            durationFormatter = durationFormatter,
            onSeekStart = onSeekStart,
            onSeekEnd = onSeekEnd
        )
        Spacer( modifier = Modifier.height( 24.dp ) )
        when {
            uiState.userData.controlsLayoutDefault ->
                NowPlayingDefaultControlsLayout(
                    isPlaying = uiState.playerState.isPlaying,
                    enableSeekControls = uiState.userData.showNowPlayingSeekControls,
                    onPausePlayButtonClick = onPausePlayButtonClick,
                    onPreviousButtonClick = onPreviousButtonClick,
                    onFastRewindButtonClick = onFastRewindButtonClick,
                    onFastForwardButtonClick = onFastForwardButtonClick,
                    onNextButtonClick = onPlayNext
                )
            else ->
                NowPlayingTraditionalControlsLayout(
                    enableSeekControls = uiState.userData.showNowPlayingSeekControls,
                    isPlaying = uiState.playerState.isPlaying,
                    onPreviousButtonClick = onPreviousButtonClick,
                    onFastRewindButtonClick = onFastRewindButtonClick,
                    onPausePlayButtonClick = onPausePlayButtonClick,
                    onFastForwardButtonClick = onFastForwardButtonClick,
                    onNextButtonClick = onPlayNext
                )
        }
        Spacer( modifier = Modifier.height( 16.dp ) )
        NowPlayingBodyBottomBar(
            language = uiState.language,
            currentSongIndex = uiState.queue.indexOfFirst { it.id == currentlyPlayingSong.id },
            queueSize = uiState.queue.size,
            currentLoopMode = uiState.userData.loopMode,
            shuffle = uiState.userData.shuffle,
            currentSpeed = uiState.userData.playbackSpeed,
            currentPitch = uiState.userData.playbackPitch,
            onQueueClicked = onQueueClicked,
            onToggleLoopMode = onToggleLoopMode,
            onToggleShuffleMode = onToggleShuffleMode,
            onSpeedChange = onPlayingSpeedChange,
            onPitchChange = onPlayingPitchChange,
            onCreateEqualizerActivityContract = onCreateEqualizerActivityContract
        )
        Spacer( modifier = Modifier.size( 32.dp ) )
    }
}



@OptIn( ExperimentalLayoutApi::class )
@Composable
fun LandscapeLayout(
    uiState: NowPlayingScreenUiState.Success,
    currentlyPlayingSong: Song,
    playbackPosition: PlaybackPosition,
    durationFormatter: ( Long ) -> String,
    onFavorite: ( String ) -> Unit,
    onSwipeArtworkLeft: () -> Unit,
    onSwipeArtworkRight: () -> Unit,
    onArtworkClicked: ( Song ) -> Unit,
    onArtistClicked: ( String ) -> Unit,
    onShowOptionsMenu: () -> Unit,
    onSeekStart: () -> Unit,
    onSeekEnd: ( Long ) -> Unit,
    onPausePlayButtonClick: () -> Unit,
    onPreviousButtonClick: () -> Unit,
    onPlayNext: () -> Unit,
    onFastRewindButtonClick: () -> Unit,
    onFastForwardButtonClick: () -> Unit,
    onQueueClicked: () -> Unit,
    onToggleLoopMode: ( LoopMode ) -> Unit,
    onToggleShuffleMode: ( Boolean ) -> Unit,
    onPlayingSpeedChange: ( Float ) -> Unit,
    onPlayingPitchChange: ( Float ) -> Unit,
    onCreateEqualizerActivityContract: () -> Unit,
    onGetSongAdditionalMetadata: () -> SongAdditionalMetadataInfo?
) {
    Row (
        modifier = Modifier
            .fillMaxSize()
            .padding( 24.dp ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        NowPlayingArtwork(
            artworkUri = currentlyPlayingSong.artworkUri?.toUri(),
            onSwipeLeft = onSwipeArtworkLeft,
            onSwipeRight = onSwipeArtworkRight,
            onArtworkClicked = { onArtworkClicked( currentlyPlayingSong ) }
        )
        Column {
            Row {
                AnimatedContent(
                    modifier = Modifier.weight( 1f ),
                    label = "now-playing-body-content",
                    targetState = currentlyPlayingSong,
                    transitionSpec = {
                        FadeTransition.enterTransition()
                            .togetherWith( FadeTransition.exitTransition() )
                    }
                ) { target ->
                    Column (
                        modifier = Modifier.padding( 16.dp, 16.dp )
                    ) {
                        Text(
                            text = target.title,
                            style = MaterialTheme.typography.titleLarge
                                .copy( fontWeight = FontWeight.Bold ),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        FlowRow {
                            target.artists.forEachIndexed { index, it ->
                                Text(
                                    text = it,
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis,
                                    modifier = Modifier.pointerInput( Unit ) {
                                        detectTapGestures { _ ->
                                            onArtistClicked( it )
                                        }
                                    }
                                )
                                if ( index != target.artists.size - 1 ) Text( text = ", " )
                            }
                        }
                    if ( uiState.userData.showNowPlayingAudioInformation ) {
                        onGetSongAdditionalMetadata()?.let {
                            Text(
                                text = it.toSamplingInfoString( uiState.language ),
                                style = MaterialTheme.typography.labelSmall
                                    .copy( color = LocalContentColor.current.copy( alpha = 0.7f ) ),
                            )
                        } ?: run {
                            Row (
                                modifier = Modifier.padding( top = 8.dp, bottom = 4.dp ),
                                horizontalArrangement = Arrangement.spacedBy( 4.dp ),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                CircularProgressIndicator(
                                    strokeWidth = 2.dp,
                                    modifier = Modifier.size( 12.dp )
                                )
                                Text(
                                    text = uiState.language.loading,
                                    style = MaterialTheme.typography.labelMedium
                                )
                            }
                        }
                    }
                    }
                }
                Row (
                    modifier = Modifier.padding( 0.dp, 16.dp )
                ) {
                    IconButton(
                        modifier = Modifier.offset( 4.dp ),
                        onClick = { onFavorite( currentlyPlayingSong.id ) }
                    ) {
                        AnimatedContent(
                            targetState = uiState.currentlyPlayingSongIsFavorite,
                            label = "now-playing-screen-is-favorite-icon"
                        ) {
                            Icon(
                                imageVector = if ( it ) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )

                        }
                    }
                    IconButton(
                        onClick = onShowOptionsMenu
                    ) {
                        Icon(
                            imageVector = Icons.Filled.MoreVert,
                            contentDescription = null
                        )
                    }
                }
            }
            Spacer( modifier = Modifier.height( 24.dp ) )
            NowPlayingSeekBar(
                playbackPosition = playbackPosition,
                durationFormatter = durationFormatter,
                onSeekStart = onSeekStart,
                onSeekEnd = onSeekEnd
            )
            Spacer( modifier = Modifier.height( 24.dp ) )
            when {
                uiState.userData.controlsLayoutDefault ->
                    NowPlayingDefaultControlsLayout(
                        isPlaying = uiState.playerState.isPlaying,
                        enableSeekControls = uiState.userData.showNowPlayingSeekControls,
                        onPausePlayButtonClick = onPausePlayButtonClick,
                        onPreviousButtonClick = onPreviousButtonClick,
                        onFastRewindButtonClick = onFastRewindButtonClick,
                        onFastForwardButtonClick = onFastForwardButtonClick,
                        onNextButtonClick = onPlayNext
                    )
                else ->
                    NowPlayingTraditionalControlsLayout(
                        enableSeekControls = uiState.userData.showNowPlayingSeekControls,
                        isPlaying = uiState.playerState.isPlaying,
                        onPreviousButtonClick = onPreviousButtonClick,
                        onFastRewindButtonClick = onFastRewindButtonClick,
                        onPausePlayButtonClick = onPausePlayButtonClick,
                        onFastForwardButtonClick = onFastForwardButtonClick,
                        onNextButtonClick = onPlayNext
                    )
            }
            Spacer( modifier = Modifier.height( 16.dp ) )
            NowPlayingBodyBottomBar(
                language = uiState.language,
                currentSongIndex = uiState.queue.indexOfFirst { it.id == currentlyPlayingSong.id },
                queueSize = uiState.queue.size,
                currentLoopMode = uiState.userData.loopMode,
                shuffle = uiState.userData.shuffle,
                currentSpeed = uiState.userData.playbackSpeed,
                currentPitch = uiState.userData.playbackPitch,
                onQueueClicked = onQueueClicked,
                onToggleLoopMode = onToggleLoopMode,
                onToggleShuffleMode = onToggleShuffleMode,
                onSpeedChange = onPlayingSpeedChange,
                onPitchChange = onPlayingPitchChange,
                onCreateEqualizerActivityContract = onCreateEqualizerActivityContract
            )
            Spacer( modifier = Modifier.size( 32.dp ) )
        }
    }
}

@Composable
fun NowPlayingArtwork(
    modifier: Modifier = Modifier,
    artworkUri: Uri?,
    onSwipeLeft: () -> Unit,
    onSwipeRight: () -> Unit,
    onArtworkClicked: () -> Unit
) {
    Box(
        modifier = modifier
    ) {
        AnimatedContent(
            modifier = Modifier.align( Alignment.Center ),
            label = "now-playing-artwork",
            targetState = artworkUri,
            transitionSpec = {
                FadeTransition.enterTransition()
                    .togetherWith( FadeTransition.exitTransition() )
            }
        ) {
            DynamicAsyncImage(
                imageUri = it,
                contentDescription = "now-playing-artwork",
                modifier = Modifier
                    .sizeIn( maxWidth = 500.dp, maxHeight = 500.dp )
                    .aspectRatio( 1f )
                    .clip(MaterialTheme.shapes.medium )
                    .swipeable(
                        minimumDragAmount = 100f,
                        onSwipeLeft = onSwipeLeft,
                        onSwipeRight = onSwipeRight,
                    )
                    .pointerInput(Unit) {
                        detectTapGestures { _ -> onArtworkClicked() }
                    }
            )
        }
    }
}

@Composable
fun NowPlayingDefaultControlsLayout(
    isPlaying: Boolean,
    enableSeekControls: Boolean,
    onPausePlayButtonClick: () -> Unit,
    onPreviousButtonClick: () -> Unit,
    onFastRewindButtonClick: () -> Unit,
    onFastForwardButtonClick: () -> Unit,
    onNextButtonClick: () -> Unit
) {
    Row(
        modifier = Modifier.padding( 16.dp, 0.dp ),
        horizontalArrangement = Arrangement.spacedBy( 12.dp )
    ) {
        NowPlayingPlayPauseButton(
            style = NowPlayingControlButtonStyle(
                color = NowPlayingControlButtonColors.Primary
            ),
            isPlaying = isPlaying,
            onClick = onPausePlayButtonClick
        )
        NowPlayingSkipPreviousButton(
            style = NowPlayingControlButtonStyle(
                color = NowPlayingControlButtonColors.Surface
            ),
            onClick = onPreviousButtonClick
        )
        if ( enableSeekControls ) {
            NowPlayingFastRewindButton(
                style = NowPlayingControlButtonStyle(
                    color = NowPlayingControlButtonColors.Surface
                ),
                onClick = onFastRewindButtonClick
            )
            NowPlayingFastForwardButton(
                style = NowPlayingControlButtonStyle(
                    color = NowPlayingControlButtonColors.Surface
                ),
                onClick = onFastForwardButtonClick
            )
        }
        NowPlayingSkipNextButton(
            style = NowPlayingControlButtonStyle(
                color = NowPlayingControlButtonColors.Surface
            ),
            onClick = onNextButtonClick
        )
    }
}

@Composable
fun NowPlayingTraditionalControlsLayout(
    enableSeekControls: Boolean,
    isPlaying: Boolean,
    onPreviousButtonClick: () -> Unit,
    onFastRewindButtonClick: () -> Unit,
    onPausePlayButtonClick: () -> Unit,
    onFastForwardButtonClick: () -> Unit,
    onNextButtonClick: () -> Unit
) {
    Row (
        modifier = Modifier
            .padding(16.dp, 0.dp)
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        NowPlayingSkipPreviousButton(
            style = NowPlayingControlButtonStyle(
                color = NowPlayingControlButtonColors.Transparent,
                size = NowPlayingControlButtonSize.Large
            ),
            onClick = onPreviousButtonClick
        )
        Spacer( modifier = Modifier.width( 4.dp ) )
        if ( enableSeekControls ) {
            NowPlayingFastRewindButton(
                style = NowPlayingControlButtonStyle(
                    color = NowPlayingControlButtonColors.Transparent,
                    size = NowPlayingControlButtonSize.Large
                ),
                onClick = onFastRewindButtonClick
            )
            Spacer( modifier = Modifier.width( 4.dp ) )
        }
        NowPlayingPlayPauseButton(
            style = NowPlayingControlButtonStyle(
                color = NowPlayingControlButtonColors.Primary,
                size = NowPlayingControlButtonSize.ExtraLarge
            ),
            isPlaying = isPlaying,
            onClick = onPausePlayButtonClick
        )
        Spacer( modifier = Modifier.width( 4.dp ) )
        if ( enableSeekControls ) {
            NowPlayingFastForwardButton(
                style = NowPlayingControlButtonStyle(
                    color = NowPlayingControlButtonColors.Transparent,
                    size = NowPlayingControlButtonSize.Large
                ),
                onClick = onFastForwardButtonClick
            )
            Spacer( modifier = Modifier.width( 4.dp ) )
        }
        NowPlayingSkipNextButton(
            style = NowPlayingControlButtonStyle(
                color = NowPlayingControlButtonColors.Transparent,
                size = NowPlayingControlButtonSize.Large
            ),
            onClick = onNextButtonClick
        )
    }
}

@Composable
fun NowPlayingSeekBar(
    playbackPosition: PlaybackPosition,
    durationFormatter: (Long ) -> String,
    onSeekStart: () -> Unit,
    onSeekEnd: ( Long ) -> Unit
) {

    Row (
        modifier = Modifier.padding( 16.dp, 0.dp ),
        horizontalArrangement = Arrangement.spacedBy( 8.dp ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        var seekRatio by remember { mutableStateOf<Float?>( null ) }

        NowPlayingPlaybackPositionText(
            durationFormatter = durationFormatter,
            duration = seekRatio?.let { it * playbackPosition.total }?.toLong()
                ?: playbackPosition.played,
            alignment = Alignment.CenterStart
        )
        Box(
            modifier = Modifier.weight( 1f )
        ) {
            NowPlayingSeekBar(
                ratio = playbackPosition.ratio,
                onSeekStart = {
                    seekRatio = 0f
                    onSeekStart()
                },
                onSeek = { seekRatio = it },
                onSeekEnd = {
                    onSeekEnd( ( it * playbackPosition.total ).toLong() )
                    seekRatio = null
                },
                onSeekCancel = { seekRatio = null }
            )
        }
        NowPlayingPlaybackPositionText(
            durationFormatter = durationFormatter,
            duration = playbackPosition.total,
            alignment = Alignment.CenterEnd
        )
    }
}

@SuppressLint( "UnusedBoxWithConstraintsScope" )
@Composable
private fun NowPlayingSeekBar(
    ratio: Float,
    onSeekStart: () -> Unit,
    onSeek: ( Float ) -> Unit,
    onSeekEnd: (Float ) -> Unit,
    onSeekCancel: () -> Unit
) {
    val sliderHeight = 12.dp
    val thumbSize = 12.dp
    val thumbSizeHalf = thumbSize.div( 2 )
    val trackHeight = 4.dp

    var dragging by remember { mutableStateOf( false ) }
    var dragRatio by remember { mutableFloatStateOf( 0f ) }

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxWidth()
            .height(sliderHeight),
        contentAlignment = Alignment.Center
    ) {
        val sliderWidth = maxWidth

        Box(
            modifier = Modifier
                .height( sliderHeight )
                .fillMaxWidth()
                .pointerInput( Unit ) {
                    detectTapGestures(
                        onTap = { offset ->
                            onSeekStart()
                            val tapRatio = ( offset.x / sliderWidth.toPx() ).coerceIn( 0f..1f )
                            onSeekEnd( tapRatio )
                        }
                    )
                }
                .pointerInput(Unit) {
                    var offsetX = 0f
                    detectHorizontalDragGestures(
                        onDragStart = { offset ->
                            offsetX = offset.x
                            dragging = true
                            onSeekStart()
                        },
                        onDragEnd = {
                            onSeekEnd( dragRatio )
                            offsetX = 0f
                            dragging = false
                            dragRatio = 0f
                        },
                        onDragCancel = {
                            onSeekCancel()
                            offsetX = 0f
                            dragging = false
                            dragRatio = 0f
                        },
                        onHorizontalDrag = { pointer, dragAmount ->
                            pointer.consume()
                            offsetX += dragAmount
                            dragRatio = ( offsetX / sliderWidth.toPx() ).coerceIn( 0f..1f )
                            onSeek( dragRatio )
                        }
                    )
                }
        )
        Box(
            modifier = Modifier
                .padding( thumbSizeHalf, 0.dp )
                .height( trackHeight )
                .fillMaxWidth()
                .background(
                    MaterialTheme.colorScheme.surfaceVariant,
                    RoundedCornerShape( thumbSizeHalf )
                )
        ) {
            Box(
                modifier = Modifier
                    .height( trackHeight )
                    .fillMaxWidth(if ( dragging ) dragRatio else ratio )
                    .background(
                        MaterialTheme.colorScheme.primary,
                        RoundedCornerShape( thumbSizeHalf )
                    )
            )
        }
        Box( modifier = Modifier.fillMaxWidth() ) {
            Box(
                modifier = Modifier
                    .size( thumbSize )
                    .offset(
                        sliderWidth
                            .minus( thumbSizeHalf.times( 2 ) )
                            .times(if ( dragging ) dragRatio else ratio ),
                        0.dp
                    )
                    .background( MaterialTheme.colorScheme.primary, CircleShape )
            )
        }
    }
}

@Composable
private fun NowPlayingPlaybackPositionText(
    duration: Long,
    durationFormatter: ( Long ) -> String,
    alignment: Alignment
) {
    val textStyle = MaterialTheme.typography.labelMedium
    val durationFormatted = durationFormatter( duration )

    Box( contentAlignment = alignment ) {
        Text(
            text = "0".repeat( durationFormatted.length ),
            style = textStyle.copy( color = Color.Transparent )
        )
        Text(
            text = durationFormatted,
            style = MaterialTheme.typography.labelMedium
        )
    }
}

@Composable
private fun NowPlayingFastForwardButton(
    style: NowPlayingControlButtonStyle,
    onClick: () -> Unit
) {
    NowPlayingControlButton(
        style = style,
        icon = Icons.Rounded.FastForward,
        roundedCornerSizeDp = 30.dp
    ) {
        onClick()
    }
}

@Composable
private fun NowPlayingFastRewindButton(
    style: NowPlayingControlButtonStyle,
    onClick: () -> Unit
) {
    NowPlayingControlButton(
        style = style,
        icon = Icons.Rounded.FastRewind,
        roundedCornerSizeDp = 30.dp
    ) {
        onClick()
    }
}

@Composable
private fun NowPlayingSkipNextButton(
    style: NowPlayingControlButtonStyle,
    onClick: () -> Unit
) {
    NowPlayingControlButton(
        style = style,
        icon = Icons.Rounded.SkipNext,
        roundedCornerSizeDp = 30.dp
    ) {
        onClick()
    }
}

@Composable
private fun NowPlayingSkipPreviousButton(
    style: NowPlayingControlButtonStyle,
    onClick: () -> Unit
) {
    NowPlayingControlButton(
        style = style,
        icon = Icons.Rounded.SkipPrevious,
        roundedCornerSizeDp = 30.dp
    ) {
        onClick()
    }
}

@Composable
private fun NowPlayingPlayPauseButton(
    style: NowPlayingControlButtonStyle,
    isPlaying: Boolean,
    onClick: () -> Unit
) {
    AnimatedContent(
        targetState = isPlaying,
        label = "now-playing-play-pause-button"
    ) {
        NowPlayingControlButton(
            modifier = Modifier.size( 60.dp ),
            style = style,
            icon = if ( it ) Icons.Rounded.Pause else Icons.Rounded.PlayArrow,
            roundedCornerSizeDp = 24.dp
        ) {
            onClick()
        }
    }
}

@Composable
private fun NowPlayingControlButton(
    modifier: Modifier = Modifier,
    style: NowPlayingControlButtonStyle,
    icon: ImageVector,
    roundedCornerSizeDp: Dp,
    onClick: () -> Unit
) {
    val backgroundColor = when ( style.color ) {
        NowPlayingControlButtonColors.Primary -> MaterialTheme.colorScheme.primary
        NowPlayingControlButtonColors.Surface -> MaterialTheme.colorScheme.surfaceVariant
        NowPlayingControlButtonColors.Transparent -> Color.Transparent
    }
    val contentColor = when ( style.color ) {
        NowPlayingControlButtonColors.Primary -> MaterialTheme.colorScheme.onPrimary
        else -> LocalContentColor.current
    }
    val iconSize = when ( style.size ) {
        NowPlayingControlButtonSize.Default -> 24.dp
        NowPlayingControlButtonSize.Large -> 32.dp
        NowPlayingControlButtonSize.ExtraLarge -> 36.dp
    }
    IconButton(
        modifier = modifier
            .background(
                backgroundColor,
                RoundedCornerShape( roundedCornerSizeDp )
            ),
        onClick = onClick
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = contentColor,
            modifier = Modifier.size( iconSize )
        )
    }
}

private data class NowPlayingControlButtonStyle(
    val color: NowPlayingControlButtonColors,
    val size: NowPlayingControlButtonSize = NowPlayingControlButtonSize.Default
)

private enum class NowPlayingControlButtonColors {
    Primary,
    Surface,
    Transparent
}

private enum class NowPlayingControlButtonSize {
    Default,
    Large,
    ExtraLarge,
}


@DevicePreviews
@Composable
fun NowPlayingScreenContentPreview() {
    MusicMattersTheme(
        themeMode = ThemeMode.LIGHT,
        primaryColorName = "Blue",
        fontName = DefaultPreferences.FONT_NAME,
        fontScale = DefaultPreferences.FONT_SCALE,
        useMaterialYou = true
    ) {
        NowPlayingScreenContent(
            uiState = NowPlayingScreenUiState.Success(
                userData = emptyUserData.copy(
                    miniPlayerShowTrackControls = false,
                    controlsLayoutDefault = false,
                    showNowPlayingSeekControls = true,
                    loopMode = LoopMode.Queue
                ),
                queue = listOf(
                    Song(
                        id = "song-id-1",
                        mediaUri = "Uri.EMPTY",
                        title = "Started From the Bottom",
                        displayTitle = "",
                        duration = 0L,
                        artists = setOf( "Drake" ),
                        size = 0L,
                        dateModified = 0L,
                        path = "",
                        trackNumber = null,
                        year = null,
                        albumTitle = null,
                        composer = null,
                        artworkUri = null,
                    )
                ),
                currentlyPlayingSongIsFavorite = true,
                playerState = PlayerState(
                    currentlyPlayingSongId = "song-id-1",
                    isPlaying = false,
                    isBuffering = false,
                ),
                playlists = emptyList(),
                songsAdditionalMetadataList = emptyList(),
            ),
            playbackPosition = PlaybackPosition( 2L, 3L, 5L ),
            durationFormatter = { "05:33" },
            onArtistClicked = {},
            onFavorite = {},
            onPausePlayButtonClick = { /*TODO*/ },
            onPreviousButtonClick = { /*TODO*/ },
            onPlayNext = { /*TODO*/ },
            onFastRewindButtonClick = { /*TODO*/ },
            onFastForwardButtonClick = { /*TODO*/ },
            onSeekEnd = {},
            onArtworkClicked = {},
            onSwipeArtworkLeft = {},
            onSwipeArtworkRight = {},
            onPlayingSpeedChange = {},
            onPlayingPitchChange = {},
            onQueueClicked = {},
            onToggleLoopMode = {},
            onToggleShuffleMode = {},
            onSeekStart = {},
            onCreateEqualizerActivityContract = {
                object : ActivityResultContract<Unit, Unit>() {
                    override fun createIntent(context: Context, input: Unit) = Intent()
                    override fun parseResult( resultCode: Int, intent: Intent? ) {}

                }
            },
            onCreatePlaylist = { _, _ -> },
            onSearchSongsMatchingQuery = { emptyList() },
            onGetSongsInPlaylist = { emptyList() },
            onAddSongsToPlaylist = { _, _ -> },
            onGetPlaylists = { emptyList() },
            onViewAlbum = {},
            onViewArtist = {},
            onHideNowPlayingBottomSheet = {},
            onGetSongAdditionalMetadata = { null }
        )
    }
}






