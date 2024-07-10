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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.odesa.musicMatters.core.data.preferences.LoopMode
import com.odesa.musicMatters.core.data.preferences.impl.SettingsDefaults
import com.odesa.musicMatters.core.datatesting.songs.testSongs
import com.odesa.musicMatters.core.designsystem.theme.MusicMattersTheme
import com.odesa.musicMatters.core.designsystem.theme.isLight
import com.odesa.musicMatters.core.i8n.English
import com.odesa.musicMatters.core.i8n.Language
import com.odesa.musicMatters.core.model.PlaylistInfo
import com.odesa.musicMatters.core.model.Song
import com.odesa.musicMatters.core.model.SongAdditionalMetadataInfo
import com.odesa.musicMatters.ui.components.BottomSheetMenuItem
import com.odesa.musicMatters.ui.components.GenericOptionsBottomSheet
import com.odesa.musicMatters.ui.components.PlaybackPosition
import com.odesa.musicMatters.ui.components.SongDetailsDialog
import com.odesa.musicMatters.ui.components.swipeable
import com.odesa.musicMatters.ui.navigation.FadeTransition
import java.time.Duration


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
        currentlyPlayingSong = uiState.currentlyPlayingSong!!,
        currentlyPlayingSongIndex = uiState.currentlyPlayingSongIndex,
        fallbackResourceId = fallbackResourceId,
        queueSize = uiState.queueSize,
        language = uiState.language,
        isFavorite = uiState.currentlyPlayingSongIsFavorite,
        controlsLayoutIsDefault = uiState.controlsLayoutIsDefault,
        isPlaying = uiState.isPlaying,
        enableSeekControls = uiState.showSeekControls,
        showLyrics = uiState.showLyrics,
        playbackPosition = uiState.playbackPosition,
        shuffle = uiState.shuffle,
        currentLoopMode = uiState.currentLoopMode,
        currentPlayingSpeed = uiState.currentPlayingSpeed,
        currentPlayingPitch = uiState.currentPlayingPitch,
        showSamplingInfo = uiState.showSamplingInfo,
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
        onShowLyrics = {},
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


@OptIn( ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun NowPlayingScreenContent(
    currentlyPlayingSong: Song,
    currentlyPlayingSongIndex: Int,
    fallbackResourceId: Int,
    queueSize: Int,
    language: Language,
    isFavorite: Boolean,
    controlsLayoutIsDefault: Boolean,
    isPlaying: Boolean,
    enableSeekControls: Boolean,
    showLyrics: Boolean,
    playbackPosition: PlaybackPosition,
    shuffle: Boolean,
    currentLoopMode: LoopMode,
    currentPlayingSpeed: Float,
    currentPlayingPitch: Float,
    showSamplingInfo: Boolean,
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
    onShowLyrics: () -> Unit,
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

    var showOptionsMenu by remember { mutableStateOf( false ) }
    var showSongDetailsDialog by remember { mutableStateOf( false ) }

    Column (
        modifier = Modifier
            .padding( start = 8.dp, end = 8.dp, top = 0.dp, bottom = 0.dp )
    ) {
        NowPlayingArtwork(
            showLyrics = showLyrics,
            artworkUri = currentlyPlayingSong.artworkUri,
            fallbackResourceId = fallbackResourceId,
            onSwipeLeft = onSwipeArtworkLeft,
            onSwipeRight = onSwipeArtworkRight,
            onArtworkClicked = onArtworkClicked
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
                    if ( showSamplingInfo ) {
//                        target.toSamplingInfoString( language )?.let {
//                            val localContentColor = LocalContentColor.current
//                            Text(
//                                text = it,
//                                style = MaterialTheme.typography.labelSmall
//                                    .copy( color = localContentColor.copy( alpha = 0.7f ) ),
//                                modifier = Modifier.padding( top = 4.dp )
//                            )
//                        }
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
                        targetState = isFavorite,
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
                    onClick = {
                        showOptionsMenu = !showOptionsMenu
                    }
                ) {
                    Icon(
                        imageVector = Icons.Filled.MoreVert,
                        contentDescription = null
                    )
                    if ( showOptionsMenu ) {
                        ModalBottomSheet(
                            onDismissRequest = {
                                showOptionsMenu = false
                            }
                        ) {
                            GenericOptionsBottomSheet(
                                headerImage = ImageRequest.Builder( LocalContext.current ).apply {
                                    data( currentlyPlayingSong.artworkUri )
                                    placeholder( fallbackResourceId )
                                    fallback( fallbackResourceId )
                                    error( fallbackResourceId )
                                    crossfade( true )
                                }.build(),
                                headerTitle = currentlyPlayingSong.title,
                                titleIsHighlighted = true,
                                headerDescription = currentlyPlayingSong.artists.joinToString(),
                                language = language,
                                fallbackResourceId = fallbackResourceId,
                                onDismissRequest = { showOptionsMenu = false },
                                onShufflePlay = { onSeekEnd( Duration.ZERO.toMillis() ) }, // Just seek to the beginning
                                onPlayNext = {}, // No need to do anything as duplicates are not allowed in queue
                                onAddToQueue = {}, // No need to do anything as duplicates are not allowed in queue
                                onGetPlaylists = onGetPlaylists,
                                onGetSongsInPlaylist = onGetSongsInPlaylist,
                                onSearchSongsMatchingQuery = onSearchSongsMatchingQuery,
                                onCreatePlaylist = onCreatePlaylist,
                                onAddSongsToPlaylist = onAddSongsToPlaylist,
                                onGetSongs = { listOf( currentlyPlayingSong ) },
                                additionalBottomSheetMenuItems = { onDismissRequest ->
                                    currentlyPlayingSong.albumTitle?.let { albumTitle ->
                                        BottomSheetMenuItem(
                                            leadingIcon = Icons.Default.Album,
                                            label = "${language.viewAlbum}: $albumTitle"
                                        ) {
                                            onDismissRequest()
                                            onHideNowPlayingBottomSheet()
                                            onViewAlbum( albumTitle )
                                        }
                                    }
                                    currentlyPlayingSong.artists.forEach { artistName ->
                                        BottomSheetMenuItem(
                                            leadingIcon = Icons.Default.Person,
                                            label = "${language.viewArtist}: $artistName"
                                        ) {
                                            onDismissRequest()
                                            onHideNowPlayingBottomSheet()
                                            onViewArtist( artistName )
                                        }
                                    }
                                    BottomSheetMenuItem(
                                        leadingIcon = Icons.Default.Info,
                                        label = language.details
                                    ) {
                                        onDismissRequest()
                                        showSongDetailsDialog = true
                                    }
                                }
                            )
                        }
                    }
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
            controlsLayoutIsDefault ->
                NowPlayingDefaultControlsLayout(
                    isPlaying = isPlaying,
                    enableSeekControls = enableSeekControls,
                    onPausePlayButtonClick = onPausePlayButtonClick,
                    onPreviousButtonClick = onPreviousButtonClick,
                    onFastRewindButtonClick = onFastRewindButtonClick,
                    onFastForwardButtonClick = onFastForwardButtonClick,
                    onNextButtonClick = onPlayNext
                )
            else ->
                NowPlayingTraditionalControlsLayout(
                    enableSeekControls = enableSeekControls,
                    isPlaying = isPlaying,
                    onPreviousButtonClick = onPreviousButtonClick,
                    onFastRewindButtonClick = onFastRewindButtonClick,
                    onPausePlayButtonClick = onPausePlayButtonClick,
                    onFastForwardButtonClick = onFastForwardButtonClick,
                    onNextButtonClick = onPlayNext
                )
        }
        Spacer( modifier = Modifier.height( 16.dp ) )
        NowPlayingBodyBottomBar(
            language = language,
            currentSongIndex = currentlyPlayingSongIndex,
            queueSize = queueSize,
            showLyrics = showLyrics,
            currentLoopMode = currentLoopMode,
            shuffle = shuffle,
            currentSpeed = currentPlayingSpeed,
            currentPitch = currentPlayingPitch,
            onQueueClicked = onQueueClicked,
            onShowLyrics = onShowLyrics,
            onToggleLoopMode = onToggleLoopMode,
            onToggleShuffleMode = onToggleShuffleMode,
            onSpeedChange = onPlayingSpeedChange,
            onPitchChange = onPlayingPitchChange,
            onCreateEqualizerActivityContract = onCreateEqualizerActivityContract
        )

        if ( showSongDetailsDialog ) {
            SongDetailsDialog(
                song = currentlyPlayingSong,
                language = language,
                durationFormatter = { it.formatMilliseconds() },
                isLoadingSongAdditionalMetadata = false,
                onGetSongAdditionalMetadata = onGetSongAdditionalMetadata
            ) {
                showSongDetailsDialog = false
            }
        }
    }
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
            currentlyPlayingSong = testSongs.first(),
            language = English,
            isFavorite = true,
            controlsLayoutIsDefault = true,
            isPlaying = true,
            enableSeekControls = true,
            showLyrics = false,
            fallbackResourceId = R.drawable.placeholder_light,
            playbackPosition = PlaybackPosition( 3, 5 ),
            currentLoopMode = LoopMode.Song,
            currentPlayingSpeed = 2f,
            currentPlayingPitch = 2f,
            currentlyPlayingSongIndex = 20,
            queueSize = 100,
            shuffle = true,
            showSamplingInfo = false,
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
            onShowLyrics = {},
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
    showLyrics: Boolean,
    artworkUri: Uri?,
    @DrawableRes fallbackResourceId: Int,
    onSwipeLeft: () -> Unit,
    onSwipeRight: () -> Unit,
    onArtworkClicked: () -> Unit
) {
    Box(
        modifier = Modifier
            .padding(16.dp, 0.dp)
            .fillMaxWidth()

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
        icon = Icons.Filled.FastForward,
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
        icon = Icons.Filled.FastRewind,
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
        icon = Icons.Filled.SkipNext,
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
        icon = Icons.Filled.SkipPrevious,
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
            icon = if ( it ) Icons.Filled.Pause else Icons.Filled.PlayArrow,
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
