package com.squad.musicmatters.feature.nowplaying

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import androidx.activity.result.contract.ActivityResultContract
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.exclude
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Album
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.window.core.layout.WindowWidthSizeClass
import com.squad.musicmatters.core.datastore.DefaultPreferences
import com.squad.musicmatters.core.designsystem.component.DevicePreviews
import com.squad.musicmatters.core.designsystem.component.MusicMattersIcons
import com.squad.musicmatters.core.media.media.extensions.formatMilliseconds
import com.squad.musicmatters.core.designsystem.theme.MusicMattersTheme
import com.squad.musicmatters.core.media.connection.PlaybackPosition
import com.squad.musicmatters.core.media.connection.PlayerState
import com.squad.musicmatters.core.media.connection.SleepTimer
import com.squad.musicmatters.core.model.LoopMode
import com.squad.musicmatters.core.model.Playlist
import com.squad.musicmatters.core.model.Song
import com.squad.musicmatters.core.model.SongAdditionalMetadata
import com.squad.musicmatters.core.model.ThemeMode
import com.squad.musicmatters.core.ui.BottomSheetMenuItem
import com.squad.musicmatters.core.ui.GenericOptionsBottomSheet
import com.squad.musicmatters.core.ui.dialog.SongDetailsDialog
import com.squad.musicmatters.feature.nowplaying.components.LandscapeLayout
import com.squad.musicmatters.feature.nowplaying.components.PortraitLayout
import com.squad.musicmatters.feature.nowplaying.components.emptyUserData
import kotlinx.coroutines.launch
import java.util.Locale
import java.util.Timer
import kotlin.time.Clock
import kotlin.time.Duration
import kotlin.time.DurationUnit
import kotlin.time.ExperimentalTime
import kotlin.time.toDuration


// Stateful
@Composable
fun NowPlayingBottomScreen(
    viewModel: NowPlayingScreenViewModel = hiltViewModel(),
    onViewAlbum: ( String ) -> Unit,
    onViewArtist: ( String ) -> Unit,
    onNavigateToQueue: () -> Unit,
    onLaunchEqualizerActivity: () -> Unit,
    onHideBottomSheet: () -> Unit
) {

    val snackBarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val lyricsUiState by viewModel.lyricsUiState.collectAsStateWithLifecycle()
    val playbackPosition by viewModel.playbackPosition.collectAsStateWithLifecycle()

    Scaffold(
        snackbarHost = {
            SnackbarHost(
                snackBarHostState,
                modifier = Modifier.windowInsetsPadding(
                    WindowInsets.safeDrawing.exclude(
                        WindowInsets.ime,
                    ),
                ),
            )
        }
    ) { innerPadding ->
        NowPlayingScreenContent(
            modifier = Modifier.consumeWindowInsets( innerPadding ),
            uiState = uiState,
            lyricsUiState = lyricsUiState,
            playbackPosition = playbackPosition,
            onFavorite = viewModel::addToFavorites,
            onPausePlayButtonClick = viewModel::playPause,
            onPreviousButtonClick = viewModel::playPreviousSong,
            onPlayNext = viewModel::playNextSong,
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
            onNavigateToQueue = {
                onHideBottomSheet()
                onNavigateToQueue()
            },
            onCreateEqualizerActivityContract = onLaunchEqualizerActivity,
            durationFormatter = { it.formatMilliseconds() },
            onCreatePlaylist = viewModel::createPlaylist,
            onAddSongsToPlaylist = viewModel::addSongsToPlaylist,
            onViewAlbum = onViewAlbum,
            onViewArtist = onViewArtist,
            onHideNowPlayingBottomSheet = onHideBottomSheet,
            onSwipeArtworkLeft = viewModel::playNextSong,
            onSwipeArtworkRight = viewModel::playPreviousSong,
            onStartSleepTimer = viewModel::startSleepTimer,
            onStopSleepTimer = viewModel::stopSleepTimer,
            onShowSnackBar = {
                coroutineScope.launch {
                    snackBarHostState.showSnackbar(
                        message = it,
                        duration = SnackbarDuration.Short
                    )
                }
            }
        )
    }
}


@OptIn( ExperimentalMaterial3Api::class )
@Composable
private fun NowPlayingScreenContent(
    modifier: Modifier = Modifier,
    uiState: NowPlayingScreenUiState,
    lyricsUiState: LyricsUiState,
    playbackPosition: PlaybackPosition,
    durationFormatter: ( Long ) -> String,
    onArtistClicked: ( String ) -> Unit,
    onFavorite: ( Song, Boolean ) -> Unit,
    onPausePlayButtonClick: () -> Unit,
    onPreviousButtonClick: () -> Unit,
    onPlayNext: () -> Unit,
    onSeekStart: () -> Unit,
    onSeekEnd: ( Long ) -> Unit,
    onArtworkClicked: ( Song ) -> Unit,
    onSwipeArtworkLeft: () -> Unit,
    onSwipeArtworkRight: () -> Unit,
    onNavigateToQueue: () -> Unit,
    onToggleLoopMode: ( LoopMode ) -> Unit,
    onToggleShuffleMode: ( Boolean ) -> Unit,
    onPlayingSpeedChange: ( Float ) -> Unit,
    onPlayingPitchChange: ( Float ) -> Unit,
    onCreateEqualizerActivityContract: () -> Unit,
    onCreatePlaylist: ( String, List<Song> ) -> Unit,
    onAddSongsToPlaylist: (Playlist, List<Song> ) -> Unit,
    onViewAlbum: ( String ) -> Unit,
    onViewArtist: ( String ) -> Unit,
    onHideNowPlayingBottomSheet: () -> Unit,
    onShowSnackBar: ( String ) -> Unit,
    onStartSleepTimer: ( Duration ) -> Unit,
    onStopSleepTimer: () -> Unit,
) {
    val currentWindowSizeClass = currentWindowAdaptiveInfo().windowSizeClass
    var showOptionsMenu by remember { mutableStateOf( false ) }
    var showSongDetailsDialog by remember { mutableStateOf( false ) }
    var showSleepTimerBottomSheet by remember { mutableStateOf( false ) }

    when ( uiState ) {
        NowPlayingScreenUiState.Loading -> {}
        is NowPlayingScreenUiState.Success -> {
            uiState.queue.firstOrNull { it.id == uiState.playerState.currentlyPlayingSongId }?.let { song ->
                when ( currentWindowSizeClass.windowWidthSizeClass ) {
                    WindowWidthSizeClass.COMPACT, WindowWidthSizeClass.MEDIUM -> {
                        PortraitLayout(
                            modifier = modifier,
                            uiState = uiState,
                            lyricsUiState = lyricsUiState,
                            currentlyPlayingSong = song,
                            playbackPosition = playbackPosition,
                            durationFormatter = durationFormatter,
                            onFavorite = onFavorite,
                            onArtworkSwipedLeft = onSwipeArtworkLeft,
                            onArtworkSwipedRight = onSwipeArtworkRight,
                            onArtworkSwipedDown = onHideNowPlayingBottomSheet,
                            onArtworkClicked = onArtworkClicked,
                            onArtistClicked = onArtistClicked,
                            onShowOptionsMenu = { showOptionsMenu = true },
                            onSeekStart = onSeekStart,
                            onSeekEnd = onSeekEnd,
                            onPausePlayButtonClick = onPausePlayButtonClick,
                            onPreviousButtonClick = onPreviousButtonClick,
                            onPlayNext = onPlayNext,
                            onNavigateToQueue = onNavigateToQueue,
                            onToggleLoopMode = onToggleLoopMode,
                            onToggleShuffleMode = onToggleShuffleMode,
                            onPlayingSpeedChange = onPlayingSpeedChange,
                            onPlayingPitchChange = onPlayingPitchChange,
                            onCreateEqualizerActivityContract = onCreateEqualizerActivityContract,
                            onShowSleepTimerBottomSheet = { showSleepTimerBottomSheet = true }
                        )
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
                            onArtworkSwipedDown = onHideNowPlayingBottomSheet,
                            onArtistClicked = onArtistClicked,
                            onShowOptionsMenu = { showOptionsMenu = true },
                            onSeekStart = onSeekStart,
                            onSeekEnd = onSeekEnd,
                            onPausePlayButtonClick = onPausePlayButtonClick,
                            onPreviousButtonClick = onPreviousButtonClick,
                            onPlayNext = onPlayNext,
                            onNavigateToQueue = onNavigateToQueue,
                            onToggleLoopMode = onToggleLoopMode,
                            onToggleShuffleMode = onToggleShuffleMode,
                            onPlayingSpeedChange = onPlayingSpeedChange,
                            onPlayingPitchChange = onPlayingPitchChange,
                            onCreateEqualizerActivityContract = onCreateEqualizerActivityContract,
                            onShowSleepTimerBottomSheet = { showSleepTimerBottomSheet = true }
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
                            playlists = uiState.playlists,
                            onDismissRequest = { showOptionsMenu = false },
                            onPlayNext = {}, // No need to do anything as duplicates are not allowed in queue
                            onAddToQueue = {}, // No need to do anything as duplicates are not allowed in queue
                            onCreatePlaylist = onCreatePlaylist,
                            onAddSongsToPlaylist = onAddSongsToPlaylist,
                            onGetSongs = { listOf( song ) },
                            leadingBottomSheetMenuItem = { onDismissRequest ->
                                BottomSheetMenuItem(
                                    leadingIcon = if ( uiState.currentlyPlayingSongIsFavorite ) {
                                        MusicMattersIcons.Favorite
                                    } else {
                                        MusicMattersIcons.FavoriteBorder
                                    },
                                    label = uiState.language.favorite,
                                    leadingIconTint = MaterialTheme.colorScheme.primary
                                ) {
                                    onDismissRequest()
                                    onFavorite( song, !uiState.currentlyPlayingSongIsFavorite )
                                }
                            },
                            onShowSnackBar = onShowSnackBar,
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
                        metadata = uiState.songAdditionalMetadata
                    ) {
                        showSongDetailsDialog = false
                    }
                }

                if ( showSleepTimerBottomSheet ) {
                    ModalBottomSheet(
                        onDismissRequest = { showSleepTimerBottomSheet = false }
                    ) {
                        val sleepTimerStartedMessage = stringResource(
                            id = R.string.feature_nowplaying_sleep_timer_set
                        )
                        val sleepTimerStoppedMessage = stringResource(
                            id = R.string.feature_nowplaying_sleep_timer_off
                        )
                        SleepTimerDialogContent(
                            sleepTimer = uiState.sleepTimer,
                            onStartSleepTimer = {
                                onStartSleepTimer( it )
                                onShowSnackBar( sleepTimerStartedMessage )
                            },
                            onStartTimerToEndOfCurrentSong = {
                                val duration = playbackPosition.total.minus(
                                    playbackPosition.played
                                )
                                onStartSleepTimer(
                                    duration.toDuration( DurationUnit.MILLISECONDS )
                                )
                                onShowSnackBar( sleepTimerStartedMessage )
                            },
                            onStopSleepTimer = {
                                onStopSleepTimer()
                                onShowSnackBar( sleepTimerStoppedMessage )
                            },
                            onDismissRequest = { showSleepTimerBottomSheet = false },
                        )
                    }
                }
            }
        }
    }

}





@OptIn( ExperimentalTime::class )
@Composable
private fun SleepTimerDialogContent(
    modifier: Modifier = Modifier,
    sleepTimer: SleepTimer?,
    onStartSleepTimer: (Duration ) -> Unit,
    onStopSleepTimer: () -> Unit,
    onStartTimerToEndOfCurrentSong: () -> Unit,
    onDismissRequest: () -> Unit,
) {

    Column(
        modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
    ) {
        Row(
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(0.dp, 8.dp),
        ) {
            Text(
                text = sleepTimer?.let {
                    val now = Clock.System.now().toEpochMilliseconds().toDuration( DurationUnit.MILLISECONDS )
                    val durationLeft = it.endsAt.minus( now )
                    buildString {
                        append(
                            durationFormatted(
                                duration = durationLeft
                            )

                        )
                        append( " " )
                        append( stringResource( id = R.string.feature_nowplaying_left ) )
                    }
                } ?: stringResource( id = R.string.feature_nowplaying_sleep_timer ),
                fontWeight = FontWeight.SemiBold,
            )
        }
        HorizontalDivider(
            thickness = 1.5.dp,
            modifier = Modifier.padding( 8.dp )
        )
        SLEEP_TIMER_VALUES.forEach {
            ListItem(
                headlineContent = {
                    Text(
                        text = durationFormatted( it ),
                        fontWeight = FontWeight.SemiBold,
                    )
                },
                modifier = Modifier.clickable {
                    onStartSleepTimer( it )
                    onDismissRequest()
                }
            )
        }
        ListItem(
            headlineContent = {
                Text(
                    text = stringResource( id = R.string.feature_nowplaying_end_of_episode ),
                    fontWeight = FontWeight.SemiBold
                )
            },
            modifier = Modifier.clickable {
                onStartTimerToEndOfCurrentSong()
                onDismissRequest()
            }
        )
        sleepTimer?.let {
            ListItem(
                headlineContent = {
                    Text(
                        text = stringResource( id = R.string.feature_nowplaying_turn_off_timer ),
                        fontWeight = FontWeight.SemiBold,
                    )
                },
                modifier = Modifier.clickable {
                    onStopSleepTimer()
                    onDismissRequest()
                }
            )
        }
    }
}

@Composable
private fun durationFormatted( duration: Duration ): String =
    duration.toComponents { hours, minutes, seconds, _ ->
        when {
            hours > 0 -> {
                if ( minutes > 0 ) {
                    String.format( Locale.getDefault(), "%d hr %02d min", hours, minutes )
                }
                else {
                    String.format( Locale.getDefault(), "%d hr", hours )
                }
            }
            minutes > 0 -> String.format( Locale.getDefault(), "%d min", minutes )
            else -> String.format( Locale.getDefault(), "%d sec", seconds )
        }
    }

private val SLEEP_TIMER_VALUES = setOf(
    5L.toDuration(DurationUnit.MINUTES ),
    10L.toDuration( DurationUnit.MINUTES ),
    15L.toDuration( DurationUnit.MINUTES ),
    30L.toDuration( DurationUnit.MINUTES ),
    45L.toDuration( DurationUnit.MINUTES ),
    1L.toDuration( DurationUnit.HOURS )
)


@DevicePreviews
@Composable
private fun NowPlayingScreenContentPreview() {
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
                    loopMode = LoopMode.Queue,
                ),
                queue = listOf(
                    Song(
                        id = "song-id-1",
                        mediaUri = "Uri.EMPTY",
                        title = "Started From the Bottom",
                        displayTitle = "",
                        duration = 0L,
                        artists = setOf( "Drake", "Disclosure", "London", "Grammar", "The Weekend", "Young thug" ),
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
                songAdditionalMetadata = SongAdditionalMetadata(
                    songId = "",
                    codec = "mp3",
                    bitrate = 0,
                    samplingRate = 0f,
                    bitsPerSample = 0,
                    genre = "Hip-Hop"
                ),
                sleepTimer = SleepTimer(
                    duration = 300000L.toDuration( DurationUnit.MILLISECONDS ),
                    endsAt = System.currentTimeMillis().toDuration( DurationUnit.MILLISECONDS ),
                    timer = Timer()
                )
            ),
            lyricsUiState = LyricsUiState.Loading,
            playbackPosition = PlaybackPosition( 2L, 3L, 5L ),
            durationFormatter = { "05:33" },
            onArtistClicked = {},
            onFavorite = { _, _ -> },
            onPausePlayButtonClick = {},
            onPreviousButtonClick = {},
            onPlayNext = {},
            onSeekEnd = {},
            onArtworkClicked = {},
            onSwipeArtworkLeft = {},
            onSwipeArtworkRight = {},
            onPlayingSpeedChange = {},
            onPlayingPitchChange = {},
            onNavigateToQueue = {},
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
            onAddSongsToPlaylist = { _, _ -> },
            onViewAlbum = {},
            onViewArtist = {},
            onHideNowPlayingBottomSheet = {},
            onShowSnackBar = {},
            onStopSleepTimer = {},
            onStartSleepTimer = {},
        )
    }
}






