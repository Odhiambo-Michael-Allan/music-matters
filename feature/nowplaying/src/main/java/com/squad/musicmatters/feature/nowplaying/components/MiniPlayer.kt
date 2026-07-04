package com.squad.musicmatters.feature.nowplaying.components

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
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
import androidx.compose.material.icons.rounded.FastForward
import androidx.compose.material.icons.rounded.FastRewind
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.SkipNext
import androidx.compose.material.icons.rounded.SkipPrevious
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.boundsInParent
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastFirstOrNull
import androidx.core.net.toUri
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kmpalette.color
import com.kmpalette.rememberPaletteState
import com.materialkolor.hct.Hct
import com.materialkolor.ktx.toColor
import com.materialkolor.ktx.toHct
import com.squad.musicmatters.core.datastore.DefaultPreferences
import com.squad.musicmatters.core.designsystem.theme.MusicMattersTheme
import com.squad.musicmatters.core.media.connection.PlaybackPosition
import com.squad.musicmatters.core.media.connection.PlayerState
import com.squad.musicmatters.core.model.BottomBarLabelVisibility
import com.squad.musicmatters.core.model.LoopMode
import com.squad.musicmatters.core.model.Song
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
import com.squad.musicmatters.core.ui.TransitionDurations
import com.squad.musicmatters.feature.nowplaying.NowPlayingScreenUiState
import com.squad.musicmatters.feature.nowplaying.NowPlayingScreenViewModel
import kotlin.math.absoluteValue



// Stateful
@Composable
fun MiniPlayer(
    modifier: Modifier = Modifier,
    viewModel: NowPlayingScreenViewModel = hiltViewModel(),
    onShowNowPlayingBottomSheet: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val playbackPosition by viewModel.playbackPosition.collectAsStateWithLifecycle()

    MiniPlayerContent(
        uiState = uiState,
        playbackPosition = playbackPosition,
        onNowPlayingBottomBarSwipeUp = onShowNowPlayingBottomSheet,
        onNowPlayingBottomBarClick = onShowNowPlayingBottomSheet,
        nextSong = viewModel::playNextSong,
        previousSong = viewModel::playPreviousSong,
        seekBack = viewModel::fastRewind,
        seekForward = viewModel::fastForward,
        playPause = viewModel::playPause,
        modifier = modifier,
    )
}

// Stateless
@Composable
private fun MiniPlayerContent(
    modifier: Modifier = Modifier,
    uiState: NowPlayingScreenUiState,
    playbackPosition: PlaybackPosition,
    onNowPlayingBottomBarSwipeUp: () -> Unit,
    onNowPlayingBottomBarClick: () -> Unit,
    nextSong: () -> Boolean,
    previousSong: () -> Boolean,
    seekBack: () -> Unit,
    seekForward: () -> Unit,
    playPause: () -> Unit,
) {

    val paletteState = rememberPaletteState()
    var coverArtBitmap by remember { mutableStateOf<ImageBitmap?>( null ) }

    LaunchedEffect( coverArtBitmap ) {
        coverArtBitmap?.let { paletteState.generate( it ) }
    }

    val colorToApply by remember( coverArtBitmap ) {
        derivedStateOf {
            val currentPalette = paletteState.palette

            val rawColor = if ( coverArtBitmap != null && currentPalette != null ) {
                val swatches = currentPalette.swatches

                if ( swatches.isNotEmpty() ) {
                    // Sort descending by population just like your original logic
                    val sortedSwatches = swatches.sortedByDescending { it.population }
                    val firstSwatch = sortedSwatches.first()
                    val firstSwatchColorHct = firstSwatch.color.toHct()
                    val firstSwatchPopulation = firstSwatch.population

                    // Search for a swatch that is significantly more vibrant
                    // (chroma delta >= 30) and makes up at least 10% of the dominant swatch
                    val moreChromatic = sortedSwatches.fastFirstOrNull { swatch ->
                        val currentHct = swatch.color.toHct()
                        ( currentHct.chroma - firstSwatchColorHct.chroma >= 30 ) &&
                                ( swatch.population.toFloat() / firstSwatchPopulation >= 0.1f )
                    }

                    // Return the more vibrant option, or fall back to the dominant one
                    moreChromatic?.color ?: firstSwatch.color
                } else {
                    Color.Unspecified // Fallback if swatches are empty
                }
            } else {
                Color.Unspecified // Fallback when loading or image is null
            }

            if ( rawColor != Color.Unspecified ) {
                // Clamping the tone between 20 (soft dark gray) and 40 (soft light gray)
                rawColor.clampBrightness( minTone = 20.0, maxTone = 40.0 )
            } else {
                Color.Unspecified
            }
        }
    }

    when ( uiState ) {
        NowPlayingScreenUiState.Loading -> {}
        is NowPlayingScreenUiState.Success -> {

            AnimatedVisibility(
                visible = uiState.currentlyPlayingSong != null,
                modifier = modifier,
            ) {

                uiState.currentlyPlayingSong?.let { playingSong ->
                    ElevatedCard (
                        colors = CardDefaults.cardColors(
                            containerColor = colorToApply
                        ),
                        onClick = onNowPlayingBottomBarClick,
                        modifier = modifier
                            .fillMaxWidth()
                            .padding( 8.dp, 0.dp )
                            .wrapContentHeight()
                            .swipeable(
                                onSwipeUp = onNowPlayingBottomBarSwipeUp,
                            ),
                    ) {
                        Column {
                            Row (
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding( 0.dp, 8.dp )
                            ) {
                                Spacer( modifier = Modifier.width( 12.dp ) )
                                AnimatedContent(
                                    label = "now-playing-card-image",
                                    targetState = playingSong,
                                    transitionSpec = {
                                        val from = fadeIn(
                                            animationSpec = TransitionDurations.Normal.asTween(
                                                delayMillis = 150
                                            )
                                        )
                                        val to = fadeOut(
                                            animationSpec = TransitionDurations.Fast.asTween()
                                        )
                                        from togetherWith to
                                    },
                                ) { song ->
                                    DynamicAsyncImage(
                                        imageUri = song.artworkUri?.toUri(),
                                        contentDescription = null,
                                        onImageLoaded = { coverArtBitmap = it },
                                        modifier = Modifier
                                            .size( 45.dp )
                                            .clip( RoundedCornerShape( 10.dp ) )
                                    )
                                }
                                Spacer( modifier = Modifier.width( 15.dp ) )
                                AnimatedContent(
                                    modifier = Modifier.weight( 1f ),
                                    label = "now-playing-card-content",
                                    targetState = playingSong,
                                    transitionSpec = {
                                        val from = fadeIn(
                                            animationSpec = TransitionDurations.Normal.asTween(
                                                delayMillis = 150
                                            )
                                        ) + scaleIn(
                                            initialScale = 0.99f,
                                            animationSpec = TransitionDurations.Normal.asTween(
                                                delayMillis = 150
                                            )
                                        )
                                        val to = fadeOut(
                                            animationSpec = TransitionDurations.Fast.asTween()
                                        )
                                        from togetherWith to
                                    }
                                ) {
                                    MiniPlayerContent(
                                        song = it,
                                        nextSong = nextSong,
                                        previousSong = previousSong,
                                        textMarquee = uiState.userData.miniPlayerTextMarquee,
                                        colorToApply = colorToApply,
                                        onNowPlayingBottomBarClick = onNowPlayingBottomBarClick,
                                    )
                                }
                                Spacer( modifier = Modifier.width( 15.dp ) )

                                IconButton( onClick = playPause ) {
                                    AnimatedContent(
                                        targetState = uiState.playerState.isPlaying,
                                        label = "now-playing-bottom-bar-play-arrow"
                                    ) {
                                        Icon(
                                            imageVector = if ( it ) {
                                                Icons.Rounded.Pause
                                            } else {
                                                Icons.Rounded.PlayArrow
                                            },
                                            tint = if ( colorToApply == Color.Unspecified ) {
                                                LocalContentColor.current
                                            } else {
                                                Color.White
                                            },
                                            contentDescription = null
                                        )
                                    }
                                }
                                Spacer( modifier = Modifier.width( 8.dp ) )
                            }
                            // ------------------------- Progress Bar ------------------------------
                            val progressBarColor = if ( colorToApply == Color.Unspecified ) {
                                MaterialTheme.colorScheme.primary
                            } else {
                                Color.White
                            }
                            Card(
                                colors = CardDefaults.cardColors(
                                    containerColor = Color.Transparent
                                ),
                                modifier = Modifier
                                    .padding( 8.dp, 0.dp )
                            ) {
                                Box(
                                    modifier = Modifier
                                        .height( 4.dp )
                                        .fillMaxWidth()
                                    
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .background(
                                                progressBarColor.copy( 0.3f )
                                            )
                                            .fillMaxWidth()
                                            .fillMaxHeight()
                                    )
                                    Box(
                                        modifier = Modifier
                                            .align( Alignment.CenterStart )
                                            .background(
                                                progressBarColor.copy( alpha = 0.4f )
                                            )
                                            .fillMaxWidth( playbackPosition.bufferedRatio )
                                            .fillMaxHeight()
                                    )
                                    Box(
                                        modifier = Modifier
                                            .align( Alignment.CenterStart )
                                            .background( progressBarColor )
                                            .fillMaxWidth( playbackPosition.playedRatio )
                                            .fillMaxHeight()
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@SuppressLint( "UnusedBoxWithConstraintsScope" )
@Composable
private fun MiniPlayerContent(
    song: Song,
    nextSong: () -> Boolean,
    previousSong: () -> Boolean,
    textMarquee: Boolean,
    colorToApply: Color,
    onNowPlayingBottomBarClick: () -> Unit,
) {
    BoxWithConstraints (
        modifier = Modifier.clipToBounds()
    ) {
        val cardWidthInPixels = this@BoxWithConstraints.constraints.maxWidth
        var offsetX by remember { mutableFloatStateOf( 0f ) }

        val cardOffsetX by animateFloatAsState(
            targetValue = offsetX / 2,
            label = "now-playing-card-offset-x"
        )
        val cardOpacity by animateFloatAsState(
            targetValue = if ( offsetX != 0f ) 0.7f else 1f,
            label = "now-playing-card-opacity"
        )

        Box(
            modifier = Modifier
                .graphicsLayer( alpha = cardOpacity, translationX = cardOffsetX )
                .pointerInput( Unit ) {
                    detectHorizontalDragGestures(
                        onDragEnd = {
                            val threshHold = cardWidthInPixels / 4
                            val affected = when {
                                -offsetX > threshHold -> nextSong()
                                offsetX > threshHold -> previousSong()
                                else -> false
                            }
                            if ( !affected ) {
                                offsetX = 0f
                            }
                        },
                        onDragCancel = {
                            offsetX = 0f
                        },
                        onHorizontalDrag = { _, dragAmount, ->
                            offsetX += dragAmount
                        },
                    )
                }
        ) {
            Column (
                modifier = Modifier.fillMaxWidth()
            ) {
                NowPlayingBottomBarContentText(
                    text = song.title,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.SemiBold
                    ),
                    textMarquee = textMarquee,
                    colorToApply = colorToApply,
                    onClick = onNowPlayingBottomBarClick,
                )
                NowPlayingBottomBarContentText(
                    text = song.artists.first(),
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface.copy( alpha = 0.5f )
                    ),
                    textMarquee = textMarquee,
                    colorToApply = colorToApply,
                    onClick = onNowPlayingBottomBarClick,
                )
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
    colorToApply: Color = Color.Unspecified,
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
            color = if ( colorToApply == Color.Unspecified ) {
                LocalContentColor.current
            } else {
                Color.White
            },
            modifier = modifier
                .pointerInput(Unit) {
                    detectTapGestures { onClick?.let { it() } }
                }
                .then(
                    if (textMarquee) {
                        Modifier.basicMarquee(Int.MAX_VALUE)
                    } else Modifier
                )
                .onGloballyPositioned {
                    val offsetX = it.boundsInParent().centerLeft.x
                    showOverlay = offsetX.absoluteValue != 0f
                }
        )
        AnimatedVisibility(
            visible = showOverlay,
            modifier = Modifier.matchParentSize(),
            enter = FadeTransition.enterTransition(),
            exit = FadeTransition.exitTransition()
        ) {

            val backgroundColor = if ( colorToApply == Color.Unspecified ) {
                CardDefaults.cardColors().containerColor
            } else {
                colorToApply
            }

            Row {
                Box(
                    modifier = Modifier
                        .width(12.dp)
                        .fillMaxHeight()
                        .background(
                            brush = Brush.horizontalGradient(
                                colors = listOf(
                                    backgroundColor,
                                    Color.Transparent
                                ),
                                tileMode = TileMode.Mirror
                            )
                        )
                )
                Spacer( modifier = Modifier.weight( 1f ) )
                Box (
                    modifier = Modifier
                        .width(12.dp)
                        .fillMaxHeight()
                        .background(
                            brush = Brush.horizontalGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    backgroundColor
                                )
                            )
                        )
                )
            }
        }
    }
}

internal fun Modifier.swipeable(
    minimumDragAmount: Float = 50f,
    onSwipeLeft: ( () -> Unit )? = null,
    onSwipeRight: ( () -> Unit )? = null,
    onSwipeUp: ( () -> Unit )? = null,
    onSwipeDown: ( () -> Unit )? = null,
) = pointerInput( Unit ) {
    var offset = Offset.Zero
    detectDragGestures(
        onDrag = { pointer, dragAmount ->
            pointer.consume()
            offset += dragAmount
        },
        onDragEnd = {
            val xAbsolute = offset.x.absoluteValue
            val yAbsolute = offset.y.absoluteValue
            when {
                xAbsolute > minimumDragAmount && xAbsolute > yAbsolute -> when {
                    offset.x > 0 -> onSwipeRight?.invoke()
                    else -> onSwipeLeft?.invoke()
                }
                yAbsolute > minimumDragAmount -> when {
                    offset.y > 0 -> onSwipeDown?.invoke()
                    else -> onSwipeUp?.invoke()
                }
            }
            offset = Offset.Zero
        },
        onDragCancel = {
            offset = Offset.Zero
        }
    )
}

fun Color.clampBrightness(
    minTone: Double = 15.0,
    maxTone: Double = 85.0,
    maxChroma: Float = 30f
): Color {
    val hct = this.toHct()

    // 1. Keep it away from pure pitch black or pure paper white
    val clampedTone = hct.tone.coerceIn( minTone, maxTone )

    // 2. Squash the saturation! If it's a loud hot pink, pull it down to a soft dusty rose
    val clampedChroma = hct.chroma.coerceAtMost(  maxChroma.toDouble() )

    // Rebuild the color with the modified tone
    return Hct.from( hct.hue, clampedChroma, clampedTone ).toColor()
}


@Preview( showSystemUi = true )
@Composable
private fun MiniPlayerPreview() {
    MusicMattersTheme(
        themeMode = ThemeMode.LIGHT,
        primaryColorName = DefaultPreferences.PRIMARY_COLOR_NAME,
        fontName = DefaultPreferences.FONT_NAME,
        fontScale = 1f,
        useMaterialYou = true,
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
        ){
            MiniPlayerContent(
                modifier = Modifier.align( Alignment.Center ),
                uiState = NowPlayingScreenUiState.Success(
                    userData = emptyUserData.copy(
                        miniPlayerTextMarquee = true
                    ),
                    currentlyPlayingSong = Song(
                        id = "song-id-1",
                        mediaUri = "Uri.EMPTY",
                        title = "Started From the Bottom Now we Here",
                        albumId = 0L,
                        duration = 0L,
                        artists = setOf( "Drake", "Majid Jordan" ),
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
                    songAdditionalMetadata = null,
                ),
                playbackPosition = PlaybackPosition(
                    played = 3L,
                    total = 5L,
                    buffered = 4L,
                ),
                onNowPlayingBottomBarSwipeUp = {},
                onNowPlayingBottomBarClick = {},
                nextSong = { true },
                previousSong = { true },
                seekBack = {},
                seekForward = {},
                playPause = {},
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

