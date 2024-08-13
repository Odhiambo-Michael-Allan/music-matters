package com.odesa.musicMatters.ui.nowPlaying

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.activity.result.contract.ActivityResultContract
import androidx.annotation.DrawableRes
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Album
import androidx.compose.material.icons.filled.FastForward
import androidx.compose.material.icons.filled.FastRewind
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material.icons.rounded.FastForward
import androidx.compose.material.icons.rounded.FastRewind
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.SkipNext
import androidx.compose.material.icons.rounded.SkipPrevious
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
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
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.odesa.musicMatters.R
import com.odesa.musicMatters.core.common.media.extensions.formatMilliseconds
import com.odesa.musicMatters.core.data.preferences.impl.SettingsDefaults
import com.odesa.musicMatters.core.designsystem.theme.MusicMattersTheme
import com.odesa.musicMatters.core.designsystem.theme.isLight
import com.odesa.musicMatters.core.model.PlaylistInfo
import com.odesa.musicMatters.core.model.Song
import com.odesa.musicMatters.core.model.SongAdditionalMetadataInfo
import com.odesa.musicMatters.ui.components.BottomSheetMenuItem
import com.odesa.musicMatters.ui.components.GenericOptionsBottomSheet
import com.odesa.musicMatters.ui.components.PlaybackPosition
import com.odesa.musicMatters.ui.components.SongDetailsDialog
import com.odesa.musicMatters.ui.components.swipeable
import com.odesa.musicMatters.ui.navigation.FadeTransition
import com.odesa.musicMatters.utils.ScreenOrientation


// Stateful
@Composable
fun NowPlayingBottomSheet(
    viewModel: NowPlayingViewModel,
    onViewAlbum: (String) -> Unit,
    onViewArtist: (String) -> Unit,
    onNavigateToQueueScreen: () -> Unit,
    onLaunchEqualizerActivity: () -> Unit,
    onHideBottomSheet: () -> Unit
) {

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    NowPlayingBottomSheetContent(
        uiState = uiState,
        onFavorite = viewModel::addToFavorites,
        playPause = viewModel::playPause,
        playPreviousSong = viewModel::playPreviousSong,
        playNextSong = viewModel::playNextSong,
        fastRewind = viewModel::fastRewind,
        fastForward = viewModel::fastForward,
        onSeekStart = viewModel::onSeekStarted,
        onSeekEnd = viewModel::onSeekEnd,
        onArtworkClicked = {
            onHideBottomSheet()
            onViewAlbum( it )
        },
        onArtistClicked = {
            onHideBottomSheet()
            onViewArtist( it )
        },
        toggleLoopMode = viewModel::toggleLoopMode,
        toggleShuffleMode = viewModel::toggleShuffleMode,
        onPlayingSpeedChange = viewModel::onPlayingSpeedChange,
        onPlayingPitchChange = viewModel::onPlayingPitchChange,
        onQueueClicked = {
            onHideBottomSheet()
            onNavigateToQueueScreen()
        },
        onCreateEqualizerActivityContract = onLaunchEqualizerActivity,
        onGetSongsInPlaylist = viewModel::getSongsInPlaylist,
        onSearchSongsMatchingQuery = viewModel::searchSongsMatching,
        onCreatePlaylist = viewModel::createPlaylist,
        onAddSongsToPlaylist = viewModel::addSongsToPlaylist,
        onViewAlbum = onViewAlbum,
        onViewArtist = onViewArtist,
        onHideBottomSheet = onHideBottomSheet
    )
}

// Stateless
@Composable
fun NowPlayingBottomSheetContent(
    uiState: NowPlayingScreenUiState,
    onFavorite: ( String ) -> Unit,
    playPause: () -> Unit,
    playPreviousSong: () -> Unit,
    playNextSong: () -> Unit,
    fastRewind: () -> Unit,
    fastForward: () -> Unit,
    onSeekStart: () -> Unit,
    onSeekEnd: ( Long ) -> Unit,
    onArtworkClicked: ( String ) -> Unit,
    onArtistClicked: ( String ) -> Unit,
    toggleLoopMode: () -> Unit,
    toggleShuffleMode: () -> Unit,
    onPlayingSpeedChange: ( Float ) -> Unit,
    onPlayingPitchChange: ( Float ) -> Unit,
    onQueueClicked: () -> Unit,
    onCreateEqualizerActivityContract: () -> Unit,
    onGetSongsInPlaylist: (PlaylistInfo ) -> List<Song>,
    onSearchSongsMatchingQuery: ( String ) -> List<Song>,
    onCreatePlaylist: ( String, List<Song> ) -> Unit,
    onAddSongsToPlaylist: (PlaylistInfo, List<Song> ) -> Unit,
    onViewAlbum: ( String ) -> Unit,
    onViewArtist: ( String ) -> Unit,
    onHideBottomSheet: () -> Unit,
) {

    val fallbackResourceId = if ( uiState.themeMode.isLight(
            LocalContext.current ) ) R.drawable.placeholder_light else
                R.drawable.placeholder_dark

    NowPlayingScreenContent(
        uiState = uiState,
        fallbackResourceId = fallbackResourceId,
        durationFormatter = Long::formatMilliseconds,
        onArtistClicked = onArtistClicked,
        onFavorite = { onFavorite( it ) },
        onPausePlayButtonClick = playPause,
        onPreviousButtonClick = playPreviousSong,
        onPlayNext = playNextSong,
        onFastRewindButtonClick = fastRewind,
        onFastForwardButtonClick = fastForward,
        onSeekStart = onSeekStart,
        onSeekEnd = { onSeekEnd( it ) },
        onArtworkClicked = { onArtworkClicked( uiState.currentlyPlayingSong.albumTitle!! ) },
        onSwipeArtworkLeft = playPreviousSong,
        onSwipeArtworkRight = playNextSong,
        onQueueClicked = onQueueClicked,
        onToggleLoopMode = toggleLoopMode,
        onToggleShuffleMode = toggleShuffleMode,
        onPlayingSpeedChange = onPlayingSpeedChange,
        onPlayingPitchChange = onPlayingPitchChange,
        onCreateEqualizerActivityContract = onCreateEqualizerActivityContract,
        onGetSongsInPlaylist = onGetSongsInPlaylist,
        onSearchSongsMatchingQuery = onSearchSongsMatchingQuery,
        onCreatePlaylist = onCreatePlaylist,
        onAddSongsToPlaylist = onAddSongsToPlaylist,
        onGetPlaylists = { uiState.playlistInfos },
        onViewAlbum = onViewAlbum,
        onViewArtist = onViewArtist,
        onHideNowPlayingBottomSheet = onHideBottomSheet,
        onGetSongAdditionalMetadata = {
            uiState.songsAdditionalMetadataList.find { it.id == uiState.currentlyPlayingSong.id }
        }
    )
}


@OptIn( ExperimentalMaterial3Api::class )
@Composable
fun NowPlayingScreenContent(
    uiState: NowPlayingScreenUiState,
    fallbackResourceId: Int,
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
    onArtworkClicked: () -> Unit,
    onSwipeArtworkLeft: () -> Unit,
    onSwipeArtworkRight: () -> Unit,
    onQueueClicked: () -> Unit,
    onToggleLoopMode: () -> Unit,
    onToggleShuffleMode: () -> Unit,
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
    val screenOrientation = ScreenOrientation.fromConfiguration( LocalConfiguration.current )
    var showOptionsMenu by remember { mutableStateOf( false ) }
    var showSongDetailsDialog by remember { mutableStateOf( false ) }

    if ( screenOrientation == ScreenOrientation.POTRAIT ) {
        NowPlayingScreenContentPortrait(
            uiState = uiState,
            fallbackResourceId = fallbackResourceId,
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
        NowPlayingScreenLandscape(
            uiState = uiState,
            fallbackResourceId = fallbackResourceId,
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

    if ( showOptionsMenu ) {
        ModalBottomSheet(
            modifier = if ( screenOrientation.isLandscape )
                Modifier.padding( start = 50.dp ) else Modifier,
            onDismissRequest = {
                showOptionsMenu = false
            }
        ) {
            GenericOptionsBottomSheet(
                headerImage = ImageRequest.Builder( LocalContext.current ).apply {
                    data( uiState.currentlyPlayingSong.artworkUri )
                    placeholder( fallbackResourceId )
                    fallback( fallbackResourceId )
                    error( fallbackResourceId )
                    crossfade( true )
                }.build(),
                headerTitle = uiState.currentlyPlayingSong.title,
                titleIsHighlighted = true,
                headerDescription = uiState.currentlyPlayingSong.artists.joinToString(),
                language = uiState.language,
                fallbackResourceId = fallbackResourceId,
                onDismissRequest = { showOptionsMenu = false },
                onPlayNext = {}, // No need to do anything as duplicates are not allowed in queue
                onAddToQueue = {}, // No need to do anything as duplicates are not allowed in queue
                onGetPlaylists = onGetPlaylists,
                onGetSongsInPlaylist = onGetSongsInPlaylist,
                onSearchSongsMatchingQuery = onSearchSongsMatchingQuery,
                onCreatePlaylist = onCreatePlaylist,
                onAddSongsToPlaylist = onAddSongsToPlaylist,
                onGetSongs = { listOf( uiState.currentlyPlayingSong ) },
                leadingBottomSheetMenuItem = { onDismissRequest ->
                    BottomSheetMenuItem(
                        leadingIcon = if ( uiState.currentlyPlayingSongIsFavorite ) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                        label = uiState.language.favorite,
                        leadingIconTint = MaterialTheme.colorScheme.primary
                    ) {
                        onDismissRequest()
                        onFavorite( uiState.currentlyPlayingSong.id )
                    }
                },
                trailingBottomSheetMenuItems = { onDismissRequest ->
                    uiState.currentlyPlayingSong.albumTitle?.let { albumTitle ->
                        BottomSheetMenuItem(
                            leadingIcon = Icons.Default.Album,
                            label = "${uiState.language.viewAlbum}: $albumTitle"
                        ) {
                            onDismissRequest()
                            onHideNowPlayingBottomSheet()
                            onViewAlbum( albumTitle )
                        }
                    }
                    uiState.currentlyPlayingSong.artists.forEach { artistName ->
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
            song = uiState.currentlyPlayingSong,
            language = uiState.language,
            durationFormatter = { it.formatMilliseconds() },
            isLoadingSongAdditionalMetadata = false,
            onGetSongAdditionalMetadata = onGetSongAdditionalMetadata
        ) {
            showSongDetailsDialog = false
        }
    }

}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun NowPlayingScreenContentPortrait(
    uiState: NowPlayingScreenUiState,
    @DrawableRes fallbackResourceId: Int,
    durationFormatter: ( Long ) -> String,
    onFavorite: ( String ) -> Unit,
    onSwipeArtworkLeft: () -> Unit,
    onSwipeArtworkRight: () -> Unit,
    onArtworkClicked: () -> Unit,
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
    onToggleLoopMode: () -> Unit,
    onToggleShuffleMode: () -> Unit,
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
            showLyrics = uiState.showLyrics,
            artworkUri = uiState.currentlyPlayingSong.artworkUri,
            fallbackResourceId = fallbackResourceId,
            onSwipeLeft = onSwipeArtworkLeft,
            onSwipeRight = onSwipeArtworkRight,
            onArtworkClicked = onArtworkClicked
        )
        Row {
            AnimatedContent(
                modifier = Modifier.weight( 1f ),
                label = "now-playing-body-content",
                targetState = uiState.currentlyPlayingSong,
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
                        maxLines = 2,
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
                    if ( uiState.showSamplingInfo ) {
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
                    onClick = { onFavorite( uiState.currentlyPlayingSong.id ) }
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
            playbackPosition = uiState.playbackPosition,
            durationFormatter = durationFormatter,
            onSeekStart = onSeekStart,
            onSeekEnd = onSeekEnd
        )
        Spacer( modifier = Modifier.height( 24.dp ) )
        when {
            uiState.controlsLayoutIsDefault ->
                NowPlayingDefaultControlsLayout(
                    isPlaying = uiState.isPlaying,
                    enableSeekControls = uiState.nowPlayingShowSeekControls,
                    onPausePlayButtonClick = onPausePlayButtonClick,
                    onPreviousButtonClick = onPreviousButtonClick,
                    onFastRewindButtonClick = onFastRewindButtonClick,
                    onFastForwardButtonClick = onFastForwardButtonClick,
                    onNextButtonClick = onPlayNext
                )
            else ->
                NowPlayingTraditionalControlsLayout(
                    enableSeekControls = uiState.nowPlayingShowSeekControls,
                    isPlaying = uiState.isPlaying,
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
            currentSongIndex = uiState.currentlyPlayingSongIndex,
            queueSize = uiState.queueSize,
            currentLoopMode = uiState.currentLoopMode,
            shuffle = uiState.shuffle,
            currentSpeed = uiState.currentPlayingSpeed,
            currentPitch = uiState.currentPlayingPitch,
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

@Preview( showSystemUi = true )
@Composable
fun NowPlayingScreenPortraitPreview() {
    NowPlayingScreenContentPortrait(
        uiState = testNowPlayingScreenUiState,
        fallbackResourceId = R.drawable.placeholder_light,
        durationFormatter = { "01:00" },
        onFavorite = {},
        onSwipeArtworkLeft = { /*TODO*/ },
        onSwipeArtworkRight = { /*TODO*/ },
        onArtworkClicked = { /*TODO*/ },
        onArtistClicked = {},
        onShowOptionsMenu = { /*TODO*/ },
        onSeekStart = { /*TODO*/ },
        onSeekEnd = {},
        onPausePlayButtonClick = { /*TODO*/ },
        onPreviousButtonClick = { /*TODO*/ },
        onPlayNext = { /*TODO*/ },
        onFastRewindButtonClick = { /*TODO*/ },
        onFastForwardButtonClick = { /*TODO*/ },
        onQueueClicked = { /*TODO*/ },
        onToggleLoopMode = { /*TODO*/ },
        onToggleShuffleMode = { /*TODO*/ },
        onPlayingSpeedChange = {},
        onPlayingPitchChange = {},
        onGetSongAdditionalMetadata = { null }
    ) {}
}

@OptIn( ExperimentalLayoutApi::class )
@Composable
fun NowPlayingScreenLandscape(
    uiState: NowPlayingScreenUiState,
    @DrawableRes fallbackResourceId: Int,
    durationFormatter: ( Long ) -> String,
    onFavorite: ( String ) -> Unit,
    onSwipeArtworkLeft: () -> Unit,
    onSwipeArtworkRight: () -> Unit,
    onArtworkClicked: () -> Unit,
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
    onToggleLoopMode: () -> Unit,
    onToggleShuffleMode: () -> Unit,
    onPlayingSpeedChange: ( Float ) -> Unit,
    onPlayingPitchChange: ( Float ) -> Unit,
    onCreateEqualizerActivityContract: () -> Unit,
    onGetSongAdditionalMetadata: () -> SongAdditionalMetadataInfo?
) {
    Row (
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        NowPlayingArtwork(
            modifier = Modifier
                .padding( 16.dp, 16.dp ),
            showLyrics = uiState.showLyrics,
            artworkUri = uiState.currentlyPlayingSong.artworkUri,
            fallbackResourceId = fallbackResourceId,
            onSwipeLeft = onSwipeArtworkLeft,
            onSwipeRight = onSwipeArtworkRight,
            onArtworkClicked = onArtworkClicked
        )
        Column {
            Row {
                AnimatedContent(
                    modifier = Modifier.weight( 1f ),
                    label = "now-playing-body-content",
                    targetState = uiState.currentlyPlayingSong,
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
                            maxLines = 2,
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
                    if ( uiState.showSamplingInfo ) {
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
                        onClick = { onFavorite( uiState.currentlyPlayingSong.id ) }
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
                playbackPosition = uiState.playbackPosition,
                durationFormatter = durationFormatter,
                onSeekStart = onSeekStart,
                onSeekEnd = onSeekEnd
            )
            Spacer( modifier = Modifier.height( 24.dp ) )
            when {
                uiState.controlsLayoutIsDefault ->
                    NowPlayingDefaultControlsLayout(
                        isPlaying = uiState.isPlaying,
                        enableSeekControls = uiState.nowPlayingShowSeekControls,
                        onPausePlayButtonClick = onPausePlayButtonClick,
                        onPreviousButtonClick = onPreviousButtonClick,
                        onFastRewindButtonClick = onFastRewindButtonClick,
                        onFastForwardButtonClick = onFastForwardButtonClick,
                        onNextButtonClick = onPlayNext
                    )
                else ->
                    NowPlayingTraditionalControlsLayout(
                        enableSeekControls = uiState.nowPlayingShowSeekControls,
                        isPlaying = uiState.isPlaying,
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
                currentSongIndex = uiState.currentlyPlayingSongIndex,
                queueSize = uiState.queueSize,
                currentLoopMode = uiState.currentLoopMode,
                shuffle = uiState.shuffle,
                currentSpeed = uiState.currentPlayingSpeed,
                currentPitch = uiState.currentPlayingPitch,
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

@Preview(
    showBackground = true,
    widthDp = 640,
    heightDp = 360
)
@Composable
fun NowPlayingScreenLandscapePreview() {
    NowPlayingScreenLandscape(
        uiState = testNowPlayingScreenUiState,
        fallbackResourceId = R.drawable.placeholder_light,
        durationFormatter = { "01:00" },
        onFavorite = {},
        onSwipeArtworkLeft = { /*TODO*/ },
        onSwipeArtworkRight = { /*TODO*/ },
        onArtworkClicked = { /*TODO*/ },
        onArtistClicked = {},
        onShowOptionsMenu = { /*TODO*/ },
        onSeekStart = { /*TODO*/ },
        onSeekEnd = {},
        onPausePlayButtonClick = { /*TODO*/ },
        onPreviousButtonClick = { /*TODO*/ },
        onPlayNext = { /*TODO*/ },
        onFastRewindButtonClick = { /*TODO*/ },
        onFastForwardButtonClick = { /*TODO*/ },
        onQueueClicked = { /*TODO*/ },
        onToggleLoopMode = { /*TODO*/ },
        onToggleShuffleMode = { /*TODO*/ },
        onPlayingSpeedChange = {},
        onPlayingPitchChange = {},
        onGetSongAdditionalMetadata = { null },
        onCreateEqualizerActivityContract = {}
    )
}

@Preview( showSystemUi = true )
@Composable
fun NowPlayingScreenContentPreview() {
    MusicMattersTheme(
        themeMode = SettingsDefaults.themeMode,
        primaryColorName = SettingsDefaults.PRIMARY_COLOR_NAME,
        fontName = SettingsDefaults.font.name,
        fontScale = SettingsDefaults.FONT_SCALE,
        useMaterialYou = true
    ) {
        NowPlayingScreenContent(
            uiState = testNowPlayingScreenUiState,
            fallbackResourceId = R.drawable.placeholder_light,
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

@Composable
fun NowPlayingArtwork(
    modifier: Modifier,
    showLyrics: Boolean,
    artworkUri: Uri?,
    @DrawableRes fallbackResourceId: Int,
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
            AsyncImage(
                model = ImageRequest.Builder( LocalContext.current ).apply {
                    data( it )
                    placeholder( fallbackResourceId )
                    fallback( fallbackResourceId )
                    error( fallbackResourceId )
                    crossfade( true )
                }.build(),
                null,
                contentScale = ContentScale.Crop,
                filterQuality = FilterQuality.High,
                modifier = Modifier
                    .size(250.dp)
                    .clip(RoundedCornerShape(12.dp))
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

@Preview( showBackground = true )
@Composable
fun NowPlayingArtworkPreview() {
    NowPlayingArtwork(
        modifier = Modifier
            .padding(16.dp, 0.dp)
            .fillMaxWidth(),
        showLyrics = false,
        artworkUri = Uri.EMPTY,
        fallbackResourceId = R.drawable.placeholder_light,
        onSwipeLeft = {},
        onSwipeRight = {},
        onArtworkClicked = {}
    )
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

@Preview( showBackground = true )
@Composable
fun NowPlayingDefaultControlsLayoutPreview() {
    Box(
        modifier = Modifier.padding( 16.dp ),
        contentAlignment = Alignment.Center
    ) {
        NowPlayingDefaultControlsLayout(
            isPlaying = true,
            enableSeekControls = true,
            onPausePlayButtonClick = {},
            onPreviousButtonClick = {},
            onFastRewindButtonClick = {},
            onFastForwardButtonClick = {}
        ) {}
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
        horizontalArrangement = Arrangement.SpaceAround
    ) {
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
        }
        NowPlayingPlayPauseButton(
            style = NowPlayingControlButtonStyle(
                color = NowPlayingControlButtonColors.Primary,
                size = NowPlayingControlButtonSize.Large
            ),
            isPlaying = isPlaying,
            onClick = onPausePlayButtonClick
        )
        if ( enableSeekControls ) {
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

@Preview( showBackground = true )
@Composable
fun NowPlayingTraditionalControlsPreview() {
    Box(
        modifier = Modifier.padding( 16.dp ),
        contentAlignment = Alignment.Center
    ) {
        NowPlayingTraditionalControlsLayout(
            enableSeekControls = true,
            isPlaying = true,
            onPreviousButtonClick = {},
            onFastRewindButtonClick = {},
            onPausePlayButtonClick = {},
            onFastForwardButtonClick = {}
        ) {}
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

@Preview( showBackground = true )
@Composable
fun NowPlayingSeekBarPreview() {
    Box(
        modifier = Modifier.padding( 16.dp ),
        contentAlignment = Alignment.Center
    ) {
        NowPlayingSeekBar(
            playbackPosition = PlaybackPosition( 3, 5 ),
            durationFormatter = { "02:45" },
            onSeekStart = {},
            onSeekEnd = {}
        )
    }
}

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
                .height(sliderHeight)
                .fillMaxWidth()
                .pointerInput(Unit) {
                    detectTapGestures(
                        onTap = { offset ->
                            onSeekStart()
                            val tapRatio = (offset.x / sliderWidth.toPx()).coerceIn(0f..1f)
                            onSeekEnd(tapRatio)
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
                            onSeekEnd(dragRatio)
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
                            dragRatio = (offsetX / sliderWidth.toPx()).coerceIn(0f..1f)
                            onSeek(dragRatio)
                        }
                    )
                }
        )
        Box(
            modifier = Modifier
                .padding(thumbSizeHalf, 0.dp)
                .height(trackHeight)
                .fillMaxWidth()
                .background(
                    MaterialTheme.colorScheme.surfaceVariant,
                    RoundedCornerShape(thumbSizeHalf)
                )
        ) {
            Box(
                modifier = Modifier
                    .height(trackHeight)
                    .fillMaxWidth(if (dragging) dragRatio else ratio)
                    .background(
                        MaterialTheme.colorScheme.primary,
                        RoundedCornerShape(thumbSizeHalf)
                    )
            )
        }
        Box( modifier = Modifier.fillMaxWidth() ) {
            Box(
                modifier = Modifier
                    .size(thumbSize)
                    .offset(
                        sliderWidth
                            .minus(thumbSizeHalf.times(2))
                            .times(if (dragging) dragRatio else ratio),
                        0.dp
                    )
                    .background(MaterialTheme.colorScheme.primary, CircleShape)
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

@Preview( showBackground = true )
@Composable
fun NowPlayingPlaybackPositionTextPreview() {
    NowPlayingPlaybackPositionText(
        durationFormatter = { "04:00" },
        duration = 40000,
        alignment = Alignment.Center
    )
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
            style = style,
            icon = if ( it ) Icons.Rounded.Pause else Icons.Rounded.PlayArrow,
            roundedCornerSizeDp = 24.dp
        ) {
            onClick()
        }
    }
}

@Preview( showBackground = true )
@Composable
fun NowPlayingControlButtonsPreview() {
    Row(
        modifier = Modifier.padding( 16.dp )
    ) {
        NowPlayingSkipPreviousButton(
            style = NowPlayingControlButtonStyle(
                color = NowPlayingControlButtonColors.Surface
            ),
            onClick = {}
        )
        NowPlayingFastRewindButton(
            style = NowPlayingControlButtonStyle(
                color = NowPlayingControlButtonColors.Surface
            )
        ) {}
        NowPlayingPlayPauseButton(
            style = NowPlayingControlButtonStyle(
                color = NowPlayingControlButtonColors.Surface
            ),
            isPlaying = true,
            onClick = {}
        )
        NowPlayingFastForwardButton(
            style = NowPlayingControlButtonStyle(
                color = NowPlayingControlButtonColors.Surface
            )
        ) {}
        NowPlayingSkipNextButton(
            style = NowPlayingControlButtonStyle(
                color = NowPlayingControlButtonColors.Surface
            )
        ) {}
    }
}

@Composable
private fun NowPlayingControlButton(
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
    }
    IconButton(
        modifier = Modifier.background( backgroundColor, RoundedCornerShape( roundedCornerSizeDp ) ),
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
}


