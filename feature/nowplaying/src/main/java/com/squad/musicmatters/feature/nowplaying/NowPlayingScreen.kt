package com.squad.musicmatters.feature.nowplaying

import android.content.Context
import android.content.Intent
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
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.window.core.layout.WindowSizeClass
import com.squad.musicmatters.core.datastore.DefaultPreferences
import com.squad.musicmatters.core.designsystem.component.MusicMattersIcons
import com.squad.musicmatters.core.media.media.extensions.formatMilliseconds
import com.squad.musicmatters.core.designsystem.theme.MusicMattersTheme
import com.squad.musicmatters.core.media.connection.PlaybackPosition
import com.squad.musicmatters.core.media.connection.PlayerState
import com.squad.musicmatters.core.media.connection.SleepTimer
import com.squad.musicmatters.core.model.LoopMode
import com.squad.musicmatters.core.model.Lyric
import com.squad.musicmatters.core.model.Playlist
import com.squad.musicmatters.core.model.Song
import com.squad.musicmatters.core.model.SongAdditionalMetadata
import com.squad.musicmatters.core.model.ThemeMode
import com.squad.musicmatters.core.ui.BottomSheetMenuItem
import com.squad.musicmatters.core.ui.GenericOptionsBottomSheet
import com.squad.musicmatters.core.ui.dialog.SongDetailsDialog
import com.squad.musicmatters.feature.nowplaying.components.emptyUserData
import kotlinx.coroutines.launch
import java.util.Locale
import java.util.Timer
import kotlin.time.Clock
import kotlin.time.Duration
import kotlin.time.DurationUnit
import kotlin.time.ExperimentalTime
import kotlin.time.toDuration

import com.squad.musicmatters.core.i8n.R as i8nR

@Retention( AnnotationRetention.BINARY )
@Target( AnnotationTarget.ANNOTATION_CLASS, AnnotationTarget.FUNCTION )
@Preview( name = "Phone", device = "spec:width=411dp,height=891dp", showSystemUi = true )
@Preview( name = "Unfolded Foldable", device = "spec:width=673dp,height=841dp", showSystemUi = true )
@Preview( name = "Tablet", device = "spec:width=1280dp,height=800dp,dpi=240", showSystemUi = true )
@Preview( name = "Desktop", device = "spec:width=1920dp,height=1080dp,dpi=160", showSystemUi = true )
annotation class PreviewScreenSizesPortrait

// Stateful
@Composable
fun NowPlayingScreen(
    viewModel: NowPlayingScreenViewModel = hiltViewModel(),
    backgroundColor: Color = Color.Unspecified,
    onViewAlbum: ( String ) -> Unit,
    onViewArtist: ( String ) -> Unit,
    onNavigateToQueueScreen: () -> Unit,
    onNavigateToLyricsScreen: () -> Unit,
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
            backgroundColor = backgroundColor,
            onGetPlaybackPosition = { playbackPosition },
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
            onNavigateToQueueScreen = {
                onHideBottomSheet()
                onNavigateToQueueScreen()
            },
            onNavigateToLyricsScreen = {
                onHideBottomSheet()
                onNavigateToLyricsScreen()
            },
            onCreateEqualizerActivityContract = onLaunchEqualizerActivity,
            onCreatePlaylist = viewModel::createPlaylist,
            onAddSongsToPlaylist = viewModel::addSongsToPlaylist,
            onViewAlbum = onViewAlbum,
            onViewArtist = onViewArtist,
            onHideNowPlayingBottomSheet = onHideBottomSheet,
            onSwipeArtworkLeft = viewModel::playNextSong,
            onSwipeArtworkRight = viewModel::playPreviousSong,
            onStartSleepTimer = viewModel::startSleepTimer,
            onStopSleepTimer = viewModel::stopSleepTimer,
            onShowLyrics = viewModel::onShowLyrics,
            onToggleLoopMode = viewModel::onToggleLoopMode,
            onToggleShuffleMode = viewModel::onToggleShuffleMode,
            onRemoveFromQueue = viewModel::removeSongFromQueue,
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
    backgroundColor: Color = Color.Unspecified,
    onGetPlaybackPosition: () -> PlaybackPosition,
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
    onNavigateToQueueScreen: () -> Unit,
    onNavigateToLyricsScreen: () -> Unit,
    onCreateEqualizerActivityContract: () -> Unit,
    onCreatePlaylist: ( String, List<Song> ) -> Unit,
    onAddSongsToPlaylist: (Playlist, List<Song> ) -> Unit,
    onViewAlbum: ( String ) -> Unit,
    onViewArtist: ( String ) -> Unit,
    onHideNowPlayingBottomSheet: () -> Unit,
    onShowSnackBar: ( String ) -> Unit,
    onStartSleepTimer: ( Duration ) -> Unit,
    onStopSleepTimer: () -> Unit,
    onShowLyrics: ( Boolean ) -> Unit,
    onToggleShuffleMode: ( Boolean ) -> Unit,
    onToggleLoopMode: ( LoopMode ) -> Unit,
    onRemoveFromQueue: ( Song ) -> Unit,
) {
    val currentWindowSizeClass = currentWindowAdaptiveInfo().windowSizeClass
    var showOptionsMenu by remember { mutableStateOf( false ) }
    var showSongDetailsDialog by remember { mutableStateOf( false ) }
    var showSleepTimerBottomSheet by remember { mutableStateOf( false ) }

    // 1. Check for EXPANDED (Tablets, unfolded foldables in landscape)
    // Checks if width is at least 840dp
    val isExpanded = currentWindowSizeClass
        .isWidthAtLeastBreakpoint( WindowSizeClass.WIDTH_DP_EXPANDED_LOWER_BOUND )

    // 2. Check for MEDIUM or higher (Small tablets, portrait foldables)
    // Checks if width is at least 600dp
    val isMediumOrWider = currentWindowSizeClass
        .isWidthAtLeastBreakpoint( WindowSizeClass.WIDTH_DP_MEDIUM_LOWER_BOUND )

    when ( uiState ) {
        NowPlayingScreenUiState.Loading -> {}
        is NowPlayingScreenUiState.Success -> {
            uiState.currentlyPlayingSong?.let { song ->
                when {
                    isExpanded || isMediumOrWider -> {
                        ExpandedLayout(
                            modifier = modifier,
                            uiState = uiState,
                            lyricsUiState = lyricsUiState,
                            currentlyPlayingSong = song,
                            onGetPlaybackPosition = onGetPlaybackPosition,
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
                            onNavigateToQueueScreen = onNavigateToQueueScreen,
                            onNavigateToLyricsScreen = onNavigateToLyricsScreen,
                            onShowLyrics = onShowLyrics,
                            onToggleShuffleMode = onToggleShuffleMode,
                            onToggleLoopMode = onToggleLoopMode,
                        )
                    }
                    else -> {
                        PortraitLayout(
                            modifier = modifier,
                            uiState = uiState,
                            lyricsUiState = lyricsUiState,
                            currentlyPlayingSong = song,
                            backgroundColor = backgroundColor,
                            onGetPlaybackPosition = onGetPlaybackPosition,
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
                            onNavigateToQueueScreen = onNavigateToQueueScreen,
                            onNavigateToLyricsScreen = onNavigateToLyricsScreen,
                            onShowLyrics = onShowLyrics,
                            onToggleShuffleMode = onToggleShuffleMode,
                            onToggleLoopMode = onToggleLoopMode,
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
                            onGetPlaylists = { uiState.playlists },
                            onDismissRequest = { showOptionsMenu = false },
                            onPlayNext = {}, // No need to do anything as duplicates are not allowed in idsOfSongsInQueue
                            onAddToQueue = {}, // No need to do anything as duplicates are not allowed in idsOfSongsInQueue
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
                                    label = stringResource( id = i8nR.string.core_i8n_favorite ),
                                    leadingIconTint = MaterialTheme.colorScheme.primary
                                ) {
                                    onDismissRequest()
                                    onFavorite( song, !uiState.currentlyPlayingSongIsFavorite )
                                }
                            },
                            onShowSnackBar = onShowSnackBar,
                            songIsPresentInQueue = { false },
                            onRemoveFromQueue = { onRemoveFromQueue( song ) },
                            trailingBottomSheetMenuItems = { onDismissRequest ->
                                song.albumTitle?.let { albumTitle ->
                                    BottomSheetMenuItem(
                                        leadingIcon = Icons.Default.Album,
                                        label = stringResource(
                                            id = i8nR.string.core_i8n_view_album,
                                            albumTitle
                                        )
                                    ) {
                                        onDismissRequest()
                                        onHideNowPlayingBottomSheet()
                                        onViewAlbum( albumTitle )
                                    }
                                }
                                song.artists.forEach { artistName ->
                                    BottomSheetMenuItem(
                                        leadingIcon = Icons.Default.Person,
                                        label = stringResource(
                                            id = i8nR.string.core_i8n_go_to_artist,
                                            artistName
                                        )
                                    ) {
                                        onDismissRequest()
                                        onHideNowPlayingBottomSheet()
                                        onViewArtist( artistName )
                                    }
                                }
                                BottomSheetMenuItem(
                                    leadingIcon = MusicMattersIcons.Info,
                                    label = stringResource( id = i8nR.string.core_i8n_details )
                                ) {
                                    onDismissRequest()
                                    showSongDetailsDialog = true
                                }
                                BottomSheetMenuItem(
                                    leadingIcon = uiState.sleepTimer?.let {
                                        MusicMattersIcons.TimerOn
                                    } ?: MusicMattersIcons.Timer,
                                    label = stringResource( id = i8nR.string.core_i8n_sleep_timer ),
                                    leadingIconTint = uiState.sleepTimer?.let {
                                        MaterialTheme.colorScheme.primary
                                    }
                                ) {
                                    onDismissRequest()
                                    showSleepTimerBottomSheet = true
                                }
                                BottomSheetMenuItem(
                                    leadingIcon = MusicMattersIcons.Equalizer,
                                    label = stringResource( id = i8nR.string.core_i8n_equalizer )
                                ) {
                                    onDismissRequest()
                                    onCreateEqualizerActivityContract()
                                }
                            }
                        )
                    }
                }

                if ( showSongDetailsDialog ) {
                    SongDetailsDialog(
                        song = song,
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
                            id = i8nR.string.core_i8n_sleep_timer_set
                        )
                        val sleepTimerStoppedMessage = stringResource(
                            id = i8nR.string.core_i8n_sleep_timer_off
                        )
                        SleepTimerDialogContent(
                            sleepTimer = uiState.sleepTimer,
                            onStartSleepTimer = {
                                onStartSleepTimer( it )
                                onShowSnackBar( sleepTimerStartedMessage )
                            },
                            onStartTimerToEndOfCurrentSong = {
                                val duration = onGetPlaybackPosition().total.minus(
                                    onGetPlaybackPosition().played
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
                    val now = Clock.System.now().toEpochMilliseconds()
                        .toDuration( DurationUnit.MILLISECONDS )
                    val durationLeft = it.endsAt.minus( now )
                    buildString {
                        append(
                            durationFormatted(
                                duration = durationLeft
                            )

                        )
                        append( " " )
                        append( stringResource( id = i8nR.string.core_i8n_left ) )
                    }
                } ?: stringResource( id = i8nR.string.core_i8n_sleep_timer ),
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
                colors = ListItemDefaults.colors(
                    containerColor = Color.Transparent
                ),
                modifier = Modifier.clickable {
                    onStartSleepTimer( it )
                    onDismissRequest()
                }
            )
        }
        ListItem(
            headlineContent = {
                Text(
                    text = stringResource( id = i8nR.string.core_i8n_end_of_episode ),
                    fontWeight = FontWeight.SemiBold
                )
            },
            colors = ListItemDefaults.colors(
                containerColor = Color.Transparent
            ),
            modifier = Modifier.clickable {
                onStartTimerToEndOfCurrentSong()
                onDismissRequest()
            }
        )
        sleepTimer?.let {
            ListItem(
                headlineContent = {
                    Text(
                        text = stringResource( id = i8nR.string.core_i8n_turn_off_timer ),
                        fontWeight = FontWeight.SemiBold,
                    )
                },
                colors = ListItemDefaults.colors(
                    containerColor = Color.Transparent
                ),
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


@PreviewScreenSizesPortrait
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
                    loopMode = LoopMode.Queue,
                ),
                currentlyPlayingSong = Song(
                    id = "song-id-1",
                    mediaUri = "Uri.EMPTY",
                    title = "Started From the Bottom",
                    albumId = 0L,
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
                ),
                currentlyPlayingSongIsFavorite = true,
                playerState = PlayerState(
                    currentlyPlayingSongId = "song-id-1",
                    isPlaying = true,
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
            lyricsUiState = LyricsUiState.Success(
                lyrics = listOf(
                    Lyric(
                        timeStamp = java.time.Duration.ofMinutes( 1 ),
                        content = "Sometime say the magic you dey feel inside is like gold"
                    ),
                    Lyric(
                        timeStamp = java.time.Duration.ofMinutes( 2 ),
                        content = "Something like do re mi fa so lat ti do do (Yeah)"
                    ),
                    Lyric(
                        timeStamp = java.time.Duration.ofMinutes( 3 ),
                        content = "Make I sing for you la la do do"
                    ),
                    Lyric(
                        timeStamp = java.time.Duration.ofMinutes( 4 ),
                        content = "Make I sing your song"
                    ),
                    Lyric(
                        timeStamp = java.time.Duration.ofMinutes( 5 ),
                        content = "Make I sing make you wine am do do o"
                    )
                ),
            ),
            onGetPlaybackPosition = { PlaybackPosition( 2L, 3L, 5L ) },
            onArtistClicked = {},
            onFavorite = { _, _ -> },
            onPausePlayButtonClick = {},
            onPreviousButtonClick = {},
            onPlayNext = {},
            onSeekEnd = {},
            onArtworkClicked = {},
            onSwipeArtworkLeft = {},
            onSwipeArtworkRight = {},
            onNavigateToQueueScreen = {},
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
            onShowLyrics = {},
            onToggleLoopMode = {},
            onToggleShuffleMode = {},
            onRemoveFromQueue = {},
            onNavigateToLyricsScreen = {},
        )
    }
}






