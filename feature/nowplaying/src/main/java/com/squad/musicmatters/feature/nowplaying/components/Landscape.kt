package com.squad.musicmatters.feature.nowplaying.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import com.squad.musicmatters.core.designsystem.component.MusicMattersIcons
import com.squad.musicmatters.core.media.connection.PlaybackPosition
import com.squad.musicmatters.core.model.LoopMode
import com.squad.musicmatters.core.model.Song
import com.squad.musicmatters.core.ui.FadeTransition
import com.squad.musicmatters.feature.nowplaying.NowPlayingScreenUiState

@OptIn(  ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class )
@Composable
internal fun LandscapeLayout(
    modifier: Modifier = Modifier,
    uiState: NowPlayingScreenUiState.Success,
    currentlyPlayingSong: Song,
    playbackPosition: PlaybackPosition,
    durationFormatter: ( Long ) -> String,
    onFavorite: ( Song, Boolean ) -> Unit,
    onSwipeArtworkLeft: () -> Unit,
    onSwipeArtworkRight: () -> Unit,
    onArtworkClicked: ( Song ) -> Unit,
    onArtistClicked: ( String ) -> Unit,
    onArtworkSwipedDown: () -> Unit,
    onShowOptionsMenu: () -> Unit,
    onSeekStart: () -> Unit,
    onSeekEnd: ( Long ) -> Unit,
    onPausePlayButtonClick: () -> Unit,
    onPreviousButtonClick: () -> Unit,
    onPlayNext: () -> Unit,
    onNavigateToQueue: () -> Unit,
    onToggleLoopMode: ( LoopMode ) -> Unit,
    onToggleShuffleMode: ( Boolean ) -> Unit,
    onPlayingSpeedChange: ( Float ) -> Unit,
    onPlayingPitchChange: ( Float ) -> Unit,
    onCreateEqualizerActivityContract: () -> Unit,
    onShowSleepTimerBottomSheet: () -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Row(
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding( top = 8.dp )
        ) {
            BottomSheetDefaults.DragHandle()
        }
        Row (
            modifier = modifier
                .fillMaxSize()
                .padding( start = 20.dp, end = 20.dp, bottom = 20.dp, top = 0.dp ),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            NowPlayingArtwork(
                artworkUri = currentlyPlayingSong.artworkUri?.toUri(),
                onSwipeLeft = onSwipeArtworkLeft,
                onSwipeRight = onSwipeArtworkRight,
                onSwipeDown = onArtworkSwipedDown,
                onArtworkClicked = { onArtworkClicked( currentlyPlayingSong ) }
            )
            Spacer( modifier = Modifier.width( 16.dp ) )
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
                            ArtistsRow(
                                artists = target.artists,
                                onArtistClicked = onArtistClicked
                            )
                            if ( uiState.userData.showNowPlayingAudioInformation ) {
                                if ( uiState.userData.showNowPlayingAudioInformation ) {
                                    uiState.songAdditionalMetadata?.let {
                                        Text(
                                            text = it.toSamplingInfoString( uiState.language ),
                                            style = MaterialTheme.typography.labelSmall
                                                .copy( color = LocalContentColor.current.copy( alpha = 0.7f ) ),
                                            maxLines = 3,
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
                            onClick = {
                                onFavorite(
                                    currentlyPlayingSong,
                                    !uiState.currentlyPlayingSongIsFavorite
                                )
                            }
                        ) {
                            AnimatedContent(
                                targetState = uiState.currentlyPlayingSongIsFavorite,
                                label = "now-playing-screen-is-favorite-icon"
                            ) {
                                Icon(
                                    imageVector = if ( it ) {
                                        MusicMattersIcons.Favorite
                                    } else {
                                        MusicMattersIcons.FavoriteBorder
                                    },
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                        IconButton(
                            onClick = onShowOptionsMenu
                        ) {
                            Icon(
                                imageVector = MusicMattersIcons.MoreVertical,
                                contentDescription = null
                            )
                        }
                    }
                }
                Spacer( modifier = Modifier.height( 24.dp ) )
                MusicMattersSeekBar(
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
                            sleepTimer = uiState.sleepTimer,
                            onPausePlayButtonClick = onPausePlayButtonClick,
                            onPreviousButtonClick = onPreviousButtonClick,
                            onNextButtonClick = onPlayNext,
                            onNavigateToQueue = onNavigateToQueue,
                            onShowSleepTimerBottomSheet = onShowSleepTimerBottomSheet,
                        )
                    else ->
                        NowPlayingTraditionalControlsLayout(
                            isPlaying = uiState.playerState.isPlaying,
                            sleepTimer = uiState.sleepTimer,
                            onPreviousButtonClick = onPreviousButtonClick,
                            onPausePlayButtonClick = onPausePlayButtonClick,
                            onNextButtonClick = onPlayNext,
                            onNavigateToQueue = onNavigateToQueue,
                            onShowSleepTimerBottomSheet = onShowSleepTimerBottomSheet,
                        )
                }
                Spacer( modifier = Modifier.height( 16.dp ) )
                NowPlayingScreenBottomBar(
                    language = uiState.language,
                    currentLoopMode = uiState.userData.loopMode,
                    shuffle = uiState.userData.shuffle,
                    currentSpeed = uiState.userData.playbackSpeed,
                    currentPitch = uiState.userData.playbackPitch,
                    lyricsLayout = uiState.userData.lyricsLayout,
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
}
