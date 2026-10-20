package com.squad.musicmatters.feature.player.components

import android.content.Context
import android.content.Intent
import androidx.activity.result.contract.ActivityResultContract
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.boundsInParent
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import com.squad.musicmatters.core.datastore.DefaultPreferences
import com.squad.musicmatters.core.designsystem.theme.MusicMattersTheme
import com.squad.musicmatters.core.media.connection.PlaybackPosition
import com.squad.musicmatters.core.media.connection.PlayerState
import com.squad.musicmatters.core.media.connection.SleepTimer
import com.squad.musicmatters.core.model.BottomBarLabelVisibility
import com.squad.musicmatters.core.model.LoopMode
import com.squad.musicmatters.core.model.Lyric
import com.squad.musicmatters.core.model.SongMetadata
import com.squad.musicmatters.core.model.SortAlbumsBy
import com.squad.musicmatters.core.model.SortArtistsBy
import com.squad.musicmatters.core.model.SortGenresBy
import com.squad.musicmatters.core.model.SortPathsBy
import com.squad.musicmatters.core.model.SortPlaylistsBy
import com.squad.musicmatters.core.model.SortSongsBy
import com.squad.musicmatters.core.model.ThemeMode
import com.squad.musicmatters.core.model.UserData
import com.squad.musicmatters.core.ui.DynamicAsyncImage
import com.squad.musicmatters.core.ui.FadeTransition
import com.squad.musicmatters.core.ui.MusicMattersPreviewParametersProvider
import com.squad.musicmatters.core.ui.PreviewData
import com.squad.musicmatters.feature.player.LyricsUiState
import com.squad.musicmatters.feature.player.NowPlayingScreenCallbacks
import com.squad.musicmatters.feature.player.NowPlayingScreenModel
import com.squad.musicmatters.feature.player.NowPlayingScreenUiState
import java.util.Timer
import kotlin.math.absoluteValue
import kotlin.time.DurationUnit
import kotlin.time.toDuration

@Composable
fun MiniPlayerContent(
    modifier: Modifier = Modifier,
    model: NowPlayingScreenModel,
    callbacks: NowPlayingScreenCallbacks,
    onNowPlayingBottomBarClick: () -> Unit,
) {

    model.uiState.currentlyPlayingSong.let { song ->
        Column(
            verticalArrangement = Arrangement.SpaceBetween,
            modifier = modifier.fillMaxWidth()
        ) {
            Row (
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding( 0.dp, 8.dp )
            ) {
                Spacer( modifier = Modifier.width( 12.dp ) )
                DynamicAsyncImage(
                    imageUri = song.artworkUri?.toUri(),
                    contentDescription = null,
                    modifier = Modifier
                        .size( 45.dp )
                        .clip( RoundedCornerShape( 10.dp ) )
                )
                Spacer( modifier = Modifier.width( 15.dp ) )
                Column (
                    modifier = Modifier.weight( 1f )
                ) {
                    NowPlayingBottomBarContentText(
                        text = song.title,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.SemiBold
                        ),
                        textMarquee = model.uiState.userData.miniPlayerTextMarquee,
                        colorScheme = model.colorScheme,
                        onClick = onNowPlayingBottomBarClick,
                    )
                    NowPlayingBottomBarContentText(
                        text = song.artist,
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface.copy( alpha = 0.5f )
                        ),
                        colorScheme = model.colorScheme,
                        textMarquee = model.uiState.userData.miniPlayerTextMarquee,
                        onClick = onNowPlayingBottomBarClick,
                    )
                }
                Spacer( modifier = Modifier.width( 15.dp ) )

                IconButton( onClick = callbacks.onPlayPauseButtonClick ) {
                    AnimatedContent(
                        targetState = model.uiState.playerState.isPlaying,
                        label = "now-playing-bottom-bar-play-arrow"
                    ) {
                        Icon(
                            imageVector = if ( it ) {
                                Icons.Rounded.Pause
                            } else {
                                Icons.Rounded.PlayArrow
                            },
                            contentDescription = null
                        )
                    }
                }
                Spacer( modifier = Modifier.width( 8.dp ) )
            }
            // ------------------------- Progress Bar ------------------------------
            val progressBarColor = model.colorScheme.primary
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = Color.Transparent
                ),
                modifier = Modifier.padding( 8.dp, 0.dp )
            ) {
                Box(
                    modifier = Modifier
                        .height( 4.dp )
                        .fillMaxWidth()

                ) {
                    Box(
                        modifier = Modifier
                            .background( progressBarColor.copy( 0.3f ) )
                            .fillMaxWidth()
                            .fillMaxHeight()
                    )
                    Box(
                        modifier = Modifier
                            .align( Alignment.CenterStart )
                            .background( progressBarColor.copy( alpha = 0.4f ) )
                            .fillMaxWidth( callbacks.onGetPlaybackPosition().bufferedRatio )
                            .fillMaxHeight()
                    )
                    Box(
                        modifier = Modifier
                            .align( Alignment.CenterStart )
                            .background( progressBarColor )
                            .fillMaxWidth( callbacks.onGetPlaybackPosition().playedRatio )
                            .fillMaxHeight()
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
internal fun NowPlayingBottomBarContentText(
    modifier: Modifier = Modifier,
    text: String,
    style: TextStyle,
    textMarquee: Boolean,
    colorScheme: ColorScheme,
    onClick: ( () -> Unit )? = null,
) {

    var showOverlay by remember { mutableStateOf( false ) }

    Box {
        Text(
            text = text,
            style = style,
            maxLines = 1,
            overflow = when {
                textMarquee -> TextOverflow.Clip
                else -> TextOverflow.Ellipsis
            },
            modifier = modifier
                .fillMaxWidth()
                .pointerInput( Unit ) {
                    detectTapGestures { onClick?.let { it() } }
                }
                .then(
                    if ( textMarquee ) {
                        Modifier.basicMarquee(Int.MAX_VALUE)
                    } else Modifier
                )
                .onGloballyPositioned {
                    val offsetX = it.boundsInParent().centerLeft.x
                    showOverlay = offsetX.absoluteValue != 0f
                }
        )
        Box(
            modifier = Modifier.matchParentSize()
        ) {
            Row(
                modifier = Modifier.fillMaxSize()
            ) {
                AnimatedVisibility(
                    visible = showOverlay,
                    enter = FadeTransition.enterTransition(),
                    exit = FadeTransition.exitTransition()
                ) {
                    Box(
                        modifier = Modifier
                            .width( 12.dp )
                            .fillMaxHeight()
                            .background(
                                brush = Brush.horizontalGradient(
                                    colors = listOf(
                                        colorScheme.primaryContainer,
                                        Color.Transparent
                                    ),
                                    tileMode = TileMode.Mirror
                                )
                            )
                    )
                }
                Spacer( modifier = Modifier.weight( 1f ) )
                Box (
                    modifier = Modifier
                        .width( 12.dp )
                        .fillMaxHeight()
                        .background(
                            brush = Brush.horizontalGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    colorScheme.primaryContainer
                                )
                            )
                        )
                )
            }
        }
    }
}

@Preview( showSystemUi = true )
@Composable
private fun MiniPlayerPreview(
    @PreviewParameter( MusicMattersPreviewParametersProvider::class )
    previewData: PreviewData
) {
    MusicMattersTheme(
        themeMode = ThemeMode.LIGHT,
        primaryColorName = DefaultPreferences.PRIMARY_COLOR_NAME,
        fontName = DefaultPreferences.FONT_NAME,
        fontScale = 1f,
        useMaterialYou = true,
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            MiniPlayerContent(
                modifier = Modifier.align( Alignment.Center ),
                model = NowPlayingScreenModel(
                    uiState = NowPlayingScreenUiState.Success(
                        userData = emptyUserData.copy(
                            loopMode = LoopMode.Queue,
                        ),
                        currentlyPlayingSong = previewData.songs.first(),
                        currentlyPlayingSongIsFavorite = true,
                        playerState = PlayerState(
                            currentlyPlayingSongId = "song-id-1",
                            isPlaying = true,
                            isBuffering = false,
                        ),
                        playlists = emptyList(),
                        songMetadata = SongMetadata(
                            songId = "",
                            codec = "mp3",
                            bitrate = 0,
                            samplingRate = 0f,
                            bitsPerSample = 0,
                            genreName = "Hip-Hop"
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
                    colorScheme = MaterialTheme.colorScheme,
                ),
                callbacks = NowPlayingScreenCallbacks(
                    onGetPlaybackPosition = { PlaybackPosition( 2L, 3L, 5L ) },
                    onArtistClicked = {},
                    onFavorite = { _, _ -> },
                    onPlayPauseButtonClick = {},
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
                    onShowSnackBar = {},
                    onStopSleepTimer = {},
                    onStartSleepTimer = {},
                    onShowLyrics = {},
                    onToggleLoopMode = {},
                    onToggleShuffleMode = {},
                    onRemoveSongFromQueue = {},
                    onNavigateToLyricsScreen = {},
                ),
                onNowPlayingBottomBarClick = {},
            )
        }
    }
}

val emptyUserData = UserData(
    fontName = "Product Sans",
    fontScale = 1f,
    themeMode = ThemeMode.FOLLOW_SYSTEM,
    useMaterialYou = true,
    primaryColorName = "Blue",
    bottomBarLabelVisibility = BottomBarLabelVisibility.ALWAYS_VISIBLE,
    fadePlayback = true,
    fadePlaybackDuration = 1f,
    requireAudioFocus = true,
    ignoreAudioFocusLoss = false,
    playOnHeadphonesConnect = true,
    pauseOnHeadphonesDisconnect = false,
    miniPlayerTextMarquee = true,
    loopMode = LoopMode.None,
    shuffle = false,
    showLyrics = false,
    currentlyDisabledTreePaths = emptySet(),
    sortSongsBy = SortSongsBy.TITLE,
    sortSongsReverse = false,
    sortArtistsBy = SortArtistsBy.ARTIST_NAME,
    sortArtistsReverse = false,
    sortGenresBy = SortGenresBy.NAME,
    sortGenresReverse = false,
    sortPlaylistsBy = SortPlaylistsBy.TITLE,
    sortPlaylistsReverse = false,
    sortAlbumsBy = SortAlbumsBy.ALBUM_NAME,
    sortAlbumsReverse = false,
    sortPathsBy = SortPathsBy.NAME,
    sortPathsReverse = false,
    currentlyPlayingSongId = "",
    showLyricsOnSeparateScreen = true
)

